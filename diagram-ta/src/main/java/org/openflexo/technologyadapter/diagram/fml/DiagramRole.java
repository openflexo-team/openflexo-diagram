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
import java.util.logging.Logger;

import org.openflexo.diana.DrawingGraphicalRepresentation;
import org.openflexo.foundation.fml.FMLRepresentationContext;
import org.openflexo.foundation.fml.FMLRepresentationContext.FMLRepresentationOutput;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.rt.AbstractVirtualModelInstanceModelFactory;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.ModelObjectActorReference;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.diagram.DiagramTechnologyAdapter;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramSpecification;
import org.openflexo.technologyadapter.diagram.model.Diagram;
import org.openflexo.technologyadapter.diagram.rm.DiagramSpecificationResource;
import org.openflexo.toolbox.StringUtils;

@ModelEntity
@ImplementationClass(DiagramRole.DiagramRoleImpl.class)
@XMLElement
@FML("DiagramRole")
public interface DiagramRole extends GraphicalElementRole<Diagram, DrawingGraphicalRepresentation> {

	@PropertyIdentifier(type = String.class)
	public static final String DIAGRAM_SPECIFICATION_URI_KEY = "diagramSpecificationURI";

	@Getter(value = DIAGRAM_SPECIFICATION_URI_KEY)
	@XMLAttribute
	public String getDiagramSpecificationURI();

	@Setter(DIAGRAM_SPECIFICATION_URI_KEY)
	public void setDiagramSpecificationURI(String diagramSpecificationURI);

	public DiagramSpecification getDiagramSpecification();

	public void setDiagramSpecification(DiagramSpecification diagramSpecification);

	public DiagramSpecificationResource getDiagramSpecificationResource();

	public void setDiagramSpecificationResource(DiagramSpecificationResource diagramSpecificationResource);

	public DiagramTechnologyAdapter getDiagramTechnologyAdapter();

	/**
	 * Called to configure a {@link DiagramRole} using prototyping {@link Diagram} from metamodel
	 * 
	 * Example label is retrieved from name of connector<br>
	 * Sets placeholders positions
	 * 
	 * @param connectorRole
	 * @param metaModelConnector
	 */
	@Override
	public void bindTo(Diagram metaModelDiagram);

	public static abstract class DiagramRoleImpl extends GraphicalElementRoleImpl<Diagram, DrawingGraphicalRepresentation>
			implements DiagramRole {

		private static final Logger logger = Logger.getLogger(DiagramRole.class.getPackage().getName());

		private DiagramSpecificationResource diagramSpecificationResource;
		private String diagramSpecificationURI;

		public DiagramRoleImpl() {
			super();
		}

		@Override
		public String getTypeDescription() {
			if (getDiagramSpecification() != null) {
				return getDiagramSpecification().getName();
			}
			return getModelSlot().getModelSlotTechnologyAdapter().getLocales().localizedForKey("diagram");
		}

		@Override
		public String getFMLRepresentation(FMLRepresentationContext context) {
			FMLRepresentationOutput out = new FMLRepresentationOutput(context);
			out.append("FlexoRole " + getName() + " as Diagram conform to " + getDiagramSpecificationURI() + ";", context);
			return out.toString();
		}

		@Override
		public Type getType() {
			return Diagram.class;
		}

		@Override
		public DiagramSpecificationResource getDiagramSpecificationResource() {
			if (diagramSpecificationResource == null && StringUtils.isNotEmpty(diagramSpecificationURI)) {
				diagramSpecificationResource = (DiagramSpecificationResource) getModelSlot().getModelSlotTechnologyAdapter()
						.getTechnologyContextManager().getResourceWithURI(diagramSpecificationURI);
				logger.info("Looked-up " + diagramSpecificationResource);
			}
			return diagramSpecificationResource;
		}

		@Override
		public void setDiagramSpecificationResource(DiagramSpecificationResource diagramSpecificationResource) {
			this.diagramSpecificationResource = diagramSpecificationResource;
		}

		@Override
		public String getDiagramSpecificationURI() {
			if (diagramSpecificationResource != null) {
				return diagramSpecificationResource.getURI();
			}
			return diagramSpecificationURI;
		}

		@Override
		public void setDiagramSpecificationURI(String diagramSpecificationURI) {
			this.diagramSpecificationURI = diagramSpecificationURI;
		}

		@Override
		public DiagramSpecification getDiagramSpecification() {
			if (getDiagramSpecificationResource() != null) {
				return getDiagramSpecificationResource().getDiagramSpecification();
			}
			return null;
		}

		@Override
		public void setDiagramSpecification(DiagramSpecification diagramSpecification) {
			diagramSpecificationResource = diagramSpecification.getResource();
		}

		/**
		 * Encodes the default cloning strategy
		 * 
		 * @return
		 */
		@Override
		public RoleCloningStrategy defaultCloningStrategy() {
			return RoleCloningStrategy.Reference;
		}

		@Override
		public boolean defaultBehaviourIsToBeDeleted() {
			return false;
		}

		@Override
		public ModelObjectActorReference<Diagram> makeActorReference(Diagram object, FlexoConceptInstance fci) {
			AbstractVirtualModelInstanceModelFactory<?> factory = fci.getFactory();
			ModelObjectActorReference<Diagram> returned = factory.newInstance(ModelObjectActorReference.class);
			returned.setFlexoRole(this);
			returned.setFlexoConceptInstance(fci);
			returned.setModellingElement(object);
			return returned;
		}

		@Override
		public DiagramTechnologyAdapter getDiagramTechnologyAdapter() {
			return getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(DiagramTechnologyAdapter.class);
		}

		@Override
		public Class<? extends TechnologyAdapter> getRoleTechnologyAdapterClass() {
			return DiagramTechnologyAdapter.class;
		}

		@Override
		public Diagram makeDiagramElementInMetaModel(Diagram exampleDiagram) {
			// Not applicable
			return null;
		}

		@Override
		public Diagram getMetamodelElement() {
			return super.getMetamodelElement();
		}

		@Override
		public void bindTo(Diagram metaModelConcept) {

		}

	}
}
