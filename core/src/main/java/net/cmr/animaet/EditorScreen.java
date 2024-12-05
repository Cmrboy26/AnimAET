package net.cmr.animaet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.tommyettinger.textra.Font;

import net.cmr.animaet.editor.EditorAction;
import net.cmr.animaet.editor.MoveEditorAction;
import net.cmr.animaet.project.AssetManager;
import net.cmr.animaet.project.BasicEntityState;
import net.cmr.animaet.project.EntityAction;
import net.cmr.animaet.project.EntityState;
import net.cmr.animaet.project.ProjectScene;
import net.cmr.animaet.project.action.StateShiftAction;
import net.cmr.animaet.project.action.VisibilityAction;
import net.cmr.animaet.project.entity.Entity;
import net.cmr.animaet.project.entity.ImageEntity;
import net.cmr.animaet.project.resource.ImageResource;
import net.cmr.animaet.ui.Dropdown;

public class EditorScreen extends ScreenAdapter {

    public static final float UI_ASPECT_RATIO = 16f / 9f;
    public static final float DEFAULT_ASPECT_RATIO = 16f / 9f;
    public static final float MINIMUM_SIZE = 600f;
    public static final float WORLD_SIZE = 500f;

    // TODO: Any area outside of the aspect ratio should be highlighted as "unsafe"
    // TODO: Music will not work when transitioning between scenes. Create a separate music OR general audio manager that can handle this.
    // Another way to go about this is to get rid of the concept of scenes and just have a single scene.

    // LibGDX classes
    ExtendViewport uiViewport;
    Viewport worldViewport;
    Stage uiStage;
    Table uiTable, worldTable;
    SpriteBatch batch;
    Texture texture;

    // Project variables
    float aspectRatio;
    boolean playing = false;
    float elapsedTime = 0;
    float speed = 1f;
    List<ProjectScene> scenes;
    ProjectScene currentScene;
    Skin skin;
    long selectCount = 0;
    EditorAction editorAction;

    AssetManager assetManager;

    boolean entityDebug = false;
    ShapeRenderer debugRenderer;

