/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Flexo-foundation, a component of the software infrastructure 
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

package org.openflexo.technologyadapter.diagram.fml.action;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.diana.DianaModelFactory;
import org.openflexo.diana.DianaModelFactoryImpl;
import org.openflexo.diana.DrawingGraphicalRepresentation;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.OpenflexoRuntimeException;
import org.openflexo.foundation.fml.CreationScheme;
import org.openflexo.foundation.fml.FMLCompilationUnit;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.action.AbstractCreateVirtualModel;
import org.openflexo.foundation.fml.action.CreateEditionAction;
import org.openflexo.foundation.fml.action.CreateFlexoBehaviour;
import org.openflexo.foundation.fml.action.CreateModelSlot;
import org.openflexo.foundation.fml.action.template.VirtualModelTemplate;
import org.openflexo.foundation.fml.editionaction.AssignationAction;
import org.openflexo.foundation.fml.rm.CompilationUnitResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.task.Progress;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.diagram.DiagramTechnologyAdapter;
import org.openflexo.technologyadapter.diagram.TypedDiagramModelSlot;
import org.openflexo.technologyadapter.diagram.fml.editionaction.CreateDiagram;
import org.openflexo.technologyadapter.diagram.rm.DiagramSpecificationRepository;
import org.openflexo.technologyadapter.diagram.rm.DiagramSpecificationResource;
import org.openflexo.toolbox.StringUtils;

/**
 * {@link VirtualModelTemplate} of a VirtualModel controlling a diagram: the VirtualModel gets a {@link TypedDiagramModelSlot} typed by a
 * new or an existing diagram specification, and an `init` creation scheme that creates the diagram.<br>
 * A new diagram specification comes with an example diagram and a palette.
 * 
 * @author sylvain
 *
 */
