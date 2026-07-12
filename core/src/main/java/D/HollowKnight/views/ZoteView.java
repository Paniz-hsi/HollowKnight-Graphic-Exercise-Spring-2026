package D.HollowKnight.views;

import D.HollowKnight.models.Zote;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ZoteView {
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> talkAnimation;
    private TextureAtlas atlas;
    private float stateTime = 0f;

    private final float ZOTE_WIDTH = 1.0f;
    private final float ZOTE_HEIGHT = 0.60f;
    private boolean faceLeft = true;

    public ZoteView() {
        atlas = new TextureAtlas("knight_animations.atlas");

        idleAnimation = new Animation<>(0.15f, atlas.findRegions("Idle Zote"), Animation.PlayMode.LOOP);
        talkAnimation = new Animation<>(0.12f, atlas.findRegions("Talk Zote"), Animation.PlayMode.LOOP);
    }

    public void render(SpriteBatch batch, Zote zote, float delta) {
        stateTime += delta;

        TextureRegion currentFrame = zote.isTalking() ?
            talkAnimation.getKeyFrame(stateTime, true) :
            idleAnimation.getKeyFrame(stateTime, true);

        if (currentFrame.isFlipX() != faceLeft) {
            currentFrame.flip(true, false);
        }

        if (currentFrame != null) {
            batch.draw(currentFrame,
                zote.getPosition().x - (ZOTE_WIDTH / 2f),
                (zote.getPosition().y - 0.08f),
                ZOTE_WIDTH,
                ZOTE_HEIGHT);
        }
    }

    public void dispose() {
        if (atlas != null) {
            atlas.dispose();
        }
    }
}
