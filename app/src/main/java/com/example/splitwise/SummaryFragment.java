package com.example.splitwise;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SummaryFragment extends Fragment {

    private TextView txtTotalAmount;
    private PieChart pieChart;
    private RecyclerView rvCategorySummary;
    private CategorySummaryAdapter adapter;
    private List<CategorySummary> summaryList;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtTotalAmount = view.findViewById(R.id.txtTotalAmount);
        pieChart = view.findViewById(R.id.pieChart);
        rvCategorySummary = view.findViewById(R.id.rvCategorySummary);

        summaryList = new ArrayList<>();
        adapter = new CategorySummaryAdapter(summaryList);
        rvCategorySummary.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCategorySummary.setAdapter(adapter);

        setupPieChart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference()
                    .child("expenses")
                    .child(user.getUid());
            
            fetchExpensesAndCalculate();
        }
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(12f);
    }

    private void fetchExpensesAndCalculate() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double total = 0;
                Map<String, Double> categoryMap = new HashMap<>();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Expense expense = data.getValue(Expense.class);
                    if (expense != null) {
                        total += expense.getAmount();
                        String cat = expense.getCategory();
                        categoryMap.put(cat, categoryMap.getOrDefault(cat, 0.0) + expense.getAmount());
                    }
                }

                txtTotalAmount.setText(String.format(Locale.getDefault(), "$%.2f", total));
                updateSummaryData(categoryMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateSummaryData(Map<String, Double> categoryMap) {
        summaryList.clear();
        List<PieEntry> entries = new ArrayList<>();

        for (Map.Entry<String, Double> entry : categoryMap.entrySet()) {
            summaryList.add(new CategorySummary(entry.getKey(), entry.getValue()));
            entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
        }

        adapter.notifyDataSetChanged();

        PieDataSet dataSet = new PieDataSet(entries, "Categories");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);

        pieChart.setData(data);
        pieChart.invalidate();
    }

    private static class CategorySummaryAdapter extends RecyclerView.Adapter<CategorySummaryAdapter.ViewHolder> {
        private final List<CategorySummary> list;

        CategorySummaryAdapter(List<CategorySummary> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_summary, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CategorySummary item = list.get(position);
            holder.txtName.setText(item.getCategory());
            holder.txtAmount.setText(String.format(Locale.getDefault(), "$%.2f", item.getAmount()));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView txtName, txtAmount;
            ViewHolder(View itemView) {
                super(itemView);
                txtName = itemView.findViewById(R.id.txtCategoryName);
                txtAmount = itemView.findViewById(R.id.txtCategoryAmount);
            }
        }
    }
}
