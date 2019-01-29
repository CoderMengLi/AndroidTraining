package ramon.better.com.components.activity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ramon.better.com.components.R;
import ramon.better.com.components.service.BindService;
import ramon.better.com.components.service.RSSPullService;
import ramon.better.com.components.service.StartService;

public class ServiceTestActivity extends AppCompatActivity {
    private static final String TAG = "BindServiceTag";
    // start 的方式
    private Button startServiceBtn;
    private Button stopServiceBtn;
    // bind 的方式
    private Button bindServiceBtn;
    private Button unbindServiceBtn;
    private Button getStatusBtn;
    private Context mContext;

    // intentService
    private Button intentServiceBtn;


    //保持所启动Service的IBinder对象
    BindService.MyBinder binder;

    //定义一个ServiceConnection对象
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i(TAG,"service connected");
            //获取Service的onBind方法所返回的MyBinder对象
            binder = (BindService.MyBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(TAG,"Service disconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_test);
        init();
    }

    private void init() {
        mContext = ServiceTestActivity.this;
        startServiceBtn = (Button)findViewById(R.id.start_service_btn);
        startServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, StartService.class);
                mContext.startService(intent);
            }
        });
        stopServiceBtn = findViewById(R.id.stop_service_btn);
        stopServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, StartService.class);
                mContext.stopService(intent);
            }
        });
        bindServiceBtn = (Button)findViewById(R.id.bind_service_btn);
        bindServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 绑定服务
                Intent bindIntent = new Intent(mContext, BindService.class);
                bindService(bindIntent, connection, Service.BIND_AUTO_CREATE);
            }
        });

        unbindServiceBtn = (Button)findViewById(R.id.unbind_service_btn);
        unbindServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 解除绑定
                unbindService(connection);
            }
        });
        // 获取 service 状态
        getStatusBtn = (Button)findViewById(R.id.get_status_btn);
        getStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取显示Servcie的count值
                Toast.makeText(mContext, "count value is " + binder.getCount(),Toast.LENGTH_SHORT).show();
            }
        });

        // 启动 IntentService
        intentServiceBtn = (Button)findViewById(R.id.intent_service_btn);
        intentServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mServiceIntent = new Intent(mContext, RSSPullService.class);
                mServiceIntent.setData(Uri.parse("send from activity"));
                mContext.startService(mServiceIntent);
            }
        });

        // 注册 IntentService 的广播
        IntentFilter mStatusIntentFilter = new IntentFilter(
                RSSPullService.Constants.BROADCAST_ACTION);
        DownloadStateReceiver mDownloadStateReceiver = new DownloadStateReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mDownloadStateReceiver, mStatusIntentFilter);
        Log.i("meng", "The thread of Activity " + Thread.currentThread().toString());
    }

    // 接收 IntentService 的广播状态
    private class DownloadStateReceiver extends BroadcastReceiver {
        private DownloadStateReceiver() {
        }
        public void onReceive(Context context, Intent intent) {
            String responseString = intent.getStringExtra(RSSPullService.Constants.EXTENDED_DATA_STATUS);
            Log.i("meng","response string is :" + responseString);
        }
    }
}
