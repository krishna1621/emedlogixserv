package com.emedlogix.entity;

import java.util.ArrayList;
import java.util.List;

public class Category {
    String value;
    List<VisRange> visRangeList;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<VisRange> getVisRangeList() {
        visRangeList = visRangeList == null ? new ArrayList<>():visRangeList;
        return visRangeList;
    }

    public void setVisRangeList(List<VisRange> visRangeList) {
        this.visRangeList = visRangeList;
    }

    @Override
    public String toString() {
        return "Category{" +
                "value='" + value + '\'' +
                ", visRangeList=" + visRangeList +
                '}';
    }
}
