package com.example.kang.lottohelper;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ajc on 2016-06-08.
 */
public class Communicator {
    private static final int readTimeout = 10 * 1000;
    private static final int connectTimeout = 10 * 1000;
    private OnCommunicatorListener listener = null;
    private String urlString;
    private String body;
    private Handler handler = null;
    private StringBuffer sb = null;
    private int responseCode = -1;
    private String erroeMessage = null;

    public Communicator(String urlString, String body, OnCommunicatorListener listener) {
        this.urlString = urlString;
        this.body = body;
        this.listener = listener;

        handler = new Handler(Looper.getMainLooper());
    }

    public void sendData() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                boolean ret = false;

                try {

                    Log.d("", "URL [" + urlString + "] body [" + body + "]");

                    URL url = new URL(urlString);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(readTimeout);
                    conn.setConnectTimeout(connectTimeout);
                    conn.setUseCaches(false);
                    conn.setDefaultUseCaches(false);
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("charset", "utf-8");
                    conn.setDoInput(true);

                    if (TextUtils.isEmpty(body) == false) {
                        conn.setDoOutput(true);
                        OutputStream out = conn.getOutputStream();
                        out.write(body.getBytes("UTF-8"));
                        out.flush();
                        out.close();
                    }

                    responseCode = conn.getResponseCode();
                    Log.d("","responseCode : " + responseCode);
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String line = null;
                        sb = new StringBuffer();
                        while (true) {
                            line = reader.readLine();
                            if (line == null) {
                                break;
                            }
                            if (sb.length() > 0) {
                                sb.append("\n" + line);
                            } else {
                                sb.append(line);
                            }
                        }

                        reader.close();
                        ret = true;
                    } else {
                        ret = false;
                    }

                    conn.disconnect();
                    conn = null;

                } catch (Exception e) {
                  e.printStackTrace();
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                        conn = null;
                    }
                }

                try {
                    if (ret) {
                        Log.d("","resutl[" + sb.toString() + "]");
                        if (listener != null) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    listener.success(sb.toString());
                                }
                            });
                        }
                    } else {
                        if (listener != null) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    listener.fail(responseCode, erroeMessage);
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        try {
            t.join();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public interface OnCommunicatorListener {
        void success(String responseData);

        void fail(int responseCode, String message);
    }
}
