package com.example.mpstar.ui.planning_ds;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mpstar.R;

import java.util.Objects;

public class planning_dsFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInsatnceState) {
        Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        return inflater.inflate(R.layout.fragment_planning_ds, container, false);
    }
}
