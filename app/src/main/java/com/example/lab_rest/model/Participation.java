package com.example.lab_rest.model;

public class Participation {

    private int participation_id;

    private int event_id;


    public int getParticipation_id() {
        return participation_id;
    }

    public void setParticipation_id(int participation_id) {
        this.participation_id = participation_id;
    }

    public int getEvent_id() {
        return event_id;
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getParticipation_date() {
        return participation_date;
    }

    public void setParticipation_date(String participation_date) {
        this.participation_date = participation_date;
    }

    private Event event;

    private int user_id;
    private User user;

    private String participation_date;


    public Participation(int participation_id, int event_id, Event event, int user_id, User user, String participation_date) {
        this.participation_id = participation_id;
        this.event_id = event_id;
        this.event = event;
        this.user_id = user_id;
        this.user = user;
        this.participation_date = participation_date;
    }
    @Override
    public String toString() {
        return "Participation{" +
                "participation_id=" + participation_id +
                ", event_id=" + event_id +
                ", event=" + event +
                ", user_id=" + user_id +
                ", user=" + user +
                ", participation_date='" + participation_date + '\'' +
                '}';
    }

}
