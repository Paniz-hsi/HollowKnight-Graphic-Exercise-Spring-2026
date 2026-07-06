package D.HollowKnight.views;

import D.HollowKnight.controllers.MenuController;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class GuideMenuTable extends Table {
    private TextButton.TextButtonStyle btnStyle;
    private Table mainTable;
    private MenuController controller;
    private Image bgImage;

    public GuideMenuTable(TextButton.TextButtonStyle btnStyle, Table mainTable, MenuController controller, Image bgImage) {
        this.btnStyle = btnStyle;
        this.mainTable = mainTable;
        this.controller = controller;
        this.bgImage = bgImage;

        this.setFillParent(true);
        this.center();
        this.setPosition(1280, 0);
    }

    public void buildUI() {
        this.clearChildren();
        if (bgImage != null) {
            this.setBackground(bgImage.getDrawable());
        }

        Label.LabelStyle titleStyle = new Label.LabelStyle(btnStyle.font, Color.YELLOW);
        Label.LabelStyle headerStyle = new Label.LabelStyle(btnStyle.font, Color.CYAN);
        Label.LabelStyle textStyle = new Label.LabelStyle(btnStyle.font, Color.WHITE);

        Table contentTable = new Table();
        contentTable.top().pad(20);

        Label titleLabel = new Label("GUIDE MENU", titleStyle);
        titleLabel.setFontScale(1.5f);
        contentTable.add(titleLabel).padBottom(40).row();

        Label controlsHeader = new Label("--- CONTROLS ---", headerStyle);
        contentTable.add(controlsHeader).padBottom(20).row();

        Table controlsTable = new Table();
        addControlRow(controlsTable, "MOVE UP:", Input.Keys.toString(controller.getKeyUp()), textStyle);
        addControlRow(controlsTable, "MOVE DOWN:", Input.Keys.toString(controller.getKeyDown()), textStyle);
        addControlRow(controlsTable, "MOVE LEFT:", Input.Keys.toString(controller.getKeyLeft()), textStyle);
        addControlRow(controlsTable, "MOVE RIGHT:", Input.Keys.toString(controller.getKeyRight()), textStyle);
        addControlRow(controlsTable, "JUMP:", Input.Keys.toString(controller.getKeyJump()), textStyle);
        addControlRow(controlsTable, "DASH:", Input.Keys.toString(controller.getKeyDash()), textStyle);
        addControlRow(controlsTable, "ATTACK (NAIL):", Input.Keys.toString(controller.getKeyAttack()), textStyle);

        contentTable.add(controlsTable).padBottom(40).row();

        Label mechanicsHeader = new Label("--- ABILITIES & MECHANICS ---", headerStyle);
        contentTable.add(mechanicsHeader).padBottom(20).row();

        String mechanicsText =
            "- HEALTH (MASKS): You lose 1 mask when hit by enemies or spikes.\n" +
                "- SOUL VESSEL: Hitting enemies with your Nail generates Soul.\n" +
                "- FOCUS (HEAL): Hold 'A' key to consume Soul and heal your masks.\n" +
                "- VENGEFUL SPIRIT: Cast a projectile magic by consuming Soul.\n" +
                "- HOWLING WRAITHS: Cast an upward magic blast by consuming Soul.";

        Label mechanicsLabel = new Label(mechanicsText, textStyle);
        mechanicsLabel.setWrap(true);
        mechanicsLabel.setAlignment(Align.left);
        contentTable.add(mechanicsLabel).width(700).padBottom(40).row();

        Label cheatsHeader = new Label("--- CHEAT CODES (Press Left Ctrl + Key) ---", headerStyle);
        contentTable.add(cheatsHeader).padBottom(20).row();

        String cheatsText =
            "1. Boss Arena Teleport: Instantly teleport to the False Knight arena.\n" +
                "2. Noclip/Spectator Mode: Increase speed, disable gravity, collisions & animations.\n" +
                "3. Emergency Heal: Gain ONE extra health mask when your health is empty.\n" +
                "4. Refill Soul Vessel: Instantly and fully refill the Soul vessel.\n" +
                "5. God Mode: Toggle invincibility against spikes, enemies, and bosses.\n" +
                "6. Custom Cheat (Bonus): E.g., Insta-Kill enemies or Time Dilate.";

        Label cheatsLabel = new Label(cheatsText, textStyle);
        cheatsLabel.setWrap(true);
        cheatsLabel.setAlignment(Align.left);
        contentTable.add(cheatsLabel).width(700).padBottom(40).row();

        ScrollPane scrollPane = new ScrollPane(contentTable);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        this.add(scrollPane).width(800).height(450).row();

        TextButton backBtn = new TextButton("BACK", btnStyle);
        this.add(backBtn).padTop(30);

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GuideMenuTable.this.addAction(Actions.moveTo(1280, 0, 0.75f, Interpolation.swingOut));
                if (mainTable != null) {
                    mainTable.addAction(Actions.moveTo(0, 0, 0.75f, Interpolation.swingOut));
                }
            }
        });
    }

    private void addControlRow(Table table, String action, String key, Label.LabelStyle style) {
        table.add(new Label(action, style)).left().padRight(40).padBottom(10);
        table.add(new Label(key, style)).right().padBottom(10).row();
    }
}
