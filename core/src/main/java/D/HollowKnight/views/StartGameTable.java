package D.HollowKnight.views;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class StartGameTable extends Table {

    public StartGameTable(TextButton.TextButtonStyle btnStyle, Table mainTable, Image bgImage) {
        this.setFillParent(true);
        this.center();
        this.setPosition(1280, 0);

        Label.LabelStyle labelStyle = new Label.LabelStyle(btnStyle.font, Color.WHITE);
        Label.LabelStyle titleStyle = new Label.LabelStyle(btnStyle.font, Color.LIGHT_GRAY);

        Label titleLabel = new Label("START GAME MENU", titleStyle);
        titleLabel.setFontScale(1.5f);
        TextButton newGameBtn = new TextButton("START NEW GAME", btnStyle);
        TextButton backBtn = new TextButton("BACK", btnStyle);

        this.add(titleLabel).colspan(2).padBottom(40).row();
        this.add(newGameBtn).colspan(2).padBottom(50).row();

        createSaveSlot(1, false, "", 0, btnStyle, labelStyle);
        createSaveSlot(2, false, "", 0, btnStyle, labelStyle);
        createSaveSlot(3, false, "", 0, btnStyle, labelStyle);
        createSaveSlot(4, false, "", 0, btnStyle, labelStyle);

        this.add(backBtn).colspan(2).padTop(40).row();

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                StartGameTable.this.addAction(Actions.moveTo(1280, 0, 0.5f, Interpolation.smooth));
                mainTable.addAction(Actions.moveTo(0, 0, 0.5f, Interpolation.smooth));
                bgImage.addAction(Actions.fadeOut(0.5f));
            }
        });

        newGameBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Starting a completely NEW GAME...");
                // اTODO open the menu of maps and let the player choose their game's map
            }
        });
    }

    private void createSaveSlot(int slotNumber, boolean hasSavedGame, String mapName, int progressPercentage,
                                TextButton.TextButtonStyle btnStyle, Label.LabelStyle labelStyle) {
        if (hasSavedGame) {
            String infoText = "SLOT " + slotNumber + " : " + progressPercentage + "% COMPLETED - MAP: " + mapName;
            Label infoLabel = new Label(infoText, labelStyle);
            TextButton loadBtn = new TextButton("LOAD", btnStyle);

            this.add(infoLabel).left().padRight(30).padBottom(15);
            this.add(loadBtn).right().padBottom(15).row();

            loadBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    //TODO open the saved game to continue
                }
            });

        }
        else {
            Label infoLabel = new Label("SLOT " + slotNumber + " : -- EMPTY --", labelStyle);
            Label placeholder = new Label("", labelStyle);
            this.add(infoLabel).left().padRight(30).padBottom(15);
            this.add(placeholder).right().padBottom(15).row();
        }
    }
}
