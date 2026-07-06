package D.HollowKnight.views;

import D.HollowKnight.models.Mossfly;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class MossflyView {
    private Animation<TextureRegion> shakeAnim;
    private Animation<TextureRegion> appearAnim;
    private Animation<TextureRegion> flyAnim;
    private Animation<TextureRegion> turnAnim;
    private Animation<TextureRegion> deathAirAnim;
    private Animation<TextureRegion> deathLandAnim;

    private TextureAtlas atlas;

    public MossflyView() {
        atlas = new TextureAtlas("knight_animations.atlas");

        shakeAnim = new Animation<>(0.15f, atlas.findRegions("Shake"), Animation.PlayMode.LOOP);
        appearAnim = new Animation<>(0.1f, atlas.findRegions("Appear"), Animation.PlayMode.NORMAL);
        flyAnim = new Animation<>(0.08f, atlas.findRegions("Fly"), Animation.PlayMode.LOOP);
        turnAnim = new Animation<>(0.1f, atlas.findRegions("TurnToFly"), Animation.PlayMode.NORMAL);
        deathAirAnim = new Animation<>(0.1f, atlas.findRegions("Death Air"), Animation.PlayMode.LOOP);
        deathLandAnim = new Animation<>(0.1f, atlas.findRegions("Death Land Moss"), Animation.PlayMode.NORMAL);
    }

    public void render(SpriteBatch batch, Mossfly mossfly) {
        TextureRegion currentFrame = null;

        switch (mossfly.getCurrentState()) {
            case HIDDEN:
                currentFrame = shakeAnim.getKeyFrame(mossfly.getStateTimer(), true);
                break;
            case APPEARING:
                currentFrame = appearAnim.getKeyFrame(mossfly.getStateTimer(), false);
                break;
            case CHASING:
            case RETURNING:
                currentFrame = flyAnim.getKeyFrame(mossfly.getStateTimer(), true);
                break;
            case TURNING:
                currentFrame = turnAnim.getKeyFrame(mossfly.getStateTimer(), false);
                break;
            case DEAD_AIR:
                currentFrame = deathAirAnim.getKeyFrame(mossfly.getStateTimer(), true);
                break;
            case DEAD_GROUND:
                currentFrame = deathLandAnim.getKeyFrame(mossfly.getStateTimer(), false);
                break;
        }

        if (currentFrame != null) {
            boolean isMovingRight = mossfly.isFacingRight();

            if ((mossfly.getCurrentState() == Mossfly.State.CHASING ||
                mossfly.getCurrentState() == Mossfly.State.RETURNING ||
                mossfly.getCurrentState() == Mossfly.State.DEAD_AIR)
                && currentFrame.isFlipX() != isMovingRight) {
                currentFrame.flip(true, false);
            }

            float scaleFactor = 550f;
            float visualWidth = currentFrame.getRegionWidth() / scaleFactor;
            float visualHeight = currentFrame.getRegionHeight() / scaleFactor;

            float drawX = mossfly.getPosition().x - (visualWidth / 2);
            float drawY = mossfly.getPosition().y - (visualHeight / 2) + 0.1f;

            if (mossfly.getCurrentState() == Mossfly.State.DEAD_GROUND) {
                drawY = mossfly.getPosition().y - 0.2f;
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
