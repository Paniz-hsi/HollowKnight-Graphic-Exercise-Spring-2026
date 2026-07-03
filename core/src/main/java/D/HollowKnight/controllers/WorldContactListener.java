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

        if (isSensorMatch(fixA, fixB, "player", "enemy") || isSensorMatch(fixA, fixB, "player", "spikes")) {
            Player player = getPlayerFromFixture(fixA, fixB, "player");
            if (player != null) {
                player.takeDamage();
            }
        }

        if (isSensorMatch(fixA, fixB, "attack", "enemy")) {
            Player player = getPlayerFromFixture(fixA, fixB, "attack");
            if (player != null) {
                player.gainSoul();
            }
        }
        if (isCheckpointContact(fixA, fixB)) {
            Player player = getPlayerFromFixture(fixA, fixB, "player");
            int checkpointId = extractCheckpointId(fixA, fixB);

            if (player != null && checkpointId != -1) {
                player.activateCheckpoint(checkpointId);
                System.out.println("Checkpoint " + checkpointId + " activated! Progress: " + player.getProgressPercentage() + "%");
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
    private boolean isCheckpointContact(Fixture fixA, Fixture fixB) {
        if (fixA.getUserData() == null || fixB.getUserData() == null) return false;
        String dataA = fixA.getUserData().toString();
        String dataB = fixB.getUserData().toString();

        return (dataA.equals("player") && dataB.startsWith("checkpoint_")) ||
            (dataB.equals("player") && dataA.startsWith("checkpoint_"));
    }

    private int extractCheckpointId(Fixture fixA, Fixture fixB) {
        String data = "";
        if (fixA.getUserData() != null && fixA.getUserData().toString().startsWith("checkpoint_")) {
            data = fixA.getUserData().toString();
        } else if (fixB.getUserData() != null && fixB.getUserData().toString().startsWith("checkpoint_")) {
            data = fixB.getUserData().toString();
        }

        if (!data.isEmpty()) {
            try {
                String[] parts = data.split("_");
                if (parts.length > 1) {
                    return Integer.parseInt(parts[1]);
                }
            } catch (NumberFormatException e) {
                System.err.println("Error parsing checkpoint ID from: " + data);
            }
        }
        return -1;
    }
}
