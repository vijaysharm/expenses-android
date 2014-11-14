package com.vijaysharma.expenses.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vijaysharma.expenses.Constants;
import com.vijaysharma.expenses.misc.ISODateAdapter;
import com.vijaysharma.expenses.service.AuthenticationService.Credentials;
import com.vijaysharma.expenses.service.AuthenticationService.Token;

import java.util.Date;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import rx.Observable;

public class Service {
    public static Observable<Token> login(String username, String password) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.HOST)
                .build();
        final AuthenticationService service = restAdapter.create(AuthenticationService.class);
        final Credentials credentials = new Credentials(username, password);

        return service.login(credentials);
    }

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
