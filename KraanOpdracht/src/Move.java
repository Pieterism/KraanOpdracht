public class Move {

    int gantryID, x, y, itemInCraneID;
    double time;

    public Move(int gantryID, int x, int y, int itemInCraneID, double time) {
        this.gantryID = gantryID;
        this.x = x;
        this.y = y;
        this.itemInCraneID = itemInCraneID;
        this.time = time;
    }

    public int getGantryID() {
        return gantryID;
    }

    public void setGantryID(int gantryID) {
        this.gantryID = gantryID;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getItemInCraneID() {
        return itemInCraneID;
    }

    public void setItemInCraneID(int itemInCraneID) {
        this.itemInCraneID = itemInCraneID;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }
}
