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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.pamela.annotations.DefineValidationRule;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.diagram.model.Diagram;
import org.openflexo.technologyadapter.diagram.model.DiagramShape;

@ModelEntity
@ImplementationClass(ShapeRole.ShapeRoleImpl.class)
@XMLElement
@FML("ShapeRole")
public interface ShapeRole extends GraphicalElementRole<DiagramShape, ShapeGraphicalRepresentation> {

	@PropertyIdentifier(type = Double.class)
	public static final String PREVIEW_X_KEY = "previewX";
	@PropertyIdentifier(type = Double.class)
	public static final String PREVIEW_Y_KEY = "previewY";
	@PropertyIdentifier(type = Double.class)
	public static final String PREVIEW_WIDTH_KEY = "previewWidth";
	@PropertyIdentifier(type = Double.class)
	public static final String PREVIEW_HEIGHT_KEY = "previewHeight";

	// @PropertyIdentifier(type = GraphicalRepresentation.class)
	// public static final String GRAPHICAL_REPRESENTATION_KEY = "graphicalRepresentation";
	@PropertyIdentifier(type = ShapeRole.class)
	public static final String PARENT_SHAPE_ROLE_KEY = "parentShapeRole";

	public static GraphicalFeature<Double, ShapeGraphicalRepresentation> POS_X_FEATURE = new GraphicalFeature<Double, ShapeGraphicalRepresentation>(
			"x", ShapeGraphicalRepresentation.X) {
		@Override
		public Double retrieveFromGraphicalRepresentation(ShapeGraphicalRepresentation gr) {
			return gr.getX();
		}

		@Override
		public void applyToGraphicalRepresentation(ShapeGraphicalRepresentation gr, Double value) {
			gr.setX(value.doubleValue());
		}
	};

	public static GraphicalFeature<Double, ShapeGraphicalRepresentation> POS_Y_FEATURE = new GraphicalFeature<Double, ShapeGraphicalRepresentation>(
			"y", ShapeGraphicalRepresentation.Y) {
		@Override
		public Double retrieveFromGraphicalRepresentation(ShapeGraphicalRepresentation gr) {
			return gr.getY();
		}

		@Override
		public void applyToGraphicalRepresentation(ShapeGraphicalRepresentation gr, Double value) {
			gr.setY(value.doubleValue());
		}
	};

	public static GraphicalFeature<Double, ShapeGraphicalRepresentation> WIDTH_FEATURE = new GraphicalFeature<Double, ShapeGraphicalRepresentation>(
			"width", ShapeGraphicalRepresentation.WIDTH) {
		@Override
		public Double retrieveFromGraphicalRepresentation(ShapeGraphicalRepresentation gr) {
			return gr.getWidth();
		}

		@Override
		public void applyToGraphicalRepresentation(ShapeGraphicalRepresentation gr, Double value) {
			gr.setWidth(value.doubleValue());
		}
	};

	public static GraphicalFeature<Double, ShapeGraphicalRepresentation> HEIGHT_FEATURE = new GraphicalFeature<Double, ShapeGraphicalRepresentation>(
			"height", ShapeGraphicalRepresentation.HEIGHT) {
		@Override
		public Double retrieveFromGraphicalRepresentation(ShapeGraphicalRepresentation gr) {
			return gr.getHeight();
		}

		@Override
		public void applyToGraphicalRepresentation(ShapeGraphicalRepresentation gr, Double value) {
			gr.setHeight(value.doubleValue());
		}
	};

	public static GraphicalFeature<Double, ShapeGraphicalRepresentation> RELATIVE_TEXT_X_FEATURE = new GraphicalFeature<Double, ShapeGraphicalRepresentation>(
			"relativeTextX", ShapeGraphicalRepresentation.RELATIVE_TEXT_X) {
		@Override
		public Double retrieveFromGraphicalRepresentation(ShapeGraphicalRepresentation gr) {
			return gr.getRelativeTextX();
		}

		@Override
		public void applyToGraphicalRepresentation(ShapeGraphicalRepresentation gr, Double value) {
			gr.setRelativeTextX(value.doubleValue());
		}
	};

