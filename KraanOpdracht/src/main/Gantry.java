package main;

import java.util.ArrayList;
import java.util.List;

public class Gantry {

    private final int id;
    private final int xMin, xMax;
    private final int startX, startY;
    private final double xSpeed, ySpeed;
    private List<Move> executedMoves;

    private int x, y;
    private double time;
    private Item item;

    public Gantry(int id,
                  int xMin, int xMax,
                  int startX, int startY,
                  double xSpeed, double ySpeed) {
        this.id = id;
        this.xMin = xMin;
        this.xMax = xMax;
        this.startX = startX;
        this.startY = startY;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.item = null;
        this.executedMoves = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public int getXMax() {
        return xMax;
    }

    public int getXMin() {
        return xMin;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public double getXSpeed() {
        return xSpeed;
    }

    public double getYSpeed() {
        return ySpeed;
    }

    public int getxMin() {
        return xMin;
    }

    public int getxMax() {
        return xMax;
    }

    public double getxSpeed() {
        return xSpeed;
    }

    public double getySpeed() {
        return ySpeed;
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

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public List<Move> getExecutedMoves() {
        return executedMoves;
    }

    public void setExecutedMoves(List<Move> executedMoves) {
        this.executedMoves = executedMoves;
    }

    public void addMove(Move m) {
        this.executedMoves.add(m);
    }

    public void clearMoves() {
        this.executedMoves.clear();
    }

    public boolean overlapsGantryArea(Gantry g) {
        return g.xMin < xMax && xMin < g.xMax;
    }

    public int[] getOverlapArea(Gantry g) {

        int maxmin = Math.max(xMin, g.xMin);
        int minmax = Math.min(xMax, g.xMax);

        if (minmax < maxmin)
            return null;
        else
            return new int[]{maxmin, minmax};
    }

    public boolean canReachSlot(Slot s) {
        return xMin <= s.getCenterX() && s.getCenterX() <= xMax;
    }

    public String printItemID() {
        return item.printID();
    }

    public Gantry copy() {
        Gantry g = new Gantry(this.id, this.xMin, this.xMax, this.startX, this.startY, this.xSpeed, this.ySpeed);
        g.setX(this.x);
        g.setY(this.y);
        g.setTime(this.time);
        g.setItem(this.item);
        return g;
    }

    //GANTRY ACTIONS
    //main.Gantry verplaatsen van huidige locatie naar nieuwe locatie(destinationX,destinationY)
    public Move moveTo(int x, int y) {
        double xTime = (Math.abs(x - this.getX())) / this.getXSpeed();
        double yTime = (Math.abs(y - this.getY())) / this.getYSpeed();

        this.time += Math.max(xTime, yTime);
        this.x = x;
        this.y = y;

        Gantry g = copy();

        Move move = new Move(g, x, y);
        return move;

    }

    //Methode die item uit slot s in this gantry zet en verwijdert uit slot
    public Move pickupItem(Slot s) {
        this.item = s.getItem();
        s.removeItem();
        return getMove();
    }

    //Methode die item uit this gantry in slot s plaatst en item uit gantry verwijdert
    public Move placeItem(Slot s) {
        s.setItem(this.item);
        this.item = null;
        return getMove();
    }

    public Move getMove() {
        this.time += 10;
        Gantry g = copy();
        Move move = new Move(g, this.x, this.y);
        return move;
    }

    public List<Move> getMovesAtTimeIndex(double time) {
        List<Move> result = new ArrayList<>();
        Move tempmove = null;
        double delta = 1000;
        for (Move move : this.executedMoves) {
            double d = Math.abs(time - move.time);
            if (d < delta && !(move.getX() == -15) && !(move.getItem() == null)) {
                tempmove = move;
                delta = d;
            }
        }
        result.add(this.executedMoves.get(this.executedMoves.indexOf(tempmove) - 1));
        result.add(this.executedMoves.get(this.executedMoves.indexOf(tempmove) + 2));

        return result;
    }

    public Move getNextInputGantryMove(double time) {
        //TODO LAST MOVE!
        Move m = this.executedMoves.stream().filter(move -> move.getTime() > time && move.getX() == -15 && move.getItem() == null).findFirst().orElse(null);
        if (m == null) {
            System.out.println("ERROR NEXTINPUT");
        }
        return m;
        //return this.executedMoves.stream().filter(move -> move.getTime() > time && move.getX() == -15 && move.getItem() ==  null).findFirst().orElse(null);
    }


    public Move getPreviousInputGantryMove(Move move) {
        if (move == null) {
            System.out.println("no next move error");
        }
        return this.executedMoves.get(this.executedMoves.indexOf(move) - 3);
    }
}



