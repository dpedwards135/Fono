package com.davidparkeredwards.fono.data;

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
}
