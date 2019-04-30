/**
 * 
 * Copyright (c) 2014-2015, Openflexo
 * 
 * This file is part of Flexodiagram, a component of the software infrastructure 
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

package org.openflexo.technologyadapter.diagram.fml;

import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.diana.ConnectorGraphicalRepresentation;
import org.openflexo.diana.GraphicalRepresentation;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.diagram.model.Diagram;
import org.openflexo.technologyadapter.diagram.model.DiagramConnector;
import org.openflexo.technologyadapter.diagram.model.dm.GraphicalRepresentationChanged;

@ModelEntity
@ImplementationClass(ConnectorRole.ConnectorRoleImpl.class)
@XMLElement
@FML("ConnectorRole")
public interface ConnectorRole extends GraphicalElementRole<DiagramConnector, ConnectorGraphicalRepresentation> {

	@PropertyIdentifier(type = GraphicalRepresentation.class)
	public static final String GRAPHICAL_REPRESENTATION_KEY = "graphicalRepresentation";
	@PropertyIdentifier(type = ShapeGraphicalRepresentation.class)
	public static final String ARTIFACT_FROM_GRAPHICAL_REPRESENTATION_KEY = "artifactFromGraphicalRepresentation";
	@PropertyIdentifier(type = ShapeGraphicalRepresentation.class)
	public static final String ARTIFACT_TO_GRAPHICAL_REPRESENTATION_KEY = "artifactToGraphicalRepresentation";
	@PropertyIdentifier(type = ShapeRole.class)
	public static final String START_SHAPE_ROLE_KEY = "startShapeRole";
	@PropertyIdentifier(type = ShapeRole.class)
	public static final String END_SHAPE_ROLE_KEY = "endShapeRole";

	public static GraphicalFeature<?, ?>[] AVAILABLE_CONNECTOR_FEATURES = {};

	@Getter(value = ARTIFACT_FROM_GRAPHICAL_REPRESENTATION_KEY, ignoreType = true)
	public ShapeGraphicalRepresentation getArtifactFromGraphicalRepresentation();

	@Setter(ARTIFACT_FROM_GRAPHICAL_REPRESENTATION_KEY)
	public void setArtifactFromGraphicalRepresentation(ShapeGraphicalRepresentation artifactFromGraphicalRepresentation);

	@Getter(value = ARTIFACT_TO_GRAPHICAL_REPRESENTATION_KEY, ignoreType = true)
	public ShapeGraphicalRepresentation getArtifactToGraphicalRepresentation();

	@Setter(ARTIFACT_TO_GRAPHICAL_REPRESENTATION_KEY)
	public void setArtifactToGraphicalRepresentation(ShapeGraphicalRepresentation artifactToGraphicalRepresentation);

	@Getter(value = START_SHAPE_ROLE_KEY)
	@XMLElement(context = "StartShape_")
	public ShapeRole getStartShapeRole();

	@Setter(START_SHAPE_ROLE_KEY)
	public void setStartShapeRole(ShapeRole startShapeRole);

	@Getter(value = END_SHAPE_ROLE_KEY)
	@XMLElement(context = "EndShape_")
	public ShapeRole getEndShapeRole();

	@Setter(END_SHAPE_ROLE_KEY)
	public void setEndShapeRole(ShapeRole endShapeRole);

	public boolean getStartShapeAsDefinedInAction();

	public void setStartShapeAsDefinedInAction(boolean flag);

	public boolean getEndShapeAsDefinedInAction();

	public void setEndShapeAsDefinedInAction(boolean flag);

	public List<ShapeRole> getAvailableShapeRoles();

	/**
	 * Called to configure a {@link ConnectorRole} using prototyping {@link DiagramConnector} from metamodel
	 * 
	 * Example label is retrieved from name of connector<br>
	 * Sets placeholders positions
	 * 
	 * @param connectorRole
	 * @param metaModelConnector
	 */
	@Override
	public void bindTo(DiagramConnector metaModelConnector);

	public static abstract class ConnectorRoleImpl extends GraphicalElementRoleImpl<DiagramConnector, ConnectorGraphicalRepresentation>
			implements ConnectorRole {

		private static final Logger logger = Logger.getLogger(ConnectorRole.class.getPackage().getName());

		private ShapeGraphicalRepresentation artifactFromGraphicalRepresentation;
		private ShapeGraphicalRepresentation artifactToGraphicalRepresentation;

		public ConnectorRoleImpl() {
			super();
		}

		@Override
		protected void initDefaultSpecifications() {
			super.initDefaultSpecifications();
			if (getFMLModelFactory() != null) {
				for (GraphicalFeature<?, ?> GF : AVAILABLE_CONNECTOR_FEATURES) {
					// logger.info("[CONNECTOR:" + getRoleName() + "] Nouvelle GraphicalElementSpecification for " + GF);
					GraphicalElementSpecification newGraphicalElementSpecification = getFMLModelFactory()
							.newInstance(GraphicalElementSpecification.class);
					newGraphicalElementSpecification.setFlexoRole(this);
					newGraphicalElementSpecification.setFeature(GF);
					newGraphicalElementSpecification.setReadOnly(false);
					newGraphicalElementSpecification.setMandatory(true);
					grSpecifications.add(newGraphicalElementSpecification);
				}
			}
			handlePendingGRSpecs();
		}

		@Override
		public DiagramConnector makeDiagramElementInMetaModel(Diagram exampleDiagram) {
			DiagramConnector returned = exampleDiagram.getDiagramFactory().makeNewConnector("Connector", null, null, exampleDiagram);
			return returned;
		}

		/*@Override
		public String getFMLRepresentation(FMLRepresentationContext context) {
			FMLRepresentationOutput out = new FMLRepresentationOutput(context);
			VirtualModel vm = getOwningVirtualModel();
			if (vm != null) {
				out.append("FlexoRole " + getName() + " as ConnectorSpecification from " + getOwningVirtualModel().getName() + ";",
						context);
			}
			else {
				out.append("FlexoRole " + getName() + " -- NO OWNING MODEL;", context);
			}
			return out.toString();
		}*/

		@Override
		public String getTypeDescription() {
			if (getModelSlot() == null) {
				return null;
			}
			return getModelSlot().getModelSlotTechnologyAdapter().getLocales().localizedForKey("connector");
		}

		@Override
		public ShapeGraphicalRepresentation getArtifactFromGraphicalRepresentation() {
			return artifactFromGraphicalRepresentation;
		}

		@Override
		public void setArtifactFromGraphicalRepresentation(ShapeGraphicalRepresentation artifactFromGraphicalRepresentation) {
			this.artifactFromGraphicalRepresentation = artifactFromGraphicalRepresentation;
			setChanged();
			notifyObservers(new GraphicalRepresentationChanged(this, artifactFromGraphicalRepresentation));
		}

		@Override
		public ShapeGraphicalRepresentation getArtifactToGraphicalRepresentation() {
			return artifactToGraphicalRepresentation;
		}

		@Override
		public void setArtifactToGraphicalRepresentation(ShapeGraphicalRepresentation artifactToGraphicalRepresentation) {
			this.artifactToGraphicalRepresentation = artifactToGraphicalRepresentation;
			setChanged();
			notifyObservers(new GraphicalRepresentationChanged(this, artifactToGraphicalRepresentation));
		}

		@Override
		public void setStartShapeRole(ShapeRole startShapeRole) {
			ShapeRole oldValue = getStartShapeRole();
			performSuperSetter(START_SHAPE_ROLE_KEY, startShapeRole);
			if (getFlexoConcept() != null) {
				getFlexoConcept().getPropertyChangeSupport().firePropertyChange(START_SHAPE_ROLE_KEY, oldValue, startShapeRole);
			}
		}

		@Override
		public void setEndShapeRole(ShapeRole endShapeRole) {
			ShapeRole oldValue = getEndShapeRole();
			performSuperSetter(END_SHAPE_ROLE_KEY, endShapeRole);
			if (getFlexoConcept() != null) {
				getFlexoConcept().getPropertyChangeSupport().firePropertyChange(END_SHAPE_ROLE_KEY, oldValue, endShapeRole);
			}
		}

		@Override
		public boolean getStartShapeAsDefinedInAction() {
			return getStartShapeRole() == null;
		}

		@Override
		public void setStartShapeAsDefinedInAction(boolean flag) {
			if (!flag && getFlexoConcept().getDeclaredProperties(ShapeRole.class).size() > 0) {
				setStartShapeRole(getFlexoConcept().getDeclaredProperties(ShapeRole.class).get(0));
			}
			else {
				// System.out.println("setStartShapePatternRole with null");
				setStartShapeRole(null);
			}
		}

		@Override
		public boolean getEndShapeAsDefinedInAction() {
			return getEndShapeRole() == null;
		}

		@Override
		public void setEndShapeAsDefinedInAction(boolean flag) {
			if (!flag && getFlexoConcept().getDeclaredProperties(ShapeRole.class).size() > 0) {
				setEndShapeRole(getFlexoConcept().getDeclaredProperties(ShapeRole.class).get(0));
			}
			else {
				// System.out.println("setEndShapePatternRole with null");
				setEndShapeRole(null);
			}
		}

		@Override
		public Type getType() {
			return DiagramConnector.class;
		}

		@Override
		public List<ShapeRole> getAvailableShapeRoles() {
			if (getFlexoConcept() != null) {
				return getFlexoConcept().getDeclaredProperties(ShapeRole.class);
			}
			return null;
		}

		/**
		 * Called to configure a {@link ConnectorRole} using prototyping {@link DiagramConnector} from metamodel
		 * 
		 * Example label is retrieved from name of connector<br>
		 * Sets placeholders positions
		 * 
		 * @param metaModelConnector
		 */
		@Override
		public void bindTo(DiagramConnector metaModelConnector) {

			if (metaModelConnector == null) {
				logger.warning("Could not bind to a null " + metaModelConnector);
				return;
			}

			setMetamodelElement(metaModelConnector);

			setExampleLabel(metaModelConnector.getName());

		}

	}
}
