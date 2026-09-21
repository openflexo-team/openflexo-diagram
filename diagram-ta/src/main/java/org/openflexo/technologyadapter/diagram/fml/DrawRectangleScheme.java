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

import org.openflexo.foundation.fml.AbstractCreationScheme;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.FMLMigration;
import org.openflexo.foundation.fml.FlexoConceptInstanceType;
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
import org.openflexo.technologyadapter.diagram.fml.binding.DrawRectangleBindingModel;
import org.openflexo.toolbox.StringUtils;

/**
 * A creation behaviour triggered while drawing a rectangle
 * 
 * @author sylvain
 *
 */
@FIBPanel("Fib/DrawRectangleSchemePanel.fib")
@ModelEntity
@ImplementationClass(DrawRectangleScheme.DrawRectangleSchemeImpl.class)
@XMLElement
@FML("DrawRectangleScheme")
public interface DrawRectangleScheme extends AbstractCreationScheme, DiagramFlexoBehaviour {

	@PropertyIdentifier(type = FlexoConceptInstanceType.class)
	public static final String TARGET_TYPE_KEY = "targetType";
	@PropertyIdentifier(type = FlexoConceptInstanceType.class)
	public static final String CHILDREN_TYPE_KEY = "childrenType";
	@PropertyIdentifier(type = String.class)
	public static final String TARGET_KEY = "target";
	@PropertyIdentifier(type = String.class)
	public static final String CHILDREN_KEY = "children";
	@PropertyIdentifier(type = Boolean.class)
	public static final String SELECT_OBJECTS_KEY = "selectObjects";
	@PropertyIdentifier(type = FlexoConcept.class)
	public static final String TARGET_FLEXO_CONCEPT_KEY = "targetFlexoConcept";
	@PropertyIdentifier(type = FlexoConcept.class)
	public static final String CHILDREN_FLEXO_CONCEPT_KEY = "childrenFlexoConcept";

	public static final String TOP_TARGET_KEY = "topTarget";

	/**
	 * Type (FlexoConcept) of the instance receiving the drawn rectangle, null value means top level
	 */
	@Getter(value = TARGET_TYPE_KEY, ignoreType = true)
	@FMLAttribute(value = TARGET_TYPE_KEY, kind = AttributeKind.Type, required = false)
	public FlexoConceptInstanceType getTargetType();

	@Setter(TARGET_TYPE_KEY)
	public void setTargetType(FlexoConceptInstanceType targetType);

	/**
	 * We define an updater for TARGET_TYPE property because we need to translate supplied Type to valid TypingSpace
	 * 
	 * @param type
	 */
	@Updater(TARGET_TYPE_KEY)
	public void updateTargetType(FlexoConceptInstanceType type);

	/**
	 * Type (FlexoConcept) of the instances the drawn rectangle encloses, and which the selection is made of
	 */
	@Getter(value = CHILDREN_TYPE_KEY, ignoreType = true)
	@FMLAttribute(value = CHILDREN_TYPE_KEY, kind = AttributeKind.Type, required = false)
	public FlexoConceptInstanceType getChildrenType();

	@Setter(CHILDREN_TYPE_KEY)
	public void setChildrenType(FlexoConceptInstanceType childrenType);

	/**
	 * We define an updater for CHILDREN_TYPE property because we need to translate supplied Type to valid TypingSpace
	 * 
	 * @param type
	 */
	@Updater(CHILDREN_TYPE_KEY)
	public void updateChildrenType(FlexoConceptInstanceType type);

	@FMLMigration
	@Deprecated
	@Getter(value = TARGET_KEY)
	@XMLAttribute
	public String _getTarget();

	@FMLMigration
	@Deprecated
	@Setter(TARGET_KEY)
	public void _setTarget(String target);

	@FMLMigration
	@Deprecated
	@Getter(value = CHILDREN_KEY)
	@XMLAttribute
	public String _getChildren();

	@FMLMigration
	@Deprecated
	@Setter(CHILDREN_KEY)
	public void _setChildren(String children);

	/**
	 * Whether the objects enclosed in the drawn rectangle are offered to the behaviour as the "selection" variable
	 */
	@Getter(value = SELECT_OBJECTS_KEY, defaultValue = "false")
	@XMLAttribute
	@FMLAttribute(value = SELECT_OBJECTS_KEY, required = false)
	public boolean getSelectObjects();

	@Setter(SELECT_OBJECTS_KEY)
	public void setSelectObjects(boolean selectObjects);

	public boolean isTopTarget();

	public boolean getTopTarget();

	public void setTopTarget(boolean flag);

	public FlexoConcept getTargetFlexoConcept();

	public void setTargetFlexoConcept(FlexoConcept childrenFlexoConcept);

	public FlexoConcept getChildrenFlexoConcept();

	public void setChildrenFlexoConcept(FlexoConcept childrenFlexoConcept);

	public boolean isValidTarget(FlexoConcept aTarget);

	public static abstract class DrawRectangleSchemeImpl extends AbstractCreationSchemeImpl implements DrawRectangleScheme {

		private static final String TOP = "top";

		private String target = TOP;
		private FlexoConcept lastKnownTargetFlexoConcept;
		private FlexoConcept targetFlexoConcept;
		private FlexoConceptInstanceType targetType;

