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

package org.openflexo.fml.diagram.controller;

import java.awt.Image;
import java.util.logging.Logger;

import org.openflexo.ApplicationContext;
import org.openflexo.components.wizard.FlexoWizard;
import org.openflexo.components.wizard.WizardStep;
import org.openflexo.fml.diagram.FMLDiagrammingIconLibrary;
import org.openflexo.fml.diagram.action.CreateFMLClassDiagram;
import org.openflexo.gina.annotation.FIBPanel;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconLibrary;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.view.controller.FlexoController;

public class CreateFMLClassDiagramWizard extends FlexoWizard {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(CreateFMLClassDiagramWizard.class.getPackage().getName());

	private final CreateFMLClassDiagram action;

	private final DescribeClassDiagram describeClassDiagram;

	public CreateFMLClassDiagramWizard(CreateFMLClassDiagram action, FlexoController controller) {
		super(controller);
		this.action = action;
		addStep(describeClassDiagram = new DescribeClassDiagram());
	}

	@Override
	public String getWizardTitle() {
		return action.getLocales().localizedForKey("create_new_class_diagram");
	}

	@Override
	public Image getDefaultPageImage() {
		return IconFactory.getImageIcon(FMLDiagrammingIconLibrary.CLASS_DIAGRAM_BIG_ICON, IconLibrary.NEW_32_32).getImage();
	}

	public DescribeClassDiagram getDescribeClassDiagram() {
		return describeClassDiagram;
	}

	/**
	 * This step is used to configure new class diagram
	 * 
	 * @author sylvain
	 *
	 */
	@FIBPanel("Fib/Wizard/DescribeClassDiagram.fib")
	public class DescribeClassDiagram extends WizardStep {

		public ApplicationContext getServiceManager() {
			return getController().getApplicationContext();
		}

		public CreateFMLClassDiagram getAction() {
			return action;
		}

		@Override
		public String getTitle() {
			return action.getLocales().localizedForKey("configure_new_class_diagram");
		}

		@Override
		public boolean isValid() {
			if (StringUtils.isEmpty(getClassDiagramName())) {
				setIssueMessage(noNameMessage(), IssueMessageType.ERROR);
				return false;
			}
			if (action.isDuplicated()) {
				setIssueMessage(duplicatedNameMessage(), IssueMessageType.ERROR);
				return false;
			}
			if (!action.isValidName()) {
				setIssueMessage(invalidNameMessage(), IssueMessageType.ERROR);
				return false;
			}
			return true;
		}

		public String getClassDiagramName() {
			return action.getClassDiagramName();
		}

		public void setClassDiagramName(String newDiagramName) {
			if ((newDiagramName == null && getClassDiagramName() != null)
					|| (newDiagramName != null && !newDiagramName.equals(getClassDiagramName()))) {
				String oldValue = getClassDiagramName();
				action.setClassDiagramName(newDiagramName);
				getPropertyChangeSupport().firePropertyChange("classDiagramName", oldValue, newDiagramName);
				checkValidity();
			}
		}

		public String getClassDiagramDescription() {
			return action.getClassDiagramDescription();
		}

		public void setClassDiagramDescription(String description) {
			if ((description == null && getClassDiagramDescription() != null)
					|| (description != null && !description.equals(getClassDiagramDescription()))) {
				String oldValue = getClassDiagramDescription();
				action.setClassDiagramDescription(description);
				getPropertyChangeSupport().firePropertyChange("classDiagramDescription", oldValue, description);
			}
		}

		private String noNameMessage() {
			return action.getLocales().localizedForKey("no_diagram_name_defined");
		}

		private String invalidNameMessage() {
			return action.getLocales().localizedForKey("invalid_name_for_new_diagram");
		}

		private String duplicatedNameMessage() {
			return action.getLocales().localizedForKey("a_class_diagram_with_that_name_already_exists");
		}
	}

}
