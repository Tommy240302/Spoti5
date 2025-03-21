package com.example.spoti5.Fragments;

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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spoti5.Activities.MainActivity;
import com.example.spoti5.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;


public class SignInFragment extends Fragment {

    private TextView dontHaveAnAccount;
    private TextView resetPassword;
    private FrameLayout frameLayout;
    private EditText email;
    private EditText password;
    private Button signInButton;
    private ProgressBar signInProgressBar;
    private LinearLayout signInWithGoogleBtn;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        dontHaveAnAccount = view.findViewById(R.id.dont_have_an_account);
        resetPassword = view.findViewById(R.id.reset_password);
        frameLayout = getActivity().findViewById(R.id.register_frame_layout);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        signInButton = view.findViewById(R.id.signInButton);
        signInProgressBar = view.findViewById(R.id.signInProgressBar);
        signInWithGoogleBtn = view.findViewById(R.id.signInWithGoogleBtn);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(getContext(), gso);
        signInWithGoogleBtn.setOnClickListener(v -> signInWithGoogle());

        mAuth = FirebaseAuth.getInstance();
        return view;
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(getContext(), "Đăng nhập Google thất bại!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(requireActivity(), "Đăng nhập Google thành công!", Toast.LENGTH_SHORT).show();

                        // Điều hướng vào MainActivity
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        requireActivity().finish();
                    } else {
                        Toast.makeText(requireActivity(), "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dontHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(new SignUpFragment());
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(new ResetPasswordFragment());
            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInButton.setEnabled(false);
                signInButton.setTextColor(ContextCompat.getColor(getContext(), R.color.transWhite));
                signInWithFirebase();
                ;
            }
        });
    }

    private void signInWithFirebase() {
        if (email.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
            signInProgressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            signInProgressBar.setVisibility(View.INVISIBLE);
                            if (task.isSuccessful()){
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                getActivity().startActivity(intent);
                                getActivity().finish();
                            }else {
                                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                signInButton.setEnabled(true);
                                signInButton.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                            }
                        }
                    });
        } else {
            email.setError("Invalid Email Pattern!");
            signInButton.setEnabled(true);
            signInButton.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        }
    }

    private void checkInputs() {
        if (!email.getText().toString().isEmpty()){
            if (!password.getText().toString().isEmpty()){
                signInButton.setEnabled(true);
                signInButton.setTextColor(getResources().getColor(R.color.white));
            }else {
                signInButton.setEnabled(false);
                signInButton.setTextColor(getResources().getColor(R.color.transWhite));
            }
        }else {
            signInButton.setEnabled(false);
            signInButton.setTextColor(getResources().getColor(R.color.transWhite));
        }
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.from_right, R.anim.out_from_left);
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }
    @Override
    public void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }



}