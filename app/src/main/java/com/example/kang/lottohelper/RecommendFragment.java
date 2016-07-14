package com.example.kang.lottohelper;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Created by Kang on 7/7/16.
 */


public class RecommendFragment extends Fragment {
    String TAG = this.getClass().getSimpleName();


    public static RecommendFragment newInstance(int week, int current) {
        String TAG = "newInstance";

        Bundle args = new Bundle();
        args.putInt("StartWeek", week);
        args.putInt("CurrentWeek", current);
        RecommendFragment fragment = new RecommendFragment();
        fragment.setArguments(args);
        Log.e(TAG, "restart fragment with week : " + week);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);

        int startWeek = getArguments().getInt("StartWeek");
        int currentWeek = getArguments().getInt("CurrentWeek");

        Log.d(TAG, "View 생성 with : " + startWeek + " ~ " + currentWeek);

        int[][] rowArr = ((MainActivity) getActivity()).getFrequentNums(startWeek);
        int[][] Arr = makePoints(rowArr, startWeek, currentWeek);
        int[][] bestNum = gethighPointsNum(Arr, 3, 8);
        int[][] bestBoNum = gethighPointsNum(Arr, 3, 2);

        TextView number1 = (TextView) view.findViewById(R.id.number1);
        TextView number2 = (TextView) view.findViewById(R.id.number2);
        TextView number3 = (TextView) view.findViewById(R.id.number3);
        TextView number4 = (TextView) view.findViewById(R.id.number4);
        TextView number5 = (TextView) view.findViewById(R.id.number5);
        TextView number6 = (TextView) view.findViewById(R.id.number6);
        TextView boNumber1 = (TextView) view.findViewById(R.id.boNumber1);

        TextView addNumber1 = (TextView) view.findViewById(R.id.addNumber1);
        TextView addNumber2 = (TextView) view.findViewById(R.id.addNumber2);
        TextView addBoNumber1 = (TextView) view.findViewById(R.id.addBoNumber1);

        setNumber(number1, bestNum[0][0]);
        setNumber(number2, bestNum[1][0]);
        setNumber(number3, bestNum[2][0]);
        setNumber(number4, bestNum[3][0]);
        setNumber(number5, bestNum[4][0]);
        setNumber(number6, bestNum[5][0]);
        setNumber(boNumber1, bestBoNum[0][0]);

        setNumber(addNumber1, bestNum[6][0]);
        setNumber(addNumber2, bestNum[7][0]);
        setNumber(addBoNumber1, bestBoNum[1][0]);

