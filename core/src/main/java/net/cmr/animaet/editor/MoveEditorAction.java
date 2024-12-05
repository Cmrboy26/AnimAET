package net.cmr.animaet.editor;

import java.util.function.BooleanSupplier;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import net.cmr.animaet.EditorScreen;
import net.cmr.animaet.project.BasicEntityState;
import net.cmr.animaet.project.EntityState;
import net.cmr.animaet.project.action.StateShiftAction;
import net.cmr.animaet.project.entity.Entity;

public class MoveEditorAction extends EditorAction{

    float relativeX, relativeY;
    float initialWorldX, initialWorldY;
    EntityState entityState;
    BooleanSupplier actionFinished;
    Entity entity;
    Float initialEditorTime;

    public MoveEditorAction(Entity entity, EntityState clickedEntityState, float worldX, float worldY, BooleanSupplier actionFinished) {
        relativeX = worldX - clickedEntityState.getX() * Entity.ENTITY_SCALE;
        relativeY = worldY - clickedEntityState.getY() * Entity.ENTITY_SCALE;
        initialWorldX = worldX;
        initialWorldY = worldY;
        entityState = clickedEntityState;
        this.actionFinished = actionFinished;
        this.entity = entity;
    }

    @Override
    public void update(EditorScreen screen, float delta) {
        if (initialEditorTime == null) {
            initialEditorTime = screen.getElapsedTime();
        }
    }

    @Override
    public void render(EditorScreen screen, SpriteBatch batch, float elapsedTime) {
        Vector2 delta = getDeltaVector(screen.getMouseWorldX(), screen.getMouseWorldY());

        float entityX = entityState.getX() * Entity.ENTITY_SCALE + delta.x;
        float entityY = entityState.getY() * Entity.ENTITY_SCALE + delta.y;

        batch.end();
        ShapeRenderer shape = screen.getDebugRenderer();
        shape.setProjectionMatrix(batch.getProjectionMatrix());
        shape.setTransformMatrix(batch.getTransformMatrix());
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(1, 1, 1, 1);
        shape.rect(entityX, entityY, entityState.getWidth() * Entity.ENTITY_SCALE,
                entityState.getHeight() * Entity.ENTITY_SCALE);
        shape.end();
        batch.begin();
    }

    public boolean isFinished() {
        return actionFinished.getAsBoolean();
    }

    public Vector2 getDeltaVector(float worldX, float worldY) {
        float dx = worldX - initialWorldX;
        float dy = worldY - initialWorldY;

        // Shift: snap to axis
        // Control: snap to grid (10%)

        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            if (Math.abs(dx) > Math.abs(dy)) {
                dy = 0;
            } else {
                dx = 0;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            float gridSize = 5 * Entity.ENTITY_SCALE;
            dx = Math.round(dx / gridSize) * gridSize;
            dy = Math.round(dy / gridSize) * gridSize;
        }
        return new Vector2(dx, dy);
    }

    @Override
    public void end(EditorScreen screen, boolean interupted) {
        if (!interupted) {
            Vector2 delta = getDeltaVector(screen.getMouseWorldX(), screen.getMouseWorldY());
            float startTime = initialEditorTime;
            float endTime = screen.getElapsedTime();
            float deltaTime = Math.max(.1f, Math.abs(endTime - startTime));

            StateShiftAction defaultActionValues = new StateShiftAction(initialEditorTime, initialEditorTime + deltaTime, new BasicEntityState(delta.x, delta.y, 0, 0));
            screen.showNewActionWindow(entity, defaultActionValues, StateShiftAction.class);
        }
    }

}
