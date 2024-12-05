package net.cmr.animaet.project;

import com.badlogic.gdx.math.Vector4;

public abstract class EntityState {

    private float x, y, width, height, alpha;

    public EntityState(float x, float y, float width, float height, float alpha) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.alpha = alpha;
    }

    public abstract EntityState copy();
    public abstract EntityState zero();

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public void add(EntityState state) {
        x += state.x;
        y += state.y;
        width += state.width;
        height += state.height;
        alpha += state.alpha;
    }

    public void sub(EntityState state) {
        x -= state.x;
        y -= state.y;
        width -= state.width;
        height -= state.height;
        alpha -= state.alpha;
    }

}
