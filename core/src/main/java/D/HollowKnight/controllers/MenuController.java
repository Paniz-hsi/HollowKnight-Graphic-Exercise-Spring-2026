package D.HollowKnight.controllers;

import D.HollowKnight.models.AudioManager;
import D.HollowKnight.models.DatabaseManager;
import D.HollowKnight.models.SaveSlotData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;


public class MenuController {
    private GameController mainGame;
    private Music bgMusic;
    private DatabaseManager db;

    public MenuController(GameController mainGame) {
        this.mainGame = mainGame;
        db = new DatabaseManager();
        applyMusicSettings();
    }

    private void applyMusicSettings() {
        float libgdxVolume = db.getVolume() / 100f;
        if (db.isMusicOn()) {
            AudioManager.getInstance().playMusic("bgmusic.mp3", libgdxVolume);
        }
    }
    public int getVolume() { return db.getVolume(); }
    public void setVolume(int volume) {
        db.updateVolume(volume);
        AudioManager.getInstance().setVolume(volume / 100f);
    }

    public boolean isMusicOn() { return db.isMusicOn(); }
    public void setMusicOn(boolean isMusicOn) {
        db.updateMusicOn(isMusicOn);
        if (isMusicOn) {
            float libgdxVolume = db.getVolume() / 100f;
            AudioManager.getInstance().playMusic("bgmusic.mp3", libgdxVolume);
        } else {
            AudioManager.getInstance().stopMusic();
        }
    }

    public boolean isSfxOn() { return db.isSfxOn(); }
    public void setSfxOn(boolean isSfxOn) { db.updateSfxOn(isSfxOn); }

    public int getBrightness() { return db.getBrightness(); }
    public void setBrightness(int brightness) {
        db.updateBrightness(brightness);
    }

    public String getLanguage() { return db.getLanguage(); }
    public void setLanguage(String language) { db.updateLanguage(language); }

    public void resetSounds() {
        setVolume(100);
        setMusicOn(true);
        setSfxOn(true);
    }

    public boolean hasSave(int slot) {
        return db.getSaveSlot(slot).isHasSave();
    }

    public String getMapName(int slot) {
        return db.getSaveSlot(slot).getMapName();
    }

    public int getProgress(int slot) {
        return db.getSaveSlot(slot).getProgress();
    }

    public void loadGame(int slot) {
        SaveSlotData data = db.getSaveSlot(slot);
        Gdx.app.postRunnable(() -> mainGame.setScreen(new MapController(mainGame, data.getMapName())));
    }

    public void startNewGame(final String path) {
        String newMusicPath = getMusicPathForMap(path);
        if (db.isMusicOn()) {
            AudioManager.getInstance().fadeToNewMusic(newMusicPath);
        }
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    MapController mapController = new MapController(mainGame, path);

                    mainGame.setScreen(mapController);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String getMusicPathForMap(String mapPath) {
        if (mapPath.contains("greenpath")) return "Greenpath.mp3";
        if (mapPath.contains("crossroads")) return "crossroads_music.mp3";
        return "default_map_music.mp3";
    }

    public int getKeyUp() {
        return db.getKeyUp();
    }

    public void setKeyUp(int keycode) {
        db.updateKeyUp(keycode);
    }

    public int getKeyDown() {
        return db.getKeyDown();
    }

    public void setKeyDown(int keycode) {
        db.updateKeyDown(keycode);
    }

    public int getKeyRight() {
        return db.getKeyRight();
    }

    public void setKeyRight(int keycode) {
        db.updateKeyRight(keycode);
    }

    public int getKeyLeft() {
        return db.getKeyLeft();
    }

    public void setKeyLeft(int keycode) {
        db.updateKeyLeft(keycode);
    }

    public void resetControls() {
        db.updateKeyDown(Input.Keys.DOWN);
        db.updateKeyLeft(Input.Keys.LEFT);
        db.updateKeyUp(Input.Keys.UP);
        db.updateKeyRight(Input.Keys.RIGHT);
        db.updateKeyDash(Input.Keys.C);
        db.updateKeyAttack(Input.Keys.X);
        db.updateKeyJump(Input.Keys.Z);
    }

    public int getKeyDash() {
        return db.getKeyDash();
    }

    public void setKeyDash(int keycode) {
        db.updateKeyDash(keycode);
    }

    public int getKeyAttack() {
        return db.getKeyAttack();
    }

    public void setKeyAttack(int keycode) {
        db.updateKeyAttack(keycode);
    }

    public int getKeyJump() {
        return db.getKeyJump();
    }

    public void setKeyJump(int keycode) {
        db.updateKeyJump(keycode);
    }
}
