package com.example.bpm_service.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bpm_service.MainActivity;
import com.example.bpm_service.R;

public class HomeActivity extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.activity_home, container, false);

        ActionBar actionbar = ((MainActivity)getActivity()).getSupportActionBar();
        actionbar.hide();

        return view;
    }
}