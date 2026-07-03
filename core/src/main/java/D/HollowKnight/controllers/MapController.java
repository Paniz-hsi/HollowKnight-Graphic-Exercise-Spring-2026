package D.HollowKnight.controllers;

import D.HollowKnight.models.DatabaseManager;
import D.HollowKnight.models.MapModel;
import D.HollowKnight.models.Player;
import D.HollowKnight.views.GameUI;
import D.HollowKnight.views.MapView;
import D.HollowKnight.views.PauseMenuView;
import D.HollowKnight.views.PlayerView;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MapController implements Screen {
    private GameController mainGame;
    private String mapPath;
    private OrthographicCamera camera;
    private Viewport viewport;
    private PlayerView playerView;

    private TmxMapLoader mapLoader;
    private TiledMap map;

    private World world;
    private Box2DDebugRenderer b2dr;

    private MapModel model;
    private MapView view;

    private SpriteBatch batch;
    private Player player;
    private MenuController menuController;
    private GameUI gameUI;
    private boolean isPaused = false;
    private PauseMenuView pauseMenuView;

    public MapController(GameController mainGame , String mapPath) {
        this.mainGame = mainGame;
        this.mapPath = mapPath;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(400 / 100f, 208 / 100f, camera);

        batch = new SpriteBatch();
        menuController = new MenuController(mainGame);

        if (Gdx.files.internal(mapPath).exists()) {
            map = new TmxMapLoader().load(mapPath);
            world = new World(new Vector2(0, -10f), true);
            world.setContactListener(new WorldContactListener());
            b2dr = new Box2DDebugRenderer();
            model = new MapModel(world, map);
            view = new MapView(map, camera);

            DatabaseManager db = menuController.getDatabase();
            int activeSlot = menuController.getCurrentSlot();

            int savedSpawnId = db.getSavedSpawnPoint(activeSlot);
            Vector2 startPos = model.getSpawnPoint(savedSpawnId);
            float startX = startPos != null ? startPos.x : 2f;
            float startY = startPos != null ? startPos.y : 1f;

            camera.position.set(startX, startY, 0);

            player = new Player(startX, startY, world);
            player.loadUnlockedSpawns(db.getSavedUnlockedSpawns(activeSlot));
            player.activateCheckpoint(savedSpawnId);
            player.currentMasks = db.getSavedMasks(activeSlot);
            player.soul = db.getSavedSoul(activeSlot);

            playerView = new PlayerView();
            FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("Trajans.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
            param.size = 30;
            BitmapFont font = gen.generateFont(param);
            gen.dispose();

            TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
            style.font = font;
            style.fontColor = Color.WHITE;
            style.overFontColor = Color.YELLOW;
            pauseMenuView = new PauseMenuView(this, mainGame, menuController, style);
            gameUI = new GameUI();
        } else {
            System.err.println("Error: File not found " + mapPath);
        }
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            if (!isPaused) {
                pauseGame();
            }
        }

        if (!isPaused) {
            if (world != null) world.step(1/60f, 6, 2);
            if (player != null) {
                player.update(delta, menuController);
                camera.position.x = player.getX();
                camera.position.y = player.getY();
            }
            camera.update();
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        view.render();

        if (player != null && playerView != null) {
            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            playerView.render(batch, player);
            batch.end();
        }

        if (gameUI != null) gameUI.render(player);
        b2dr.render(world, camera.combined);
        if (isPaused && pauseMenuView != null) {
            pauseMenuView.render(delta);
        }
    }

    @Override
    public void resize(int width, int height) {
        if (viewport != null) {
            viewport.update(width, height);
        }
        if (gameUI != null) {
            gameUI.resize(width, height);
        }
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (map != null) map.dispose();
        if (view != null) view.dispose();
        if (world != null) world.dispose();
        if (b2dr != null) b2dr.dispose();
        if (batch != null) batch.dispose();
        if (playerView != null) playerView.dispose();
        if (gameUI != null) gameUI.dispose();
        if (pauseMenuView != null) pauseMenuView.dispose();
    }

    public void pauseGame() {
        isPaused = true;
        Gdx.input.setInputProcessor(pauseMenuView.getStage());
    }

    public void resumeGame() {
        isPaused = false;
        Gdx.input.setInputProcessor(null);
    }

    public Player getPlayer() { return player; }

    public String getMapPath() { return mapPath;}
}
