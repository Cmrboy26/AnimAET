package net.cmr.animaet.project;

import net.cmr.animaet.project.entity.Entity;

public abstract class EntityAction {

    private float startTime, endTime;
    private Entity entity;

    public EntityAction(float startTime, float endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Called every frame after begin() and before end().
     * @param elapsedActionTime the time since the action started. In the interval: [0, getDuration()]
     */
    public abstract void apply(EntityState entity, float elapsedActionTime);
    /**
     * Called before the first update.
     */
    public void begin(EntityState entity) {

    }
    /**
     * Called when the action is finished (project time >= end time).
     */
    public void end(EntityState entity) {

    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
    protected Entity getEntity() {
        return entity;
    }
    public float getDuration() {
        return endTime - startTime;
    }
    public float getStartTime() {
        return startTime;
    }
    public float getEndTime() {
        return endTime;
    }

    public float getProgress(float elapsedActionTime) {
        float duration = getDuration();
        if (duration == 0) return 1;
        return Math.min(1, elapsedActionTime / duration);
    }
    public void setStartTime(float startTime) {
        this.startTime = startTime;
        if (entity != null) entity.updateActionList();
    }
    public void setEndTime(float endTime) {
        this.endTime = endTime;
    }
}
