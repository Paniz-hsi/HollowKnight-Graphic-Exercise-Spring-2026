package D.HollowKnight.models;

import D.HollowKnight.controllers.MapController;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import java.util.Random;

public class FalseKnight {
    public enum State {
        IDLE, TURN, CHARGE_RUN, OFFENSIVE_LEAP, DEFENSIVE_LEAP,
        MACE_SLAM_ANTIC, MACE_SLAM_ATTACK, MACE_SLAM_RECOVER,
        MACE_FRENZY, STUN_FALL, STUNNED, WAKING_UP, DEATH
    }

    public State currentState;
    private State lastMove;

    public Body body;
    private Fixture maceFixture;
    private Fixture maggotFixture;

    public int hp = 12;
    public boolean isPhase2 = false;
    private float speedMultiplier = 1.0f;
    public int currentMaceDamage = 1;

    public float stateTimer = 0;
    private float decisionCooldown = 2.0f;
    public boolean isFacingRight = false;
    public boolean isMaceActive = false;
    private boolean isDead = false;

    private int recentDamageCount = 0;
    private float damageTimer = 0;

    private Random random;

    public FalseKnight(World world, float startX, float startY) {
        this.currentState = State.IDLE;
        this.lastMove = State.IDLE;
        this.random = new Random();

        BodyDef bdef = new BodyDef();
        bdef.position.set(startX, startY);
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.45f, 0.5f, new Vector2(0, -0.2f), 0);
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        body.createFixture(fdef).setUserData("enemy");
        body.setUserData(this);
        shape.dispose();

        PolygonShape maceShape = new PolygonShape();
        maceShape.setAsBox(0.5f, 0.1f, new Vector2(0, 0), 0);
        FixtureDef maceDef = new FixtureDef();
        maceDef.shape = maceShape;
        maceDef.isSensor = true;
        maceFixture = body.createFixture(maceDef);
        maceFixture.setUserData("falseKnight_mace");
        maceShape.dispose();

