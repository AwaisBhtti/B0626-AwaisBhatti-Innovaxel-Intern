package com.example.splitwise;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Calendar;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etAmount, etDate;
    private Spinner spinnerCategory;
    private Button btnSave;
    private DatabaseReference mDatabase;
    private final String[] categories = {"Food", "Utilities", "Transport", "Shopping", "Entertainment", "Health", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        etTitle = findViewById(R.id.etTitle);
        etAmount = findViewById(R.id.etAmount);
        etDate = findViewById(R.id.etDate);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSave = findViewById(R.id.btnSaveExpense);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("expenses")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        etDate.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> saveExpense());
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
        String category = spinnerCategory.getSelectedItem().toString();

        if (title.isEmpty() || amountStr.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        String id = mDatabase.push().getKey();
        Expense expense = new Expense(id, title, amount, category, date, "");

        if (id != null) {
            mDatabase.child(id).setValue(expense).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(AddExpenseActivity.this, "Expense Saved", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddExpenseActivity.this, "Failed to save expense", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
