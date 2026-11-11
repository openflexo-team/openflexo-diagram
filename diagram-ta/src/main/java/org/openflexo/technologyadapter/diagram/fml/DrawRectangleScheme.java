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
import org.openflexo.foundation.fml.FlexoConceptInstanceType;
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
import org.openflexo.technologyadapter.diagram.fml.binding.DrawRectangleBindingModel;

/**
 * A creation behaviour triggered while drawing a rectangle
 * 
 * @author sylvain
 *
 */
@FIBPanel("Fib/DrawRectangleSchemePanel.fib")
@ModelEntity
@ImplementationClass(DrawRectangleScheme.DrawRectangleSchemeImpl.class)
@FML("DrawRectangleScheme")
public interface DrawRectangleScheme extends AbstractCreationScheme, DiagramFlexoBehaviour {

	@PropertyIdentifier(type = FlexoConceptInstanceType.class)
	public static final String TARGET_TYPE_KEY = "targetType";
	@PropertyIdentifier(type = FlexoConceptInstanceType.class)
	public static final String CHILDREN_TYPE_KEY = "childrenType";
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

	/**
	 * Whether the objects enclosed in the drawn rectangle are offered to the behaviour as the "selection" variable
	 */
	@Getter(value = SELECT_OBJECTS_KEY, defaultValue = "false")
	@FMLAttribute(value = SELECT_OBJECTS_KEY, required = false)
	public boolean getSelectObjects();

	@Setter(SELECT_OBJECTS_KEY)
	public void setSelectObjects(boolean selectObjects);

	/**
	 * Whether the rectangle is drawn at top level: no target type is declared
	 */
	public boolean isTopTarget();

	public boolean getTopTarget();

	/**
	 * Setting the top target removes the target type; unsetting it, when no target type is set, starts from the concept declaring this
	 * scheme (the inspector then shows the concept selector)
	 */
	public void setTopTarget(boolean flag);

	/**
	 * The concept of {@link #getTargetType()}: null at top level
	 */
	public FlexoConcept getTargetFlexoConcept();

	/**
	 * Set the target type to the instance type of the supplied concept (null for top level)
	 */
	public void setTargetFlexoConcept(FlexoConcept targetFlexoConcept);

	/**
	 * The concept of {@link #getChildrenType()}: null when not set
	 */
	public FlexoConcept getChildrenFlexoConcept();

	/**
	 * Set the children type to the instance type of the supplied concept (null to unset it)
	 */
	public void setChildrenFlexoConcept(FlexoConcept childrenFlexoConcept);

	public boolean isValidTarget(FlexoConcept aTarget);

	public static abstract class DrawRectangleSchemeImpl extends AbstractCreationSchemeImpl implements DrawRectangleScheme {

		private FlexoConceptInstanceType targetType;
		private FlexoConceptInstanceType childrenType;

		/**
		 * Return the type declared for the target, or null for a scheme drawing at top level.<br>
		 * That value is the one the FML attribute shows: computing the top level type here made a scheme written without
		 * <code>targetType</code> look as if it had declared one, which the pretty-print had no node for (and wrote on save).
		 */
		@Override
		public FlexoConceptInstanceType getTargetType() {
			return targetType;
		}

		@Override
		public void setTargetType(FlexoConceptInstanceType targetType) {
			if ((targetType == null && this.targetType != null) || (targetType != null && !targetType.equals(this.targetType))) {
				FlexoConceptInstanceType oldValue = this.targetType;
				this.targetType = targetType;
				getPropertyChangeSupport().firePropertyChange(TARGET_TYPE_KEY, oldValue, targetType);
				getPropertyChangeSupport().firePropertyChange(TARGET_FLEXO_CONCEPT_KEY, oldValue != null ? oldValue.getFlexoConcept() : null,
						getTargetFlexoConcept());
				getPropertyChangeSupport().firePropertyChange(TOP_TARGET_KEY, oldValue == null, isTopTarget());
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
			return childrenType;
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

		@Override
		public FlexoConcept getTargetFlexoConcept() {
			return targetType != null ? targetType.getFlexoConcept() : null;
		}

		@Override
		public void setTargetFlexoConcept(FlexoConcept aTargetFlexoConcept) {
			setTargetType(aTargetFlexoConcept != null ? aTargetFlexoConcept.getInstanceType() : null);
		}

		@Override
		public boolean isTopTarget() {
			return getTopTarget();
		}

		@Override
		public boolean getTopTarget() {
			return targetType == null;
		}

		@Override
		public void setTopTarget(boolean flag) {
			if (flag) {
				setTargetType(null);
			}
			else if (targetType == null && getFlexoConcept() != null) {
				setTargetType(getFlexoConcept().getInstanceType());
			}
		}

		@Override
		public FlexoConcept getChildrenFlexoConcept() {
			return childrenType != null ? childrenType.getFlexoConcept() : null;
		}

		@Override
		public void setChildrenFlexoConcept(FlexoConcept aChildrenFlexoConcept) {
			setChildrenType(aChildrenFlexoConcept != null ? aChildrenFlexoConcept.getInstanceType() : null);
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
