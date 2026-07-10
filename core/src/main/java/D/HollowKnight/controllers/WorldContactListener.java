package D.HollowKnight.controllers;

import D.HollowKnight.models.*;
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

        if (isSensorMatch(fixA, fixB, "edge_left", "ground") || isSensorMatch(fixA, fixB, "edge_left", "platform")) {
            Crawlid c = getCrawlidFromFixture(fixA, fixB, "edge_left");
            if (c != null) c.leftEdgeContacts++;
        }

        if (isSensorMatch(fixA, fixB, "edge_right", "ground") || isSensorMatch(fixA, fixB, "edge_right", "platform")) {
            Crawlid c = getCrawlidFromFixture(fixA, fixB, "edge_right");
            if (c != null) c.rightEdgeContacts++;
        }

        if (isSensorMatch(fixA, fixB, "enemy_wall_sensor", "ground") ||
            isSensorMatch(fixA, fixB, "enemy_wall_sensor", "wall") ||
            isSensorMatch(fixA, fixB, "enemy_wall_sensor", "hazard")) {
            Fixture enemyFix = fixA.getUserData().equals("enemy_wall_sensor") ? fixA : fixB;
            if (enemyFix.getBody().getUserData() instanceof Crawlid) {
                Crawlid crawlid = (Crawlid) enemyFix.getBody().getUserData();
                crawlid.reverseDirection();
            }
        }

        if (isSensorMatch(fixA, fixB, "hornhead_left", "ground") || isSensorMatch(fixA, fixB, "hornhead_left", "platform")) {
            HuskHornhead h = getHuskHornheadFromFixture(fixA, fixB, "hornhead_left");
            if (h != null) h.leftEdgeContacts++;
        }
        if (isSensorMatch(fixA, fixB, "hornhead_right", "ground") || isSensorMatch(fixA, fixB, "hornhead_right", "platform")) {
            HuskHornhead h = getHuskHornheadFromFixture(fixA, fixB, "hornhead_right");
            if (h != null) h.rightEdgeContacts++;
        }
        if (isSensorMatch(fixA, fixB, "player", "enemy")) {
            Player player = getPlayerFromFixture(fixA, fixB, "player");
            Fixture enemyFix = fixA.getUserData().equals("enemy") ? fixA : fixB;

            if (player != null && enemyFix.getBody() != null) {
                player.takeDamageFromEnemy(enemyFix.getBody().getPosition().x);
            }
        }

        if (isSensorMatch(fixA, fixB, "player", "spike") || isSensorMatch(fixA, fixB, "player", "hazard")) {
            Player player = getPlayerFromFixture(fixA, fixB, "player");

            if (player != null) {
                player.takeDamageFromHazard();

            }
        }

        if (isSensorMatch(fixA, fixB, "attack", "enemy")) {
            Fixture enemyFix = fixA.getUserData().equals("enemy") ? fixA : fixB;
            Player p = getPlayerFromFixture(fixA, fixB, "attack");

            if (!(enemyFix.getBody().getUserData() instanceof FalseKnight)) {
                AudioManager.getInstance().playSound("enemy_damage.wav");
            }

            if (enemyFix.getBody().getUserData() instanceof Crawlid) {
                ((Crawlid) enemyFix.getBody().getUserData()).takeDamage();
                if (p != null) { p.enemiesKilled++; p.killedEnemyTypes.add("Crawlid"); }
            }
            else if (enemyFix.getBody().getUserData() instanceof Mossfly) {
                ((Mossfly) enemyFix.getBody().getUserData()).takeDamage();
                if (p != null) { p.enemiesKilled++; p.killedEnemyTypes.add("Mossfly"); }
            }
            else if (enemyFix.getBody().getUserData() instanceof HuskHornhead) {
                ((HuskHornhead) enemyFix.getBody().getUserData()).takeDamage();
                if (p != null) { p.enemiesKilled++; p.killedEnemyTypes.add("HuskHornhead"); }
            }
            else if (enemyFix.getBody().getUserData() instanceof CrystalGuardian) {
                ((CrystalGuardian) enemyFix.getBody().getUserData()).takeDamage();
                if (p != null) { p.enemiesKilled++; p.killedEnemyTypes.add("CrystalGuardian"); }
            }
            if (enemyFix.getBody().getUserData() instanceof FalseKnight) {
                FalseKnight boss = (FalseKnight) enemyFix.getBody().getUserData();
                boolean isMaggot = enemyFix.getUserData().equals("falseKnight_maggot");
                boss.takeDamage(isMaggot);
            }


            if (p != null) p.gainSoul();
        }

        if (isSensorMatch(fixA, fixB, "downAttack", "enemy")) {
            Fixture enemyFix = fixA.getUserData().equals("enemy") ? fixA : fixB;

            if (!(enemyFix.getBody().getUserData() instanceof FalseKnight)) {
                AudioManager.getInstance().playSound("enemy_damage.wav");
            }

            if (enemyFix.getBody().getUserData() instanceof Crawlid) {
                ((Crawlid) enemyFix.getBody().getUserData()).takeDamage();
            } else if (enemyFix.getBody().getUserData() instanceof Mossfly) {
                ((Mossfly) enemyFix.getBody().getUserData()).takeDamage();
            } else if (enemyFix.getBody().getUserData() instanceof HuskHornhead) {
                ((HuskHornhead) enemyFix.getBody().getUserData()).takeDamage();
            } else if (enemyFix.getBody().getUserData() instanceof CrystalGuardian) {
                ((CrystalGuardian) enemyFix.getBody().getUserData()).takeDamage();
            }
            if (enemyFix.getBody().getUserData() instanceof FalseKnight) {
                FalseKnight boss = (FalseKnight) enemyFix.getBody().getUserData();
                boolean isMaggot = enemyFix.getUserData().equals("falseKnight_maggot");
                boss.takeDamage(isMaggot);
            }

            Player p = getPlayerFromFixture(fixA, fixB, "downAttack");
            if (p != null) {
                p.gainSoul();
                p.triggerPogoJump();
            }
        }

        if (isSensorMatch(fixA, fixB, "downAttack", "hazard")) {
            Player p = getPlayerFromFixture(fixA, fixB, "downAttack");
            if (p != null) p.triggerPogoJump();
        }

        if (isSensorMatch(fixA, fixB, "attack", "door") || isSensorMatch(fixA, fixB, "downAttack", "door")) {
            Fixture doorFix = fixA.getUserData().equals("door") ? fixA : fixB;
            Fixture playerAttackFix = fixA.getUserData().equals("door") ? fixB : fixA;
            Object bodyData = doorFix.getBody().getUserData();

            if (bodyData instanceof Door) {
                Door door = (Door) bodyData;

                if (!door.isLocked) {
                    Player p = (Player) playerAttackFix.getBody().getUserData();
                    if (p != null) {
                        p.setPendingMapTransition(door.targetMap);
                    }
                }
            }
        }
        if (isSensorMatch(fixA, fixB, "player", "falseKnight_mace")) {
            Player player = getPlayerFromFixture(fixA, fixB, "player");
            Fixture maceFix = fixA.getUserData().equals("falseKnight_mace") ? fixA : fixB;

            if (maceFix.getBody().getUserData() instanceof FalseKnight) {
                FalseKnight boss = (FalseKnight) maceFix.getBody().getUserData();
                if (boss.isMaceActive && player != null) {
                    for (int i = 0; i < boss.currentMaceDamage; i++) {
                        player.takeDamageFromEnemy(boss.body.getPosition().x);
                    }
                }
            }
        }


        if (isSensorMatch(fixA, fixB, "guardian_left", "ground") || isSensorMatch(fixA, fixB, "guardian_left", "platform")) {
            CrystalGuardian g = getGuardianFromFixture(fixA, fixB, "guardian_left");
            if (g != null) g.leftEdgeContacts++;
        }
        if (isSensorMatch(fixA, fixB, "guardian_right", "ground") || isSensorMatch(fixA, fixB, "guardian_right", "platform")) {
            CrystalGuardian g = getGuardianFromFixture(fixA, fixB, "guardian_right");
            if (g != null) g.rightEdgeContacts++;
        }

        if (isSensorMatch(fixA, fixB, "guardian_wall_sensor", "ground") ||
            isSensorMatch(fixA, fixB, "guardian_wall_sensor", "wall") ||
            isSensorMatch(fixA, fixB, "guardian_wall_sensor", "platform")) {

            Fixture guardianFix = fixA.getUserData().equals("guardian_wall_sensor") ? fixA : fixB;
            if (guardianFix.getBody().getUserData() instanceof CrystalGuardian) {
                CrystalGuardian guardian = (CrystalGuardian) guardianFix.getBody().getUserData();
                guardian.reverseDirection();
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

        if (isSensorMatch(fixA, fixB, "edge_left", "ground") || isSensorMatch(fixA, fixB, "edge_left", "platform")) {
            Crawlid c = getCrawlidFromFixture(fixA, fixB, "edge_left");
            if (c != null) c.leftEdgeContacts--;
        }

        if (isSensorMatch(fixA, fixB, "edge_right", "ground") || isSensorMatch(fixA, fixB, "edge_right", "platform")) {
            Crawlid c = getCrawlidFromFixture(fixA, fixB, "edge_right");
            if (c != null) c.rightEdgeContacts--;
        }

        if (isSensorMatch(fixA, fixB, "hornhead_left", "ground") || isSensorMatch(fixA, fixB, "hornhead_left", "platform")) {
            HuskHornhead h = getHuskHornheadFromFixture(fixA, fixB, "hornhead_left");
            if (h != null) h.leftEdgeContacts--;
        }
        if (isSensorMatch(fixA, fixB, "hornhead_right", "ground") || isSensorMatch(fixA, fixB, "hornhead_right", "platform")) {
            HuskHornhead h = getHuskHornheadFromFixture(fixA, fixB, "hornhead_right");
            if (h != null) h.rightEdgeContacts--;
        }
        if (isSensorMatch(fixA, fixB, "guardian_left", "ground") || isSensorMatch(fixA, fixB, "guardian_left", "platform")) {
            CrystalGuardian g = getGuardianFromFixture(fixA, fixB, "guardian_left");
            if (g != null) g.leftEdgeContacts--;
        }
        if (isSensorMatch(fixA, fixB, "guardian_right", "ground") || isSensorMatch(fixA, fixB, "guardian_right", "platform")) {
            CrystalGuardian g = getGuardianFromFixture(fixA, fixB, "guardian_right");
            if (g != null) g.rightEdgeContacts--;
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

    private Crawlid getCrawlidFromFixture(Fixture fixA, Fixture fixB, String target) {
        if (fixA.getUserData() != null && fixA.getUserData().equals(target) && fixA.getBody().getUserData() instanceof Crawlid) {
            return (Crawlid) fixA.getBody().getUserData();
        } else if (fixB.getUserData() != null && fixB.getUserData().equals(target) && fixB.getBody().getUserData() instanceof Crawlid) {
            return (Crawlid) fixB.getBody().getUserData();
        }
        return null;
    }

    private HuskHornhead getHuskHornheadFromFixture(Fixture fixA, Fixture fixB, String target) {
        if (fixA.getUserData() != null && fixA.getUserData().equals(target) && fixA.getBody().getUserData() instanceof HuskHornhead) {
            return (HuskHornhead) fixA.getBody().getUserData();
        } else if (fixB.getUserData() != null && fixB.getUserData().equals(target) && fixB.getBody().getUserData() instanceof HuskHornhead) {
            return (HuskHornhead) fixB.getBody().getUserData();
        }
        return null;
    }
    private CrystalGuardian getGuardianFromFixture(Fixture fixA, Fixture fixB, String target) {
        if (fixA.getUserData() != null && fixA.getUserData().equals(target) && fixA.getBody().getUserData() instanceof CrystalGuardian) {
            return (CrystalGuardian) fixA.getBody().getUserData();
        } else if (fixB.getUserData() != null && fixB.getUserData().equals(target) && fixB.getBody().getUserData() instanceof CrystalGuardian) {
            return (CrystalGuardian) fixB.getBody().getUserData();
        }
        return null;
    }
}
