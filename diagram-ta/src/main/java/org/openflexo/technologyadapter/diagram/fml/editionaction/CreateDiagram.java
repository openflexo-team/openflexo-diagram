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

package org.openflexo.technologyadapter.diagram.fml.editionaction;

import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.annotations.SeeAlso;
import org.openflexo.foundation.fml.annotations.UsageExample;
import org.openflexo.foundation.fml.editionaction.AbstractCreateResource;
import org.openflexo.foundation.fml.rt.FMLExecutionException;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.diagram.DiagramModelSlot;
import org.openflexo.technologyadapter.diagram.DiagramTechnologyAdapter;
import org.openflexo.technologyadapter.diagram.FreeDiagramModelSlot;
import org.openflexo.technologyadapter.diagram.TypedDiagramModelSlot;
import org.openflexo.technologyadapter.diagram.fml.DiagramRole;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramSpecification;
import org.openflexo.technologyadapter.diagram.model.Diagram;
import org.openflexo.technologyadapter.diagram.rm.DiagramResource;
import org.openflexo.technologyadapter.diagram.rm.DiagramResourceFactory;
import org.openflexo.technologyadapter.diagram.rm.DiagramSpecificationResource;
import org.openflexo.toolbox.StringUtils;

@ModelEntity
@ImplementationClass(CreateDiagram.CreateDiagramImpl.class)
@XMLElement
@FML(
		value = "CreateDiagram",
		description = "<html>This edition primitive addresses the creation of a new diagram.<br>"
				+ "Both the resource and the resource data are created through this action.</html>",
		examples = { @UsageExample(
				example = "myDiagram = AddDiagram(resourceName='myDiagramName',relativePath='/Diagrams',resourceCenter=myRC);",
				description = "Creates a new Diagram in resource center referenced as 'myRc', in folder '/Diagrams' and with name 'myDiagramName.diagram', and assign this new diagram to ‘myDiagram’"),
				@UsageExample(
						example = "myDiagram = DIAGRAM::AddDiagram(resourceName='myDiagramName',resourceURI='anURI',relativePath='/Diagrams',resourceCenter=myRC);",
						description = "Creates a new Diagram in resource center referenced as 'myRc', in folder '/Diagrams' with URI 'anURI' and with name 'myDiagramName.diagram' and assign this new diagram to ‘myDiagram’") },
		references = { @SeeAlso(TypedDiagramModelSlot.class), @SeeAlso(FreeDiagramModelSlot.class), @SeeAlso(GraphicalAction.class) })
public interface CreateDiagram extends AbstractCreateResource<DiagramModelSlot, Diagram, DiagramTechnologyAdapter> {

	@PropertyIdentifier(type = String.class)
	public static final String DIAGRAM_SPECIFICATION_URI_KEY = "diagramSpecificationURI";

	@PropertyIdentifier(type = DataBinding.class)
	public static final String DIAGRAM_NAME_KEY = "diagramName";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String DIAGRAM_URI_KEY = "diagramURI";

	@Getter(value = DIAGRAM_NAME_KEY)
	@XMLAttribute
	@Deprecated // Use getResourceName()
	public DataBinding<String> getDiagramName();

	@Setter(DIAGRAM_NAME_KEY)
	@Deprecated // Use setResourceName(String)
	public void setDiagramName(DataBinding<String> diagramName);

	@Getter(value = DIAGRAM_URI_KEY)
	@XMLAttribute
	@Deprecated // Use getResourceURI()
	public DataBinding<String> getDiagramURI();

	@Setter(DIAGRAM_URI_KEY)
	@Deprecated // Use setResourceURI()
	public void setDiagramURI(DataBinding<String> diagramURI);

	@Getter(value = DIAGRAM_SPECIFICATION_URI_KEY)
	@XMLAttribute
	public String getDiagramSpecificationURI();

	@Setter(DIAGRAM_SPECIFICATION_URI_KEY)
	public void setDiagramSpecificationURI(String diagramSpecificationURI);

	public DiagramSpecification getDiagramSpecification();

	public void setDiagramSpecification(DiagramSpecification diagramSpecification);

	public DiagramSpecificationResource getDiagramSpecificationResource();

	public void setDiagramSpecificationResource(DiagramSpecificationResource diagramSpecificationResource);

