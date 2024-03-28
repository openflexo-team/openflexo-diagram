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
import org.openflexo.foundation.fml.FMLMigration;
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
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.diagram.fml.binding.LinkSchemeBindingModel;
import org.openflexo.toolbox.StringUtils;

/**
 * A creation behaviour triggered while connecting two shapes
 * 
 * @author sylvain
 *
 */
@FIBPanel("Fib/LinkSchemePanel.fib")
@ModelEntity
@ImplementationClass(LinkScheme.LinkSchemeImpl.class)
@XMLElement
@FML("LinkScheme")
public interface LinkScheme extends AbstractCreationScheme, DiagramFlexoBehaviour {

	@PropertyIdentifier(type = FlexoConceptInstanceType.class)
	public static final String FROM_TYPE_KEY = "fromType";
	@PropertyIdentifier(type = FlexoConceptInstanceType.class)
	public static final String TO_TYPE_KEY = "toType";
	@PropertyIdentifier(type = String.class)
	public static final String FROM_TARGET_KEY = "fromTarget";
	@PropertyIdentifier(type = String.class)
	public static final String TO_TARGET_KEY = "toTarget";
	@PropertyIdentifier(type = boolean.class)
	public static final String IS_AVAILABLE_WITH_FLOATING_PALETTE_KEY = "isAvailableWithFloatingPalette";
	@PropertyIdentifier(type = boolean.class)
	public static final String INVERSE_DIRECTION_WHEN_COMPOSED_KEY = "inverseDirectionWhenComposed";
	@PropertyIdentifier(type = boolean.class)
	public static final String NORTH_DIRECTION_SUPPORTED_KEY = "northDirectionSupported";
	@PropertyIdentifier(type = boolean.class)
	public static final String EAST_DIRECTION_SUPPORTED_KEY = "eastDirectionSupported";
	@PropertyIdentifier(type = boolean.class)
	public static final String SOUTH_DIRECTION_SUPPORTED_KEY = "southDirectionSupported";
	@PropertyIdentifier(type = boolean.class)
	public static final String WEST_DIRECTION_SUPPORTED_KEY = "westDirectionSupported";

	@PropertyIdentifier(type = FlexoConcept.class)
	public static final String FROM_TARGET_FLEXO_CONCEPT_KEY = "fromTargetFlexoConcept";
	@PropertyIdentifier(type = FlexoConcept.class)
	public static final String TO_TARGET_FLEXO_CONCEPT_KEY = "toTargetFlexoConcept";

	@Getter(value = FROM_TYPE_KEY, ignoreType = true)
	@FMLAttribute(value = FROM_TYPE_KEY, kind = AttributeKind.Type, required = true)
	public FlexoConceptInstanceType getFromType();

	@Setter(FROM_TYPE_KEY)
	public void setFromType(FlexoConceptInstanceType from);

	/**
	 * We define an updater for FROM_TYPE_KEY property because we need to translate supplied Type to valid TypingSpace
	 * 
	 * @param type
	 */
	@Updater(FROM_TYPE_KEY)
	public void updateFromType(FlexoConceptInstanceType from);

	@Getter(value = TO_TYPE_KEY, ignoreType = true)
	@FMLAttribute(value = TO_TYPE_KEY, kind = AttributeKind.Type, required = true)
	public FlexoConceptInstanceType getToType();

	@Setter(TO_TYPE_KEY)
	public void setToType(FlexoConceptInstanceType to);

	/**
	 * We define an updater for TO_TYPE_KEY property because we need to translate supplied Type to valid TypingSpace
	 * 
	 * @param type
	 */
	@Updater(TO_TYPE_KEY)
	public void updateToType(FlexoConceptInstanceType to);

	@FMLMigration
	@Deprecated
	@Getter(value = FROM_TARGET_KEY)
	@XMLAttribute
	public String _getFromTarget();

	@FMLMigration
	@Deprecated
	@Setter(FROM_TARGET_KEY)
	public void _setFromTarget(String fromTarget);

	@FMLMigration
	@Deprecated
	@Getter(value = TO_TARGET_KEY)
	@XMLAttribute
	public String _getToTarget();

