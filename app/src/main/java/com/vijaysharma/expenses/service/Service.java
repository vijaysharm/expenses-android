package com.vijaysharma.expenses.service;

import com.vijaysharma.expenses.Constants;
import com.vijaysharma.expenses.service.AuthenticationService.Credentials;
import com.vijaysharma.expenses.service.AuthenticationService.Token;

import retrofit.RestAdapter;
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
}
