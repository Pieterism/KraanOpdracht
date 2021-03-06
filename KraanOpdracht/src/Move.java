public class Move {

    int destinationX, destinationY;
    Gantry gantry;
    Item item;
    double time;

    public Move(Gantry gantry, int destinationX, int destinationY) {
        this.destinationX = destinationX;
        this.destinationY = destinationY;
        this.time = gantry.getTime();
        this.item = gantry.getItem();
        this.gantry = gantry;
    }

    public int getX() {
        return destinationX;
    }

    public void setX(int destinationX) {
        this.destinationX = destinationX;
    }

    public int getDestinationY() {
        return destinationY;
    }

    public void setDestinationY(int destinationY) {
        this.destinationY = destinationY;
    }

    public Item getItem() { return  item;}

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
        return  gantry.getId() +
                ";" + time +
                ";" + destinationX +
                ";" + destinationY +
                ";" + gantry.getItem();
    }

}
