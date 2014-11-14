package com.vijaysharma.expenses.features.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.vijaysharma.expenses.R;
import com.vijaysharma.expenses.database.models.Expense;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ExpenseListAdapter extends ArrayAdapter<Expense> {
    private final Context context;

    public static class ViewHolder {
        @InjectView(R.id.textView)
        TextView textView;
        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    public ExpenseListAdapter(Context context) {
        super(context, 0, new ArrayList<Expense>());
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if ( convertView  == null ) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_expense, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Expense item = getItem(position);
        holder.textView.setText(item.description);

        return convertView;
    }
}