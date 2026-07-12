package D.HollowKnight.models;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Vector2;

public class Mossfly {
    public enum State { HIDDEN, APPEARING, CHASING, TURNING, RETURNING, DEAD_AIR, DEAD_GROUND }

    private Body body;
    private State currentState;
    private float stateTimer;
    private boolean isFacingRight;

    private int hp = 3;
    private float speed = 1.0f;
    private boolean isDead = false;

    private final float APPEAR_DURATION = 0.5f;
    private final float TURN_DURATION = 0.3f;

    private State nextStateAfterTurn = State.CHASING;

    private float startX;
    private float startY;
    private final float ROOM_RADIUS_X = 5.0f;
    private final float ROOM_RADIUS_Y = 1.0f;

    public float knockbackTimer = 0;
    public Mossfly(World world, float x, float y) {
        currentState = State.HIDDEN;
        stateTimer = 0;
        isFacingRight = false;

        this.startX = x;
        this.startY = y;

        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(x, y);
        body = world.createBody(bdef);

        body.setGravityScale(0f);
        body.setFixedRotation(true);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.2f, 0.2f);
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        body.createFixture(fdef).setUserData("enemy");

        shape.dispose();
        body.setUserData(this);
    }

    public void update(float delta, Player player) {
        stateTimer += delta;

        if (knockbackTimer > 0) {
            knockbackTimer -= delta;
            return;
        }

        if (isDead) {
            if (stateTimer > 0.5f && Math.abs(body.getLinearVelocity().y) < 0.05f) {

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

        if (player == null || player.isDead()) {
            body.setLinearVelocity(0, 0);
            return;
        }

        boolean isPlayerInRoom = Math.abs(player.getX() - startX) <= ROOM_RADIUS_X &&
            Math.abs(player.getY() - startY) <= ROOM_RADIUS_Y;

        switch (currentState) {
            case HIDDEN:
                body.setLinearVelocity(0, 0);
                if (isPlayerInRoom) {
                    currentState = State.APPEARING;
                    stateTimer = 0;
                }
                break;

            case APPEARING:
                body.setLinearVelocity(0, 0);
                if (stateTimer >= APPEAR_DURATION) {
                    currentState = State.CHASING;
                    stateTimer = 0;
                }
                break;

            case CHASING:
                if (!isPlayerInRoom) {
                    for (Fixture f : body.getFixtureList()) f.setSensor(true);

                    Vector2 homeDir = new Vector2(startX - body.getPosition().x, startY - body.getPosition().y);
                    boolean shouldFaceRightHome = homeDir.x > 0;

                    if (Math.abs(homeDir.x) > 0.5f && shouldFaceRightHome != isFacingRight) {
                        currentState = State.TURNING;
                        nextStateAfterTurn = State.RETURNING;
                        stateTimer = 0;
                        body.setLinearVelocity(0, 0);
                    } else {
                        currentState = State.RETURNING;
                        stateTimer = 0;
                    }
                    break;
                }

                Vector2 direction = new Vector2(player.getX() - body.getPosition().x, player.getY() - body.getPosition().y);
                direction.nor();
                boolean shouldFaceRight = direction.x > 0;

                if (Math.abs(direction.x) > 0.1f && shouldFaceRight != isFacingRight) {
                    currentState = State.TURNING;
                    nextStateAfterTurn = State.CHASING;
                    stateTimer = 0;
                    body.setLinearVelocity(0, 0);
                } else {
                    body.setLinearVelocity(direction.x * speed, direction.y * speed);
                }
                break;

            case TURNING:
                body.setLinearVelocity(0, 0);
                if (stateTimer >= TURN_DURATION) {
                    isFacingRight = !isFacingRight;
                    currentState = nextStateAfterTurn;
                    stateTimer = 0;
                }
                break;

            case RETURNING:
                Vector2 homeDirection = new Vector2(startX - body.getPosition().x, startY - body.getPosition().y);
                float distToHome = homeDirection.len();

                if (distToHome < 0.2f) {
                    body.setTransform(startX, startY, 0);
                    body.setLinearVelocity(0, 0);
                    currentState = State.HIDDEN;
                    stateTimer = 0;
                    for (Fixture f : body.getFixtureList()) f.setSensor(false);
                } else {
                    homeDirection.nor();
                    if (Math.abs(homeDirection.x) > 0.1f) {
                        isFacingRight = homeDirection.x > 0;
                    }
                    body.setLinearVelocity(homeDirection.x * speed, homeDirection.y * speed);
                }
                break;
        }
    }

    public void takeDamage() {
        if (isDead) return;
        hp--;

        if (currentState == State.HIDDEN) {
            currentState = State.APPEARING;
            stateTimer = 0;
        }

        if (hp <= 0) {
            isDead = true;
            stateTimer = 0;
            body.setGravityScale(1f);
            body.setLinearVelocity(isFacingRight ? -1.5f : 1.5f, 3f);

            for (Fixture f : body.getFixtureList()) {
                if (f.getUserData() != null && f.getUserData().equals("enemy")) {
                    f.setUserData("dead_enemy");
                }
            }
        } else {
            body.setLinearVelocity(isFacingRight ? -2f : 2f, 2f);
        }
    }

    public State getCurrentState() { return currentState; }
    public float getStateTimer() { return stateTimer; }
    public boolean isFacingRight() { return isFacingRight; }
    public Vector2 getPosition() { return body.getPosition(); }
    public boolean isDead() { return isDead; }
}
