package org.openflexo.technologyadapter.diagram.controller.diagrameditor;

import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.openflexo.connie.type.TypeUtils;
import org.openflexo.diana.DianaUtils;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.geom.DianaDimension;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.impl.ShapeNodeImpl;
import org.openflexo.diana.swing.control.tools.DataFlavorDelegate;
import org.openflexo.diana.swing.control.tools.DianaViewDropListener;
import org.openflexo.diana.view.DianaView;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.rt.FMLRTVirtualModelInstance;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.gina.view.widget.browser.impl.FIBBrowserModel;
import org.openflexo.gina.view.widget.browser.impl.FIBBrowserModel.BrowserCell;
import org.openflexo.gina.view.widget.browser.impl.FIBBrowserModel.TransferedBrowserCell;
import org.openflexo.technologyadapter.diagram.fml.DropScheme;
import org.openflexo.technologyadapter.diagram.fml.ShapeRole;
import org.openflexo.technologyadapter.diagram.model.Diagram;
import org.openflexo.technologyadapter.diagram.model.DiagramContainerElement;
import org.openflexo.technologyadapter.diagram.model.DiagramElement;
import org.openflexo.technologyadapter.diagram.model.action.DropSchemeAction;

/**
 * Implementation of a {@link DataFlavorDelegate} dedicated to FIBBrowserModel.BROWSER_CELL_FLAVOR<br>
 * 
 * @author sylvain
 *
 */
public class BrowserCellDataFlavorDelegate extends DataFlavorDelegate {

	private static final Logger logger = Logger.getLogger(BrowserCellDataFlavorDelegate.class.getPackage().getName());

	public BrowserCellDataFlavorDelegate(DianaViewDropListener dropListener) {
		super(dropListener);
	}

	@Override
	public DataFlavor getDataFlavor() {
		return FIBBrowserModel.BROWSER_CELL_FLAVOR;
	}

	@Override
	public int getAcceptableActions() {
		return DnDConstants.ACTION_COPY_OR_MOVE;
	}

	@Override
	public boolean isDragOk(DropTargetDragEvent e) {

		// System.out.println("isDragOK with " + e + " ????");

		try {
			TransferedBrowserCell transferedBrowserCell = (TransferedBrowserCell) e.getTransferable()
					.getTransferData(FIBBrowserModel.BROWSER_CELL_FLAVOR);

			// System.out.println("transferedBrowserCell=" + transferedBrowserCell);
			// System.out.println("e=" + e);

			DrawingTreeNode<?, ?> focused = getFocusedObject(e);

			// System.out.println("focused=" + focused);

			if (focused == null) {
				return false;
			}

			// System.out.println("getDianaEditor()=" + getDianaEditor());
			// System.out.println("getDianaEditor().getDragSourceContext()=" + getDianaEditor().getDragSourceContext());

			if (getDiagramEditor().getDragSourceContext() == null && getDiagramEditor().getObjectBeingTransfered() instanceof BrowserCell) {
				// Happen sometimes in some OS, see FOR-62, https://bugs.openflexo.org/browse/FOR-62
				Object representedObject = ((BrowserCell) getDianaEditor().getObjectBeingTransfered()).getRepresentedObject();
				return getApplicableDropSchemes(representedObject, focused).size() > 0;
			}

			if (getDianaEditor().getDragSourceContext().getTransferable() instanceof BrowserCell) {
				Object representedObject = ((BrowserCell) getDianaEditor().getDragSourceContext().getTransferable()).getRepresentedObject();
				return getApplicableDropSchemes(representedObject, focused).size() > 0;
			}
		} catch (UnsupportedFlavorException e1) {
			logger.warning("Unexpected: " + e1);
			e1.printStackTrace();
		} catch (IOException e1) {
			logger.warning("Unexpected: " + e1);
			e1.printStackTrace();
		}

		return false;
	}