	@FMLMigration
	@Deprecated
	@Setter(TO_TARGET_KEY)
	public void _setToTarget(String toTarget);

	@Getter(value = IS_AVAILABLE_WITH_FLOATING_PALETTE_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getIsAvailableWithFloatingPalette();

	@Setter(IS_AVAILABLE_WITH_FLOATING_PALETTE_KEY)
	public void setIsAvailableWithFloatingPalette(boolean isAvailableWithFloatingPalette);

	@Getter(value = INVERSE_DIRECTION_WHEN_COMPOSED_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getInverseDirectionWhenComposed();

	@Setter(INVERSE_DIRECTION_WHEN_COMPOSED_KEY)
	public void setInverseDirectionWhenComposed(boolean inverseDirectionWhenComposed);

	@Getter(value = NORTH_DIRECTION_SUPPORTED_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getNorthDirectionSupported();

	@Setter(NORTH_DIRECTION_SUPPORTED_KEY)
	public void setNorthDirectionSupported(boolean northDirectionSupported);

	@Getter(value = EAST_DIRECTION_SUPPORTED_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getEastDirectionSupported();

	@Setter(EAST_DIRECTION_SUPPORTED_KEY)
	public void setEastDirectionSupported(boolean eastDirectionSupported);

	@Getter(value = SOUTH_DIRECTION_SUPPORTED_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getSouthDirectionSupported();

	@Setter(SOUTH_DIRECTION_SUPPORTED_KEY)
	public void setSouthDirectionSupported(boolean southDirectionSupported);

	@Getter(value = WEST_DIRECTION_SUPPORTED_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getWestDirectionSupported();

	@Setter(WEST_DIRECTION_SUPPORTED_KEY)
	public void setWestDirectionSupported(boolean westDirectionSupported);

	@FMLMigration
	@Deprecated
	public FlexoConcept getFromTargetFlexoConcept();

	@FMLMigration
	@Deprecated
	public void setFromTargetFlexoConcept(FlexoConcept targetFlexoConcept);

	@FMLMigration
	@Deprecated
	public FlexoConcept getToTargetFlexoConcept();

	@FMLMigration
	@Deprecated
	public void setToTargetFlexoConcept(FlexoConcept targetFlexoConcept);

	public boolean isValidTarget(FlexoConcept actualFromTarget, FlexoConcept actualToTarget);

	public static abstract class LinkSchemeImpl extends AbstractCreationSchemeImpl implements LinkScheme {

		private String fromTarget;
		private String toTarget;

		private boolean northDirectionSupported = true;
		private boolean eastDirectionSupported = true;
		private boolean southDirectionSupported = true;
		private boolean westDirectionSupported = true;

		private boolean isAvailableWithFloatingPalette = true;

		private FlexoConcept fromTargetFlexoConcept = null;
		private FlexoConcept toTargetFlexoConcept = null;

		private FlexoConcept lastKnownFromTargetFlexoConcept = null;
		private FlexoConcept lastKnownToTargetFlexoConcept = null;

		private FlexoConceptInstanceType fromType;
		private FlexoConceptInstanceType toType;

		@Override
		@Deprecated
		public String _getFromTarget() {
			return fromTarget;
		}

		@Override
		@Deprecated
		public void _setFromTarget(String fromTarget) {
			if (requireChange(this.fromTarget, fromTarget)) {
				FlexoConcept oldValue = lastKnownFromTargetFlexoConcept;
				this.fromTarget = fromTarget;
				getPropertyChangeSupport().firePropertyChange(FROM_TARGET_FLEXO_CONCEPT_KEY, oldValue, getFromTargetFlexoConcept());
			}
		}

		@Override
		@Deprecated
		public String _getToTarget() {
			return toTarget;
		}

		@Override
		@Deprecated
		public void _setToTarget(String toTarget) {
			if (requireChange(this.toTarget, toTarget)) {
				FlexoConcept oldValue = lastKnownToTargetFlexoConcept;
				this.toTarget = toTarget;
				getPropertyChangeSupport().firePropertyChange(TO_TARGET_FLEXO_CONCEPT_KEY, oldValue, getToTargetFlexoConcept());
			}
		}

		@Override
		public void finalizeDeserialization() {
			super.finalizeDeserialization();
			getFromTargetFlexoConcept();
			getToTargetFlexoConcept();
		}

		@Override
		public FlexoConceptInstanceType getFromType() {
			if (fromType != null) {
				return fromType;
			}
			if (getFromTargetFlexoConcept() != null) {
				return getFromTargetFlexoConcept().getInstanceType();
			}
			return FlexoConceptInstanceType.UNDEFINED_FLEXO_CONCEPT_INSTANCE_TYPE;
		}

		@Override
		public void setFromType(FlexoConceptInstanceType fromType) {
			if ((fromType == null && this.fromType != null) || (fromType != null && !fromType.equals(this.fromType))) {
				FlexoConceptInstanceType oldValue = this.fromType;
				this.fromType = fromType;
				getPropertyChangeSupport().firePropertyChange(FROM_TYPE_KEY, oldValue, fromType);
			}
		}

		/**
		 * We define an updater for FROM_TYPE_KEY property because we need to translate supplied Type to valid TypingSpace
		 * 
		 * This updater is called during updateWith() processing (generally applied during the FML parsing phases)
		 * 
		 * @param type
		 */
		@Override
		public void updateFromType(FlexoConceptInstanceType type) {

			if (getDeclaringCompilationUnit() != null && type != null) {
				setFromType(type.translateTo(getDeclaringCompilationUnit().getTypingSpace()));
			}
			else {
				setFromType(type);
			}
		}

		@Override
		public FlexoConceptInstanceType getToType() {
			if (toType != null) {
				return toType;
			}
			if (getToTargetFlexoConcept() != null) {
				return getToTargetFlexoConcept().getInstanceType();
			}
			return FlexoConceptInstanceType.UNDEFINED_FLEXO_CONCEPT_INSTANCE_TYPE;
		}

		@Override
		public void setToType(FlexoConceptInstanceType toType) {
			if ((toType == null && this.toType != null) || (toType != null && !toType.equals(this.toType))) {
				FlexoConceptInstanceType oldValue = this.toType;
				this.toType = toType;
				getPropertyChangeSupport().firePropertyChange(TO_TYPE_KEY, oldValue, toType);
			}
		}

		/**
		 * We define an updater for TO_TYPE_KEY property because we need to translate supplied Type to valid TypingSpace
		 * 
		 * This updater is called during updateWith() processing (generally applied during the FML parsing phases)
		 * 
		 * @param type
		 */
		@Override
		public void updateToType(FlexoConceptInstanceType type) {

			if (getDeclaringCompilationUnit() != null && type != null) {
				setToType(type.translateTo(getDeclaringCompilationUnit().getTypingSpace()));
			}
			else {
				setToType(type);
			}
		}

		@Override
		@Deprecated
		public FlexoConcept getFromTargetFlexoConcept() {

			if (fromType != null && fromType.getFlexoConcept() != null) {
				return fromType.getFlexoConcept();
			}

			if (fromTargetFlexoConcept != null) {
				return fromTargetFlexoConcept;
			}

			if (!StringUtils.isEmpty(_getFromTarget()) && getOwningVirtualModel() != null) {
				fromTargetFlexoConcept = getOwningVirtualModel().getFlexoConcept(_getFromTarget());
			}
			if (lastKnownFromTargetFlexoConcept != fromTargetFlexoConcept) {
				FlexoConcept oldValue = lastKnownFromTargetFlexoConcept;
				lastKnownFromTargetFlexoConcept = fromTargetFlexoConcept;
				getPropertyChangeSupport().firePropertyChange(FROM_TARGET_FLEXO_CONCEPT_KEY, oldValue, fromTargetFlexoConcept);
			}
			return fromTargetFlexoConcept;
		}

		@Override
		@Deprecated
		public void setFromTargetFlexoConcept(FlexoConcept targetFlexoConcept) {
			FlexoConcept oldFromTargetFlexoConcept = fromTargetFlexoConcept;
			fromTargetFlexoConcept = targetFlexoConcept;
			if (fromTargetFlexoConcept != null) {
				setFromType(fromTargetFlexoConcept.getInstanceType());
			}
			_setFromTarget(targetFlexoConcept != null ? targetFlexoConcept.getURI() : null);
			getPropertyChangeSupport().firePropertyChange(FROM_TARGET_FLEXO_CONCEPT_KEY, oldFromTargetFlexoConcept, fromTargetFlexoConcept);
		}

		@Override
		@Deprecated
		public FlexoConcept getToTargetFlexoConcept() {

			if (toType != null && toType.getFlexoConcept() != null) {
				return toType.getFlexoConcept();
			}

			if (toTargetFlexoConcept != null) {
				return toTargetFlexoConcept;
			}

			if (!StringUtils.isEmpty(_getToTarget()) && getOwningVirtualModel() != null) {
				toTargetFlexoConcept = getOwningVirtualModel().getFlexoConcept(_getToTarget());
			}
			if (lastKnownToTargetFlexoConcept != toTargetFlexoConcept) {
				FlexoConcept oldValue = lastKnownToTargetFlexoConcept;
				lastKnownToTargetFlexoConcept = toTargetFlexoConcept;
				getPropertyChangeSupport().firePropertyChange(TO_TARGET_FLEXO_CONCEPT_KEY, oldValue, toTargetFlexoConcept);
			}
			return toTargetFlexoConcept;
		}

		@Override
		@Deprecated
		public void setToTargetFlexoConcept(FlexoConcept targetFlexoConcept) {
			FlexoConcept oldToTargetFlexoConcept = toTargetFlexoConcept;
			toTargetFlexoConcept = targetFlexoConcept;
			if (toTargetFlexoConcept != null) {
				setToType(toTargetFlexoConcept.getInstanceType());
			}
			_setToTarget(targetFlexoConcept != null ? targetFlexoConcept.getURI() : null);
			getPropertyChangeSupport().firePropertyChange(TO_TARGET_FLEXO_CONCEPT_KEY, oldToTargetFlexoConcept, toTargetFlexoConcept);
		}

		@Override
		public boolean isValidTarget(FlexoConcept actualFromTarget, FlexoConcept actualToTarget) {
			// TODO: improved this so that we can take into account adressed models restrictions. See also
			// LinkScheme.isValidTarget on branch 1.5.1
			return getFromTargetFlexoConcept() != null && getFromTargetFlexoConcept().isAssignableFrom(actualFromTarget)
					&& getToTargetFlexoConcept() != null && getToTargetFlexoConcept().isAssignableFrom(actualToTarget);
		}

		@Override
		protected LinkSchemeBindingModel makeBindingModel() {
			return new LinkSchemeBindingModel(this);
		}

		@Override
		public boolean getIsAvailableWithFloatingPalette() {
			return isAvailableWithFloatingPalette;
		}

		@Override
		public void setIsAvailableWithFloatingPalette(boolean isAvailableWithFloatingPalette) {
			this.isAvailableWithFloatingPalette = isAvailableWithFloatingPalette;
		}

		@Override
		public boolean getNorthDirectionSupported() {
			return northDirectionSupported;
		}

		@Override
		public void setNorthDirectionSupported(boolean northDirectionSupported) {
			this.northDirectionSupported = northDirectionSupported;
		}

		@Override
		public boolean getEastDirectionSupported() {
			return eastDirectionSupported;
		}

		@Override
		public void setEastDirectionSupported(boolean eastDirectionSupported) {
			this.eastDirectionSupported = eastDirectionSupported;
		}

		@Override
		public boolean getSouthDirectionSupported() {
			return southDirectionSupported;
		}

		@Override
		public void setSouthDirectionSupported(boolean southDirectionSupported) {
			this.southDirectionSupported = southDirectionSupported;
		}

		@Override
		public boolean getWestDirectionSupported() {
			return westDirectionSupported;
		}

		@Override
		public void setWestDirectionSupported(boolean westDirectionSupported) {
			this.westDirectionSupported = westDirectionSupported;
		}

		/*@Override
		public DiagramTechnologyAdapter getTechnologyAdapter() {
			return getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(DiagramTechnologyAdapter.class);
		}*/

	}
}
