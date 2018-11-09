public class Move {

    int x, y;
    Item item;
    Gantry gantry;
    double time;

    public Move(Gantry gantry, double time, int x, int y, Item item) {
        this.x = x;
        this.y = y;
        this.item = item;
        this.gantry = gantry;
        this.time = time;
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

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Gantry getGantry() {
        return gantry;
    }

    public void setGantry(Gantry gantry) {
        this.gantry = gantry;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }
    @Override
    public String toString() {
        return "Move{" +
                "" + gantry.getId() +
                ", " + time +
                ", " + x +
                ", " + y +
                ", " + item +
                '}';
    }

}
