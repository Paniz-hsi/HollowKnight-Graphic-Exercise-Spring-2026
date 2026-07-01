package D.HollowKnight.views;

import D.HollowKnight.models.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameUI {
    private OrthographicCamera uiCamera;
    private Viewport uiViewport;
    private SpriteBatch uiBatch;

    private Texture filledMaskTex;
    private Texture emptyMaskTex;
    private Texture staticHealthBarTex;

    private Animation<TextureRegion> breakAnimation;
    private Animation<TextureRegion> refillAnimation;
    private Animation<TextureRegion> introHealthBarAnim;

    private int lastKnownMasks = -1;
    private Array<MaskEffect> activeEffects = new Array<>();
    private float introTimer = 0f;

    private final float UI_SCALE = 0.5f;

    private enum EffectType { BREAK, REFILL }
    private static class MaskEffect {
        float x, y, stateTime;
        EffectType type;

        public MaskEffect(float x, float y, EffectType type) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.stateTime = 0f;
        }
    }

    public GameUI() {
        uiCamera = new OrthographicCamera();
        uiViewport = new FitViewport(800, 480, uiCamera);
        uiCamera.position.set(800 / 2f, 480 / 2f, 0);
        uiBatch = new SpriteBatch();

        filledMaskTex = new Texture("FilledHealth.png");
        emptyMaskTex = new Texture("EmptyHealth.png");
        staticHealthBarTex = new Texture("HealthBar_005.png");

        Texture introHBSheet = new Texture("HealthBar.png");
        int introFrameWidth = introHBSheet.getWidth() / 6;
        TextureRegion[][] introTmp = TextureRegion.split(introHBSheet, introFrameWidth, introHBSheet.getHeight());
        // سرعت انیمیشن شروع رو کمی کندتر کردم تا قشنگ‌تر دیده بشه (0.15 ثانیه برای هر فریم)
        introHealthBarAnim = new Animation<>(0.15f, introTmp[0]);

        Texture breakSheet = new Texture("BreakHealth.png");
        TextureRegion[][] breakTmp = TextureRegion.split(breakSheet, breakSheet.getWidth() / 5, breakSheet.getHeight());
        breakAnimation = new Animation<>(0.08f, breakTmp[0]);

        Texture refillSheet = new Texture("HealthRefill.png");
        TextureRegion[][] refillTmp = TextureRegion.split(refillSheet, refillSheet.getWidth() / 5, refillSheet.getHeight());
        refillAnimation = new Animation<>(0.08f, refillTmp[0]);
    }

    public void render(Player player) {
        uiViewport.apply();
        uiCamera.update();
        uiBatch.setProjectionMatrix(uiCamera.combined);
        uiBatch.begin();

        float delta = Gdx.graphics.getDeltaTime();
        if (delta > 0.1f) delta = 0.1f;
        introTimer += delta;

        float hbWidth = staticHealthBarTex.getWidth() * UI_SCALE;
        float hbHeight = staticHealthBarTex.getHeight() * UI_SCALE;

        float hbX = 20f;
        float hbY = 480f - hbHeight - 20f;

        if (!introHealthBarAnim.isAnimationFinished(introTimer)) {
            TextureRegion hbFrame = introHealthBarAnim.getKeyFrame(introTimer, false);
            uiBatch.draw(hbFrame, hbX, hbY, hbWidth, hbHeight);
        } else {
            uiBatch.draw(staticHealthBarTex, hbX, hbY, hbWidth, hbHeight);
        }

        if (introHealthBarAnim.isAnimationFinished(introTimer)) {
            float maskStartX = hbX + (120f * UI_SCALE);
            float maskStartY = hbY + (-50f * UI_SCALE);
            float maskSpacing = 60f * UI_SCALE;

            float maskWidth = filledMaskTex.getWidth() * UI_SCALE;
            float maskHeight = filledMaskTex.getHeight() * UI_SCALE;

            for (int i = 0; i < player.maxMasks; i++) {
                float x = maskStartX + (i * maskSpacing);
                float y = maskStartY;

                if (i < player.currentMasks) {
                    uiBatch.draw(filledMaskTex, x, y, maskWidth, maskHeight);
                } else {
                    uiBatch.draw(emptyMaskTex, x, y, maskWidth, maskHeight);
                }
            }

            if (lastKnownMasks == -1) {
                lastKnownMasks = player.currentMasks;
            }

            if (player.currentMasks < lastKnownMasks) {
                int brokenIndex = player.currentMasks;
                float effectX = maskStartX + (brokenIndex * maskSpacing);
                activeEffects.add(new MaskEffect(effectX, maskStartY, EffectType.BREAK));
                lastKnownMasks = player.currentMasks;
            } else if (player.currentMasks > lastKnownMasks) {
                int refilledIndex = player.currentMasks - 1;
                float effectX = maskStartX + (refilledIndex * maskSpacing);
                activeEffects.add(new MaskEffect(effectX, maskStartY, EffectType.REFILL));
                lastKnownMasks = player.currentMasks;
            }

            for (int i = activeEffects.size - 1; i >= 0; i--) {
                MaskEffect effect = activeEffects.get(i);
                effect.stateTime += delta;

                Animation<TextureRegion> currentAnim = (effect.type == EffectType.BREAK) ? breakAnimation : refillAnimation;

                if (currentAnim.isAnimationFinished(effect.stateTime)) {
                    activeEffects.removeIndex(i);
                } else {
                    TextureRegion frame = currentAnim.getKeyFrame(effect.stateTime, false);
                    uiBatch.draw(frame, effect.x, effect.y, maskWidth, maskHeight);
                }
            }
        }

        uiBatch.end();
    }

    public void resize(int width, int height) {
        uiViewport.update(width, height, true);
    }

    public void dispose() {
        uiBatch.dispose();
        filledMaskTex.dispose();
        emptyMaskTex.dispose();
        staticHealthBarTex.dispose();
    }
}
