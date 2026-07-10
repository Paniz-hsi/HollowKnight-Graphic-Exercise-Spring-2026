package D.HollowKnight.views;

import D.HollowKnight.controllers.GameController;
import D.HollowKnight.models.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class EndGameScreen implements Screen {
    private GameController mainGame;
    private Stage stage;
    private BitmapFont font;

    public EndGameScreen(GameController mainGame, Player player) {
        this.mainGame = mainGame;
        stage = new Stage(new FitViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);

        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("Trajans.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 35;
        font = gen.generateFont(param);
        gen.dispose();

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = font;
        btnStyle.fontColor = Color.CYAN;
        btnStyle.overFontColor = Color.YELLOW;

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        int minutes = (int) (player.playTime / 60);
        int seconds = (int) (player.playTime % 60);
        String timeStr = String.format("%02d:%02d", minutes, seconds);

        Label titleLabel = new Label("BOSS DEFEATED!", new Label.LabelStyle(font, Color.GOLD));
        Label deathsLabel = new Label("Total Deaths: " + player.deathCount, labelStyle);
        Label killsLabel = new Label("Enemies Killed: " + player.enemiesKilled, labelStyle);
        Label timeLabel = new Label("Total Time: " + timeStr, labelStyle);

        TextButton restartBtn = new TextButton("RESTART GAME", btnStyle);
        TextButton menuBtn = new TextButton("MAIN MENU", btnStyle);

        restartBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainGame.setScreen(new D.HollowKnight.controllers.MapController(mainGame, "MAPS/CROSSROADS.tmx"));
            }
        });

        menuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainGame.setScreen(new MainMenuView(mainGame));
            }
        });

        // چیدمان در جدول
        table.add(titleLabel).padBottom(40).row();
        table.add(deathsLabel).padBottom(20).row();
        table.add(killsLabel).padBottom(20).row();
        table.add(timeLabel).padBottom(50).row();
        table.add(restartBtn).padBottom(20).row();
        table.add(menuBtn).row();

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void show() {} @Override public void hide() {} @Override public void pause() {} @Override public void resume() {}
    @Override public void dispose() { stage.dispose(); font.dispose(); }
}
