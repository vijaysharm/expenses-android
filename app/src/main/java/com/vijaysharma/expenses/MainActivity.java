package com.vijaysharma.expenses;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vijaysharma.expenses.misc.ISODateAdapter;
import com.vijaysharma.expenses.service.AuthenticationService;
import com.vijaysharma.expenses.service.AuthenticationService.Credentials;
import com.vijaysharma.expenses.service.AuthenticationService.Token;
import com.vijaysharma.expenses.service.ExpenseService;
import com.vijaysharma.expenses.service.ExpenseService.Expense;

import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import rx.Observable;
import rx.android.events.OnClickEvent;
import rx.android.observables.ViewObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

public class MainActivity extends Activity {
    private static final String HOST = "http://192.168.0.25:5000";
    private static final String TOKEN_KEY = "token";

    @InjectView(R.id.login) Button loginButton;
    @InjectView(R.id.fetch) Button fetchButton;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ButterKnife.inject(this);

        ViewObservable.clicks(loginButton)
            .flatMap(new Func1<OnClickEvent, Observable<Token>>() {
                @Override
                public Observable<Token> call(OnClickEvent onClickEvent) {
                    RestAdapter restAdapter = new RestAdapter.Builder()
                            .setEndpoint(HOST)
                            .build();
                    AuthenticationService service = restAdapter.create(AuthenticationService.class);
                    Credentials credentials = new Credentials("vijay", "password");
                    return service.login(credentials);
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new ObservableAdapter<Token>() {
                @Override
                public void onNext(Token token) {
                    Log.i("TAG", "Logged in: " + token);
                    preferences.edit().putString(TOKEN_KEY, token.getToken()).commit();
                }

                @Override
                public void onError(Throwable e) {
                    Log.e("TAG", "Failed to login", e);
                }
            });

        ViewObservable.clicks(fetchButton)
            .flatMap(new Func1<OnClickEvent, Observable<List<Expense>>>() {
                @Override
                public Observable<List<Expense>> call(OnClickEvent onClickEvent) {
                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(Date.class, new ISODateAdapter())
                            .create();
                    RestAdapter restAdapter = new RestAdapter.Builder()
                            .setConverter(new GsonConverter(gson))
                            .setEndpoint(HOST)
                            .build();
                    ExpenseService service = restAdapter.create(ExpenseService.class);
                    String token = preferences.getString(TOKEN_KEY, null);
                    return service.getExpenses(token);
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new ObservableAdapter<List<Expense>>() {
                @Override
                public void onNext(List<Expense> expenses) {
                    Log.i("TAG", "Got: " + expenses);
                }

                @Override
                public void onError(Throwable e) {
                    Log.e("TAG", "Failed to fetch", e);
                }
            });
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
}
