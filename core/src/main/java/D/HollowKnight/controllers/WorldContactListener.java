package D.HollowKnight.controllers;

import D.HollowKnight.models.Player;
import com.badlogic.gdx.physics.box2d.*;

public class WorldContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        if (isSensorMatch(fixA, fixB, "foot", "ground")) {
            getPlayerFromFixture(fixA, fixB, "foot").addFootContact();
        }

        if (isSensorMatch(fixA, fixB, "wallSensor", "wall")) {
            getPlayerFromFixture(fixA, fixB, "wallSensor").addWallContact();
        }

        if (isSensorMatch(fixA, fixB, "downAttack", "enemy") || isSensorMatch(fixA, fixB, "downAttack", "spikes")) {
            Player player = getPlayerFromFixture(fixA, fixB, "downAttack");
            if (player != null) {
                player.triggerPogoJump();
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        if (isSensorMatch(fixA, fixB, "foot", "ground")) {
            getPlayerFromFixture(fixA, fixB, "foot").removeFootContact();
        }

        if (isSensorMatch(fixA, fixB, "wallSensor", "wall")) {
            getPlayerFromFixture(fixA, fixB, "wallSensor").removeWallContact();
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}

    private boolean isSensorMatch(Fixture fixA, Fixture fixB, String targetA, String targetB) {
        if (fixA.getUserData() == null || fixB.getUserData() == null) return false;
        String dataA = fixA.getUserData().toString();
        String dataB = fixB.getUserData().toString();

        return (dataA.equals(targetA) && dataB.equals(targetB)) ||
            (dataA.equals(targetB) && dataB.equals(targetA));
    }

    private Player getPlayerFromFixture(Fixture fixA, Fixture fixB, String playerSensorName) {
        if (fixA.getUserData() != null && fixA.getUserData().equals(playerSensorName)) {
            return (Player) fixA.getBody().getUserData();
        } else if (fixB.getUserData() != null && fixB.getUserData().equals(playerSensorName)) {
            return (Player) fixB.getBody().getUserData();
        }
        return null;
    }
}
