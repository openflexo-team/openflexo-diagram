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

package org.openflexo.technologyadapter.diagram.controller.action;

import java.util.List;
import java.util.logging.Logger;

import org.openflexo.fml.rt.controller.VirtualModelInstancePasteHandler;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.action.copypaste.FlexoClipboard;
import org.openflexo.foundation.action.copypaste.PastingContext;
import org.openflexo.foundation.fml.rt.FMLRTVirtualModelInstance;
import org.openflexo.foundation.fml.rt.ModelSlotInstance;
import org.openflexo.foundation.fml.rt.TypeAwareModelSlotInstance;
import org.openflexo.foundation.fml.rt.VirtualModelInstance;
import org.openflexo.pamela.factory.Clipboard;
import org.openflexo.selection.MouseSelectionManager;
import org.openflexo.technologyadapter.diagram.TypedDiagramModelSlot;
import org.openflexo.technologyadapter.diagram.controller.diagrameditor.FMLControlledDiagramEditor;
import org.openflexo.technologyadapter.diagram.fml.FMLControlledDiagramVirtualModelInstanceNature;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramSpecification;
import org.openflexo.technologyadapter.diagram.model.Diagram;
import org.openflexo.technologyadapter.diagram.model.DiagramShape;

/**
 * Paste Handler suitable for pasting something into a FMLControlledDiagram
 * 
 * @author sylvain
 * 
 */
public class FMLControlledDiagramPasteHandler extends VirtualModelInstancePasteHandler {

	private static final Logger logger = Logger.getLogger(FMLControlledDiagramPasteHandler.class.getPackage().getName());

	private final FMLRTVirtualModelInstance virtualModelInstance;
	private final FMLControlledDiagramEditor editor;

	public static final int PASTE_DELTA = 10;
	public static final String REFLEXIVE_PASTE = "ReflexivePaste";

	public FMLControlledDiagramPasteHandler(FMLRTVirtualModelInstance virtualModelInstance, FMLControlledDiagramEditor editor) {
		this.virtualModelInstance = virtualModelInstance;
		this.editor = editor;
	}

	public class FMLControlledDiagramPastingContext extends HeterogeneousPastingContext {
		public FMLControlledDiagramPastingContext(VirtualModelInstance<?, ?> holder) {
			super(holder);
		}

		public Diagram getDiagram() {
			return FMLControlledDiagramVirtualModelInstanceNature.getDiagram((FMLRTVirtualModelInstance) getPastingPointHolder());
		}
	}

	@Override
	public PastingContext<VirtualModelInstance<?, ?>> retrievePastingContext(FlexoObject focusedObject, List<FlexoObject> globalSelection,
			FlexoClipboard clipboard) {

		PastingContext<VirtualModelInstance<?, ?>> returned = null;

		// System.out.println("retrievePastingContext() in FMLControlledDiagramPasteHander, focusedObject=" + focusedObject);

		if (virtualModelInstance.hasNature(FMLControlledDiagramVirtualModelInstanceNature.INSTANCE)) {
			if (focusedObject == FMLControlledDiagramVirtualModelInstanceNature.getDiagram(virtualModelInstance)) {
				System.out.println("Lookep up " + virtualModelInstance + " from " + focusedObject);
				returned = new FMLControlledDiagramPastingContext(virtualModelInstance);
			}
		}

		if (returned == null) {
			returned = super.retrievePastingContext(focusedObject, globalSelection, clipboard);
		}

		if (returned != null) {

			Clipboard clipboardLeader = clipboard.getLeaderClipboard();

			boolean reflexivePaste = false;

			for (Object originalContent : clipboardLeader.getOriginalContents()) {
				if (originalContent == focusedObject) {
					reflexivePaste = true;
				}
			}

			if (reflexivePaste) {
				returned.setPasteProperty(REFLEXIVE_PASTE, "true");
			}
			else {
				returned.setPasteProperty(REFLEXIVE_PASTE, "false");
			}
		}

		return returned;

	}

	@Override
	public void prepareClipboardForPasting(FlexoClipboard clipboard, PastingContext<VirtualModelInstance<?, ?>> pastingContext) {

		System.out.println("Preparing clipboard for pasting in FMLControlledDiagramPasteHander");

		super.prepareClipboardForPasting(clipboard, pastingContext);
	}

