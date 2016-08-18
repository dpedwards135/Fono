package com.davidparkeredwards.fono;

import java.util.List;

/**
 * Created by User on 7/26/2016.
 */
public class FonoEvent {

    String name;
    String date;
    String venueName;
    String address;
    String description;
    String category_1;
    String category_2;
    String category_3;
    String linkToOrigin;
    int id;

    String locationCoordinates;
    String requestCoordinates;
    double distance;
    double eventScore;

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    String requester;


    public String getRequestCoordinates() {
        return requestCoordinates;
    }

    public void setRequestCoordinates(String requestCoordinates) {
        this.requestCoordinates = requestCoordinates;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getEventScore() {
        return eventScore;
    }

    public void setEventScore(double eventScore) {
        this.eventScore = eventScore;
    }


    public FonoEvent(String name, String date, String venueName, String address, String description,
                     String category_1, String category_2, String category_3, String linkToOrigin,
                     int id, String locationCoordinates, String requestCoordinates, double distance,
                     double eventScore, String requester) {
        this.name = name;
        this.date = date;
        this.venueName = venueName;
        this.address = address;
        this.description = description;
        this.category_1 = category_1;
        this.category_2 = category_2;
        this.category_3 = category_3;
        this.linkToOrigin = linkToOrigin;
        this.id = id;
        this.locationCoordinates = locationCoordinates;
        this.requestCoordinates = requestCoordinates;
        this.distance = distance;
        this.eventScore = eventScore;
        this.requester = requester;

    }

    public String getLocationCoordinates() {
        return locationCoordinates;
    }

    public void setLocationCoordinates(String locationCoordinates) {
        this.locationCoordinates = locationCoordinates;
    }


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

    public String getCategory_1() {
        return category_1;
    }

    public void setCategory_1(String category_1) {
        this.category_1 = category_1;
    }

    public String getCategory_2() {
        return category_2;
    }

    public void setCategory_2(String category_2) {
        this.category_2 = category_2;
    }

    public String getCategory_3() {
        return category_3;
    }

    public void setCategory_3(String category_3) {
        this.category_3 = category_3;
    }

    public String getLinkToOrigin() {
        return linkToOrigin;
    }

    public void setLinkToOrigin(String linkToOrigin) {
        this.linkToOrigin = linkToOrigin;
    }

    public int getId() {
        return id;
    }

    public String toString() {
        return this.name + "\n" + this.venueName;
    }
}
