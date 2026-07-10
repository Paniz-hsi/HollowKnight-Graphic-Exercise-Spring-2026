package D.HollowKnight.views;

import D.HollowKnight.controllers.MenuController;
import D.HollowKnight.models.DatabaseManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class AchievementsMenuTable extends Table {
    private Table mainTable;
    private Image background;
    private MenuController controller;
    private TextButton.TextButtonStyle style;

    public AchievementsMenuTable(TextButton.TextButtonStyle style, Table mainTable, MenuController controller, Image background) {
        this.style = style;
        this.mainTable = mainTable;
        this.background = background;
        this.controller = controller;

        this.setFillParent(true);
        this.center();
        this.setPosition(1280, 0);

        buildUI();
    }

    public void buildUI() {
        this.clearChildren();

        Label.LabelStyle titleStyle = new Label.LabelStyle(style.font, Color.CYAN);
        Label title = new Label("ACHIEVEMENTS", titleStyle);
        this.add(title).padBottom(50).row();

        DatabaseManager db = controller.getDatabase();

        boolean compUnlocked = db.isAchievementUnlocked("Completion");
        boolean speedUnlocked = db.isAchievementUnlocked("Speedrun");
        boolean hunterUnlocked = db.isAchievementUnlocked("True Hunter");
        boolean knightUnlocked = db.isAchievementUnlocked("Defeat False Knight");
        boolean masksUnlocked = db.isAchievementUnlocked("Resilient Knight");

        addAchievementRow("1. Completion : Finish the game", compUnlocked, style);
        addAchievementRow("2. Speedrun : Finish game under 1 hour", speedUnlocked, style);
        addAchievementRow("3. True Hunter : Kill all types of enemies", hunterUnlocked, style);
        addAchievementRow("4. Defeat False Knight : Defeat False Knight", knightUnlocked, style);
        addAchievementRow("5. Resilient Knight : Finish game with 3+ masks", masksUnlocked, style);

        TextButton backBtn = new TextButton("BACK", style);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                AchievementsMenuTable.this.addAction(Actions.moveTo(1280, 0, 0.75f, Interpolation.exp10Out));
                mainTable.addAction(Actions.moveTo(0, 0, 0.75f, Interpolation.exp10Out));
                background.addAction(Actions.fadeOut(0.4f));
            }
        });

        this.add(backBtn).padTop(40).row();
    }

    private void addAchievementRow(String text, boolean isUnlocked, TextButton.TextButtonStyle style) {
        Color textColor = isUnlocked ? Color.WHITE : Color.DARK_GRAY;
        Label.LabelStyle descStyle = new Label.LabelStyle(style.font, textColor);
        Label descLabel = new Label(text, descStyle);

        if (!isUnlocked) {
            descLabel.getColor().a = 0.6f;
        }

        this.add(descLabel).padBottom(15).left().row();
    }
}
