<h1><tt>AddShape</tt> <img src="https://support.openflexo.org/images/components/ta/diagram_ta/Diagram20x20.png" alt="Diagram20x20" /></h1>

This edition primitive addresses the creation of a new shape in a diagram.

## Usage

```java
[DiagramShape <value> =] 
DIAGRAM::AddShape(container=<container_path>[,options]) in (<diagram_path>);
```
or 

```java
[DiagramShape <value> =] 
AddShape(container=<container_path>[,options]) in (<diagram_path>);
```

where 

- \<container_path\> adresses a `DiagramContainerElement`
- \<diagram_path\> adresses a `Diagram`

## Configuration

| Property        | Type                    | Required  |
| --------------- |-------------------------| :--------:|
| `container`      | `DiagramContainerElement` | yes       | 
| `extendParentBoundsToHostThisShape`      | `boolean` | no  |
   
- `container`: Container for newly created shape
- `extendParentBoundsToHostThisShape` : Flag indicating if parent container should be extended in order to fully contain newly created shape

## Javadoc

Java class: [AddShape](./flexodiagram/apidocs/org/openflexo/technologyadapter/diagram/model/action/AddShape.html)

## See also

- [`CreateDiagram`](CreateDiagram.html) : This edition primitive addresses the creation of a new diagram.
- [`AddConnector`](AddConnector.html)  : This edition primitive addresses the creation of a new connector in a diagram.
- [`GraphicalAction`](GraphicalAction.html)  : This edition primitive allows to set a given graphical property for a diagram element.

