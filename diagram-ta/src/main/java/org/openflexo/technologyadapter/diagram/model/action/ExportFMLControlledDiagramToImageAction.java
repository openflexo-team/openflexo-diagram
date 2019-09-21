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

package org.openflexo.technologyadapter.diagram.model.action;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.diana.ScreenshotBuilder.ScreenshotImage;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.action.FlexoActionFactory;
import org.openflexo.foundation.action.FlexoGUIAction;
import org.openflexo.foundation.action.TechnologySpecificFlexoAction;
import org.openflexo.foundation.fml.rt.FMLRTVirtualModelInstance;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.swing.ImageUtils;
import org.openflexo.swing.ImageUtils.ImageType;
import org.openflexo.technologyadapter.diagram.DiagramTechnologyAdapter;
import org.openflexo.technologyadapter.diagram.fml.FMLControlledDiagramVirtualModelInstanceNature;
import org.openflexo.technologyadapter.diagram.model.DiagramElement;

/**
 * @author vincent leilde
 * 
 */
public class ExportFMLControlledDiagramToImageAction
		extends FlexoGUIAction<ExportFMLControlledDiagramToImageAction, FlexoConceptInstance, FlexoConceptInstance>
		implements TechnologySpecificFlexoAction<DiagramTechnologyAdapter> {

	private static final Logger logger = Logger.getLogger(ExportFMLControlledDiagramToImageAction.class.getPackage().getName());

	public static final FlexoActionFactory<ExportFMLControlledDiagramToImageAction, FlexoConceptInstance, FlexoConceptInstance> actionType = new FlexoActionFactory<ExportFMLControlledDiagramToImageAction, FlexoConceptInstance, FlexoConceptInstance>(
			"export_diagram_to_image", FlexoActionFactory.exportMenu, FlexoActionFactory.defaultGroup) {

		@Override
		public boolean isEnabledForSelection(FlexoConceptInstance object, Vector<FlexoConceptInstance> globalSelection) {
			return true;
		}

		@Override
		public boolean isVisibleForSelection(FlexoConceptInstance object, Vector<FlexoConceptInstance> globalSelection) {
			if (object instanceof FMLRTVirtualModelInstance) {
				return ((FMLRTVirtualModelInstance) object).hasNature(FMLControlledDiagramVirtualModelInstanceNature.INSTANCE);
			}
			if (object != null && object.getVirtualModelInstance() instanceof FMLRTVirtualModelInstance) {
				FMLRTVirtualModelInstance vmi = (FMLRTVirtualModelInstance) object.getVirtualModelInstance();
				if (vmi != null)
					return vmi.hasNature(FMLControlledDiagramVirtualModelInstanceNature.INSTANCE);
			}
			return false;
		}

		@Override
		public ExportFMLControlledDiagramToImageAction makeNewAction(FlexoConceptInstance focusedObject,
				Vector<FlexoConceptInstance> globalSelection, FlexoEditor editor) {
			return new ExportFMLControlledDiagramToImageAction(focusedObject, globalSelection, editor);
		}

	};

	static {
		FlexoObjectImpl.addActionForClass(ExportFMLControlledDiagramToImageAction.actionType, FlexoConceptInstance.class);
	}

	/**
	 * @param actionType
	 * @param focusedObject
	 * @param globalSelection
	 * @param editor
	 */
	protected ExportFMLControlledDiagramToImageAction(FlexoConceptInstance focusedObject, Vector<FlexoConceptInstance> globalSelection,
			FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	private File fileToExport;

	@Override
	public Class<DiagramTechnologyAdapter> getTechnologyAdapterClass() {
		return DiagramTechnologyAdapter.class;
	}

	public DiagramTechnologyAdapter getDiagramTechnologyAdapter() {
		return getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(DiagramTechnologyAdapter.class);
	}

	public File getFileToExport() {
		return fileToExport;
	}

	public void setFileToExport(File fileToExport) throws FileIsNotAnImageFileExtension {
		this.fileToExport = fileToExport;
		if (getImageType() == null) {
			throw new FileIsNotAnImageFileExtension(this, fileToExport);
		}
	}

	public ImageType getImageType() {
		if (fileToExport == null) {
			return null;
		}
		for (ImageType type : ImageType.values()) {
			if (fileToExport.getName().toUpperCase().endsWith(type.getExtension().toUpperCase())) {
				return type;
			}
		}
		return null;
	}

	public boolean saveScreenshot(ScreenshotImage<DiagramElement<?>> screenshot) {
		logger.info("Saving " + fileToExport);

		try {
			ImageUtils.saveImageToFile(screenshot.image, fileToExport, getImageType());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			logger.warning("Could not save " + fileToExport.getAbsolutePath());
			return false;
		}
	}

}
