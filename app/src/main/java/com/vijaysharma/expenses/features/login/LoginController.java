package com.vijaysharma.expenses.features.login;

import com.vijaysharma.expenses.misc.ObserverAdapter;
import com.vijaysharma.expenses.service.AuthenticationService;

import rx.subscriptions.CompositeSubscription;

public class LoginController {
    public interface LoginView {
        void onLoginSuccess(AuthenticationService.Token token);
        void onLoginFail(String reason);
    }

    private final LoginOperations operations;
    private final CompositeSubscription subscriptions;

    private LoginView view;
    private boolean loggingIn;
    private Throwable loginFailed;

    public LoginController(LoginOperations operations) {
        this.loggingIn = false;
        this.loginFailed = null;
        this.operations = operations;
        this.subscriptions = new CompositeSubscription();
        this.subscriptions.add(operations.sucess().subscribe(new ObserverAdapter<AuthenticationService.Token>() {
            @Override
            public void onNext(AuthenticationService.Token token) {
                loggingIn = false;
                loginFailed = null;
                if (view != null) {
                    view.onLoginSuccess(token);
                }
            }
        }));
        this.subscriptions.add(operations.errors().subscribe(new ObserverAdapter<Throwable>() {
            @Override
            public void onNext(Throwable throwable) {
                loggingIn = false;
                loginFailed = throwable;
                if (view != null) {
                    view.onLoginFail("Invalid username or password");
                }
            }
        }));
    }

    public void login(String username, String password) {
        loggingIn = true;
        loginFailed = null;
        subscriptions.add(operations.doLogin(
            username, password
        ));
    }

    public boolean didLoginFail() {
        return loginFailed != null;
    }

    public boolean isLoggingIn() {
        return loggingIn;
    }

    public void attach(LoginView view) {
        this.view = view;
    }

    public void detach(LoginView view) {
        this.view = view;
    }
}
