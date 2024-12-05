package net.cmr.animaet.editor;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.cmr.animaet.EditorScreen;

public abstract class EditorAction {

    public void update(EditorScreen screen, float delta) {

    }

    public void render(EditorScreen screen, SpriteBatch batch, float elapsedTime) {

    }

    public abstract boolean isFinished();

    public void end(EditorScreen screen, boolean interupted) {

    }

}