	public static GraphicalFeature<Double, ShapeGraphicalRepresentation> RELATIVE_TEXT_Y_FEATURE = new GraphicalFeature<Double, ShapeGraphicalRepresentation>(
			"relativeTextY", ShapeGraphicalRepresentation.RELATIVE_TEXT_Y) {
		@Override
		public Double retrieveFromGraphicalRepresentation(ShapeGraphicalRepresentation gr) {
			return gr.getRelativeTextY();
		}

		@Override
		public void applyToGraphicalRepresentation(ShapeGraphicalRepresentation gr, Double value) {
			gr.setRelativeTextY(value.doubleValue());
		}
	};

	public static GraphicalFeature<Double, ShapeGraphicalRepresentation> ABSOLUTE_TEXT_X_FEATURE = new GraphicalFeature<Double, ShapeGraphicalRepresentation>(
			"absoluteTextX", ShapeGraphicalRepresentation.ABSOLUTE_TEXT_X) {
		@Override
		public Double retrieveFromGraphicalRepresentation(ShapeGraphicalRepresentation gr) {
			return gr.getAbsoluteTextX();
		}

		@Override
		public void applyToGraphicalRepresentation(ShapeGraphicalRepresentation gr, Double value) {
			gr.setAbsoluteTextX(value.doubleValue());
		}
	};

	public static GraphicalFeature<Double, ShapeGraphicalRepresentation> ABSOLUTE_TEXT_Y_FEATURE = new GraphicalFeature<Double, ShapeGraphicalRepresentation>(
			"absoluteTextY", ShapeGraphicalRepresentation.ABSOLUTE_TEXT_Y) {
		@Override
		public Double retrieveFromGraphicalRepresentation(ShapeGraphicalRepresentation gr) {
			return gr.getAbsoluteTextY();
		}

		@Override
		public void applyToGraphicalRepresentation(ShapeGraphicalRepresentation gr, Double value) {
			gr.setAbsoluteTextY(value.doubleValue());
		}
	};

	public static GraphicalFeature<?, ?>[] AVAILABLE_SHAPE_FEATURES = { POS_X_FEATURE, POS_Y_FEATURE, WIDTH_FEATURE, HEIGHT_FEATURE,
			RELATIVE_TEXT_X_FEATURE, RELATIVE_TEXT_Y_FEATURE, ABSOLUTE_TEXT_X_FEATURE, ABSOLUTE_TEXT_Y_FEATURE };

	@Getter(value = PARENT_SHAPE_ROLE_KEY)
	@XMLElement(context = "Parent")
	public ShapeRole getParentShapeRole();

	@Setter(PARENT_SHAPE_ROLE_KEY)
	public void setParentShapeRole(ShapeRole parentShapeRole);

	public boolean isContainedIn(ShapeRole container);

	public boolean getParentShapeAsDefinedInAction();

	public void setParentShapeAsDefinedInAction(boolean flag);

	/**
	 * Get the list of shape roles that can be set as parent shape pattern property. This list contains all other shape pattern roles of
	 * current flexo concept which are not already in the containment subtree
	 * 
	 * @return
	 */
	public List<ShapeRole> getPossibleParentShapeRoles();

	@Getter(value = PREVIEW_X_KEY, defaultValue = "3.14")
	@XMLAttribute
	public double getPreviewX();

	@Setter(value = PREVIEW_X_KEY)
	public void setPreviewX(double aValue);

	@Getter(value = PREVIEW_Y_KEY, defaultValue = "3.14")
	@XMLAttribute
	public double getPreviewY();

	@Setter(value = PREVIEW_Y_KEY)
	public void setPreviewY(double aValue);

	@Getter(value = PREVIEW_WIDTH_KEY, defaultValue = "50.0")
	@XMLAttribute
	public double getPreviewWidth();

	@Setter(value = PREVIEW_WIDTH_KEY)
	public void setPreviewWidth(double aValue);

	@Getter(value = PREVIEW_HEIGHT_KEY, defaultValue = "40.0")
	@XMLAttribute
	public double getPreviewHeight();

	@Setter(value = PREVIEW_HEIGHT_KEY)
	public void setPreviewHeight(double aValue);

	/**
	 * Called to configure a {@link ShapeRole} using prototyping {@link DiagramShape} from metamodel
	 * 
	 * Example label is retrieved from name of shape<br>
	 * Preview size is normalized on a 250:200 rectangle when bigger<br>
	 * Preview location is centered on a 250:200 rectangle
	 * 
	 * @param metaModelShape
	 */
	@Override
	public void bindTo(DiagramShape metaModelShape);

