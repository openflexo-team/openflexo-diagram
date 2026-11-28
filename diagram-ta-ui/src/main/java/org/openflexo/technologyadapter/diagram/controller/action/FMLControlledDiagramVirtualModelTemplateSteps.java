/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Fml-technologyadapter-ui, a component of the software infrastructure 
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

import java.lang.reflect.Type;

import org.openflexo.components.wizard.FlexoWizard;
import org.openflexo.components.wizard.WizardStep;
import org.openflexo.fml.controller.action.VirtualModelTemplateStep;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.gina.annotation.FIBPanel;
import org.openflexo.technologyadapter.diagram.DiagramTechnologyAdapter;
import org.openflexo.technologyadapter.diagram.fml.action.FMLControlledDiagramVirtualModelTemplate;
import org.openflexo.technologyadapter.diagram.fml.action.FMLControlledDiagramVirtualModelTemplate.DiagramSpecificationChoice;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramSpecification;
import org.openflexo.technologyadapter.diagram.rm.DiagramSpecificationResource;
import org.openflexo.toolbox.StringUtils;

/**
 * Wizard steps configuring a {@link FMLControlledDiagramVirtualModelTemplate}, inserted in the wizard creating a VirtualModel when the
 * template is chosen: the choice between a new and an existing diagram specification, then the step matching the choice.
 * 
 * @author sylvain
 *
 */
public class FMLControlledDiagramVirtualModelTemplateSteps {

	private FMLControlledDiagramVirtualModelTemplateSteps() {
	}

	/**
	 * Base of the steps: they all edit the template
	 */
	public static abstract class AbstractDiagramTemplateStep extends VirtualModelTemplateStep<FMLControlledDiagramVirtualModelTemplate> {

		protected AbstractDiagramTemplateStep(FMLControlledDiagramVirtualModelTemplate template, FlexoWizard wizard) {
			super(template, wizard);
		}

		public String getDiagramModelSlotName() {
			return getTemplate().getDiagramModelSlotName();
		}

		public void setDiagramModelSlotName(String diagramModelSlotName) {
			if (diagramModelSlotName != null && !diagramModelSlotName.equals(getDiagramModelSlotName())) {
				String oldValue = getDiagramModelSlotName();
				getTemplate().setDiagramModelSlotName(diagramModelSlotName);
				getPropertyChangeSupport().firePropertyChange("diagramModelSlotName", oldValue, diagramModelSlotName);
				checkValidity();
			}
		}

		public Type getDiagramSpecificationType() {
			return DiagramSpecification.class;
		}

		public DiagramTechnologyAdapter getDiagramTechnologyAdapter() {
			return getTemplate().getTechnologyAdapter();
		}

		protected boolean isDiagramModelSlotNameValid() {
			if (StringUtils.isEmpty(getDiagramModelSlotName())) {
				setIssueMessage(getLocales().localizedForKey("please_provide_a_valid_diagram_model_slot_name"), IssueMessageType.ERROR);
				return false;
			}
			return true;
		}
	}

	/**
	 * Choice between a new and an existing diagram specification: going next inserts the step matching the choice
	 */
	@FIBPanel("Fib/Wizard/CreateDiagramVirtualModel/ChooseDiagramSpecification.fib")
	public static class ChooseDiagramSpecification extends AbstractDiagramTemplateStep {

		private WizardStep choiceStep;

		public ChooseDiagramSpecification(FMLControlledDiagramVirtualModelTemplate template, FlexoWizard wizard) {
			super(template, wizard);
		}

		@Override
		public String getTitle() {
			return getLocales().localizedForKey("choose_diagram_specification");
		}

		public DiagramSpecificationChoice getChoice() {
			return getTemplate().getChoice();
		}

		public void setChoice(DiagramSpecificationChoice choice) {
			if (choice != getChoice()) {
				DiagramSpecificationChoice oldValue = getChoice();
				getTemplate().setChoice(choice);
				getPropertyChangeSupport().firePropertyChange("choice", oldValue, choice);
				checkValidity();
			}
		}

		@Override
		public boolean isValid() {
			return getChoice() != null;
		}

		@Override
		public boolean isTransitionalStep() {
			return true;
		}

		@Override
		public void performTransition() {
			super.performTransition();
			removeChoiceStep();
			if (getChoice() == DiagramSpecificationChoice.CreateNewDiagramSpecification) {
				choiceStep = new CreateNewDiagramSpecification(getTemplate(), getFlexoWizard());
			}
			else if (getChoice() == DiagramSpecificationChoice.UseExistingDiagramSpecification) {
				choiceStep = new UseExistingDiagramSpecification(getTemplate(), getFlexoWizard());
			}
			insertStepAfter(choiceStep);
		}

