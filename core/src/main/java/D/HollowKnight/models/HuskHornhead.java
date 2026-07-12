package D.HollowKnight.models;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Vector2;

public class HuskHornhead {
    public enum State {
        IDLE, WALKING, TURNING,
        ATTACK_ANTICIPATE, ATTACK_LUNGE, ATTACK_COOLDOWN,
        DEAD_AIR, DEAD_GROUND
    }

    private Body body;
    private State currentState;
    private float stateTimer;
    private boolean isFacingRight;

    private int hp = 5;
    private boolean isDead = false;
    private boolean hasStartedFalling = false;

    private float walkSpeed = 1.5f;
    private float lungeSpeed = 6.5f;
    private final float WALK_DURATION = 3.0f;
    private final float IDLE_DURATION = 1.5f;
    private final float TURN_DURATION = 0.4f;
    private final float ANTICIPATE_DURATION = 0.6f;
    private final float COOLDOWN_DURATION = 1.0f;

    public int leftEdgeContacts = 0;
    public int rightEdgeContacts = 0;

    private final float VISION_RANGE = 6.0f;
    public float knockbackTimer = 0;

    public HuskHornhead(World world, float x, float y) {
        currentState = State.IDLE;
        stateTimer = 0;
        isFacingRight = false;

        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(x, y);
        body = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.28f, 0.25f);
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 1.5f;
        body.createFixture(fdef).setUserData("enemy");
        shape.dispose();

        PolygonShape leftEdge = new PolygonShape();
        leftEdge.setAsBox(0.05f, 0.15f, new Vector2(-0.35f, -0.25f), 0);
        FixtureDef leftDef = new FixtureDef();
        leftDef.shape = leftEdge;
        leftDef.isSensor = true;
        body.createFixture(leftDef).setUserData("hornhead_left");
        leftEdge.dispose();

        PolygonShape rightEdge = new PolygonShape();
        rightEdge.setAsBox(0.05f, 0.15f, new Vector2(0.35f, -0.25f), 0);
        FixtureDef rightDef = new FixtureDef();
        rightDef.shape = rightEdge;
        rightDef.isSensor = true;
        body.createFixture(rightDef).setUserData("hornhead_right");
        rightEdge.dispose();

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
        if (currentState == State.WALKING && stateTimer > 0.1f) {
            if (Math.abs(body.getLinearVelocity().x) < 0.1f) {
                isHittingWall = true;
            }
        }
        if (currentState == State.ATTACK_LUNGE && stateTimer > 0.1f) {
            if (Math.abs(body.getLinearVelocity().x) < 0.5f) {
                isHittingWall = true;
            }
        }

        boolean canSeePlayer = false;
        if (player != null && !player.isDead()) {
            float distX = player.getX() - body.getPosition().x;
            float distY = Math.abs(player.getY() - body.getPosition().y);

            if (distY < 1.0f) {
                if (isFacingRight && distX > 0 && distX < VISION_RANGE) canSeePlayer = true;
                if (!isFacingRight && distX < 0 && distX > -VISION_RANGE) canSeePlayer = true;
            }
        }

        switch (currentState) {
            case IDLE:
                body.setLinearVelocity(0, body.getLinearVelocity().y);
                if (canSeePlayer) {
                    changeState(State.ATTACK_ANTICIPATE);
                } else if (stateTimer >= IDLE_DURATION) {
                    changeState(State.TURNING);
                }
                break;

            case WALKING:
                body.setLinearVelocity(isFacingRight ? walkSpeed : -walkSpeed, body.getLinearVelocity().y);
                if (canSeePlayer) {
                    changeState(State.ATTACK_ANTICIPATE);
                } else if (isAtEdge || isHittingWall) {
                    changeState(State.IDLE);
                } else if (stateTimer >= WALK_DURATION) {
                    changeState(State.IDLE);
                }
                break;

            case TURNING:
                body.setLinearVelocity(0, body.getLinearVelocity().y);
                if (stateTimer >= TURN_DURATION) {
                    isFacingRight = !isFacingRight;
                    changeState(State.WALKING);
                }
                break;

            case ATTACK_ANTICIPATE:
                body.setLinearVelocity(0, body.getLinearVelocity().y);
                if (stateTimer >= ANTICIPATE_DURATION) {
                    changeState(State.ATTACK_LUNGE);
                }
                break;

            case ATTACK_LUNGE:
                body.setLinearVelocity(isFacingRight ? lungeSpeed : -lungeSpeed, body.getLinearVelocity().y);
                if (isAtEdge || isHittingWall) {
                    changeState(State.ATTACK_COOLDOWN);
                }
                break;

            case ATTACK_COOLDOWN:
                body.setLinearVelocity(0, body.getLinearVelocity().y);
                if (stateTimer >= COOLDOWN_DURATION) {
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

        if (currentState == State.ATTACK_LUNGE || currentState == State.ATTACK_ANTICIPATE) {
            changeState(State.ATTACK_COOLDOWN);
        }

        if (hp <= 0) {
            isDead = true;
            hasStartedFalling = false;
            stateTimer = 0;
            body.setLinearVelocity(isFacingRight ? -1f : 1f, 3.5f);

            for (Fixture f : body.getFixtureList()) {
                f.setSensor(false);
                if (f.getUserData() != null && f.getUserData().equals("enemy")) {
                    f.setUserData("dead_enemy");
                }
            }
        } else {
            body.setLinearVelocity(isFacingRight ? -1.5f : 1.5f, 1.5f);
        }
    }

    public State getCurrentState() { return currentState; }
    public float getStateTimer() { return stateTimer; }
    public boolean isFacingRight() { return isFacingRight; }
    public Vector2 getPosition() { return body.getPosition(); }
    public boolean isDead() { return isDead; }
}
