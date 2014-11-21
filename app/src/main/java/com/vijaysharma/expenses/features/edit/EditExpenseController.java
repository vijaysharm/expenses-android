package com.vijaysharma.expenses.features.edit;

import com.vijaysharma.expenses.database.models.Expense;
import com.vijaysharma.expenses.misc.ObserverAdapter;

import rx.subscriptions.CompositeSubscription;

public class EditExpenseController {
    public interface EditExpenseView {
        void showError(String reason);
        void showExpense(Expense expense);
        void done();
    }

    private final EditExpenseOperations operations;
    private final CompositeSubscription subscriptions;
    private final ObserverAdapter<Expense> fetch;

    private EditExpenseView view;

    public EditExpenseController(EditExpenseOperations operations) {
        this.operations = operations;
        this.subscriptions = new CompositeSubscription();

        fetch = new ObserverAdapter<Expense>() {
            @Override
            public void onNext(Expense expense) {
                if ( view != null )
                    view.showExpense(expense);
            }
        };

        subscriptions.add(operations.saved().subscribe(new ObserverAdapter<Expense>() {
            @Override
            public void onNext(Expense expense) {
                if ( view != null )
                    view.done();
            }
        }));

        subscriptions.add(operations.errors().subscribe(new ObserverAdapter<Throwable>() {
            @Override
            public void onNext(Throwable throwable) {
                if ( view != null )
                    view.showError("Failed to save expense");
            }
        }));
    }

    public void fetch(long expenseId) {
        subscriptions.add(operations.fetch(expenseId).subscribe(fetch));
    }

    public void save(long expenseId, Expense expense) {
        if (isEditMode(expenseId)) {
            expense.setLocalState(Expense.UPDATED);
            subscriptions.add(operations.update(expenseId, expense));
        } else {
            expense.setLocalState(Expense.NEW);
            subscriptions.add(operations.save(expense));
        }
    }

    public void attach(EditExpenseView view) {
        this.view = view;
    }

    public void detach(EditExpenseView view) {
        this.view = null;
    }

    private static boolean isEditMode(long expenseId) {
        return expenseId != 0L;
    }
}
