# openflexo-diagram — Known defects

Bugs of the diagram technology adapter (diagram-ta, diagram-ta-ui) that are known and not fixed yet. Each entry says what is verified, how to reproduce it, and how to work around it. Features and evolutions go to `BACKLOG.md`, not here.

Identifiers are stable and never reused: `DIAGRAM-D-<n>`. Reference them from commits and from other entries.

Status legend: `TODO` · `IN PROGRESS` · `BLOCKED` · `DONE` · `DEFERRED`.

---

### DIAGRAM-D-1 — A `DrawRectangleScheme` written without `targetType` answers one, and the FML printer has no node for it  ·  `DONE`

**Symptom.** `Cannot find P2PPNode for object FMLTypePropertyValue[…/targetType=GoalModelingDiagram,required=false]`, twice per load of `formod-rc` `GoalModelingDiagram.fml` (once per start of formod-app, and per scenario of `formod-test`), for `drawGoalGroup(required String title) with DrawRectangleScheme(childrenType = GoalGR, selectObjects = true)`.

**Cause (measured 2026-10-14, stack and code).** `DrawRectangleScheme.getTargetType()` returned, when no type was set, the type of the top level virtual model (`getTopLevelInstanceType()`). `FMLObject.updateFMLPropertyValues` reads the FML attributes through that getter and keeps a value that differs from the default (`isRequired`): a property value `targetType=GoalModelingDiagram` was created for text that never wrote it. The parsed text has no node for it: `ChildrenContents.initializePrettyPrint` warns, and the printer writes the value back on the next save (the expected output of `TestFMLDiagramPrettyPrint1` shows it for a `DropScheme`: `DropScheme(skipConfirmationPanel=false,targetType=TestVirtualModel)` from a text without it). The 14 `DropScheme` of `GoalModelingDiagram.fml` write `targetType` explicitly, hence one scheme only warned.

**Fixed (2026-10-14).** `getTargetType()` of `DrawRectangleScheme` answers what is declared (the field, or the type of the legacy `target`), and null for a scheme drawing at top level, as its Javadoc already said; the computation of the top level type (`getTopLevelInstanceType()`, `getVirtualModelWithDiagramNature(…)`) had no other user and is gone. Nobody else calls `DrawRectangleScheme.getTargetType()`. Test: `TestDrawRectangleSchemeTargetType` (fixture `TestDrawRectangleScheme.fml` of `diagram-ta-test-rc`), red without the fix.

**Same pattern, not fixed.** See DIAGRAM-D-2.

### DIAGRAM-D-2 — `DropScheme.getTargetType()` computes the same way, and a printed text gains `targetType=…`  ·  `TODO`

`DropScheme.getTargetType()` falls back on `getTargetFlexoConcept().getInstanceType()` then on the top level type, like `DrawRectangleScheme` did. Verified in an existing test: the fixture of `TestFMLDiagramPrettyPrint1` writes `DropScheme(skipConfirmationPanel=false)` and the reference files `Step1PrettyPrint.fml` / `Step1Normalized.fml` hold `DropScheme(skipConfirmationPanel=false,targetType=TestVirtualModel)`. Not seen as a warning in formod (every `DropScheme` there writes `targetType`), but a text round trip adds the argument. `DropScheme.isTopTarget`-like code (`DropScheme.java`, `getTargetType() == null || …equals(getTopLevelInstanceType())`) and `DropSchemeBindingModel` call `getTargetType()`, so the change is not the one of DIAGRAM-D-1: those reference files and these two callers have to follow. To decide: print the computed value (the normalized form wants it explicit) or not.

### DIAGRAM-D-3 — An empty `paletteElementBindings` list of a `TypedDiagram` role has no node either  ·  `TODO`

Seen while writing the test of DIAGRAM-D-1 (fixture with `Diagram diagram with TypedDiagram(diagramSpecification=…, isRequired=true)`, no `paletteElementBindings`): `Cannot find P2PPNode for object FMLInstancesListPropertyValue[paletteElementBindings=[]]`. Same mechanism: an empty list is a value different from the default (null) of an attribute nobody wrote. Not seen in formod. Not investigated further.
