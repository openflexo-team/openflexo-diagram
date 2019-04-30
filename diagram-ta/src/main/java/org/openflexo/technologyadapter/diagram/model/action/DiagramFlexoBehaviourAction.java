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

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.connie.BindingVariable;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.fml.AbstractCreationScheme;
import org.openflexo.foundation.fml.rt.FMLRTVirtualModelInstance;
import org.openflexo.foundation.fml.rt.VirtualModelInstanceObject;
import org.openflexo.foundation.fml.rt.action.AbstractCreationSchemeAction;
import org.openflexo.technologyadapter.diagram.fml.DiagramFlexoBehaviour;
import org.openflexo.technologyadapter.diagram.fml.FMLControlledDiagramVirtualModelInstanceNature;
import org.openflexo.technologyadapter.diagram.fml.binding.DiagramBehaviourBindingModel;
import org.openflexo.technologyadapter.diagram.model.Diagram;

/**
 * 
 * @author sylvain
 * 
 * @param <A>
 * @param <ES>
 * @param <O>
 */
// TODO: inherits from AbstractCreationSchemeAction
public abstract class DiagramFlexoBehaviourAction<A extends DiagramFlexoBehaviourAction<A, ES, O>, ES extends AbstractCreationScheme & DiagramFlexoBehaviour, O extends FMLRTVirtualModelInstance>
		extends AbstractCreationSchemeAction<A, ES, O> {

	private static final Logger logger = Logger.getLogger(DiagramFlexoBehaviourAction.class.getPackage().getName());

	/**
	 * Constructor to be used for creating a new action without factory
	 * 
	 * @param creationScheme
	 * @param focusedObject
	 * @param globalSelection
	 * @param editor
	 */
	public DiagramFlexoBehaviourAction(ES abstractCreationScheme, O focusedObject, Vector<VirtualModelInstanceObject> globalSelection,
			FlexoEditor editor) {
		super(abstractCreationScheme, focusedObject, globalSelection, editor);
	}

	/**
	 * Constructor to be used for creating a new action as an action embedded in another one
	 * 
	 * @param creationScheme
	 * @param focusedObject
	 * @param globalSelection
	 * @param ownerAction
	 */
	public DiagramFlexoBehaviourAction(ES abstractCreationScheme, O focusedObject, Vector<VirtualModelInstanceObject> globalSelection,
			FlexoAction<?, ?, ?> ownerAction) {
		super(abstractCreationScheme, focusedObject, globalSelection, ownerAction);
	}

	/*@Override
	public final LocalizedDelegate getLocales() {
		if (getServiceManager() != null) {
			return getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(DiagramTechnologyAdapter.class).getLocales();
		}
		return super.getLocales();
	}*/

	public abstract Diagram getDiagram();

	@Override
	public Object getValue(BindingVariable variable) {
		if (variable.getVariableName().equals(DiagramBehaviourBindingModel.TOP_LEVEL)) {
			return FMLControlledDiagramVirtualModelInstanceNature.getDiagram((FMLRTVirtualModelInstance) getVirtualModelInstance());
		}
		return super.getValue(variable);
	}

}
