package D.HollowKnight.models;

import com.badlogic.gdx.physics.box2d.Body;

public class BossDoor {
    public Body body;
    public boolean isClosed;

    public BossDoor(Body body) {
        this.body = body;
        this.isClosed = false;
    }
}
