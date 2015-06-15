package com.freelancer.whatsbeef.beans;


public class Programs {

    private String name;
    private String start_time;
    private String end_time;
    private String channel;
    private String rating;

    public Programs() {

    }

    public Programs(String name, String start_time, String end_time, String channel, String rating) {
        this.name = name;
        this.start_time = start_time;
        this.end_time = end_time;
        this.channel = channel;
        this.rating = rating;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