		@Override
		public void discardTransition() {
			super.discardTransition();
			removeChoiceStep();
		}

		private void removeChoiceStep() {
			if (choiceStep != null) {
				removeInsertedStep(choiceStep);
				choiceStep = null;
			}
		}
	}

	/**
	 * Use an existing diagram specification
	 */
	@FIBPanel("Fib/Wizard/CreateDiagramVirtualModel/UseExistingDiagramSpecification.fib")
	public static class UseExistingDiagramSpecification extends AbstractDiagramTemplateStep {

		public UseExistingDiagramSpecification(FMLControlledDiagramVirtualModelTemplate template, FlexoWizard wizard) {
			super(template, wizard);
		}

		@Override
		public String getTitle() {
			return getLocales().localizedForKey("choose_existing_diagram_specification");
		}

		public DiagramSpecificationResource getDiagramSpecificationResource() {
			return getTemplate().getDiagramSpecificationResource();
		}

		public void setDiagramSpecificationResource(DiagramSpecificationResource diagramSpecificationResource) {
			if (diagramSpecificationResource != getDiagramSpecificationResource()) {
				DiagramSpecificationResource oldValue = getDiagramSpecificationResource();
				getTemplate().setDiagramSpecificationResource(diagramSpecificationResource);
				getPropertyChangeSupport().firePropertyChange("diagramSpecificationResource", oldValue, diagramSpecificationResource);
				checkValidity();
			}
		}

		@Override
		public boolean isValid() {
			if (!isDiagramModelSlotNameValid()) {
				return false;
			}
			if (getDiagramSpecificationResource() == null) {
				setIssueMessage(getLocales().localizedForKey("please_choose_an_existing_diagram_specification"), IssueMessageType.ERROR);
				return false;
			}
			return true;
		}
	}

	/**
	 * Create a new diagram specification
	 */
	@FIBPanel("Fib/Wizard/CreateDiagramVirtualModel/CreateNewDiagramSpecification.fib")
	public static class CreateNewDiagramSpecification extends AbstractDiagramTemplateStep {

		public CreateNewDiagramSpecification(FMLControlledDiagramVirtualModelTemplate template, FlexoWizard wizard) {
			super(template, wizard);
		}

		@Override
		public String getTitle() {
			return getLocales().localizedForKey("creates_new_diagram_specification");
		}

		public String getNewDiagramSpecificationName() {
			return getTemplate().getNewDiagramSpecificationName();
		}

		public void setNewDiagramSpecificationName(String newDiagramSpecificationName) {
			if ((newDiagramSpecificationName == null && getNewDiagramSpecificationName() != null)
					|| (newDiagramSpecificationName != null && !newDiagramSpecificationName.equals(getNewDiagramSpecificationName()))) {
				String oldValue = getNewDiagramSpecificationName();
				getTemplate().setNewDiagramSpecificationName(newDiagramSpecificationName);
				getPropertyChangeSupport().firePropertyChange("newDiagramSpecificationName", oldValue, newDiagramSpecificationName);
				checkValidity();
			}
		}

		public String getNewDiagramSpecificationURI() {
			return getTemplate().getNewDiagramSpecificationURI();
		}

		public void setNewDiagramSpecificationURI(String newDiagramSpecificationURI) {
			if ((newDiagramSpecificationURI == null && getNewDiagramSpecificationURI() != null)
					|| (newDiagramSpecificationURI != null && !newDiagramSpecificationURI.equals(getNewDiagramSpecificationURI()))) {
				String oldValue = getNewDiagramSpecificationURI();
				getTemplate().setNewDiagramSpecificationURI(newDiagramSpecificationURI);
				getPropertyChangeSupport().firePropertyChange("newDiagramSpecificationURI", oldValue, newDiagramSpecificationURI);
				checkValidity();
			}
		}

		public RepositoryFolder<DiagramSpecificationResource, ?> getRepositoryFolder() {
			return getTemplate().getRepositoryFolder();
		}

		public void setRepositoryFolder(RepositoryFolder<DiagramSpecificationResource, ?> repositoryFolder) {
			if (repositoryFolder != getRepositoryFolder()) {
				RepositoryFolder<DiagramSpecificationResource, ?> oldValue = getRepositoryFolder();
				getTemplate().setRepositoryFolder(repositoryFolder);
				getPropertyChangeSupport().firePropertyChange("repositoryFolder", oldValue, repositoryFolder);
				checkValidity();
			}
		}

		@Override
		public boolean isValid() {
			if (!isDiagramModelSlotNameValid()) {
				return false;
			}
			return checkTemplate();
		}
	}

}
