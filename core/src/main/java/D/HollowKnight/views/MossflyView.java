package D.HollowKnight.views;

import D.HollowKnight.models.Mossfly;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MossflyView {
    private Animation<TextureRegion> shakeAnim;
    private Animation<TextureRegion> appearAnim;
    private Animation<TextureRegion> flyAnim;
    private Animation<TextureRegion> turnAnim;
    private Animation<TextureRegion> deathAirAnim;
    private Animation<TextureRegion> deathLandAnim;

    private Texture shakeTex, appearTex, flyTex, turnTex, deathAirTex, deathLandTex;

    public MossflyView() {
        shakeTex = new Texture("Shake.png");
        appearTex = new Texture("Appear.png");
        flyTex = new Texture("Fly.png");
        turnTex = new Texture("TurnToFly.png");
        deathAirTex = new Texture("Death Air Moss.png");
        deathLandTex = new Texture("Death Land Moss.png");

        shakeAnim = new Animation<>(0.15f, TextureRegion.split(shakeTex, shakeTex.getWidth() / 3, shakeTex.getHeight())[0]);
        shakeAnim.setPlayMode(Animation.PlayMode.LOOP);

        appearAnim = new Animation<>(0.1f, TextureRegion.split(appearTex, appearTex.getWidth() / 5, appearTex.getHeight())[0]);
        appearAnim.setPlayMode(Animation.PlayMode.NORMAL);

        flyAnim = new Animation<>(0.08f, TextureRegion.split(flyTex, flyTex.getWidth() / 4, flyTex.getHeight())[0]);
        flyAnim.setPlayMode(Animation.PlayMode.LOOP);

        turnAnim = new Animation<>(0.1f, TextureRegion.split(turnTex, turnTex.getWidth() / 3, turnTex.getHeight())[0]);
        turnAnim.setPlayMode(Animation.PlayMode.NORMAL);

        deathAirAnim = new Animation<>(0.1f, TextureRegion.split(deathAirTex, deathAirTex.getWidth() / 4, deathAirTex.getHeight())[0]);
        deathAirAnim.setPlayMode(Animation.PlayMode.LOOP);

        deathLandAnim = new Animation<>(0.1f, TextureRegion.split(deathLandTex, deathLandTex.getWidth() / 2, deathLandTex.getHeight())[0]);
        deathLandAnim.setPlayMode(Animation.PlayMode.NORMAL);
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
        shakeTex.dispose(); appearTex.dispose(); flyTex.dispose();
        turnTex.dispose(); deathAirTex.dispose(); deathLandTex.dispose();
    }
}
