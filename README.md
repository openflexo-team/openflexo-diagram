# openflexo-diagram — Diagram technology adapter

**Status:** Active <!-- Active · Frozen · POC · Deprecated · Retired: same as TA-CATALOG.md -->

Diagrams controlled by FML: free diagrams, or diagrams typed by a diagram specification (palettes, shape and connector roles, drop and link schemes).

| | |
|---|---|
| Technology | OpenFlexo diagrams drawn with DIANA; import of PowerPoint slides through Apache POI |
| Libraries | `poi-scratchpad` 5.5.1, `poi` 5.5.1, `batik-dom` 1.20, `batik-svggen` 1.20 |
| Family | `DOC` · `R/W+GEN` (see the classification of `openflexo-technology-adapters/TA-CATALOG.md`) |
| Resource factories | `DiagramSpecificationResourceFactory` (.diagramspecification directory), `DiagramResourceFactory` (.diagram) |
| Test resource center | `http://openflexo.org/diagram-test` |

## FML surface

| Model slot | FML name | Roles | Edition actions and behaviours | Fetch requests |
|---|---|---|---|---|
| `TypedDiagramModelSlot` | `TypedDiagram` | `ShapeRole`, `ConnectorRole`, `DiagramRole` | `CreateDiagram`, `AddShape`, `AddConnector`, `GraphicalAction`, `DropScheme`, `DrawRectangleScheme`, `LinkScheme`, `DiagramNavigationScheme` | — |
| `FreeDiagramModelSlot` | `FreeDiagram` | `ShapeRole`, `ConnectorRole`, `DiagramRole` | `CreateDiagram`, `AddShape`, `AddConnector`, `GraphicalAction`, `DropScheme`, `DrawRectangleScheme`, `LinkScheme`, `DiagramNavigationScheme` | — |

## Example

From the test resource center (`TestResourceCenter/TestDrawRectangleScheme.fml/TestDrawRectangleScheme.fml`):

```fml
namespace "http://openflexo.org/test/TestDiagramSpecification" as DIAGRAM_SPECIFICATION;

use org.openflexo.technologyadapter.diagram.TypedDiagramModelSlot as DIAGRAM;

import org.openflexo.technologyadapter.diagram.model.DiagramShape;
import org.openflexo.technologyadapter.diagram.model.Diagram;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramSpecification;

import DiagramSpecification TEST_DIAGRAM_SPECIFICATION from [DIAGRAM_SPECIFICATION];
import Diagram EXAMPLE_DIAGRAM from [DIAGRAM_SPECIFICATION + "/ExampleDiagram.diagram"];
import DiagramShape DIAGRAM_SHAPE from [EXAMPLE_DIAGRAM:"SYL-2"];

@URI("http://openflexo.org/test/TestResourceCenter/TestDrawRectangleScheme.fml")
@Version("0.1")
model TestDrawRectangleScheme {

	Diagram diagram with TypedDiagram(diagramSpecification=TEST_DIAGRAM_SPECIFICATION, isRequired=true);

	concept Box {
		DiagramShape shape with ShapeRole(exampleLabel="label",metamodelElement=DIAGRAM_SHAPE);
	}

	concept Group {
		/** A rectangle drawn at top level: the scheme declares no target type */
		void drawGroup(required String title) with DrawRectangleScheme(childrenType=Box,selectObjects=true) {
			log "drawing " + title;
		}
	}
}
```

## Modules

| Module | Content |
|---|---|
| `diagram-ta` | adapter, model slots, resources, domain model, dictionaries |
| `diagram-ta-ui` | controller, icons, module views, inspectors |
| `diagram-ta-test-rc` | test resource center |

## Build and test

From `openflexo-dev` (local sources of the whole platform):

    ./gradlew --continue :openflexo-diagram:diagram-ta:cleanTest :openflexo-diagram:diagram-ta:test

Tests run with `ignoreFailures = true`: read the `Results:` line, not the exit status.

## Known defects and backlog

- Known defects: [KNOWN_DEFECTS.md](KNOWN_DEFECTS.md) (`DIAGRAM-D-<n>`), fixed ones in [FIXED_DEFECTS.md](FIXED_DEFECTS.md).
- Features, evolutions and user requests: [BACKLOG.md](BACKLOG.md) (`DIAGRAM-F-<n>`).
- Issues: https://github.com/openflexo-team/openflexo-diagram/issues

## License

Dual-licensed under the EUPL (1.1 or later) and the GNU GPL (3 or later), as stated in the header of each source file;
[LICENSE](LICENSE) holds the text of the GPL.
