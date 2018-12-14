package model;

public class Country {
    public final String name;
    public final int lowerLeftX;
    public final int lowerLeftY;
    public final int upperRightX;
    public final int upperRightY;
    private int filledDay;

    public Country(String name, int lowerLeftX, int lowerLefty, int upperRightX, int upperRighty) {
        this.name = name;
        this.lowerLeftX = lowerLeftX;
        this.lowerLeftY = lowerLefty;
        this.upperRightX = upperRightX;
        this.upperRightY = upperRighty;
        this.filledDay = -1;
    }

    public int getFilledDay() {
        return filledDay;
    }

    public void setFilledDay(int filledDay) {
        this.filledDay = filledDay;
    }
}
