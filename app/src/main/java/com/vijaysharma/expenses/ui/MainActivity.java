package com.vijaysharma.expenses.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.vijaysharma.expenses.Constants;
import com.vijaysharma.expenses.ObserverAdapter;
import com.vijaysharma.expenses.R;
import com.vijaysharma.expenses.service.AuthenticationService;

import rx.subjects.PublishSubject;

public class MainActivity extends Activity implements LoginFragment.Callback {
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(android.R.id.content, LoginFragment.newInstance("vijay"))
                    .commit();
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
    public void subscribe(PublishSubject<AuthenticationService.Token> token) {
        token.subscribe(new ObserverAdapter<AuthenticationService.Token>() {
            @Override
            public void onNext(AuthenticationService.Token token) {
                SharedPreferences.Editor editor = preferences.edit();
                if ( token == null ) {
                    editor.remove(Constants.TOKEN_KEY);
                } else {
                    editor.putString(Constants.TOKEN_KEY, token.getToken());
                    Log.i("Tag", "Logged in: " + token);
                }
                editor.commit();
            }
        });
    }
}