public class FMLControlledDiagramVirtualModelTemplate extends VirtualModelTemplate<DiagramTechnologyAdapter> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FMLControlledDiagramVirtualModelTemplate.class.getPackage().getName());

	public static enum DiagramSpecificationChoice {
		CreateNewDiagramSpecification, UseExistingDiagramSpecification
	}

	private String diagramModelSlotName = "diagram";
	private DiagramSpecificationChoice choice = DiagramSpecificationChoice.CreateNewDiagramSpecification;

	private DiagramSpecificationResource diagramSpecificationResource;
	private RepositoryFolder<DiagramSpecificationResource, ?> repositoryFolder;
	private String newDiagramSpecificationName;
	private String newDiagramSpecificationURI;

	public FMLControlledDiagramVirtualModelTemplate(AbstractCreateVirtualModel<?, ?, ?> action, DiagramTechnologyAdapter technologyAdapter) {
		super(action, technologyAdapter);
	}

	@Override
	public String getName() {
		return getLocales().localizedForKey("diagram_virtual_model_template");
	}

	@Override
	public String getDescription() {
		return getLocales().localizedForKey("diagram_virtual_model_template_description");
	}

	@Override
	public void virtualModelNameChanged(String newVirtualModelName) {
		if (newVirtualModelName != null) {
			setNewDiagramSpecificationName(newVirtualModelName + "Spec");
		}
	}

	@Override
	public String getConfigurationErrorMessage() {
		if (StringUtils.isEmpty(getDiagramModelSlotName())) {
			return getLocales().localizedForKey("please_provide_a_valid_diagram_model_slot_name");
		}
		if (getChoice() == null) {
			return null;
		}
		switch (getChoice()) {
			case UseExistingDiagramSpecification:
				if (getDiagramSpecificationResource() == null) {
					return getLocales().localizedForKey("please_choose_an_existing_diagram_specification");
				}
				break;
			case CreateNewDiagramSpecification:
				if (getRepositoryFolder() == null) {
					return getLocales().localizedForKey("please_provide_a_folder_for_the_new_component");
				}
				if (StringUtils.isEmpty(getNewDiagramSpecificationName())) {
					return getLocales().localizedForKey("please_provide_a_name_for_the_new_diagram_specification");
				}
				if (StringUtils.isEmpty(getNewDiagramSpecificationURI())) {
					return getLocales().localizedForKey("please_provide_a_uri_for_the_new_diagram_specification");
				}
				try {
					new URL(getNewDiagramSpecificationURI());
				} catch (MalformedURLException e) {
					return getLocales().localizedForKey("invalid_uri_for_the_new_diagram_specification");
				}
				break;
		}
		return null;
	}

	@Override
	public int getExpectedProgressSteps() {
		return 5;
	}

	protected DrawingGraphicalRepresentation makePaletteGraphicalRepresentation() throws ModelDefinitionException {
		DianaModelFactory factory = new DianaModelFactoryImpl();
		DrawingGraphicalRepresentation gr = factory.makeDrawingGraphicalRepresentation();
		gr.setDrawWorkingArea(true);
		gr.setWidth(260);
		gr.setHeight(300);
		gr.setIsVisible(true);
		return gr;
	}

	@Override
	public void apply(VirtualModel newVirtualModel) throws OpenflexoRuntimeException {

		if (getChoice() == DiagramSpecificationChoice.CreateNewDiagramSpecification) {
			Progress.progress(getLocales().localizedForKey("create_diagram_specification"));
			CreateDiagramSpecification createDiagramSpecification = CreateDiagramSpecification.actionType
					.makeNewEmbeddedAction(getRepositoryFolder(), null, getAction());
			createDiagramSpecification.setNewDiagramSpecificationName(getNewDiagramSpecificationName());
			createDiagramSpecification.setNewDiagramSpecificationURI(getNewDiagramSpecificationURI());
			createDiagramSpecification.doAction();
			setDiagramSpecificationResource(createDiagramSpecification.getNewDiagramSpecification().getResource());

			CreateExampleDiagram createExampleDiagram = CreateExampleDiagram.actionType
					.makeNewEmbeddedAction(createDiagramSpecification.getNewDiagramSpecification(), null, getAction());
			createExampleDiagram.setNewDiagramName("ExampleDiagram");
			createExampleDiagram.setNewDiagramTitle("Example diagram");
			createExampleDiagram.doAction();

			CreateDiagramPalette createDiagramPalette = CreateDiagramPalette.actionType
					.makeNewEmbeddedAction(createDiagramSpecification.getNewDiagramSpecification(), null, getAction());
			createDiagramPalette.setNewPaletteName("Palette");
			try {
				createDiagramPalette.setGraphicalRepresentation(makePaletteGraphicalRepresentation());
			} catch (ModelDefinitionException e) {
				throw new OpenflexoRuntimeException(e);
			}
			createDiagramPalette.doAction();
		}

		// Create model slot
		Progress.progress(getLocales().localizedForKey("create_model_slot") + " " + getDiagramModelSlotName());
		CreateModelSlot action = CreateModelSlot.actionType.makeNewEmbeddedAction(newVirtualModel, null, getAction());
		action.setModelSlotName(getDiagramModelSlotName());
		action.setTechnologyAdapter(getTechnologyAdapter());
		action.setModelSlotClass(TypedDiagramModelSlot.class);
		action.setMmRes(getDiagramSpecificationResource());
		action.doAction();
		TypedDiagramModelSlot diagramModelSlot = (TypedDiagramModelSlot) action.getNewModelSlot();

		// Create init behaviour
		CreateFlexoBehaviour createCreationScheme = CreateFlexoBehaviour.actionType.makeNewEmbeddedAction(newVirtualModel, null,
				getAction());
		createCreationScheme.setFlexoBehaviourClass(CreationScheme.class);
		createCreationScheme.setFlexoBehaviourName("init");
		createCreationScheme.doAction();
		CreationScheme initBehaviour = (CreationScheme) createCreationScheme.getNewFlexoBehaviour();

		CreateEditionAction configureModelSlotAction = CreateEditionAction.actionType.makeNewEmbeddedAction(initBehaviour.getControlGraph(),
				null, getAction());
		configureModelSlotAction.setModelSlot(diagramModelSlot);
		configureModelSlotAction.setEditionActionClass(CreateDiagram.class);
		configureModelSlotAction.setAssignation(new DataBinding<>(getDiagramModelSlotName()));
		configureModelSlotAction.doAction();

		CreateDiagram createDiagramAction = (CreateDiagram) ((AssignationAction<?>) configureModelSlotAction.getNewEditionAction())
				.getAssignableAction();
		createDiagramAction.setDiagramSpecificationResource(getDiagramSpecificationResource());
		createDiagramAction.setDiagramName(new DataBinding<String>("\"diagram\""));
		createDiagramAction.setResourceCenter(new DataBinding<FlexoResourceCenter<?>>("this.resourceCenter"));
		createDiagramAction.setRelativePath("/Diagrams");
	}

	public String getDiagramModelSlotName() {
		return diagramModelSlotName;
	}

	public void setDiagramModelSlotName(String diagramModelSlotName) {
		if ((diagramModelSlotName == null && this.diagramModelSlotName != null)
				|| (diagramModelSlotName != null && !diagramModelSlotName.equals(this.diagramModelSlotName))) {
			String oldValue = this.diagramModelSlotName;
			this.diagramModelSlotName = diagramModelSlotName;
			getPropertyChangeSupport().firePropertyChange("diagramModelSlotName", oldValue, diagramModelSlotName);
		}
	}

	public DiagramSpecificationChoice getChoice() {
		return choice;
	}

	public void setChoice(DiagramSpecificationChoice choice) {
		if (choice != this.choice) {
			DiagramSpecificationChoice oldValue = this.choice;
			this.choice = choice;
			getPropertyChangeSupport().firePropertyChange("choice", oldValue, choice);
		}
	}

	public DiagramSpecificationResource getDiagramSpecificationResource() {
		return diagramSpecificationResource;
	}

	public void setDiagramSpecificationResource(DiagramSpecificationResource diagramSpecificationResource) {
		if (diagramSpecificationResource != this.diagramSpecificationResource) {
			DiagramSpecificationResource oldValue = this.diagramSpecificationResource;
			this.diagramSpecificationResource = diagramSpecificationResource;
			getPropertyChangeSupport().firePropertyChange("diagramSpecificationResource", oldValue, diagramSpecificationResource);
		}
	}

	public String getNewDiagramSpecificationURI() {
		return newDiagramSpecificationURI;
	}

	public void setNewDiagramSpecificationURI(String diagramSpecificationURI) {
		if ((diagramSpecificationURI == null && this.newDiagramSpecificationURI != null)
				|| (diagramSpecificationURI != null && !diagramSpecificationURI.equals(this.newDiagramSpecificationURI))) {
			String oldValue = this.newDiagramSpecificationURI;
			this.newDiagramSpecificationURI = diagramSpecificationURI;
			getPropertyChangeSupport().firePropertyChange("newDiagramSpecificationURI", oldValue, diagramSpecificationURI);
		}
	}

	public String getNewDiagramSpecificationName() {
		return newDiagramSpecificationName;
	}

	public void setNewDiagramSpecificationName(String newDiagramSpecificationName) {
		if ((newDiagramSpecificationName == null && this.newDiagramSpecificationName != null)
				|| (newDiagramSpecificationName != null && !newDiagramSpecificationName.equals(this.newDiagramSpecificationName))) {
			String oldValue = this.newDiagramSpecificationName;
			this.newDiagramSpecificationName = newDiagramSpecificationName;
			getPropertyChangeSupport().firePropertyChange("newDiagramSpecificationName", oldValue, newDiagramSpecificationName);
		}
	}

	/**
	 * The resource center in which the VirtualModel is created
	 */
	private FlexoResourceCenter<?> getResourceCenter() {
		FlexoObject focusedObject = getAction().getFocusedObject();
		if (focusedObject instanceof RepositoryFolder) {
			return ((RepositoryFolder<?, ?>) focusedObject).getResourceRepository().getResourceCenter();
		}
		CompilationUnitResource resource = null;
		if (focusedObject instanceof FMLCompilationUnit) {
			resource = (CompilationUnitResource) ((FMLCompilationUnit) focusedObject).getResource();
		}
		else if (focusedObject instanceof VirtualModel) {
			resource = ((VirtualModel) focusedObject).getCompilationUnitResource();
		}
		return resource != null ? resource.getResourceCenter() : null;
	}

	public RepositoryFolder<DiagramSpecificationResource, ?> getRepositoryFolder() {
		if (repositoryFolder == null) {
			FlexoResourceCenter<?> rc = getResourceCenter();
			if (rc != null) {
				DiagramSpecificationRepository<?> diagramSpecificationRepository = getTechnologyAdapter().getDiagramSpecificationRepository(rc);
				if (diagramSpecificationRepository != null) {
					return diagramSpecificationRepository.getRootFolder();
				}
			}
			return null;
		}
		return repositoryFolder;
	}

	public void setRepositoryFolder(RepositoryFolder<DiagramSpecificationResource, ?> repositoryFolder) {
		if (repositoryFolder != this.repositoryFolder) {
			RepositoryFolder<DiagramSpecificationResource, ?> oldValue = this.repositoryFolder;
			this.repositoryFolder = repositoryFolder;
			getPropertyChangeSupport().firePropertyChange("repositoryFolder", oldValue, repositoryFolder);
		}
	}

}
