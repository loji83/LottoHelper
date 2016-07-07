package com.example.kang.lottohelper;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Kang on 7/7/16.
 */
public class RecommFragment extends Fragment {

    int[][] array;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_recommend, container, false);




        return view;

    }

    public int[][] setArray(int[][] array){
        return array;
    }

}
