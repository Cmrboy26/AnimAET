package net.cmr.animaet.project.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.cmr.animaet.project.BasicEntityState;
import net.cmr.animaet.project.ProjectScene;
import net.cmr.animaet.project.resource.ImageResource;

public class ImageEntity extends Entity<BasicEntityState> {

    ImageResource imageResource;

    public ImageEntity(String id, BasicEntityState state, ImageResource imageResource) {
        super(id, state);
        this.imageResource = imageResource;
    }

    @Override
    public void render(SpriteBatch batch, float elapsedTime, ProjectScene scene) {
        super.render(batch, elapsedTime, scene);
        Texture texture = imageResource.getTexture();
        BasicEntityState state = getEntityState(elapsedTime);
        batch.draw(texture, state.getX() * ENTITY_SCALE, state.getY() * ENTITY_SCALE, state.getWidth() * ENTITY_SCALE, state.getHeight() * ENTITY_SCALE);
        postRender(batch, elapsedTime, scene);
    }

    @Override
    public void update(float elapsedTime, float delta, ProjectScene scene) {
        super.update(elapsedTime, delta, scene);

    }

    public ImageResource getImageResource() {
        return imageResource;
    }

    public void setImageResource(ImageResource imageResource) {
        this.imageResource = imageResource;
    }


}