    TextFieldFilter decimalOnly = new TextFieldFilter.DigitsOnlyFilter() {
        @Override
        public boolean acceptChar(TextField textField, char c) {
            String text = textField.getText();
            int cursor = textField.getCursorPosition();
            String newText = text.substring(0, cursor) + c + text.substring(cursor);

            if (newText.equals("-")) {
                return true;
            }

            if (newText.equals(".")) {
                return true;
            }

            try {
                Float.parseFloat(newText);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    };

    public EditorScreen(SpriteBatch batch, Skin skin) {
        this.batch = batch;
        this.scenes = new ArrayList<>();
        this.skin = skin;
        this.assetManager = new AssetManager();
    }

    @Override
    public void show() {
        super.show();
        texture = new Texture(Gdx.files.internal("libgdx.png"));
        debugRenderer = new ShapeRenderer();

        uiViewport = new ExtendViewport(MINIMUM_SIZE * UI_ASPECT_RATIO, MINIMUM_SIZE);
        setProjectResolution(DEFAULT_ASPECT_RATIO);
        uiStage = new Stage(uiViewport);

        uiTable = new Table();
        uiTable.setSkin(skin);
        uiTable.setFillParent(true);

        Table dropdownTable = new Table();
        dropdownTable.setSkin(skin);
        dropdownTable.setFillParent(true);
        dropdownTable.align(Align.topLeft);
        uiTable.add(dropdownTable).grow();

        ArrayList<String> fileOptions = new ArrayList<>();
        fileOptions.add("New Project...");
        fileOptions.add("Open Project...");
        fileOptions.add("Save Project");
        fileOptions.add("Save Project As...");
        fileOptions.add("Manage Assets");
        ArrayList<Runnable> fileActions = new ArrayList<>();
        fileActions.add(() -> {
            //System.out.println("New");
        });
        fileActions.add(() -> {
            //System.out.println("Open");
        });
        fileActions.add(() -> {
            //System.out.println("Save");
        });
        fileActions.add(() -> {
            //System.out.println("Save As");
        });
        fileActions.add(() -> {
            //System.out.println("Manage Assets");
        });
        Dropdown fileDropdown = new Dropdown("Scene", skin, fileOptions, fileActions);
        dropdownTable.add(fileDropdown).pad(10);

        ArrayList<String> sceneOptions = new ArrayList<>();
        sceneOptions.add("New Scene...");
        sceneOptions.add("Open Scene...");
        sceneOptions.add("Next Scene");
        sceneOptions.add("Previous Scene");
        sceneOptions.add("Manage Scenes...");
        ArrayList<Runnable> sceneActions = new ArrayList<>();
        sceneActions.add(() -> {
            Window newSceneWindow = createWindow("New Scene");

            Label nameLabel = new Label("Name:", skin);
            nameLabel.setAlignment(Align.center);
            newSceneWindow.add(nameLabel).pad(10).colspan(1).fillX();
            TextField nameField = new TextField(null, skin);
            nameField.setAlignment(Align.center);
            newSceneWindow.add(nameField).width(100).height(20).pad(10).fillX().colspan(1).row();;

            TextButton createButton = new TextButton("Create", skin);
            TextButton cancelButton = new TextButton("Cancel", skin);
            createButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    String name = nameField.getText();
                    if (name == null || name.isEmpty()) {
                        showErrorWindow("Name cannot be empty.");
                        return;
                    }
                    ProjectScene newScene = new ProjectScene(EditorScreen.this, name);
                    newSceneWindow.remove();
                    addScene(newScene);
                    setScene(newScene);
                }
            });
            cancelButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    newSceneWindow.remove();
                }
            });
            newSceneWindow.add(cancelButton).pad(10).width(100).fillX().colspan(1);
            newSceneWindow.add(createButton).pad(10).width(100).fillX().colspan(1).row();

            finalizeWindow(newSceneWindow);
        });
        sceneActions.add(() -> {

        });
        sceneActions.add(() -> {
            //System.out.println("Next Scene");
            nextScene();
        });
        sceneActions.add(() -> {
            //System.out.println("Previous Scene");
            previousScene();
        });
        sceneActions.add(() -> {
            // TODO: Open scene manager
        });
        Dropdown sceneDropdown = new Dropdown("Scene", skin, sceneOptions, sceneActions);
        dropdownTable.add(sceneDropdown).pad(10);

        ArrayList<String> editorOptions = new ArrayList<>();
        editorOptions.add("Toggle Play (Space)");
        editorOptions.add("Restart (Ctrl+Space)");
        editorOptions.add("Half Speed");
        editorOptions.add("Normal Speed");
        editorOptions.add("Double Speed");
        ArrayList<Runnable> editorActions = new ArrayList<>();
        editorActions.add(() -> {
            togglePlay();
        });
        editorActions.add(() -> {
            resetTime();
            pausePlay();
        });
        editorActions.add(() -> {
            speed = 0.5f;
        });
        editorActions.add(() -> {
            speed = 1f;
        });
        editorActions.add(() -> {
            speed = 2f;
        });
        Dropdown editorDropdown = new Dropdown("Playback", skin, editorOptions, editorActions);
        dropdownTable.add(editorDropdown).pad(10);

        ArrayList<String> objectOptions = new ArrayList<>();
        objectOptions.add("Add Object... (Ctrl+N)");
        objectOptions.add("Toggle Debug View");
        ArrayList<Runnable> objectActions = new ArrayList<>();
        objectActions.add(() -> {
            //System.out.println("Add Object");
            showNewEntityWindow();
        });
        objectActions.add(() -> {
            entityDebug = !entityDebug;
            //System.out.println("Debug: " + entityDebug);
        });
        Dropdown objectDropdown = new Dropdown("Objects", skin, objectOptions, objectActions);
        dropdownTable.add(objectDropdown).pad(10);

        Slider timeSlider = new Slider(0, 10, 0.001f, false, skin) {
            @Override
            public void act(float delta) {
                super.act(delta);
                setValue(elapsedTime);
            }
        };
        timeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (Objects.equals(event.getTarget(), timeSlider)) {
                    elapsedTime = timeSlider.getValue();
                }
            }
        });
        timeSlider.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                pausePlay();
                return true;
            }
        });
        timeSlider.setValue(0);
        dropdownTable.add(timeSlider).pad(10).growX().top();

        Label timeLabel = new Label("", skin) {
            @Override
            public void act(float delta) {
                super.act(delta);
                String sceneNumber = String.format("%d/%d", scenes.indexOf(currentScene) + 1, scenes.size());
                String minutes = String.format("%02d", (int) (elapsedTime / 60));
                String seconds = String.format("%02d", (int) (elapsedTime % 60));
                String millis = String.format("%03d", (int) ((elapsedTime % 1) * 1000));
                setText(sceneNumber + " | " + minutes + ":" + seconds + "." + millis);
            }
        };
        timeLabel.setAlignment(Align.left);
        dropdownTable.add(timeLabel).pad(10).left().row();

        uiStage.addActor(uiTable);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(uiStage);
        Gdx.input.setInputProcessor(multiplexer);

        ProjectScene exampleScene = new ProjectScene(this, "Example Scene");
        scenes.add(exampleScene);
        currentScene = exampleScene;

        ImageResource iconResource = new ImageResource("icon", "libgdx.png");
        assetManager.register(iconResource);
        ImageResource icon2Resource = new ImageResource("icon2", "ui/uiskin.png");
        assetManager.register(icon2Resource);
        ImageEntity imageEntity = new ImageEntity("example", new BasicEntityState(-25, 0, 50, 15f), (ImageResource) assetManager.getResource("icon"));
        currentScene.addEntity(imageEntity);
        showEntityInformationWindow(imageEntity);

        StateShiftAction shiftAction = new StateShiftAction(1f, 2f, new BasicEntityState(0, 50, 0, 0), Interpolation.elasticOut);
        imageEntity.addAction(shiftAction);

        StateShiftAction shiftAction2 = new StateShiftAction(2f, 5f, new BasicEntityState(0, 0, 100, 0), Interpolation.bounce, Align.center);
        imageEntity.addAction(shiftAction2);
    }

    @Override
    public void render(float delta) {
        update(delta);
        renderWorld(delta * speed);

        uiViewport.apply();
        batch.setProjectionMatrix(uiViewport.getCamera().combined);
        batch.begin();
        uiStage.act();
        uiStage.draw();
        batch.end();

        super.render(delta);
    }

    public void renderWorld(float delta) {
        worldViewport.apply();
        if (playing && currentScene != null) {
            elapsedTime += delta;
            currentScene.update(elapsedTime, delta);
        }

        batch.setProjectionMatrix(worldViewport.getCamera().combined);
        batch.begin();

        //batch.draw(texture, 0, 0, 50, 12.5f);
        if (currentScene != null) {
            currentScene.render(elapsedTime, batch);
        }

        if (editorAction != null) {
            editorAction.render(this, batch, elapsedTime);
        }

        batch.end();
    }

    public void update(float delta) {
        if (editorAction != null) {
            editorAction.update(this, delta);
            if (editorAction.isFinished()) {
                setEditorAction(null);
            }
        }

        boolean rightClicked = Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT);
        if (rightClicked) {
            float inputX = Gdx.input.getX();
            float inputY = Gdx.input.getY();
            Vector3 worldCoordinates = worldViewport.unproject(new Vector3(inputX, inputY, 0));

            Entity hoveringEntity = getHoveringEntity();
            if (hoveringEntity != null) {
                showEntityInformationWindow(hoveringEntity);
            }
        }

        boolean escapePressed = Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE);
        if (escapePressed) {
            Window highestWindow = null;
            for (Actor actor : uiStage.getActors()) {
                if (actor instanceof Window) {
                    Window window = (Window) actor;
                    if (highestWindow == null || window.getZIndex() > highestWindow.getZIndex()) {
                        highestWindow = window;
                    }
                }
            }
            if (highestWindow != null) {
                highestWindow.remove();
            }
        }

        // An easy way to create a move event for an entity would be dragging it to the target position.
        // The user would hold a key and drag the entity to the target position.
        // (Holding specific keys could lock the movement to a specific axis)
        // The user would then release the mouse button and then a window would pop up asking for:
        // - the time to start the movement
        // - the time to end the movement
        // - the interpolation to use
        // - the target position
        // This could also be the same dialog shown when creating a new action in the action window.
        if (Gdx.input.isKeyPressed(Input.Keys.M) && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Entity entity = getHoveringEntity();
            if (entity != null) {
                MoveEditorAction moveAction = new MoveEditorAction(entity, entity.getEntityState(elapsedTime), getMouseWorldX(), getMouseWorldY(), () -> !Gdx.input.isButtonPressed(Input.Buttons.LEFT));
                setEditorAction(moveAction);
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            togglePlay();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            resetTime();
            pausePlay();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            showNewEntityWindow();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            uiStage.getRoot().setVisible(!uiStage.getRoot().isVisible());
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            entityDebug = !entityDebug;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.PERIOD)) {
            nextScene();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.COMMA)) {
            previousScene();
        }
    }

    /**
     * Changes the resolution of the project.
     * @param aspectRatio width/height
     */
    public void setProjectResolution(float aspectRatio) {
        this.aspectRatio = aspectRatio;
        worldViewport = new FitViewport(WORLD_SIZE*aspectRatio, WORLD_SIZE);
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }
        uiViewport.update(width, height, true);
        worldViewport.update(width, height, true);
        worldViewport.getCamera().position.sub(worldViewport.getWorldWidth() / 2, 0, 0);
        super.resize(width, height);
    }

    public void setScene(ProjectScene scene) {
        this.currentScene = scene;
    }

    public void addScene(ProjectScene scene) {
        scenes.add(scene);
    }

    public void nextScene() {
        int index = scenes.indexOf(currentScene);
        if (index < scenes.size() - 1) {
            setScene(scenes.get(index + 1));
        } else {
            setScene(scenes.get(0));
        }
    }

    public void previousScene() {
        int index = scenes.indexOf(currentScene);
        if (index > 0) {
            setScene(scenes.get(index - 1));
        } else {
            setScene(scenes.get(scenes.size() - 1));
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        uiStage.dispose();
        texture.dispose();
        //currentScene.dispose();
        debugRenderer.dispose();
        /*for (ProjectScene scene : scenes) {
            scene.dispose();
        }*/
        assetManager.dispose();
    }

    public boolean getEntityDebug() {
        return entityDebug;
    }

    public ShapeRenderer getDebugRenderer() {
        return debugRenderer;
    }

    public BitmapFont getDebugFont() {
        BitmapFont font = skin.get(BitmapFont.class);
        font.setUseIntegerPositions(false);
        return font;
    }

    public void showEntityInformationWindow(Entity entity) {
        /*if (uiStage.getRoot().findActor("entity_window_"+entity.getId()) != null) {
            return;
        }*/

        Window window = createWindow(String.format("Entity \"%s\"", entity.getId()), "entity_window_"+entity.getId());

        TextButton actionsButton = new TextButton("Actions...", skin);
        actionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showEntityActionsWindow(entity);
            }
        });
        window.add(actionsButton).width(100).pad(10).colspan(1).fillX();

        TextButton editButton = new TextButton("Edit...", skin);
        editButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showEntityEditWindow(entity);
            }
        });
        window.add(editButton).width(100).pad(10).colspan(1).fillX().row();

        TextButton deleteButton = new TextButton("Delete", skin);
        deleteButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Window confirm = createWindow("Confirm Delete");
                confirm.setModal(true);
                confirm.add(String.format("Are you sure you want to delete entity \"%s\"?", entity.getId())).pad(10).colspan(2).row();

                TextButton cancelButton = new TextButton("Cancel", skin);
                cancelButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        confirm.remove();
                    }
                });

                TextButton confirmButton = new TextButton("Confirm", skin);
                confirmButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        currentScene.removeEntity(entity);
                        confirm.remove();
                        window.remove();
                    }
                });

                confirm.add(confirmButton).width(100).pad(10).colspan(1);
                confirm.add(cancelButton).width(100).pad(10).colspan(1).row();

                finalizeWindow(confirm);
            }
        });
        window.add(deleteButton).width(100).pad(10).colspan(1).fillX();

        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                window.remove();
            }
        });
        window.add(closeButton).width(100).pad(10).colspan(1).fillX().row();

        finalizeWindow(window);
    }

    public void showEntityActionsWindow(Entity entity) {
        // Show the list of actions associated with the entity.
        // Present this as a timeline with the ability to add, remove, and edit actions.
        Window window = createWindow(String.format("Actions for Entity \"%s\"", entity.getId()), "entity_actions_window_"+entity.getId());

        finalizeWindow(window);
    }

    public <T extends EntityAction> void showNewActionWindow(Entity entity, @Null T defaultActionValue, Class<? extends T> actionType) {
        Window window = createWindow("New Action");

        Table actionTable = getActionEditTable(defaultActionValue, actionType);
        TextButton closeButton = new TextButton("Close", skin);
        TextButton addButton = new TextButton("Save & Add", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                window.remove();
            }
        });
        addButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Save the action and add it to the entity.
                try {
                    EntityAction action = createActionFromTable(actionTable, actionType);
                    entity.addAction(action);
                    window.remove();
                } catch (IllegalArgumentException e) {
                    showErrorWindow(e.getMessage());
                }
            }
        });

        actionTable.setName("action_table");
        window.add(actionTable).colspan(4).row();

        window.add(closeButton).pad(10).colspan(2).fillX();
        window.add(addButton).pad(10).colspan(2).fillX().row();

        finalizeWindow(window);
    }

    public <T extends EntityAction> Table getActionEditTable(@Null T action, Class<? extends T> type) {
        Table table = new Table(skin);
        table.pad(10);

        Objects.requireNonNull(type);

        if (StateShiftAction.class.equals(type)) {
            StateShiftAction shiftAction = (StateShiftAction) action;

            table.add("Start Time:").pad(10).colspan(1);
            TextField startTimeField = new TextField(null, skin);
            startTimeField.setAlignment(Align.center);
            startTimeField.setTextFieldFilter(decimalOnly);
            startTimeField.setName("startTime");
            if (shiftAction != null) {
                startTimeField.setText(String.valueOf(shiftAction.getStartTime()));
            }
            table.add(startTimeField).width(100).height(20).pad(10);

            table.add("End Time:").pad(10).colspan(1);
            TextField endTimeField = new TextField(null, skin);
            endTimeField.setAlignment(Align.center);
            endTimeField.setTextFieldFilter(decimalOnly);
            endTimeField.setName("endTime");
            if (shiftAction != null) {
                endTimeField.setText(String.valueOf(shiftAction.getEndTime()));
            }
            table.add(endTimeField).width(100).height(20).pad(10).row();

            table.add("X Shift:").pad(10).colspan(1);
            TextField xShiftField = new TextField(null, skin);
            xShiftField.setAlignment(Align.center);
            xShiftField.setTextFieldFilter(decimalOnly);
            xShiftField.setName("xShift");
            if (shiftAction != null) {
                xShiftField.setText(String.valueOf(shiftAction.getModifier().getX()));
            }
            table.add(xShiftField).width(100).height(20).pad(10);

            table.add("Y Shift:").pad(10).colspan(1);
            TextField yShiftField = new TextField(null, skin);
            yShiftField.setAlignment(Align.center);
            yShiftField.setTextFieldFilter(decimalOnly);
            yShiftField.setName("yShift");
            if (shiftAction != null) {
                yShiftField.setText(String.valueOf(shiftAction.getModifier().getY()));
            }
            table.add(yShiftField).width(100).height(20).pad(10).row();

            table.add("Width Shift:").pad(10).colspan(1);
            TextField widthShiftField = new TextField(null, skin);
            widthShiftField.setAlignment(Align.center);
            widthShiftField.setTextFieldFilter(decimalOnly);
            widthShiftField.setName("widthShift");
            if (shiftAction != null) {
                widthShiftField.setText(String.valueOf(shiftAction.getModifier().getWidth()));
            }
            table.add(widthShiftField).width(100).height(20).pad(10);

            table.add("Height Shift:").pad(10).colspan(1);
            TextField heightShiftField = new TextField(null, skin);
            heightShiftField.setAlignment(Align.center);
            heightShiftField.setTextFieldFilter(decimalOnly);
            heightShiftField.setName("heightShift");
            if (shiftAction != null) {
                heightShiftField.setText(String.valueOf(shiftAction.getModifier().getHeight()));
            }
            table.add(heightShiftField).width(100).height(20).pad(10).row();

            table.add("Interpolation:").pad(10).colspan(1);
            SelectBox<String> interpolationSelect = new SelectBox<>(skin);
            interpolationSelect.setAlignment(Align.center);

            HashMap<String, Interpolation> interpolations = new HashMap<>();

            interpolations.put("Linear", Interpolation.linear);
            interpolations.put("Smooth", Interpolation.smooth);
            interpolations.put("Sine", Interpolation.sine);
            interpolations.put("Sine In", Interpolation.sineIn);
            interpolations.put("Sine Out", Interpolation.sineOut);
            interpolations.put("Bounce", Interpolation.bounce);
            interpolations.put("Bounce In", Interpolation.bounceIn);
            interpolations.put("Bounce Out", Interpolation.bounceOut);
            interpolations.put("Elastic", Interpolation.elastic);
            interpolations.put("Elastic In", Interpolation.elasticIn);
            interpolations.put("Elastic Out", Interpolation.elasticOut);

            interpolationSelect.setItems(interpolations.keySet().toArray(new String[0]));

            interpolationSelect.setName("interpolation");
            if (shiftAction != null) {
                Interpolation interpolation = shiftAction.getInterpolation();
                for (String key : interpolations.keySet()) {
                    if (interpolations.get(key) == interpolation) {
                        interpolationSelect.setSelected(key);
                        break;
                    }
                }
            }
            table.add(interpolationSelect).width(100).height(20).pad(10);

            table.add("Scale Align:").pad(10).colspan(1);
            SelectBox<String> scaleAlignSelect = new SelectBox<>(skin);

            HashMap<String, Integer> alignMap = new HashMap<>();
            alignMap.put("Center", Align.center);
            alignMap.put("Top", Align.top);
            alignMap.put("Bottom", Align.bottom);
            alignMap.put("Left", Align.left);
            alignMap.put("Right", Align.right);
            alignMap.put("Top Left", Align.topLeft);
            alignMap.put("Top Right", Align.topRight);
            alignMap.put("Bottom Left", Align.bottomLeft);
            alignMap.put("Bottom Right", Align.bottomRight);

            scaleAlignSelect.setItems(alignMap.keySet().toArray(new String[0]));

            scaleAlignSelect.setName("scaleAlign");
            if (shiftAction != null) {
                int align = shiftAction.getScaleAlign();
                for (String key : alignMap.keySet()) {
                    if (alignMap.get(key) == align) {
                        scaleAlignSelect.setSelected(key);
                        break;
                    }
                }
            }
            table.add(scaleAlignSelect).width(100).height(20).pad(10).row();
        } else if (VisibilityAction.class.equals(type)) {

        }

        return table;
    }

    public EntityAction createActionFromTable(Table table, Class<? extends EntityAction> type) throws IllegalArgumentException {
        Objects.requireNonNull(type);

        if (StateShiftAction.class.equals(type)) {
            TextField startTimeField = table.findActor("startTime");
            TextField endTimeField = table.findActor("endTime");
            TextField xShiftField = table.findActor("xShift");
            TextField yShiftField = table.findActor("yShift");
            TextField widthShiftField = table.findActor("widthShift");
            TextField heightShiftField = table.findActor("heightShift");
            SelectBox<String> interpolationSelect = table.findActor("interpolation");
            SelectBox<String> scaleAlignSelect = table.findActor("scaleAlign");

            float startTime = Float.parseFloat(startTimeField.getText());
            float endTime = Float.parseFloat(endTimeField.getText());
            float xShift = Float.parseFloat(xShiftField.getText()) / Entity.ENTITY_SCALE;
            float yShift = Float.parseFloat(yShiftField.getText()) / Entity.ENTITY_SCALE;
            float widthShift = Float.parseFloat(widthShiftField.getText());
            float heightShift = Float.parseFloat(heightShiftField.getText());
            Interpolation interpolation = Interpolation.linear;
            int scaleAlign = Align.center;

            HashMap<String, Interpolation> interpolations = new HashMap<>();
            interpolations.put("Linear", Interpolation.linear);
            interpolations.put("Smooth", Interpolation.smooth);
            interpolations.put("Sine", Interpolation.sine);
            interpolations.put("Sine In", Interpolation.sineIn);
            interpolations.put("Sine Out", Interpolation.sineOut);
            interpolations.put("Bounce", Interpolation.bounce);
            interpolations.put("Bounce In", Interpolation.bounceIn);
            interpolations.put("Bounce Out", Interpolation.bounceOut);
            interpolations.put("Elastic", Interpolation.elastic);
            interpolations.put("Elastic In", Interpolation.elasticIn);
            interpolations.put("Elastic Out", Interpolation.elasticOut);

            for (String key : interpolations.keySet()) {
                if (key.equals(interpolationSelect.getSelected())) {
                    interpolation = interpolations.get(key);
                    break;
                }
            }

            HashMap<String, Integer> alignMap = new HashMap<>();
            alignMap.put("Center", Align.center);
            alignMap.put("Top", Align.top);
            alignMap.put("Bottom", Align.bottom);
            alignMap.put("Left", Align.left);
            alignMap.put("Right", Align.right);
            alignMap.put("Top Left", Align.topLeft);
            alignMap.put("Top Right", Align.topRight);
            alignMap.put("Bottom Left", Align.bottomLeft);
            alignMap.put("Bottom Right", Align.bottomRight);

            for (String key : alignMap.keySet()) {
                if (key.equals(scaleAlignSelect.getSelected())) {
                    scaleAlign = alignMap.get(key);
                    break;
                }
            }

            return new StateShiftAction(startTime, endTime, new BasicEntityState(xShift, yShift, widthShift, heightShift), interpolation, scaleAlign);
        } else if (VisibilityAction.class.equals(type)) {
            return null;
        }

        return null;
    }

    public void showEntityEditWindow(Entity entity) {
        // Depending on the type of entity, show different options.
        // For example, let the user change the image resource of an image entity.
        Window window = createWindow(String.format("Edit Entity \"%s\"", entity.getId()), "entity_edit_window_"+entity.getId());

        NewEntityType entityType = null;
        for (NewEntityType type : NewEntityType.values()) {
            if (type.type.isInstance(entity)) {
                entityType = type;
                break;
            }
        }

        window.add("Initial Width:").pad(10);
        TextField widthField = new TextField(null, skin);
        widthField.setAlignment(Align.center);
        widthField.setTextFieldFilter(decimalOnly);
        window.add(widthField).width(100).height(20).pad(10);

        window.add("Initial Height:").pad(10);
        TextField heightField = new TextField(null, skin);
        heightField.setAlignment(Align.center);
        heightField.setTextFieldFilter(decimalOnly);
        window.add(heightField).width(100).height(20).pad(10).row();

        Table variableTable = getVariableEditorTable(entityType, entity);
        variableTable.setName("entity_table");
        window.add(variableTable).colspan(4).row();

        TextButton cancelButton = new TextButton("Close", skin);
        TextButton saveButton = new TextButton("Save", skin);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                window.remove();
            }
        });
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (saveButton.isDisabled()) return;
                Table entityTable = window.findActor("entity_table");
                boolean closeWindow = applyVariableEditorTable(entityTable, entity);
                if (closeWindow) {
                    EntityState state = entity.getInitialState();
                    String widthText = widthField.getText();
                    String heightText = heightField.getText();
                    if (widthText != null && !widthText.isEmpty()) {
                        state.setWidth(Float.parseFloat(widthText));
                    }
                    if (heightText != null && !heightText.isEmpty()) {
                        state.setHeight(Float.parseFloat(heightText));
                    }
                    window.remove();
                }
            }
        });
        window.add(cancelButton).pad(10).colspan(2).fillX();
        window.add(saveButton).pad(10).colspan(2).fillX().row();

        finalizeWindow(window);
    }

    public void showNewEntityWindow() {
        final Window window = createWindow("New Entity", "new_entity_window");

        window.add("ID:").pad(10);
        TextField idField = new TextField(null, skin);
        idField.setAlignment(Align.center);
        window.add(idField).width(100).height(20).pad(10);

        window.add("Type:").pad(10);
        SelectBox<NewEntityType> typeSelect = new SelectBox<>(skin);
        typeSelect.setItems(NewEntityType.values());
        typeSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                NewEntityType type = typeSelect.getSelected();
                Table table = getVariableEditorTable(type);
                table.setName("entity_table");
                Table entityTable = window.findActor("entity_table");
                Cell<?> entityTableCell = window.getCell(entityTable);
                entityTableCell.clearActor();
                entityTableCell.setActor(table);
                entityTable.pack();
                window.pack();
            }
        });
        window.add(typeSelect).width(100).height(20).pad(10).row();

        window.add("Width:").pad(10);
        TextField widthField = new TextField(null, skin);
        widthField.setAlignment(Align.center);
        widthField.setTextFieldFilter(decimalOnly);
        window.add(widthField).width(100).height(20).pad(10);

        window.add("Height:").pad(10);
        TextField heightField = new TextField(null, skin);
        heightField.setAlignment(Align.center);
        heightField.setTextFieldFilter(decimalOnly);
        window.add(heightField).width(100).height(20).pad(10).row();

        ClickListener closeWindow = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                window.remove();
            }
        };

        Table table = getVariableEditorTable(NewEntityType.IMAGE);
        table.setName("entity_table");
        Table entityTable = new Table();
        entityTable.pack();
        table.add(entityTable).grow();
        window.add(table).colspan(4).row();
        table.pack();

        TextButton cancelButton = new TextButton("Cancel", skin);
        TextButton createButton = new TextButton("Create", skin);
        cancelButton.addListener(closeWindow);
        createButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                NewEntityType type = typeSelect.getSelected();
                Table entityTable = window.findActor("entity_table");
                float width = Float.parseFloat(widthField.getText());
                float height = Float.parseFloat(heightField.getText());
                String id = idField.getText();
                try {
                    Entity endEntity = null;
                    if (type == NewEntityType.IMAGE) {
                        TextField imageResourceField = entityTable.findActor("imageResource");
                        String imageResource = imageResourceField.getText();
                        ImageResource resource = (ImageResource) assetManager.getResource(imageResource);
                        if (resource == null) {
                            throw new IllegalArgumentException("Resource with id " + imageResource + " does not exist");
                        }
                        ImageEntity entity = new ImageEntity(id, new BasicEntityState(0, 0, width, height, 0), resource);
                        endEntity = entity;
                    } else if (type == NewEntityType.TEXT) {

                    }
                    endEntity.addAction(new VisibilityAction(elapsedTime, VisibilityAction.OPAQUE));
                    currentScene.addEntity(endEntity);
                    window.remove();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    showErrorWindow("Invalid input: " + e.getMessage());
                }
            }
        });
        window.add(cancelButton).pad(10).colspan(2).fillX();
        window.add(createButton).pad(10).colspan(2).fillX().row();

        finalizeWindow(window);
    }

    private Table getVariableEditorTable(NewEntityType type) {
        return getVariableEditorTable(type, null);
    }

    private Table getVariableEditorTable(NewEntityType type, @Null Entity defaultValues) {
        Table table = new Table();
        table.setSkin(skin);
        table.pad(10);
        table.add(type.name()).colspan(4).pad(10).row();

        if (type == NewEntityType.IMAGE) {
            table.add("Image Resource:").pad(10);
            ImageEntity imageEntity = (ImageEntity) defaultValues;
            TextField pathField = new TextField(null, skin);
            pathField.setName("imageResource");
            pathField.setAlignment(Align.center);
            if (imageEntity != null) {
                pathField.setText(imageEntity.getImageResource().getId());
            }
            table.add(pathField).width(100).height(20).colspan(1).pad(10).row();
        } else if (type == NewEntityType.TEXT) {
            table.add("Text:").pad(10);
            // TextEntity textEntity = (TextEntity) defaultValues;
            TextField textField = new TextField(null, skin);
            textField.setAlignment(Align.center);
            /*
            if (textEntity != null) {
                textField.setText(textEntity.getText().toString());
            }
             */
            table.add(textField).width(100).height(20).colspan(1).pad(10).row();
        }

        return table;
    }

    public boolean applyVariableEditorTable(Table variableEditorTable, Entity entity) {
        if (variableEditorTable == null) {
            return false;
        }
        try {
            if (entity instanceof ImageEntity) {
                ImageEntity imageEntity = (ImageEntity) entity;
                TextField imageResourceField = variableEditorTable.findActor("imageResource");
                String id = imageEntity.getId();
                String imageResource = imageResourceField.getText();
                ImageResource resource = (ImageResource) assetManager.getResource(imageResource);
                if (resource == null) {
                    throw new IllegalArgumentException("Resource with id " + imageResource + " does not exist");
                }
                imageEntity.setImageResource(resource);
            }
            return true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            showErrorWindow("Invalid input: " + e.getMessage());
            return false;
        }
    }

    public void showErrorWindow(String message) {
        Window window = createWindow("Error");
        window.setModal(true);
        window.add(message).pad(10).row();
        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                window.remove();
            }
        });
        window.add(closeButton).pad(10).row();
        finalizeWindow(window);
    }

    private Window createWindow(String title) {
        return createWindow(title, null);
    }

    private Window createWindow(String title, String id) {
        Window window = new Window(title, skin);
        window.setMovable(true);
        window.setResizable(true);
        window.setModal(false);
        window.setName(id);
        window.getTitleLabel().setAlignment(Align.center);
        window.pad(10);
        window.padTop(25);
        return window;
    }

    private void finalizeWindow(Window window) {
        if (window.getName() != null) {
            // Ensure that the window is unique.
            if (uiStage.getRoot().findActor(window.getName()) != null) {
                return;
            }
        }
        window.pack();
        window.setPosition(uiStage.getWidth() / 2f, uiStage.getHeight() / 2f, Align.center);
        uiStage.addActor(window);
    }

    public void togglePlay() {
        playing = !playing;
    }

    public void pausePlay() {
        playing = false;
    }

    public void play() {
        playing = true;
    }

    public void resetTime() {
        elapsedTime = 0;
    }

    public float getMouseWorldX() {
        return worldViewport.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0)).x;
    }

    public float getMouseWorldY() {
        return worldViewport.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0)).y;
    }

    public void setEditorAction(EditorAction action) {
        if (editorAction != null) {
            editorAction.end(this, action != null);
        }
        this.editorAction = action;

    }

    public @Null Entity getHoveringEntity() {
        float wx = getMouseWorldX();
        float wy = getMouseWorldY();
        selectCount++;
        ArrayList<Entity> insideEntities = new ArrayList<>();
        for (Entity entity : currentScene.getEntities()) {
            EntityState state = entity.getEntityState(elapsedTime);
            float x = state.getX() * Entity.ENTITY_SCALE;
            float y = state.getY() * Entity.ENTITY_SCALE;
            float width = state.getWidth() * Entity.ENTITY_SCALE;
            float height = state.getHeight() * Entity.ENTITY_SCALE;
            boolean insideRectangle = wx >= x && wx <= x + width &&
                    wy >= y && wy <= y + height;
            if (insideRectangle) {
                insideEntities.add(entity);
            }
        }
        if (insideEntities.size() > 0) {
            return insideEntities.get((int) (selectCount % insideEntities.size()));
        }
        return null;
    }

    private enum NewEntityType {
        IMAGE(ImageEntity.class, "Image"),
        TEXT(ImageEntity.class, "Text"),
        ;

        public final Class<? extends Entity> type;
        public final String name;

        NewEntityType(Class<? extends Entity> type, String name) {
            this.type = type;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public float getElapsedTime() {
        return elapsedTime;
    }

}
