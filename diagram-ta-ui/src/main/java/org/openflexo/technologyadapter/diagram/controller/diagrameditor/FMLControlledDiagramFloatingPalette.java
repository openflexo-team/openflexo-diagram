/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Openflexo-technology-adapters-ui, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.technologyadapter.diagram.controller.diagrameditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.openflexo.diana.BackgroundStyle;
import org.openflexo.diana.DianaCoreUtils;
import org.openflexo.diana.DianaUtils;
import org.openflexo.diana.ForegroundStyle;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.ColorGradientBackgroundStyle.ColorGradientDirection;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.Drawing.RootNode;
import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.control.DianaEditor;
import org.openflexo.diana.cp.ControlArea;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.DianaRoundRectangle;
import org.openflexo.diana.geom.DianaShape;
import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.DianaGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.diana.graphics.DianaGraphics;
import org.openflexo.diana.swing.paint.DianaPaintManager;
import org.openflexo.diana.swing.view.JShapeView;
import org.openflexo.foundation.fml.rt.FMLRTVirtualModelInstance;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.technologyadapter.diagram.controller.diagrameditor.FMLControlledDiagramShape.DropAndLinkScheme;
import org.openflexo.technologyadapter.diagram.fml.DropScheme;
import org.openflexo.technologyadapter.diagram.fml.LinkScheme;
import org.openflexo.technologyadapter.diagram.fml.ShapeRole;
import org.openflexo.technologyadapter.diagram.model.Diagram;
import org.openflexo.technologyadapter.diagram.model.DiagramShape;
import org.openflexo.technologyadapter.diagram.model.action.AddConnector;
import org.openflexo.technologyadapter.diagram.model.action.DropSchemeAction;
import org.openflexo.technologyadapter.diagram.model.action.LinkSchemeAction;

/**
 * Represents a floating palette associated to a {@link FMLControlledDiagramShape} and a cardinal orientation.<br>
 * From a technical point of view, this floating palette is a {@link ControlArea} allowing to trigger LinkScheme(s) and a combination of a
 * DropScheme with a LinkScheme
 * 
 * @author sylvain
 *
 */
public class FMLControlledDiagramFloatingPalette extends ControlArea<DianaRoundRectangle> implements PropertyChangeListener {

	private static final Logger logger = Logger.getLogger(FMLControlledDiagramFloatingPalette.class.getPackage().getName());

	protected static final Color OK = new Color(0, 191, 0);

	private enum Mode {
		CREATE_SHAPE_AND_LINK, LINK_ONLY;
	}

	private DianaRoundRectangle roleRect;
	private DianaRectangle edgeRect;

	/** The vertical space between two elements of the palette */
	private static final int SPACING = 5;
	/** The height of an element of the palette */
	private static final int ELEMENTS_HEIGHT = 8;
	/** The width of an element of the palette */
	private static final int ELEMENTS_WIDTH = 12;

	private static final ForegroundStyle NONE = DianaCoreUtils.TOOLS_FACTORY.makeNoneForegroundStyle();
	private static final BackgroundStyle DEFAULT = DianaCoreUtils.TOOLS_FACTORY.makeColoredBackground(Color.WHITE);
	private static final ForegroundStyle NODE_FOREGROUND = DianaCoreUtils.TOOLS_FACTORY.makeForegroundStyle(Color.RED, 1.0f);
	private static final ForegroundStyle EDGE_FOREGROUND = DianaCoreUtils.TOOLS_FACTORY.makeForegroundStyle(DianaUtils.NICE_BROWN, 1.0f);
	private static final BackgroundStyle NODE_BACKGROUND = DianaCoreUtils.TOOLS_FACTORY.makeColorGradientBackground(Color.ORANGE, Color.WHITE,
			ColorGradientDirection.NORTH_WEST_SOUTH_EAST);

	static {
		DEFAULT.setUseTransparency(true);
		DEFAULT.setTransparencyLevel(0.3f);
		NODE_BACKGROUND.setUseTransparency(true);
		NODE_BACKGROUND.setTransparencyLevel(0.7f);
	}

	private final SimplifiedCardinalDirection orientation;

	protected Point currentDraggingLocationInDrawingView = null;
	protected boolean drawEdge = false;
	protected boolean isDnd = false;

	protected ShapeNode<?> to = null;
	protected DrawingTreeNode<?, ?> focusedNode;
	private FMLControlledDiagramEditor controller;
	private DianaPoint normalizedStartPoint;

	private Rectangle previousRectangle;
	private Mode mode;

