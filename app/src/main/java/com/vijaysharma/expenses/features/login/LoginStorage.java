package com.vijaysharma.expenses.features.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.vijaysharma.expenses.Constants;
import com.vijaysharma.expenses.misc.ObserverAdapter;
import com.vijaysharma.expenses.service.AuthenticationService.Token;

import rx.Observer;

public class LoginStorage {
    private final SharedPreferences preferences;
    public LoginStorage(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public Observer<Token> save() {
        return new ObserverAdapter<Token>() {
            @Override
            public void onNext(Token token) {
                SharedPreferences.Editor editor = preferences.edit();
                if ( token == null ) {
                    editor.remove(Constants.TOKEN_KEY);
                }
                else {
                    editor.putString(Constants.TOKEN_KEY, token.getToken());
                    editor.putString(Constants.USERNAME_KEY, token.getUsername());
                }
                editor.commit();
            }
        };
    }
}
