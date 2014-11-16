package com.vijaysharma.expenses.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import com.vijaysharma.expenses.Constants;
import com.vijaysharma.expenses.R;
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

        String token = preferences.getString(Constants.TOKEN_KEY, null);
        if (token == null || savedInstanceState == null) {
            gotoLogin();
        }
        else {
            gotoExpense();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
