package D.HollowKnight.models;

import D.HollowKnight.controllers.MenuController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;

public class Player {
    public enum State {
        IDLE, RUNNING, RUN_TO_IDLE, AIRBORNE, FALLING, LANDING,
        DASHING, DOUBLE_JUMPING, WALL_SLIDING,
        ATTACKING, UP_ATTACKING, DOWN_ATTACKING
    }

    private Body body;
    private float hitBoxWidth = 0.2f;
    private float hitBoxHeight = 0.4f;
    private float speed = 4.0f;

    private State currentState;
    private State previousState;
    private float stateTimer;
    private boolean isFacingRight;
    private int footContacts = 0;
    private int wallContacts = 0;

    private boolean canDoubleJump = true;
    private boolean isDashing = false;
    private float dashTimer = 0;
    private float dashCooldown = 0;
    private final float DASH_DURATION = 0.2f;
    private final float DASH_SPEED = 12f;
    private final float JUMP_VELOCITY = 6.5f;

    private float attackTimer = 0;
    private final float ATTACK_DURATION = 0.25f;
    private float landingTimer = 0;
    private final float LANDING_DURATION = 0.12f;

    public Player(float startX, float startY, World world) {
        currentState = State.IDLE;
        previousState = State.IDLE;
        stateTimer = 0;
        isFacingRight = true;

        BodyDef bdef = new BodyDef();
        bdef.position.set(startX, startY);
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(hitBoxWidth / 2, hitBoxHeight / 2);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.friction = 0.2f;

        body.createFixture(fdef).setUserData("player");
        shape.dispose();

        body.createFixture(fdef).setUserData("player");

        body.setUserData(this);

        PolygonShape footShape = new PolygonShape();
        footShape.setAsBox(hitBoxWidth / 2.5f, 0.05f, new com.badlogic.gdx.math.Vector2(0, -hitBoxHeight / 2), 0);
        FixtureDef footDef = new FixtureDef();
        footDef.shape = footShape;
        footDef.isSensor = true;
        body.createFixture(footDef).setUserData("foot");
        footShape.dispose();

        PolygonShape wallShape = new PolygonShape();
        wallShape.setAsBox(hitBoxWidth / 2 + 0.02f, hitBoxHeight / 3f, new com.badlogic.gdx.math.Vector2(0, 0), 0);
        FixtureDef wallDef = new FixtureDef();
        wallDef.shape = wallShape;
        wallDef.isSensor = true;
        body.createFixture(wallDef).setUserData("wallSensor");
        wallShape.dispose();
    }

    public void update(float delta, MenuController controller) {
        float velX = 0;
        float velY = body.getLinearVelocity().y;

        if (dashCooldown > 0) dashCooldown -= delta;
        if (attackTimer > 0) attackTimer -= delta;
        if (landingTimer > 0) landingTimer -= delta;

        if (isOnGround() && previousState == State.FALLING) {
            landingTimer = LANDING_DURATION;
            canDoubleJump = true;
        }

        if (Gdx.input.isKeyJustPressed(controller.getKeyDash()) && dashCooldown <= 0 && !isDashing) {
            isDashing = true;
            dashTimer = DASH_DURATION;
            dashCooldown = 0.8f;
            attackTimer = 0;
        }

        if (isDashing) {
            dashTimer -= delta;
            velX = isFacingRight ? DASH_SPEED : -DASH_SPEED;
            velY = 0;
            if (dashTimer <= 0) {
                isDashing = false;
            }
        } else {
            if (Gdx.input.isKeyPressed(controller.getKeyLeft())) {
                velX = -speed;
                isFacingRight = false;
            } else if (Gdx.input.isKeyPressed(controller.getKeyRight())) {
                velX = speed;
                isFacingRight = true;
            }

            if (Gdx.input.isKeyJustPressed(controller.getKeyJump())) {
                if (isOnGround()) {
                    velY = JUMP_VELOCITY;
                    landingTimer = 0;
                } else if (canDoubleJump && currentState != State.WALL_SLIDING) {
                    velY = JUMP_VELOCITY;
                    canDoubleJump = false;
                    currentState = State.DOUBLE_JUMPING;
                    stateTimer = 0;
                } else if (isSlidingOnWall()) {
                    velY = JUMP_VELOCITY;
                    velX = isFacingRight ? -speed * 1.5f : speed * 1.5f;
                    isFacingRight = !isFacingRight;
                }
            }

            if (!Gdx.input.isKeyPressed(controller.getKeyJump()) && velY > 0) {
                velY *= 0.5f;
            }
            if (Gdx.input.isKeyJustPressed(controller.getKeyAttack()) && attackTimer <= 0) {
                attackTimer = ATTACK_DURATION;
                landingTimer = 0;
            }

            if (isSlidingOnWall() && velY < 0) {
                velY = -1.2f;
                canDoubleJump = true;
            }
        }

        body.setLinearVelocity(velX, velY);

        currentState = determineState(velX, velY, controller);

        if (currentState != previousState) {
            stateTimer = 0;
        } else {
            stateTimer += delta;
        }
        previousState = currentState;
    }

    private State determineState(float velX, float velY, MenuController controller) {
        if (isDashing) return State.DASHING;

        if (attackTimer > 0) {
            if (Gdx.input.isKeyPressed(controller.getKeyUp())) {
                return State.UP_ATTACKING;
            }
            if (Gdx.input.isKeyPressed(controller.getKeyDown()) && !isOnGround()) {
                return State.DOWN_ATTACKING;
            }
            return State.ATTACKING;
        }

        if (!isOnGround()) {
            if (isSlidingOnWall()) return State.WALL_SLIDING;
            if (currentState == State.DOUBLE_JUMPING && stateTimer < 0.25f) return State.DOUBLE_JUMPING;

            return (velY > 0.1f) ? State.AIRBORNE : State.FALLING;
        }

        if (landingTimer > 0) return State.LANDING;

        if (Math.abs(velX) > 0.1f) {
            return State.RUNNING;
        }

        if (previousState == State.RUNNING && Math.abs(velX) <= 0.1f) {
            return State.RUN_TO_IDLE;
        }

        if (previousState == State.RUN_TO_IDLE && stateTimer < 0.4f) {
            return State.RUN_TO_IDLE;
        }

        return State.IDLE;
    }

    public void addFootContact() { footContacts++; }
    public void removeFootContact() { footContacts--; }
    public void addWallContact() { wallContacts++; }
    public void removeWallContact() { wallContacts--; }

    public boolean isOnGround() {
        return footContacts > 0;
    }

    public boolean isSlidingOnWall() {
        return wallContacts > 0 && !isOnGround() &&
            (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT) ||
                Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT));
    }

    public void triggerPogoJump() {
        body.setLinearVelocity(body.getLinearVelocity().x, JUMP_VELOCITY * 1.2f);
        canDoubleJump = true;
    }

    public State getCurrentState() { return currentState; }
    public float getStateTimer() { return stateTimer; }
    public boolean isFacingRight() { return isFacingRight; }
    public float getX() { return body.getPosition().x; }
    public float getY() { return body.getPosition().y; }
    public float getWidth() { return hitBoxWidth; }
    public float getHeight() { return hitBoxHeight; }
}
