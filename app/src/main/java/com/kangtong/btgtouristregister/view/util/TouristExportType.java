package com.kangtong.btgtouristregister.view.util;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class TouristExportType {
    public static final int ITINERARY = 101;
    public static final int TICKETS = 102;

    @IntDef({ITINERARY, TICKETS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ExportType {
    }
}
