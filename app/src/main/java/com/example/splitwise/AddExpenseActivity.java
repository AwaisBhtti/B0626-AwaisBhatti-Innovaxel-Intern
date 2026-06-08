package com.example.splitwise;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etAmount, etDate, etNotes;
    private Spinner spinnerCategory;
    private Button btnSave;
    private TextView txtPageTitle;
    private DatabaseReference mDatabase;
    private final String[] categories = {"Food", "Utilities", "Transport", "Shopping", "Entertainment", "Health", "Other"};
    private Expense existingExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        txtPageTitle = findViewById(R.id.txtPageTitle);
        etTitle = findViewById(R.id.etTitle);
        etAmount = findViewById(R.id.etAmount);
        etDate = findViewById(R.id.etDate);
        etNotes = findViewById(R.id.etNotes);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSave = findViewById(R.id.btnSaveExpense);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("expenses")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        etDate.setOnClickListener(v -> showDatePicker());

        // Check if we are editing an existing expense
        existingExpense = (Expense) getIntent().getSerializableExtra("expense");
        if (existingExpense != null) {
            txtPageTitle.setText("Edit Expense");
            btnSave.setText("Update Expense");
            preFillData();
        }

        btnSave.setOnClickListener(v -> saveExpense());
    }

    private void preFillData() {
        etTitle.setText(existingExpense.getTitle());
        etAmount.setText(String.valueOf(existingExpense.getAmount()));
        etDate.setText(existingExpense.getDate());
        etNotes.setText(existingExpense.getNotes());
        
        int categoryIndex = Arrays.asList(categories).indexOf(existingExpense.getCategory());
        if (categoryIndex >= 0) {
            spinnerCategory.setSelection(categoryIndex);
        }
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> etDate.setText(String.format(Locale.getDefault(), "%d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth)),
                year, month, day);
        datePickerDialog.show();
    }

    private void saveExpense() {
        String title = etTitle.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        if (title.isEmpty() || amountStr.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        String id = (existingExpense != null) ? existingExpense.getId() : mDatabase.push().getKey();
        Expense expense = new Expense(id, title, amount, category, date, notes);

        if (id != null) {
            mDatabase.child(id).setValue(expense).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String msg = (existingExpense != null) ? "Expense Updated" : "Expense Saved";
                    Toast.makeText(AddExpenseActivity.this, msg, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddExpenseActivity.this, "Operation failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
