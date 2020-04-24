package com.example.rangefinder_tradition.bluetoothdemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rangefinder_tradition.bluetoothdemo.adapter.ListAdapter;
import com.example.rangefinder_tradition.bluetoothdemo.thread.ConnectThread;
import com.example.rangefinder_tradition.bluetoothdemo.thread.ConnectedThread;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ClientActivity extends AppCompatActivity implements
        View.OnClickListener,
        ConnectThread.ConnectCallBack {

    private static final String TAG = "ClientActivity";

    private TextView currentString;
    private ListView mListView;
    private RelativeLayout mRelativeLayoutInput;
    private Button btnSend;
    private EditText inputEditText;

    private ListAdapter adapter;
    private BluetoothAdapter mBluetoothAdapter;

    private List<BluetoothDevice> deviceList = new ArrayList<>();
    private ConnectedThread connectedThread;

    public static final int CONNECT_SUCCEED = 101;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONNECT_SUCCEED:
                    mRelativeLayoutInput.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        initView();
        initListView();
        registerReceiver();
    }

    private void initView() {
        currentString = findViewById(R.id.tv);
        mListView = findViewById(R.id.listview);
        mRelativeLayoutInput = findViewById(R.id.rl_et_view);
        btnSend = findViewById(R.id.btn_send);
        inputEditText = findViewById(R.id.et);

        currentString.setOnClickListener(this);
        btnSend.setOnClickListener(this);
    }

    private void initListView() {
        adapter = new ListAdapter(this, deviceList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 藍芽配對
                BluetoothDevice device = deviceList.get(position);
                pin(device);
            }
        });
    }

    private void registerReceiver() {
        // 掃描
        IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter filter3 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        IntentFilter filter4 = new IntentFilter(BluetoothDevice.ACTION_NAME_CHANGED);
        // 配對
        IntentFilter filter5 = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        // 連接
        IntentFilter filter6 = new IntentFilter(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);

        registerReceiver(receiver, filter1);
        registerReceiver(receiver, filter2);
        registerReceiver(receiver, filter3);
        registerReceiver(receiver, filter4);
        registerReceiver(receiver, filter5);
        registerReceiver(receiver, filter6);
    }

    private void openBlueTooth() {
        if (mBluetoothAdapter == null) {
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }

        // 掃描藍芽設備
        mBluetoothAdapter.startDiscovery();
    }

    /**
     * 配对，配对结果通过广播返回
     * @param device
     */
    public void pin(BluetoothDevice device) {
        if (device == null || !mBluetoothAdapter.isEnabled()) {
            return;
        }

        //配对之前把扫描关闭
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        //判断设备是否配对，没有就进行配对
        if (device.getBondState() == BluetoothDevice.BOND_NONE ) {
            try {

                //Method createBondMethod = device.getClass().getMethod("createBond");
                //Boolean returnValue = (Boolean) createBondMethod.invoke(device);
                //returnValue.booleanValue();

                //BTReceiverUtils.setPin(device.getClass(), device, "123456"); // 手机和蓝牙采集器配对
                BTReceiverUtils.createBond(device.getClass(), device);
                //BTReceiverUtils.cancelPairingUserInput(device.getClass(), device);

                //BTReceiverUtils.createBond(device.getClass(), device);
                //BTReceiverUtils.setPairingConfirmation(device.getClass(), device, true);
                //2.終止有序廣播
                //Log.i("order...", "isOrderedBroadcast:"+isOrderedBroadcast()+",isInitialStickyBroadcast:"+isInitialStickyBroadcast());
                //receiver.abortBroadcast();//如果沒有將廣播終止，則會出現一個一閃而過的配對框。
                //3.呼叫setPin方法進行配對...
                //boolean ret = BTReceiverUtils.setPin(device.getClass(), device, "1234");

                //BTReceiverUtils.autoBond(device.getClass(), device, "0000");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Log.i(TAG, "action = " + action);

            switch (action) {
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    // 掃描開始
                    Log.i(TAG, "--- 掃描開始 ---");
                    currentString.setText("掃描開始...");
                    break;
                case BluetoothDevice.ACTION_FOUND: {
                    //发现蓝牙
                    /*BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (!TextUtils.isEmpty(device.getName())) {
                        Log.i(TAG, "--- 發現了：" + device.getName() + " ---");
                        deviceList.add(device);
                        // 更新蓝牙列表
                        adapter.notifyDataSetChanged();
                    }*/
                }
                case BluetoothDevice.ACTION_NAME_CHANGED: {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (!TextUtils.isEmpty(device.getName())) {
                        Log.i(TAG, "--- 發現了：" + device.getName() + " ---");
                        deviceList.add(device);
                        // 更新蓝牙列表
                        adapter.notifyDataSetChanged();
                    }
                }
                break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    // 掃描结束
                    Log.i(TAG, "--- 掃描完成 ---");
                    currentString.setText("掃描完成");

                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED: {
                    // 配對狀態
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    switch (device.getBondState()) {
                        case BluetoothDevice.BOND_NONE:
                            Log.i(TAG, "--- 配對失敗 ---");
                            currentString.setText("配對失敗");
                            break;
                        case BluetoothDevice.BOND_BONDING:
                            Log.i(TAG, "--- 配對中... ---");
                            currentString.setText("配對中... ");
                            break;
                        case BluetoothDevice.BOND_BONDED:
                            Log.i(TAG, "--- 配對成功 ---");
                            currentString.setText("配對成功");
                            new ConnectThread(device, mBluetoothAdapter, ClientActivity.this)
                                    .start();
                            break;
                    }
                    break;
                }
                case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED: {
                    // 連接狀態
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice
                            .EXTRA_DEVICE);
                    switch (device.getBondState()) {
                        case BluetoothAdapter.STATE_DISCONNECTED:
                            // 未連接
                            Log.i(TAG, "--- 未連接 ---");
                            break;
                        case BluetoothAdapter.STATE_CONNECTING:
                            // 連接中
                            Log.i(TAG, "--- 連接中... ---");
                            break;
                        case BluetoothAdapter.STATE_CONNECTED:
                            // 連接成功
                            Log.i(TAG, "--- 連接成功 ---");
                            break;
                    }
                }
                break;
            }
        }
    };

    @Override
    public void onConnectSucceed(BluetoothSocket serverSocket) {

        Message msg = new Message();
        msg.what = CONNECT_SUCCEED;
        handler.sendMessage(msg);

        connectedThread = new ConnectedThread(serverSocket, null);
        connectedThread.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv:

                //要模糊定位权限才能搜索到蓝牙
                //每次扫描前清空列表
                deviceList.clear();
                adapter.notifyDataSetChanged();

                //授权成功后打开蓝牙
                openBlueTooth();

                break;
            case R.id.btn_send:
                String msg = inputEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(msg)) {
                    connectedThread.write(msg.getBytes());
                } else {
                    Toast.makeText(this, "請輸入內容 ", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
