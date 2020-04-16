package com.example.covid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CheckActivity extends AppCompatActivity {

    private TextView questionText;
    private String levelResult;
    private String currentTime;
    private Location mLocation;
    private String latitude;
    private String longitude;

    private int tapCount = 0;
    private int countYes = 0;
    private int countNo = 0;
    public final ArrayList<Integer> point = new ArrayList<Integer>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private DocumentReference questionRef = db.collection("question").document("q_one");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //transparent statusBar
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setTransparentStatusBarOnly(CheckActivity.this);

        //hide actionBar
        try
        {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        catch (NullPointerException ignored){}

        setContentView(R.layout.activity_check);
        questionText = findViewById(R.id.question);

        Calendar calendar = Calendar.getInstance();
        currentTime = DateFormat.getDateTimeInstance().format(calendar.getTime());

        final Button buttonCount = findViewById(R.id.button_yes);
        final Button buttonNotCount = findViewById(R.id.button_no);

        if (countYes == 0) {
            buttonCount.setEnabled(true);
        }

        buttonCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(view.getId()) {
                    case R.id.button_yes:
                        countYes++;
                        buttonCount.setEnabled(false);
                        break;
                }
                point.add(1);
                tapCount++;
                if (tapCount >= 7){
                    checkResult();
                    saveResult();
                    goToMainActivity();
                }

                questionRef.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()){
                                    String questionString = documentSnapshot.getString(Integer.toString(tapCount));
                                    questionText.setText(questionString);
                                    buttonCount.setEnabled(true);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CheckActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        if (countNo == 0) {
            buttonNotCount.setEnabled(true);
        }

        buttonNotCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(view.getId()) {
                    case R.id.button_no:
                        countNo++;
                        buttonNotCount.setEnabled(false);
                        break;
                }
                point.add(0);
                tapCount++;
                if (tapCount >= 7){
                    checkResult();
                    saveResult();
                    goToMainActivity();
                }

                questionRef.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()){
                                    String questionString = documentSnapshot.getString(Integer.toString(tapCount));
                                    questionText.setText(questionString);
                                    buttonNotCount.setEnabled(true);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CheckActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    public void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void checkResult(){
        if(point.get(0) == 1 && point.get(1) == 2 && point.get(2) == 3 && point.get(3) == 1 && point.get(4) == 1 && point.get(5) == 1 && point.get(6) == 1) {levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 1&&point.get(2) == 1&&point.get(3) == 1&&point.get(4) == 1&&point.get(5) == 1&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 1&&point.get(2) == 1&&point.get(3) == 1&&point.get(4) == 1&&point.get(5) == 1&&point.get(6) == 0){ levelResult = "rendah";}
        else if(point.get(0) == 1&&point.get(1) == 1&&point.get(2) == 1&&point.get(3) == 1&&point.get(4) == 1&&point.get(5) == 0&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 1&&point.get(2) == 1&&point.get(3) == 1&&point.get(4) == 1&&point.get(5) == 0&&point.get(6) == 0){ levelResult = "rendah";}
        else if(point.get(0) == 1&&point.get(1) == 1&&point.get(2) == 1&&point.get(3) == 1&&point.get(4) == 0&&point.get(5) == 1&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 1&&point.get(2) == 1&&point.get(3) == 1&&point.get(4) == 0&&point.get(5) == 1&&point.get(6) == 0){ levelResult = "rendah";}
        else if(point.get(0) == 1&&point.get(1) == 1&&point.get(2) == 1&&point.get(3) == 1&&point.get(4) == 0&&point.get(5) == 0&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 1&&point.get(2) == 1&&point.get(3) == 1&&point.get(4) == 0&&point.get(5) == 0&&point.get(6) == 0){ levelResult = "rendah";}
        else if(point.get(0) == 1&&point.get(1) == 1&&point.get(2) == 1&&point.get(3) == 0&&point.get(4) == 1&&point.get(5) == 1&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 1&&point.get(2) == 1&&point.get(3) == 0&&point.get(4) == 1&&point.get(5) == 1&&point.get(6) == 0){ levelResult = "rendah";}
        else if(point.get(0) == 1&&point.get(1) == 1&&point.get(2) == 1&&point.get(3) == 0&&point.get(4) == 1&&point.get(5) == 0&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 1&&point.get(2) == 1&&point.get(3) == 0&&point.get(4) == 1&&point.get(5) == 0&&point.get(6) == 0){ levelResult = "rendah";}
        else if(point.get(0) == 1&&point.get(1) == 1&&point.get(2) == 1&&point.get(3) == 0&&point.get(4) == 0&&point.get(5) == 1&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 1&&point.get(2) == 1&&point.get(3) == 0&&point.get(4) == 0&&point.get(5) == 1&&point.get(6) == 0){ levelResult = "rendah";}
        else if(point.get(0) == 1&&point.get(1) == 1&&point.get(2) == 1&&point.get(3) == 0&&point.get(4) == 0&&point.get(5) == 0&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 1&&point.get(2) == 1&&point.get(3) == 0&&point.get(4) == 0&&point.get(5) == 0&&point.get(6) == 0){ levelResult = "rendah";}
        else if(point.get(0) == 1&&point.get(1) == 1&&point.get(2) == 0&&point.get(3) == 1&&point.get(4) == 1&&point.get(5) == 1&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 1&&point.get(2) == 0&&point.get(3) == 1&&point.get(4) == 1&&point.get(5) == 1&&point.get(6) == 0){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 1&&point.get(2) == 0&&point.get(3) == 1&&point.get(4) == 1&&point.get(5) == 0&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 1&&point.get(2) == 0&&point.get(3) == 1&&point.get(4) == 1&&point.get(5) == 0&&point.get(6) == 0){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 1&&point.get(2) == 0&&point.get(3) == 1&&point.get(4) == 0&&point.get(5) == 1&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 1&&point.get(2) == 0&&point.get(3) == 1&&point.get(4) == 0&&point.get(5) == 1&&point.get(6) == 0){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 1&&point.get(2) == 0&&point.get(3) == 1&&point.get(4) == 0&&point.get(5) == 0&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 1&&point.get(2) == 0&&point.get(3) == 1&&point.get(4) == 0&&point.get(5) == 0&&point.get(6) == 0){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 1&&point.get(2) == 0&&point.get(3) == 0&&point.get(4) == 1&&point.get(5) == 1&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 1&&point.get(2) == 0&&point.get(3) == 0&&point.get(4) == 1&&point.get(5) == 1&&point.get(6) == 0){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 1&&point.get(2) == 0&&point.get(3) == 0&&point.get(4) == 1&&point.get(5) == 0&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 1&&point.get(2) == 0&&point.get(3) == 0&&point.get(4) == 1&&point.get(5) == 0&&point.get(6) == 0){ levelResult = "rendah";}
        else if(point.get(0) == 1&&point.get(1) == 1&&point.get(2) == 0&&point.get(3) == 0&&point.get(4) == 0&&point.get(5) == 1&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 1&&point.get(2) == 0&&point.get(3) == 0&&point.get(4) == 0&&point.get(5) == 1&&point.get(6) == 0){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 1&&point.get(2) == 0&&point.get(3) == 0&&point.get(4) == 0&&point.get(5) == 0&&point.get(6) == 1){ levelResult = "sedang";}
        else if(point.get(0) == 1&&point.get(1) == 1&&point.get(2) == 0&&point.get(3) == 0&&point.get(4) == 0&&point.get(5) == 0&&point.get(6) == 0){ levelResult = "rendah";}
        else if(point.get(0) == 1&&point.get(1) == 0&&point.get(2) == 1&&point.get(3) == 1&&point.get(4) == 1&&point.get(5) == 1&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 0&&point.get(2) == 1&&point.get(3) == 1&&point.get(4) == 1&&point.get(5) == 1&&point.get(6) == 0){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 0&&point.get(2) == 1&&point.get(3) == 1&&point.get(4) == 1&&point.get(5) == 0&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 0&&point.get(2) == 1&&point.get(3) == 1&&point.get(4) == 1&&point.get(5) == 0&&point.get(6) == 0){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 0&&point.get(2) == 1&&point.get(3) == 1&&point.get(4) == 0&&point.get(5) == 1&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 0&&point.get(2) == 1&&point.get(3) == 1&&point.get(4) == 0&&point.get(5) == 1&&point.get(6) == 0){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 0&&point.get(2) == 1&&point.get(3) == 1&&point.get(4) == 0&&point.get(5) == 0&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 0&&point.get(2) == 1&&point.get(3) == 1&&point.get(4) == 0&&point.get(5) == 0&&point.get(6) == 0){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 0&&point.get(2) == 1&&point.get(3) == 0&&point.get(4) == 1&&point.get(5) == 1&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 0&&point.get(2) == 1&&point.get(3) == 0&&point.get(4) == 1&&point.get(5) == 1&&point.get(6) == 0){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 0&&point.get(2) == 1&&point.get(3) == 0&&point.get(4) == 1&&point.get(5) == 0&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 0&&point.get(2) == 1&&point.get(3) == 0&&point.get(4) == 1&&point.get(5) == 0&&point.get(6) == 0){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 0&&point.get(2) == 1&&point.get(3) == 0&&point.get(4) == 0&&point.get(5) == 1&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 0&&point.get(2) == 1&&point.get(3) == 0&&point.get(4) == 0&&point.get(5) == 1&&point.get(6) == 0){ levelResult = "sedang";}
        else if(point.get(0) == 1&&point.get(1) == 0&&point.get(2) == 1&&point.get(3) == 0&&point.get(4) == 0&&point.get(5) == 0&&point.get(6) == 1){ levelResult = "rendah";}
        else if(point.get(0) == 1&&point.get(1) == 0&&point.get(2) == 1&&point.get(3) == 0&&point.get(4) == 0&&point.get(5) == 0&&point.get(6) == 0){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 0&&point.get(2) == 0&&point.get(3) == 1&&point.get(4) == 1&&point.get(5) == 1&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 0&&point.get(2) == 0&&point.get(3) == 1&&point.get(4) == 1&&point.get(5) == 1&&point.get(6) == 0){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 0&&point.get(2) == 0&&point.get(3) == 1&&point.get(4) == 1&&point.get(5) == 0&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 0&&point.get(2) == 0&&point.get(3) == 1&&point.get(4) == 1&&point.get(5) == 0&&point.get(6) == 0){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 0&&point.get(2) == 0&&point.get(3) == 1&&point.get(4) == 0&&point.get(5) == 1&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 0&&point.get(2) == 0&&point.get(3) == 1&&point.get(4) == 0&&point.get(5) == 1&&point.get(6) == 0){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 0&&point.get(2) == 0&&point.get(3) == 1&&point.get(4) == 0&&point.get(5) == 0&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 0&&point.get(2) == 0&&point.get(3) == 1&&point.get(4) == 0&&point.get(5) == 0&&point.get(6) == 0){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 0&&point.get(2) == 0&&point.get(3) == 0&&point.get(4) == 1&&point.get(5) == 1&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 0&&point.get(2) == 0&&point.get(3) == 0&&point.get(4) == 1&&point.get(5) == 1&&point.get(6) == 0){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 0&&point.get(2) == 0&&point.get(3) == 0&&point.get(4) == 1&&point.get(5) == 0&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 0&&point.get(2) == 0&&point.get(3) == 0&&point.get(4) == 1&&point.get(5) == 0&&point.get(6) == 0){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 0&&point.get(2) == 0&&point.get(3) == 0&&point.get(4) == 0&&point.get(5) == 1&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 0&&point.get(2) == 0&&point.get(3) == 0&&point.get(4) == 0&&point.get(5) == 1&&point.get(6) == 0){ levelResult = "tinggi";}
        else if(point.get(0) == 1&&point.get(1) == 0&&point.get(2) == 0&&point.get(3) == 0&&point.get(4) == 0&&point.get(5) == 0&&point.get(6) == 1){ levelResult = "sedang";}
        else if(point.get(0) == 1&&point.get(1) == 0&&point.get(2) == 0&&point.get(3) == 0&&point.get(4) == 0&&point.get(5) == 0&&point.get(6) == 0){ levelResult = "rendah";}
        else if(point.get(0) == 0&&point.get(1) == 1&&point.get(2) == 1&&point.get(3) == 1&&point.get(4) == 1&&point.get(5) == 1&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 0&&point.get(1) == 1&&point.get(2) == 1&&point.get(3) == 1&&point.get(4) == 1&&point.get(5) == 1&&point.get(6) == 0){ levelResult = "tinggi";}
        else if(point.get(0) == 0&&point.get(1) == 1&&point.get(2) == 1&&point.get(3) == 1&&point.get(4) == 1&&point.get(5) == 0&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 0&&point.get(1) == 1&&point.get(2) == 1&&point.get(3) == 1&&point.get(4) == 1&&point.get(5) == 0&&point.get(6) == 0){ levelResult = "tinggi";}
        else if(point.get(0) == 0&&point.get(1) == 1&&point.get(2) == 1&&point.get(3) == 1&&point.get(4) == 0&&point.get(5) == 1&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 0&&point.get(1) == 1&&point.get(2) == 1&&point.get(3) == 1&&point.get(4) == 0&&point.get(5) == 1&&point.get(6) == 0){ levelResult = "tinggi";}
        else if(point.get(0) == 0&&point.get(1) == 1&&point.get(2) == 1&&point.get(3) == 1&&point.get(4) == 0&&point.get(5) == 0&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 0&&point.get(1) == 1&&point.get(2) == 1&&point.get(3) == 1&&point.get(4) == 0&&point.get(5) == 0&&point.get(6) == 0){ levelResult = "tinggi";}
        else if(point.get(0) == 0&&point.get(1) == 1&&point.get(2) == 1&&point.get(3) == 0&&point.get(4) == 1&&point.get(5) == 1&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 0&&point.get(1) == 1&&point.get(2) == 1&&point.get(3) == 0&&point.get(4) == 1&&point.get(5) == 1&&point.get(6) == 0){ levelResult = "tinggi";}
        else if(point.get(0) == 0&&point.get(1) == 1&&point.get(2) == 1&&point.get(3) == 0&&point.get(4) == 1&&point.get(5) == 0&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 0&&point.get(1) == 1&&point.get(2) == 1&&point.get(3) == 0&&point.get(4) == 1&&point.get(5) == 0&&point.get(6) == 0){ levelResult = "tinggi";}
        else if(point.get(0) == 0&&point.get(1) == 1&&point.get(2) == 1&&point.get(3) == 0&&point.get(4) == 0&&point.get(5) == 1&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 0&&point.get(1) == 1&&point.get(2) == 1&&point.get(3) == 0&&point.get(4) == 0&&point.get(5) == 1&&point.get(6) == 0){ levelResult = "tinggi";}
        else if(point.get(0) == 0&&point.get(1) == 1&&point.get(2) == 1&&point.get(3) == 0&&point.get(4) == 0&&point.get(5) == 0&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 0&&point.get(1) == 1&&point.get(2) == 1&&point.get(3) == 0&&point.get(4) == 0&&point.get(5) == 0&&point.get(6) == 0){ levelResult = "rendah";}
        else if(point.get(0) == 0&&point.get(1) == 1&&point.get(2) == 0&&point.get(3) == 1&&point.get(4) == 1&&point.get(5) == 1&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 0&&point.get(1) == 1&&point.get(2) == 0&&point.get(3) == 1&&point.get(4) == 1&&point.get(5) == 1&&point.get(6) == 0){ levelResult = "tinggi";}
        else if(point.get(0) == 0&&point.get(1) == 1&&point.get(2) == 0&&point.get(3) == 1&&point.get(4) == 1&&point.get(5) == 0&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 0&&point.get(1) == 1&&point.get(2) == 0&&point.get(3) == 1&&point.get(4) == 1&&point.get(5) == 0&&point.get(6) == 0){ levelResult = "tinggi";}
        else if(point.get(0) == 0&&point.get(1) == 1&&point.get(2) == 0&&point.get(3) == 1&&point.get(4) == 0&&point.get(5) == 1&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 0&&point.get(1) == 1&&point.get(2) == 0&&point.get(3) == 1&&point.get(4) == 0&&point.get(5) == 1&&point.get(6) == 0){ levelResult = "tinggi";}
        else if(point.get(0) == 0&&point.get(1) == 1&&point.get(2) == 0&&point.get(3) == 1&&point.get(4) == 0&&point.get(5) == 0&&point.get(6) == 1){ levelResult = "tinggi";}
        else if(point.get(0) == 0&&point.get(1) == 1&&point.get(2) == 0&&point.get(3) == 1&&point.get(4) == 0&&point.get(5) == 0&&point.get(6) == 0){ levelResult = "tinggi";}
        else if(point.get(0) == 0&&point.get(1) == 1&&point.get(2) == 0&&point.get(3) == 0&&point.get(4) == 1&&point.get(5) == 1&&point.get(6) == 1){ levelResult = "tinggi";}
    }

    public void saveResult(){
        Map<String, Object> finalResult = new HashMap<>();
        finalResult.put("Level", levelResult);
        finalResult.put("Last Check", currentTime);
        //finalResult.put("Latitude", latitude);
        //finalResult.put("Longitude", longitude);

        db.collection("UsersData").document(userID).
                set(finalResult, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Status Check", "done");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Status Check", "fail");
                    }
                });
    }

    public void setTransparentStatusBarOnly(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        // this lines ensure only the status-bar to become transparent without affecting the nav-bar
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}
