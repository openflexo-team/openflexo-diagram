/**
 * 
 * Copyright (c) 2014-2015, Openflexo
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

package org.openflexo.technologyadapter.diagram.gui.widget;

import java.awt.Color;
import java.util.Hashtable;
import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.diana.ConnectorGraphicalRepresentation;
import org.openflexo.diana.DrawingGraphicalRepresentation;
import org.openflexo.diana.DianaModelFactory;
import org.openflexo.diana.GRStructureVisitor;
import org.openflexo.diana.GraphicalRepresentation;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.GRBinding.ConnectorGRBinding;
import org.openflexo.diana.GRBinding.DrawingGRBinding;
import org.openflexo.diana.GRBinding.ShapeGRBinding;
import org.openflexo.diana.GRProvider.ConnectorGRProvider;
import org.openflexo.diana.GRProvider.DrawingGRProvider;
import org.openflexo.diana.GRProvider.ShapeGRProvider;
import org.openflexo.diana.connectors.ConnectorSpecification.ConnectorType;
import org.openflexo.diana.impl.DrawingImpl;
import org.openflexo.diana.shapes.ShapeSpecification.ShapeType;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.FlexoProperty;
import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.technologyadapter.diagram.fml.ConnectorRole;
import org.openflexo.technologyadapter.diagram.fml.ShapeRole;

public class FlexoConceptPreviewRepresentation extends DrawingImpl<FlexoConcept> implements FlexoConceptPreviewConstants {

	private static final Logger logger = Logger.getLogger(FlexoConceptPreviewRepresentation.class.getPackage().getName());

	// private FlexoConceptPreviewShemaGR graphicalRepresentation;

	// private Boolean ignoreNotifications = true;

	// private Hashtable<FlexoRole, FlexoConceptPreviewShapeGR> shapesGR;
	// private Hashtable<FlexoRole, FlexoConceptPreviewConnectorGR> connectorsGR;

	/*static DianaModelFactory PREVIEW_FACTORY;
	
	static {
		try {
			PREVIEW_FACTORY = new DianaModelFactoryImpl();
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
	}*/

	private final Hashtable<FlexoRole, ConnectorFromArtifact> fromArtifacts;
	private final Hashtable<FlexoRole, ConnectorToArtifact> toArtifacts;

	public FlexoConceptPreviewRepresentation(FlexoConcept model, DianaModelFactory factory) {
		super(model, factory, PersistenceMode.UniqueGraphicalRepresentations);
		// Sylvain: commented this because not movable nor rezizable shapes
		// setEditable(false);

		fromArtifacts = new Hashtable<>();
		toArtifacts = new Hashtable<>();
	}

	@Override
	public void init() {

		final DrawingGRBinding<FlexoConcept> drawingBinding = bindDrawing(FlexoConcept.class, "flexoConcept",
				new DrawingGRProvider<FlexoConcept>() {
					@Override
					public DrawingGraphicalRepresentation provideGR(FlexoConcept drawable, DianaModelFactory factory) {
						DrawingGraphicalRepresentation returned = factory.makeDrawingGraphicalRepresentation();
						returned.setWidth(WIDTH);
						returned.setHeight(HEIGHT);
						returned.setBackgroundColor(BACKGROUND_COLOR);
						returned.setDrawWorkingArea(true);
						return returned;
					}
				});
		final ShapeGRBinding<ShapeRole> shapeBinding = bindShape(ShapeRole.class, "shapeRole", new ShapeGRProvider<ShapeRole>() {
			@Override
			public ShapeGraphicalRepresentation provideGR(ShapeRole drawable, DianaModelFactory factory) {
				/*if (drawable.getGraphicalRepresentation() == null) {
					drawable.setGraphicalRepresentation(makeDefaultShapeGR());
				}*/
				return drawable.getGraphicalRepresentation();
			}
		});

		shapeBinding.setDynamicPropertyValue(GraphicalRepresentation.TEXT, new DataBinding<String>("drawable.exampleLabel"), true);
		shapeBinding.setDynamicPropertyValue(ShapeGraphicalRepresentation.X, new DataBinding<Double>("drawable.previewX"), true);
		shapeBinding.setDynamicPropertyValue(ShapeGraphicalRepresentation.Y, new DataBinding<Double>("drawable.previewY"), true);
		shapeBinding.setDynamicPropertyValue(ShapeGraphicalRepresentation.WIDTH, new DataBinding<Double>("drawable.previewWidth"), true);
		shapeBinding.setDynamicPropertyValue(ShapeGraphicalRepresentation.HEIGHT, new DataBinding<Double>("drawable.previewHeight"), true);

		final ConnectorGRBinding<ConnectorRole> connectorBinding = bindConnector(ConnectorRole.class, "connector", shapeBinding,
				shapeBinding, new ConnectorGRProvider<ConnectorRole>() {
					@Override
					public ConnectorGraphicalRepresentation provideGR(ConnectorRole drawable, DianaModelFactory factory) {
						/*if (drawable.getGraphicalRepresentation() == null) {
							drawable.setGraphicalRepresentation(makeDefaultConnectorGR());
						}*/
						return drawable.getGraphicalRepresentation();
					}
				});
		final ShapeGRBinding<ConnectorFromArtifact> fromArtefactBinding = bindShape(ConnectorFromArtifact.class, "fromArtifact",
				new ShapeGRProvider<ConnectorFromArtifact>() {
					@Override
					public ShapeGraphicalRepresentation provideGR(ConnectorFromArtifact drawable, DianaModelFactory factory) {
						return makeFromArtefactGR();
					}

				});
		final ShapeGRBinding<ConnectorToArtifact> toArtefactBinding = bindShape(ConnectorToArtifact.class, "toArtifact",
				new ShapeGRProvider<ConnectorToArtifact>() {
					@Override
					public ShapeGraphicalRepresentation provideGR(ConnectorToArtifact drawable, DianaModelFactory factory) {
						return makeToArtefactGR();
					}

				});

		drawingBinding.addToWalkers(new GRStructureVisitor<FlexoConcept>() {

			@Override
			public void visit(FlexoConcept flexoConcept) {

				for (FlexoProperty<?> role : flexoConcept.getFlexoProperties()) {
					if (role instanceof ShapeRole) {
						if (((ShapeRole) role).getParentShapeAsDefinedInAction()) {
							drawShape(shapeBinding, (ShapeRole) role, getFlexoConcept());
							// System.out.println("Add shape " + property.getRoleName() + " under FlexoConcept");
						}
					}
					else if (role instanceof ConnectorRole) {
						ConnectorRole connectorRole = (ConnectorRole) role;
						ShapeGRBinding fromBinding;
						ShapeGRBinding toBinding;
						Object fromDrawable;
						Object toDrawable;
						if (connectorRole.getStartShapeRole() == null) {
							drawShape(fromArtefactBinding, getFromArtifact(connectorRole), getFlexoConcept());
							fromBinding = fromArtefactBinding;
							fromDrawable = getFromArtifact(connectorRole);
							// System.out.println("Add From artifact under FlexoConcept");
						}
						else {
							fromBinding = shapeBinding;
							fromDrawable = connectorRole.getStartShapeRole();
						}
						if (connectorRole.getEndShapeRole() == null) {
							drawShape(toArtefactBinding, getToArtifact(connectorRole), getFlexoConcept());
							// System.out.println("Add To artifact under FlexoConcept");
							toBinding = toArtefactBinding;
							toDrawable = getToArtifact(connectorRole);
						}
						else {
							toBinding = shapeBinding;
							toDrawable = connectorRole.getEndShapeRole();
						}

						drawConnector(connectorBinding, connectorRole, fromBinding, fromDrawable, toBinding, toDrawable);
					}
				}

			}
		});

		shapeBinding.addToWalkers(new GRStructureVisitor<ShapeRole>() {

			@Override
			public void visit(ShapeRole parentRole) {

				// NPE Protection - no impact at this stage

				FlexoConcept concept = parentRole.getFlexoConcept();
				if (concept != null) {
					for (FlexoProperty<?> role : concept.getFlexoProperties()) {
						if (role instanceof ShapeRole) {
							if (((ShapeRole) role).getParentShapeRole() == parentRole) {
								drawShape(shapeBinding, (ShapeRole) role, parentRole);
								// System.out.println("Add shape " + property.getRoleName() + " under " + parentRole.getRoleName());
							}
						}
					}
				}

			}
		});

		shapeBinding.setDynamicPropertyValue(GraphicalRepresentation.TEXT, new DataBinding<String>("drawable.exampleLabel"), true);
		connectorBinding.setDynamicPropertyValue(GraphicalRepresentation.TEXT, new DataBinding<String>("drawable.exampleLabel"), true);

	}

	@Override
	public void delete() {

		/*if (graphicalRepresentation != null) {
			graphicalRepresentation.delete();
		}*/
		/*if (getFlexoConcept() != null) {
			getFlexoConcept().deleteObserver(this);
		}*/
		/*for (FlexoRole property : getFlexoConcept().getPatternRoles()) {
			property.deleteObserver(this);
		}*/
		super.delete();
	}

	public FlexoConcept getFlexoConcept() {
		return getModel();
	}

	protected ConnectorFromArtifact getFromArtifact(ConnectorRole connector) {
		ConnectorFromArtifact returned = fromArtifacts.get(connector);
		if (returned == null) {
			returned = new ConnectorFromArtifact(connector);
			fromArtifacts.put(connector, returned);
		}
		return returned;
	}

	protected ConnectorToArtifact getToArtifact(ConnectorRole connector) {
		ConnectorToArtifact returned = toArtifacts.get(connector);
		if (returned == null) {
			returned = new ConnectorToArtifact(connector);
			toArtifacts.put(connector, returned);
		}
		return returned;
	}

	protected class ConnectorFromArtifact {

		private final ConnectorRole connector;

		protected ConnectorFromArtifact(ConnectorRole aConnector) {
			connector = aConnector;
		}

	}

	protected class ConnectorToArtifact {

		private final ConnectorRole connector;

		protected ConnectorToArtifact(ConnectorRole aConnector) {
			connector = aConnector;
		}

	}

	private ShapeGraphicalRepresentation makeFromArtefactGR() {
		ShapeGraphicalRepresentation returned = factory.makeShapeGraphicalRepresentation(ShapeType.CIRCLE);
		returned.setX(10);
		returned.setY(HEIGHT / 2 - 10);
		returned.setWidth(20);
		returned.setHeight(20);
		returned.setForeground(factory.makeForegroundStyle(new Color(255, 204, 0)));
		returned.setBackground(factory.makeColoredBackground(new Color(255, 255, 204)));
		returned.setIsFocusable(true);
		returned.setIsSelectable(false);
		return returned;
	}

	private ShapeGraphicalRepresentation makeToArtefactGR() {
		ShapeGraphicalRepresentation returned = factory.makeShapeGraphicalRepresentation(ShapeType.CIRCLE);
		returned.setX(WIDTH - 30);
		returned.setY(HEIGHT / 2 - 10);
		returned.setWidth(20);
		returned.setHeight(20);
		returned.setForeground(factory.makeForegroundStyle(new Color(255, 204, 0)));
		returned.setBackground(factory.makeColoredBackground(new Color(255, 255, 204)));
		returned.setIsFocusable(true);
		returned.setIsSelectable(false);
		return returned;
	}

	private ShapeGraphicalRepresentation makeDefaultShapeGR() {
		ShapeGraphicalRepresentation returned = factory.makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
		returned.setTextStyle(factory.makeTextStyle(DEFAULT_SHAPE_TEXT_COLOR, DEFAULT_FONT));
		returned.setX((WIDTH - DEFAULT_SHAPE_WIDTH) / 2);
		returned.setY((HEIGHT - DEFAULT_SHAPE_HEIGHT) / 2);
		returned.setWidth(DEFAULT_SHAPE_WIDTH);
		returned.setHeight(DEFAULT_SHAPE_HEIGHT);
		returned.setBackground(factory.makeColoredBackground(DEFAULT_SHAPE_BACKGROUND_COLOR));
		returned.setIsFloatingLabel(false);
		return returned;
	}

	private ConnectorGraphicalRepresentation makeDefaultConnectorGR() {
		ConnectorGraphicalRepresentation returned = factory.makeConnectorGraphicalRepresentation(ConnectorType.LINE);
		returned.setTextStyle(factory.makeTextStyle(DEFAULT_SHAPE_TEXT_COLOR, DEFAULT_FONT));
		return returned;
	}

}
