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
import rx.subscriptions.CompositeSubscription;

public class LoginFragment extends Fragment {
    public interface Callback {
        public void onLoginComplete(Token token);
    }

    private static final String USERNAME_KEY = "username";

    @InjectView(R.id.username) EditText usernameEditText;
    @InjectView(R.id.password) EditText passwordEditText;
    @InjectView(R.id.login) Button loginButton;
    @InjectView(R.id.error) TextView error;

    private String username;
    private CompositeSubscription subscriptions;
    private LoginOperations operations;
    private Callback callback;
    private ObserverAdapter<Throwable> errors;
    private ObserverAdapter<Token> login;

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
        operations = new LoginOperations(getActivity());
        subscriptions = new CompositeSubscription();
        login = new ObserverAdapter<Token>() {
            @Override
            public void onNext(Token token) {
                usernameEditText.setEnabled(true);
                passwordEditText.setEnabled(true);
                loginButton.setEnabled(true);

                callback.onLoginComplete(token);
            }
        };
        errors = new ObserverAdapter<Throwable>() {
            @Override
            public void onNext(Throwable throwable) {
                usernameEditText.setEnabled(true);
                passwordEditText.setEnabled(true);
                loginButton.setEnabled(true);

                error.setVisibility(View.VISIBLE);
                error.setText("Invalid username or password");
            }
        };
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
                    subscriptions.add(operations.doLogin(
                        usernameEditText.getText().toString(),
                        passwordEditText.getText().toString()
                    ));
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
        subscriptions.add(operations.sucess().subscribe(login));
        subscriptions.add(operations.errors().subscribe(errors));
    }

    @Override
    public void onPause() {
        super.onPause();
        subscriptions.unsubscribe();
    }
}
