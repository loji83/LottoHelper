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


        int[][] bestNum = getLowNum(Arr, 1, 8);
        int[][] bestBoNum = getLowNum(Arr, 3, 2);

        number1.setText(String.valueOf(bestNum[0][0]));
        number2.setText(String.valueOf(bestNum[1][0]));
        number3.setText(String.valueOf(bestNum[2][0]));
        number4.setText(String.valueOf(bestNum[3][0]));
        number5.setText(String.valueOf(bestNum[4][0]));
        number6.setText(String.valueOf(bestNum[5][0]));
        boNumber1.setText(String.valueOf(bestBoNum[0][0]));

        StringBuilder a = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            a.append(String.valueOf(bestNum[i][0]));
            a.append("         ");
        }


        TextView hiNumbsView = (TextView) view.findViewById(R.id.highNums);
        hiNumbsView.setText(a);


//        long avgParize = getPrize();

        return view;

    }

    public int[][] getLowNum(int[][] Arr, int row, int depthNum) {
        int[][] temp = new int[depthNum][3];


        for (int i = 0; i < Arr.length; i++) {
            for (int j = 0; j < temp.length; j++) {
                if (temp[j][row] == 0 || Arr[i][row] < temp[j][row]) {
//                    Log.d(TAG, "Numbers is greater : " + String.valueOf(NumBers[i][row]) + " than " + String.valueOf(temp[j][row]));
                    for (int t = depthNum - 1; t > j; t--) {
                        temp[t] = temp[t - 1];
//                        Log.d(TAG, "Shift : temp[" + (t - 1) + "](" + temp[t - 1][1] + ") to [" + t + "](" + temp[t][1] + ")");
                    }
//                    Log.d(TAG, "insert : Numbers[" + i + "](" + NumBers[i][1] + ") to temp[" + j + "](" + temp[j][1] + ")");
                    temp[j] = Arr[i];
                    break;
                }
            }
        }


        return temp;
    }


    public int[][] makePoints(int[][] rowArr, int start, int current) {

        int tempa = 0;
        int tempb = 0;

        int[][] temp = new int[45][7];
        int weekNum = current - start + 1;
        int avgPick = 6 * weekNum / 45;
        int avgWeek = 7;
        int avgBoPick = weekNum / 45;
        int avgBoWeek = 45;

        for (int i = 0; i < 45; i++) {
            temp[i][0] = rowArr[i][0];
            temp[i][1] = rowArr[i][1];
            temp[i][2] = rowArr[i][2];
            temp[i][3] = (avgPick - rowArr[i][1]) + (avgWeek - rowArr[i][2]);
            Log.d(TAG, "(" + (i + 1) + ")" + "compare : ");
            Log.d(TAG, temp[i][1] + " / " + (avgPick - rowArr[i][1]) + " / " + temp[i][2] + " / " + (((current - rowArr[i][2]) + 1) - avgWeek));

            tempa = tempa + (int)Math.pow((avgPick - rowArr[i][1]), 2);
            tempb = tempb + (int)Math.pow(((current - rowArr[i][2]) + 1) - avgWeek, 2);

            temp[i][4] = rowArr[i][3];
            temp[i][5] = rowArr[i][4];
            temp[i][6] = (avgBoPick - rowArr[i][3]) + (avgBoWeek - rowArr[i][4]);
            Log.d(TAG, "compare Bo : " + (avgBoPick - rowArr[i][1]) + ", " + (avgBoWeek - ((current - rowArr[i][2]) + 1)));
        }
        Log.d(TAG, "temp a / b = " + tempa + " and " + tempb);


        return temp;
    }

}




