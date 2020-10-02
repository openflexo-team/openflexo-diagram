# `AddShape`

This edition primitive addresses the creation of a new shape in a diagram.

## Usage

```java
[DiagramShape <value> =] DIAGRAM::AddShape(container=<container_path>[,options]) in (<diagram_path>);
```
or 

```java
[DiagramShape <value> =] AddShape(container=<container_path>[,options]) in (<diagram_path>);
```

where 

- <container_path> adresses a `DiagramContainerElement`
- <diagram_path> adresses a `Diagram`

## Configuration

| Property        | Type                    | Required  | Description |
| --------------- |:-----------------------:| ---------:| -----------:|
| `container`      | `DiagramContainerElement` | yes       | Container for newly created shape |
| `extendParentBoundsToHostThisShape`      | `boolean` | no       | Flag indicating if parent container should be extended in order to fully contain newly created shape |


## Javadoc

Java class: [AddShape](./flexodiagram/apidocs/org/openflexo/technologyadapter/diagram/model/action/AddShape.html)

## See also

- [`CreateDiagram`](CreateDiagram.html) : This edition primitive addresses the creation of a new diagram.
- [`AddConnector`](AddConnector.html)  : This edition primitive addresses the creation of a new connector in a diagram.
- [`GraphicalAction`](GraphicalAction.html)  : This edition primitive allows to set a given graphical property for a diagram element.

