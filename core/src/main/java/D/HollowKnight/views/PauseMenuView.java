package D.HollowKnight.views;

import D.HollowKnight.controllers.GameController;
import D.HollowKnight.controllers.MapController;
import D.HollowKnight.controllers.MenuController;
import D.HollowKnight.models.Charm;
import D.HollowKnight.models.DatabaseManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class PauseMenuView {
    private Stage stage;
    private Table mainPauseTable;
    private Table cheatCodesTable;
    private SettingsTable settingsTable;
    private Image darkOverlay;

    public PauseMenuView(MapController mapController, GameController mainGame, MenuController menuController, TextButton.TextButtonStyle btnStyle) {
        stage = new Stage(new FitViewport(1280, 720));

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0, 0, 0, 0.75f));
        pixmap.fill();
        darkOverlay = new Image(new Texture(pixmap));
        darkOverlay.setSize(1280, 720);
        pixmap.dispose();
        stage.addActor(darkOverlay);

        mainPauseTable = new Table();
        mainPauseTable.setFillParent(true);
        mainPauseTable.center();

        TextButton continueBtn = new TextButton("CONTINUE", btnStyle);
        TextButton cheatBtn = new TextButton("CHEAT CODES", btnStyle);
        TextButton settingsBtn = new TextButton("SETTINGS", btnStyle);
        TextButton saveExitBtn = new TextButton("SAVE & EXIT", btnStyle);

        mainPauseTable.add(continueBtn).padBottom(20).row();
        mainPauseTable.add(cheatBtn).padBottom(20).row();
        mainPauseTable.add(settingsBtn).padBottom(20).row();
        mainPauseTable.add(saveExitBtn).row();
        stage.addActor(mainPauseTable);
        settingsTable = new SettingsTable(btnStyle, mainPauseTable, null, menuController, darkOverlay);
        settingsTable.setVisible(false);
        settingsTable.setPosition(0, 0);
        stage.addActor(settingsTable);

        cheatCodesTable = new Table();
        cheatCodesTable.setFillParent(true);
        cheatCodesTable.center();
        cheatCodesTable.setVisible(false);

        Label.LabelStyle labelStyle = new Label.LabelStyle(btnStyle.font, Color.YELLOW);
        cheatCodesTable.add(new Label("CHEAT CODES MENU", labelStyle)).padBottom(30).row();

        Label.LabelStyle textStyle = new Label.LabelStyle(btnStyle.font, Color.WHITE);
        String cheatsText =
            "1. Boss Arena Teleport (Ctrl & T): Instantly teleport to the False Knight arena.\n" +
                "2. Noclip / Spectator (Ctrl & N): Disable gravity, collisions & animations.\n" +
                "3. Emergency Heal (Ctrl & H): Gain ONE extra health mask.\n" +
                "4. Refill Soul (Ctrl & M): Instantly and fully refill the Soul vessel.\n" +
                "5. God Mode (Ctrl & G): Toggle invincibility against spikes and enemies.\n" +
                "6. Insta-Kill (Ctrl & K): Instantly kill all enemies in the current screen.";

        Label cheatsLabel = new Label(cheatsText, textStyle);
        cheatsLabel.setAlignment(Align.left);

        cheatsLabel.setWrap(true);
        cheatsLabel.setFontScale(0.7f);

        cheatCodesTable.add(cheatsLabel).width(1000).padBottom(40).row();

        TextButton backFromCheatBtn = new TextButton("BACK", btnStyle);
        cheatCodesTable.add(backFromCheatBtn);
        stage.addActor(cheatCodesTable);


        continueBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                mapController.resumeGame();
            }
        });

        cheatBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                mainPauseTable.setVisible(false);
                cheatCodesTable.setVisible(true);
            }
        });

        backFromCheatBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                cheatCodesTable.setVisible(false);
                mainPauseTable.setVisible(true);
            }
        });

        settingsBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                mainPauseTable.setVisible(false);
                settingsTable.setVisible(true);
            }
        });

        saveExitBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                DatabaseManager db = menuController.getDatabase();
                int activeSlot = menuController.getCurrentSlot();
                String currentMap = mapController.getMapPath();

                StringBuilder charmsBuilder = new StringBuilder();
                int count = 0;
                for (Charm charm : mapController.getPlayer().equippedCharms) {
                    charmsBuilder.append(charm.name());
                    count++;
                    // تا زمانی که به آخرین آیتم نرسیده‌ایم، ویرگول اضافه کن
                    if (count < mapController.getPlayer().equippedCharms.size()) {
                        charmsBuilder.append(",");
                    }
                }
                String charmsToSave = charmsBuilder.toString();
                db.saveGameState(
                    activeSlot,
                    currentMap,
                    mapController.getPlayer().getCurrentSpawnPointId(),
                    mapController.getPlayer().getProgressPercentage(),
                    mapController.getPlayer().getUnlockedSpawnsString(),
                    mapController.getPlayer().currentMasks,
                    mapController.getPlayer().maxMasks,
                    mapController.getPlayer().soul,
                    charmsToSave
                );

                mainGame.setScreen(new MainMenuView(mainGame));
            }
        });

        stage.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public boolean keyDown(com.badlogic.gdx.scenes.scene2d.InputEvent event, int keycode) {
                if (keycode == com.badlogic.gdx.Input.Keys.ESCAPE) {

                    if (settingsTable != null && settingsTable.isVisible()) {
                        settingsTable.setVisible(false);
                        mainPauseTable.setVisible(true);
                    }
                    else if (cheatCodesTable != null && cheatCodesTable.isVisible()) {
                        cheatCodesTable.setVisible(false);
                        mainPauseTable.setVisible(true);
                    }
                    else {
                        mapController.resumeGame();
                    }
                    return true;
                }
                return super.keyDown(event, keycode);
            }
        });
    }

    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    public Stage getStage() { return stage; }
    public void dispose() { stage.dispose(); }
}
