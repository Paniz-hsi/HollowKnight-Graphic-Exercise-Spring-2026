package D.HollowKnight.lwjgl3;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class AssetPacker {
    public static void main(String[] args) {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.maxWidth = 2048;
        settings.maxHeight = 2048;

        String inputDir = "D:/HollowKnight/animations";

        String outputDir = "assets";

        String packFileName = "knight_animations";

        TexturePacker.process(settings, inputDir, outputDir, packFileName);
        System.out.println("Atlas generated successfully!");
    }
}
