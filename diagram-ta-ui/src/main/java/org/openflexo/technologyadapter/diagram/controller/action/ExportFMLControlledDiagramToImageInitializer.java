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

import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JFileChooser;

import org.openflexo.diana.ScreenshotBuilder.ScreenshotImage;
import org.openflexo.foundation.action.FlexoActionFactory;
import org.openflexo.foundation.action.FlexoActionRunnable;
import org.openflexo.foundation.fml.rt.FMLRTVirtualModelInstance;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.icon.IconLibrary;
import org.openflexo.swing.FlexoFileChooser;
import org.openflexo.technologyadapter.diagram.controller.FMLControlledDiagramScreenshotBuilder;
import org.openflexo.technologyadapter.diagram.controller.action.ExportDiagramToImageInitializer.ImageFileFilter;
import org.openflexo.technologyadapter.diagram.controller.diagrameditor.FMLControlledDiagramEditor;
import org.openflexo.technologyadapter.diagram.controller.diagrameditor.FMLControlledDiagramElement;
import org.openflexo.technologyadapter.diagram.fml.FMLControlledDiagramVirtualModelInstanceNature;
import org.openflexo.technologyadapter.diagram.model.DiagramElement;
import org.openflexo.technologyadapter.diagram.model.action.ExportFMLControlledDiagramToImageAction;
import org.openflexo.technologyadapter.diagram.model.action.FileIsNotAnImageFileExtension;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class ExportFMLControlledDiagramToImageInitializer
		extends ActionInitializer<ExportFMLControlledDiagramToImageAction, FlexoConceptInstance, FlexoConceptInstance> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	public ExportFMLControlledDiagramToImageInitializer(ControllerActionInitializer actionInitializer) {
		super(ExportFMLControlledDiagramToImageAction.actionType, actionInitializer);
	}

	@Override
	protected FlexoActionRunnable<ExportFMLControlledDiagramToImageAction, FlexoConceptInstance, FlexoConceptInstance> getDefaultInitializer() {
		return (e, action) -> {
			FlexoFileChooser fileChooser;
			fileChooser = new FlexoFileChooser(getController().getFlexoFrame());
			fileChooser.setCurrentDirectory(getController().getApplicationContext().getAdvancedPrefs().getLastVisitedDirectory());
			fileChooser.setDialogTitle(getController().getFlexoLocales().localizedForKey("enter_file_to_export_(png,gif,jpg)"));
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setFileFilter(new ImageFileFilter());
			if (fileChooser.showSaveDialog() == JFileChooser.APPROVE_OPTION) {
				try {
					action.setFileToExport(fileChooser.getSelectedFile());
				} catch (FileIsNotAnImageFileExtension e1) {
					FlexoController.showError("Invalid file name", e1.getMessage());
					return false;
				}
				return true;
			}

			return false;
		};
	}

	@Override
	protected FlexoActionRunnable<ExportFMLControlledDiagramToImageAction, FlexoConceptInstance, FlexoConceptInstance> getDefaultFinalizer() {
		return (e, action) -> {

			ScreenshotImage<DiagramElement<?>> screenshotImage = null;
			FMLControlledDiagramEditor editor = null;
			FMLControlledDiagramScreenshotBuilder builder = (FMLControlledDiagramScreenshotBuilder) action.getDiagramTechnologyAdapter()
					.getFMLControlledDiagramElementScreenshotBuilder();
			FlexoConceptInstance focusedObject = action.getFocusedObject();
			if (focusedObject instanceof FMLRTVirtualModelInstance) {
				builder.setDrawing(new FMLControlledDiagramEditor((FMLRTVirtualModelInstance) focusedObject, true, null, null));
				screenshotImage = builder
						.getImage(FMLControlledDiagramVirtualModelInstanceNature.getDiagram((FMLRTVirtualModelInstance) focusedObject));
			}
			else if (focusedObject != null) {
				editor = new FMLControlledDiagramEditor((FMLRTVirtualModelInstance) focusedObject.getVirtualModelInstance(), true, null,
						null);
				FMLControlledDiagramElement<?, ?> element = editor.getDrawing().getFMLControlledDiagramElements(focusedObject).get(0);
				builder.setDrawing(editor);
				screenshotImage = builder.getImage(element.getDiagramElement());
			}
			else {
				logger.warning("Could not create a screenshot");
				return false;
			}
			return action.saveScreenshot(screenshotImage);
		};
	}

	@Override
	protected Icon getEnabledIcon(
			FlexoActionFactory<ExportFMLControlledDiagramToImageAction, FlexoConceptInstance, FlexoConceptInstance> actionType) {
		return IconLibrary.EXPORT_ICON;
	}

}
