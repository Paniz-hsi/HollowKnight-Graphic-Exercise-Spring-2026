package D.HollowKnight.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;

public class AudioManager {
    private static AudioManager instance;
    private Music currentMusic;
    private boolean isFadingOut = false;
    private float fadeTimer = 0f;
    private float fadeDuration = 1.5f;
    private float targetVolume = 1.0f;
    private String nextMusicPath = null;
    private String currentMusicPath = "";
    private HashMap<String, Sound> soundCache = new HashMap<>();

    private AudioManager() {}

    public static AudioManager getInstance() {
        if (instance == null) instance = new AudioManager();
        return instance;
    }


    public void update(float delta) {
        if (isFadingOut && currentMusic != null) {
            fadeTimer += delta;
            float progress = fadeTimer / fadeDuration;

            if (progress >= 1.0f) {
                isFadingOut = false;
                currentMusic.stop();
                currentMusic.dispose();

                if (nextMusicPath != null) {
                    playMusic(nextMusicPath, targetVolume);
                    nextMusicPath = null;
                }
            } else {
                float currentVol = targetVolume * (1f - progress);
                currentMusic.setVolume(currentVol);
            }
        }
    }

    public void setVolume(float volume) {
        this.targetVolume = volume;
        if (currentMusic != null && !isFadingOut) {
            currentMusic.setVolume(volume);
        }
    }

    public void stopMusic() {
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.stop();
        }
    }
    public void playMusic(String path, float volume) {
        if (path.equals(currentMusicPath) && currentMusic != null && currentMusic.isPlaying()) {
            this.targetVolume = volume;
            currentMusic.setVolume(volume);
            return;
        }

        this.targetVolume = volume;
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.dispose();
        }

        currentMusic = Gdx.audio.newMusic(Gdx.files.internal(path));
        currentMusic.setLooping(true);
        currentMusic.setVolume(targetVolume);
        currentMusic.play();

        currentMusicPath = path;
    }

    public void fadeToNewMusic(String nextPath) {
        if (nextPath != null && nextPath.equals(currentMusicPath)) {
            return;
        }

        this.nextMusicPath = nextPath;
        this.isFadingOut = true;
        this.fadeTimer = 0f;
    }

    public void playSound(String path) {
        if (!soundCache.containsKey(path)) {
            try {
                Sound sound = Gdx.audio.newSound(Gdx.files.internal(path));
                soundCache.put(path, sound);
            } catch (Exception e) {
                System.out.println("Error loading sound: " + path);
                return;
            }
        }

        soundCache.get(path).play(targetVolume);
    }
}
