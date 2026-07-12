package D.HollowKnight.views;

import D.HollowKnight.models.CrystalGuardian;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class CrystalGuardianView {
    private Animation<TextureRegion> idleAnim;
    private Animation<TextureRegion> turnAnim;
    private Animation<TextureRegion> shootAnim;
    private Animation<TextureRegion> runAnim;
    private Animation<TextureRegion> deathAirAnim;
    private Animation<TextureRegion> deathLandAnim;

    private TextureRegion laserRegion;
    private TextureAtlas atlas;

    public CrystalGuardianView() {
        atlas = new TextureAtlas("knight_animations.atlas");

        idleAnim = new Animation<>(0.15f, atlas.findRegions("Idle Guardian"), Animation.PlayMode.LOOP);
        turnAnim = new Animation<>(0.1f, atlas.findRegions("Turn Guardian"), Animation.PlayMode.NORMAL);
        shootAnim = new Animation<>(0.15f, atlas.findRegions("Shoot"), Animation.PlayMode.NORMAL);
        runAnim = new Animation<>(0.1f, atlas.findRegions("Run Guardian"), Animation.PlayMode.LOOP);
        deathAirAnim = new Animation<>(0.1f, atlas.findRegions("Death Air Guardian"), Animation.PlayMode.LOOP);
        deathLandAnim = new Animation<>(0.1f, atlas.findRegions("Death Land Guardian"), Animation.PlayMode.NORMAL);

        laserRegion = atlas.findRegion("CrystalLaser");

        if (laserRegion == null) {
            System.err.println("❌ خطا: ریجن 'CrystalLaser' در اطلس پیدا نشد! عکس لیزر توی پک نیست.");
        }
    }

    public void render(SpriteBatch batch, CrystalGuardian guardian) {
        TextureRegion currentFrame = null;

        switch (guardian.getCurrentState()) {
            case IDLE: currentFrame = idleAnim.getKeyFrame(guardian.getStateTimer(), true); break;
            case TURNING: currentFrame = turnAnim.getKeyFrame(guardian.getStateTimer(), false); break;
            case SHOOTING: currentFrame = shootAnim.getKeyFrame(guardian.getStateTimer(), false); break;
            case ENRAGED_RUN: currentFrame = runAnim.getKeyFrame(guardian.getStateTimer(), true); break;
            case DEAD_AIR: currentFrame = deathAirAnim.getKeyFrame(guardian.getStateTimer(), true); break;
            case DEAD_GROUND: currentFrame = deathLandAnim.getKeyFrame(guardian.getStateTimer(), false); break;
        }

        if (currentFrame != null) {
            boolean isFacingRight = guardian.isFacingRight();
            if (currentFrame.isFlipX() != isFacingRight) {
                currentFrame.flip(true, false);
            }

            float scaleFactor = 300f;
            float visualWidth = currentFrame.getRegionWidth() / scaleFactor;
            float visualHeight = currentFrame.getRegionHeight() / scaleFactor;

            float drawY = guardian.getPosition().y - (visualHeight / 2) - 0.1f;
            float drawX = guardian.getPosition().x - (visualWidth / 2);
            if (guardian.getCurrentState() == CrystalGuardian.State.DEAD_GROUND) {
                drawY = guardian.getPosition().y - 0.45f;
            }

            batch.draw(currentFrame, drawX, drawY, visualWidth, visualHeight);

            if (guardian.isLaserActive() && laserRegion != null) {
                float laserHeight = 0.3f;
                float laserLength = guardian.getLaserRange();
                float laserY = guardian.getPosition().y - (laserHeight / 2) + 0.1f;
                float laserX = isFacingRight ? guardian.getPosition().x : guardian.getPosition().x - laserLength;
                batch.draw(laserRegion, laserX, laserY, laserLength, laserHeight);
            }
        }
    }

    public void dispose() {
        if (atlas != null) atlas.dispose();
    }
}
