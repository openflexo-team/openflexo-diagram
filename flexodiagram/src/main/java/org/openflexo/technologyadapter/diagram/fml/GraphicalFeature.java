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

package org.openflexo.technologyadapter.diagram.fml;

import java.util.logging.Logger;

import org.openflexo.diana.GRProperty;
import org.openflexo.diana.GraphicalRepresentation;

/**
 * This class represent a graphical feature that is to be associated on a DiagramElement
 * 
 * @author sylvain
 * 
 */
public abstract class GraphicalFeature<T, GR extends GraphicalRepresentation> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(GraphicalFeature.class.getPackage().getName());

	private String name;
	private GRProperty<T> parameter;

	public GraphicalFeature(String name, GRProperty<T> parameter) {
		this.name = name;
		this.parameter = parameter;
	}

	public String getName() {
		return name;
	}

	public GRProperty<T> getParameter() {
		return parameter;
	}

	public Class<T> getType() {
		return parameter.getType();
	}

	public abstract void applyToGraphicalRepresentation(GR gr, T value);

	public abstract T retrieveFromGraphicalRepresentation(GR gr);

	@Override
	public String toString() {
		return "GraphicalFeature[" + getName() + "]";
	}
}
