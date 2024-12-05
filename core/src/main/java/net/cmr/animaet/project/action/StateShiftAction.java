package net.cmr.animaet.project.action;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Align;

import net.cmr.animaet.project.EntityAction;
import net.cmr.animaet.project.EntityState;

public class StateShiftAction extends EntityAction {

    EntityState modifier;
    int scaleAlign = Align.bottomLeft;
    Interpolation interpolation = Interpolation.linear;

    public StateShiftAction(float startTime, float endTime, EntityState modifier) {
        this(startTime, endTime, modifier, Interpolation.linear, Align.bottomLeft);
    }

    public StateShiftAction(float startTime, float endTime, EntityState modifier, Interpolation interpolation) {
        this(startTime, endTime, modifier, interpolation, Align.bottomLeft);
    }

    public StateShiftAction(float startTime, float endTime, EntityState modifier, int scaleAlign) {
        this(startTime, endTime, modifier, Interpolation.linear, scaleAlign);
    }

    public StateShiftAction(float startTime, float endTime, EntityState modifier, Interpolation interpolation, int scaleAlign) {
        super(startTime, endTime);
        this.modifier = modifier;
        this.scaleAlign = scaleAlign;
        this.interpolation = interpolation;
    }

    @Override
    public void apply(EntityState entity, float elapsedActionTime) {
        float progress = getProgress(elapsedActionTime);
        progress = interpolation.apply(progress);
        float dx = modifier.getX() * progress;
        float dy = modifier.getY() * progress;
        float dw = modifier.getWidth() * progress;
        float dh = modifier.getHeight() * progress;

        boolean left = Align.isLeft(scaleAlign);
        boolean right = Align.isRight(scaleAlign);
        boolean top = Align.isTop(scaleAlign);
        boolean bottom = Align.isBottom(scaleAlign);

        float x = (right ? 1 : 0) - (left ? 1 : 0) + 1;
        float y = (top ? 1 : 0) - (bottom ? 1 : 0) + 1;

        dx -= x * dw / 2f;
        dy -= y * dh / 2f;

        EntityState state = entity.copy().zero();
        state.setX(dx);
        state.setY(dy);
        state.setWidth(dw);
        state.setHeight(dh);
        entity.add(state);
    }

    public EntityState getModifier() {
        return modifier;
    }

    public void setModifier(EntityState modifier) {
        this.modifier = modifier;
    }

    public int getScaleAlign() {
        return scaleAlign;
    }

    public void setScaleAlign(int scaleAlign) {
        this.scaleAlign = scaleAlign;
    }

    public Interpolation getInterpolation() {
        return interpolation;
    }

    public void setInterpolation(Interpolation interpolation) {
        this.interpolation = interpolation;
    }

}
