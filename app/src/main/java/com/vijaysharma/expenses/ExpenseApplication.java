package com.vijaysharma.expenses;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.vijaysharma.expenses.features.login.LoginController;
import com.vijaysharma.expenses.features.login.LoginOperations;
import com.vijaysharma.expenses.features.login.LoginService;
import com.vijaysharma.expenses.features.login.LoginStorage;
import com.vijaysharma.expenses.misc.ObjectFactory;
import com.vijaysharma.expenses.misc.ObjectFactory.Factory;

import java.util.HashMap;
import java.util.Map;

public class ExpenseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Map<Class<?>, Object> singletons = new HashMap<Class<?>, Object>();
        singletons.put(LoginController.class, createLoginController(preferences));

        Map<Class<?>, Factory> factories = new HashMap<Class<?>, Factory>();

        ObjectFactory.setInstance(new ObjectFactory(singletons, factories));
    }

    private LoginController createLoginController(SharedPreferences preferences) {
        LoginService service = new LoginService(Constants.HOST);
        LoginStorage storage = new LoginStorage(preferences);
        LoginOperations operations = new LoginOperations(service, storage);
        return new LoginController(operations);
    }
}
