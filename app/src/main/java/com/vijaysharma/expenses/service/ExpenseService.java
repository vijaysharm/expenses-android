package com.vijaysharma.expenses.service;

import java.util.Date;
import java.util.List;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface ExpenseService {
    @GET("/expenses")
    public Observable<List<Expense>> getExpenses(
        @Query("token") String token
    );

    @GET("/expenses/{id}")
    public Observable<Expense> getExpense(
        @Path("id") int expenseId,
        @Query("token") String token
    );

    @POST("/expenses")
    public Observable<Expense> addExpense(
        @Query("token") String token,
        @Body Expense expense
    );

    @PUT("/expenses/{id}")
    public Observable<Expense> updateExpense(
        @Path("id") int expenseId,
        @Query("token") String token,
        @Body Expense expense
    );

    @DELETE("/expenses/{id}")
    public Observable<Void> deleteExpense(
        @Path("id") int expenseId,
        @Query("token") String token
    );

    public static class Expense {
        private final String _id;
        private final String comment;
        private final String description;
        private final double amount;
        private final Date date;

        public Expense(String id, String description, String comment, double amount, Date date) {
            this._id = id;
            this.description = description;
            this.comment = comment;
            this.amount = amount;
            this.date = date;
        }

        public String getId() {
            return _id;
        }

        public String getDescription() {
            return description;
        }

        public String getComment() {
            return comment;
        }

        public double getAmount() {
            return amount;
        }

        public Date getDate() {
            return date;
        }

        @Override
        public String toString() {
            return "Expense {" +
                    "id='" + _id + '\'' +
                    ", comment='" + comment + '\'' +
                    ", description='" + description + '\'' +
                    ", amount=" + amount +
                    ", date=" + date +
                    '}';
        }
    }
}
