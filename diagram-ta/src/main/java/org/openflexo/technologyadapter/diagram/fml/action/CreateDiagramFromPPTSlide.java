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

package org.openflexo.technologyadapter.diagram.fml.action;

import java.security.InvalidParameterException;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.action.FlexoActionFactory;
import org.openflexo.foundation.fml.FMLObject;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.diagram.DiagramTechnologyAdapter;
import org.openflexo.technologyadapter.diagram.rm.DiagramRepository;
import org.openflexo.technologyadapter.diagram.rm.DiagramResource;
import org.openflexo.technologyadapter.diagram.rm.DiagramResourceFactory;

public class CreateDiagramFromPPTSlide extends AbstractCreateDiagramFromPPTSlide<CreateDiagramFromPPTSlide, RepositoryFolder> {

	private static final Logger logger = Logger.getLogger(CreateDiagramFromPPTSlide.class.getPackage().getName());

	public static FlexoActionFactory<CreateDiagramFromPPTSlide, RepositoryFolder, FMLObject> actionType = new FlexoActionFactory<CreateDiagramFromPPTSlide, RepositoryFolder, FMLObject>(
			"create_diagram_from_ppt_slide", FlexoActionFactory.newMenu, FlexoActionFactory.defaultGroup,
			FlexoActionFactory.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateDiagramFromPPTSlide makeNewAction(RepositoryFolder focusedObject, Vector<FMLObject> globalSelection,
				FlexoEditor editor) {
			return new CreateDiagramFromPPTSlide(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(RepositoryFolder object, Vector<FMLObject> globalSelection) {
			if (object != null && object.getResourceRepository() instanceof DiagramRepository) {
				return true;
			}
			return false;
		}

		@Override
		public boolean isEnabledForSelection(RepositoryFolder object, Vector<FMLObject> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoObjectImpl.addActionForClass(CreateDiagramFromPPTSlide.actionType, RepositoryFolder.class);
	}

	CreateDiagramFromPPTSlide(RepositoryFolder focusedObject, Vector<FMLObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws InvalidParameterException, FlexoException {
		logger.info("Add diagram from ppt slide");

		try {
			if (getDiagram() == null) {
				DiagramResource newDiagramResource = makeDiagramResource(true);
				setDiagramResource(newDiagramResource);
			}

			else if (getDiagram().getResource() == null) {
				DiagramResource newDiagramResource = makeDiagramResource(false);
				getDiagram().setResource(newDiagramResource);
				newDiagramResource.setResourceData(getDiagram());
				setDiagramResource(newDiagramResource);
			}
		} catch (ModelDefinitionException e) {
			throw new FlexoException(e);
		}

		if (getSlide() != null) {
			convertSlideToDiagram(getSlide());
		}
		else {
			System.out.println("Error: no Slide");
		}
	}

	protected <I> DiagramResource makeDiagramResource(boolean createEmptyContents) throws SaveResourceException, ModelDefinitionException {
		DiagramTechnologyAdapter diagramTA = getServiceManager().getTechnologyAdapterService()
				.getTechnologyAdapter(DiagramTechnologyAdapter.class);

		FlexoResourceCenter<I> rc = getFocusedObject().getResourceRepository().getResourceCenter();

		String artefactName = getDiagramName().endsWith(DiagramResourceFactory.DIAGRAM_SUFFIX) ? getDiagramName()
				: getDiagramName() + DiagramResourceFactory.DIAGRAM_SUFFIX;

		I serializationArtefact = rc.createEntry(artefactName, (I) getFocusedObject().getSerializationArtefact());

		DiagramResource newDiagramResource = diagramTA.getDiagramResourceFactory().makeResource(serializationArtefact, rc, artefactName,
				getDiagramURI(), createEmptyContents);

		return newDiagramResource;
	}

	@Override
	public String getDiagramURI() {
		return getDefaultDiagramURI();
	}

	public String getDefaultDiagramURI() {
		return getFocusedObject().getResourceRepository().generateURI(getDiagramName());
	}

	/*@Override
	public File getDiagramFile() {
		return getDefaultDiagramFile();
	}
	
	public File getDefaultDiagramFile() {
		return new File(getFocusedObject().getFile(), getDiagramName() + DiagramResource.DIAGRAM_SUFFIX);
	}*/

}