	public static abstract class CreateDiagramImpl extends AbstractCreateResourceImpl<DiagramModelSlot, Diagram, DiagramTechnologyAdapter>
			implements CreateDiagram {

		private static final Logger logger = Logger.getLogger(CreateDiagram.class.getPackage().getName());

		private DiagramSpecificationResource diagramSpecificationResource;
		private String diagramSpecificationURI;

		@Override
		public DataBinding<String> getDiagramName() {
			return getResourceName();
		}

		@Override
		public void setDiagramName(DataBinding<String> diagramName) {
			setResourceName(diagramName);
		}

		@Override
		public DataBinding<String> getDiagramURI() {
			return getResourceURI();
		}

		@Override
		public void setDiagramURI(DataBinding<String> diagramURI) {
			setResourceURI(diagramURI);
		}

		@Override
		public DiagramSpecificationResource getDiagramSpecificationResource() {
			if (getAssignedFlexoProperty() instanceof DiagramRole) {
				return ((DiagramRole) getAssignedFlexoProperty()).getDiagramSpecificationResource();
			}
			if (getAssignedFlexoProperty() instanceof TypedDiagramModelSlot) {
				return (DiagramSpecificationResource) ((TypedDiagramModelSlot) getAssignedFlexoProperty()).getMetaModelResource();
			}
			if (diagramSpecificationResource == null && StringUtils.isNotEmpty(diagramSpecificationURI)
					&& getModelSlotTechnologyAdapter() != null) {
				diagramSpecificationResource = (DiagramSpecificationResource) getModelSlotTechnologyAdapter().getTechnologyContextManager()
						.getResourceWithURI(diagramSpecificationURI);
				logger.info("Looked-up " + diagramSpecificationResource);
			}
			return diagramSpecificationResource;
		}

		@Override
		public void setDiagramSpecificationResource(DiagramSpecificationResource diagramSpecificationResource) {
			if (getAssignedFlexoProperty() instanceof DiagramRole) {
				((DiagramRole) getAssignedFlexoProperty()).setDiagramSpecificationResource(diagramSpecificationResource);
			}
			if (getAssignedFlexoProperty() instanceof TypedDiagramModelSlot) {
				((TypedDiagramModelSlot) getAssignedFlexoProperty()).setMetaModelResource(diagramSpecificationResource);
			}
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

		@Override
		public Type getAssignableType() {
			return Diagram.class;
		}

		@Override
		public Diagram execute(RunTimeEvaluationContext evaluationContext) throws FMLExecutionException {

			DiagramTechnologyAdapter diagramTA = getServiceManager().getTechnologyAdapterService()
					.getTechnologyAdapter(DiagramTechnologyAdapter.class);

			DiagramResource newDiagramResource;
			try {
				newDiagramResource = createResource(diagramTA, DiagramResourceFactory.class, evaluationContext,
						DiagramResourceFactory.DIAGRAM_SUFFIX, true);
				System.out.println("Return new diagram resource: " + newDiagramResource);

				newDiagramResource.setMetaModelResource(getDiagramSpecificationResource());

				System.out.println("model slot = " + getAssignedFlexoProperty());

				if (getAssignedFlexoProperty() instanceof TypedDiagramModelSlot) {
					TypedDiagramModelSlot ms = (TypedDiagramModelSlot) getAssignedFlexoProperty();
					if (ms.getTemplateDiagram() != null) {
						System.out.println("newDiagramResource.getDiagram().getGraphicalRepresentation()="
								+ newDiagramResource.getDiagram().getGraphicalRepresentation());
						System.out.println("ms.getTemplateDiagram().getGraphicalRepresentation()="
								+ ms.getTemplateDiagram().getGraphicalRepresentation());
						Diagram newDiagram = newDiagramResource.getDiagram();
						if (newDiagram.getGraphicalRepresentation() == null) {
							newDiagram.setGraphicalRepresentation(newDiagramResource.getFactory().makeDrawingGraphicalRepresentation());
						}
						newDiagramResource.getDiagram().getGraphicalRepresentation()
								.setsWith(ms.getTemplateDiagram().getGraphicalRepresentation());
						if (ms.initializeWithContents()) {
							logger.warning("Please implement initializeWithContents");
						}
					}
				}

				newDiagramResource.setIsModified();

				Diagram diagram = newDiagramResource.getResourceData();

				System.out.println("Return " + diagram);
				return diagram;
			} catch (SaveResourceException | ModelDefinitionException | FileNotFoundException | ResourceLoadingCancelledException e) {
				throw new FMLExecutionException(e);
			} catch (FlexoException e) {
				throw new FMLExecutionException(e);
			}
		}

		/*private <I> DiagramResource _makeDiagram(RunTimeEvaluationContext evaluationContext)
				throws SaveResourceException, ModelDefinitionException {
			DiagramTechnologyAdapter diagramTA = getServiceManager().getTechnologyAdapterService()
					.getTechnologyAdapter(DiagramTechnologyAdapter.class);
		
			FlexoResourceCenter<I> rc = getResourceCenter(evaluationContext);
		
			String artefactPath = getRelativePath();
			if (artefactPath == null) {
				artefactPath = "";
			}
			if (!artefactPath.endsWith(File.separator)) {
				artefactPath = artefactPath + File.separator;
			}
			artefactPath = artefactPath + JavaUtils.getClassName(getDiagramName(evaluationContext));
			if (!artefactPath.endsWith(DiagramResourceFactory.DIAGRAM_SUFFIX)) {
				artefactPath = artefactPath + DiagramResourceFactory.DIAGRAM_SUFFIX;
			}
		
			I serializationArtefact = rc.createEntry(artefactPath, rc.getBaseArtefact());
		
			DiagramResource newDiagramResource = diagramTA.getDiagramResourceFactory().makeResource(serializationArtefact, rc, true);
			newDiagramResource.setMetaModelResource(getDiagramSpecificationResource());
		
			System.out.println("model slot = " + getAssignedFlexoProperty());
		
			if (getAssignedFlexoProperty() instanceof TypedDiagramModelSlot) {
				TypedDiagramModelSlot ms = (TypedDiagramModelSlot) getAssignedFlexoProperty();
				if (ms.getTemplateDiagram() != null) {
					System.out.println("newDiagramResource.getDiagram().getGraphicalRepresentation()="
							+ newDiagramResource.getDiagram().getGraphicalRepresentation());
					System.out.println(
							"ms.getTemplateDiagram().getGraphicalRepresentation()=" + ms.getTemplateDiagram().getGraphicalRepresentation());
					Diagram newDiagram = newDiagramResource.getDiagram();
					if (newDiagram.getGraphicalRepresentation() == null) {
						newDiagram.setGraphicalRepresentation(newDiagramResource.getFactory().makeDrawingGraphicalRepresentation());
					}
					newDiagramResource.getDiagram().getGraphicalRepresentation()
							.setsWith(ms.getTemplateDiagram().getGraphicalRepresentation());
					if (ms.initializeWithContents()) {
						logger.warning("Please implement initializeWithContents");
					}
				}
			}
		
			return newDiagramResource;
		}*/

	}

}
