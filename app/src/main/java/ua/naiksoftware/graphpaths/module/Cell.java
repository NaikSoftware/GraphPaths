package ua.naiksoftware.graphpaths.module;

/**
 * @author Naik
 */
public class Cell {

    public static final String INFINITY = "∞";

    protected int value;

    public Cell() {
    }

    public Cell(int v) {
        value = v;
    }

    public int getInt() {
        return value;
    }

    public void setInt(int v) {
        value = v;
    }

    @Override
    public String toString() {
        if (value == Integer.MAX_VALUE) return INFINITY;
        else return String.valueOf(value);
    }

}
