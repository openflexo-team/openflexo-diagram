package org.openflexo.technologyadapter.diagram.model.action;

import java.io.File;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoAction;

@SuppressWarnings("serial")
public class FileIsNotAnImageFileExtension extends FlexoException {

	public FileIsNotAnImageFileExtension(FlexoAction<?, ?, ?> action, File f) {
		super(action.getLocales().localizedForKey("this_file_does_not_appear_as_a_valid_image_file_name") + " : " + f.getName());
	}
}
