# openflexo-diagram — Fixed defects

Defects of openflexo-diagram that are fixed: entries moved from [`KNOWN_DEFECTS.md`](./KNOWN_DEFECTS.md) once their status is `DONE`,
with what was verified, the cause and the fix. Identifiers are those of `KNOWN_DEFECTS.md`, never reused.

---

### DIAGRAM-D-1 — A `DrawRectangleScheme` written without `targetType` answers one, and the FML printer has no node for it  ·  `DONE`

**GitHub:** [openflexo-team/openflexo-diagram#8](https://github.com/openflexo-team/openflexo-diagram/issues/8)

**Symptom.** `Cannot find P2PPNode for object FMLTypePropertyValue[…/targetType=GoalModelingDiagram,required=false]`, twice per load of `formod-rc` `GoalModelingDiagram.fml` (once per start of formod-app, and per scenario of `formod-test`), for `drawGoalGroup(required String title) with DrawRectangleScheme(childrenType = GoalGR, selectObjects = true)`.

**Cause (measured 2026-10-14, stack and code).** `DrawRectangleScheme.getTargetType()` returned, when no type was set, the type of the top level virtual model (`getTopLevelInstanceType()`). `FMLObject.updateFMLPropertyValues` reads the FML attributes through that getter and keeps a value that differs from the default (`isRequired`): a property value `targetType=GoalModelingDiagram` was created for text that never wrote it. The parsed text has no node for it: `ChildrenContents.initializePrettyPrint` warns, and the printer writes the value back on the next save (the expected output of `TestFMLDiagramPrettyPrint1` shows it for a `DropScheme`: `DropScheme(skipConfirmationPanel=false,targetType=TestVirtualModel)` from a text without it). The 14 `DropScheme` of `GoalModelingDiagram.fml` write `targetType` explicitly, hence one scheme only warned.

**Fixed (2026-10-14).** `getTargetType()` of `DrawRectangleScheme` answers what is declared (the field, or the type of the legacy `target`), and null for a scheme drawing at top level, as its Javadoc already said; the computation of the top level type (`getTopLevelInstanceType()`, `getVirtualModelWithDiagramNature(…)`) had no other user and is gone. Nobody else calls `DrawRectangleScheme.getTargetType()`. Test: `TestDrawRectangleSchemeTargetType` (fixture `TestDrawRectangleScheme.fml` of `diagram-ta-test-rc`), red without the fix.

**Same pattern, not fixed.** See DIAGRAM-D-2.

### DIAGRAM-D-5 — The concept dropdown of "Create palette element binding" offers every concept, droppable or not  ·  `DONE`

**GitHub:** [openflexo-team/openflexo-diagram#11](https://github.com/openflexo-team/openflexo-diagram/issues/11)

**Symptom.** In the dialog that binds a diagram palette element to a FlexoConcept (`CreateFMLDiagramPaletteElementBindingFromDiagramPaletteElementDialog.fib`), the concept dropdown lists every concept of the VirtualModel, including concepts without a `DropScheme`, which cannot be bound to a palette element (`FMLDiagramPaletteElementBinding.allAvailableFlexoConcepts()` only offers concepts with a `DropScheme`).

**Cause (read in the code, 2026-11-20).** `CreateFMLDiagramPaletteElementBindingFromDiagramPaletteElement.computeFlexoConcepts()` filtered with `getFlexoBehaviours(DropScheme.class) != null`, always true: `FlexoConcept.getFlexoBehaviours(Class)` returns a new list, empty when the concept has no such behaviour.

**Fixed (2026-11-20).** The filter tests that the list is not empty, as `FMLDiagramPaletteElementBinding` does. Found during the review of CORE-F-19 phase 3c; not covered by a test (the dialog's action has none), checked by compilation and the diagram-ta / diagram-ta-ui suites.
