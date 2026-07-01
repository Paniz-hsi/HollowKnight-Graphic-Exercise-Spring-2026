package D.HollowKnight.views;

import D.HollowKnight.controllers.GameController;
import D.HollowKnight.controllers.MenuController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainMenuView implements Screen {
    private GameController mainGame;
    private Stage stage;
    private Texture background;
    private BitmapFont font;

    private Table mainTable;
    private SettingsTable settingsTable;
    private StartGameTable startGameTable;

    private Image mainBg;
    private Image settingsBg;

    public MainMenuView(GameController mainGame) {
        this.mainGame = mainGame;
        stage = new Stage(new FitViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);

        background = new Texture("backgroundmain.png");
        mainBg = new Image(background);
        mainBg.setFillParent(true);

        settingsBg = new Image(new Texture("setback.png"));
        settingsBg.setFillParent(true);
        settingsBg.getColor().a = 0f;

        stage.addActor(mainBg);
        stage.addActor(settingsBg);

        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("Trajans.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 30;
        font = gen.generateFont(param);
        gen.dispose();

        TextButtonStyle style = new TextButtonStyle();
        style.font = font;
        style.fontColor = Color.WHITE;
        style.overFontColor = Color.YELLOW;

        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center().padTop(450);

        TextButton startBtn = new TextButton("START GAME", style);
        TextButton settingsBtn =new TextButton("SETTINGS", style);
        TextButton guideBtn = new TextButton("GUIDE", style);
        TextButton achievementsBtn = new TextButton("ACHIEVEMENTS", style);
        TextButton quitBtn = new TextButton("QUIT GAME", style);

        mainTable.add(startBtn).padBottom(15).padLeft(45).row();
        mainTable.add(settingsBtn).padBottom(15).padLeft(45).row();
        mainTable.add(guideBtn).padBottom(15).padLeft(45).row();
        mainTable.add(achievementsBtn).padBottom(15).padLeft(45).row();
        mainTable.add(quitBtn).padLeft(45).row();


        MenuController controller = new MenuController(mainGame);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        Image darknessLayer = new Image(new Texture(pixmap));
        pixmap.dispose();

        darknessLayer.setFillParent(true);
        darknessLayer.setTouchable(Touchable.disabled);
        int initialBrightness = controller.getBrightness();
        darknessLayer.getColor().a = 1f - (initialBrightness / 100f);

        settingsTable = new SettingsTable(style, mainTable, settingsBg, controller, darknessLayer);
        startGameTable = new StartGameTable(style, mainTable, settingsBg, controller);
        ControlsTable controlsTable = new ControlsTable(style, settingsTable, controller);
        settingsTable.setControlsTable(controlsTable);

        MapSelectionTable mapSelectionTable = new MapSelectionTable(style, startGameTable, controller);
        startGameTable.setMapSelectionTable(mapSelectionTable);

        settingsBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainTable.addAction(Actions.moveTo(-1280, 0, 0.75f, Interpolation.exp10Out));
                settingsTable.addAction(Actions.moveTo(0, 0, 0.75f, Interpolation.exp10Out));
                settingsBg.addAction(Actions.fadeIn(0.4f));
            }
        });

        startBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainTable.addAction(Actions.moveTo(-1280, 0, 0.75f, Interpolation.exp10Out));
                startGameTable.addAction(Actions.moveTo(0, 0, 0.75f, Interpolation.exp10Out));
                settingsBg.addAction(Actions.fadeIn(0.4f));
            }
        });

        quitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });


        stage.addActor(mainTable);
        stage.addActor(settingsTable);
        stage.addActor(controlsTable);
        stage.addActor(startGameTable);
        stage.addActor(mapSelectionTable);
        stage.addActor(darknessLayer);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void dispose() { stage.dispose(); background.dispose(); font.dispose(); }
    @Override public void show() {} @Override public void hide() {stage.getRoot().clear();}
    @Override public void pause() {} @Override public void resume() {}
}
