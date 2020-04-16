package com.example.covid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private TextView riskText;
    private TextView lastCheckText;
    private TextView nameText;
    private TextView zoneText;
    private Button gpsButton;
    private Button logoutButton;
    private Button loadButton;
    private ProgressBar spinner;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private DocumentReference profile = db.collection("UsersData").document(userID);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        nameText = view.findViewById(R.id.profile_name);
        riskText = view.findViewById(R.id.profile_risk);
        lastCheckText = view.findViewById(R.id.profile_lastcheck);
        zoneText = view.findViewById(R.id.profile_zona);
        gpsButton = view.findViewById(R.id.profile_gps);
        logoutButton = view.findViewById(R.id.profile_logout);

        profile.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Log.d(TAG, "Current data: " + documentSnapshot.getData());
                    String fullName = documentSnapshot.getString("Name");
                    String[] firstName = fullName.split(" ");
                    nameText.setText(firstName[0]);
                    lastCheckText.setText(documentSnapshot.getString("Last Check"));
                    riskText.setText(documentSnapshot.getString("Level"));
                } else {
                    Log.d(TAG, "Current data: null");
                    nameText.setText("-");
                    lastCheckText.setText("-");
                    riskText.setText("-");
                    zoneText.setText("-");
                }
            }
        });

        gpsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), MoveActivity.class));
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), IntroActivity.class));
                getActivity().finish();
            }
        });

        return view;
    }

}
