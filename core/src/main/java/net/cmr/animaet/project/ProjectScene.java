package net.cmr.animaet.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.cmr.animaet.EditorScreen;
import net.cmr.animaet.project.entity.Entity;
import net.cmr.animaet.project.resource.ImageResource;
import net.cmr.animaet.project.resource.Resource;

public class ProjectScene {

    String name;
    private HashMap<String, Entity> entities;
    private transient EditorScreen screen;

    public ProjectScene(EditorScreen screen, String name) {
        this.screen = screen;
        this.name = name;
        this.entities = new HashMap<>();
    }

    public void render(float elapsedTime, SpriteBatch batch) {
        batch.setColor(Color.WHITE);
        for (Entity entity : entities.values()) {
            entity.render(batch, elapsedTime, this);
        }
        batch.setColor(Color.WHITE);
    }

    public void update(float elapsedTime, float delta) {
        float previousTime = elapsedTime - delta;
        for (Entity entity : entities.values()) {
            entity.update(elapsedTime, delta, this);
        }
    }

    public void addEntity(Entity entity){
        if (entities.containsKey(entity.getId())) {
            throw new IllegalArgumentException("Entity with id " + entity.getId() + " already exists in scene " + name);
        }
        entities.put(entity.getId(), entity);
    }

    public void removeEntity(String id) {
        entities.remove(id);
    }

    public void removeEntity(Entity entity) {
        removeEntity(entity.getId());
    }

    public Entity getEntity(String id) {
        return entities.get(id);
    }

    public List<Entity> getEntities() {
        return new ArrayList<>(entities.values());
    }

    public EditorScreen getScreen() {
        return screen;
    }

}
