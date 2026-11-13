# openflexo-diagram — Fixed defects

Defects of openflexo-diagram that are fixed: entries moved from [`KNOWN_DEFECTS.md`](./KNOWN_DEFECTS.md) once their status is `DONE`,
with what was verified, the cause and the fix. Identifiers are those of `KNOWN_DEFECTS.md`, never reused.

---

### DIAGRAM-D-1 — A `DrawRectangleScheme` written without `targetType` answers one, and the FML printer has no node for it  ·  `DONE`

**Symptom.** `Cannot find P2PPNode for object FMLTypePropertyValue[…/targetType=GoalModelingDiagram,required=false]`, twice per load of `formod-rc` `GoalModelingDiagram.fml` (once per start of formod-app, and per scenario of `formod-test`), for `drawGoalGroup(required String title) with DrawRectangleScheme(childrenType = GoalGR, selectObjects = true)`.

**Cause (measured 2026-10-14, stack and code).** `DrawRectangleScheme.getTargetType()` returned, when no type was set, the type of the top level virtual model (`getTopLevelInstanceType()`). `FMLObject.updateFMLPropertyValues` reads the FML attributes through that getter and keeps a value that differs from the default (`isRequired`): a property value `targetType=GoalModelingDiagram` was created for text that never wrote it. The parsed text has no node for it: `ChildrenContents.initializePrettyPrint` warns, and the printer writes the value back on the next save (the expected output of `TestFMLDiagramPrettyPrint1` shows it for a `DropScheme`: `DropScheme(skipConfirmationPanel=false,targetType=TestVirtualModel)` from a text without it). The 14 `DropScheme` of `GoalModelingDiagram.fml` write `targetType` explicitly, hence one scheme only warned.

**Fixed (2026-10-14).** `getTargetType()` of `DrawRectangleScheme` answers what is declared (the field, or the type of the legacy `target`), and null for a scheme drawing at top level, as its Javadoc already said; the computation of the top level type (`getTopLevelInstanceType()`, `getVirtualModelWithDiagramNature(…)`) had no other user and is gone. Nobody else calls `DrawRectangleScheme.getTargetType()`. Test: `TestDrawRectangleSchemeTargetType` (fixture `TestDrawRectangleScheme.fml` of `diagram-ta-test-rc`), red without the fix.

**Same pattern, not fixed.** See DIAGRAM-D-2.
