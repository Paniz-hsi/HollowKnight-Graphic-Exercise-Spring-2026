package D.HollowKnight.controllers;

import D.HollowKnight.models.*;
import D.HollowKnight.views.*;
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
import com.badlogic.gdx.physics.box2d.Fixture;
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
    private BeeParticleSystem beeParticles;
    private boolean isTransitioning = false;
    private float shakeTimer = 0;
    private float shakeIntensity = 0;
    private float defaultCameraX, defaultCameraY;

    private TmxMapLoader mapLoader;
    private TiledMap map;

    private World world;
    private Box2DDebugRenderer b2dr;

    private MapModel model;
    private MapView view;

    private SpriteBatch batch;
    private Player player;
    private Crawlid crawlid;
    private CrawlidView crawlidView;
    private MossflyView mossflyView;
    private Mossfly mossfly;
    private HuskHornhead husk;
    private HuskHornheadView huskView;
    private CrystalGuardian crystalGuardian;
    private CrystalGuardianView crystalGuardianView;
    private Zote zote;
    private ZoteView zoteView;
    private FalseKnight falseKnight;
    private FalseKnightView falseKnightView;
    private MenuController menuController;
    private GameUI gameUI;
    private boolean isPaused = false;
    private PauseMenuView pauseMenuView;
    private boolean isInventoryOpen = false;
    private InventoryView inventoryView;

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
        if (menuController.isMusicOn()) {
            String mapMusic = menuController.getMusicPathForMap(mapPath);
            AudioManager.getInstance().playMusic(mapMusic, menuController.getVolume() / 100f);
        }

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

            player = new Player(startX, startY, world, this);
            crawlidView = new CrawlidView();
            Vector2 enemyPos = model.getCrawlidSpawn();
            if (enemyPos != null) {
                crawlid = new Crawlid(world, enemyPos.x, enemyPos.y);
            }

            mossflyView = new MossflyView();
            enemyPos = model.getMossflySpawn();
            if (enemyPos != null) {
                mossfly = new Mossfly(world, enemyPos.x, enemyPos.y);
            }

            huskView = new HuskHornheadView();
            enemyPos = model.getHuskSpawn();
            if (enemyPos != null) {
                husk = new HuskHornhead(world, enemyPos.x, enemyPos.y);
            }

            crystalGuardianView = new CrystalGuardianView();
            enemyPos = model.getCrystallizedSpawn();
            if (enemyPos != null) {
                crystalGuardian = new CrystalGuardian(world, enemyPos.x, enemyPos.y);
            }
            zoteView = new ZoteView();
            Vector2 pos = model.getZoteSpawn();
            if (pos != null) {
                zote = new Zote(pos.x, pos.y);
            }
            Vector2 bossPos = model.getFalseKnightSpawn();
            if (bossPos != null) {
                falseKnight = new FalseKnight(world, bossPos.x, bossPos.y);
                falseKnightView = new FalseKnightView();
            }

            player.loadUnlockedSpawns(db.getSavedUnlockedSpawns(activeSlot));
            player.activateCheckpoint(savedSpawnId);
            player.currentMasks = db.getSavedMasks(activeSlot);
            player.soul = db.getSavedSoul(activeSlot);
            inventoryView = new InventoryView(player);
            playerView = new PlayerView();
            FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("Trajans.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
            param.size = 30;
            BitmapFont font = gen.generateFont(param);
            gen.dispose();
            beeParticles = new BeeParticleSystem(8, camera.position.x, camera.position.y);
            TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
            style.font = font;
            style.fontColor = Color.WHITE;
            style.overFontColor = Color.YELLOW;
            pauseMenuView = new PauseMenuView(this, mainGame, menuController, style);
            gameUI = new GameUI(mainGame , menuController , style);
            gameUI.setZote(zote);
        } else {
            System.err.println("Error: File not found " + mapPath);
        }
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.I) && !isPaused) {
            isInventoryOpen = !isInventoryOpen;
        }

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            if (isInventoryOpen) {
                isInventoryOpen = false;
            } else if (!isPaused) {
                pauseGame();
            }
        }

        if (isInventoryOpen) {
            inventoryView.handleInput();
        }
        if (falseKnight != null && falseKnight.currentState == FalseKnight.State.DEATH) {
            if (!isTransitioning) {
                isTransitioning = true;
                triggerEndGame();
            }
        }
        if (!isPaused && !isInventoryOpen) {
            if (world != null) world.step(1/60f, 6, 2);
            if (player != null && player.needsRespawn()) {
                Vector2 spawnPos = model.getSpawnPoint(player.getCurrentSpawnPointId());
                if (spawnPos != null) {
                    player.respawnAt(spawnPos);
                    if (menuController.isMusicOn()) {
                        String mapMusic = menuController.getMusicPathForMap(mapPath);
                        AudioManager.getInstance().playMusic(mapMusic, menuController.getVolume() / 100f);
                    }
                }
                player.setNeedsRespawn(false);

                if (model.getBossDoors() != null) {
                    for (BossDoor bossDoor : model.getBossDoors()) {
                        bossDoor.isClosed = false;
                        for (com.badlogic.gdx.physics.box2d.Fixture fixture : bossDoor.body.getFixtureList()) {
                            fixture.setSensor(true);
                        }
                    }
                }

                if (falseKnight != null) {
                    falseKnight.hp = 12; // جون باس پر میشه
                    falseKnight.isPhase2 = false;
                    falseKnight.currentState = FalseKnight.State.IDLE;
                    falseKnight.isMaceActive = false;
                    falseKnight.updateMaceHitbox();
                }
            }
            if (player != null) {
                player.playTime += delta;
                player.update(delta, menuController);
                camera.position.x = player.getX();
                camera.position.y = player.getY();
                if (model.getBossDoors() != null) {
                    for (BossDoor bossDoor : model.getBossDoors()) {
                        if (!bossDoor.isClosed && player != null) {

                            if (player.getCurrentSpawnPointId() == 5) {

                                bossDoor.isClosed = true;
                                if (menuController.isMusicOn()) {
                                    String mapMusic = "footbalistha.mp3";
                                    AudioManager.getInstance().playMusic(mapMusic, menuController.getVolume() / 100f);
                                }
                                for (Fixture fixture : bossDoor.body.getFixtureList()) {
                                    fixture.setSensor(false);
                                }

                            }
                        }
                    }
                }
            }
            if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.CONTROL_LEFT)) {

                if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.T)) {
                    Vector2 bossPos = model.getFalseKnightSpawn();
                    if (bossPos != null) player.respawnAt(bossPos);
                    System.out.println("Cheat Activated: Teleported to Boss Arena");
                }

                if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.N)) {
                    player.toggleNoclip();
                    System.out.println("Cheat Activated: Noclip Mode -> " + player.isNoclip);
                }

                if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.H)) {
                    player.cheatHeal();
                    System.out.println("Cheat Activated: Emergency Heal Received");
                }

                if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.M)) {
                    player.cheatFillSoul();
                    System.out.println("Cheat Activated: Soul Vessel Refilled");
                }

                if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.G)) {
                    player.isGodMode = !player.isGodMode;
                    System.out.println("Cheat Activated: God Mode -> " + player.isGodMode);
                }

                if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.K)) {
                    if (crawlid != null) crawlid.takeDamage();
                    if (mossfly != null) mossfly.takeDamage();
                    if (husk != null) husk.takeDamage();
                    if (crystalGuardian != null) crystalGuardian.takeDamage();

                    try {
                        if (falseKnight != null && falseKnight.hp > 0) {
                            falseKnight.hp = 0;
                            falseKnight.takeDamage(true);
                        }
                    } catch (Exception ignored) {}

                    System.out.println("Cheat Activated: Insta-Kill! All enemies wiped out.");
                }
            }
            if (crawlid != null) {
                crawlid.update(delta);
            }
            if (mossfly != null) {
                mossfly.update(delta, player);
            }
            if (husk != null) {
                husk.update(delta, player);
            }
            if(crystalGuardian != null){
                crystalGuardian.update(delta , player);
            }
            if(zote != null){
                zote.update(delta , player);
            }
            if (falseKnight != null) {
               falseKnight.update(delta , player , this);
            }
            if (player != null && player.getPendingMapTransition() != null && !isTransitioning) {
                String nextMap = player.getPendingMapTransition();
                player.clearPendingMapTransition();
                openDoorAndTransition(nextMap);
            }
            defaultCameraX = player.getX();
            defaultCameraY = player.getY();

            defaultCameraX = player.getX();
            defaultCameraY = player.getY();

            if (shakeTimer > 0) {
                float offsetX = (float) (Math.random() - 0.5f) * shakeIntensity;
                float offsetY = (float) (Math.random() - 0.5f) * shakeIntensity;
                camera.position.set(defaultCameraX + offsetX, defaultCameraY + offsetY, 0);

                shakeTimer -= delta;
            } else {
                camera.position.set(defaultCameraX, defaultCameraY, 0);
            }

            camera.update();
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        view.render();

        if (player != null && playerView != null) {
            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            if (beeParticles != null) {
                beeParticles.render(batch, delta, camera);
            }
            if (crawlid != null && crawlidView != null) {
                crawlidView.render(batch, crawlid);
            }
            if (mossfly != null && mossflyView != null) {
                mossflyView.render(batch, mossfly);
            }
            if (husk != null && huskView != null) {
                huskView.render(batch, husk);
            }
            if (crystalGuardian != null && crystalGuardianView != null) {
                crystalGuardianView.render(batch, crystalGuardian);
            }
            if (zote != null && zoteView != null) {
                zoteView.render(batch, zote, delta);
            }
            if (falseKnight != null && falseKnightView != null) {
                falseKnightView.render(batch, falseKnight, falseKnight.stateTimer);
            }
            playerView.render(batch, player);
            batch.end();
        }

        if (gameUI != null) gameUI.render(player);
        b2dr.render(world, camera.combined);
        if (isPaused && pauseMenuView != null) {
            pauseMenuView.render(delta);
        } else if (isInventoryOpen && inventoryView != null) { // <--- این خطوط جا مانده بود!
            batch.begin();
            inventoryView.render(batch);
            batch.end();
        }
    }

    public void shakeCamera(float intensity, float duration) {
        this.shakeIntensity = intensity;
        this.shakeTimer = duration;
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
        if (crawlidView != null) crawlidView.dispose();
        if (zoteView != null) zoteView.dispose();
        if (beeParticles != null) beeParticles.dispose();
        if (falseKnightView != null) falseKnightView.dispose();
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

    private void openDoorAndTransition(String targetMapPath) {
        if (targetMapPath == null || targetMapPath.isEmpty()) {
            System.err.println("Error: Target map is empty or not defined!");
            return;
        }

        isTransitioning = true;
        DatabaseManager db = menuController.getDatabase();
        int activeSlot = menuController.getCurrentSlot();

        StringBuilder charmsBuilder = new StringBuilder();
        int count = 0;
        for (Charm charm : player.equippedCharms) {
            charmsBuilder.append(charm.name());
            count++;
            if (count < player.equippedCharms.size()) {
                charmsBuilder.append(",");
            }
        }
        String charmsToSave = charmsBuilder.toString();

        db.saveGameState(
            activeSlot,
            targetMapPath,
            1,
            db.getSaveProgress(activeSlot),
            player.getUnlockedSpawnsString(),
            player.currentMasks,
            player.maxMasks,
            player.soul,
            charmsToSave
        );
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                mainGame.setScreen(new MapController(mainGame, targetMapPath));
                dispose();
            }
        });
    }
    private void triggerEndGame() {
        DatabaseManager db = menuController.getDatabase();

        db.unlockAchievement("Completion");

        db.unlockAchievement("Defeat False Knight");

        if (player.currentMasks >= 3) {
            db.unlockAchievement("Resilient Knight");
        }

        if (player.playTime < 1800f) {
            db.unlockAchievement("Speedrun");
        }

        if (player.killedEnemyTypes.size() >= 4) {
            db.unlockAchievement("True Hunter");
        }

        Gdx.app.postRunnable(() -> {
            mainGame.setScreen(new EndGameScreen(mainGame, player));
            dispose();
        });
    }
}
