package com.vijaysharma.expenses.ui;

import android.app.Activity;
import android.app.Fragment;
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
import com.vijaysharma.expenses.misc.ObserverAdapter;
import com.vijaysharma.expenses.service.AuthenticationService;

import rx.subjects.PublishSubject;

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
        gotoPage(EditExpenseFragment.newInstance(expense));
    }

    @Override
    public void onExpenseAdd() {
        gotoPage(EditExpenseFragment.newInstance(null));
    }

    @Override
    public void subscribe(PublishSubject<AuthenticationService.Token> token) {
        token.subscribe(new ObserverAdapter<AuthenticationService.Token>() {
            @Override
            public void onNext(AuthenticationService.Token token) {
                SharedPreferences.Editor editor = preferences.edit();
                if ( token == null ) {
                    editor.remove(Constants.TOKEN_KEY);
                    editor.commit();
                } else {
                    editor.putString(Constants.TOKEN_KEY, token.getToken());
                    editor.commit();
                    gotoExpense();
                }
            }
        });
    }

    private void gotoExpense() {
        gotoPage(new ExpenseListFragment());
    }

    private void gotoLogin() {
        gotoPage(LoginFragment.newInstance("vijay"));
    }

    private void gotoPage(Fragment fragment) {
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, fragment)
                .commit();
    }
}
