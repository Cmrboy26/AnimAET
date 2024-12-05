package net.cmr.animaet;

import java.io.File;

/**
 * Creates and saves information about saved and created projects.
 */
public class EditorSave {

    File saveFolder;

    /**
     * When loading from a saved project, the EditorSave object should be created with the file to load from.
     * If a new project is created, the EditorSave object should be created when the project is saved, and the
     * save location should be passed in.
     */
    public EditorSave(File saveFolder) {
        this.saveFolder = saveFolder;
    }

    public void load(EditorScreen screen) {

    }

    public void save(EditorScreen screen) {

    }

}
