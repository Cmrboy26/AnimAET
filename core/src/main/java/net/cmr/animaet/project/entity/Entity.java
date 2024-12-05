package net.cmr.animaet.project.entity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType.Bitmap;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;

import net.cmr.animaet.EditorScreen;
import net.cmr.animaet.project.EntityAction;
import net.cmr.animaet.project.EntityState;
import net.cmr.animaet.project.ProjectScene;

/**
 * Represents an entity in a scene (in a project).
 */
public abstract class Entity<State extends EntityState> {

    public static final float ENTITY_SCALE = EditorScreen.WORLD_SIZE / 100f;
    private ArrayList<EntityAction> actions;
    private State initialState;
    private String id;
    // Add a cached x and y value that can be used when delta is 0
    //private float x, y, width, height;

    public Entity(String id, State initialState) {
        this.initialState = initialState;
        this.id = id;
        actions = new ArrayList<>();
    }

    public void update(float elapsedTime, float updateDelta, ProjectScene scene) {
        float previousTime = elapsedTime - updateDelta;
        State state = getEntityState(elapsedTime);
    }

    public void render(SpriteBatch batch, float elapsedTime, ProjectScene scene) {
        State state = getEntityState(elapsedTime);
        batch.setColor(1, 1, 1, state.getAlpha());
        //System.out.println(state.getX() + " " + state.getY() + " " + state.getWidth() + " " + state.getHeight());
    }

    public final void postRender(SpriteBatch batch, float elapsedTime, ProjectScene scene) {
        batch.setColor(1, 1, 1, 1);
        // Draw debug information
        if (scene.getScreen().getEntityDebug()) {
            State state = getEntityState(elapsedTime);
            String debugInformation = "id: \"" + getId() + "\"";
            DecimalFormat df = new DecimalFormat("#.#");
            debugInformation += "\nx: " + df.format(state.getX()) + " \ty: " + df.format(state.getY()) + " \nw: " + df.format(state.getWidth()) + " \th: " + df.format(state.getHeight());
            BitmapFont font = scene.getScreen().getDebugFont();
            font.draw(batch, debugInformation, state.getX() * ENTITY_SCALE, state.getY() * ENTITY_SCALE + state.getHeight() * ENTITY_SCALE);
            batch.end();
            ShapeRenderer shape = scene.getScreen().getDebugRenderer();
            shape.setProjectionMatrix(batch.getProjectionMatrix());
            shape.setTransformMatrix(batch.getTransformMatrix());
            shape.begin(ShapeRenderer.ShapeType.Line);
            shape.setColor(1, 1, 1, 1);
            shape.rect(state.getX() * ENTITY_SCALE, state.getY() * ENTITY_SCALE, state.getWidth() * ENTITY_SCALE, state.getHeight() * ENTITY_SCALE);
            shape.end();
            batch.begin();
        }
    }

    public void addAction(EntityAction action) {
        action.setEntity(this);
        actions.add(action);
        updateActionList();
    }

    public void updateActionList() {
        actions.sort(Comparator.comparing(EntityAction::getStartTime));
    }

    public State getInitialState() {
        return initialState;
    }
    public void setInitialState(State initialState) {
        this.initialState = initialState;
    }

    public State getEntityState(float elapsedTime) {
        @SuppressWarnings("unchecked")
        State state = (State) getInitialState().copy();
        for (EntityAction action : actions) {
            if (action.getStartTime() <= elapsedTime) {
                action.apply(state, Math.min(action.getDuration(), elapsedTime - action.getStartTime()));
            }
        }
        return state;
    }

    public String getId() {
        return id;
    }
}
