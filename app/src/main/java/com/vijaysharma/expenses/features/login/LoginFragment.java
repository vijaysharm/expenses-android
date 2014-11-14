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

import com.vijaysharma.expenses.misc.ObserverAdapter;
import com.vijaysharma.expenses.R;
import com.vijaysharma.expenses.misc.Checks;
import com.vijaysharma.expenses.service.AuthenticationService.Token;
import com.vijaysharma.expenses.service.Service;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.events.OnClickEvent;
import rx.android.events.OnTextChangeEvent;
import rx.android.observables.ViewObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subjects.PublishSubject;

public class LoginFragment extends Fragment {
    public interface Callback {
        void subscribe(PublishSubject<Token> token);
    }

    private static final String USERNAME_KEY = "username";

    @InjectView(R.id.username) EditText usernameEditText;
    @InjectView(R.id.password) EditText passwordEditText;
    @InjectView(R.id.login) Button loginButton;
    @InjectView(R.id.error) TextView error;

    private final PublishSubject<Token> token = PublishSubject.create();
    private String username;
    private Subscription login;

    public static LoginFragment newInstance(String param1) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(USERNAME_KEY, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString(USERNAME_KEY);
        }
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

        login = ViewObservable.clicks(loginButton)
            .doOnEach(new ObserverAdapter<OnClickEvent>() {
                @Override
                public void onNext(OnClickEvent onClickEvent) {
                    usernameEditText.setEnabled(false);
                    passwordEditText.setEnabled(false);
                    loginButton.setEnabled(false);
                    error.setVisibility(View.GONE);
                }
            })
            .flatMap(new Func1<OnClickEvent, Observable<Token>>() {
                @Override
                public Observable<Token> call(OnClickEvent onClickEvent) {
                    return login(
                        usernameEditText.getText().toString(),
                        passwordEditText.getText().toString()
                    );
                }
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(token);

        token.subscribe(new ObserverAdapter<Token>() {
            @Override
            public void onNext(Token token) {
                usernameEditText.setEnabled(true);
                passwordEditText.setEnabled(true);
                loginButton.setEnabled(true);

                if (token == null) {
                    error.setVisibility(View.VISIBLE);
                    error.setText("Invalid username or password");
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof Callback) {
            Callback callback = (Callback) activity;
            callback.subscribe(token);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (login != null) login.unsubscribe();
    }

    private static Observable<Token> login(final String username, final String password) {
        return Observable.create(new Observable.OnSubscribe<Token>() {
            @Override
            public void call(final Subscriber<? super Token> subscriber) {
                Service.login(username, password).subscribe(new ObserverAdapter<Token>() {
                    @Override
                    public void onNext(Token token) {
                        subscriber.onNext(token);
                    }

                    @Override
                    public void onError(Throwable e) {
                        subscriber.onNext(null);
                    }
                });
            }
        });
    }
}
