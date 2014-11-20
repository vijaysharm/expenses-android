package com.vijaysharma.expenses.features.list;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.vijaysharma.expenses.R;
import com.vijaysharma.expenses.database.models.Expense;
import com.vijaysharma.expenses.misc.ObjectFactory;

import java.util.List;

public class ExpenseListFragment extends Fragment implements ExpenseListController.ExpenseListView {
    public interface Callback {
        public void onExpenseSelect(Expense expense);
        public void onExpenseAdd();
    }

    private Callback mListener;
    private ExpenseListAdapter adapter;
    private ExpenseListController controller;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        controller = ObjectFactory.singleton(ExpenseListController.class);
        adapter = new ExpenseListAdapter(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_expense_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == R.id.refresh) {
            controller.refresh();
            return true;
        }

        if (item.getItemId() == R.id.add) {
            if (mListener != null) {
                mListener.onExpenseAdd();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_expense, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if ( mListener != null )
                    mListener.onExpenseSelect(adapter.getItem(position));
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if ( activity instanceof Callback) {
            mListener = (Callback) activity;
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

        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        getFragmentManager().invalidateOptionsMenu();

        controller.attach(this);
        controller.fetch();
    }

    @Override
    public void onPause() {
        super.onPause();
        controller.detach(this);
    }

    @Override
    public void showError() {
        Toast.makeText(getActivity(), "Failed to get new things...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void add(Expense expense) {
        adapter.add(expense);
    }

    @Override
    public void refresh(List<Expense> expenses) {
        adapter.clear();
        adapter.addAll(expenses);
    }
}
