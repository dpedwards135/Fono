package com.davidparkeredwards.fono.data;

import android.util.Log;

import com.davidparkeredwards.fono.FONO;
import com.davidparkeredwards.fono.FonoEvent;

/**
 * Created by User on 8/18/2016.
 */
public class FonoEventScored extends FonoEvent {

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
    String requester;
    double eventScore;
    double distance;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDate() {
        return date;
    }

    @Override
    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String getVenueName() {
        return venueName;
    }

    @Override
    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getCategory_1() {
        return category_1;
    }

    @Override
    public void setCategory_1(String category_1) {
        this.category_1 = category_1;
    }

    @Override
    public String getCategory_2() {
        return category_2;
    }

    @Override
    public void setCategory_2(String category_2) {
        this.category_2 = category_2;
    }

    @Override
    public String getCategory_3() {
        return category_3;
    }

    @Override
    public void setCategory_3(String category_3) {
        this.category_3 = category_3;
    }

    @Override
    public String getLinkToOrigin() {
        return linkToOrigin;
    }

    @Override
    public void setLinkToOrigin(String linkToOrigin) {
        this.linkToOrigin = linkToOrigin;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getLocationCoordinates() {
        return locationCoordinates;
    }

    @Override
    public void setLocationCoordinates(String locationCoordinates) {
        this.locationCoordinates = locationCoordinates;
    }

    @Override
    public String getRequestCoordinates() {
        return requestCoordinates;
    }

    @Override
    public void setRequestCoordinates(String requestCoordinates) {
        this.requestCoordinates = requestCoordinates;
    }

    @Override
    public String getRequester() {
        return requester;
    }

    @Override
    public void setRequester(String requester) {
        this.requester = requester;
    }

    public double getEventScore() {
        return eventScore;
    }

    public void setEventScore(double eventScore) {
        this.eventScore = eventScore;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }




    public FonoEventScored(String name, String date, String venueName, String address, String description,
                     String category_1, String category_2, String category_3, String linkToOrigin,
                     int id, String locationCoordinates, String requestCoordinates, String requester) {//, String eventScore, String distance) {
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
        this.requester = requester;

        EventScorer eventScorer = new EventScorer();

        this.distance = eventScorer.calculateDistance(locationCoordinates, requestCoordinates);
        this.eventScore = eventScorer.scoreEvents(FONO.getContext(), distance, category_1,
                category_2, category_3, description);
    }

    public String toString() {
        return this.name + "\n    " + this.venueName + "\n    " + Math.ceil((this.distance*100)/100) + " miles";
    }
}