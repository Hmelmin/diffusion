package model;

public class Country {
    private String name;
    private int number;
    private int lowerLeftX;
    private int lowerLeftY;
    private int upperRightX;
    private int upperRightY;
    private int filledDay;

    public Country(String name, int lowerLeftX, int lowerLefty, int upperRightX, int upperRighty) {
        this.name = name;
        this.lowerLeftX = lowerLeftX;
        this.lowerLeftY = lowerLefty;
        this.upperRightX = upperRightX;
        this.upperRightY = upperRighty;
        this.filledDay = -1;
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getLowerLeftX() {
        return lowerLeftX;
    }

    public int getLowerLeftY() {
        return lowerLeftY;
    }

    public int getUpperRightX() {
        return upperRightX;
    }

    public int getUpperRightY() {
        return upperRightY;
    }

    public int getFilledDay() {
        return filledDay;
    }

    public void setFilledDay(int filledDay) {
        this.filledDay = filledDay;
    }
}
