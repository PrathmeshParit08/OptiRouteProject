package com.optiroute.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private String id;
    private String name;
    private LocationType type;

    public enum LocationType {
        CITY, AIRPORT, STATION
    }
}
