package com.vijaysharma.expenses.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vijaysharma.expenses.Constants;
import com.vijaysharma.expenses.misc.ISODateAdapter;

import java.util.Date;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import rx.Observable;

public class Service {
    public static Observable<List<ExpenseService.Expense>> fetch(String token) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new ISODateAdapter())
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
            .setConverter(new GsonConverter(gson))
            .setEndpoint(Constants.HOST)
            .build();
        ExpenseService service = restAdapter.create(ExpenseService.class);

        return service.getExpenses(token);
    }
}
