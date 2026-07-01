package D.HollowKnight.models;

public class SaveSlotData {
    private boolean hasSave;
    private String mapName;
    private int progress;

    public SaveSlotData(boolean hasSave, String mapName, int progress) {
        this.hasSave = hasSave;
        this.mapName = mapName;
        this.progress = progress;
    }

    public boolean isHasSave() {
        return hasSave;
    }

    public String getMapName() {
        return mapName;
    }

    public int getProgress() {
        return progress;
    }

    public void setHasSave(boolean hasSave) {
        this.hasSave = hasSave;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public void setProgress(int progress) {
        if (progress >= 0 && progress <= 100) {
            this.progress = progress;
        } else {
            System.out.println("Error: Progress must be between 0 and 100!");
        }
    }
}
