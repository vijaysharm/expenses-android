package com.vijaysharma.expenses;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.vijaysharma.expenses.database.ExpenseDatabase;
import com.vijaysharma.expenses.features.list.ExpenseListController;
import com.vijaysharma.expenses.features.list.ExpenseListOperations;
import com.vijaysharma.expenses.features.list.ExpenseListService;
import com.vijaysharma.expenses.features.list.ExpenseListStorage;
import com.vijaysharma.expenses.features.login.LoginController;
import com.vijaysharma.expenses.features.login.LoginOperations;
import com.vijaysharma.expenses.features.login.LoginService;
import com.vijaysharma.expenses.features.login.LoginStorage;
import com.vijaysharma.expenses.misc.ObjectFactory;
import com.vijaysharma.expenses.misc.ObjectFactory.Factory;

import java.util.HashMap;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;

public class ExpenseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Context context = getApplicationContext();
        String host = Constants.HOST;

        Map<Class<?>, Object> singletons = new HashMap<Class<?>, Object>();
        singletons.put(LoginController.class, createLoginController(host, preferences));
        singletons.put(ExpenseListController.class, createExpenseListController(host, context, preferences));

        Map<Class<?>, Factory> factories = new HashMap<Class<?>, Factory>();

        ObjectFactory.setInstance(new ObjectFactory(singletons, factories));
    }

    private LoginController createLoginController(String host, SharedPreferences preferences) {
        LoginService service = new LoginService(host);
        LoginStorage storage = new LoginStorage(preferences);
        LoginOperations operations = new LoginOperations(service, storage, AndroidSchedulers.mainThread());
        return new LoginController(operations);
    }

    private ExpenseListController createExpenseListController(String host, Context context, SharedPreferences preferences) {
        ExpenseDatabase database = new ExpenseDatabase(context);
        ExpenseListStorage storage = new ExpenseListStorage(database, preferences);
        ExpenseListService service = new ExpenseListService(host);
        ExpenseListOperations operations = new ExpenseListOperations(storage, service, AndroidSchedulers.mainThread());

        return new ExpenseListController(operations);
    }
}
