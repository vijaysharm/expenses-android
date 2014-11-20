package com.vijaysharma.expenses.features.list;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.vijaysharma.expenses.misc.ISODateAdapter;
import com.vijaysharma.expenses.service.ExpenseService;

import java.util.Date;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import rx.Observable;

public class ExpenseListService {
    private final ExpenseService service;

    public ExpenseListService(String host) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new ISODateAdapter())
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setConverter(new GsonConverter(gson))
                .setEndpoint(host)
                .build();
        service = restAdapter.create(ExpenseService.class);
    }

    public Observable<List<ExpenseService.Expense>> fetch(String token) {
        return service.getExpenses(token);
    }
}
