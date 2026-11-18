# openflexo-diagram — Backlog

Features and evolutions to develop. Each entry says what problem it solves and when it is done. Bugs that are known and not fixed go to `KNOWN_DEFECTS.md`, not here.

Identifiers are stable and never reused: `DIAGRAM-F-<n>`. Reference them from commits and from other entries.

Status legend: `TODO` · `IN PROGRESS` · `BLOCKED` · `DONE` · `DEFERRED`.

---

## FML runtime errors

### DIAGRAM-F-1 — The diagram adapter raises typed FML errors instead of returning null after a warning or a printed stack  ·  `DONE`

**GitHub:** [openflexo-team/openflexo-diagram#9](https://github.com/openflexo-team/openflexo-diagram/issues/9)

**Problem.** Part of `CORE-F-11` phase 6 (`openflexo-core/BACKLOG.md`), done with the other remaining adapters in one batch (asked by the user, 2026-11-16), with the rules settled on the first adapters (xlsx, xml, jdbc, rest, formod, docx): the edition actions caught the failures of their evaluations (four catches printing the stack, or a log), and went on with null after a warning.

**Rules applied (decided with the user on the first adapters).** Evaluations through `FMLEvaluation`; every `execute` / `performExecute` declares `throws FMLExecutionException`; a null receiver, model, document, name or required input is an `FMLNullReferenceError`; a select from a null source is an `FMLNullReferenceError` naming the select; a foreseen failure of the technology an `FMLTechnologyError` with a message of its own; a `ModelDefinitionException` an `FMLInternalError`; a cancelled loading an `FMLCancellation`; a typed failure of `AbstractCreateResource` rethrown unchanged; a log just before a throw removed (the failure is reported once, by the editor); the `System.out` traces of the actions removed. Scope: the paths that FML execution goes through; loading and conversion code left.

**Done (2026-11-16).**
- `AddShape`: no container is an `FMLNullReferenceError` (null after a warning); `AddConnector`: no common ancestor to the shapes is an `FMLTechnologyError` (an `IllegalArgumentException`, an internal error), a null source or target shape an `FMLNullReferenceError`.
- `GraphicalAction`: no graphical feature is an `FMLResolutionError`, a null element an `FMLNullReferenceError`; an element without graphical representation yet (a diagram created headlessly) and a numeric value that cannot be computed still leave the feature unchanged, with a warning, as before; so does a null met while computing the value (read from a representation that does not exist yet): the strict evaluation failed the creation of a formod domain model diagram (found by formod-test).
- `CreateDiagram`: typed failures, rethrown unchanged when already typed.

**Known limits (batch review).** The null met while computing the value of a `GraphicalAction` is tolerated even when it comes from a
behaviour called by the expression; `AddConnector.getFromShape` / `getToShape` and part of `AddShape.getContainer` still swallow their
failures (the null error that follows hides the cause). Chosen without an explicit decision: a null subject of a `GraphicalAction` is an
error (it was ignored).

**Verified (2026-11-16).** diagram-ta 25 (22 run, 2 skipped): `TestFMLDiagramPrettyPrint3` red as before phase 3 (same message), the others green; formod-test 32 green (a diagram created headlessly, see GraphicalAction); ECJ compiles diagram-ta. No new test.
