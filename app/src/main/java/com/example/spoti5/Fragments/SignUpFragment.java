package com.example.spoti5.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spoti5.Activities.MainActivity;
import com.example.spoti5.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpFragment extends Fragment {

    private TextView alreadyHaveAnAccount;
    private FrameLayout frameLayout;
    private EditText userName;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private Button signUpButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        alreadyHaveAnAccount = view.findViewById(R.id.already_have_an_account);
        if (getActivity() != null) {
            frameLayout = getActivity().findViewById(R.id.register_frame_layout);
        }

        userName = view.findViewById(R.id.userName);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        confirmPassword = view.findViewById(R.id.confirmPassword);
        signUpButton = view.findViewById(R.id.signUpButton);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        alreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(new SignInFragment());
            }
        });

        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpWithFirebase();
                signUpButton.setEnabled(false);
                signUpButton.setTextColor(ContextCompat.getColor(getContext(), R.color.transWhite));
            }
        });
    }

    private void signUpWithFirebase() {
        if (email.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
            if (password.getText().toString().equals(confirmPassword.getText().toString())) {
                mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("userName", userName.getText().toString());
                                    user.put("email", email.getText().toString());
                                    db.collection("users")
                                            .document(task.getResult().getUser().getUid())
                                            .set(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                                    getActivity().startActivity(intent);
                                                    getActivity().finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    signUpButton.setEnabled(true);
                                                    signUpButton.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                                                }
                                            });
                                } else {
                                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    signUpButton.setEnabled(true);
                                    signUpButton.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                                }
                            }
                        });
            } else {
                confirmPassword.setError("Password doesn't match!");
                signUpButton.setEnabled(true);
                signUpButton.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            }
        } else {
            email.setError("Invalid Email Pattern!");
            signUpButton.setEnabled(true);
            signUpButton.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        }
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.from_left, R.anim.out_from_right);
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void checkInputs() {
        if (!userName.getText().toString().isEmpty()) {
            if (!email.getText().toString().isEmpty()) {
                if (!password.getText().toString().isEmpty() && password.getText().toString().length() >= 6) {
                    if (!confirmPassword.getText().toString().isEmpty()) {
                        signUpButton.setEnabled(true);
                        signUpButton.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    } else {
                        signUpButton.setEnabled(false);
                        signUpButton.setTextColor(ContextCompat.getColor(getContext(), R.color.transWhite));
                    }
                } else {
                    signUpButton.setEnabled(false);
                    signUpButton.setTextColor(ContextCompat.getColor(getContext(), R.color.transWhite));
                }
            } else {
                signUpButton.setEnabled(false);
                signUpButton.setTextColor(ContextCompat.getColor(getContext(), R.color.transWhite));
            }
        } else {
            signUpButton.setEnabled(false);
            signUpButton.setTextColor(ContextCompat.getColor(getContext(), R.color.transWhite));
        }
    }
}
