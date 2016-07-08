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

        TextView number1 = (TextView)view.findViewById(R.id.number1);
        TextView number2 = (TextView)view.findViewById(R.id.number2);
        TextView number3 = (TextView)view.findViewById(R.id.number3);
        TextView number4 = (TextView)view.findViewById(R.id.number4);
        TextView number5 = (TextView)view.findViewById(R.id.number5);
        TextView number6 = (TextView)view.findViewById(R.id.number6);
        TextView boNumber1 = (TextView)view.findViewById(R.id.boNumber1);



        int Arr[][] = new int[45][3];

        if (getArguments() != null) {
            Arr = (int[][]) getArguments().getSerializable("Numbers");
            Log.d(TAG, "데이터 전달 : " + Arr[0][1]);
        }

        int[][] bestNum = getLowNum(Arr, 1, 8);
        int[][] bestBoNum = getLowNum(Arr, 2, 2);

        number1.setText(String.valueOf(bestNum[0][0]));
        number2.setText(String.valueOf(bestNum[1][0]));
        number3.setText(String.valueOf(bestNum[2][0]));
        number4.setText(String.valueOf(bestNum[3][0]));
        number5.setText(String.valueOf(bestNum[4][0]));
        number6.setText(String.valueOf(bestNum[5][0]));
        boNumber1.setText(String.valueOf(bestBoNum[0][0]));

        bestNum = getHighNum(Arr, 1, 8);
        bestBoNum = getHighNum(Arr, 2, 2);

        StringBuilder a = new StringBuilder();
        for(int i = 0 ; i < 8 ; i++) {
            a.append(String.valueOf(bestNum[i][0]));
            a.append("         ");
        }


        TextView hiNumbsView = (TextView)view.findViewById(R.id.highNums);
        hiNumbsView.setText(a);




//        long avgParize = getPrize();

        return view;

    }

    // 2중 배열에서 depthNum만큼의 상위 배열 만들기
    public int[][] getHighNum(int[][] Arr, int row, int depthNum) {
        int[][] temp = new int[depthNum][3];
        for (int i = 0; i < Arr.length; i++) {
            for (int j = 0; j < temp.length; j++) {
                if (Arr[i][row] > temp[j][row]) {
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


}




