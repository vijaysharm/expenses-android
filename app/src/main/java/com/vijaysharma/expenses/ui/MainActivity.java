package com.vijaysharma.expenses.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.vijaysharma.expenses.database.models.Expense;
import com.vijaysharma.expenses.features.edit.EditExpenseFragment;
import com.vijaysharma.expenses.features.list.ExpenseListFragment;
import com.vijaysharma.expenses.features.login.LoginFragment;
import com.vijaysharma.expenses.service.AuthenticationService;

public class MainActivity extends Activity
        implements
            LoginFragment.Callback,
            ExpenseListFragment.Callback,
            EditExpenseFragment.Callback {

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();

//        String token = preferences.getString(Constants.TOKEN_KEY, null);
//        if (token == null) {
            gotoLogin();
//        }
//        else {
//            gotoExpense();
//        }
    }

    @Override
    public void onEditingComplete() {
        gotoExpense();
    }

    @Override
    public void onExpenseSelect(Expense expense) {
        gotoPage(EditExpenseFragment.newInstance(expense), true);
    }

    @Override
    public void onExpenseAdd() {
        gotoPage(EditExpenseFragment.newInstance(null), true);
    }

    @Override
    public void onLoginComplete(AuthenticationService.Token token) {
        gotoExpense();
    }

    private void gotoExpense() {
        gotoPage(new ExpenseListFragment(), false);
    }

    private void gotoLogin() {
        gotoPage(LoginFragment.newInstance("vijay"), false);
    }

    private void gotoPage(Fragment fragment, boolean push) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction()
            .replace(android.R.id.content, fragment);

        if (push)
            transaction.addToBackStack(null);

        transaction.commit();
    }
}
