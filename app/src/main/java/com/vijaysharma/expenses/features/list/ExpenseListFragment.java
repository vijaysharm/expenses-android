package com.vijaysharma.expenses.features.list;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.vijaysharma.expenses.Constants;
import com.vijaysharma.expenses.misc.ObserverAdapter;
import com.vijaysharma.expenses.R;
import com.vijaysharma.expenses.database.models.Expense;

import java.util.List;

import rx.subscriptions.CompositeSubscription;

public class ExpenseListFragment extends Fragment {
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String id);
    }

    private OnFragmentInteractionListener mListener;
    private ExpenseListAdapter adapter;
    private SharedPreferences preferences;
    private ListView listView;
    private ExpenseListOperations operations;
    private ObserverAdapter<List<Expense>> fetchItems;
    private ObserverAdapter<Expense> newItem;
    private ObserverAdapter<Expense> updatedItem;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        operations = new ExpenseListOperations(getActivity());
        adapter = new ExpenseListAdapter(getActivity());
        fetchItems = new ObserverAdapter<List<Expense>>() {
            @Override
            public void onNext(List<Expense> expenses) {
                Log.d("TAG", "All Expenses: " + expenses);
                adapter.clear();
                adapter.addAll(expenses);
            }
        };
        newItem = new ObserverAdapter<Expense>() {
            @Override
            public void onNext(Expense expense) {
                Log.d("TAG", "Added Expense: " + expense);
                adapter.add(expense);
            }
        };

       updatedItem = new ObserverAdapter<Expense>() {
            @Override
            public void onNext(Expense expense) {
                Log.d("TAG", "Updated Expense: " + expense);
                subscriptions.add(operations.fetch().subscribe(fetchItems));
            }
        };
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_expense, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == R.id.refresh) {
            String token = preferences.getString(Constants.TOKEN_KEY, null);
            subscriptions.add(operations.refresh(token));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_expense, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        listView = (ListView) rootView.findViewById(R.id.listview);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if ( activity instanceof OnFragmentInteractionListener ) {
            mListener = (OnFragmentInteractionListener) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        subscriptions.add(operations.fetch().subscribe(fetchItems));
        subscriptions.add(operations.newItem().subscribe(newItem));
        subscriptions.add(operations.updatedItem().subscribe(updatedItem));
    }

    @Override
    public void onPause() {
        super.onPause();
        subscriptions.unsubscribe();
    }
}
