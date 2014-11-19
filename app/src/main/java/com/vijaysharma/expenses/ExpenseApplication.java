package com.vijaysharma.expenses;

import android.app.Application;

import com.vijaysharma.expenses.features.login.LoginController;
import com.vijaysharma.expenses.features.login.LoginOperations;
import com.vijaysharma.expenses.misc.ObjectFactory;
import com.vijaysharma.expenses.misc.ObjectFactory.Factory;

import java.util.HashMap;
import java.util.Map;

public class ExpenseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Map<Class<?>, Object> singletons = new HashMap<Class<?>, Object>();
        LoginController loginController = new LoginController(new LoginOperations(this.getApplicationContext()));
        singletons.put(loginController.getClass(), loginController);

        Map<Class<?>, Factory> factories = new HashMap<Class<?>, Factory>();

        ObjectFactory.setInstance(new ObjectFactory(singletons, factories));
    }
}