	@Override
	public Object getModelSlotSpecificPastingPointHolder(ModelSlotInstance<?, ?> modelSlotInstance,
			HeterogeneousPastingContext pastingContext) {
		if (modelSlotInstance.getModelSlot() instanceof TypedDiagramModelSlot) {
			return ((TypeAwareModelSlotInstance<Diagram, DiagramSpecification, TypedDiagramModelSlot>) modelSlotInstance)
					.getAccessedResourceData();
		}
		return super.getModelSlotSpecificPastingPointHolder(modelSlotInstance, pastingContext);
	}

	@Override
	public void prepareModelSlotSpecificClipboard(Clipboard modelSlotSpecificClipboard, ModelSlotInstance<?, ?> modelSlotInstance,
			HeterogeneousPastingContext pastingContext) {

		if (modelSlotInstance == FMLControlledDiagramVirtualModelInstanceNature.getModelSlotInstance(virtualModelInstance)) {

			System.out.println("Preparing clipboard for pasting in FMLControlledDiagramPasteHander");

			Clipboard diagramClipboard = modelSlotSpecificClipboard;

			// Setting positions

			System.out.println("reflexive paste=" + (pastingContext.getPasteProperty(REFLEXIVE_PASTE).equals("true")));

			if (virtualModelInstance.hasNature(FMLControlledDiagramVirtualModelInstanceNature.INSTANCE)
					&& pastingContext.getPasteProperty(REFLEXIVE_PASTE).equals("true")) {

				System.out.println("C'est bien un reflexive paste, et voici le clipboard du diagram");
				System.out.println(diagramClipboard.debug());

				if (diagramClipboard.isSingleObject()) {
					if (diagramClipboard.getSingleContents() instanceof DiagramShape) {
						DiagramShape shapeBeingPasted = (DiagramShape) diagramClipboard.getSingleContents();
						System.out.println("La shape que je decale: " + shapeBeingPasted);
						shapeBeingPasted.getGraphicalRepresentation()
								.setX(shapeBeingPasted.getGraphicalRepresentation().getX() + PASTE_DELTA);
						shapeBeingPasted.getGraphicalRepresentation()
								.setY(shapeBeingPasted.getGraphicalRepresentation().getY() + PASTE_DELTA);
					}
				}
				else {
					for (Object o : diagramClipboard.getMultipleContents()) {
						if (o instanceof DiagramShape) {
							((DiagramShape) o).getGraphicalRepresentation()
									.setX(((DiagramShape) o).getGraphicalRepresentation().getX() + PASTE_DELTA);
							((DiagramShape) o).getGraphicalRepresentation()
									.setY(((DiagramShape) o).getGraphicalRepresentation().getY() + PASTE_DELTA);
						}
					}
				}
			}

			else if (editor.getSelectionManager() instanceof MouseSelectionManager) {
				if (diagramClipboard.isSingleObject()) {
					if (diagramClipboard.getSingleContents() instanceof DiagramShape) {
						DiagramShape shapeBeingPasted = (DiagramShape) diagramClipboard.getSingleContents();
						shapeBeingPasted.getGraphicalRepresentation()
								.setX(((MouseSelectionManager) editor.getSelectionManager()).getLastClickedPoint().getX());
						shapeBeingPasted.getGraphicalRepresentation()
								.setY(((MouseSelectionManager) editor.getSelectionManager()).getLastClickedPoint().getY());
					}
				}
				else {
					boolean deltaSet = false;
					double deltaX = Double.NaN;
					double deltaY = Double.NaN;

					for (Object o : diagramClipboard.getMultipleContents()) {
						if (o instanceof DiagramShape) {
							DiagramShape shape = (DiagramShape) o;
							if (!deltaSet) {
								deltaX = ((MouseSelectionManager) editor.getSelectionManager()).getLastClickedPoint().getX()
										- shape.getGraphicalRepresentation().getX();
								deltaY = ((MouseSelectionManager) editor.getSelectionManager()).getLastClickedPoint().getY()
										- shape.getGraphicalRepresentation().getY();
							}
							shape.getGraphicalRepresentation().setX(shape.getGraphicalRepresentation().getX() + deltaX);
							shape.getGraphicalRepresentation().setY(shape.getGraphicalRepresentation().getY() + deltaY);
							deltaSet = true;
						}
					}
				}

			}
		}
	}

}
