package D.HollowKnight.models;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Vector2;

public class CrystalGuardian {
    public enum State {
        IDLE, TURNING, SHOOTING, ENRAGED_RUN, DEAD_AIR, DEAD_GROUND
    }

    private Body body;
    private State currentState;
    private float stateTimer;
    private boolean isFacingRight;

    private int hp = 7;
    private boolean isDead = false;
    private boolean hasStartedFalling = false;

    private float enragedSpeed = 7.0f;
    private final float ENRAGED_DURATION = 2.5f;
    private final float TURN_DURATION = 0.4f;
    private final float SHOOT_DURATION = 1.6f;

    private final float LASER_START_TIME = 0.4f;
    private final float LASER_END_TIME = 1.2f;

    private final float LASER_RANGE = 4.0f;

    public int leftEdgeContacts = 0;
    public int rightEdgeContacts = 0;
    public float knockbackTimer = 0;

    public CrystalGuardian(World world, float x, float y) {
        currentState = State.IDLE;
        stateTimer = 0;
        isFacingRight = true;

        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(x, y);
        body = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.3f, 0.35f);
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 2.0f;
        body.createFixture(fdef).setUserData("enemy");
        shape.dispose();

        PolygonShape leftEdge = new PolygonShape();
        leftEdge.setAsBox(0.05f, 0.15f, new Vector2(-0.35f, -0.35f), 0);
        FixtureDef leftDef = new FixtureDef();
        leftDef.shape = leftEdge;
        leftDef.isSensor = true;
        body.createFixture(leftDef).setUserData("guardian_left");
        leftEdge.dispose();

        PolygonShape rightEdge = new PolygonShape();
        rightEdge.setAsBox(0.05f, 0.15f, new Vector2(0.35f, -0.35f), 0);
        FixtureDef rightDef = new FixtureDef();
        rightDef.shape = rightEdge;
        rightDef.isSensor = true;
        body.createFixture(rightDef).setUserData("guardian_right");
        rightEdge.dispose();

        PolygonShape wallSensor = new PolygonShape();
        wallSensor.setAsBox(0.35f, 0.2f, new Vector2(0, 0), 0);
        FixtureDef wallDef = new FixtureDef();
        wallDef.shape = wallSensor;
        wallDef.isSensor = true;
        body.createFixture(wallDef).setUserData("guardian_wall_sensor");
        wallSensor.dispose();

        body.setUserData(this);
    }

    public void update(float delta, Player player) {
        stateTimer += delta;

        if (knockbackTimer > 0) {
            knockbackTimer -= delta;
            return;
        }

        if (isDead) {
            if (body.getLinearVelocity().y < -0.1f) hasStartedFalling = true;
            if (hasStartedFalling && Math.abs(body.getLinearVelocity().y) < 0.05f) {
                if (currentState != State.DEAD_GROUND) {
                    currentState = State.DEAD_GROUND;
                    stateTimer = 0;
                    body.setLinearVelocity(0, 0);
                    body.setType(BodyDef.BodyType.StaticBody);
                    for (Fixture f : body.getFixtureList()) f.setSensor(true);
                }
            } else if (currentState != State.DEAD_GROUND) {
                currentState = State.DEAD_AIR;
            }
            return;
        }

        boolean isAtEdge = false;
        if (Math.abs(body.getLinearVelocity().y) < 0.1f) {
            if (isFacingRight && rightEdgeContacts <= 0) isAtEdge = true;
            else if (!isFacingRight && leftEdgeContacts <= 0) isAtEdge = true;
        }

        boolean isHittingWall = false;
        if (currentState == State.ENRAGED_RUN && stateTimer > 0.1f) {
            if (Math.abs(body.getLinearVelocity().x) < 0.5f) isHittingWall = true;
        }

        boolean canSeePlayer = false;
        if (player != null && !player.isDead()) {
            float distX = player.getPosition().x - body.getPosition().x;
            float distY = Math.abs(player.getPosition().y - body.getPosition().y);

            if (distY < 3.0f) {
                if (isFacingRight && distX > 0 && distX < LASER_RANGE) canSeePlayer = true;
                if (!isFacingRight && distX < 0 && distX > -LASER_RANGE) canSeePlayer = true;
            }
        }

        switch (currentState) {
            case IDLE:
                body.setLinearVelocity(0, body.getLinearVelocity().y);

                if (canSeePlayer || stateTimer > 1.5f) {
                    changeState(State.SHOOTING);
                }
                break;

            case SHOOTING:
                body.setLinearVelocity(0, body.getLinearVelocity().y);

                if (stateTimer >= LASER_START_TIME && stateTimer <= LASER_END_TIME) {
                    checkLaserCollision(player);
                }

                if (stateTimer >= SHOOT_DURATION) {
                    changeState(State.ENRAGED_RUN);
                }
                break;

            case ENRAGED_RUN:
                body.setLinearVelocity(isFacingRight ? enragedSpeed : -enragedSpeed, body.getLinearVelocity().y);

                if (isAtEdge || isHittingWall || stateTimer >= ENRAGED_DURATION) {
                    changeState(State.IDLE);
                }
                break;

            case TURNING:
                body.setLinearVelocity(0, body.getLinearVelocity().y);
                if (stateTimer >= TURN_DURATION) {
                    isFacingRight = !isFacingRight;
                    changeState(State.IDLE);
                }
                break;
        }
    }

    private void changeState(State newState) {
        if (currentState != newState) {
            currentState = newState;
            stateTimer = 0;
        }
    }

    public void takeDamage() {
        if (isDead) return;
        hp--;

        if (currentState == State.IDLE) {
            changeState(State.TURNING);
        }

        if (hp <= 0) {
            isDead = true;
            hasStartedFalling = false;
            stateTimer = 0;
            body.setLinearVelocity(isFacingRight ? -1.5f : 1.5f, 4f);

            for (Fixture f : body.getFixtureList()) {
                f.setSensor(false);
                if (f.getUserData() != null && f.getUserData().equals("enemy")) {
                    f.setUserData("dead_enemy");
                }
            }
        } else {
            body.setLinearVelocity(isFacingRight ? -1.0f : 1.0f, 1.5f);
        }
    }

    public State getCurrentState() { return currentState; }
    public float getStateTimer() { return stateTimer; }
    public boolean isFacingRight() { return isFacingRight; }
    public Vector2 getPosition() { return body.getPosition(); }
    public boolean isDead() { return isDead; }
    public float getLaserRange() { return LASER_RANGE; }

    public void reverseDirection() {
        if (currentState != State.DEAD_GROUND && currentState != State.DEAD_AIR) {
            changeState(State.TURNING);
        }
    }

    private void checkLaserCollision(Player player) {
        if (player == null || player.isDead()) return;

        float playerX = player.getPosition().x;
        float playerY = player.getPosition().y;
        float guardianX = body.getPosition().x;
        float guardianY = body.getPosition().y;

        boolean isWithinHeight = Math.abs(playerY - guardianY) < 0.5f;

        if (isWithinHeight) {
            if (isFacingRight) {
                if (playerX >= guardianX && playerX <= guardianX + LASER_RANGE) {
                    player.takeDamageFromEnemy(body.getPosition().x);
                }
            } else {
                if (playerX <= guardianX && playerX >= guardianX - LASER_RANGE) {
                    player.takeDamageFromEnemy(body.getPosition().x);
                }
            }
        }
    }

    public boolean isLaserActive() {
        return currentState == State.SHOOTING &&
            stateTimer >= LASER_START_TIME &&
            stateTimer <= LASER_END_TIME;
    }
}
