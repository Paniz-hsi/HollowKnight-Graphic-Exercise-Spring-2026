package D.HollowKnight.models;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Vector2;

public class Crawlid {
    public enum State { WALKING, TURNING, DEAD_AIR, DEAD_GROUND }

    private Body body;
    private State currentState;
    private float stateTimer;
    private boolean movingRight;
    private int hp = 2;
    private float speed = 0.8f;
    private boolean isDead = false;
    public int leftEdgeContacts = 0;
    public int rightEdgeContacts = 0;

    private boolean isTurning = false;
    private float turnTimer = 0;
    private final float TURN_DURATION = 0.5f;

    public Crawlid(World world, float x, float y) {
        currentState = State.WALKING;
        stateTimer = 0;
        movingRight = false;

        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(x, y);
        body = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.2f, 0.15f);
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        body.createFixture(fdef).setUserData("enemy");

        PolygonShape wallSensor = new PolygonShape();
        wallSensor.setAsBox(0.25f, 0.1f, new Vector2(0, 0), 0);
        FixtureDef wallDef = new FixtureDef();
        wallDef.shape = wallSensor;
        wallDef.isSensor = true;
        body.createFixture(wallDef).setUserData("enemy_wall_sensor");

        shape.dispose();
        wallSensor.dispose();
        PolygonShape leftEdge = new PolygonShape();
        leftEdge.setAsBox(0.05f, 0.05f, new Vector2(-0.25f, -0.2f), 0);
        FixtureDef leftDef = new FixtureDef();
        leftDef.shape = leftEdge;
        leftDef.isSensor = true;
        body.createFixture(leftDef).setUserData("edge_left");

        PolygonShape rightEdge = new PolygonShape();
        rightEdge.setAsBox(0.05f, 0.05f, new Vector2(0.25f, -0.2f), 0);
        FixtureDef rightDef = new FixtureDef();
        rightDef.shape = rightEdge;
        rightDef.isSensor = true;
        body.createFixture(rightDef).setUserData("edge_right");

        leftEdge.dispose();
        rightEdge.dispose();
        body.setUserData(this);
    }

    public void update(float delta) {
        stateTimer += delta;

        if (isDead) {
            boolean sensorsTouchGround = (leftEdgeContacts > 0 || rightEdgeContacts > 0) && body.getLinearVelocity().y <= 0;
            boolean isRestingOnGround = (stateTimer > 0.5f && Math.abs(body.getLinearVelocity().y) < 0.05f);

            if (sensorsTouchGround || isRestingOnGround) {
                if (currentState != State.DEAD_GROUND) {
                    currentState = State.DEAD_GROUND;
                    stateTimer = 0;

                    body.setLinearVelocity(0, 0);
                    body.setType(BodyDef.BodyType.StaticBody);

                    for (Fixture f : body.getFixtureList()) {
                        f.setSensor(true);
                    }
                }
            } else if (currentState != State.DEAD_GROUND) {
                currentState = State.DEAD_AIR;
            }

            return;
        }

        if (!isTurning && Math.abs(body.getLinearVelocity().y) < 0.1f) {
            if (movingRight && rightEdgeContacts <= 0) {
                reverseDirection();
            } else if (!movingRight && leftEdgeContacts <= 0) {
                reverseDirection();
            }
        }
        if (!isTurning && Math.abs(body.getLinearVelocity().y) < 0.1f) {
            if (movingRight && rightEdgeContacts <= 0) {
                reverseDirection();
            } else if (!movingRight && leftEdgeContacts <= 0) {
                reverseDirection();
            }
        }

        if (isTurning) {
            currentState = State.TURNING;
            body.setLinearVelocity(0, body.getLinearVelocity().y);
            turnTimer += delta;
            if (turnTimer >= TURN_DURATION) {
                isTurning = false;
                turnTimer = 0;
                movingRight = !movingRight;
                stateTimer = 0;
            }
        } else {
            currentState = State.WALKING;
            body.setLinearVelocity(movingRight ? speed : -speed, body.getLinearVelocity().y);
        }
    }

    public void reverseDirection() {
        if (!isTurning && !isDead) {
            isTurning = true;
            turnTimer = 0;
            stateTimer = 0;
        }
    }

    public void takeDamage() {
        if (isDead) return;
        hp--;
        if (hp <= 0) {
            isDead = true;
            stateTimer = 0;
            body.setLinearVelocity(movingRight ? -1f : 1f, 3f);

            for (Fixture f : body.getFixtureList()) {
                if (f.getUserData() != null && f.getUserData().equals("enemy")) {
                    f.setUserData("dead_enemy");
                }
            }
        } else {
            body.setLinearVelocity(movingRight ? -2f : 2f, 2f);
        }
    }

    public State getCurrentState() { return currentState; }
    public float getStateTimer() { return stateTimer; }
    public boolean isMovingRight() { return movingRight; }
    public Vector2 getPosition() { return body.getPosition(); }
    public boolean isDead() { return isDead; }
}
