//Fragment telas principais
package com.example.projetomarvel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/** Container fragment for the five portrait destinations and their native sub-screens. */
public final class ScreenFragment extends Fragment {
    private static final String ARG_SCREEN="screen";
    public static ScreenFragment create(String screen){ScreenFragment fragment=new ScreenFragment();Bundle args=new Bundle();args.putString(ARG_SCREEN,screen);fragment.setArguments(args);return fragment;}
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle state){return inflater.inflate(R.layout.fragment_screen,container,false);}
    public String getScreen(){return getArguments()==null?"Home":getArguments().getString(ARG_SCREEN,"Home");}
}
