public class Move {

    private Item item;
    private Gantry gantry;
    private Slot from;
    private Slot to;

    public Move(Item item, Gantry gantry, Slot from, Slot to) {
        this.item = item;
        this.gantry = gantry;
        this.from = from;
        this.to = to;
    }

    public Item getItem() {
        return item;
    }

    public Gantry getGantry() {
        return gantry;
    }

    public Slot getFrom() {
        return from;
    }

    public Slot getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "Move{" +
                "" + gantry.getId() +
                ", " + from +
                ", " + to +
                ", " + item +
                '}';
    }

}
