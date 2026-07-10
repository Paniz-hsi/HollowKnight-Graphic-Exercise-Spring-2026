package D.HollowKnight.models;

public class Door {
    public boolean isLocked;
    public String targetMap;

    public Door(boolean isLocked, String targetMap) {
        this.isLocked = isLocked;
        this.targetMap = targetMap;
    }
}
