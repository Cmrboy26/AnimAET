package net.cmr.animaet.project.action;

import com.badlogic.gdx.math.Interpolation;

import net.cmr.animaet.project.EntityAction;
import net.cmr.animaet.project.EntityState;

public class VisibilityAction extends EntityAction {

    float alpha;
    Interpolation interpolation;

    public static final float OPAQUE = 1f;
    public static final float TRANSPARENT = 0f;

    public VisibilityAction(float time, float alpha) {
        this(time, time, alpha);
    }

    public VisibilityAction(float startTime, float endTime, float alpha) {
        this(startTime, endTime, alpha, Interpolation.linear);
    }

    public VisibilityAction(float startTime, float endTime, float alpha, Interpolation interpolation) {
        super(startTime, endTime);
        this.alpha = alpha;
        this.interpolation = interpolation;
    }

    @Override
    public void apply(EntityState entity, float elapsedActionTime) {
        float progress = getProgress(elapsedActionTime);
        progress = interpolation.apply(progress);
        float targetAlpha = alpha;
        float currentAlpha = entity.getAlpha();
        float deltaAlpha = targetAlpha - currentAlpha;

        EntityState state = entity.copy().zero();
        state.setAlpha(currentAlpha + deltaAlpha * progress);
        entity.add(state);
    }

}
