package com.example.kang.lottohelper;

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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);






    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recommend, container, false);
        Log.d(TAG, "View 생성");

        TextView number1 = (TextView) view.findViewById(R.id.number1);
        TextView number2 = (TextView) view.findViewById(R.id.number2);
        TextView number3 = (TextView) view.findViewById(R.id.number3);
        TextView number4 = (TextView) view.findViewById(R.id.number4);
        TextView number5 = (TextView) view.findViewById(R.id.number5);
        TextView number6 = (TextView) view.findViewById(R.id.number6);
        TextView boNumber1 = (TextView) view.findViewById(R.id.boNumber1);


        int startWeek = getArguments().getInt("StartWeek");
        int currentWeek = getArguments().getInt("CurrentWeek");

        int[][] rowArr = ((MainActivity) getActivity()).getFrequentNums(startWeek);

        int[][] Arr = makePoints(rowArr, startWeek, currentWeek);


        int[][] bestNum = gethighPointsNum(Arr, 3, 8);
        int[][] bestBoNum = gethighPointsNum(Arr, 3, 2);

        number1.setText(String.valueOf(bestNum[0][0]));
        number2.setText(String.valueOf(bestNum[1][0]));
        number3.setText(String.valueOf(bestNum[2][0]));
        number4.setText(String.valueOf(bestNum[3][0]));
        number5.setText(String.valueOf(bestNum[4][0]));
        number6.setText(String.valueOf(bestNum[5][0]));
        boNumber1.setText(String.valueOf(bestBoNum[0][0]));


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
        Log.d(TAG, "sum of V^2 and V = " + tempA + ", " + tempB + " / " + numV + ", " + weekV);

        //표준편차
        numDeviation = Math.sqrt(numV);
        weekDeviation = Math.sqrt(weekV);
        Log.d(TAG, "Deviation : " + numDeviation + ", " + weekDeviation);

        //편차
        numBoV = tempC / 45;
        weekBoV = tempD / 45;
        Log.d(TAG, "sum of V^2 and V = " + tempC + ", " + tempD + " / " + numBoV + ", " + weekBoV);

        //표준편차
        numBoDeviation = Math.sqrt(numBoV);
        weekBoDeviation = Math.sqrt(weekBoV);
        Log.d(TAG, "Deviation : " + numBoDeviation + ", " + weekBoDeviation);

        for (int i = 0; i < 45; i++) {
            temp[i][0] = rowArr[i][0];
            temp[i][1] = rowArr[i][1];
            temp[i][2] = rowArr[i][2];
            prePoint = ((avgPick - rowArr[i][1]) / numDeviation + (current - rowArr[i][2] + 1 - avgWeek) / weekDeviation) * 100;
            point = (int) prePoint;
            temp[i][3] = point;
            Log.d(TAG, "[number : " + (i + 1) + "]");
            Log.d(TAG, "times = " + temp[i][1] + ", timesPoint = " + (avgPick - rowArr[i][1]) + ", last week = " + temp[i][2] + ", weekPoint = " + (((current - rowArr[i][2]) + 1) - avgWeek));
            Log.d(TAG, "Total point is " + temp[i][3]);

            temp[i][4] = rowArr[i][3];
            temp[i][5] = rowArr[i][4];
            prePoint = ((avgBoPick - rowArr[i][3]) / numDeviation + (current - rowArr[i][4] + 1 - avgBoWeek) / weekBoDeviation) * 100;
            point = (int) prePoint;
            temp[i][6] = point;
            Log.d(TAG, "Bo times = " + temp[i][3] + ", timesPoint = " + (avgPick - rowArr[i][3]) + ", last week = " + temp[i][4] + ", weekPoint = " + (((current - rowArr[i][4]) + 1) - avgWeek));
            Log.d(TAG, "Bo Total point is " + temp[i][6]);
        }

        return temp;
    }

}




