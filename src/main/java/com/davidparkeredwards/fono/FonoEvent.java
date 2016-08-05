package com.davidparkeredwards.fono;

/**
 * Created by User on 7/26/2016.
 */
public class FonoEvent {

    String name;
    String date;
    String venueName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLinkToOrigin() {
        return linkToOrigin;
    }

    public void setLinkToOrigin(String linkToOrigin) {
        this.linkToOrigin = linkToOrigin;
    }

    String address;
    String description;
    String category;
    String linkToOrigin;
    int id;

    public FonoEvent(String name, String date, String venueName, String address, String description, String category, String linkToOrigin, int id) {
        this.name = name;
        this.date = date;
        this.venueName = venueName;
        this.address = address;
        this.description = description;
        this.category = category;
        this.linkToOrigin = linkToOrigin;
        this.id = id;
    }

    public String toString() {
        return this.name + "\n" + this.venueName;
    }
}
