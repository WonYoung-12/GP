package com.example.kwy2868.practice.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kwy2868.practice.R;
import com.example.kwy2868.practice.util.SinglePracticeUIObserver;
import com.example.kwy2868.practice.util.SoundAnalyzer;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.neurosky.thinkgear.TGDevice;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class EEGActivity extends SamplingActivity {
    public static final String KEY_STDDEV_MED = "key_stddev_med";
    public static final String KEY_STDDEV_ATT = "key_stddev_att";
    public static final String KEY_AVG_MED = "key_avg_med";
    public static final String KEY_AVG_ATT = "key_avg_att";

    private static final String TAG = "EEGActivity";

    private BluetoothAdapter bluetoothAdapter;
    private TGDevice device;

    final boolean rawEnabled = true;

    private TextView mainTextView;
    private TextView attentionTextView;
    private TextView meditationTextView;

    private int attention;
    private int meditation;

    //chart
    private RadarChart mChart;
    private int[] wave;

    //fft
    private SoundAnalyzer soundAnalyzer;
    public int currentFrequencyIndex;

    public short raw[];
    public int rawIndex;

    //user emotion
    private int emotion;
    public ArrayList<Integer> attentionList;
    public ArrayList<Integer> meditationList;

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_eeg);

        setView();
        init();
        setChart();
        setFFT();
        startTimer();
    }

    private void setView() {
        mainTextView = (TextView)findViewById( R.id.textView1 );
        attentionTextView = (TextView) findViewById(R.id.attention);
        meditationTextView = (TextView) findViewById(R.id.meditation);
    }


    private void init() {
        emotion = getIntent().getIntExtra(CheckEmotionActivity.KEY_EMOTION, 0);
        wave = new int[7];
        raw = new short[SoundAnalyzer.AUDIO_DATA_SIZE];
        attentionList = new ArrayList<>();
        meditationList = new ArrayList<>();

        mainTextView.setText( "" );
        mainTextView.append( "Android version: " + Integer.valueOf(android.os.Build.VERSION.SDK) + "\n" );

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if( bluetoothAdapter == null ) {
            Toast.makeText( this, "Bluetooth not available", Toast.LENGTH_LONG ).show();
            return;
        } else {
            device = new TGDevice(bluetoothAdapter, handler);
        }

        mainTextView.append("NeuroSky: " + TGDevice.version + " " + TGDevice.build_title);
        mainTextView.append("\n" );
    }
    private void startTimer() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                setData();
            }
        };

        Timer timer = new Timer();
        timer.schedule(timerTask, 1000, 1600);
    }

    private void setFFT() {
        try {
            soundAnalyzer = new SoundAnalyzer();
        } catch (Exception e) {
            Toast.makeText(this, "뇌파를 읽을 수 없습니다.", Toast.LENGTH_LONG).show();
        }

        SinglePracticeUIObserver uiObserver = new SinglePracticeUIObserver(this);
        soundAnalyzer.addObserver(uiObserver);

        uiObserver.updateUI();
    }

    private void setChart() {
        mChart = (RadarChart) findViewById(R.id.chart1);
        mChart.setBackgroundColor(Color.rgb(60, 65, 82));

        mChart.getDescription().setEnabled(false);

        mChart.setWebLineWidth(1f);
        mChart.setWebColor(Color.LTGRAY);
        mChart.setWebLineWidthInner(1f);
        mChart.setWebColorInner(Color.LTGRAY);
        mChart.setWebAlpha(100);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTextSize(9f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(0f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            private String[] mActivities = new String[]{"Delta", "theta", "alpha", "low betta", "high betta", "gamma", "smr"};

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mActivities[(int) value % mActivities.length];
            }
        });
        xAxis.setTextColor(Color.WHITE);

        YAxis yAxis = mChart.getYAxis();
        yAxis.setLabelCount(7, false);
        yAxis.setTextSize(9f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(100f);
        yAxis.setDrawLabels(true);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(5f);
        l.setTextColor(Color.WHITE);

        entries1 = new ArrayList<>();

        set1 = new RadarDataSet(entries1, "brain wave");
        set1.setColor(Color.rgb(103, 110, 129));
        set1.setFillColor(Color.rgb(103, 110, 129));
        set1.setDrawFilled(true);
        set1.setFillAlpha(180);
        set1.setLineWidth(2f);
        set1.setDrawHighlightCircleEnabled(true);
        set1.setDrawHighlightIndicators(false);

        data = new RadarData(set1);
        data.setValueTextSize(7f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.WHITE);

        mChart.setData(data);
    }

    ArrayList<RadarEntry> entries1;
    RadarDataSet set1;
    RadarData data;

    public void setData() {
        entries1.clear();

        for (int i = 0; i < wave.length; i++) {
            entries1.add(new RadarEntry((wave[i])));
        }
        YAxis yAxis = mChart.getYAxis();
        yAxis.setLabelCount(7, false);
        yAxis.setTextSize(9f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(100);
        yAxis.setDrawLabels(false);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                set1.notifyDataSetChanged();
                data.notifyDataChanged();
                mChart.notifyDataSetChanged();
                mChart.invalidate();
                mChart.animateXY(
                        800, 800,
                        Easing.EasingOption.EaseInOutQuad,
                        Easing.EasingOption.EaseInOutQuad);
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0)
        {
            device.close();
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onStart() {
        super.onStart();
        //If BT is not on, request that it be enabled.
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, 1);
        }
    }

    @Override
    public void onPause() {
        // device.close();
        super.onPause();
    }

    @Override
    public void onStop() {
        device.close();
        super.onStop();

    }

    final Handler handler = new Handler() {
        @Override
        public void handleMessage( Message msg ) {
            switch( msg.what ) {
                case TGDevice.MSG_STATE_CHANGE:

                    switch( msg.arg1 ) {
                        case TGDevice.STATE_IDLE:
                            break;
                        case TGDevice.STATE_CONNECTING:
                            mainTextView.append( "Connecting...\n" );
                            break;
                        case TGDevice.STATE_CONNECTED:
                            mainTextView.append( "Connected.\n" );
                            device.start();
                            //TODO: test 로 10초 동안 데이터받고 담으로 넘긴다.
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(EEGActivity.this, MusicListActivity.class);
                                    intent.putExtra(CheckEmotionActivity.KEY_EMOTION, emotion);
                                    intent.putExtra(KEY_AVG_ATT, calcAverage(attentionList));
                                    intent.putExtra(KEY_AVG_MED, calcAverage(meditationList));
                                    intent.putExtra(KEY_STDDEV_ATT, calcStandardDeviation(attentionList));
                                    intent.putExtra(KEY_STDDEV_MED, calcStandardDeviation(meditationList));
                                    startActivity(intent);
                                }
                            }, 20000);
                            break;
                        case TGDevice.STATE_NOT_FOUND:
                            mainTextView.append( "Could not connect any of the paired BT devices.  Turn them on and try again.\n" );
                            break;
                        case TGDevice.STATE_ERR_NO_DEVICE:
                            mainTextView.append( "No Bluetooth devices paired.  Pair your device and try again.\n" );
                            break;
                        case TGDevice.STATE_ERR_BT_OFF:
                            mainTextView.append( "Bluetooth is off.  Turn on Bluetooth and try again." );
                            break;

                        case TGDevice.STATE_DISCONNECTED:
                            mainTextView.append( "Disconnected.\n" );
                    } /* end switch on msg.arg1 */
                    break;

                // 로우 데이터 처리하는 부분.
                case TGDevice.MSG_RAW_DATA:

                    int index = (int) ((msg.arg1 + 2048) / 583);
                    index = index >= 6 ? 6 : index;
                    index = index < 0 ? 0 : index;

                    wave[index] = (int) ((((float)msg.arg1 + 2048f) * 100f)/ 4096f);

                    wave[index] = wave[index] >= 100 ? 100 : wave[index];

                    //mainTextView.setText("wave : " + index);
                    Log.d("raw","raw : " + msg.arg1);
                    Log.d("index", "index : " + index + " , wave : " + wave[index]);
                    raw[rawIndex] = (short) msg.arg1;
                    rawIndex++;
                    if (rawIndex >= SoundAnalyzer.AUDIO_DATA_SIZE) {
                        soundAnalyzer.startAudioReaderThread(rawIndex, raw);
                        soundAnalyzer.start();
                        rawIndex = 0;
                    }

                    break;
                case TGDevice.MSG_ATTENTION:
                    attention = msg.arg1;
                    //Log.d("attention","attention : " + attention);
                    attentionTextView.setText( "Attention: " + attention+ "\n" );
                    attentionList.add(attention);
                    break;
                case TGDevice.MSG_MEDITATION:
                    meditation  = msg.arg1;
                    //Log.d("meditation","meditation : " + meditation);
                    meditationTextView.setText( "Meditation: " + meditation + "\n" );
                    meditationList.add(meditation);
                    break;
            }
        } /* end handleMessage() */

    };

    public void doStuff(View view) {
        if( device.getState() != TGDevice.STATE_CONNECTING && device.getState() != TGDevice.STATE_CONNECTED ) {

            device.connect( rawEnabled );
        }

    }

    @Override
    public void generateNextPractice() {
    }

    @Override
    protected TextView getMessageView() {
        return mainTextView;
    }

    public double calcStandardDeviation (ArrayList<Integer> data) {
        double avg = 0.0;
        double avg2 = 0.0;
        for(Integer i : data) {
            avg += i;
            avg2 += Math.pow(i,2);
        }

        avg /= data.size();
        avg = Math.pow(avg,2);
        avg2 /= data.size();

        return Math.sqrt(avg - avg2);
    }

    public double calcAverage(ArrayList<Integer> data) {
        double avg = 0.0;
        for(Integer i : data) {
            avg += i;
        }
        return avg / data.size();
    }
}