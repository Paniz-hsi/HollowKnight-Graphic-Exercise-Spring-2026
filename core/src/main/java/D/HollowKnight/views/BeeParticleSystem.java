package D.HollowKnight.views;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.OrthographicCamera;

import java.util.ArrayList;

public class BeeParticleSystem {
    private Animation<TextureRegion> beeAnim;
    private Texture beeTex;
    private ArrayList<Bee> bees;

    private class Bee {
        float x, y;
        float stateTimer;
        float speedX, speedY;
        float scale;
        float hoverOffset;

        public Bee(float camX, float camY, float viewWidth, float viewHeight) {
            // اسپاون رندوم زنبورها در محدوده دوربین
            x = camX + MathUtils.random(-viewWidth, viewWidth);
            y = camY + MathUtils.random(-viewHeight, viewHeight);

            stateTimer = MathUtils.random(0f, 5f);

            speedX = MathUtils.random(-0.5f, 0.5f);
            speedY = MathUtils.random(-0.1f, 0.1f);

            scale = MathUtils.random(0.5f, 1.2f);
            hoverOffset = MathUtils.random(0f, 10f);
        }

        public void update(float delta) {
            stateTimer += delta;
            x += speedX * delta;

            y += (speedY + MathUtils.sin(stateTimer * 3f + hoverOffset) * 0.1f) * delta;
        }
    }

    public BeeParticleSystem(int count, float startX, float startY) {
        beeTex = new Texture("bee_particle_anim.png");
        TextureRegion[][] tmp = TextureRegion.split(beeTex, beeTex.getWidth() / 5, beeTex.getHeight());
        beeAnim = new Animation<>(0.08f, tmp[0]);
        beeAnim.setPlayMode(Animation.PlayMode.LOOP);

        bees = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            bees.add(new Bee(startX, startY, 10f, 10f));
        }
    }

    public void render(SpriteBatch batch, float delta, OrthographicCamera camera) {
        float viewLeft = camera.position.x - (camera.viewportWidth / 2) * camera.zoom;
        float viewRight = camera.position.x + (camera.viewportWidth / 2) * camera.zoom;
        float viewBottom = camera.position.y - (camera.viewportHeight / 2) * camera.zoom;
        float viewTop = camera.position.y + (camera.viewportHeight / 2) * camera.zoom;

        for (Bee bee : bees) {
            bee.update(delta);

            if (bee.x < viewLeft - 1f) bee.x = viewRight + 1f;
            if (bee.x > viewRight + 1f) bee.x = viewLeft - 1f;
            if (bee.y < viewBottom - 1f) bee.y = viewTop + 1f;
            if (bee.y > viewTop + 1f) bee.y = viewBottom - 1f;

            TextureRegion currentFrame = beeAnim.getKeyFrame(bee.stateTimer, true);

            boolean isMovingRight = bee.speedX > 0;
            if (currentFrame.isFlipX() != isMovingRight) {
                currentFrame.flip(true, false);
            }

            float scaleFactor = 400f;
            float width = (currentFrame.getRegionWidth() / scaleFactor) * bee.scale;
            float height = (currentFrame.getRegionHeight() / scaleFactor) * bee.scale;

            batch.draw(currentFrame, bee.x, bee.y, width, height);
        }
    }

    public void dispose() {
        beeTex.dispose();
    }
}
