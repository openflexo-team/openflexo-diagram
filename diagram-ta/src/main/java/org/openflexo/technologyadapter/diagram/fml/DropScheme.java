/**
 * 
 * Copyright (c) 2014-2015, Openflexo
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

package org.openflexo.technologyadapter.diagram.fml;

import java.util.List;

import org.openflexo.foundation.fml.AbstractCreationScheme;
import org.openflexo.foundation.fml.FMLMigration;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.FlexoConceptInstanceType;
import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.annotations.FMLAttribute;
import org.openflexo.foundation.fml.annotations.FMLAttribute.AttributeKind;
import org.openflexo.gina.annotation.FIBPanel;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.Updater;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.diagram.fml.binding.DropSchemeBindingModel;
import org.openflexo.toolbox.StringUtils;

/**
 * A creation behaviour triggered while dropping a shape from a palette
 * 
 * @author sylvain
 *
 */
@FIBPanel("Fib/DropSchemePanel.fib")
@ModelEntity
@ImplementationClass(DropScheme.DropSchemeImpl.class)
@XMLElement
@FML("DropScheme")
public interface DropScheme extends AbstractCreationScheme, DiagramFlexoBehaviour {

	@PropertyIdentifier(type = FlexoConceptInstanceType.class)
	public static final String TARGET_TYPE_KEY = "targetType";
	@PropertyIdentifier(type = String.class)
	public static final String TARGET_KEY = "target";
	@PropertyIdentifier(type = ShapeRole.class)
	public static final String TARGET_SHAPE_ROLE_KEY = "targetShapeRole";
	@PropertyIdentifier(type = FlexoConcept.class)
	public static final String TARGET_FLEXO_CONCEPT_KEY = "targetFlexoConcept";

	public static final String TOP_TARGET_KEY = "topTarget";

	/**
	 * Type (FlexoConcept) which may receive this drop, null value means top level
	 * 
	 * @return
	 */
	@Getter(value = TARGET_TYPE_KEY, ignoreType = true)
	@FMLAttribute(value = TARGET_TYPE_KEY, kind = AttributeKind.Type, required = false)
	public FlexoConceptInstanceType getTargetType();

	@Setter(TARGET_TYPE_KEY)
	public void setTargetType(FlexoConceptInstanceType from);

	/**
	 * We define an updater for TARGET_TYPE property because we need to translate supplied Type to valid TypingSpace
	 * 
	 * @param type
	 */
	@Updater(TARGET_TYPE_KEY)
	public void updateTargetType(FlexoConceptInstanceType type);

	@FMLMigration
	@Deprecated
	@Getter(value = TARGET_KEY)
	@XMLAttribute
	public String _getTarget();

	@FMLMigration
	@Deprecated
	@Setter(TARGET_KEY)
	public void _setTarget(String target);

	@Getter(value = TARGET_SHAPE_ROLE_KEY)
	@XMLElement
	public ShapeRole getTargetShapeRole();

	@Setter(TARGET_SHAPE_ROLE_KEY)
	public void setTargetShapeRole(ShapeRole targetShapeRole);

	@FMLMigration
	@Deprecated
	public FlexoConcept getTargetFlexoConcept();

	@FMLMigration
	@Deprecated
	public void setTargetFlexoConcept(FlexoConcept targetFlexoConcept);

	public boolean isValidTarget(FlexoConcept aTarget, FlexoRole<?> contextRole);

	public List<ShapeRole> getAvailableTargetShapeRoles();

	public boolean targetHasMultipleRoles();

	public boolean isTopTarget();

	public void setAsTopTarget();

	public static abstract class DropSchemeImpl extends AbstractCreationSchemeImpl implements DropScheme {

		private static final String TOP = "top";

		private String target = TOP;
		private ShapeRole targetShapeRole;
		private FlexoConcept lastKnownTargetFlexoConcept;

		private FlexoConceptInstanceType targetType;

		public DropSchemeImpl() {
			super();
		}

		@Override
		public FlexoConceptInstanceType getTargetType() {
			if (targetType != null) {
				return targetType;
			}
			if (getTargetFlexoConcept() != null) {
				return getTargetFlexoConcept().getInstanceType();
			}
			return getTopLevelInstanceType();
		}

		@Override
		public void setTargetType(FlexoConceptInstanceType targetType) {
			if ((targetType == null && this.targetType != null) || (targetType != null && !targetType.equals(this.targetType))) {
				FlexoConceptInstanceType oldValue = this.targetType;
				this.targetType = targetType;
				getPropertyChangeSupport().firePropertyChange(TARGET_TYPE_KEY, oldValue, targetType);
			}
		}

