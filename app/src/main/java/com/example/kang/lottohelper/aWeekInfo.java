package com.example.kang.lottohelper;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Kang on 6/22/16.
 */
public class aWeekInfo {
    int weekNum;
    Date pickDay;
    int number1;
    int number2;
    int number3;
    int number4;
    int number5;
    int number6;
    int bonusNum;
    long firstWinnerPrize;
    long totalPrize;
    int howManyFirstWinner;

    SimpleDateFormat transForm = new SimpleDateFormat("yyyy-MM-dd");

    public aWeekInfo (String JsonObjStr) {
        try {
            JSONObject jObj = new JSONObject(JsonObjStr);

            bonusNum = jObj.getInt("bnusNo");
            firstWinnerPrize = jObj.getLong("firstWinamnt");
            weekNum = jObj.getInt("drwNo");
            pickDay = transForm.parse(jObj.getString("drwNoDate"));
            number1 = jObj.getInt("drwtNo1");
            number2 = jObj.getInt("drwtNo2");
            number3 = jObj.getInt("drwtNo3");
            number4 = jObj.getInt("drwtNo4");
            number5 = jObj.getInt("drwtNo5");
            number6 = jObj.getInt("drwtNo6");
            totalPrize = jObj.getLong("totSellamnt");
            howManyFirstWinner = jObj.getInt("firstPrzwnerCo");
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
