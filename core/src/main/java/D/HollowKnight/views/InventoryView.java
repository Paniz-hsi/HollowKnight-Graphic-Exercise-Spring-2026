package D.HollowKnight.views;

import D.HollowKnight.models.Charm;
import D.HollowKnight.models.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class InventoryView {
    private Player player;
    private BitmapFont font;
    private TextureAtlas atlas;
    private OrthographicCamera uiCamera;
    private Texture backgroundTexture;
    private Texture voidHeartTexture;

    public InventoryView(Player player) {
        this.player = player;

        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("Trajans.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 20;
        font = gen.generateFont(param);
        gen.dispose();

        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        atlas = new TextureAtlas(Gdx.files.internal("knight_animations.atlas"));
        voidHeartTexture = new Texture("Void Heart - charm_black.png");

        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, 1280, 720);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0, 0, 0, 0.85f));
        pixmap.fill();
        backgroundTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    public void render(SpriteBatch batch) {
        uiCamera.update();
        batch.setProjectionMatrix(uiCamera.combined);

        batch.draw(backgroundTexture, 0, 0, 1280, 720);

        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);
        font.draw(batch, "INVENTORY & CHARMS", 530, 690);
        font.draw(batch, "Notches Used: " + player.equippedCharms.size() + " / " + player.MAX_NOTCHES, 530, 640);

        int startX = 100;
        int startY = 480;
        int spacingX = 280;
        int spacingY = 240;
        int index = 0;

        for (Charm charm : Charm.values()) {
            if (charm.name().equals("VOID_HEART") && !player.hasFoundVoidHeart) {
                continue;
            }
            boolean isEquipped = player.hasCharm(charm);

            float x = startX + (index % 4) * spacingX;
            float y = startY - (index / 4) * spacingY;

            if (charm.name().equals("VOID_HEART")) {
                if (isEquipped) {
                    batch.setColor(Color.WHITE);
                } else {
                    batch.setColor(0.3f, 0.3f, 0.3f, 1f);
                }
                batch.draw(voidHeartTexture, x + 30, y, 100, 100);
                batch.setColor(Color.WHITE);
            } else {
                String regionName = getCharmRegionName(charm);
                TextureRegion region = atlas.findRegion(regionName);
                if (region != null) {
                    if (isEquipped) {
                        batch.setColor(Color.WHITE);
                    } else {
                        batch.setColor(0.3f, 0.3f, 0.3f, 1f);
                    }
                    batch.draw(region, x + 30, y, 100, 100);
                    batch.setColor(Color.WHITE);
                }
            }

            font.getData().setScale(1.1f);
            font.setColor(isEquipped ? Color.GREEN : Color.LIGHT_GRAY);
            String displayName = (index + 1) + ". " + getCharmDisplayName(charm);
            font.draw(batch, displayName, x, y - 10);

            font.getData().setScale(0.85f);
            font.setColor(Color.GOLD);
            String description = getCharmDescription(charm);
            font.draw(batch, description, x, y - 65);

            index++;
        }

        font.getData().setScale(1.1f);
        font.setColor(Color.WHITE);
        font.draw(batch, "Press Keys [1 - 6] to Toggle Charms", 480, 100);
        font.draw(batch, "Press [I] or [ESC] to Return", 50, 680);
    }

    private String getCharmDisplayName(Charm charm) {
        switch (charm) {
            case UNBREAKABLE_STRENGTH: return "Unbreakable\nStrength";
            case SOUL_CATCHER: return "Soul Catcher";
            case DASHMASTER: return "Dashmaster";
            case QUICK_SLASH: return "Quick Slash";
            case QUICK_FOCUS: return "Quick Focus";
            case HEAVY_BLOW: return "Heavy Blow";
            case VOID_HEART: return "Void Heart";
            default: return charm.name();
        }
    }

    private String getCharmDescription(Charm charm) {
        switch (charm) {
            case SOUL_CATCHER: return "Increases Soul gained\nfrom striking enemies.";
            case DASHMASTER: return "Allows you to dash\nmore frequently.";
            case UNBREAKABLE_STRENGTH: return "Increases the damage\ndealt by the Nail.";
            case QUICK_SLASH: return "Increases striking\nspeed with the Nail.";
            case QUICK_FOCUS: return "Increases the speed\nof focusing Soul.";
            case HEAVY_BLOW: return "Increases knockback\nforce of Nail strikes.";
            case VOID_HEART: return "Unifies the void.";
            default: return "";
        }
    }

    private String getCharmRegionName(Charm charm) {
        switch (charm) {
            case SOUL_CATCHER: return "Soul Catcher";
            case DASHMASTER: return "Dashmaster";
            case UNBREAKABLE_STRENGTH: return "Unbreakable Strength";
            case QUICK_SLASH: return "Quick Slash";
            case QUICK_FOCUS: return "Quick Focus";
            case HEAVY_BLOW: return "Heavy Blow";
            default: return "default";
        }
    }

    public void handleInput() {
        Charm[] allCharms = Charm.values();
        for (int i = 0; i < allCharms.length; i++) {
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_1 + i)) {
                toggleCharm(allCharms[i]);
            }
        }
    }

    private void toggleCharm(Charm charm) {
        if (player.hasCharm(charm)) {
            player.equippedCharms.remove(charm);
        } else {
            if (player.equippedCharms.size() < player.MAX_NOTCHES) {
                player.equippedCharms.add(charm);
            } else {
                System.out.println("Not enough notches!");
            }
        }
    }

    public void dispose() {
        if (font != null) font.dispose();
        if (atlas != null) atlas.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (voidHeartTexture != null) voidHeartTexture.dispose();
    }
}
