package com.emedlogix.entity;

public class VisRange {
    String min;
    String max;

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    @Override
    public String toString() {
        return "VisRange{" +
                "min='" + min + '\'' +
                ", max='" + max + '\'' +
                '}';
    }
}
