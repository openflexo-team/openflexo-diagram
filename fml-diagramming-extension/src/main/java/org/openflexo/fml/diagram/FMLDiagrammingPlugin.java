/**
 * 
 * Copyright (c) 2014-2015, Openflexo
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

package org.openflexo.fml.diagram;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import org.openflexo.connie.exception.InvalidBindingException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.fml.diagram.controller.CreateFMLClassDiagramInitializer;
import org.openflexo.fml.diagram.controller.UpdateConceptPropertiesInitializer;
import org.openflexo.fml.diagram.view.FMLClassDiagramModuleView;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.fml.FMLTechnologyAdapter;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.rt.FMLRTVirtualModelInstance;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.module.FlexoModule;
import org.openflexo.technologyadapter.diagram.controller.DiagramTechnologyAdapterController;
import org.openflexo.technologyadapter.diagram.controller.diagrameditor.FMLControlledDiagramEditor;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.TechnologyAdapterPluginController;
import org.openflexo.view.controller.model.FlexoPerspective;

/**
 * Technology-specific controller provided by {@link FMLTechnologyAdapter}<br>
 * 
 * @author sylvain
 *
 */
public class FMLDiagrammingPlugin extends TechnologyAdapterPluginController<FMLTechnologyAdapter> {

	private static final Logger logger = Logger.getLogger(FMLDiagrammingPlugin.class.getPackage().getName());

	public static final String FML_CLASS_DIAGRAM_VIRTUAL_MODEL_URI = "http://openflexo.org/fml-diagramming/FMLClassDiagram.fml";

	@Override
	public Class<FMLTechnologyAdapter> getTargetTechnologyAdapterClass() {
		return FMLTechnologyAdapter.class;
	}

	@Override
	protected String getLocalizationDirectory() {
		return "FlexoLocalization/FMLDiagrammingPlugin";
	}

	@Override
	public boolean isActivable(FlexoModule<?> module) {
		return getTechnologyAdapterControllerService().getTechnologyAdapterController(DiagramTechnologyAdapterController.class)
				.isActivated();
	}

	@Override
	protected void initializeActions(ControllerActionInitializer actionInitializer) {

		System.out.println("initializeActions for FMLDiagrammingPlugin");

		new CreateFMLClassDiagramInitializer(actionInitializer);
		new UpdateConceptPropertiesInitializer(actionInitializer);

	}

	@Override
	public boolean hasModuleViewForObject(FlexoObject object) {
		if (object instanceof FMLRTVirtualModelInstance && (((FMLRTVirtualModelInstance) object).getVirtualModel() != null)
				&& ((FMLRTVirtualModelInstance) object).getVirtualModel().getURI().equals(FML_CLASS_DIAGRAM_VIRTUAL_MODEL_URI)) {
			return true;
		}
		return false;
	}

	@Override
	public ModuleView<?> createModuleViewForObject(FlexoObject object, FlexoController controller, FlexoPerspective perspective) {
		if (object instanceof FMLRTVirtualModelInstance
				&& ((FMLRTVirtualModelInstance) object).getVirtualModel().getURI().equals(FML_CLASS_DIAGRAM_VIRTUAL_MODEL_URI)) {
			FMLRTVirtualModelInstance vmInstance = (FMLRTVirtualModelInstance) object;
			DiagramTechnologyAdapterController diagramTAController = getTechnologyAdapterControllerService()
					.getTechnologyAdapterController(DiagramTechnologyAdapterController.class);
			FMLControlledDiagramEditor editor = new FMLControlledDiagramEditor(vmInstance, false, controller,
					diagramTAController.getToolFactory());
			return new FMLClassDiagramModuleView(editor, perspective);
		}
		return null;
	}

	@Override
	public boolean handleObject(FlexoObject object) {
		if (object instanceof FlexoConceptInstance && ((FlexoConceptInstance) object).getFlexoConcept() != null
				&& ((FlexoConceptInstance) object).getFlexoConcept().getName().equals("FlexoConceptGR")) {
			return true;
		}
		return false;
	}

	@Override
	public FlexoObject getRelevantObject(FlexoObject object) {
		if (object instanceof FlexoConceptInstance && ((FlexoConceptInstance) object).getFlexoConcept() != null
				&& ((FlexoConceptInstance) object).getFlexoConcept().getName().equals("FlexoConceptGR")) {
			try {
				return (FlexoConcept) ((FlexoConceptInstance) object).execute("concept");
			} catch (TypeMismatchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NullReferenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidBindingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return object;
	}

}
