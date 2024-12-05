package net.cmr.animaet.project;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Texture;

import net.cmr.animaet.project.resource.ImageResource;
import net.cmr.animaet.project.resource.Resource;

public class AssetManager {

    private HashMap<String, Resource> resources;

    public AssetManager() {
        resources = new HashMap<>();
    }

    public void register(Resource resource) {
        if (resources.containsKey(resource.getId())) {
            throw new IllegalArgumentException("Resource with id " + resource.getId() + " already exists");
        }
        resources.put(resource.getId(), resource);
        resource.load();
    }

    public void unregister(Resource resource) {
        resources.remove(resource.getId());
        resource.dispose();
    }

    public Resource getResource(String id) {
        return resources.get(id);
    }

    public void dispose() {
        for (Resource resource : resources.values()) {
            resource.dispose();
        }
    }

    public Texture getTexture(String id) {
        Resource resource = getResource(id);
        if (resource == null) {
            throw new IllegalArgumentException("Resource with id " + id + " does not exist");
        }
        if (resource instanceof ImageResource) {
            return ((ImageResource) resource).getTexture();
        }
        throw new IllegalArgumentException("Resource with id " + id + " is not an image resource");
    }

}
