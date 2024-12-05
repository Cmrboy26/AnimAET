package net.cmr.animaet;

import java.awt.Color;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;

// Anim(ation) AET (Awesome Editor Tool)
/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class AnimApp extends ApplicationAdapter {

    public static Texture UNKNOWN_TEXTURE;
    private SpriteBatch batch;
    private Screen screen;
    private Skin skin;

    private static AnimApp instance;
    public static AnimApp getAnimApp() {
        return instance;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        Pixmap myPixMap = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
        myPixMap.setColor(Color.BLACK.getRGB());
        myPixMap.fillRectangle(0, 0, 2, 2);
        instance = this;
        setScreen(new EditorScreen(batch, skin));
    }

    @Override
    public void pause() {
        if (screen != null) {
            screen.pause();
        }
    }

    @Override
    public void resume() {
        if (screen != null) {
            screen.resume();
        }
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        if (screen != null) {
            screen.render(Gdx.graphics.getDeltaTime());
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        if (screen != null) {
            screen.resize(width, height);
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (screen != null) {
            screen.hide();
        }
    }

    public void setScreen(Screen screen) {
        if (this.screen != null) {
            this.screen.hide();
        }
        this.screen = screen;
        if (this.screen != null) {
            this.screen.show();
            this.screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
    }
}
