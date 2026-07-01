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
                float x = ((PointMapObject) object).getPoint().x / PPM;
                float y = ((PointMapObject) object).getPoint().y / PPM;
                int id = object.getProperties().get("id", Integer.class);
                spawnPoints.put(id, new Vector2(x, y));
                System.out.println("Spawn point ID: " + id + " loaded at X:" + x + " Y:" + y);
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
}
