package D.HollowKnight.views;

import D.HollowKnight.controllers.MenuController;
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
    private Table mapSelectionTable;
    private TextButton.TextButtonStyle btnStyle;
    private Table mainTable;
    private Image bgImage;
    private MenuController controller;

    public StartGameTable(TextButton.TextButtonStyle btnStyle, Table mainTable, Image bgImage, MenuController controller) {
        this.btnStyle = btnStyle;
        this.mainTable = mainTable;
        this.bgImage = bgImage;
        this.controller = controller;

        this.setFillParent(true);
        this.center();
        this.setPosition(1280, 0);

        buildUI();
    }

    public void setMapSelectionTable(Table mapSelectionTable) {
        this.mapSelectionTable = mapSelectionTable;
    }

    private void buildUI() {
        this.clearChildren();

        Label.LabelStyle labelStyle = new Label.LabelStyle(btnStyle.font, Color.WHITE);
        Label.LabelStyle titleStyle = new Label.LabelStyle(btnStyle.font, Color.LIGHT_GRAY);

        Label titleLabel = new Label("START GAME MENU", titleStyle);
        titleLabel.setFontScale(1.5f);
        TextButton newGameBtn = new TextButton("START NEW GAME", btnStyle);
        TextButton backBtn = new TextButton("BACK", btnStyle);

        this.add(titleLabel).colspan(2).padBottom(40).row();
        this.add(newGameBtn).colspan(2).padBottom(50).row();

        for (int i = 1; i <= 4; i++) {
            boolean hasSave = controller.hasSave(i);
            String mapName = controller.getMapName(i);
            int progress = controller.getProgress(i);
            createSaveSlot(i, hasSave, mapName, progress, labelStyle);
        }

        this.add(backBtn).colspan(2).padTop(40).row();

        backBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                StartGameTable.this.addAction(Actions.moveTo(1280, 0, 0.75f, Interpolation.swingOut));
                mainTable.addAction(Actions.moveTo(0, 0, 0.75f, Interpolation.swingOut));
                bgImage.addAction(Actions.fadeOut(0.75f));
            }
        });

        newGameBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                int emptySlot = -1;

                for (int i = 1; i <= 4; i++) {
                    if (!controller.hasSave(i)) {
                        emptySlot = i;
                        break;
                    }
                }

                if (emptySlot != -1) {
                    controller.setCurrentSlot(emptySlot);
                    controller.getDatabase().deleteSaveSlot(emptySlot);

                    if (mapSelectionTable != null) {
                        StartGameTable.this.addAction(Actions.moveTo(-1280, 0, 0.75f, Interpolation.exp10Out));
                        mapSelectionTable.addAction(Actions.moveTo(0, 0, 0.75f, Interpolation.exp10Out));
                    }
                } else {
                    newGameBtn.setText("ALL SLOTS FULL! DELETE ONE TO START");
                    newGameBtn.getLabel().setColor(Color.RED);
                }
            }
        });
    }

    private void createSaveSlot(int slotNumber, boolean hasSavedGame, String mapName, int progressPercentage, Label.LabelStyle labelStyle) {
        if (hasSavedGame) {
            String infoText = "SLOT " + slotNumber + " : " + progressPercentage + "% COMPLETED - MAP: " + mapName;
            Label infoLabel = new Label(infoText, labelStyle);

            Table buttonsTable = new Table();
            TextButton loadBtn = new TextButton("LOAD", btnStyle);
            TextButton deleteBtn = new TextButton("DELETE", btnStyle);

            deleteBtn.getLabel().setColor(Color.RED);

            buttonsTable.add(loadBtn).padRight(15);
            buttonsTable.add(deleteBtn);

            this.add(infoLabel).left().padRight(30).padBottom(15);
            this.add(buttonsTable).right().padBottom(15).row();

            loadBtn.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    controller.setCurrentSlot(slotNumber);

                    controller.loadGame(slotNumber);
                }
            });

            deleteBtn.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    controller.getDatabase().deleteSaveSlot(slotNumber);
                    buildUI();
                }
            });
        } else {
            Label infoLabel = new Label("SLOT " + slotNumber + " : -- EMPTY --", labelStyle);
            Label placeholder = new Label("", labelStyle); // فضای خالی برای تراز بودن جدول
            this.add(infoLabel).left().padRight(30).padBottom(15);
            this.add(placeholder).right().padBottom(15).row();
        }
    }
}
