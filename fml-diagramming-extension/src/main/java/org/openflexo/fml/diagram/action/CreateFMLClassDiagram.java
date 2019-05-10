/**
 * 
 * Copyright (c) 2014-2015, Openflexo
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

package org.openflexo.fml.diagram.action;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fml.diagram.FMLDiagrammingPlugin;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.IOFlexoException;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionFactory;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.fml.CreationScheme;
import org.openflexo.foundation.fml.FMLObject;
import org.openflexo.foundation.fml.InconsistentFlexoConceptHierarchyException;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.rm.VirtualModelResource;
import org.openflexo.foundation.fml.rm.VirtualModelResourceFactory;
import org.openflexo.foundation.fml.rt.FMLRTVirtualModelInstance;
import org.openflexo.foundation.fml.rt.rm.FMLRTVirtualModelInstanceResource;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.technologyadapter.diagram.fml.action.CreateFMLControlledDiagramVirtualModelInstance;
import org.openflexo.toolbox.JavaUtils;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.view.controller.TechnologyAdapterControllerService;

public class CreateFMLClassDiagram extends FlexoAction<CreateFMLClassDiagram, VirtualModel, FMLObject> {

	private static final Logger logger = Logger.getLogger(CreateFMLClassDiagram.class.getPackage().getName());

	public static FlexoActionFactory<CreateFMLClassDiagram, VirtualModel, FMLObject> actionType = new FlexoActionFactory<CreateFMLClassDiagram, VirtualModel, FMLObject>(
			"class_diagram", FlexoActionFactory.newMenu, FlexoActionFactory.newMenuGroup2, FlexoActionFactory.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateFMLClassDiagram makeNewAction(VirtualModel focusedObject, Vector<FMLObject> globalSelection, FlexoEditor editor) {
			return new CreateFMLClassDiagram(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(VirtualModel object, Vector<FMLObject> globalSelection) {
			return object != null;
		}

		@Override
		public boolean isEnabledForSelection(VirtualModel object, Vector<FMLObject> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoObjectImpl.addActionForClass(CreateFMLClassDiagram.actionType, VirtualModel.class);
	}

	private String classDiagramName;
	private String classDiagramDescription;
	private FMLRTVirtualModelInstance newClassDiagram;

	private FMLRTVirtualModelInstance newFMLControlledDiagram;

	private CreateFMLClassDiagram(VirtualModel focusedObject, Vector<FMLObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	private <I> RepositoryFolder<?, I> getRepositoryFolder(VirtualModelResource resource, FlexoResourceCenter<I> rc) throws IOException {
		RepositoryFolder<FlexoResource<?>, I> vmFolder = rc.getRepositoryFolder(resource);
		I vmContainerDirectory = vmFolder.getSerializationArtefact();
		String directoryName = resource.getName();
		if (!directoryName.endsWith(VirtualModelResourceFactory.FML_SUFFIX)) {
			directoryName = directoryName + VirtualModelResourceFactory.FML_SUFFIX;
		}
		I vmDirectory = rc.getDirectory(directoryName, vmContainerDirectory);
		RepositoryFolder<FlexoResource<?>, I> returned = rc.getRepositoryFolder(vmDirectory, true);
		return returned;
	}

	@Override
	protected void doAction(Object context)
			throws NotImplementedException, InvalidParameterException, InconsistentFlexoConceptHierarchyException, IOFlexoException {

		VirtualModelResource classDiagramVirtualModelResource = getServiceManager().getVirtualModelLibrary()
				.getVirtualModelResource(FMLDiagrammingPlugin.FML_CLASS_DIAGRAM_VIRTUAL_MODEL_URI);
		VirtualModel classDiagramVirtualModel = classDiagramVirtualModelResource.getVirtualModel();

		FlexoResourceCenter<?> resourceCenter = getFocusedObject().getResource().getResourceCenter();

		RepositoryFolder<?, ?> vmFolder;
		try {
			vmFolder = getRepositoryFolder(getFocusedObject().getVirtualModelResource(), resourceCenter);
		} catch (IOException e) {
			throw new IOFlexoException(e);
		}

		CreateFMLControlledDiagramVirtualModelInstance action = CreateFMLControlledDiagramVirtualModelInstance.actionType
				.makeNewAction(vmFolder, null, getEditor());
		action.setNewVirtualModelInstanceName(getClassDiagramName());
		action.setNewVirtualModelInstanceTitle(getClassDiagramName());
		action.setVirtualModel(classDiagramVirtualModel);
		CreationScheme creationScheme = classDiagramVirtualModel.getCreationSchemes().get(0);
		action.setCreationScheme(creationScheme);
		action.setParameterValue(creationScheme.getParameters().get(0), getFocusedObject().getResource());
		action.setSkipChoosePopup(true);

		action.doAction();

		newFMLControlledDiagram = action.getNewVirtualModelInstance();

	}

	public FMLRTVirtualModelInstance getNewFMLControlledDiagram() {
		return newFMLControlledDiagram;
	}

	public String getClassDiagramName() {
		return classDiagramName;
	}

	public void setClassDiagramName(String classDiagramName) {
		if ((classDiagramName == null && this.classDiagramName != null)
				|| (classDiagramName != null && !classDiagramName.equals(this.classDiagramName))) {
			String oldValue = this.classDiagramName;
			this.classDiagramName = classDiagramName;
			getPropertyChangeSupport().firePropertyChange("classDiagramName", oldValue, classDiagramName);
		}
	}

	public String getClassDiagramDescription() {
		return classDiagramDescription;
	}

	public void setClassDiagramDescription(String classDiagramDescription) {
		if ((classDiagramDescription == null && this.classDiagramDescription != null)
				|| (classDiagramDescription != null && !classDiagramDescription.equals(this.classDiagramDescription))) {
			String oldValue = this.classDiagramDescription;
			this.classDiagramDescription = classDiagramDescription;
			getPropertyChangeSupport().firePropertyChange("classDiagramDescription", oldValue, classDiagramDescription);
		}
	}

	public FMLRTVirtualModelInstance getNewClassDiagram() {
		return newClassDiagram;
	}

	@Override
	public boolean isValid() {
		if (StringUtils.isEmpty(getClassDiagramName())) {
			return false;
		}
		if (isDuplicated()) {
			return false;
		}
		return true;
	}

	public boolean isDuplicated() {
		for (FMLRTVirtualModelInstanceResource vmiRes : ((VirtualModelResource) getFocusedObject().getResource()).getContainedVMI()) {
			if (vmiRes.getName().equals(getClassDiagramName())) {
				return true;
			}
		}
		return false;
	}

	public boolean isValidName() {
		if (!getClassDiagramName().equals(JavaUtils.getClassName(getClassDiagramName()))
				&& !getClassDiagramName().equals(JavaUtils.getVariableName(getClassDiagramName()))) {
			return false;
		}
		return true;
	}

	@Override
	public LocalizedDelegate getLocales() {
		if (getServiceManager() != null) {
			TechnologyAdapterControllerService tacService = getServiceManager().getService(TechnologyAdapterControllerService.class);
			FMLDiagrammingPlugin plugin = tacService.getPlugin(FMLDiagrammingPlugin.class);
			return plugin.getLocales();
		}
		return super.getLocales();
	}

}
