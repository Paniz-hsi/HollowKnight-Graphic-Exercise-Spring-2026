package D.HollowKnight.models;

import D.HollowKnight.controllers.MapController;
import D.HollowKnight.controllers.MenuController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Player {
    public enum State {
        IDLE, RUNNING, RUN_TO_IDLE, AIRBORNE, FALLING, LANDING,
        DASHING, DOUBLE_JUMPING, WALL_SLIDING,
        ATTACKING, UP_ATTACKING, DOWN_ATTACKING, DEAD, FOCUSING
    }
    public enum AttackDirection { SIDE, UP, DOWN }
    private AttackDirection currentAttackDir = AttackDirection.SIDE;

    public int deathCount = 0;
    public int enemiesKilled = 0;
    public float playTime = 0f;
    public Set<String> killedEnemyTypes = new HashSet<>();
    private Body body;
    private float hitBoxWidth = 0.2f;
    private float hitBoxHeight = 0.4f;
    private float speed = 4.0f;
    private MapController mapController;

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

    public int maxMasks = 5;
    public int currentMasks = 5;
    public int soul = 0;
    public final int MAX_SOUL = 99;

    public boolean isInvincible = false;
    public float invincibilityTimer = 0f;
    public final float INVINCIBILITY_DURATION = 1.0f;

    public boolean isFocusing = false;
    public float focusTimer = 0f;
    public final float FOCUS_DURATION = 1.5f;
    public final int FOCUS_SOUL_COST = 33;
    private int currentSpawnPointId = 1;
    private Set<Integer> unlockedSpawns = new HashSet<>();
    private boolean needsRespawn = false;
    private boolean isDead = false;
    private Fixture attackFixture;

    public boolean justHealed = false;
    public float healEffectTimer = 0f;
    public boolean canMove = true;
    private String pendingMapTransition = null;
    public boolean isGodMode = false;
    public boolean isNoclip = false;
    private String[] swordSounds = {
        "sword_1.wav", "sword_2.wav", "sword_3.wav", "sword_4.wav", "sword_5.wav"
    };
    private String[] soulSounds = {
        "soul_pickup_1.wav", "soul_pickup_2.wav", "soul_pickup_3.wav",
        "soul_pickup_4.wav", "soul_pickup_5.wav", "soul_pickup_6.wav", "soul_pickup_7.wav"
    };
    private Random random = new Random();
    private boolean isChargingSoundPlaying = false;
    public Set<Charm> equippedCharms = new HashSet<>();
    public final int MAX_NOTCHES = 3;

    public Player(float startX, float startY, World world , MapController mapController) {
        currentState = State.IDLE;
        previousState = State.IDLE;
        stateTimer = 0;
        isFacingRight = true;
        this.mapController = mapController;

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

        body.setUserData(this);

        PolygonShape footShape = new PolygonShape();
        footShape.setAsBox(hitBoxWidth / 2.5f, 0.05f, new Vector2(0, -hitBoxHeight / 2), 0);
        FixtureDef footDef = new FixtureDef();
        footDef.shape = footShape;
        footDef.isSensor = true;
        body.createFixture(footDef).setUserData("foot");
        footShape.dispose();

        PolygonShape wallShape = new PolygonShape();
        wallShape.setAsBox(hitBoxWidth / 2 + 0.02f, hitBoxHeight / 3f, new Vector2(0, 0), 0);
        FixtureDef wallDef = new FixtureDef();
        wallDef.shape = wallShape;
        wallDef.isSensor = true;
        body.createFixture(wallDef).setUserData("wallSensor");
        wallShape.dispose();

        PolygonShape attackShape = new PolygonShape();
        attackShape.setAsBox(0.15f, 0.2f, new Vector2(0.3f, 0), 0);
        FixtureDef attackDef = new FixtureDef();
        attackDef.shape = attackShape;
        attackDef.isSensor = true;
        attackFixture = body.createFixture(attackDef);
        attackFixture.setUserData("attack");
        attackShape.dispose();
    }

    public void update(float delta, MenuController controller) {
        if (isDead) {
            stateTimer += delta;
            body.setLinearVelocity(0, 0);

            if (stateTimer > 2.5f) {
                isDead = false;
                currentMasks = 5;
                currentState = State.IDLE;

                currentSpawnPointId = 1;
                needsRespawn = true;
            }
            return;
        }

        if (isNoclip) {
            float noclipSpeed = 15f;
            float velX = 0;
            float velY = 0;

            if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT)) velX = -noclipSpeed;
            if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT)) velX = noclipSpeed;
            if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.UP)) velY = noclipSpeed;
            if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.DOWN)) velY = -noclipSpeed;

            body.setLinearVelocity(velX, velY);
            currentState = State.AIRBORNE;
            return;
        }

        float velX = 0;
        float velY = body.getLinearVelocity().y;

        if (attackTimer > 0) {
            PolygonShape shape = (PolygonShape) attackFixture.getShape();
            if (currentAttackDir == AttackDirection.UP) {
                shape.setAsBox(0.3f, 0.2f, new Vector2(0, 0.4f), 0);
                attackFixture.setUserData("attack");
            } else if (currentAttackDir == AttackDirection.DOWN) {
                shape.setAsBox(0.3f, 0.2f, new Vector2(0, -0.4f), 0);
                attackFixture.setUserData("downAttack");
            } else {
                shape.setAsBox(0.15f, 0.2f, new Vector2(isFacingRight ? 0.3f : -0.3f, 0), 0);
                attackFixture.setUserData("attack");
            }
            attackFixture.setSensor(true);

            Filter filter = attackFixture.getFilterData();
            attackFixture.setFilterData(filter);

        } else {
            ((PolygonShape)attackFixture.getShape()).setAsBox(0f, 0f, new Vector2(0, 0), 0);
            attackFixture.setUserData("attack");
        }

        if (dashCooldown > 0) dashCooldown -= delta;
        if (attackTimer > 0) attackTimer -= delta;
        if (landingTimer > 0) landingTimer -= delta;
        if (isInvincible) {
            invincibilityTimer -= delta;
            if (invincibilityTimer <= 0) {
                isInvincible = false;
            }
        }

        if (canMove && Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.A) && isOnGround() &&
            (currentState == State.IDLE || currentState == State.FOCUSING)) {

            if (soul >= FOCUS_SOUL_COST && currentMasks < maxMasks) {

                if (!isChargingSoundPlaying) {
                    AudioManager.getInstance().playSound("focus_health_charging.wav");
                    isChargingSoundPlaying = true;
                }

                isFocusing = true;
                focusTimer += delta;

                if (focusTimer >= getFocusDuration()) {
                    currentMasks++;
                    soul -= FOCUS_SOUL_COST;
                    focusTimer = 0;
                    justHealed = true;
                    healEffectTimer = 0;
                    AudioManager.getInstance().playSound("focus_health_heal.wav");

                    isChargingSoundPlaying = false;
                }
            } else {
                isFocusing = false;
                focusTimer = 0;
                isChargingSoundPlaying = false;
            }
        } else {
            isFocusing = false;
            focusTimer = 0;
            isChargingSoundPlaying = false;
        }

        if (justHealed) {
            healEffectTimer += delta;
            if (healEffectTimer >= 0.4f) {
                justHealed = false;
            }
        }

        if (isOnGround() && previousState == State.FALLING) {
            landingTimer = LANDING_DURATION;
            canDoubleJump = true;
        }

        if (canMove && Gdx.input.isKeyJustPressed(controller.getKeyDash()) && dashCooldown <= 0 && !isDashing && !isFocusing) {
            isDashing = true;
            dashTimer = DASH_DURATION;
            dashCooldown = getDashCooldown();
            attackTimer = 0;
        }

        if (!canMove) {
            velX = 0;
            velY = body.getLinearVelocity().y;
            isDashing = false;
            attackTimer = 0;
        } else if (isFocusing) {
            velX = 0;
            velY = body.getLinearVelocity().y;
        } else if (isDashing) {
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
                attackTimer = getAttackDuration();
                landingTimer = 0;

                int index = random.nextInt(swordSounds.length);
                AudioManager.getInstance().playSound(swordSounds[index]);

                if (Gdx.input.isKeyPressed(controller.getKeyUp())) {
                    currentAttackDir = AttackDirection.UP;
                } else if (Gdx.input.isKeyPressed(controller.getKeyDown()) && !isOnGround()) {
                    currentAttackDir = AttackDirection.DOWN;
                } else {
                    currentAttackDir = AttackDirection.SIDE;
                }
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
        if (isFocusing) return State.FOCUSING;

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
        if (Math.abs(velX) > 0.1f) return State.RUNNING;
        if (previousState == State.RUNNING && Math.abs(velX) <= 0.1f) return State.RUN_TO_IDLE;
        if (previousState == State.RUN_TO_IDLE && stateTimer < 0.4f) return State.RUN_TO_IDLE;

        return State.IDLE;
    }

    public void addFootContact() { footContacts++; }
    public void removeFootContact() { footContacts--; }
    public void addWallContact() { wallContacts++; }
    public void removeWallContact() { wallContacts--; }
    public boolean isOnGround() { return footContacts > 0; }
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

    public void gainSoul() {
        if (soul < MAX_SOUL) {
            int index = random.nextInt(soulSounds.length);
            AudioManager.getInstance().playSound(soulSounds[index]);
        }
        int soulGained = hasCharm(Charm.SOUL_CATCHER) ? 16 : 11; // سول بیشتر
        soul = Math.min(soul + soulGained, MAX_SOUL);
    }
    public void activateCheckpoint(int spawnId) {
        this.currentSpawnPointId = spawnId;
        this.unlockedSpawns.add(spawnId);
    }
    public int getCurrentSpawnPointId() { return currentSpawnPointId; }
    public int getProgressPercentage() { return Math.min(this.unlockedSpawns.size() * 20, 100); }
    public String getUnlockedSpawnsString() {
        StringBuilder sb = new StringBuilder();
        for (Integer id : unlockedSpawns) {
            sb.append(id).append(",");
        }
        if (sb.length() > 0) sb.setLength(sb.length() - 1);
        return sb.toString();
    }
    public void loadUnlockedSpawns(String data) {
        unlockedSpawns.clear();
        if (data != null && !data.isEmpty()) {
            String[] parts = data.split(",");
            for (String part : parts) unlockedSpawns.add(Integer.parseInt(part.trim()));
        }
    }

    public void takeDamageFromEnemy(float attackerX) {
        if (isInvincible || isDead || isGodMode) return;
        currentMasks--;
        AudioManager.getInstance().playSound("hero_damage.wav");
        mapController.shakeCamera(0.2f, 0.3f);
        if (currentMasks <= 0) {
            triggerDeath();
        } else {
            isInvincible = true;
            invincibilityTimer = INVINCIBILITY_DURATION;

            float knockbackDir = (body.getPosition().x < attackerX) ? -1.0f : 1.0f;
            body.setLinearVelocity(knockbackDir * 5.0f, 3.0f);
        }
        isFocusing = false;
        focusTimer = 0;
    }

    public void takeDamageFromHazard() {
        if (isInvincible || isDead || isGodMode) return;
        currentMasks--;
        AudioManager.getInstance().playSound("hero_damage.wav");
        mapController.shakeCamera(0.2f, 0.3f);
        if (currentMasks <= 0) {
            triggerDeath();
        } else {
            needsRespawn = true;
            isInvincible = true;
            invincibilityTimer = INVINCIBILITY_DURATION;
        }
        isFocusing = false;
        focusTimer = 0;
    }

    private void triggerDeath() {
        isDead = true;
        deathCount++;
        currentState = State.DEAD;
        stateTimer = 0;
        body.setLinearVelocity(0, 0);
    }
    public void respawnAt(Vector2 pos) {
        body.setTransform(pos.x, pos.y, 0);
        body.setLinearVelocity(0, 0);
    }
    public boolean needsRespawn() { return needsRespawn; }
    public void setNeedsRespawn(boolean needsRespawn) { this.needsRespawn = needsRespawn; }
    public boolean isDead() { return isDead; }
    public void setDead(boolean dead) { isDead = dead; }
    public Vector2 getPosition() {
        if (body != null) {
            return body.getPosition();
        }
        return new Vector2(0, 0);
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    public boolean isCanMove() {
        return canMove;
    }
    public void setPendingMapTransition(String targetMap) {
        this.pendingMapTransition = targetMap;
    }

    public String getPendingMapTransition() {
        return pendingMapTransition;
    }

    public void clearPendingMapTransition() {
        this.pendingMapTransition = null;
    }

    public void toggleNoclip() {
        isNoclip = !isNoclip;
        if (isNoclip) {
            body.setType(BodyDef.BodyType.KinematicBody);
            for(Fixture fix : body.getFixtureList()) fix.setSensor(true);
        } else {
            body.setType(BodyDef.BodyType.DynamicBody);
            for(Fixture fix : body.getFixtureList()) {
                String userData = (String) fix.getUserData();
                if ("player".equals(userData)) fix.setSensor(false);
            }
        }
    }

    public void cheatHeal() {
        if (currentMasks == maxMasks) {
            maxMasks++;
        }
        currentMasks++;
        justHealed = true;
        healEffectTimer = 0;
    }

    public void cheatFillSoul() {
        soul = MAX_SOUL;
    }
    public boolean hasCharm(Charm charm) {
        return equippedCharms.contains(charm);
    }
    public float getDashCooldown() {
        return hasCharm(Charm.DASHMASTER) ? 0.4f : 0.8f;
    }

    public float getDashSpeed() {
        float speed = DASH_SPEED;
        return speed;
    }


    public float getAttackDuration() {
        return hasCharm(Charm.QUICK_SLASH) ? 0.15f : 0.25f;
    }


    public float getFocusDuration() {
        return hasCharm(Charm.QUICK_FOCUS) ? 0.8f : 1.5f;
    }

    public int getAttackDamage() {
        int damage = 1;
        if (hasCharm(Charm.UNBREAKABLE_STRENGTH)) damage += 1; // دمیج دو برابر
        return damage;
    }
}
