package D.HollowKnight.views;

import D.HollowKnight.controllers.MenuController;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MapSelectionTable extends Table {

    public MapSelectionTable(TextButton.TextButtonStyle btnStyle, Table startGameTable, MenuController controller) {
        this.setFillParent(true);
        this.center();
        this.setPosition(1280, 0);

        Label.LabelStyle titleStyle = new Label.LabelStyle(btnStyle.font, Color.LIGHT_GRAY);
        Label titleLabel = new Label("CHOOSE YOUR MAP", titleStyle);
        titleLabel.setFontScale(1.5f);

        TextButton greenpathBtn = new TextButton("GREENPATH", btnStyle);
        TextButton crossroadsBtn = new TextButton("CROSSROADS", btnStyle);
        TextButton backBtn = new TextButton("BACK", btnStyle);

        this.add(titleLabel).padBottom(50).row();
        this.add(greenpathBtn).padBottom(20).row();
        this.add(crossroadsBtn).padBottom(50).row();
        this.add(backBtn).padTop(20).row();

        greenpathBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.startNewGame("maps/greenpath.tmx");
            }
        });

        crossroadsBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                controller.startNewGame("maps/crossroads.tmx");
            }
        });

        backBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                MapSelectionTable.this.addAction(Actions.moveTo(1280, 0, 0.75f, Interpolation.exp10Out));
                startGameTable.addAction(Actions.moveTo(0, 0, 0.75f, Interpolation.exp10Out));
            }
        });
    }
}
