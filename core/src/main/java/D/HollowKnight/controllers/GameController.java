package D.HollowKnight.controllers;

import D.HollowKnight.models.AudioManager;
import D.HollowKnight.views.MainMenuView;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class GameController extends Game {

    @Override
    public void create() {
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
