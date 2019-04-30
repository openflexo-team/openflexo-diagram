/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Openflexo-technology-adapters-ui, a component of the software infrastructure 
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

package org.openflexo.technologyadapter.diagram.controller.diagrameditor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.openflexo.connie.binding.BindingValueChangeListener;
import org.openflexo.connie.exception.NotSettableContextException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.diana.GraphicalRepresentation;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.VirtualModelInstance.ObjectLookupResult;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.technologyadapter.diagram.fml.GraphicalElementRole;
import org.openflexo.technologyadapter.diagram.fml.GraphicalElementSpecification;
import org.openflexo.technologyadapter.diagram.model.DiagramContainerElement;
import org.openflexo.technologyadapter.diagram.model.DiagramElement;

/**
 * Represents a {@link DiagramElement} seen in federated context<br>
 * Instead of just referencing the {@link DiagramElement}, we address it from a {@link FlexoConceptInstance} and a
 * {@link GraphicalElementRole} (which references the {@link DiagramElement}).
 * 
 * 
 * @author sylvain
 * 
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(FMLControlledDiagramElement.FMLControlledDiagramElementImpl.class)
public interface FMLControlledDiagramElement<E extends DiagramElement<GR>, GR extends GraphicalRepresentation>
		extends FlexoObject, PropertyChangeListener {

	public static final String DRAWING_KEY = "drawing";
	public static final String FLEXO_CONCEPT_INSTANCE_KEY = "flexoConceptInstance";
	public static final String DIAGRAM_ELEMENT_KEY = "diagramElement";
	public static final String ROLE_KEY = "property";

	/**
	 * Return the {@link FMLControlledDiagramDrawing} where this {@link FMLControlledDiagramElement} is referenced
	 * 
	 * @return
	 */
	@Getter(value = DRAWING_KEY, ignoreType = true)
	public FMLControlledDiagramDrawing getDrawing();

	/**
	 * Sets the {@link FMLControlledDiagramDrawing} where this {@link FMLControlledDiagramElement} is referenced
	 * 
	 * @param aFlexoConceptInstance
	 */
	@Setter(DRAWING_KEY)
	public void setDrawing(FMLControlledDiagramDrawing aDrawing);

	/**
	 * Return the {@link FlexoConceptInstance} where {@link DiagramElement} is referenced
	 * 
	 * @return
	 */
	@Getter(FLEXO_CONCEPT_INSTANCE_KEY)
	public FlexoConceptInstance getFlexoConceptInstance();

	/**
	 * Sets the {@link FlexoConceptInstance} where {@link DiagramElement} is referenced
	 * 
	 * @param aFlexoConceptInstance
	 */
	@Setter(FLEXO_CONCEPT_INSTANCE_KEY)
	public void setFlexoConceptInstance(FlexoConceptInstance aFlexoConceptInstance);

	/**
	 * Return the addressed {@link DiagramElement}
	 * 
	 * @return
	 */
	@Getter(DIAGRAM_ELEMENT_KEY)
	public E getDiagramElement();

	/**
	 * 
	 * @param diagramElement
	 */
	@Setter(DIAGRAM_ELEMENT_KEY)
	public void setDiagramElement(E diagramElement);

	/**
	 * Return the property from which related {@link FlexoConceptInstance} addresses the {@link DiagramElement}
	 * 
	 * @return
	 */
	@Getter(ROLE_KEY)
	public GraphicalElementRole<E, GR> getRole();

	/**
	 * Sets the property from which related {@link FlexoConceptInstance} addresses the {@link DiagramElement}
	 * 
	 * @return
	 */
	@Setter(ROLE_KEY)
	public void setRole(GraphicalElementRole<E, GR> aRole);

	public String getLabel();

	public void setLabel(String aName);

	public static abstract class FMLControlledDiagramElementImpl<E extends DiagramElement<GR>, GR extends GraphicalRepresentation>
			implements FMLControlledDiagramElement<E, GR> {

		private final Map<GraphicalElementSpecification<?, GR>, BindingValueChangeListener<?>> listeners = new HashMap<>();

		@Override
		public void setDiagramElement(E diagramElement) {
			if (getDiagramElement() != diagramElement) {
				E oldValue = getDiagramElement();
				if (getDiagramElement() != null) {
					getDiagramElement().getPropertyChangeSupport().removePropertyChangeListener(this);
				}
				performSuperSetter(DIAGRAM_ELEMENT_KEY, diagramElement);
				if (diagramElement != null) {
					diagramElement.getPropertyChangeSupport().addPropertyChangeListener(this);
				}

				if (oldValue == null && diagramElement != null) {
					// We handle here the fact that a FMLControlledDiagramElement was identified and managed
					// But a its creation, GR is null because diagramElement was null
					// Now that we have access to the diagram element, GR could be retrieved
					// But we also need to notify the parent that this diagram element is now to be managed
					if (diagramElement.getParent() != null) {
						diagramElement.getParent().getPropertyChangeSupport().firePropertyChange(DiagramContainerElement.SHAPES, null,
								diagramElement.getParent().getShapes());
					}
				}

				if (getRole() != null && getRole().getGrSpecifications() != null) {
					// When diagram element is set, apply initial values of all GRSpecs
					for (GraphicalElementSpecification<?, GR> grSpec : getRole().getGrSpecifications()) {
						applyValue(grSpec);
					}
				}

			}
		}

		/**
		 * Internally called to apply a GRSpec
		 * 
		 * @param grSpec
		 */
		private <T> void applyValue(GraphicalElementSpecification<T, GR> grSpec) {
			if (grSpec.getValue() != null && grSpec.getValue().isValid()) {
				T initValue;
				try {
					initValue = grSpec.getValue().getBindingValue(getFlexoConceptInstance());
					grSpec.getFeature().applyToGraphicalRepresentation(getDiagramElement().getGraphicalRepresentation(), initValue);
					getPropertyChangeSupport().firePropertyChange(grSpec.getFeatureName(), null, initValue);
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(DiagramElement.INVALIDATE)) {
				// We detect here that the shape which is the graphical facet of this FMLControlledDiagramElement
				// changed it's hierarchy. We have here to propagate this event from this FMLControlledDiagramElement.
				// This notification will be caught by the FMLControlledDiagramDrawing, and relevant GRStructureVisitor
				// will be called
				getPropertyChangeSupport().firePropertyChange(DiagramElement.INVALIDATE, null, getDiagramElement());
			}
		}

		@Override
		public void setRole(GraphicalElementRole<E, GR> aRole) {
			if (aRole != getRole()) {
				for (BindingValueChangeListener<?> l : listeners.values()) {
					l.stopObserving();
					l.delete();
				}
				listeners.clear();
				performSuperSetter(ROLE_KEY, aRole);
				if (aRole != null && aRole.getGrSpecifications() != null) {
					for (GraphicalElementSpecification<?, GR> grSpec : aRole.getGrSpecifications()) {
						listenToGRSpecification(grSpec);
					}
				}
			}
		}

		private <T> void listenToGRSpecification(final GraphicalElementSpecification<T, GR> grSpec) {
			BindingValueChangeListener<T> l = new BindingValueChangeListener<T>(grSpec.getValue(), getFlexoConceptInstance()) {
				@Override
				public void bindingValueChanged(Object source, T newValue) {
					// Hack to force element name (non FML-controlled) to take the name of federated diagram element
					if (grSpec.getFeatureName().equals("label") && getDiagramElement() != null) {
						getDiagramElement().setText((String) newValue);
					}
					// We detect here that the value computed from GRSpec has changed, apply it
					if (getDiagramElement() != null) {
						grSpec.getFeature().applyToGraphicalRepresentation(getDiagramElement().getGraphicalRepresentation(), newValue);
					}
					getPropertyChangeSupport().firePropertyChange(grSpec.getFeatureName(), null, newValue);
				}
			};
			listeners.put(grSpec, l);

		}

		@Override
		public boolean delete(Object... context) {
			for (BindingValueChangeListener<?> l : listeners.values()) {
				l.stopObserving();
				l.delete();
			}
			listeners.clear();
			return performSuperDelete(context);
		}

		@Override
		public boolean undelete(boolean restoreProperties) {
			return performSuperUndelete(restoreProperties);
		}

		// TODO: do it generically for all GRSpecs
		@Override
		public String getLabel() {
			if (getRole() != null && getRole().getLabel() != null) {

				try {

					/*if (getRole().getLabel().toString().equals("company.companyName")) {
						System.out.println("OK, faut que je calcule le label pour " + getFlexoConceptInstance().getStringRepresentation());
						System.out.println("Je tombe sur " + getRole().getLabel().getBindingValue(getFlexoConceptInstance()));
						System.out.println("value=" + getRole().getLabel());*/
					// System.out.println("valid=" + getRole().getLabel().isValid());
					// System.out.println("reason=" + getRole().getLabel().invalidBindingReason());
					// Thread.dumpStack();

					/*if (!getRole().getLabel().isValid()) {
						getRole().getLabel().markedAsToBeReanalized();
						System.out.println("et maintenant pour value=" + getRole().getLabel());
						System.out.println("valid=" + getRole().getLabel().isValid());
						System.out.println("reason=" + getRole().getLabel().invalidBindingReason());
					}*/

					// }

					String newLabel = getRole().getLabel().getBindingValue(getFlexoConceptInstance());

					// We force the name of the DiagramElement to be last computed name
					getDiagramElement().setText(newLabel);

					return newLabel;
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		// TODO: to it generically for all GRSpecs
		@Override
		public void setLabel(String aLabel) {

			// We handle here a special use case encountered in FME
			// When a FlexoConceptInstance changes its type (its FlexoConcept)
			// The role that was registered is not good anymore
			// What we do here is checking that it's the "good" role
			if (getRole().getFlexoConcept() != getFlexoConceptInstance().getFlexoConcept()) {
				ObjectLookupResult r = getDrawing().getObjectLookupResult(getDiagramElement());
				if (r != null) {
					GraphicalElementRole<E, GR> newRole = (GraphicalElementRole<E, GR>) r.property;
					setRole(newRole);
				}
			}

			if (getRole().getLabel() != null) {
				try {
					getRole().getLabel().setBindingValue(aLabel, getFlexoConceptInstance());
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
					e.printStackTrace();
				} catch (NotSettableContextException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