        PolygonShape maggotShape = new PolygonShape();
        maggotShape.setAsBox(0.2f, 0.2f, new Vector2(0, -0.4f), 0);
        FixtureDef maggotDef = new FixtureDef();
        maggotDef.shape = maggotShape;
        maggotDef.isSensor = true;
        maggotFixture = body.createFixture(maggotDef);
        maggotFixture.setUserData("falseKnight_maggot");
        maggotShape.dispose();
    }

    public void update(float delta, Player player, MapController mapController) {
        stateTimer += delta * speedMultiplier;

        if (damageTimer > 0) {
            damageTimer -= delta;
            if (damageTimer <= 0) recentDamageCount = 0;
        }
        if (currentState == State.DEATH) {
            body.setLinearVelocity(0, body.getLinearVelocity().y);
            return;
        }
        if (currentState == State.STUN_FALL || currentState == State.STUNNED) {
            body.setLinearVelocity(0, body.getLinearVelocity().y);
            isMaceActive = false;

            if (currentState == State.STUN_FALL && stateTimer > 1.0f) {
                currentState = State.STUNNED;
                stateTimer = 0;
            }

            if (currentState == State.STUNNED && stateTimer > 4.0f) {
                currentState = State.WAKING_UP;
                stateTimer = 0;
            }
            return;
        }

        if (currentState == State.WAKING_UP) {
            if (stateTimer > 1.0f) wakeUpToPhase2();
            return;
        }

        float distToPlayer = player.getX() - body.getPosition().x;

        if (currentState == State.IDLE) {
            boolean shouldFaceRight = distToPlayer > 0;
            if (shouldFaceRight != isFacingRight && Math.abs(distToPlayer) > 1.0f) {
                currentState = State.TURN;
                stateTimer = 0;
            }
        }

        if (currentState == State.IDLE && stateTimer > decisionCooldown) {
            decideNextMove(Math.abs(distToPlayer));
        }

        executeCurrentState(delta, mapController, distToPlayer);
    }

    private void decideNextMove(float distance) {
        State nextMove = State.IDLE;
        boolean moveChosen = false;

        while (!moveChosen) {
            int rand = random.nextInt(100);

            if (distance < 2.5f) {
                if (rand < 60) nextMove = State.MACE_SLAM_ANTIC;
                else if (rand < 85) nextMove = State.CHARGE_RUN;
                else nextMove = State.OFFENSIVE_LEAP;
            } else {
                if (rand < 50) nextMove = State.OFFENSIVE_LEAP;
                else if (rand < 90) nextMove = State.CHARGE_RUN;
                else nextMove = State.MACE_SLAM_ANTIC;
            }
            if (isPhase2 && rand > 70) {
                nextMove = State.MACE_FRENZY;
            }

            if (recentDamageCount >= 2 && rand > 40) {
                nextMove = State.DEFENSIVE_LEAP;
            }
            if (nextMove != lastMove) {
                moveChosen = true;
            }
        }

        lastMove = nextMove;
        currentState = nextMove;
        stateTimer = 0;
        recentDamageCount = 0;
    }

    private void executeCurrentState(float delta, MapController mapController, float distToPlayer) {
        boolean isLanded = Math.abs(body.getLinearVelocity().y) < 0.01f && stateTimer > 0.5f;

        switch (currentState) {
            case TURN:
                body.setLinearVelocity(0, body.getLinearVelocity().y);
                if (stateTimer > 0.2f) {
                    isFacingRight = !isFacingRight;
                    updateMaceHitbox();
                    currentState = State.IDLE;
                    stateTimer = 0;
                }
                break;

            case MACE_SLAM_ANTIC:
                body.setLinearVelocity(0, body.getLinearVelocity().y);
                if (stateTimer > 0.6f / speedMultiplier) {
                    currentState = State.MACE_SLAM_ATTACK;
                    stateTimer = 0;
                }
                break;

            case MACE_SLAM_ATTACK:
                if (stateTimer == 0) {
                    mapController.shakeCamera(0.2f, 0.3f);
                    isMaceActive = true;
                    updateMaceHitbox();
                }
                if (stateTimer > 0.2f) {
                    isMaceActive = false;
                    updateMaceHitbox();
                    currentState = State.MACE_SLAM_RECOVER;
                    stateTimer = 0;
                }
                break;

            case MACE_SLAM_RECOVER:
                if (stateTimer > 0.4f) {
                    currentState = State.IDLE;
                    stateTimer = 0;
                }
                break;

            case CHARGE_RUN:
                float runSpeed = (isFacingRight ? 3.5f : -3.5f) * speedMultiplier;
                body.setLinearVelocity(runSpeed, body.getLinearVelocity().y);
                isMaceActive = true;
                if (stateTimer > (1.5f / speedMultiplier)) {
                    isMaceActive = false;
                    updateMaceHitbox();
                    body.setLinearVelocity(0, body.getLinearVelocity().y);
                    currentState = State.IDLE;
                    stateTimer = 0;
                }
                break;

            case OFFENSIVE_LEAP:
                if (stateTimer == 0) {
                    float jumpDir = isFacingRight ? 5.0f : -5.0f;
                    body.setLinearVelocity(jumpDir, 8.0f);
                }
                if (isLanded) {
                    mapController.shakeCamera(0.1f, 0.2f);
                    body.setLinearVelocity(0, 0);
                    currentState = State.IDLE;
                    stateTimer = 0;
                }
                break;

            case DEFENSIVE_LEAP:
                if (stateTimer == 0) {
                    float escapeDir = isFacingRight ? -4.0f : 4.0f;
                    body.setLinearVelocity(escapeDir, 6.0f);
                }
                if (isLanded) {
                    body.setLinearVelocity(0, 0);
                    currentState = State.IDLE;
                    stateTimer = 0;
                }
                break;

            case MACE_FRENZY:
                if (stateTimer == 0) {
                    float dir = isFacingRight ? 4.0f : -4.0f;
                    body.setLinearVelocity(dir, 10.0f);
                }
                if (isLanded) {
                    mapController.shakeCamera(0.4f, 0.5f);
                    isMaceActive = true;
                    if (stateTimer > 0.8f) {
                        isMaceActive = false;
                        updateMaceHitbox();
                        currentState = State.IDLE;
                        stateTimer = 0;
                    }
                }
                break;
        }
    }

    public void takeDamage(boolean isMaggotHit) {
        if (currentState == State.DEATH) return;

        if ((currentState == State.STUN_FALL || currentState == State.STUNNED) && !isMaggotHit) {
            return;
        }

        hp -= 1;
        recentDamageCount++;
        damageTimer = 1.5f;

        if (isMaggotHit) {
            AudioManager.getInstance().playSound("enemy_damage.wav");
        } else {
            AudioManager.getInstance().playSound("false_knight_damage_armour.wav");
        }

        if (hp == 6 && !isPhase2) {
            triggerStun();
        } else if (hp <= 0) {
            currentState = State.DEATH;
            isDead = true;
            stateTimer = 0;
        }
    }

    private void triggerStun() {
        currentState = State.STUN_FALL;
        stateTimer = 0;
        recentDamageCount = 0;
        body.setLinearVelocity(0, body.getLinearVelocity().y);
    }

    private void wakeUpToPhase2() {
        isPhase2 = true;
        speedMultiplier = 1.4f;
        decisionCooldown = 0.8f;
        currentMaceDamage = 2;
        currentState = State.IDLE;
        stateTimer = 0;
    }

    public void updateMaceHitbox() {
        PolygonShape shape = (PolygonShape) maceFixture.getShape();

        if (!isMaceActive) {
            shape.setAsBox(0f, 0f, new Vector2(0, 0), 0);
        } else {
            float offsetX = isFacingRight ? 0.8f : -0.8f;
            shape.setAsBox(0.5f, 0.5f, new Vector2(offsetX, 0), 0);
        }

        Filter filter = maceFixture.getFilterData();
        maceFixture.setFilterData(filter);
    }

    public boolean isDead() {
        return isDead;
    }
}
