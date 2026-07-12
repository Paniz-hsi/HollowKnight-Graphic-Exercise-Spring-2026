package D.HollowKnight.models;

import com.badlogic.gdx.physics.box2d.Body;
import java.util.Random;

public class BreakableWall {
    public Body body;
    public int hp;
    public boolean isDestroyed = false;
    public String visualTargetName;

    private Random random;

    public BreakableWall(Body body, int hp, String visualTargetName) {
        this.body = body;
        this.hp = hp;
        this.visualTargetName = visualTargetName;
        this.random = new Random();
    }

    public void takeDamage() {
        if (isDestroyed) return;
        hp--;
        if (random.nextBoolean()) {
            AudioManager.getInstance().playSound("breakable_wall_hit_1.wav");
        } else {
            AudioManager.getInstance().playSound("breakable_wall_hit_2.wav");
        }

        if (hp <= 0) {
            isDestroyed = true;
            AudioManager.getInstance().playSound("break_wall_after_tutorial_area.wav");
        }
    }
}