		/**
		 * We define an updater for TARGET_TYPE property because we need to translate supplied Type to valid TypingSpace
		 * 
		 * This updater is called during updateWith() processing (generally applied during the FML parsing phases)
		 * 
		 * @param type
		 */
		@Override
		public void updateTargetType(FlexoConceptInstanceType type) {

			if (getDeclaringCompilationUnit() != null && type != null) {
				setTargetType(type.translateTo(getDeclaringCompilationUnit().getTypingSpace()));
			}
			else {
				setTargetType(type);
			}
		}

		@Override
		public String _getTarget() {
			return target;
		}

		@Override
		public void _setTarget(String target) {
			if (requireChange(this.target, target)) {
				FlexoConcept oldValue = getTargetFlexoConcept();
				this.target = target;
				getPropertyChangeSupport().firePropertyChange(TARGET_FLEXO_CONCEPT_KEY, oldValue, getTargetFlexoConcept());
			}
		}

		@Override
		public void finalizeDeserialization() {
			super.finalizeDeserialization();
			getTargetFlexoConcept();
		}

		private FlexoConcept targetFlexoConcept;

		@Override
		@Deprecated
		public FlexoConcept getTargetFlexoConcept() {
			if (targetType != null && targetType.getFlexoConcept() != null) {
				return targetType.getFlexoConcept();
			}
			if (targetFlexoConcept != null) {
				return targetFlexoConcept;
			}

			if (StringUtils.isEmpty(_getTarget())) {
				return null;
			}
			if (getOwningVirtualModel() != null) {
				targetFlexoConcept = getOwningVirtualModel().getFlexoConcept(_getTarget());
				if (lastKnownTargetFlexoConcept != targetFlexoConcept) {
					FlexoConcept oldValue = lastKnownTargetFlexoConcept;
					lastKnownTargetFlexoConcept = targetFlexoConcept;
					getPropertyChangeSupport().firePropertyChange(TARGET_FLEXO_CONCEPT_KEY, oldValue, targetFlexoConcept);
				}
				return targetFlexoConcept;
			}
			return null;
		}

		@Override
		@Deprecated
		public void setTargetFlexoConcept(FlexoConcept aTargetFlexoConcept) {
			FlexoConcept oldTargetFlexoConcept = this.targetFlexoConcept;
			this.targetFlexoConcept = aTargetFlexoConcept;
			if (aTargetFlexoConcept != null) {
				setTargetType(aTargetFlexoConcept.getInstanceType());
			}
			_setTarget(aTargetFlexoConcept != null ? aTargetFlexoConcept.getURI() : null);
			getPropertyChangeSupport().firePropertyChange(TARGET_FLEXO_CONCEPT_KEY, oldTargetFlexoConcept, aTargetFlexoConcept);
			// updateBindingModels();
		}

		@Override
		public boolean isTopTarget() {
			return getTargetType() == null || getTargetType().equals(getTopLevelInstanceType());
		}

		@Override
		public void setAsTopTarget() {
			setTargetType(getTopLevelInstanceType());
		}

		private FlexoConceptInstanceType getTopLevelInstanceType() {
			VirtualModel rootVM = getVirtualModelWithDiagramNature();
			if (rootVM != null) {
				return rootVM.getInstanceType();
			}
			return null;
		}

		private VirtualModel getVirtualModelWithDiagramNature() {
			if (getFlexoConcept() != null) {
				return getVirtualModelWithDiagramNature(getFlexoConcept().getOwningVirtualModel());
			}
			return null;
		}

		private VirtualModel getVirtualModelWithDiagramNature(VirtualModel vm) {
			if (vm == null) {
				return null;
			}
			if (vm.hasNature(FMLControlledDiagramVirtualModelNature.INSTANCE)) {
				return vm;
			}
			return getVirtualModelWithDiagramNature(vm.getContainerVirtualModel());
		}

		@Override
		public boolean targetHasMultipleRoles() {
			return getTargetFlexoConcept() != null && getAvailableTargetShapeRoles().size() > 1;
		}

		@Override
		public List<ShapeRole> getAvailableTargetShapeRoles() {
			if (getTargetFlexoConcept() != null) {
				return getTargetFlexoConcept().getDeclaredProperties(ShapeRole.class);
			}
			return null;
		}

		@Override
		public ShapeRole getTargetShapeRole() {
			return targetShapeRole;
		}

		@Override
		public void setTargetShapeRole(ShapeRole targetPatternRole) {
			this.targetShapeRole = targetPatternRole;
		}

		@Override
		public boolean isValidTarget(FlexoConcept aTarget, FlexoRole<?> contextRole) {
			if (getTargetFlexoConcept() != null && getTargetFlexoConcept().isAssignableFrom(aTarget)) {
				if (targetHasMultipleRoles()) {
					// TODO make proper implementation when property inheritance will be in use !!!
					return getTargetShapeRole() == null || getTargetShapeRole().getRoleName().equals(contextRole.getRoleName());
				}
				else {
					return true;
				}
			}
			return false;
		}

		@Override
		protected DropSchemeBindingModel makeBindingModel() {
			return new DropSchemeBindingModel(this);
		}

	}
}
