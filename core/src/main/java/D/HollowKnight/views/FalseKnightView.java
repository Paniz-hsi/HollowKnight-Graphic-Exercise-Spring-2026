package D.HollowKnight.views;

import D.HollowKnight.models.FalseKnight;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import static D.HollowKnight.models.MapModel.PPM;

public class FalseKnightView {
    private TextureAtlas atlas;

    private Animation<TextureRegion> idleAnim;
    private Animation<TextureRegion> runAnim;
    private Animation<TextureRegion> jumpAnim;
    private Animation<TextureRegion> jumpAttackAnim;
    private Animation<TextureRegion> attackAnticAnim;
    private Animation<TextureRegion> attackAnim;
    private Animation<TextureRegion> attackRecoverAnim;
    private Animation<TextureRegion> deathFallAnim;
    private Animation<TextureRegion> deathLandAnim;
    private Animation<TextureRegion> stunRecoverAnim;
    private Animation<TextureRegion> deathHitAnim;
    private Animation<TextureRegion> turnAnim;

    private final float SCALE = 1 / PPM;
    private final float OFFSET_X = 1.5f;
    private final float OFFSET_Y = 0.75f;

    public FalseKnightView() {
        atlas = new TextureAtlas("knight_animations.atlas");

        idleAnim = new Animation<>(0.1f, atlas.findRegions("Idle False"), Animation.PlayMode.LOOP);
        runAnim = new Animation<>(0.08f, atlas.findRegions("Run False"), Animation.PlayMode.LOOP);
        jumpAnim = new Animation<>(0.1f, atlas.findRegions("Jump False"), Animation.PlayMode.NORMAL);
        turnAnim = new Animation<>(0.1f, atlas.findRegions("Turn False"), Animation.PlayMode.NORMAL);

        attackAnticAnim = new Animation<>(0.1f, atlas.findRegions("Attack Antic False"), Animation.PlayMode.NORMAL);
        attackAnim = new Animation<>(0.08f, atlas.findRegions("Attack False"), Animation.PlayMode.NORMAL);
        attackRecoverAnim = new Animation<>(0.1f, atlas.findRegions("Attack Recover False"), Animation.PlayMode.NORMAL);
        jumpAttackAnim = new Animation<>(0.08f, atlas.findRegions("Jump Attack False"), Animation.PlayMode.NORMAL);
        deathFallAnim = new Animation<>(0.1f, atlas.findRegions("DeathFall"), Animation.PlayMode.NORMAL);
        deathLandAnim = new Animation<>(0.1f, atlas.findRegions("DeathLand"), Animation.PlayMode.NORMAL);
        stunRecoverAnim = new Animation<>(0.1f, atlas.findRegions("Stun Recover"), Animation.PlayMode.NORMAL);
        deathHitAnim = new Animation<>(0.1f, atlas.findRegions("DeathHit"), Animation.PlayMode.NORMAL);
    }

    public void render(SpriteBatch batch, FalseKnight knight, float stateTimer) {
        TextureRegion currentFrame = getFrame(knight, stateTimer);

        if (currentFrame != null) {
            boolean facingRight = knight.isFacingRight;
            if (facingRight && !currentFrame.isFlipX()) {
                currentFrame.flip(true, false);
            } else if (!facingRight && currentFrame.isFlipX()) {
                currentFrame.flip(true, false);
            }

            float drawX = knight.body.getPosition().x - OFFSET_X;
            float drawY = knight.body.getPosition().y - OFFSET_Y;

            float width = currentFrame.getRegionWidth() * SCALE;
            float height = currentFrame.getRegionHeight() * SCALE;

            batch.draw(currentFrame, drawX, drawY, width, height);
        }
    }

    private TextureRegion getFrame(FalseKnight knight, float stateTimer) {
        switch (knight.currentState) {
            case TURN:
                return turnAnim.getKeyFrame(stateTimer);
            case IDLE:
                return idleAnim.getKeyFrame(stateTimer);
            case CHARGE_RUN:
                return runAnim.getKeyFrame(stateTimer);
            case OFFENSIVE_LEAP:
            case DEFENSIVE_LEAP:
                return jumpAnim.getKeyFrame(stateTimer);
            case MACE_SLAM_ANTIC:
                return attackAnticAnim.getKeyFrame(stateTimer);
            case MACE_SLAM_ATTACK:
                return attackAnim.getKeyFrame(stateTimer);
            case MACE_SLAM_RECOVER:
                return attackRecoverAnim.getKeyFrame(stateTimer);
            case MACE_FRENZY:
                return jumpAttackAnim.getKeyFrame(stateTimer, true);
            case STUN_FALL:
                return deathFallAnim.getKeyFrame(stateTimer);
            case STUNNED:
                return deathLandAnim.getKeyFrame(stateTimer);
            case DEATH:
                return deathHitAnim.getKeyFrame(stateTimer);
            case WAKING_UP:
                return stunRecoverAnim.getKeyFrame(stateTimer);
            default:
                return idleAnim.getKeyFrame(stateTimer);
        }
    }

    public void dispose() {
        atlas.dispose();
    }
}
