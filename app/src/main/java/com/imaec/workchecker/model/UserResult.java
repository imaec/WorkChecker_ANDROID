package com.imaec.workchecker.model;

import java.util.ArrayList;

/**
 * Created by imaec on 2018-06-24.
 */

public class UserResult {

    private String msg;
    private ArrayList<UserInfo> result;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ArrayList<UserInfo> getResult() {
        return result;
    }

    public void setResult(ArrayList<UserInfo> result) {
        this.result = result;
    }
}
