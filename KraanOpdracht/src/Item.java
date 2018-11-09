public class Item {

    private final int id;

    public Item(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String printID() {
        if (this.equals(null)) {
            return "null";
        } else {
            return "" + id;
        }
    }

    @Override
    public String toString() {
        return  String.valueOf(id);
    }
}
