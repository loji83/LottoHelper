package com.example.kang.lottohelper;

import android.app.DatePickerDialog;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    String TAG = this.getClass().getSimpleName();

    final GregorianCalendar firstDay = new GregorianCalendar(2002, 11, 07, 21, 00, 00);
    GregorianCalendar today;
    GregorianCalendar startDay;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    TextView dateView;
    Button dateButton;
    Button resetButton;

    SQLiteHelper DBhelper;
    int lastWeekOfDB;
    int currentWeek;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        //화면 요소 로딩
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        dateButton = (Button) findViewById(R.id.dateButton);
        resetButton = (Button) findViewById(R.id.resetButton);
        dateView = (TextView) findViewById(R.id.priod);

        // 날짜 출력
        today = new GregorianCalendar(Locale.KOREA);
        startDay = new GregorianCalendar(firstDay.get(Calendar.YEAR), firstDay.get(Calendar.MONTH), firstDay.get(Calendar.DAY_OF_MONTH));
        setTimeText(dateButton, startDay);
        dateView.setText(dateToString(today));


        //DB 생성 및 최신화
        DBhelper = new SQLiteHelper(this);
        DBhelper.open();
        currentWeek = getWeekNum(today);
        lastWeekOfDB = DBhelper.getLastWeek();
        Log.d(TAG, "Default week info is " + String.valueOf(lastWeekOfDB) + " / " + String.valueOf(currentWeek));

        if (lastWeekOfDB < currentWeek) {
            updateList();
        }

        //페이지 뷰 생성


        resetButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                startDay.set(firstDay.get(Calendar.YEAR), firstDay.get(Calendar.MONTH), firstDay.get(Calendar.DAY_OF_MONTH));

                setTimeText(dateButton, startDay);
                Fragment fr = RecommendFragment.newInstance(getWeekNum(startDay), currentWeek);
                FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
                tr.replace(R.id.container, fr);
                tr.commit();
                mSectionsPagerAdapter.notifyDataSetChanged();
                mViewPager.refreshDrawableState();

            }

        });

        //버튼 리스너 - date pickup 실행
        dateButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                DatePickerDialog startdayPicker = new DatePickerDialog(MainActivity.this, mOndateSetListener, startDay.get(Calendar.YEAR), startDay.get(Calendar.MONTH), startDay.get(Calendar.DAY_OF_MONTH));
                startdayPicker.show();
            }

        });
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fr;
            Bundle args = new Bundle();
            args.putInt("CurrentWeek", currentWeek);
            args.putInt("StartWeek", getWeekNum(startDay));
            if (position == 1) {
                fr = new StatsFragment();
                Log.d(TAG, "Stats Fragment Num : " + String.valueOf(position));
                return fr;
            } else {
                Log.d(TAG, "Recommend Fragment Num : " + String.valueOf(position));
                fr = new RecommendFragment();
            }
            fr.setArguments(args);
            return fr;
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

        @Override
        public int getItemPosition(Object obj) {
            return POSITION_NONE;
        }
    }


    DatePickerDialog.OnDateSetListener mOndateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            startDay = new GregorianCalendar(year, monthOfYear, dayOfMonth);

            GregorianCalendar compareDate = new GregorianCalendar(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
            compareDate.add(Calendar.YEAR, -1);

            if (startDay.before(firstDay)) {
                Toast toast = Toast.makeText(MainActivity.this, "1회 추첨일 부터 계산합니다.", Toast.LENGTH_SHORT);
                toast.show();
            } else if (startDay.after(compareDate)) {
                Toast toast = Toast.makeText(MainActivity.this, "최소 1년 이상의 DB가 필요합니다", Toast.LENGTH_SHORT);
                toast.show();
            } else {

                Log.d(TAG, "StartDay = " + dateToString(startDay));

                setTimeText(dateButton, startDay);
                Fragment fr = RecommendFragment.newInstance(getWeekNum(startDay), currentWeek);
                FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
                tr.replace(R.id.container, fr);
                tr.commit();

                mSectionsPagerAdapter.notifyDataSetChanged();
                mViewPager.refreshDrawableState();
            }
        }
    };


    // 특정일 주차 정보
    int getWeekNum(GregorianCalendar day) {
        Log.d(TAG, "Day is " + dateToString(day));
        Log.d(TAG, "Day : " + day.getTimeInMillis() + " / firstDay : " + firstDay.getTimeInMillis());
        long mis = day.getTimeInMillis() - firstDay.getTimeInMillis();

        Double w = (mis / (double) (36 * 24 * 100000 * 7));
        int week = w.intValue();

        Log.d(TAG, String.valueOf(mis) + " / " + String.valueOf(w) + " / " + String.valueOf(week));
        return week + 1;
    }

    //리스트 최신화
    public void updateList() {

        if (lastWeekOfDB == 0) {
            Toast toast = Toast.makeText(this, "초기 DB 구축이 필요합니다. 잠시만 기다려 주시기 바랍니다.", Toast.LENGTH_LONG);
            toast.show();
        } else if (currentWeek - lastWeekOfDB > 100) {
            Toast toast = Toast.makeText(this, "DB Update 중입니다. 잠시만 기다려 주시기 바랍니다.", Toast.LENGTH_SHORT);
            toast.show();
        }

        Log.d(TAG, "Updating " + lastWeekOfDB + "th week");

        callURL(++lastWeekOfDB);

        Toast toast = Toast.makeText(this, "DB Update 완료.", Toast.LENGTH_LONG);
        toast.show();
        Log.d(TAG, "List was updated");

        Log.d(TAG, lastWeekOfDB + "th week Complete");

    }

    //로또 통계 서버 조회
    public void callURL(int week) {
        Log.d(TAG, "request week info of " + String.valueOf(week));
        Communicator res = new Communicator("http://www.nlotto.co.kr/common.do?method=getLottoNumber&drwNo=" + String.valueOf(week), null, listener);
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

    //추천에서 이용할 어레이 생성 : fragment 1에서 호출
    public int[][] getFrequentNums(int startWeek) {
        int[][] numArray;
        numArray = DBhelper.getNumFrequency(startWeek, currentWeek);
        Log.d(TAG, "GetNumFrequency with : " + startWeek);
//        for (int i = 0; i < 45; i++) {
//            Log.d(TAG, "num : " + numArray[i][0] + " / " + numArray[i][1]);
//        }
        return numArray;
    }

// 상금 평균
//    public long getPrize() {
//        long prize = DBhelper.getPrize();
//        return prize;
//    }


//    public int[][] makeArray(int startWeek, String[] columns) {
//        int[][] numArray;
//        numArray = DBhelper.getNumFrequency(startWeek, currentWeek, columns);
////        for (int i = 0; i < 45; i++) {
////            Log.d(TAG, "num : " + numArray[i][0] + " / " + numArray[i][1]);
////        }
//        return numArray;
//    }

    public String dateToString(GregorianCalendar cal) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy년 MM월 dd일");
        fmt.setCalendar(cal);
        String dateStr = fmt.format(cal.getTime());
        return dateStr;
    }

    public void setTimeText(Button tv, GregorianCalendar day) {
        tv.setText(dateToString(day));
    }

}