	@Override
	public boolean performDrop(DropTargetDropEvent e) {
		Object data = getTransferData(e);

		if (data instanceof TransferedBrowserCell && getDianaEditor().getDragSourceContext().getTransferable() instanceof BrowserCell) {
			try {

				Object representedObject = ((BrowserCell) getDianaEditor().getDragSourceContext().getTransferable()).getRepresentedObject();

				DrawingTreeNode<?, ?> focused = getFocusedObject(e);
				if (focused == null) {
					return false;
				}

				List<DropScheme> applicableDropSchemes = getApplicableDropSchemes(representedObject, focused);

				// OK, let's got for the drop
				if (applicableDropSchemes.size() > 0) {
					Component targetComponent = e.getDropTargetContext().getComponent();
					Point pt = e.getLocation();
					DianaPoint modelLocation = new DianaPoint();
					if (targetComponent instanceof DianaView) {
						pt = DianaUtils.convertPoint(((DianaView<?, ?>) targetComponent).getNode(), pt, focused,
								((DianaView<?, ?>) targetComponent).getScale());
						modelLocation.x = pt.x / ((DianaView<?, ?>) targetComponent).getScale();
						modelLocation.y = pt.y / ((DianaView<?, ?>) targetComponent).getScale();
						// modelLocation.x -= ((TransferedPaletteElement) data).getOffset().x;
						// modelLocation.y -= ((TransferedPaletteElement) data).getOffset().y;
					}
					/*else {
						modelLocation.x -= ((TransferedPaletteElement) data).getOffset().x;
						modelLocation.y -= ((TransferedPaletteElement) data).getOffset().y;
					}*/

					// System.out.println("node was: " + ((DianaView<?, ?>) targetComponent).getNode());
					// System.out.println("element: " + element);
					modelLocation.x += ShapeNodeImpl.DEFAULT_BORDER_LEFT;
					modelLocation.y += ShapeNodeImpl.DEFAULT_BORDER_TOP;

					if (applicableDropSchemes.size() > 1) {
						JPopupMenu popup = new JPopupMenu();
						for (final DropScheme dropScheme : applicableDropSchemes) {
							JMenuItem menuItem = new JMenuItem(dropScheme.getDeclaringVirtualModel().getLocalizedDictionary()
									.localizedForKey(dropScheme.getLabel() != null ? dropScheme.getLabel() : dropScheme.getName()));
							menuItem.addActionListener(new DropSchemeActionListener(focused, dropScheme, representedObject, modelLocation));
							popup.add(menuItem);
						}
						popup.show(getDrawingView(), (int) modelLocation.x, (int) modelLocation.y);
						return true;
					}
					else { // availableDropSchemes.size() == 1
						return performDropScheme(focused, applicableDropSchemes.get(0), representedObject, modelLocation);
					}

				}

			} catch (Exception e1) {
				logger.warning("Unexpected: " + e1);
				e1.printStackTrace();
			}

		}
		return false;
	}

	private DiagramEditor getDiagramEditor() {
		if (getDianaEditor() instanceof DiagramEditor) {
			return (DiagramEditor) getDianaEditor();
		}
		return null;
	}

	private FMLRTVirtualModelInstance getFMLControlledDiagram() {
		if (getDianaEditor().getDrawing() instanceof FMLControlledDiagramDrawing) {
			return ((FMLControlledDiagramDrawing) getDianaEditor().getDrawing()).getVirtualModelInstance();
		}
		return null;
	}

	/*private List<DropScheme> getApplicableDropSchemes(FlexoConceptInstance fci, DrawingTreeNode<?, ?> focused) {
		List<DropScheme> returned = new ArrayList<>();
		if (getFMLControlledDiagram() == null) {
			return returned;
		}
		VirtualModel diagramVirtualModel = getFMLControlledDiagram().getVirtualModel();
		for (FlexoConcept concept : diagramVirtualModel.getFlexoConcepts()) {
			for (DropScheme ds : concept.getFlexoBehaviours(DropScheme.class)) {
				if (ds.getParameters().size() == 1 && ds.getParameters().get(0).getType() instanceof FlexoConceptInstanceType
						&& ((FlexoConceptInstanceType) ds.getParameters().get(0).getType()).getFlexoConcept()
								.isAssignableFrom(fci.getFlexoConcept())) {
					returned.add(ds);
				}
			}
		}
		return returned;
	}*/

