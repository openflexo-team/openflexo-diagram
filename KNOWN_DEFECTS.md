# openflexo-diagram — Known defects

Bugs of the diagram technology adapter (diagram-ta, diagram-ta-ui) that are known and not fixed yet. Each entry says what is verified, how to reproduce it, and how to work around it. Features and evolutions go to `BACKLOG.md`, not here.

Identifiers are stable and never reused: `DIAGRAM-D-<n>`. Reference them from commits and from other entries.

Status legend: `TODO` · `IN PROGRESS` · `BLOCKED` · `DONE` · `DEFERRED`.

Fixed defects are moved to [`FIXED_DEFECTS.md`](./FIXED_DEFECTS.md) once their status is `DONE`.

---

### DIAGRAM-D-2 — `DropScheme.getTargetType()` computes the same way, and a printed text gains `targetType=…`  ·  `TODO`

**GitHub:** [openflexo-team/openflexo-diagram#5](https://github.com/openflexo-team/openflexo-diagram/issues/5)

`DropScheme.getTargetType()` falls back on `getTargetFlexoConcept().getInstanceType()` then on the top level type, like `DrawRectangleScheme` did. Verified in an existing test: the fixture of `TestFMLDiagramPrettyPrint1` writes `DropScheme(skipConfirmationPanel=false)` and the reference files `Step1PrettyPrint.fml` / `Step1Normalized.fml` hold `DropScheme(skipConfirmationPanel=false,targetType=TestVirtualModel)`. Not seen as a warning in formod (every `DropScheme` there writes `targetType`), but a text round trip adds the argument. `DropScheme.isTopTarget`-like code (`DropScheme.java`, `getTargetType() == null || …equals(getTopLevelInstanceType())`) and `DropSchemeBindingModel` call `getTargetType()`, so the change is not the one of DIAGRAM-D-1: those reference files and these two callers have to follow. To decide: print the computed value (the normalized form wants it explicit) or not.

### DIAGRAM-D-3 — An empty `paletteElementBindings` list of a `TypedDiagram` role has no node either  ·  `TODO`

**GitHub:** [openflexo-team/openflexo-diagram#6](https://github.com/openflexo-team/openflexo-diagram/issues/6)

Seen while writing the test of DIAGRAM-D-1 (fixture with `Diagram diagram with TypedDiagram(diagramSpecification=…, isRequired=true)`, no `paletteElementBindings`): `Cannot find P2PPNode for object FMLInstancesListPropertyValue[paletteElementBindings=[]]`. Same mechanism: an empty list is a value different from the default (null) of an attribute nobody wrote. Not seen in formod. Not investigated further.

### DIAGRAM-D-4 — `TestCommonPalettes.testCommonPaletteGroup` fails now and then: an opened palette is still empty  ·  `TODO`

**GitHub:** [openflexo-team/openflexo-diagram#7](https://github.com/openflexo-team/openflexo-diagram/issues/7)

UI test of diagram-ta-ui. Failed once on 2026-11-15, in the run of the whole module through the openflexo-dev composite (`testAll`):
`AssertionError` at `TestCommonPalettes.java:128`, `<title> is empty`, so a palette of the common group had no elements just after its panel
was opened. Passed in the previous full run, and 3 times out of 3 when run alone (`./gradlew :openflexo-diagram:diagram-ta-ui:uiTest --tests
'*TestCommonPalettes'`). Not investigated: probably the palette of a panel is loaded asynchronously and the test reads it before it is loaded.
Not related to CORE-F-11 (the test runs no action).
