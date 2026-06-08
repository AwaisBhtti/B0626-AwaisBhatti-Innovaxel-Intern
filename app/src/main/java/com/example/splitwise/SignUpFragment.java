package com.example.splitwise;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view. View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUpFragment extends Fragment {

    private static final String TAG = "SignUpFragment";
    private TextInputEditText etEmail, etPassword, etVerifyPassword;
    private Button btnSignUp;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        etEmail = view.findViewById(R.id.etEmail2);
        etPassword = view.findViewById(R.id.etPassword2);
        etVerifyPassword = view.findViewById(R.id.etVerifyPassword);
        btnSignUp = view.findViewById(R.id.btnSignUp);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        btnSignUp.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String verifyPassword = etVerifyPassword.getText().toString().trim();

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Please enter a valid email");
                return;
            }
            if (password.length() < 8) {
                etPassword.setError("Password must be at least 8 characters");
                return;
            }
            if (!password.equals(verifyPassword)) {
                etVerifyPassword.setError("Passwords do not match");
                return;
            }

            btnSignUp.setEnabled(false);
            btnSignUp.setAlpha(0.5f);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        String uid = mAuth.getCurrentUser().getUid();
                        saveUserToDatabase(uid, email);
                    })
                    .addOnFailureListener(e -> {
                        String errorMsg = "Registration failed: " + e.getMessage();
                        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                        Log.e(TAG, errorMsg, e);
                        btnSignUp.setEnabled(true);
                        btnSignUp.setAlpha(1.0f);
                    });
        });
    }

    private void saveUserToDatabase(String uid, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("uid", uid);
        user.put("email", email);
        user.put("role", "user");
        user.put("name", "");
        user.put("age", "");
        user.put("bio", "");
        user.put("gender", "");
        user.put("photoUrl", "");

        mDatabase.child("users").child(uid).setValue(user)
                .addOnSuccessListener(unused -> {
                    String successMsg = "Registration Successful! Please Log In.";
                    Toast.makeText(getContext(), successMsg, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, successMsg);
                    etEmail.setText("");
                    etPassword.setText("");
                    etVerifyPassword.setText("");
                    btnSignUp.setEnabled(true);
                    btnSignUp.setAlpha(1.0f);

                    ViewPager2 viewPager = requireActivity().findViewById(R.id.viewPager);
                    if (viewPager != null) {
                        viewPager.setCurrentItem(0, true);
                    }
                })
                .addOnFailureListener(e -> {
                    String errorMsg = "Failed to save profile: " + e.getMessage();
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                    Log.e(TAG, errorMsg, e);
                    btnSignUp.setEnabled(true);
                    btnSignUp.setAlpha(1.0f);
                });
    }
}