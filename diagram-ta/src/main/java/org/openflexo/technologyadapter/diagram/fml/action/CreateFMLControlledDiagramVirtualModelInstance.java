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

package org.openflexo.technologyadapter.diagram.fml.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.action.FlexoActionFactory;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.rt.VirtualModelInstance;
import org.openflexo.foundation.fml.rt.action.CreateFMLRTVirtualModelInstance;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.technologyadapter.diagram.fml.FMLControlledDiagramContainerNature;
import org.openflexo.technologyadapter.diagram.fml.FMLControlledDiagramVirtualModelNature;

public class CreateFMLControlledDiagramVirtualModelInstance
		extends CreateFMLRTVirtualModelInstance<CreateFMLControlledDiagramVirtualModelInstance> {

	private static final Logger logger = Logger.getLogger(CreateFMLControlledDiagramVirtualModelInstance.class.getPackage().getName());

	public static FlexoActionFactory<CreateFMLControlledDiagramVirtualModelInstance, FlexoObject, FlexoObject> actionType = new FlexoActionFactory<CreateFMLControlledDiagramVirtualModelInstance, FlexoObject, FlexoObject>(
			"fml_controlled_diagram", FlexoActionFactory.newMenu, FlexoActionFactory.defaultGroup,
			FlexoActionFactory.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateFMLControlledDiagramVirtualModelInstance makeNewAction(FlexoObject focusedObject, Vector<FlexoObject> globalSelection,
				FlexoEditor editor) {
			return new CreateFMLControlledDiagramVirtualModelInstance(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoObject container, Vector<FlexoObject> globalSelection) {
			if (container instanceof VirtualModelInstance) {
				VirtualModel containerVirtualModel = ((VirtualModelInstance<?, ?>) container).getVirtualModel();
				if (containerVirtualModel != null && containerVirtualModel.hasNature(FMLControlledDiagramContainerNature.INSTANCE)) {
					return true;
				}
			}
			if (container instanceof RepositoryFolder) {
				return true;
			}
			return false;
		}

		@Override
		public boolean isEnabledForSelection(FlexoObject container, Vector<FlexoObject> globalSelection) {
			return isVisibleForSelection(container, globalSelection);
		}

	};

	static {
		FlexoObjectImpl.addActionForClass(CreateFMLControlledDiagramVirtualModelInstance.actionType, VirtualModelInstance.class);
		FlexoObjectImpl.addActionForClass(CreateFMLControlledDiagramVirtualModelInstance.actionType, RepositoryFolder.class);
	}

	CreateFMLControlledDiagramVirtualModelInstance(FlexoObject focusedObject, Vector<FlexoObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	public CreateFMLControlledDiagramVirtualModelInstance doAction() {
		System.out.println("Creating FMLControlledDiagramVirtualModelInstance");
		return super.doAction();
	}

	@Override
	public boolean isVisible(VirtualModel virtualModel) {
		if (virtualModel.hasNature(FMLControlledDiagramVirtualModelNature.INSTANCE)) {
			return true;
		}
		else {
			return false;
		}
	}

}
