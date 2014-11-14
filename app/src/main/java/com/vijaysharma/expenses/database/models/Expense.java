package com.vijaysharma.expenses.database.models;

import java.util.Date;

public class Expense {
    public Long _id;
    public String description;
    public String comment;
    public double amount;
    public Date date;
    public String serverId;
}
