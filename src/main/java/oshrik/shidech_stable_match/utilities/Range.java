package oshrik.shidech_stable_match.utilities;

/**
 * מחלקה המייצגת טווח מספרי (מינימום ומקסימום).
 */
public class Range {
    private int min;
    private int max;

    public Range(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public Range()
    {
        
    }

    public int getMin() { return min; }
    public int getMax() { return max; }

    

    public void setMin(int min) {
        this.min = min;
    }

    public void setMax(int max) {
        this.max = max;
    }

    // בדיקה האם מספר נמצא בתוך הטווח
    public boolean contains(int value) {
        return value >= min && value <= max;
    }
}