		private String children = null;
		private FlexoConcept lastKnownChildrenFlexoConcept;
		private FlexoConcept childrenFlexoConcept;
		private FlexoConceptInstanceType childrenType;

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
		public FlexoConceptInstanceType getChildrenType() {
			if (childrenType != null) {
				return childrenType;
			}
			if (getChildrenFlexoConcept() != null) {
				return getChildrenFlexoConcept().getInstanceType();
			}
			return null;
		}

		@Override
		public void setChildrenType(FlexoConceptInstanceType childrenType) {
			if ((childrenType == null && this.childrenType != null) || (childrenType != null && !childrenType.equals(this.childrenType))) {
				FlexoConceptInstanceType oldValue = this.childrenType;
				this.childrenType = childrenType;
				getPropertyChangeSupport().firePropertyChange(CHILDREN_TYPE_KEY, oldValue, childrenType);
				getPropertyChangeSupport().firePropertyChange(CHILDREN_FLEXO_CONCEPT_KEY, oldValue != null ? oldValue.getFlexoConcept() : null,
						getChildrenFlexoConcept());
			}
		}

		/**
		 * We define an updater for CHILDREN_TYPE property because we need to translate supplied Type to valid TypingSpace
		 * 
		 * This updater is called during updateWith() processing (generally applied during the FML parsing phases)
		 * 
		 * @param type
		 */
		@Override
		public void updateChildrenType(FlexoConceptInstanceType type) {
			if (getDeclaringCompilationUnit() != null && type != null) {
				setChildrenType(type.translateTo(getDeclaringCompilationUnit().getTypingSpace()));
			}
			else {
				setChildrenType(type);
			}
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
		public String _getTarget() {
			return target;
		}

		@Override
		public void _setTarget(String target) {
			if (requireChange(this.target, target)) {
				FlexoConcept oldValue = getTargetFlexoConcept();
				this.target = target;
				getPropertyChangeSupport().firePropertyChange(TARGET_FLEXO_CONCEPT_KEY, oldValue, getTargetFlexoConcept());
				getPropertyChangeSupport().firePropertyChange(TOP_TARGET_KEY, !isTopTarget(), isTopTarget());
			}
		}

		@Override
		public FlexoConcept getTargetFlexoConcept() {
			if (targetType != null) {
				return targetType.getFlexoConcept();
			}
			if (isTopTarget()) {
				return null;
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
			return getTopTarget();
		}

		@Override
		public boolean getTopTarget() {
			if (StringUtils.isEmpty(_getTarget())) {
				return false;
			}
			return _getTarget().equalsIgnoreCase(TOP);
		}

		@Override
		public void setTopTarget(boolean flag) {
			if (flag) {
				_setTarget(TOP);
			}
			else {
				_setTarget("");
			}
		}

		@Override
		public String _getChildren() {
			return children;
		}

		@Override
		public void _setChildren(String children) {
			if (requireChange(this.children, children)) {
				FlexoConcept oldValue = getChildrenFlexoConcept();
				this.children = children;
				getPropertyChangeSupport().firePropertyChange(CHILDREN_FLEXO_CONCEPT_KEY, oldValue, getChildrenFlexoConcept());
			}
		}

		@Override
		public void finalizeDeserialization() {
			super.finalizeDeserialization();
			getChildrenFlexoConcept();
		}

		@Override
		public FlexoConcept getChildrenFlexoConcept() {
			if (childrenType != null) {
				return childrenType.getFlexoConcept();
			}
			if (StringUtils.isEmpty(_getChildren())) {
				return null;
			}
			if (childrenFlexoConcept != null) {
				return childrenFlexoConcept;
			}

			if (StringUtils.isEmpty(_getChildren())) {
				return null;
			}
			if (getOwningVirtualModel() != null) {
				childrenFlexoConcept = getOwningVirtualModel().getFlexoConcept(_getChildren());
				if (lastKnownChildrenFlexoConcept != childrenFlexoConcept) {
					FlexoConcept oldValue = lastKnownChildrenFlexoConcept;
					lastKnownChildrenFlexoConcept = childrenFlexoConcept;
					getPropertyChangeSupport().firePropertyChange(CHILDREN_FLEXO_CONCEPT_KEY, oldValue, childrenFlexoConcept);
				}
				return childrenFlexoConcept;
			}
			return null;
		}

		@Override
		public void setChildrenFlexoConcept(FlexoConcept aChildrenFlexoConcept) {
			/*if (targetFlexoConcept != null) {
				setTopTarget(false);
			}*/
			FlexoConcept oldChildrenFlexoConcept = this.childrenFlexoConcept;
			this.childrenFlexoConcept = aChildrenFlexoConcept;
			if (aChildrenFlexoConcept != null) {
				setChildrenType(aChildrenFlexoConcept.getInstanceType());
			}
			_setChildren(aChildrenFlexoConcept != null ? aChildrenFlexoConcept.getURI() : null);
			getPropertyChangeSupport().firePropertyChange(CHILDREN_FLEXO_CONCEPT_KEY, oldChildrenFlexoConcept, aChildrenFlexoConcept);
			// updateBindingModels();
		}

		@Override
		public boolean isValidTarget(FlexoConcept aTarget) {
			if (getTargetFlexoConcept() != null && getTargetFlexoConcept().isAssignableFrom(aTarget)) {
				return true;
			}
			return false;
		}

		@Override
		protected DrawRectangleBindingModel makeBindingModel() {
			return new DrawRectangleBindingModel(this);
		}

	}
}
