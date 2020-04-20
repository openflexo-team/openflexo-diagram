/**
 * 
 * Copyright (c) 2014, Openflexo
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

package org.openflexo.technologyadapter.diagram.model.action;

import java.io.File;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.action.FlexoActionFactory;
import org.openflexo.foundation.action.FlexoGUIAction;
import org.openflexo.foundation.action.TechnologySpecificFlexoAction;
import org.openflexo.technologyadapter.diagram.DiagramTechnologyAdapter;
import org.openflexo.technologyadapter.diagram.model.DiagramElement;

/**
 * @author vincent leilde
 * 
 */
public class ExportDiagramToSVG extends FlexoGUIAction<ExportDiagramToSVG, DiagramElement<?>, DiagramElement<?>>
		implements TechnologySpecificFlexoAction<DiagramTechnologyAdapter> {

	private static final Logger logger = Logger.getLogger(ExportDiagramToSVG.class.getPackage().getName());

	public static final FlexoActionFactory<ExportDiagramToSVG, DiagramElement<?>, DiagramElement<?>> actionType = new FlexoActionFactory<ExportDiagramToSVG, DiagramElement<?>, DiagramElement<?>>(
			"export_diagram_to_svg", FlexoActionFactory.exportMenu, FlexoActionFactory.defaultGroup) {

		@Override
		public boolean isEnabledForSelection(DiagramElement<?> object, Vector<DiagramElement<?>> globalSelection) {
			return true;
		}

		@Override
		public boolean isVisibleForSelection(DiagramElement<?> object, Vector<DiagramElement<?>> globalSelection) {
			return true;
		}

		@Override
		public ExportDiagramToSVG makeNewAction(DiagramElement<?> focusedObject, Vector<DiagramElement<?>> globalSelection,
				FlexoEditor editor) {
			return new ExportDiagramToSVG(focusedObject, globalSelection, editor);
		}

	};

	static {
		FlexoObjectImpl.addActionForClass(ExportDiagramToSVG.actionType, DiagramElement.class);
	}

	/**
	 * @param actionType
	 * @param focusedObject
	 * @param globalSelection
	 * @param editor
	 */
	protected ExportDiagramToSVG(DiagramElement<?> focusedObject, Vector<DiagramElement<?>> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	public Class<DiagramTechnologyAdapter> getTechnologyAdapterClass() {
		return DiagramTechnologyAdapter.class;
	}

	private File exportFile;

	public File getExportFile() {
		return exportFile;
	}

	public void setExportFile(File exportFile) {
		this.exportFile = exportFile;
	}

}
