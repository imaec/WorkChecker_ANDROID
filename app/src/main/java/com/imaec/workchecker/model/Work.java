package com.imaec.workchecker.model;

/**
 * Created by imaec on 2018-06-24.
 */

public class Work {

    private String _id;
    private String user_id;
    private String status;
    private String date;
    private String time_a;
    private String time_b;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime_a() {
        return time_a;
    }

    public void setTime_a(String time_a) {
        this.time_a = time_a;
    }

    public String getTime_b() {
        return time_b;
    }

    public void setTime_b(String time_b) {
        this.time_b = time_b;
    }
}