	public FMLControlledDiagramFloatingPalette(ShapeNode<FMLControlledDiagramShape> node, SimplifiedCardinalDirection orientation) {
		super(node, makeRoundRect(node, orientation));
		this.orientation = orientation;
		node.getPropertyChangeSupport().addPropertyChangeListener(this);

		updateElements(orientation);
	}

	@Override
	public void delete() {
		if (getNode() != null && getNode().getPropertyChangeSupport() != null) {
			getNode().getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		super.delete();
	}

	@Override
	public ShapeNode<FMLControlledDiagramShape> getNode() {
		return (ShapeNode<FMLControlledDiagramShape>) super.getNode();
	}

	@Override
	public boolean isDraggable() {
		return getNode().getDrawing().isEditable();
	}

	public void paint(Graphics g, DiagramEditor controller) {
		if (drawEdge && currentDraggingLocationInDrawingView != null) {
			DianaShape<?> dianaShape = getNode().getShape().getOutline();
			RootNode<Diagram> rootNode = controller.getDrawing().getRoot();
			// DrawingGraphicalRepresentation<?> drawingGR = controller.getDrawingGraphicalRepresentation();
			double scale = controller.getScale();
			DianaPoint nearestOnOutline = dianaShape.getNearestPoint(
					rootNode.convertLocalViewCoordinatesToRemoteNormalizedPoint(currentDraggingLocationInDrawingView, getNode(), scale));
			/*nodeGR.convertLocalNormalizedPointToRemoteViewCoordinates(this.normalizedStartPoint, controller.getDrawingGraphicalRepresentation(), controller.getScale())*/
			Point fromPoint = getNode().convertLocalNormalizedPointToRemoteViewCoordinates(nearestOnOutline, rootNode, scale);
			Point toPoint = currentDraggingLocationInDrawingView;

			if (mode == Mode.LINK_ONLY) {
				if (to != null && isDnd) {
					// toPoint = drawingGR.convertRemoteNormalizedPointToLocalViewCoordinates(to.getShape().getShape().getCenter(), to,
					// scale);
					g.setColor(OK);
				}
				else {
					g.setColor(Color.RED);
				}
				g.drawLine(fromPoint.x, fromPoint.y, toPoint.x, toPoint.y);

			}
			else {
				if (isDnd) {
					g.setColor(OK);
				}
				else {
					g.setColor(Color.RED);
				}
				Rectangle rect = new Rectangle(toPoint.x - 10, toPoint.y - 10, 20, 20);
				g.drawRect(rect.x, rect.y, rect.width, rect.height);
				DianaRectangle r = new DianaRectangle(rect);
				DianaPoint outlineToPoint = r.nearestOutlinePoint(new DianaPoint(fromPoint.x, fromPoint.y));
				g.drawLine(fromPoint.x, fromPoint.y, (int) outlineToPoint.x, (int) outlineToPoint.y);
			}
			int x, y, w, h;
			if (fromPoint.x >= toPoint.x) {
				x = toPoint.x;
				w = fromPoint.x - toPoint.x;
			}
			else {
				x = fromPoint.x;
				w = toPoint.x - fromPoint.x;
			}
			if (fromPoint.y >= toPoint.y) {
				y = toPoint.y;
				h = fromPoint.y - toPoint.y;
			}
			else {
				y = fromPoint.y;
				h = toPoint.y - fromPoint.y;
			}
			previousRectangle = new Rectangle(x - 11, y - 11, w + 22, h + 22);
		}
	}

	@Override
	public void startDragging(DianaEditor<?> controller, DianaPoint startPoint) {
		mode = null;
		if (roleRect.contains(startPoint)) {
			mode = Mode.CREATE_SHAPE_AND_LINK;
		}
		else if (edgeRect.contains(startPoint)) {
			mode = Mode.LINK_ONLY;
		}
		if (mode != null) {
			drawEdge = true;
			normalizedStartPoint = startPoint;
			this.controller = (FMLControlledDiagramEditor) controller;
			this.controller.getDrawingView().setFloatingPalette(this);
		}
		else {
			drawEdge = false;
		}
	}

	@Override
	public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration, DianaPoint newAbsolutePoint,
			DianaPoint initialPoint, MouseEvent event) {
		if (drawEdge) {
			DiagramView drawingView = controller.getDrawingView();
			DianaPaintManager paintManager = drawingView.getPaintManager();
			// Attempt to repaint relevant zone only
			Rectangle oldBounds = previousRectangle;
			if (oldBounds != null) {
				oldBounds.x -= 1;
				oldBounds.y -= 1;
				oldBounds.width += 2;
				oldBounds.height += 2;
			}
			focusedNode = controller.getDrawingView().getFocusRetriever().getFocusedObject(event);
			if (focusedNode instanceof ShapeNode && focusedNode != getNode()) {
				to = (ShapeNode<?>) focusedNode;
			}
			else {
				to = null;
			}

			currentDraggingLocationInDrawingView = SwingUtilities.convertPoint((Component) event.getSource(), event.getPoint(),
					controller.getDrawingView());
			if (!isDnd) {
				isDnd = getNode().convertLocalNormalizedPointToRemoteViewCoordinates(normalizedStartPoint, getNode().getDrawing().getRoot(),
						controller.getScale()).distance(currentDraggingLocationInDrawingView) > 5;
			}

			// Attempt to repaint relevant zone only
			Rectangle newBounds = getBoundsToRepaint(drawingView);
			Rectangle boundsToRepaint;
			if (oldBounds != null) {
				boundsToRepaint = oldBounds.union(newBounds);
			}
			else {
				boundsToRepaint = newBounds;
			}
			paintManager.repaint(drawingView, boundsToRepaint);

			// Alternative @brutal zone
			// paintManager.repaint(drawingView);

			return true;
		}
		return false;
	}

