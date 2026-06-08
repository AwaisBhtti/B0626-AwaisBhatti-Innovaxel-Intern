package com.example.splitwise;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvExpenses;
    private ExpenseAdapter adapter;
    private List<Expense> expenseList;
    private DatabaseReference mDatabase;
    private FloatingActionButton fabAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvExpenses = view.findViewById(R.id.rvExpenses);
        fabAdd = view.findViewById(R.id.fabAddExpense);

        expenseList = new ArrayList<>();
        adapter = new ExpenseAdapter(expenseList);
        rvExpenses.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExpenses.setAdapter(adapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference()
                    .child("expenses")
                    .child(user.getUid());
            
            fetchExpenses();
        }

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddExpenseActivity.class);
            startActivity(intent);
        });
    }

    private void fetchExpenses() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                expenseList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Expense expense = data.getValue(Expense.class);
                    if (expense != null) {
                        expenseList.add(expense);
                    }
                }
                
                // Sort by date (most recent first)
                // Assuming date format is YYYY-MM-DD for correct lexicographical sorting
                Collections.sort(expenseList, (e1, e2) -> e2.getDate().compareTo(e1.getDate()));
                
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
