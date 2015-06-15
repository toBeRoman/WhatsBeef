package com.freelancer.whatsbeef.beans;


public class Response {

    private Object results;
    private int count;

    public Object getResults() {
        return results;
    }

    public void setResults(Object results) {
        this.results = results;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Response() {

    }

    public Response(Object results, int count) {
        this.results = results;
        this.count = count;

    }

}