	// Attempt to repaint relevant zone only
	private Rectangle getBoundsToRepaint(DiagramView drawingView) {
		JShapeView<?> fromView = drawingView.shapeViewForNode(getNode());
		Rectangle fromViewBounds = SwingUtilities.convertRectangle(fromView, fromView.getBounds(), drawingView);
		Rectangle boundsToRepaint = fromViewBounds;

		if (to != null) {
			JShapeView<?> toView = drawingView.shapeViewForNode(to);
			Rectangle toViewBounds = SwingUtilities.convertRectangle(toView, toView.getBounds(), drawingView);
			boundsToRepaint = fromViewBounds.union(toViewBounds);
		}

		if (currentDraggingLocationInDrawingView != null) {
			Rectangle lastLocationBounds = new Rectangle(currentDraggingLocationInDrawingView);
			boundsToRepaint = fromViewBounds.union(lastLocationBounds);
		}

		// logger.fine("boundsToRepaint="+boundsToRepaint);

		return boundsToRepaint;
	}

	@Override
	public void stopDragging(DianaEditor<?> controller, DrawingTreeNode<?, ?> focused) {
		if (drawEdge && currentDraggingLocationInDrawingView != null && isDnd) {
			try {
				// Unused DrawingTreeNode<?, ?> targetNode = getNode().getParentNode();
				if (focused == null) {
					focused = getNode().getDrawing().getRoot();
				}
				SimplifiedCardinalDirection direction = DianaPoint.getSimplifiedOrientation(
						new DianaPoint(getNode().convertLocalNormalizedPointToRemoteViewCoordinates(this.normalizedStartPoint,
								getNode().getDrawing().getRoot(), controller.getScale())),
						new DianaPoint(currentDraggingLocationInDrawingView));
				Point dropPoint = currentDraggingLocationInDrawingView;
				if (dropPoint.x < 0) {
					dropPoint.x = 0;
				}
				if (dropPoint.y < 0) {
					dropPoint.y = 0;
				}

				Point p = DianaUtils.convertPoint(getNode().getDrawing().getRoot(), dropPoint, focused, controller.getScale());
				DianaPoint dropLocation = new DianaPoint(p.x / controller.getScale(), p.y / controller.getScale());
				// ShapeNode<?> to = null;

				switch (mode) {
					case CREATE_SHAPE_AND_LINK:
						askAndApplyDropAndLinkScheme(dropLocation, focused);
						break;
					case LINK_ONLY:
						if (to != null) {
							// to = this.to.getDrawable();
							askAndApplyLinkScheme(dropLocation, to);
						}
						break;
					default:
						logger.warning("Not implemented !!!");
						break;
				}
				if (to == null) {
					return;
				}

			} finally {
				resetVariables();
				DiagramView diagramView = ((DiagramEditor) controller).getDrawingView();
				diagramView.resetFloatingPalette();
				DianaPaintManager paintManager = diagramView.getPaintManager();
				paintManager.invalidate(diagramView.getDrawing().getRoot());
				paintManager.repaint(diagramView.getDrawing().getRoot());
			}
		}
		else {
			resetVariables();
		}
		super.stopDragging(controller, focusedNode);
	}