        return view;
    }


    public int[][] gethighPointsNum(int[][] Arr, int row, int depthNum) {

        int[][] temp = new int[depthNum][7];

        for (int i = 0; i < Arr.length; i++) {
            for (int j = 0; j < temp.length; j++) {
                if (Arr[i][row] > temp[j][row]) {
//                    Log.d(TAG, "Numbers is greater : " + String.valueOf(Arr[i][row]) + " than " + String.valueOf(temp[j][row]));
                    for (int t = depthNum - 1; t > j; t--) {
                        temp[t] = temp[t - 1];
//                        Log.d(TAG, "Shift : temp[" + (t - 1) + "](" + temp[t - 1][1] + ") to [" + t + "](" + temp[t][1] + ")");
                    }
//                    Log.d(TAG, "insert : Numbers[" + i + "](" + Arr[i][1] + ") to temp[" + j + "](" + temp[j][1] + ")");
                    temp[j] = Arr[i];
                    break;
                }
            }
        }


        return temp;
    }


    public int[][] makePoints(int[][] rowArr, int start, int current) {

        int[][] temp = new int[45][7];

        double tempA = 0;
        double tempB = 0;
        double tempC = 0;
        double tempD = 0;

        double numV;
        double weekV;
        double numBoV;
        double weekBoV;

        double numDeviation;
        double weekDeviation;
        double numBoDeviation;
        double weekBoDeviation;
        double prePoint = 0;
        int point = 0;

        int weekNum = current - start + 1;
        double avgPick = 6 * weekNum / 45;
        double avgWeek = 45 / 6;
        double avgBoPick = weekNum / 45;
        double avgBoWeek = 45;


        //분산
        for (int i = 0; i < 45; i++) {
            tempA = tempA + (int) Math.pow((avgPick - rowArr[i][1]), 2);
            tempB = tempB + (int) Math.pow(((current - rowArr[i][2]) + 1) - avgWeek, 2);
            tempC = tempC + (int) Math.pow((avgBoPick - rowArr[i][3]), 2);
            tempD = tempD + (int) Math.pow(((current - rowArr[i][4]) + 1) - avgBoWeek, 2);
        }

        //편차
        numV = tempA / 45;
        weekV = tempB / 45;
        Log.d(TAG, "sum of V and V = " + tempA + ", " + numV + " / " + tempB + ", " + weekV);

        //표준편차
        numDeviation = Math.sqrt(numV);
        weekDeviation = Math.sqrt(weekV);
        Log.d(TAG, "Deviation        : " + numDeviation + ", " + weekDeviation);

        //편차
        numBoV = tempC / 45;
        weekBoV = tempD / 45;
//        Log.d(TAG, "sum of V^2 and V = " + tempC + ", " + tempD + " / " + numBoV + ", " + weekBoV);

        //표준편차
        numBoDeviation = Math.sqrt(numBoV);
        weekBoDeviation = Math.sqrt(weekBoV);
//        Log.d(TAG, "Deviation          : " + numBoDeviation + ", " + weekBoDeviation);


        for (int i = 0; i < 45; i++) {
            temp[i][0] = rowArr[i][0];  // 수 입력

            temp[i][1] = rowArr[i][1];  // 횟수 입력

            if (rowArr[i][2] != 0) {      // 주차 입력(0은 현재 주 - 1)
                temp[i][2] = rowArr[i][2];
            } else {
                temp[i][2] = start - 1;
            }

            prePoint = ((avgPick - rowArr[i][1]) / numDeviation + (current - rowArr[i][2] + 1 - avgWeek) / weekDeviation) * 10000;
            point = (int) prePoint;
            temp[i][3] = point;
            Log.d(TAG, "[number : " + (i + 1) + "]");
            Log.d(TAG, "    횟수  = " + temp[i][1] + ", " + rowArr[i][1] + " / " + ((avgPick - rowArr[i][1]) / numDeviation));
            Log.d(TAG, "    주차  = " + temp[i][2] + ", " + rowArr[i][2] + " / " + ((current - rowArr[i][2] + 1 - avgWeek) / weekDeviation));
            Log.d(TAG, "    점수  = " + prePoint + " = " + ((avgPick - rowArr[i][1]) / numDeviation) + " + " + ((current - rowArr[i][2] + 1 - avgWeek) / weekDeviation));


            temp[i][4] = rowArr[i][3];
            if (rowArr[i][4] != 0) {
                temp[i][5] = rowArr[i][4];
            } else {
                temp[i][5] = start - 1;
            }
            prePoint = ((avgBoPick - rowArr[i][3]) / numBoDeviation + (current - rowArr[i][4] + 1 - avgBoWeek) / weekBoDeviation) * 10000;
            point = (int) prePoint;
            temp[i][6] = point;

//            Log.d(TAG, "    횟수  = " + temp[i][4] + ", " + rowArr[i][3] + " / " + ((avgPick - rowArr[i][3]) / numDeviation));
//            Log.d(TAG, "    주차  = " + temp[i][5] + ", " + rowArr[i][4] + " / " + ((current - rowArr[i][4] + 1 - avgWeek) / weekDeviation));
//            Log.d(TAG, "    점수  = " + prePoint + " = " + ((avgPick - rowArr[i][3]) / numDeviation) + " + " + ((current - rowArr[i][4] + 1 - avgWeek) / weekDeviation));

        }

        return temp;
    }

    void setNumber(TextView textView, int number) {
        int color = Color.WHITE;
        switch ((number - 1) / 10) {
            case 0:
                color = Color.rgb(230, 230, 20);  //yellow
                break;
            case 1:
                color = Color.BLUE;   //blue
                break;
            case 2:
                color = Color.rgb(204, 0, 0);   //red
                break;
            case 3:
                color = Color.BLACK;  //black
                break;
            case 4:
                color = Color.rgb(000, 153, 51);   //green
                break;
            default:
                Log.d(TAG, "Wrong number");
        }
//        Log.d(TAG, "number = " + number + " / color = " + color);
        ((GradientDrawable) textView.getBackground()).setColor(color);
        textView.setText(String.valueOf(number));
    }


}




