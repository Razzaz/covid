package com.example.covid;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

public class CheckFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //startActivity(new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), MoveActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
        return inflater.inflate(R.layout.fragment_check, container, false);
    }



}
