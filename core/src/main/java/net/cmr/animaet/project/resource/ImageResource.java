package net.cmr.animaet.project.resource;

import java.util.UUID;

import com.badlogic.gdx.graphics.Texture;

public class ImageResource extends Resource {

    Texture texture;

    public ImageResource(String id, String globalPath) {
        super(id, globalPath);
    }

    @Override
    public void load() {
        texture = new Texture(globalPath);
    }

    @Override
    public void dispose() {
        texture.dispose();
    }

    // NOT NULL
    public Texture getTexture() {
        return texture;
    }

    @Override
    public String toString() {
        return getId();
    }

}
