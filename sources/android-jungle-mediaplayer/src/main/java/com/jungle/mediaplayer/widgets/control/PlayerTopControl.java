/**
 * Android Jungle-MediaPlayer framework project.
 *
 * Copyright 2016 Arno Zhang <zyfgood12@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jungle.mediaplayer.widgets.control;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.jungle.mediaplayer.R;

import java.text.SimpleDateFormat;

public class PlayerTopControl extends FrameLayout {

    public interface Listener {
        void onBackBtnClicked();
    }


    private Listener mListener;


    public PlayerTopControl(Context context) {
        super(context);
        initLayout(context);
    }

    public PlayerTopControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public PlayerTopControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    private void initLayout(Context context) {
    }

    public void createDefault() {
        create(R.layout.layout_default_player_top_control);
    }

    public void create(int resId) {
        View.inflate(getContext(), resId, this);

        findViewById(R.id.player_back_zone).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onBackBtnClicked();
            }
        });

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        getContext().registerReceiver(mBatteryReceiver, filter);

        updateSystemTime(false);
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void show() {
        removeCallbacks(mUpdateSystemTimeRunnable);
        updateSystemTime(true);

        setVisibility(View.VISIBLE);
    }

    public void setTitle(String title) {
        TextView titleView = (TextView) findViewById(R.id.player_title);
        titleView.setText(title);
    }

    public ViewGroup getTitleBarExtraContainer() {
        return (ViewGroup) findViewById(R.id.player_title_extra_container);
    }

    public void doDestroy() {
        removeCallbacks(mUpdateSystemTimeRunnable);

        try {
            getContext().unregisterReceiver(mBatteryReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updatePowerStatus(intent);
        }
    };

    private Runnable mUpdateSystemTimeRunnable = new Runnable() {
        @Override
        public void run() {
            updateSystemTime(true);
        }
    };

    private void updateSystemTime(boolean schedule) {
        TextView systemTimeView = (TextView) findViewById(R.id.player_system_time);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        systemTimeView.setText(format.format(System.currentTimeMillis()));

        if (schedule) {
            postDelayed(mUpdateSystemTimeRunnable, 10 * 1000);
        }
    }

    private void updatePowerStatus(Intent intent) {
        float current = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        float total = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);

        View powerIconView = findViewById(R.id.player_power_icon);
        View inChargeView = findViewById(R.id.player_in_charge_icon);
        boolean inChargeNow = status == BatteryManager.BATTERY_STATUS_FULL
                || status == BatteryManager.BATTERY_STATUS_CHARGING;
        inChargeView.setVisibility(inChargeNow ? View.VISIBLE : View.GONE);

        int resId = R.drawable.battery0;
        float percent = current / total;
        if (percent <= 0.1) {
            resId = R.drawable.battery0;
        } else if (percent <= 0.3) {
            resId = R.drawable.battery1;
        } else if (percent <= 0.5) {
            resId = R.drawable.battery2;
        } else if (percent <= 0.8) {
            resId = R.drawable.battery3;
        } else {
            resId = R.drawable.battery4;
        }

        powerIconView.setBackgroundResource(resId);
    }

    public void hide() {
        setVisibility(View.GONE);
        removeCallbacks(mUpdateSystemTimeRunnable);
    }
}