package com.vijaysharma.expenses.features.edit;

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
import android.widget.EditText;
import android.widget.Toast;

import com.vijaysharma.expenses.R;
import com.vijaysharma.expenses.database.models.Expense;
import com.vijaysharma.expenses.misc.ObserverAdapter;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.subscriptions.CompositeSubscription;

public class EditExpenseFragment extends Fragment {
    public interface Callback {
        public void onEditingComplete();
    }

    private static final String EXPENSE_ID_PARAM = "param1";

    @InjectView(R.id.comment) EditText comment;
    @InjectView(R.id.amount) EditText amount;
    @InjectView(R.id.description) EditText description;
    @InjectView(R.id.date) EditText date;

    private EditExpenseOperations operations;
    private long expenseId;
    private Callback callback;
    private CompositeSubscription subscriptions;
    private ObserverAdapter<Expense> fetch;
    private ObserverAdapter<Throwable> errors;
    private ObserverAdapter<Expense> saved;

    public static EditExpenseFragment newInstance(Expense expense) {
        EditExpenseFragment fragment = new EditExpenseFragment();
        if ( expense != null ) {
            Bundle args = new Bundle();
            args.putLong(EXPENSE_ID_PARAM, expense.getId());
            fragment.setArguments(args);
        }

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        getFragmentManager().invalidateOptionsMenu();

        if (getArguments() != null) { expenseId = getArguments().getLong(EXPENSE_ID_PARAM); }
        operations = new EditExpenseOperations(getActivity());
        subscriptions = new CompositeSubscription();
        fetch = new ObserverAdapter<Expense>() {
            @Override
            public void onNext(Expense expense) {
                comment.setText(expense.getComment());
                description.setText(expense.getDescription());
                amount.setText(String.valueOf(expense.getAmount()));
                date.setText(expense.getDate().toString());
            }
        };
        saved = new ObserverAdapter<Expense>() {
            @Override
            public void onNext(Expense expense) {
                if ( callback != null ) callback.onEditingComplete();
            }
        };
        errors = new ObserverAdapter<Throwable>() {
            @Override
            public void onNext(Throwable throwable) {
                Toast.makeText(getActivity(), "Failed to save expense", Toast.LENGTH_SHORT);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_expense, container, false);
        ButterKnife.inject(this, view);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callback = (Callback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement Callback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isEditMode()) subscriptions.add(operations.fetch(expenseId).subscribe(fetch));
        subscriptions.add(operations.saved().subscribe(saved));
        subscriptions.add(operations.errors().subscribe(errors));
    }

    @Override
    public void onPause() {
        super.onPause();
        subscriptions.unsubscribe();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_expense_edit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == R.id.save) {
            Expense expense = new Expense();
            expense.setComment(comment.getText().toString());
            expense.setDescription(description.getText().toString());
            expense.setDate(new Date());
            expense.setAmount(Double.parseDouble(amount.getText().toString()));
            if (isEditMode()) {
                expense.setLocalState(Expense.UPDATED);
                subscriptions.add(operations.update(expenseId, expense));
            } else {
                expense.setLocalState(Expense.NEW);
                subscriptions.add(operations.save(expense));
            }
            return true;
        }

        if (item.getItemId() == android.R.id.home) {
            if ( callback != null ) callback.onEditingComplete();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isEditMode() {
        return expenseId != 0L;
    }
}
