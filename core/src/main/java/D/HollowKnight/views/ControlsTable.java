package D.HollowKnight.views;

import D.HollowKnight.controllers.MenuController;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ControlsTable extends Table {
    private TextButton upBtn;
    private TextButton downBtn;
    private TextButton leftBtn;
    private TextButton rightBtn;
    private MenuController controller;

    public ControlsTable(TextButton.TextButtonStyle btnStyle, Table settingsTable, MenuController controller) {
        this.controller = controller;
        this.setFillParent(true);
        this.center().padTop(100);
        this.setPosition(1280, 0);

        Label.LabelStyle labelStyle = new Label.LabelStyle(btnStyle.font, Color.WHITE);

         upBtn = createKeyBindButton(controller.getKeyUp(), btnStyle, controller, "up");
         downBtn = createKeyBindButton(controller.getKeyDown(), btnStyle, controller, "down");
         leftBtn = createKeyBindButton(controller.getKeyLeft(), btnStyle, controller, "left");
        rightBtn = createKeyBindButton(controller.getKeyRight(), btnStyle, controller, "right");

        TextButton backBtn = new TextButton("BACK", btnStyle);

        this.add(new Label("MOVE UP:", labelStyle)).padRight(30).padBottom(15);
        this.add(upBtn).width(150).padBottom(15).row();
        this.add(new Label("MOVE DOWN:", labelStyle)).padRight(30).padBottom(15);
        this.add(downBtn).width(150).padBottom(15).row();
        this.add(new Label("MOVE LEFT:", labelStyle)).padRight(30).padBottom(15);
        this.add(leftBtn).width(150).padBottom(15).row();
        this.add(new Label("MOVE RIGHT:", labelStyle)).padRight(30).padBottom(30);
        this.add(rightBtn).width(150).padBottom(30).row();
        this.add(backBtn).colspan(2).padTop(20);

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ControlsTable.this.addAction(Actions.moveTo(1280, 0, 0.5f, Interpolation.exp10Out));
                settingsTable.addAction(Actions.moveTo(0, 0, 0.5f, Interpolation.exp10Out));
            }
        });
    }

    public void refreshLabels() {
        upBtn.setText(Input.Keys.toString(controller.getKeyUp()));
        downBtn.setText(Input.Keys.toString(controller.getKeyDown()));
        leftBtn.setText(Input.Keys.toString(controller.getKeyLeft()));
        rightBtn.setText(Input.Keys.toString(controller.getKeyRight()));
    }

    private TextButton createKeyBindButton(int currentKeycode, TextButton.TextButtonStyle btnStyle, MenuController controller, String actionId) {
        TextButton btn = new TextButton(Input.Keys.toString(currentKeycode), btnStyle);

        btn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                btn.setText("...");
                if(getStage() != null) {
                    getStage().setKeyboardFocus(btn);
                }
            }

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (btn.getText().toString().equals("...")) {
                    btn.setText(Input.Keys.toString(keycode));
                    getStage().setKeyboardFocus(null);

                    switch (actionId) {
                        case "up": controller.setKeyUp(keycode); break;
                        case "down": controller.setKeyDown(keycode); break;
                        case "left": controller.setKeyLeft(keycode); break;
                        case "right": controller.setKeyRight(keycode); break;
                    }
                    return true;
                }
                return false;
            }
        });

        return btn;
    }
}
