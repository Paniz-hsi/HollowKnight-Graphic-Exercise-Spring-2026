package D.HollowKnight.views;

import D.HollowKnight.models.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class PlayerView {
    private TextureAtlas atlas;

    private Animation<TextureRegion> idleAnim;
    private Animation<TextureRegion> runAnim;
    private Animation<TextureRegion> runToIdleAnim;
    private Animation<TextureRegion> airborneAnim;
    private Animation<TextureRegion> fallAnim;
    private Animation<TextureRegion> landingAnim;
    private Animation<TextureRegion> dashAnim;
    private Animation<TextureRegion> doubleJumpAnim;
    private Animation<TextureRegion> wallSideAnim;
    private Animation<TextureRegion> slashAnim;
    private Animation<TextureRegion> upSlashAnim;
    private Animation<TextureRegion> downSlashAnim;
    private Animation<TextureRegion> dashEffectAnim;
    private Animation<TextureRegion> slashEffectAnim;
    private Animation<TextureRegion> upSlashEffectAnim;
    private Animation<TextureRegion> downSlashEffectAnim;

    private TextureRegion fallbackFrame;

    public PlayerView() {
        atlas = new TextureAtlas(Gdx.files.internal("knight_animations.atlas"));

        idleAnim = createAnim("Idle", 0.1f, Animation.PlayMode.LOOP);
        runAnim = createAnim("Run", 0.08f, Animation.PlayMode.LOOP);
        runToIdleAnim = createAnim("Run To Idle", 0.1f, Animation.PlayMode.NORMAL);

        airborneAnim = createAnim("Airborne", 0.1f, Animation.PlayMode.NORMAL);
        fallAnim = createAnim("ّFall", 0.1f, Animation.PlayMode.LOOP);
        landingAnim = createAnim("Landing", 0.06f, Animation.PlayMode.NORMAL);
        dashAnim = createAnim("Dash", 0.05f, Animation.PlayMode.NORMAL);
        doubleJumpAnim = createAnim("Double Jump", 0.08f, Animation.PlayMode.NORMAL);
        wallSideAnim = createAnim("Wall Slide", 0.1f, Animation.PlayMode.LOOP);

        slashAnim = createAnim("SlashAlt", 0.06f, Animation.PlayMode.NORMAL);
        upSlashAnim = createAnim("UpSlash", 0.06f, Animation.PlayMode.NORMAL);
        downSlashAnim = createAnim("DownSlash", 0.06f, Animation.PlayMode.NORMAL);

        dashEffectAnim = createAnim("DashEffect", 0.05f, Animation.PlayMode.NORMAL);
        slashEffectAnim = createAnim("SlashEffectAlt", 0.05f, Animation.PlayMode.NORMAL);
        upSlashEffectAnim = createAnim("UpSlashEffect", 0.05f, Animation.PlayMode.NORMAL);
        downSlashEffectAnim = createAnim("DownSlashEffect", 0.05f, Animation.PlayMode.NORMAL);
        Array<TextureAtlas.AtlasRegion> runFrames = atlas.findRegions("Run");
        if (runFrames.size > 0) {
            fallbackFrame = runFrames.first();
        }
    }

    private Animation<TextureRegion> createAnim(String regionName, float frameDuration, Animation.PlayMode mode) {
        Array<TextureAtlas.AtlasRegion> frames = atlas.findRegions(regionName);
        if (frames.size > 0) {
            return new Animation<>(frameDuration, frames, mode);
        }
        return null;
    }

    public void render(SpriteBatch batch, Player player) {
        TextureRegion currentFrame = getFrame(player);
        if (currentFrame == null) return;
        boolean isMovingRight = player.isFacingRight();
        if (currentFrame.isFlipX() != isMovingRight) {
            currentFrame.flip(true, false);
        }

        float scaleFactor = 400f;
        float visualWidth = currentFrame.getRegionWidth() / scaleFactor;
        float visualHeight = currentFrame.getRegionHeight() / scaleFactor;

        float drawX = player.getX() - (visualWidth / 2);
        float drawY = player.getY() - (player.getHeight() / 2);

        batch.draw(currentFrame, drawX, drawY, visualWidth, visualHeight);

        TextureRegion effectFrame = null;
        float effectDrawX = drawX;
        float effectDrawY = drawY;

        switch (player.getCurrentState()) {
            case DASHING:
                if (dashEffectAnim != null) {
                    effectFrame = dashEffectAnim.getKeyFrame(player.getStateTimer(), false);
                    effectDrawX += isMovingRight ? -visualWidth / 1.5f : visualWidth / 1.5f;
                }
                break;

            case ATTACKING:
                if (slashEffectAnim != null) {
                    effectFrame = slashEffectAnim.getKeyFrame(player.getStateTimer(), false);
                    effectDrawX += isMovingRight ? visualWidth / 3f : -visualWidth / 3f;
                }
                break;

            case UP_ATTACKING:
                if (upSlashEffectAnim != null) {
                    effectFrame = upSlashEffectAnim.getKeyFrame(player.getStateTimer(), false);
                    effectDrawY += visualHeight / 1.5f;
                }
                break;

            case DOWN_ATTACKING:
                if (downSlashEffectAnim != null) {
                    effectFrame = downSlashEffectAnim.getKeyFrame(player.getStateTimer(), false);
                    effectDrawY -= visualHeight / 1.5f;
                }
                break;
        }
        if (effectFrame != null) {
            if (effectFrame.isFlipX() != isMovingRight) {
                effectFrame.flip(true, false);
            }
            float effectWidth = effectFrame.getRegionWidth() / scaleFactor;
            float effectHeight = effectFrame.getRegionHeight() / scaleFactor;

            float finalEffectX = effectDrawX + (visualWidth / 2) - (effectWidth / 2);
            float finalEffectY = effectDrawY + (visualHeight / 2) - (effectHeight / 2);

            batch.draw(effectFrame, finalEffectX, finalEffectY, effectWidth, effectHeight);
        }
    }

    private TextureRegion getFrame(Player player) {
        Animation<TextureRegion> selectedAnim;

        switch (player.getCurrentState()) {
            case DASHING:
                selectedAnim = dashAnim;
                break;
            case DOUBLE_JUMPING:
                selectedAnim = doubleJumpAnim;
                break;
            case AIRBORNE:
                selectedAnim = airborneAnim;
                break;
            case FALLING:
                selectedAnim = fallAnim;
                break;
            case LANDING:
                selectedAnim = landingAnim;
                break;
            case WALL_SLIDING:
                selectedAnim = wallSideAnim;
                break;
            case ATTACKING:
                selectedAnim = slashAnim;
                break;
            case UP_ATTACKING:
                selectedAnim = upSlashAnim;
                break;
            case DOWN_ATTACKING:
                selectedAnim = downSlashAnim;
                break;
            case RUNNING:
                selectedAnim = runAnim;
                break;
            case RUN_TO_IDLE:
                selectedAnim = runToIdleAnim;
                break;
            case IDLE:
            default:
                selectedAnim = idleAnim;
                break;
        }
        if (selectedAnim != null) {
            boolean isLooping = (player.getCurrentState() == Player.State.IDLE ||
                player.getCurrentState() == Player.State.RUNNING ||
                player.getCurrentState() == Player.State.FALLING ||
                player.getCurrentState() == Player.State.WALL_SLIDING);
            return selectedAnim.getKeyFrame(player.getStateTimer(), isLooping);
        }

        return fallbackFrame;
    }

    public void dispose() {
        if (atlas != null) atlas.dispose();
    }
}
