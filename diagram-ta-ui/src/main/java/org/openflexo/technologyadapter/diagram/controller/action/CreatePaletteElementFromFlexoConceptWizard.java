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
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.ApplicationContext;
import org.openflexo.components.wizard.FlexoActionWizard;
import org.openflexo.components.wizard.WizardStep;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.rm.VirtualModelResource;
import org.openflexo.gina.annotation.FIBPanel;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconLibrary;
import org.openflexo.technologyadapter.diagram.fml.DropScheme;
import org.openflexo.technologyadapter.diagram.fml.action.CreatePaletteElementFromFlexoConcept;
import org.openflexo.technologyadapter.diagram.gui.DiagramIconLibrary;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramPalette;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramSpecification;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.view.controller.FlexoController;

public class CreatePaletteElementFromFlexoConceptWizard extends FlexoActionWizard<CreatePaletteElementFromFlexoConcept> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(CreatePaletteElementFromFlexoConceptWizard.class.getPackage().getName());

	private final PutToPaletteOptions configureNewConcept;

	public CreatePaletteElementFromFlexoConceptWizard(CreatePaletteElementFromFlexoConcept action, FlexoController controller) {
		super(action, controller);
		addStep(configureNewConcept = new PutToPaletteOptions());
	}

	@Override
	public String getWizardTitle() {
		return getAction().getLocales().localizedForKey("create_palette_element_representing_this_concept");
	}

	@Override
	public Image getDefaultPageImage() {
		return IconFactory.getImageIcon(DiagramIconLibrary.DIAGRAM_PALETTE_BIG_ICON, IconLibrary.BIG_NEW_MARKER).getImage();
	}

	public PutToPaletteOptions getConfigureNewConcept() {
		return configureNewConcept;
	}

	/**
	 * This step is used to configure new {@link DiagramSpecification}
	 * 
	 * @author sylvain
	 *
	 */
	@FIBPanel("Fib/Wizard/PutToPalette/PutToPaletteFromConceptOptions.fib")
	public class PutToPaletteOptions extends WizardStep {

		public ApplicationContext getServiceManager() {
			return getController().getApplicationContext();
		}

		public CreatePaletteElementFromFlexoConcept getAction() {
			return CreatePaletteElementFromFlexoConceptWizard.this.getAction();
		}

		@Override
		public String getTitle() {
			return getAction().getLocales().localizedForKey("configure_palette_element");
		}

		@Override
		public boolean isValid() {
			if (getPalette() == null) {
				setIssueMessage(noPaletteSelectedMessage(), IssueMessageType.ERROR);
				return false;
			}

			if (StringUtils.isEmpty(getNewElementName())) {
				setIssueMessage(noNameMessage(), IssueMessageType.ERROR);
				return false;
			}

			if (getPalette().getPaletteElement(getNewElementName()) != null) {
				setIssueMessage(duplicatedPaletteElement(), IssueMessageType.ERROR);
				return false;
			}

			if (getConfigureFMLControls()) {
				if (getVirtualModel() == null) {
					setIssueMessage(noVirtualModelSelectedMessage(), IssueMessageType.ERROR);
					return false;
				}
				if (getFlexoConcept() == null) {
					setIssueMessage(noFlexoConceptSelectedMessage(), IssueMessageType.ERROR);
					return false;
				}

				if (getDropScheme() == null) {
					setIssueMessage(noDropSchemeSelectedMessage(), IssueMessageType.ERROR);
					return false;
				}

			}

			return true;
		}

		public String getNewElementName() {
			return getAction().getNewElementName();
		}

		public void setNewElementName(String newElementName) {
			if (!newElementName.equals(getNewElementName())) {
				String oldValue = getNewElementName();
				getAction().setNewElementName(newElementName);
				getPropertyChangeSupport().firePropertyChange("newElementName", oldValue, newElementName);
				checkValidity();
			}
		}

		public DiagramPalette getPalette() {
			return getAction().getPalette();
		}

		public void setPalette(DiagramPalette palette) {
			if (palette != getPalette()) {
				DiagramPalette oldValue = getPalette();
				getAction().setPalette(palette);
				getPropertyChangeSupport().firePropertyChange("palette", oldValue, palette);
				checkValidity();
			}
		}

		public boolean getConfigureFMLControls() {
			return getAction().getConfigureFMLControls();
		}

		public void setConfigureFMLControls(boolean configureFMLControls) {
			if (configureFMLControls != getConfigureFMLControls()) {
				getAction().setConfigureFMLControls(configureFMLControls);
				getPropertyChangeSupport().firePropertyChange("configureFMLControls", !configureFMLControls, configureFMLControls);
				checkValidity();
			}
		}

		public FlexoConcept getFlexoConcept() {
			return getAction().getFlexoConcept();
		}

		public void setFlexoConcept(FlexoConcept flexoConcept) {
			if (flexoConcept != getFlexoConcept()) {
				FlexoConcept oldValue = getFlexoConcept();
				getAction().setFlexoConcept(flexoConcept);
				getPropertyChangeSupport().firePropertyChange("flexoConcept", oldValue, flexoConcept);
				getPropertyChangeSupport().firePropertyChange("newElementName", null, getNewElementName());
				getPropertyChangeSupport().firePropertyChange("availableDropSchemes", null, getAvailableDropSchemes());
				checkValidity();
			}
		}

		public DropScheme getDropScheme() {
			return getAction().getDropScheme();
		}

		public void setDropScheme(DropScheme dropScheme) {
			if (dropScheme != getDropScheme()) {
				DropScheme oldValue = getDropScheme();
				getAction().setDropScheme(dropScheme);
				getPropertyChangeSupport().firePropertyChange("dropScheme", oldValue, dropScheme);
				checkValidity();
			}
		}

		public VirtualModel getVirtualModel() {
			return getAction().getVirtualModel();
		}

		public void setVirtualModel(VirtualModel virtualModel) {
			if (virtualModel != getVirtualModel()) {
				VirtualModel oldValue = getVirtualModel();
				getAction().setVirtualModel(virtualModel);
				getPropertyChangeSupport().firePropertyChange("virtualModel", oldValue, virtualModel);
				checkValidity();
			}
		}

		public VirtualModelResource getVirtualModelResource() {
			return getAction().getVirtualModelResource();
		}

		public void setVirtualModelResource(VirtualModelResource virtualModelResource) {
			if (virtualModelResource != getVirtualModelResource()) {
				VirtualModelResource oldValue = getVirtualModelResource();
				getAction().setVirtualModelResource(virtualModelResource);
				getPropertyChangeSupport().firePropertyChange("virtualModelResource", oldValue, virtualModelResource);
				checkValidity();
			}
		}

		public boolean takeScreenshotForTopLevelElement() {
			return getAction().takeScreenshotForTopLevelElement();
		}

		public void setTakeScreenshotForTopLevelElement(boolean takeScreenshotForTopLevelElement) {
			if (takeScreenshotForTopLevelElement != takeScreenshotForTopLevelElement()) {
				getAction().setTakeScreenshotForTopLevelElement(takeScreenshotForTopLevelElement);
				getPropertyChangeSupport().firePropertyChange("takeScreenshotForTopLevelElement", !takeScreenshotForTopLevelElement,
						takeScreenshotForTopLevelElement);
				checkValidity();
			}
		}

		public boolean overrideDefaultGraphicalRepresentations() {
			return getAction().overrideDefaultGraphicalRepresentations();
		}

		public void setOverrideDefaultGraphicalRepresentations(boolean overrideDefaultGraphicalRepresentations) {
			if (overrideDefaultGraphicalRepresentations != overrideDefaultGraphicalRepresentations()) {
				getAction().setOverrideDefaultGraphicalRepresentations(overrideDefaultGraphicalRepresentations);
				getPropertyChangeSupport().firePropertyChange("overrideDefaultGraphicalRepresentations",
						!overrideDefaultGraphicalRepresentations, overrideDefaultGraphicalRepresentations);
				checkValidity();
			}
		}

		public DiagramSpecification getDiagramSpecification() {
			return getAction().getDiagramSpecification();
		}

		public List<DropScheme> getAvailableDropSchemes() {
			return getAction().getAvailableDropSchemes();
		}

		public String noNameMessage() {
			return getAction().getLocales().localizedForKey("no_palette_element_name_defined");
		}

		public String noPaletteSelectedMessage() {
			return getAction().getLocales().localizedForKey("no_palette_selected");
		}

		public String duplicatedPaletteElement() {
			return getAction().getLocales().localizedForKey("a_palette_element_with_that_name_already_exists");
		}

		public String noFlexoConceptSelectedMessage() {
			return getAction().getLocales().localizedForKey("no_flexo_concept_selected");
		}

		public String noDropSchemeSelectedMessage() {
			return getAction().getLocales().localizedForKey("no_drop_scheme_selected");
		}

		public String noVirtualModelSelectedMessage() {
			return getAction().getLocales().localizedForKey("no_virtual_model_selected");
		}

	}

}
