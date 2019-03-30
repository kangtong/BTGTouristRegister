package com.kangtong.btgtouristregister.model;

import java.util.ArrayList;
import java.util.List;

public class Guide extends IDCard {
    private List<Tourist> touristList = new ArrayList<Tourist>();

    public List<Tourist> getTouristList() {
        return touristList;
    }

    public void setTouristList(List<Tourist> touristList) {
        this.touristList = touristList;
    }
}
