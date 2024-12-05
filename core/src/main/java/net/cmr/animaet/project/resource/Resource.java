package net.cmr.animaet.project.resource;

import java.util.UUID;

public abstract class Resource {

    public String id;
    public String globalPath;

    public Resource(String id, String globalPath) {
        this.globalPath = globalPath;
        this.id = id;
    }

    public abstract void load();
    public abstract void dispose();

    public String getId() {
        return id;
    }

}
