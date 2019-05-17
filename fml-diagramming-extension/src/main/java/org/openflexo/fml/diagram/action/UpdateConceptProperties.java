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

package org.openflexo.fml.diagram.action;

import java.lang.reflect.InvocationTargetException;
import java.security.InvalidParameterException;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.ApplicationContext;
import org.openflexo.connie.exception.InvalidBindingException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.fml.diagram.FMLDiagrammingPlugin;
import org.openflexo.fml.diagram.view.FMLClassDiagramModuleView;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionFactory;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.action.TechnologySpecificFlexoAction;
import org.openflexo.foundation.fml.FMLObject;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.rt.VirtualModelInstance.ObjectLookupResult;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.technologyadapter.diagram.DiagramTechnologyAdapter;
import org.openflexo.view.controller.InteractiveFlexoEditor;
import org.openflexo.view.controller.TechnologyAdapterControllerService;

/**
 * This action is called to force refresh properties
 * 
 * @author sylvain
 * 
 */
public class UpdateConceptProperties extends FlexoAction<UpdateConceptProperties, FlexoConcept, FMLObject>
		implements TechnologySpecificFlexoAction<DiagramTechnologyAdapter> {

	private static final Logger logger = Logger.getLogger(UpdateConceptProperties.class.getPackage().getName());

	public static FlexoActionFactory<UpdateConceptProperties, FlexoConcept, FMLObject> actionType = new FlexoActionFactory<UpdateConceptProperties, FlexoConcept, FMLObject>(
			"update_properties", FlexoActionFactory.defaultGroup, FlexoActionFactory.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public UpdateConceptProperties makeNewAction(FlexoConcept focusedObject, Vector<FMLObject> globalSelection, FlexoEditor editor) {
			return new UpdateConceptProperties(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoConcept object, Vector<FMLObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(FlexoConcept object, Vector<FMLObject> globalSelection) {
			return true;
		}

	};

	static {
		FlexoObjectImpl.addActionForClass(UpdateConceptProperties.actionType, FlexoConcept.class);
	}

	private UpdateConceptProperties(FlexoConcept focusedObject, Vector<FMLObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	public Class<DiagramTechnologyAdapter> getTechnologyAdapterClass() {
		return DiagramTechnologyAdapter.class;
	}

	@Override
	protected void doAction(Object context) throws NotImplementedException, InvalidParameterException {
		System.out.println("Hop, UpdateConceptProperties for " + getFocusedObject());

		if (getEditor() instanceof InteractiveFlexoEditor) {
			ApplicationContext ac = ((InteractiveFlexoEditor) getEditor()).getApplicationContext();
			if (ac.getModuleLoader().getActiveModule() != null) {
				System.out.println("moduleView=" + ac.getModuleLoader().getActiveModule().getController().getCurrentModuleView());
				if (ac.getModuleLoader().getActiveModule().getController().getCurrentModuleView() instanceof FMLClassDiagramModuleView) {
					FMLClassDiagramModuleView diagramModuleView = (FMLClassDiagramModuleView) ac.getModuleLoader().getActiveModule()
							.getController().getCurrentModuleView();
					ObjectLookupResult lookup = diagramModuleView.getEditor().getVirtualModelInstance().lookup(getFocusedObject());
					System.out.println("lookup=" + lookup);
					System.out.println("fci=" + lookup.flexoConceptInstance);
					System.out.println("property=" + lookup.property);
					if (lookup.flexoConceptInstance != null) {
						try {
							lookup.flexoConceptInstance.execute("this.updateProperties()");
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
				}
			}
		}

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
