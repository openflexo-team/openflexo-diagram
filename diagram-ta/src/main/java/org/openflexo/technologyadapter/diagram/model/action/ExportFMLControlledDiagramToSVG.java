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
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.action.FlexoActionFactory;
import org.openflexo.foundation.action.FlexoGUIAction;
import org.openflexo.foundation.action.TechnologySpecificFlexoAction;
import org.openflexo.foundation.fml.rt.FMLRTVirtualModelInstance;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.technologyadapter.diagram.DiagramTechnologyAdapter;
import org.openflexo.technologyadapter.diagram.fml.FMLControlledDiagramVirtualModelInstanceNature;

/**
 * @author sylvain
 * 
 */
public class ExportFMLControlledDiagramToSVG
		extends FlexoGUIAction<ExportFMLControlledDiagramToSVG, FlexoConceptInstance, FlexoConceptInstance>
		implements TechnologySpecificFlexoAction<DiagramTechnologyAdapter> {

	private static final Logger logger = Logger.getLogger(ExportFMLControlledDiagramToSVG.class.getPackage().getName());

	public static final FlexoActionFactory<ExportFMLControlledDiagramToSVG, FlexoConceptInstance, FlexoConceptInstance> actionType = new FlexoActionFactory<ExportFMLControlledDiagramToSVG, FlexoConceptInstance, FlexoConceptInstance>(
			"export_diagram_to_svg", FlexoActionFactory.exportMenu, FlexoActionFactory.defaultGroup) {

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
		public ExportFMLControlledDiagramToSVG makeNewAction(FlexoConceptInstance focusedObject,
				Vector<FlexoConceptInstance> globalSelection, FlexoEditor editor) {
			return new ExportFMLControlledDiagramToSVG(focusedObject, globalSelection, editor);
		}

	};

	static {
		FlexoObjectImpl.addActionForClass(ExportFMLControlledDiagramToSVG.actionType, FlexoConceptInstance.class);
	}

	/**
	 * @param actionType
	 * @param focusedObject
	 * @param globalSelection
	 * @param editor
	 */
	protected ExportFMLControlledDiagramToSVG(FlexoConceptInstance focusedObject, Vector<FlexoConceptInstance> globalSelection,
			FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	public Class<DiagramTechnologyAdapter> getTechnologyAdapterClass() {
		return DiagramTechnologyAdapter.class;
	}

	public DiagramTechnologyAdapter getDiagramTechnologyAdapter() {
		return getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(DiagramTechnologyAdapter.class);
	}

	private File exportFile;

	public File getExportFile() {
		return exportFile;
	}

	public void setExportFile(File exportFile) {
		this.exportFile = exportFile;
	}

}
