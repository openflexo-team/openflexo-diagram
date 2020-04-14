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

import java.awt.Image;
import java.util.logging.Logger;

import org.openflexo.ApplicationContext;
import org.openflexo.components.wizard.FlexoActionWizard;
import org.openflexo.components.wizard.WizardStep;
import org.openflexo.gina.annotation.FIBPanel;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconLibrary;
import org.openflexo.technologyadapter.diagram.fml.action.CreateExampleDiagram;
import org.openflexo.technologyadapter.diagram.gui.DiagramIconLibrary;
import org.openflexo.technologyadapter.diagram.model.Diagram;
import org.openflexo.toolbox.JavaUtils;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.view.controller.FlexoController;

public class CreateExampleDiagramWizard extends FlexoActionWizard<CreateExampleDiagram> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(CreateExampleDiagramWizard.class.getPackage().getName());

	private final DescribeExampleDiagram configureNewExampleDiagram;

	public CreateExampleDiagramWizard(CreateExampleDiagram action, FlexoController controller) {
		super(action, controller);
		addStep(configureNewExampleDiagram = new DescribeExampleDiagram());
	}

	@Override
	public String getWizardTitle() {
		return getAction().getLocales().localizedForKey("create_new_example_diagram");
	}

	@Override
	public Image getDefaultPageImage() {
		return IconFactory.getImageIcon(DiagramIconLibrary.DIAGRAM_BIG_ICON, IconLibrary.BIG_NEW_MARKER).getImage();
	}

	public DescribeExampleDiagram getConfigureNewConcept() {
		return configureNewExampleDiagram;
	}

	/**
	 * This step is used to configure new {@link Diagram}
	 * 
	 * @author sylvain
	 *
	 */
	@FIBPanel("Fib/Wizard/CreateElement/DescribeExampleDiagram.fib")
	public class DescribeExampleDiagram extends WizardStep {

		public ApplicationContext getServiceManager() {
			return getController().getApplicationContext();
		}

		public CreateExampleDiagram getAction() {
			return CreateExampleDiagramWizard.this.getAction();
		}

		@Override
		public String getTitle() {
			return getAction().getLocales().localizedForKey("configure_new_diagram");
		}

		@Override
		public boolean isValid() {
			if (StringUtils.isEmpty(getNewDiagramName())) {
				setIssueMessage(noNameMessage(), IssueMessageType.ERROR);
				return false;
			}

			if (getAction().getFocusedObject().getExampleDiagram(getNewDiagramName()) != null) {
				setIssueMessage(duplicatedNameMessage(), IssueMessageType.ERROR);
				return false;
			}

			if (!getNewDiagramName().equals(JavaUtils.getClassName(getNewDiagramName()))
					&& !getNewDiagramName().equals(JavaUtils.getVariableName(getNewDiagramName()))) {
				setIssueMessage(invalidNameMessage(), IssueMessageType.ERROR);
				return false;
			}

			if (StringUtils.isEmpty(getNewDiagramName())) {
				setIssueMessage(noTitleMessage(), IssueMessageType.ERROR);
				return false;
			}

			return true;
		}

		public String getNewDiagramName() {
			return getAction().getNewDiagramName();
		}

		public void setNewDiagramName(String newDiagramName) {
			if ((newDiagramName == null && getNewDiagramName() != null)
					|| (newDiagramName != null && !newDiagramName.equals(getNewDiagramName()))) {
				String oldValue = getNewDiagramName();
				getAction().setNewDiagramName(newDiagramName);
				getPropertyChangeSupport().firePropertyChange("newDiagramName", oldValue, newDiagramName);
				checkValidity();
			}
		}

		public String getNewDiagramTitle() {
			return getAction().getNewDiagramTitle();
		}

		public void setNewDiagramTitle(String newDiagramTitle) {
			if ((newDiagramTitle == null && getNewDiagramTitle() != null)
					|| (newDiagramTitle != null && !newDiagramTitle.equals(getNewDiagramTitle()))) {
				String oldValue = getNewDiagramTitle();
				getAction().setNewDiagramTitle(newDiagramTitle);
				getPropertyChangeSupport().firePropertyChange("newDiagramTitle", oldValue, newDiagramTitle);
				checkValidity();
				checkValidity();
			}
		}

		public String getDescription() {
			return getAction().getDescription();
		}

		public void setDescription(String description) {
			if ((description == null && getDescription() != null) || (description != null && !description.equals(getDescription()))) {
				String oldValue = getDescription();
				getAction().setDescription(description);
				getPropertyChangeSupport().firePropertyChange("description", oldValue, description);
			}
		}

		private String noTitleMessage() {
			return getAction().getLocales().localizedForKey("no_diagram_title_defined");
		}

		private String noNameMessage() {
			return getAction().getLocales().localizedForKey("no_diagram_name_defined");
		}

		private String invalidNameMessage() {
			return getAction().getLocales().localizedForKey("invalid_name_for_new_diagram");
		}

		private String duplicatedNameMessage() {
			return getAction().getLocales().localizedForKey("a_diagram_with_that_name_already_exists");
		}

	}

}
