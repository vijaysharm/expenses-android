package com.vijaysharma.expenses.database.models;

import java.util.Date;

public class Expense {
    public Long _id;
    public String description;
    public String comment;
    public float amount;
    public Date date;
    public String serverId;
}
