package com.vijaysharma.expenses;

import rx.Observer;

public class ObservableAdapter<T> implements Observer<T> {
    @Override public void onCompleted() {}
    @Override public void onError(Throwable e) {}
    @Override public void onNext(T t) {}
}
