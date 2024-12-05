package net.cmr.animaet.project;

public class BasicEntityState extends EntityState {

    public BasicEntityState(float x, float y, float width, float height, float alpha) {
        super(x, y, width, height, alpha);
    }

    public BasicEntityState(float x, float y, float width, float height) {
        super(x, y, width, height, 1);
    }

    @Override
    public BasicEntityState copy() {
        return new BasicEntityState(getX(), getY(), getWidth(), getHeight(), getAlpha());
    }

    @Override
    public BasicEntityState zero() {
        return new BasicEntityState(0, 0, 0, 0, 0);
    }

}
