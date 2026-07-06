package D.HollowKnight.views;

import D.HollowKnight.models.HuskHornhead;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class HuskHornheadView {
    private Animation<TextureRegion> idleAnim;
    private Animation<TextureRegion> walkAnim;
    private Animation<TextureRegion> turnAnim;
    private Animation<TextureRegion> anticipateAnim;
    private Animation<TextureRegion> lungeAnim;
    private Animation<TextureRegion> deathAirAnim;
    private Animation<TextureRegion> deathLandAnim;

    private TextureRegion cooldownFrame;
    private TextureAtlas atlas;

    public HuskHornheadView() {
        atlas = new TextureAtlas("knight_animations.atlas");

        idleAnim = new Animation<>(0.15f, atlas.findRegions("Idle Husk"), Animation.PlayMode.LOOP);
        walkAnim = new Animation<>(0.1f, atlas.findRegions("Walk Husk"), Animation.PlayMode.LOOP);
        turnAnim = new Animation<>(0.15f, atlas.findRegions("Turn Husk"), Animation.PlayMode.NORMAL);
        anticipateAnim = new Animation<>(0.15f, atlas.findRegions("Attack Anticipate"), Animation.PlayMode.NORMAL);
        lungeAnim = new Animation<>(0.08f, atlas.findRegions("Attack Lunge"), Animation.PlayMode.LOOP);

        cooldownFrame = atlas.findRegion("Attack Cooldown Husk");

        deathAirAnim = new Animation<>(0.1f, atlas.findRegions("Death Air Husk"), Animation.PlayMode.LOOP);
        deathLandAnim = new Animation<>(0.1f, atlas.findRegions("Death Land"), Animation.PlayMode.NORMAL);
    }

    public void render(SpriteBatch batch, HuskHornhead husk) {
        TextureRegion currentFrame = null;

        switch (husk.getCurrentState()) {
            case IDLE: currentFrame = idleAnim.getKeyFrame(husk.getStateTimer(), true); break;
            case WALKING: currentFrame = walkAnim.getKeyFrame(husk.getStateTimer(), true); break;
            case TURNING: currentFrame = turnAnim.getKeyFrame(husk.getStateTimer(), false); break;
            case ATTACK_ANTICIPATE: currentFrame = anticipateAnim.getKeyFrame(husk.getStateTimer(), false); break;
            case ATTACK_LUNGE: currentFrame = lungeAnim.getKeyFrame(husk.getStateTimer(), true); break;
            case ATTACK_COOLDOWN: currentFrame = cooldownFrame; break;
            case DEAD_AIR: currentFrame = deathAirAnim.getKeyFrame(husk.getStateTimer(), true); break;
            case DEAD_GROUND: currentFrame = deathLandAnim.getKeyFrame(husk.getStateTimer(), false); break;
        }

        if (currentFrame != null) {
            boolean isMovingRight = husk.isFacingRight();
            if (currentFrame.isFlipX() != isMovingRight) {
                currentFrame.flip(true, false);
            }

            float scaleFactor = 350f;
            float visualWidth = currentFrame.getRegionWidth() / scaleFactor;
            float visualHeight = currentFrame.getRegionHeight() / scaleFactor;

            float drawX = husk.getPosition().x - (visualWidth / 2);
            float drawY = husk.getPosition().y - (visualHeight / 2) + 0.1f;

            if (husk.getCurrentState() == HuskHornhead.State.DEAD_GROUND) {
                drawY = husk.getPosition().y - 0.25f;
            }

            batch.draw(currentFrame, drawX, drawY, visualWidth, visualHeight);
        }
    }

    public void dispose() {
        if (atlas != null) {
            atlas.dispose();
        }
    }
}
