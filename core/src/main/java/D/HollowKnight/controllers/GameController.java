package D.HollowKnight.controllers;

import D.HollowKnight.models.AudioManager;
import D.HollowKnight.views.MainMenuView;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;

public class GameController extends Game {

    @Override
    public void create() {
        Pixmap pixmap = new Pixmap(Gdx.files.internal("Cursor.png"));
        int xHotspot = 0;
        int yHotspot = 0;
        Cursor customCursor = Gdx.graphics.newCursor(pixmap, xHotspot, yHotspot);
        Gdx.graphics.setCursor(customCursor);

        pixmap.dispose();
        this.setScreen(new MainMenuView(this));
    }

    @Override
    public void render () {
        super.render();
        AudioManager.getInstance().update(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void dispose () {
        super.dispose();
    }
}
