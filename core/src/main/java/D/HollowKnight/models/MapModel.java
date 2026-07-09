package D.HollowKnight.models;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.HashMap;

public class MapModel {
    private World world;
    private TiledMap map;
    public static final float PPM = 450f;
    private HashMap<Integer, Vector2> spawnPoints;
    private Vector2 crawlidSpawn;
    private Vector2 mossflySpawn;
    private Vector2 huskSpawn;
    private Vector2 crystallizedSpawn;
    private Vector2 falseKnightSpawn;
    private Vector2 zoteSpawn;

    public MapModel(World world, TiledMap map) {
        this.world = world;
        this.map = map;
        this.spawnPoints = new HashMap<>();
        parseMapData();
    }

    private void parseMapData() {
        if (map.getLayers().get("ground") != null) {
            for (MapObject object : map.getLayers().get("ground").getObjects().getByType(RectangleMapObject.class)) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                if (rect.getWidth() == 0 || rect.getHeight() == 0) {
                    continue;
                }
                createStaticBody(rect, false, "ground");
            }
        }

        if (map.getLayers().get("walls") != null) {
            for (MapObject object : map.getLayers().get("walls").getObjects().getByType(RectangleMapObject.class)) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                if (rect.getWidth() == 0 || rect.getHeight() == 0) {
                    continue;
                }
                createStaticBody(rect, false, "wall");
            }
        }

        if (map.getLayers().get("hazards") != null) {
            for (MapObject object : map.getLayers().get("hazards").getObjects().getByType(RectangleMapObject.class)) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                if (rect.getWidth() == 0 || rect.getHeight() == 0) {
                    continue;
                }
                createStaticBody(rect, true, "hazard");
            }
        }

        if (map.getLayers().get("interactables") != null) {
            for (MapObject object : map.getLayers().get("interactables").getObjects().getByType(RectangleMapObject.class)) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                if (rect.getWidth() == 0 || rect.getHeight() == 0) {
                    continue;
                }

                if (object.getProperties().containsKey("isBreakable")) {
                    createStaticBody(rect, false, "breakable_wall");
                } else if (object.getProperties().containsKey("type") &&
                    object.getProperties().get("type", String.class).equals("door")) {
                    createStaticBody(rect, false, "door");
                }
            }
        }

        if (map.getLayers().get("spawns") != null) {
            for (MapObject object : map.getLayers().get("spawns").getObjects().getByType(PointMapObject.class)) {
                float rawX = ((PointMapObject) object).getPoint().x;
                float rawY = ((PointMapObject) object).getPoint().y;

                if (object.getName() != null && object.getName().equalsIgnoreCase("crawlid")) {
                    crawlidSpawn = new Vector2(rawX / PPM, rawY / PPM);
                    System.out.println("Crawlid spawn loaded at X:" + crawlidSpawn.x + " Y:" + crawlidSpawn.y);
                    continue;
                }
                if (object.getName() != null && object.getName().equalsIgnoreCase("mossfly")) {
                    mossflySpawn = new Vector2(rawX / PPM, rawY / PPM);
                    System.out.println("Mossfly spawn loaded at X:" + mossflySpawn.x + " Y:" + mossflySpawn.y);
                    continue;
                }
                if (object.getName() != null && object.getName().equalsIgnoreCase("husk")) {
                    huskSpawn = new Vector2(rawX / PPM, rawY / PPM);
                    System.out.println("Husk spawn loaded at X:" + huskSpawn.x + " Y:" + huskSpawn.y);
                    continue;
                }
                if (object.getName() != null && object.getName().equalsIgnoreCase("Crystallized")) {
                    crystallizedSpawn = new Vector2(rawX / PPM, rawY / PPM);
                    System.out.println("Crystallized spawn loaded at X:" + crystallizedSpawn.x + " Y:" + crystallizedSpawn.y);
                    continue;
                }
                if (object.getName() != null && object.getName().equalsIgnoreCase("falseKnight")) {
                    falseKnightSpawn = new Vector2(rawX / PPM, rawY / PPM);
                    System.out.println("False Knight spawn loaded at X:" + falseKnightSpawn.x + " Y:" + falseKnightSpawn.y);
                    continue;
                }
                if (object.getName() != null && object.getName().equalsIgnoreCase("zote")) {
                    zoteSpawn = new Vector2(rawX / PPM, rawY / PPM);
                    System.out.println("Zote spawn loaded at X:" + zoteSpawn.x + " Y:" + zoteSpawn.y);
                    continue;
                }

                float x = rawX / PPM;
                float y = rawY / PPM;

                int id = 1;
                if (object.getProperties().containsKey("id")) {
                    Object idProp = object.getProperties().get("id");
                    if (idProp instanceof Integer) {
                        id = (Integer) idProp;
                    } else if (idProp instanceof String) {
                        id = Integer.parseInt((String) idProp);
                    }
                }

                spawnPoints.put(id, new Vector2(x, y));
                System.out.println("Spawn point ID: " + id + " loaded at X:" + x + " Y:" + y);
                float sensorSize = 40f;
                Rectangle sensorRect = new Rectangle(
                    rawX - (sensorSize / 2),
                    rawY - (sensorSize / 2),
                    sensorSize,
                    sensorSize
                );
                createStaticBody(sensorRect, true, "checkpoint_" + id);
            }
        }
    }

    public Vector2 getSpawnPoint(int id) {
        return spawnPoints.get(id);
    }

    private void createStaticBody(Rectangle rect, boolean isSensor, String userData) {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((rect.getX() + rect.getWidth() / 2) / PPM,
            (rect.getY() + rect.getHeight() / 2) / PPM);

        Body body = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox((rect.getWidth() / 2) / PPM, (rect.getHeight() / 2) / PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.isSensor = isSensor;

        body.createFixture(fdef).setUserData(userData);

        shape.dispose();
    }

    public Vector2 getCrawlidSpawn() {
        return crawlidSpawn;
    }

    public Vector2 getMossflySpawn() {
        return mossflySpawn;
    }

    public Vector2 getHuskSpawn() {
        return huskSpawn;
    }

    public Vector2 getCrystallizedSpawn() {
        return crystallizedSpawn;
    }

    public Vector2 getFalseKnightSpawn() {
        return falseKnightSpawn;
    }

    public Vector2 getZoteSpawn() {
        return zoteSpawn;
    }
}
