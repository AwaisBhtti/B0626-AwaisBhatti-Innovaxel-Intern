package com.example.splitwise;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<Expense> expenseList;
    private OnExpenseActionListener listener;

    public interface OnExpenseActionListener {
        void onEdit(Expense expense);
        void onDelete(Expense expense);
    }

    public ExpenseAdapter(List<Expense> expenseList) {
        this(expenseList, null);
    }

    public ExpenseAdapter(List<Expense> expenseList, OnExpenseActionListener listener) {
        this.expenseList = expenseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.txtTitle.setText(expense.getTitle());
        holder.txtCategory.setText(expense.getCategory());
        holder.txtDate.setText(expense.getDate());
        holder.txtAmount.setText(String.format(Locale.getDefault(), "$%.2f", expense.getAmount()));

        if (listener != null) {
            holder.btnEdit.setOnClickListener(v -> listener.onEdit(expense));
            holder.btnDelete.setOnClickListener(v -> listener.onDelete(expense));
            holder.layoutActions.setVisibility(View.VISIBLE);
        } else {
            holder.layoutActions.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtCategory, txtDate, txtAmount;
        ImageButton btnEdit, btnDelete;
        View layoutActions;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtExpenseTitle);
            txtCategory = itemView.findViewById(R.id.txtExpenseCategory);
            txtDate = itemView.findViewById(R.id.txtExpenseDate);
            txtAmount = itemView.findViewById(R.id.txtExpenseAmount);
            btnEdit = itemView.findViewById(R.id.btnEditExpense);
            btnDelete = itemView.findViewById(R.id.btnDeleteExpense);
            layoutActions = itemView.findViewById(R.id.layoutActions);
        }
    }
}
