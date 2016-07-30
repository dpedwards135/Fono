package com.davidparkeredwards.fono;

/**
 * Created by User on 7/26/2016.
 */
public class FonoEvent {

    String name;
    String date;
    String venueName;
    String address;
    String description;
    String category;
    String linkToOrigin;

    public FonoEvent(String name, String date, String venueName, String address, String description, String category, String linkToOrigin) {
        this.name = name;
        this.date = date;
        this.venueName = venueName;
        this.address = address;
        this.description = description;
        this.category = category;
        this.linkToOrigin = linkToOrigin;
    }

}
