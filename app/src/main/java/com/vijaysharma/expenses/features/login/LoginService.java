package com.vijaysharma.expenses.features.login;

import com.vijaysharma.expenses.service.AuthenticationService;
import com.vijaysharma.expenses.service.AuthenticationService.Token;
import com.vijaysharma.expenses.service.AuthenticationService.Credentials;

import retrofit.RestAdapter;
import rx.Observable;

public class LoginService {
    private final AuthenticationService service;
    public LoginService(String endpoint) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .build();
        service = restAdapter.create(AuthenticationService.class);
    }

    public Observable<Token> login(String username, String password) {
        return service.login(new Credentials(username, password));
    }
}
