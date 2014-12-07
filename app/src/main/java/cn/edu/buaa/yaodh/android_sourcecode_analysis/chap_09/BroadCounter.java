package cn.edu.buaa.yaodh.android_sourcecode_analysis.chap_09;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.edu.buaa.yaodh.android_sourcecode_analysis.Constants;
import cn.edu.buaa.yaodh.android_sourcecode_analysis.R;

public class BroadCounter extends ActionBarActivity implements View.OnClickListener{
    private TextView tvCounter;
    private Button btnStartCounter;
    private Button btnStopCounter;

    private ICounterService counterService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broad_counter);

        tvCounter = (TextView) findViewById(R.id.counter);
        btnStartCounter = (Button) findViewById(R.id.start_counter);
        btnStopCounter = (Button) findViewById(R.id.stop_counter);

        btnStartCounter.setOnClickListener(this);
        btnStopCounter.setOnClickListener(this);

        Intent intent = new Intent(this, CounterService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        Log.i(Constants.TAG, "BroadCounter Activity Created.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(CounterService.BROADCAST_COUNTER_ACTION);
        registerReceiver(counterActionReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(counterActionReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_broad_counter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_counter:
                if(counterService == null) return;
                counterService.startCounter(0);
                btnStartCounter.setEnabled(false);
                btnStopCounter.setEnabled(true);
                break;
            case R.id.stop_counter:
                if(counterService == null) return;
                counterService.stopCounter();
                btnStartCounter.setEnabled(true);
                btnStopCounter.setEnabled(false);
                break;
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            counterService = ((CounterService.CounterBinder)service).getService();
            Log.i(Constants.TAG, "Counter Service Connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            counterService = null;
            Log.i(Constants.TAG, "Counter Service DisConnected");
        }
    };

    private BroadcastReceiver counterActionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int counter = intent.getIntExtra(CounterService.COUNTER_VALUE, 0);
            tvCounter.setText("Counter: " + counter);
        }
    };
}
