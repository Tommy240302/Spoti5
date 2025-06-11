package com.example.spoti5.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.spoti5.Activities.RegisterActivity;
import com.example.spoti5.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {

    private ImageView btnLogout;
    private TextView songLiked;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if (account != null) {
            String personName = account.getDisplayName();
            String displayName = personName;

            // Lấy 2 từ cuối nếu có đủ
            if (personName != null && personName.contains(" ")) {
                String[] words = personName.trim().split(" ");
                if (words.length >= 2) {
                    displayName = words[words.length - 2] + " " + words[words.length - 1];
                } else {
                    displayName = words[words.length - 1]; // fallback: lấy từ cuối
                }
            }

            TextView tvUsername = view.findViewById(R.id.tv_username);
            tvUsername.setText(displayName);
            Log.d("TAG", "Tên người dùng hiển thị: " + displayName);
        }

        btnLogout = view.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });

        songLiked = view.findViewById(R.id.songLiked);
        songLiked.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SongLikedActivity.class);
            startActivity(intent);
        });


        return view;
    }
}
