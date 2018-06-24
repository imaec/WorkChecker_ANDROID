package com.imaec.workchecker.model;

import java.util.ArrayList;

/**
 * Created by imaec on 2018-06-24.
 */

public class WorkResult {

    private String msg;
    private ArrayList<Work> result;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ArrayList<Work> getResult() {
        return result;
    }

    public void setResult(ArrayList<Work> result) {
        this.result = result;
    }
}
