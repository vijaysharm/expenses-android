package com.vijaysharma.expenses.features.login;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.vijaysharma.expenses.R;
import com.vijaysharma.expenses.misc.Checks;
import com.vijaysharma.expenses.misc.ObjectFactory;
import com.vijaysharma.expenses.misc.ObserverAdapter;
import com.vijaysharma.expenses.service.AuthenticationService.Token;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.android.events.OnClickEvent;
import rx.android.events.OnTextChangeEvent;
import rx.android.observables.ViewObservable;
import rx.functions.Func1;
import rx.functions.Func2;

public class LoginFragment extends Fragment implements LoginController.LoginView {
    public interface Callback {
        public void onLoginComplete(Token token);
    }

    private static final String USERNAME_KEY = "username";

    @InjectView(R.id.username) EditText usernameEditText;
    @InjectView(R.id.password) EditText passwordEditText;
    @InjectView(R.id.login) Button loginButton;
    @InjectView(R.id.error) TextView error;

    private String username;
    private Callback callback;
    private LoginController controller;

    public static LoginFragment newInstance(String username) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(USERNAME_KEY, username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) { username = getArguments().getString(USERNAME_KEY); }
        controller = ObjectFactory.singleton(LoginController.class);
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.inject(this, view);

        usernameEditText.setText(username);
        Observable<Boolean> usernameValid = ViewObservable.text(usernameEditText, !username.isEmpty())
            .map(new Func1<OnTextChangeEvent, Boolean>() {
                @Override
                public Boolean call(OnTextChangeEvent onTextChangeEvent) {
                    return Checks.isUsernameValid(onTextChangeEvent.view.getText().toString());
                }
            });

        if ( !username.isEmpty() ) { passwordEditText.requestFocus(); }
        Observable<Boolean> passwordValid = ViewObservable.text(passwordEditText)
            .map(new Func1<OnTextChangeEvent, Boolean>() {
                @Override
                public Boolean call(OnTextChangeEvent onTextChangeEvent) {
                    return Checks.isPasswordValid(onTextChangeEvent.view.getText().toString());
                }
            });

        Observable.combineLatest(usernameValid, passwordValid, new Func2<Boolean, Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean isUsernameValid, Boolean isPasswordValid) {
                return isUsernameValid && isPasswordValid;
            }
        }).subscribe(new ObserverAdapter<Boolean>() {
            @Override
            public void onNext(Boolean enabled) {
                loginButton.setEnabled(enabled);
            }
        });

        ViewObservable.clicks(loginButton)
            .doOnEach(new ObserverAdapter<OnClickEvent>() {
                @Override
                public void onNext(OnClickEvent onClickEvent) {
                    usernameEditText.setEnabled(false);
                    passwordEditText.setEnabled(false);
                    loginButton.setEnabled(false);
                    error.setVisibility(View.GONE);
                }
            }).subscribe(new ObserverAdapter<OnClickEvent>() {
                @Override
                public void onNext(OnClickEvent onClickEvent) {
                    controller.login(
                        usernameEditText.getText().toString(),
                        passwordEditText.getText().toString()
                    );
                }
            });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof Callback) {
            callback = (Callback) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        controller.attach(this);

        usernameEditText.setEnabled(true);
        passwordEditText.setEnabled(true);
        loginButton.setEnabled(true);

        if (controller.isLoggingIn()) {
            usernameEditText.setEnabled(false);
            passwordEditText.setEnabled(false);
            loginButton.setEnabled(false);
            error.setVisibility(View.GONE);
        }

        if(controller.didLoginFail()) {
            usernameEditText.setEnabled(true);
            passwordEditText.setEnabled(true);
            loginButton.setEnabled(true);

            error.setVisibility(View.VISIBLE);
            error.setText("Login Failed");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        controller.detach(this);
    }

    @Override
    public void onLoginSuccess(Token token) {
        usernameEditText.setEnabled(true);
        passwordEditText.setEnabled(true);
        loginButton.setEnabled(true);

        if ( callback != null ) {
            callback.onLoginComplete(token);
        }
    }

    @Override
    public void onLoginFail(String reason) {
        usernameEditText.setEnabled(true);
        passwordEditText.setEnabled(true);
        loginButton.setEnabled(true);

        error.setVisibility(View.VISIBLE);
        error.setText(reason);
    }
}
