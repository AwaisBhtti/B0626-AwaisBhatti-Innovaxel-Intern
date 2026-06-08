package com.example.splitwise;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        btnLogin.setOnClickListener(v -> {
            String inputEmail = etEmail.getText().toString().trim();
            String inputPassword = etPassword.getText().toString().trim();
            
            if (inputEmail.isEmpty() || inputPassword.isEmpty()) {
                String errorMsg = "Please fill out all fields.";
                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                Log.e(TAG, errorMsg);
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()) {
                etEmail.setError("Please enter a valid email");
                return;
            }
            if (inputPassword.length() < 6) {
                etPassword.setError("Password must be at least 6 characters");
                return;
            }

            btnLogin.setEnabled(false);
            btnLogin.setAlpha(0.5f);

            mAuth.signInWithEmailAndPassword(inputEmail, inputPassword)
                    .addOnSuccessListener(authResult -> {
                        checkUserStatus(authResult.getUser().getUid());
                    })
                    .addOnFailureListener(e -> {
                        String errorMsg = "Login failed: " + e.getMessage();
                        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                        Log.e(TAG, errorMsg, e);
                        btnLogin.setEnabled(true);
                        btnLogin.setAlpha(1.0f);
                    });
        });
    }

    private void checkUserStatus(String uid) {
        mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String role = snapshot.child("role").getValue(String.class);

                    if ("suspended".equals(role)) {
                        FirebaseAuth.getInstance().signOut();
                        String errorMsg = "Your account has been suspended.";
                        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                        Log.e(TAG, errorMsg);
                        btnLogin.setEnabled(true);
                        btnLogin.setAlpha(1.0f);
                        return;
                    }
                }
                
                // All active users redirect to MainActivity
                if (getActivity() != null) {
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    getActivity().finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                String errorMsg = "Error: " + error.getMessage();
                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                Log.e(TAG, errorMsg, error.toException());
                btnLogin.setEnabled(true);
                btnLogin.setAlpha(1.0f);
            }
        });
    }
}