	public static abstract class ShapeRoleImpl extends GraphicalElementRoleImpl<DiagramShape, ShapeGraphicalRepresentation>
			implements ShapeRole {

		private static final Logger logger = Logger.getLogger(ShapeRole.class.getPackage().getName());

		private ShapeRole parentShapeRole;

		// private List<ShapeRole> _possibleParentPatternRole;

		public ShapeRoleImpl() {
			super();
		}

		@Override
		protected void initDefaultSpecifications() {
			super.initDefaultSpecifications();
			if (getFMLModelFactory() != null) {
				for (GraphicalFeature<?, ?> GF : AVAILABLE_SHAPE_FEATURES) {
					// logger.info("[SHAPE:" + getRoleName() + "] Nouvelle GraphicalElementSpecification for " + GF);
					GraphicalElementSpecification newGraphicalElementSpecification = getFMLModelFactory()
							.newInstance(GraphicalElementSpecification.class);
					newGraphicalElementSpecification.setFlexoRole(this);
					newGraphicalElementSpecification.setFeature(GF);
					newGraphicalElementSpecification.setReadOnly(false);
					newGraphicalElementSpecification.setMandatory(true);
					grSpecifications.add(newGraphicalElementSpecification);
				}
			}
			handlePendingGRSpecs();

		}

		@Override
		public DiagramShape makeDiagramElementInMetaModel(Diagram exampleDiagram) {
			DiagramShape returned = exampleDiagram.getDiagramFactory().makeNewShape("Shape", exampleDiagram);
			return returned;
		}

		/*@Override
		public String getFMLRepresentation(FMLRepresentationContext context) {
			FMLRepresentationOutput out = new FMLRepresentationOutput(context);
			VirtualModel vm = getOwningVirtualModel();
			if (vm != null) {
				out.append("FlexoRole " + getName() + " as ShapeSpecification from " + vm.getName() + ";", context);
			}
			else {
				out.append("FlexoRole " + getName() + " -- NO OWNING MODEL;", context);
			}
			return out.toString();
		}*/

		/*@Override
		public String getFMLRepresentation(FMLRepresentationContext context) {
			FMLRepresentationOutput out = new FMLRepresentationOutput(context);
			out.append((getReceiver().isValid() ? getReceiver().toString() + "." : "") + getTechnologyAdapterIdentifier() + "::"
					+ getImplementedInterface().getSimpleName() + " {" + StringUtils.LINE_SEPARATOR, context);
			out.append(getGraphicalElementSpecificationFMLRepresentation(context), context);
			out.append("}", context);
			return out.toString();
		}*/

		@Override
		public String getTypeDescription() {
			return getModelSlot().getModelSlotTechnologyAdapter().getLocales().localizedForKey("shape");
		}

		/*public void tryToFindAGR() {
			if (getGraphicalRepresentation() == null && getModelSlot() instanceof TypedDiagramModelSlot) {
				// Try to find one somewhere
				TypedDiagramModelSlot ms = (TypedDiagramModelSlot) getModelSlot();
				for (FMLDiagramPaletteElementBinding binding : ms.getPaletteElementBindings()) {
					if (binding.getBoundFlexoConcept() == getFlexoConcept()) {
						setGraphicalRepresentation(binding.getPaletteElement().getGraphicalRepresentation());
					}
				}
			}
		}*/

		@Override
		public Type getType() {
			return DiagramShape.class;
		}

		private boolean detectLoopInParentShapePatternRoleDefinition() {
			List<ShapeRole> list = new ArrayList<>();
			ShapeRole current = this;
			while (!list.contains(current) && current != null) {
				list.add(current);
				current = current.getParentShapeRole();
			}
			if (current != null) {
				return true;
			}
			return false;
		}

		@Override
		public ShapeRole getParentShapeRole() {
			return parentShapeRole;
		}

		@Override
		public void setParentShapeRole(ShapeRole parentShapeRole) {
			if (parentShapeRole != this.parentShapeRole) {
				ShapeRole oldParentShapeRole = this.parentShapeRole;
				// logger.info(">>>> setParentShapePatternRole() with " + parentShapeRole);
				this.parentShapeRole = parentShapeRole;
				if (detectLoopInParentShapePatternRoleDefinition()) {
					logger.warning("Detecting a loop in parent shape pattern property definition. Resetting parent shape pattern property");
					this.parentShapeRole = null;
				}

				// center the shape in the parent
				if (parentShapeRole != null && getGraphicalRepresentation() != null) {
					getGraphicalRepresentation()
							.setX((parentShapeRole.getGraphicalRepresentation().getWidth() - getGraphicalRepresentation().getWidth()) / 2);
					getGraphicalRepresentation().setY(
							(parentShapeRole.getGraphicalRepresentation().getHeight() - getGraphicalRepresentation().getHeight()) / 2);
				}
				else {
					// Center shape in preview
					if (getGraphicalRepresentation() != null) {
						getGraphicalRepresentation().setX((250 - getGraphicalRepresentation().getWidth()) / 2);
						getGraphicalRepresentation().setY((200 - getGraphicalRepresentation().getHeight()) / 2);
					}
				}
				// setChanged();
				// notifyObservers();
				getPropertyChangeSupport().firePropertyChange(PARENT_SHAPE_ROLE_KEY, oldParentShapeRole, parentShapeRole);
				getPropertyChangeSupport().firePropertyChange("parentShapeAsDefinedInAction", oldParentShapeRole, parentShapeRole);
				if (getFlexoConcept() != null) {
					getFlexoConcept().getPropertyChangeSupport().firePropertyChange(PARENT_SHAPE_ROLE_KEY, oldParentShapeRole,
							parentShapeRole);
				}
			}
		}

		@Override
		public boolean getParentShapeAsDefinedInAction() {
			return getParentShapeRole() == null;
		}

		@Override
		public void setParentShapeAsDefinedInAction(boolean flag) {
			// System.out.println(">>>> setParentShapeAsDefinedInAction() with " + flag);
			List<ShapeRole> possibleParentPatternRole = getPossibleParentShapeRoles();
			if (!flag) {
				if (possibleParentPatternRole.size() > 0) {
					setParentShapeRole(possibleParentPatternRole.get(0));
				}
			}
			else {
				// System.out.println("setParentShapePatternRole with null");
				setParentShapeRole(null);
				// flag = true;
			}
		}

		@Override
		public boolean isContainedIn(ShapeRole container) {
			if (container == this) {
				return true;
			}
			if (getParentShapeRole() != null) {
				return getParentShapeRole().isContainedIn(container);
			}
			return false;
		}

		/**
		 * Get the list of shape pattern roles that can be set as parent shape pattern property. This list contains all other shape pattern
		 * roles of current flexo concept which are not already in the containment subtree
		 * 
		 * @return
		 */
		@Override
		public List<ShapeRole> getPossibleParentShapeRoles() {
			List<ShapeRole> returned = new ArrayList<>();
			if (getFlexoConcept() != null) {
				List<ShapeRole> shapesPatternRoles = getFlexoConcept().getDeclaredProperties(ShapeRole.class);
				for (ShapeRole shapeRole : shapesPatternRoles) {
					if (!shapeRole.isContainedIn(this)) {
						returned.add(shapeRole);
					}
				}
			}
			return returned;
		}

		/**
		 * Called to configure a {@link ShapeRole} using prototyping {@link DiagramShape} from metamodel
		 * 
		 * Example label is retrieved from name of shape<br>
		 * Preview size is normalized on a 250:200 rectangle when bigger<br>
		 * Preview location is centered on a 250:200 rectangle
		 * 
		 * @param metaModelShape
		 */
		@Override
		public void bindTo(DiagramShape metaModelShape) {

			if (metaModelShape == null) {
				logger.warning("Could not bind to a null " + metaModelShape);
				return;
			}

			setMetamodelElement(metaModelShape);

			setExampleLabel(metaModelShape.getName());

			if (metaModelShape.getGraphicalRepresentation().getWidth() > 230) {
				setPreviewWidth(230);
			}
			else {
				setPreviewWidth(metaModelShape.getGraphicalRepresentation().getWidth());
			}

			if (metaModelShape.getGraphicalRepresentation().getHeight() > 230) {
				setPreviewHeight(230);
			}
			else {
				setPreviewHeight(metaModelShape.getGraphicalRepresentation().getHeight());
			}

			setPreviewX((250 - getPreviewWidth()) / 2);
			setPreviewY((200 - getPreviewHeight()) / 2);

		}

	}

	@DefineValidationRule
	public static class LabelBindingdMustBeValid extends BindingMustBeValid<ShapeRole> {
		public LabelBindingdMustBeValid() {
			super("'label'_binding_must_be_valid", ShapeRole.class);
		}

		@Override
		public DataBinding<String> getBinding(ShapeRole object) {
			return object.getLabel();
		}

	}

}
