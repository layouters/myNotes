/*
 * Copyright (c) 2016.  Lukasz Fiszer
 */

package com.uznamska.lukas.mynotes.splashscreen;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uznamska.lukas.mynotes.R;


public class ScreenSlideFragment extends Fragment {
    private int mPosition ;
    String[] ads = { "Notatki textowe!",
                     "Twórz listy!",
                     "Don't sweat over this guy talking bullshit",
                     "Keep track of your ideas!!!",
                     "Your ideas, This is what really matters!!" };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_screen_slide_page, container, false);
        TextView text = (TextView)rootView.findViewById(R.id.ad_text);
        text.setText(ads[mPosition]);


        return rootView;
    }


    public void setPosition(int pos) {
        mPosition = pos;
    }
}
