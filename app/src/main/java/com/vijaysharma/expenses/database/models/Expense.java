package com.vijaysharma.expenses.database.models;

import java.util.Date;

public class Expense {
    public static final int SYNCHED = 0;
    public static final int NEW = 1;
    public static final int UPDATED = 2;
    public static final int DELETED = 3;

    private Long _id;
    private String description;
    private String comment;
    private double amount;
    private Date date;
    private String serverId;
    private int localState;

    public Long getId() {
        return _id;
    }

    public void setId(Long _id) {
        this._id = _id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public int getLocalState() {
        return localState;
    }

    public void setLocalState(int localState) {
        this.localState = localState;
    }
}