	private void askAndApplyDropAndLinkScheme(final DianaPoint dropLocation, DrawingTreeNode<?, ?> focused) {

		FlexoConceptInstance parentFlexoConceptInstance = null;
		ShapeRole parentShapeRole = null;

		if (focused.getDrawable() instanceof FMLControlledDiagramElement) {
			parentFlexoConceptInstance = ((FMLControlledDiagramElement<?, ?>) focused.getDrawable()).getFlexoConceptInstance();
		}
		if (focused.getDrawable() instanceof FMLControlledDiagramShape) {
			parentShapeRole = (ShapeRole) ((FMLControlledDiagramShape) focused.getDrawable()).getRole();
		}

		/*if (parentFlexoConceptInstance == null || parentShapeRole == null) {
			return;
		}*/

		/*if (focused.getDrawable() instanceof FMLControlledDiagramShape) {
			container = ((FMLControlledDiagramShape) focused.getDrawable()).getDiagramElement();
			containerConcept = ((FMLControlledDiagramShape) focused.getDrawable()).getFlexoConceptInstance().getFlexoConcept();
		} else if (focused.getDrawable() instanceof Diagram) {
			container = (Diagram) focused.getDrawable();
			containerConcept = null;
		}
		
		if (container == null) {
			return;
		}*/

		List<DropAndLinkScheme> allDropAndLinkSchemes = getNode().getDrawable()
				.getAvailableDropAndLinkSchemes(parentFlexoConceptInstance != null ? parentFlexoConceptInstance.getFlexoConcept() : null);

		if (allDropAndLinkSchemes.size() == 0) {
			return;
		}

		else if (allDropAndLinkSchemes.size() == 1) {
			applyDropAndLinkScheme(allDropAndLinkSchemes.get(0), dropLocation, parentFlexoConceptInstance, parentShapeRole);
		}

		else {
			JPopupMenu popup = new JPopupMenu();
			for (final DropAndLinkScheme dropAndLinkScheme : allDropAndLinkSchemes) {
				LocalizedDelegate localizedDictionary = dropAndLinkScheme.linkScheme.getDeclaringVirtualModel().getLocalizedDictionary();
				String linkLabel = dropAndLinkScheme.linkScheme.getLabel() != null ? dropAndLinkScheme.linkScheme.getLabel()
						: dropAndLinkScheme.linkScheme.getName();
				String localizedLinkLabel = localizedDictionary.localizedForKeyAndLanguage(linkLabel,
						FlexoLocalization.getCurrentLanguage());
				if (localizedLinkLabel == null) {
					localizedLinkLabel = linkLabel;
				}
				String dropLabel = dropAndLinkScheme.dropScheme.getFlexoConcept().getName();
				String localizedDropLabel = localizedDictionary.localizedForKeyAndLanguage(dropLabel,
						FlexoLocalization.getCurrentLanguage());
				if (localizedDropLabel == null) {
					localizedDropLabel = dropLabel;
				}
				String withNew = FlexoLocalization.getMainLocalizer().localizedForKey("with_new");
				JMenuItem menuItem = new JMenuItem(localizedLinkLabel + " " + withNew + " " + localizedDropLabel);
				// final DiagramContainerElement<?> finalContainer = container;
				final FlexoConceptInstance finalParentFlexoConceptInstance = parentFlexoConceptInstance;
				final ShapeRole finalParentShapeRole = parentShapeRole;

				menuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						applyDropAndLinkScheme(dropAndLinkScheme, dropLocation, finalParentFlexoConceptInstance, finalParentShapeRole);
					}
				});
				menuItem.setToolTipText(dropAndLinkScheme.dropScheme.getDescription());
				popup.add(menuItem);
			}
			popup.show((Component) controller.getDrawingView().viewForNode(getNode().getParentNode()), (int) dropLocation.x,
					(int) dropLocation.y);

		}
	}

	private void askAndApplyLinkScheme(final DianaPoint dropLocation, final ShapeNode<?> to) {

		if (to.getDrawable() instanceof FMLControlledDiagramShape) {
			final FMLControlledDiagramShape toShape = (FMLControlledDiagramShape) to.getDrawable();
			List<LinkScheme> availableLinkSchemes = getNode().getDrawable()
					.getAvailableLinkSchemes(toShape.getFlexoConceptInstance().getFlexoConcept());

			if (availableLinkSchemes.size() == 1) {
				LinkSchemeAction action = new LinkSchemeAction(availableLinkSchemes.get(0),
						(FMLRTVirtualModelInstance) getNode().getDrawable().getFlexoConceptInstance().getVirtualModelInstance(), null,
						controller.getFlexoController().getEditor());
				action.setFromShape(getNode().getDrawable().getDiagramElement());
				action.setToShape(toShape.getDiagramElement());
				action.escapeParameterRetrievingWhenValid = true;
				action.doAction();
			}
			else if (availableLinkSchemes.size() > 1) {
				JPopupMenu popup = new JPopupMenu();
				for (final LinkScheme linkScheme : availableLinkSchemes) {
					// final CalcPaletteConnector connector = availableConnectors.get(linkScheme);
					// System.out.println("Available: "+paletteConnector.getEditionPattern().getName());
					LocalizedDelegate localizedDictionary = linkScheme.getDeclaringVirtualModel().getLocalizedDictionary();
					String label = linkScheme.getLabel() != null ? linkScheme.getLabel() : linkScheme.getName();
					String localized = localizedDictionary.localizedForKeyAndLanguage(label, FlexoLocalization.getCurrentLanguage());
					if (localized == null) {
						localized = label;
					}
					JMenuItem menuItem = new JMenuItem(localized);
					menuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							// System.out.println("Action "+paletteConnector.getEditionPattern().getName());
							LinkSchemeAction action = new LinkSchemeAction(linkScheme,
									(FMLRTVirtualModelInstance) getNode().getDrawable().getFlexoConceptInstance().getVirtualModelInstance(),
									null, controller.getFlexoController().getEditor());
							action.setFromShape(getNode().getDrawable().getDiagramElement());
							action.setToShape(toShape.getDiagramElement());
							action.escapeParameterRetrievingWhenValid = true;
							action.doAction();
						}
					});
					menuItem.setToolTipText(linkScheme.getDescription());
					popup.add(menuItem);
				}
				popup.show((Component) controller.getDrawingView().viewForNode(getNode().getParentNode()), (int) dropLocation.x,
						(int) dropLocation.y);
			}
		}
		else if (to.getDrawable() instanceof DiagramShape) {
			// Graphical connector only
			AddConnector action = AddConnector.actionType.makeNewAction(getNode().getDrawable().getDiagramElement(), null,
					controller.getFlexoController().getEditor());
			action.setToShape((DiagramShape) to.getDrawable());
			action.doAction();
		}

	}

	protected void applyDropAndLinkScheme(final DropAndLinkScheme dropAndLinkScheme, final DianaPoint dropLocation,
			/*DiagramContainerElement<?> container*/FlexoConceptInstance parentFlexoConceptInstance, ShapeRole parentShapeRole) {
		applyDropAndLinkScheme(dropAndLinkScheme.dropScheme, dropAndLinkScheme.linkScheme, dropLocation, parentFlexoConceptInstance,
				parentShapeRole);
	}

	protected void applyDropAndLinkScheme(DropScheme dropScheme, LinkScheme linkScheme, DianaPoint dropLocation,
			/*DiagramContainerElement<?> container*/FlexoConceptInstance parentFlexoConceptInstance, ShapeRole parentShapeRole) {

		logger.info("applyDropAndLinkScheme dropScheme=" + dropScheme + " linkScheme=" + linkScheme + " parentFlexoConceptInstance="
				+ parentFlexoConceptInstance + " parentShapeRole=" + parentShapeRole);

		FMLControlledDiagramShape newShape = createNewShape(dropLocation, parentFlexoConceptInstance, parentShapeRole, dropScheme);

		if (newShape != null) {
			if (linkScheme.getInverseDirectionWhenComposed()) {
				createNewConnector(newShape, getNode().getDrawable(), linkScheme);
			}
			else {
				createNewConnector(getNode().getDrawable(), newShape, linkScheme);
			}
			controller.setSelectedObject(controller.getDrawing().getDrawingTreeNode(newShape));
		}
	}

	private void resetVariables() {
		drawEdge = false;
		isDnd = false;
		to = null;
		currentDraggingLocationInDrawingView = null;
	}

	private FMLControlledDiagramShape createNewShape(DianaPoint dropLocation, /*DiagramContainerElement<?> container*/
			FlexoConceptInstance parentFlexoConceptInstance, ShapeRole parentShapeRole, DropScheme dropScheme) {

		DropSchemeAction dropSchemeAction = new DropSchemeAction(dropScheme,
				(FMLRTVirtualModelInstance) getNode().getDrawable().getFlexoConceptInstance().getVirtualModelInstance(), null,
				controller.getFlexoController().getEditor());
		// dropSchemeAction.setParent(container);
		dropSchemeAction.setParentInformations(parentFlexoConceptInstance, parentShapeRole);
		dropSchemeAction.setDropLocation(dropLocation);
		dropSchemeAction.escapeParameterRetrievingWhenValid = true;
		dropSchemeAction.doAction();

		return controller.getDrawing().getFederatedShape(dropSchemeAction.getPrimaryShape());

	}

	private FMLControlledDiagramConnector createNewConnector(FMLControlledDiagramShape from, FMLControlledDiagramShape to,
			LinkScheme linkScheme) {

		LinkSchemeAction action = new LinkSchemeAction(linkScheme,
				(FMLRTVirtualModelInstance) getNode().getDrawable().getFlexoConceptInstance().getVirtualModelInstance(), null,
				controller.getFlexoController().getEditor());
		action.setFromShape(getNode().getDrawable().getDiagramElement());
		action.setToShape(to.getDiagramElement());
		action.escapeParameterRetrievingWhenValid = true;
		action.doAction();

		return controller.getDrawing().getFederatedConnector(action.getNewConnector());
	}

	@Override
	public Rectangle paint(DianaGraphics drawingGraphics) {
		// System.out.println("Focused:"+nodeGR.getIsFocused());

		if (getNode().getIsSelected() && !getNode().getIsFocused()) {
			return null;
		}
		if (/*nodeGR.getIsSelected() ||*/getNode().isResizing() || getNode().isMoving()) {
			return null;
		}
		if (!getNode().getDrawing().isEditable()) {
			return null;
		}
		AffineTransform at = DianaUtils.convertNormalizedCoordinatesAT(getNode(), getNode().getDrawing().getRoot());

		// Graphics2D oldGraphics = drawingGraphics.cloneGraphics();

		drawingGraphics.setDefaultForeground(NONE);
		drawingGraphics.setDefaultBackground(DEFAULT);
		DianaRoundRectangle paletteRect = (DianaRoundRectangle) getArea().transform(at);
		DianaRoundRectangle nodeRect = (DianaRoundRectangle) this.roleRect.transform(at);
		DianaRectangle edgeRect = (DianaRectangle) this.edgeRect.transform(at);
		double arrowSize = 4/** drawingGraphics.getScale() */
		;

		paletteRect.paint(drawingGraphics);

		// 1. Node
		drawingGraphics.setDefaultForeground(NODE_FOREGROUND);
		drawingGraphics.setDefaultBackground(NODE_BACKGROUND);
		nodeRect.paint(drawingGraphics);

		// 2. Edge
		drawingGraphics.setDefaultForeground(EDGE_FOREGROUND);
		// drawingGraphics.setDefaultBackground(EDGE_BACKGROUND);
		drawingGraphics.useDefaultForegroundStyle();
		// drawingGraphics.useDefaultBackgroundStyle();
		DianaPoint eastPt, westPt, northPt, southPt;
		switch (orientation) {
			case EAST:
				eastPt = edgeRect.getEastPt();
				westPt = edgeRect.getWestPt();
				drawingGraphics.drawLine(westPt.x, westPt.y, eastPt.x - arrowSize, eastPt.y);
				drawingGraphics.drawLine(eastPt.x - arrowSize, edgeRect.y + 1, eastPt.x - arrowSize, edgeRect.y + ELEMENTS_HEIGHT - 1);
				drawingGraphics.drawLine(eastPt.x - arrowSize, edgeRect.y + 1, eastPt.x, eastPt.y);
				drawingGraphics.drawLine(eastPt.x - arrowSize, edgeRect.y + ELEMENTS_HEIGHT - 1, eastPt.x, eastPt.y);
				break;
			case WEST:
				eastPt = edgeRect.getEastPt();
				westPt = edgeRect.getWestPt();
				drawingGraphics.drawLine(eastPt.x, eastPt.y, edgeRect.x + arrowSize, eastPt.y);
				drawingGraphics.drawLine(edgeRect.x + arrowSize, edgeRect.y + 1, edgeRect.x + arrowSize, edgeRect.y + ELEMENTS_HEIGHT - 1);
				drawingGraphics.drawLine(edgeRect.x + arrowSize, edgeRect.y + 1, westPt.x, westPt.y);
				drawingGraphics.drawLine(edgeRect.x + arrowSize, edgeRect.y + ELEMENTS_HEIGHT - 1, westPt.x, westPt.y);
				break;
			case NORTH:
				northPt = edgeRect.getNorthPt();
				southPt = edgeRect.getSouthPt();
				drawingGraphics.drawLine(southPt.x, southPt.y, southPt.x, edgeRect.y + arrowSize);
				drawingGraphics.drawLine(edgeRect.x + 2, edgeRect.y + arrowSize, edgeRect.x + ELEMENTS_WIDTH - 2, edgeRect.y + arrowSize);
				drawingGraphics.drawLine(edgeRect.x + 2, edgeRect.y + arrowSize, northPt.x, northPt.y);
				drawingGraphics.drawLine(edgeRect.x + ELEMENTS_WIDTH - 2, edgeRect.y + arrowSize, northPt.x, northPt.y);
				break;
			case SOUTH:
				northPt = edgeRect.getNorthPt();
				southPt = edgeRect.getSouthPt();
				drawingGraphics.drawLine(northPt.x, northPt.y, northPt.x, southPt.y - arrowSize);
				drawingGraphics.drawLine(edgeRect.x + 2, southPt.y - arrowSize, edgeRect.x + ELEMENTS_WIDTH - 2, southPt.y - arrowSize);
				drawingGraphics.drawLine(edgeRect.x + 2, southPt.y - arrowSize, southPt.x, southPt.y);
				drawingGraphics.drawLine(edgeRect.x + ELEMENTS_WIDTH - 2, southPt.y - arrowSize, southPt.x, southPt.y);
				break;

			default:
				break;
		}

		// drawingGraphics.releaseClonedGraphics(oldGraphics);
		Rectangle returned = getNode().getDrawing().getRoot().convertNormalizedRectangleToViewCoordinates(paletteRect.getBoundingBox(),
				drawingGraphics.getScale());
		returned.x = returned.x - 20;
		returned.y = returned.y - 20;
		returned.width = returned.width + 40;
		returned.height = returned.height + 40;
		return returned;
	}

	@Override
	public boolean isClickable() {
		return false;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(ShapeGraphicalRepresentation.WIDTH_KEY)
				|| evt.getPropertyName().equals(ShapeGraphicalRepresentation.HEIGHT_KEY)) {
			updateElements(orientation);
		}
	}

	private int PALETTE_WIDTH = 16;
	private int PALETTE_HEIGHT = 2 * ELEMENTS_HEIGHT + 3 * SPACING;

	private void updateElements(SimplifiedCardinalDirection orientation) {
		setArea(makeRoundRect(getNode(), orientation));
		AffineTransform at = AffineTransform.getScaleInstance(1 / getNode().getWidth(), 1 / getNode().getHeight());

		if (orientation == SimplifiedCardinalDirection.EAST || orientation == SimplifiedCardinalDirection.WEST) {
			PALETTE_WIDTH = ELEMENTS_WIDTH + 4;
			PALETTE_HEIGHT = 2 * ELEMENTS_HEIGHT + 3 * SPACING;
		}
		else if (orientation == SimplifiedCardinalDirection.NORTH || orientation == SimplifiedCardinalDirection.SOUTH) {
			PALETTE_WIDTH = 2 * ELEMENTS_WIDTH + 3 * SPACING;
			PALETTE_HEIGHT = ELEMENTS_HEIGHT + 4;
		}

		switch (orientation) {
			case EAST:
				roleRect = (DianaRoundRectangle) new DianaRoundRectangle(
						getNode().getWidth() + SPACING + (PALETTE_WIDTH - ELEMENTS_WIDTH) / 2 + 0.5,
						(getNode().getHeight() - PALETTE_HEIGHT) / 2 + SPACING, ELEMENTS_WIDTH, ELEMENTS_HEIGHT, 2, 2, Filling.FILLED)
								.transform(at);
				edgeRect = (DianaRectangle) new DianaRectangle(getNode().getWidth() + SPACING + (PALETTE_WIDTH - ELEMENTS_WIDTH) / 2,
						(getNode().getHeight() - PALETTE_HEIGHT) / 2 + SPACING + (SPACING + ELEMENTS_HEIGHT), ELEMENTS_WIDTH,
						ELEMENTS_HEIGHT, Filling.FILLED).transform(at);
				break;
			case WEST:
				roleRect = (DianaRoundRectangle) new DianaRoundRectangle(-SPACING - ELEMENTS_WIDTH,
						(getNode().getHeight() - PALETTE_HEIGHT) / 2 + SPACING, ELEMENTS_WIDTH, ELEMENTS_HEIGHT, 2, 2, Filling.FILLED)
								.transform(at);
				edgeRect = (DianaRectangle) new DianaRectangle(-SPACING - ELEMENTS_WIDTH,
						(getNode().getHeight() - PALETTE_HEIGHT) / 2 + SPACING + (SPACING + ELEMENTS_HEIGHT), ELEMENTS_WIDTH,
						ELEMENTS_HEIGHT, Filling.FILLED).transform(at);
				break;
			case NORTH:
				roleRect = (DianaRoundRectangle) new DianaRoundRectangle((getNode().getWidth() - PALETTE_WIDTH) / 2 + SPACING,
						-SPACING - ELEMENTS_HEIGHT, ELEMENTS_WIDTH, ELEMENTS_HEIGHT, 2, 2, Filling.FILLED).transform(at);
				edgeRect = (DianaRectangle) new DianaRectangle(
						(getNode().getWidth() - PALETTE_WIDTH) / 2 + SPACING + (SPACING + ELEMENTS_WIDTH), -SPACING - ELEMENTS_HEIGHT,
						ELEMENTS_WIDTH, ELEMENTS_HEIGHT, Filling.FILLED).transform(at);
				break;
			case SOUTH:
				roleRect = (DianaRoundRectangle) new DianaRoundRectangle((getNode().getWidth() - PALETTE_WIDTH) / 2 + SPACING,
						getNode().getHeight() + SPACING + (PALETTE_HEIGHT - ELEMENTS_HEIGHT) / 2 + 0.5, ELEMENTS_WIDTH, ELEMENTS_HEIGHT, 2,
						2, Filling.FILLED).transform(at);
				edgeRect = (DianaRectangle) new DianaRectangle(
						(getNode().getWidth() - PALETTE_WIDTH) / 2 + SPACING + (SPACING + ELEMENTS_WIDTH),
						getNode().getHeight() + SPACING + (PALETTE_HEIGHT - ELEMENTS_HEIGHT) / 2 + 0.5, ELEMENTS_WIDTH, ELEMENTS_HEIGHT,
						Filling.FILLED).transform(at);
				break;

			default:
				break;
		}

	}

	private static DianaRoundRectangle makeRoundRect(ShapeNode<FMLControlledDiagramShape> node, SimplifiedCardinalDirection orientation) {
		double x, y, width, height;
		int PALETTE_WIDTH = 0, PALETTE_HEIGHT = 0;
		ShapeGraphicalRepresentation shapeGR = node.getGraphicalRepresentation();

		if (orientation == SimplifiedCardinalDirection.EAST || orientation == SimplifiedCardinalDirection.WEST) {
			PALETTE_WIDTH = ELEMENTS_WIDTH + 4;
			PALETTE_HEIGHT = 2 * ELEMENTS_HEIGHT + 3 * SPACING;
		}
		else if (orientation == SimplifiedCardinalDirection.NORTH || orientation == SimplifiedCardinalDirection.SOUTH) {
			PALETTE_WIDTH = 2 * ELEMENTS_WIDTH + 3 * SPACING;
			PALETTE_HEIGHT = ELEMENTS_HEIGHT + 4;
		}

		switch (orientation) {
			case EAST:
				x = shapeGR.getWidth() + SPACING;
				y = (shapeGR.getHeight() - PALETTE_HEIGHT) / 2;
				width = PALETTE_WIDTH;
				height = PALETTE_HEIGHT;
				return new DianaRoundRectangle(x / shapeGR.getWidth(), y / shapeGR.getHeight(), width / shapeGR.getWidth(),
						height / shapeGR.getHeight(), 13.0 / shapeGR.getWidth(), 13.0 / shapeGR.getHeight(), Filling.FILLED);
			case WEST:
				x = -SPACING - ELEMENTS_WIDTH;
				y = (shapeGR.getHeight() - PALETTE_HEIGHT) / 2;
				width = PALETTE_WIDTH;
				height = PALETTE_HEIGHT;
				return new DianaRoundRectangle(x / shapeGR.getWidth(), y / shapeGR.getHeight(), width / shapeGR.getWidth(),
						height / shapeGR.getHeight(), 13.0 / shapeGR.getWidth(), 13.0 / shapeGR.getHeight(), Filling.FILLED);
			case NORTH:
				x = (shapeGR.getWidth() - PALETTE_WIDTH) / 2;
				y = -SPACING - ELEMENTS_HEIGHT;
				width = PALETTE_WIDTH;
				height = PALETTE_HEIGHT;
				return new DianaRoundRectangle(x / shapeGR.getWidth(), y / shapeGR.getHeight(), width / shapeGR.getWidth(),
						height / shapeGR.getHeight(), 13.0 / shapeGR.getWidth(), 13.0 / shapeGR.getHeight(), Filling.FILLED);
			case SOUTH:
				x = (shapeGR.getWidth() - PALETTE_WIDTH) / 2;
				y = shapeGR.getHeight() + SPACING;
				width = PALETTE_WIDTH;
				height = PALETTE_HEIGHT;
				return new DianaRoundRectangle(x / shapeGR.getWidth(), y / shapeGR.getHeight(), width / shapeGR.getWidth(),
						height / shapeGR.getHeight(), 13.0 / shapeGR.getWidth(), 13.0 / shapeGR.getHeight(), Filling.FILLED);
			default:
				return null;
		}
	}

}
