package net.cmr.animaet.ui;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class Dropdown extends Table {

    private List<String> options;
    private List<Runnable> actions;
    private Table dropdownTable;
    private Label nameLabel;

    public Dropdown(String name, Skin skin, List<String> options, List<Runnable> actions) {
        super(skin);
        this.options = options;
        this.actions = actions;
        nameLabel = new Label(name, skin);
        nameLabel.setAlignment(Align.left);
        Cell<Label> nameLabelCell = add(nameLabel).left().grow().pad(5).padBottom(5);
        nameLabelCell.row();

        dropdownTable = new Table(skin);
        for (int i = 0; i < options.size(); i++) {
            String option = options.get(i);
            Runnable action = null;
            if (actions.size() >= i) {
                action = actions.get(i);
            }
            if (action == null) {
                action = () -> {};
            }
            final Runnable finalAction = action;
            TextButton optionButton = new TextButton(option, skin);
            optionButton.getLabel().setAlignment(Align.left);
            optionButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    finalAction.run();
                    dropdownTable.setVisible(false);
                }
            });
            optionButton.pad(5);
            dropdownTable.add(optionButton).left().fillX().row();
        }
        dropdownTable.pack();

        nameLabel.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                dropdownTable.setVisible(true);
                addActor(dropdownTable);
                dropdownTable.setPosition(0, 0, Align.topLeft);
            }
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        Vector2 mouseScreenPosition = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        Vector2 mouseLocalPosition = this.screenToLocalCoordinates(mouseScreenPosition);

        if (hit(mouseLocalPosition.x, mouseLocalPosition.y, false) == null) {
            dropdownTable.setVisible(false);
        }
    }

}
