package com.vijaysharma.expenses.features.login;

import android.content.Context;

import com.vijaysharma.expenses.misc.ObserverAdapter;
import com.vijaysharma.expenses.service.AuthenticationService;
import com.vijaysharma.expenses.service.AuthenticationService.Token;
import com.vijaysharma.expenses.service.Service;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.observers.Observers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class LoginOperations {
    private final LoginStorage storage;
    private final PublishSubject<Throwable> errors;
    private final PublishSubject<Token> success;
    private final Scheduler mainThread;
    private final Scheduler networkThread;

    public LoginOperations(Context context) {
        this.storage = new LoginStorage(context);
        this.errors = PublishSubject.create();
        this.success = PublishSubject.create();
        this.mainThread = AndroidSchedulers.mainThread();
        this.networkThread = Schedulers.io();
    }

    public Observable<Throwable> errors() {
        return errors.observeOn(mainThread);
    }

    public Observable<Token> sucess() {
        return success.observeOn(mainThread);
    }

    public Subscription doLogin(final String username, final String password) {
        return Observable.create(new Observable.OnSubscribe<AuthenticationService.Token>() {
            @Override
            public void call(final Subscriber<? super AuthenticationService.Token> subscriber) {
                try { Thread.sleep(10000); } catch (Exception e){};
                Service.login(username, password).subscribe(new ObserverAdapter<AuthenticationService.Token>() {
                    @Override
                    public void onNext(AuthenticationService.Token token) {
                        success.onNext(token);
                        subscriber.onNext(token);
                    }

                    @Override
                    public void onError(Throwable e) {
                        errors.onNext(e);
                        subscriber.onNext(null);
                    }
                });
            }
        })
        .subscribeOn(networkThread)
        .observeOn(mainThread)
        .doOnEach(storage.save())
        .subscribe(Observers.empty());
    }
}