	private List<DropScheme> getApplicableDropSchemes(Object object, DrawingTreeNode<?, ?> focused) {
		List<DropScheme> returned = new ArrayList<>();
		if (getFMLControlledDiagram() == null) {
			return returned;
		}
		VirtualModel diagramVirtualModel = getFMLControlledDiagram().getVirtualModel();
		for (FlexoConcept concept : diagramVirtualModel.getFlexoConcepts()) {
			// System.out.println("on regarde le concept " + concept);
			for (DropScheme ds : concept.getFlexoBehaviours(DropScheme.class)) {
				// System.out.println("ds=" + ds);
				if (ds.getParameters().size() == 1) {
					// System.out.println("isOfType " + ds.getParameters().get(0).getType() + " : " + object + " ? -> "
					// + TypeUtils.isOfType(object, ds.getParameters().get(0).getType()));
					if (TypeUtils.isOfType(object, ds.getParameters().get(0).getType())) {
						returned.add(ds);
					}
				}
			}
		}
		return returned;
	}

	private boolean performDropScheme(DrawingTreeNode<?, ?> target, DropScheme dropScheme, Object droppedObject, DianaPoint dropLocation) {

		FlexoConceptInstance parentFlexoConceptInstance = null;
		ShapeRole parentShapeRole = null;
		DiagramContainerElement<?> container = null;

		if (target.getDrawable() instanceof FMLControlledDiagramElement) {
			parentFlexoConceptInstance = ((FMLControlledDiagramElement<?, ?>) target.getDrawable()).getFlexoConceptInstance();
			container = (DiagramContainerElement<?>) ((FMLControlledDiagramElement<?, ?>) target.getDrawable()).getDiagramElement();
		}
		if (target.getDrawable() instanceof FMLControlledDiagramShape) {
			parentShapeRole = ((FMLControlledDiagramShape) target.getDrawable()).getRole();
			container = (DiagramContainerElement<?>) ((FMLControlledDiagramElement<?, ?>) target.getDrawable()).getDiagramElement();
		}
		if (target.getDrawable() instanceof Diagram) {
			container = (Diagram) target.getDrawable();
		}

		// Try to consider the dropped location as the center of the dropped shape
		if (dropScheme.getFlexoConcept().getAccessibleProperties(ShapeRole.class).size() > 0) {
			ShapeRole shapeRole = dropScheme.getFlexoConcept().getAccessibleProperties(ShapeRole.class).get(0);
			DianaDimension size = shapeRole.getGraphicalRepresentation().getSize();
			dropLocation.x = dropLocation.x - size.width / 2;
			dropLocation.y = dropLocation.y - size.height / 2;
		}

		DropSchemeAction action = new DropSchemeAction(dropScheme, getFMLControlledDiagram(), null,
				getDiagramEditor().getFlexoController().getEditor());
		action.setParentInformations(parentFlexoConceptInstance, parentShapeRole);
		action.setDropLocation(dropLocation);
		action.setParameterValue(dropScheme.getParameters().get(0), droppedObject);
		action.doAction();

		// The new shape has well be added to the diagram, and the drawing (which listen to the diagram) has well received the event
		// The drawing is now up-to-date... but there is something wrong if we are in FML-controlled mode.
		// Since the shape has been added BEFORE the FlexoConceptInstance has been set, the drawing only knows about the DiagamShape,
		// and not about an FMLControlledDiagramShape. That's why we need to notify again the new diagram element's parent, to be
		// sure that the Drawing can discover that the new shape is FML-controlled

		container.getPropertyChangeSupport().firePropertyChange(DiagramElement.INVALIDATE, null, container);

		return action.hasActionExecutionSucceeded();
	}

	public class DropSchemeActionListener implements ActionListener {

		private final DrawingTreeNode<?, ?> target;
		private final DropScheme dropScheme;
		private final Object droppedObject;
		private final DianaPoint dropLocation;

		DropSchemeActionListener(DrawingTreeNode<?, ?> target, DropScheme dropScheme, Object droppedObject, DianaPoint dropLocation) {
			this.target = target;
			this.dropScheme = dropScheme;
			this.droppedObject = droppedObject;
			this.dropLocation = dropLocation;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			performDropScheme(target, dropScheme, droppedObject, dropLocation);
		}

	}

}
