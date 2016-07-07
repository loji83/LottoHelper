package com.example.kang.lottohelper;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {
    String TAG = this.getClass().getSimpleName();
    final GregorianCalendar firstDay = new GregorianCalendar(2002, 11, 07, 21, 00, 00);

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    SQLiteHelper DBhelper;
    int lastWeekOfDB = 0;
    int currentWeek = getWeekNum();
    int[][] NumBers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 탭 구성
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //DB 생성 및 최신화
        DBhelper = new SQLiteHelper(this);
        DBhelper.open();
        lastWeekOfDB = DBhelper.getLastWeek();
        Log.d(TAG, "Default week info is " + String.valueOf(lastWeekOfDB) + " / " + String.valueOf(currentWeek));

        updateList();


        int startWeek = 1;
        NumBers = makeArray(startWeek);









        int[][] bestNum = getNum(1, 8);
        int[][] bestBoNum = getNum(2, 2);
        long avgParize = getPrize();


        Log.d(TAG, String.valueOf(avgParize));

    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 1:
                    return (Fragment) getFragmentManager().findFragmentById(R.id.container) ;

                case 2:
                    return new StatsFragment();

            }
            return new StatsFragment();
        }

        @Override
        public int getCount() {

            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "추천";
                case 1:
                    return "통계";
            }
            return null;
        }
    }


    // 현재 주차 정보
    int getWeekNum() {
        long mis = System.currentTimeMillis() - firstDay.getTimeInMillis();
        Double w = (mis / (double) (36 * 24 * 100000 * 7));
        return w.intValue() + 1;
    }

    // 특정일 주차 정보
    int getWeekNum(int year, int month, int day_of_month) {
        GregorianCalendar pickDay = new GregorianCalendar(year, month, day_of_month, 21, 00, 00);
        long mis = pickDay.getTimeInMillis() - firstDay.getTimeInMillis();
        Double w = (mis / (double) (36 * 24 * 100000 * 7));
        int week = w.intValue();
        return week + 1;
    }

    //리스트 최신화
    public void updateList() {
        Log.d(TAG, "Updating " + lastWeekOfDB + "th week");
        if (lastWeekOfDB == 0) {
            Toast toast = Toast.makeText(this, "초기 DB 구축이 필요합니다. 잠시만 기다려 주시기 바랍니다.", Toast.LENGTH_LONG);
            toast.show();
        } else if (currentWeek - lastWeekOfDB > 100) {
            Toast toast = Toast.makeText(this, "DB Update 중입니다. 잠시만 기다려 주시기 바랍니다.", Toast.LENGTH_SHORT);
            toast.show();
        }

        if (lastWeekOfDB < currentWeek) {
            callURL(++lastWeekOfDB);
        } else {
            Toast toast = Toast.makeText(this, "DB Update 완료.", Toast.LENGTH_LONG);
            toast.show();
            Log.d(TAG, "List was updated");
            return;
        }
        Log.d(TAG, lastWeekOfDB + "th week Complete");

    }


    //로또 통계 서버 조회
    public void callURL(int i) {
        Log.d(TAG, "request week info of " + String.valueOf(i));
        Communicator res = new Communicator("http://www.nlotto.co.kr/common.do?method=getLottoNumber&drwNo=" + String.valueOf(i), null, listener);
        res.sendData();
    }

    Communicator.OnCommunicatorListener listener = new Communicator.OnCommunicatorListener() {
        @Override
        public void success(String responseData) {
            Log.d(TAG, "add week of " + lastWeekOfDB);
            //주차 정보 생성
            aWeekInfo aWeek = new aWeekInfo(responseData);
            //주차 정보 저장
            DBhelper.addWeek(aWeek);
            if (lastWeekOfDB < currentWeek) {
                callURL(++lastWeekOfDB);
            }
        }

        @Override
        public void fail(int responseCode, String message) {
            Toast toast = Toast.makeText(MainActivity.this, lastWeekOfDB + "주차 정보를 얻어오는 데 문제가 생겼습니다", Toast.LENGTH_LONG);
            toast.show();
        }
    };

    //{숫자, 횟수, 보너스 횟수}를 원소로 하는 배열 생성
    public int[][] makeArray(int startWeek) {
        int[][] numArray;
        numArray = DBhelper.getNumFrequency(startWeek, currentWeek);
        for (int i = 0; i < 45; i++) {
            Log.d(TAG, "num : " + numArray[i][0] + " / " + numArray[i][1]);
        }
        return numArray;
    }

    // 상금 평균
    public long getPrize() {
        long prize = DBhelper.getPrize();
        return prize;
    }

    // 2중 배열에서 depthNum만큼의 상위 배열 만들기
    public int[][] getNum(int row, int depthNum) {
        int[][] temp = new int[depthNum][3];
        for (int i = 0; i < NumBers.length; i++) {
            for (int j = 0; j < temp.length; j++) {
                if (NumBers[i][row] > temp[j][row]) {
//                    Log.d(TAG, "Numbers is greater : " + String.valueOf(NumBers[i][row]) + " than " + String.valueOf(temp[j][row]));
                    for (int t = depthNum - 1; t > j; t--) {
                        temp[t] = temp[t - 1];
//                        Log.d(TAG, "Shift : temp[" + (t - 1) + "](" + temp[t - 1][1] + ") to [" + t + "](" + temp[t][1] + ")");
                    }
//                    Log.d(TAG, "insert : Numbers[" + i + "](" + NumBers[i][1] + ") to temp[" + j + "](" + temp[j][1] + ")");
                    temp[j] = NumBers[i];
                    break;
                }
            }
        }
        return temp;
    }


}
