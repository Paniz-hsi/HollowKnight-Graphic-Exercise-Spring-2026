package D.HollowKnight.views;

import D.HollowKnight.models.Crawlid;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class CrawlidView {
    private Animation<TextureRegion> walkAnim;
    private Animation<TextureRegion> turnAnim;
    private Animation<TextureRegion> deathAirAnim;
    private Animation<TextureRegion> deathLandAnim;

    private Texture walkTex, turnTex, deathAirTex, deathLandTex;

    public CrawlidView() {
        walkTex = new Texture("Walk.png");
        turnTex = new Texture("Turn.png");
        deathAirTex = new Texture("Death Air.png");
        deathLandTex = new Texture("Death Land.png");

        walkAnim = new Animation<>(0.1f, TextureRegion.split(walkTex, walkTex.getWidth() / 4, walkTex.getHeight())[0]);
        walkAnim.setPlayMode(Animation.PlayMode.LOOP);

        turnAnim = new Animation<>(0.15f, TextureRegion.split(turnTex, turnTex.getWidth() / 2, turnTex.getHeight())[0]);
        deathAirAnim = new Animation<>(0.1f, TextureRegion.split(deathAirTex, deathAirTex.getWidth() / 3, deathAirTex.getHeight())[0]);
        deathLandAnim = new Animation<>(0.1f, TextureRegion.split(deathLandTex, deathLandTex.getWidth() / 2, deathLandTex.getHeight())[0]);
    }

    public void render(SpriteBatch batch, Crawlid crawlid) {
        TextureRegion currentFrame = null;

        switch (crawlid.getCurrentState()) {
            case WALKING: currentFrame = walkAnim.getKeyFrame(crawlid.getStateTimer(), true); break;
            case TURNING: currentFrame = turnAnim.getKeyFrame(crawlid.getStateTimer(), false); break;
            case DEAD_AIR: currentFrame = deathAirAnim.getKeyFrame(crawlid.getStateTimer(), true); break;
            case DEAD_GROUND: currentFrame = deathLandAnim.getKeyFrame(crawlid.getStateTimer(), false); break;
        }

        if (currentFrame != null) {
            if (currentFrame.isFlipX() != crawlid.isMovingRight()) {
                currentFrame.flip(true, false);
            }

            float scaleFactor = 400f;
            float visualWidth = currentFrame.getRegionWidth() / scaleFactor;
            float visualHeight = currentFrame.getRegionHeight() / scaleFactor;

            float drawX = crawlid.getPosition().x - (visualWidth / 2);
            float drawY = crawlid.getPosition().y - 0.15f - 0.1f;
            batch.draw(currentFrame, drawX, drawY, visualWidth, visualHeight);
        }
    }

    public void dispose() {
        walkTex.dispose(); turnTex.dispose(); deathAirTex.dispose(); deathLandTex.dispose();
    }
}
