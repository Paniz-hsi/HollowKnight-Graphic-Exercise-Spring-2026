package D.HollowKnight.views;

import D.HollowKnight.models.MapModel;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class MapView {
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private TiledMap map;

    public MapView(TiledMap map, OrthographicCamera camera) {
        this.camera = camera;
        this.map = map;
        this.renderer = new OrthogonalTiledMapRenderer(map, 1f / MapModel.PPM);
    }

    public void render() {
        renderer.setView(camera);
        renderer.render();

        SpriteBatch mainBatch = new SpriteBatch();

        MapLayer objectLayer = renderer.getMap().getLayers().get("platforms");

        mainBatch.setProjectionMatrix(camera.combined);
        mainBatch.begin();
        for (MapLayer layer : renderer.getMap().getLayers()) {
            for (MapObject object : layer.getObjects()) {
                if (object instanceof TiledMapTileMapObject) {
                    TiledMapTileMapObject tileObj = (TiledMapTileMapObject) object;
                    TextureRegion region = tileObj.getTile().getTextureRegion();

                    float x = tileObj.getX() / MapModel.PPM;
                    float y = tileObj.getY() / MapModel.PPM;

                    float width = region.getRegionWidth() / MapModel.PPM;
                    float height = region.getRegionHeight() / MapModel.PPM;
                    boolean flipX = tileObj.isFlipHorizontally();
                    boolean flipY = tileObj.isFlipVertically();


                    mainBatch.draw(
                        region.getTexture(),
                        x, y,
                        0, 0,
                        width, height,
                        tileObj.getScaleX(), tileObj.getScaleY(),
                        -tileObj.getRotation(),
                        region.getRegionX(), region.getRegionY(),
                        region.getRegionWidth(), region.getRegionHeight(),
                        flipX, flipY
                    );
                }
            }
        }
        mainBatch.end();
    }

    public void dispose() {
        renderer.dispose();
    }
}
