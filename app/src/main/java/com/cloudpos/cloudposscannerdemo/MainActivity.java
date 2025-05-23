package com.cloudpos.cloudposscannerdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cloudpos.DeviceException;
import com.cloudpos.advance.ext.POSTerminalAdvance;
import com.cloudpos.advance.ext.scanner.IScanCallBack;
import com.cloudpos.advance.ext.scanner.IScannerDevice;
import com.cloudpos.advance.ext.scanner.ScanParameter;
import com.cloudpos.advance.ext.scanner.ScanResult;
import com.cloudpos.cloudposscannerdemo.UI.ListViewAdapter;

public class MainActivity extends AppCompatActivity {

    private TextView mTv;
    private ListView mListView;
    private ImageView mImage;
    private Context mContext;
    private IScannerDevice scannerDevice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        initView();
        initUI();
        scannerDevice = POSTerminalAdvance.getInstance().getScannerDevice();
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.listview);
        mTv = (TextView) findViewById(R.id.textview);
        mImage = (ImageView) findViewById(R.id.imageview);
    }

    private void initUI() {
        mTv.setMovementMethod(ScrollingMovementMethod.getInstance());
        mListView.setAdapter(new ListViewAdapter(mContext));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mTv.setText("");
                mImage.setImageBitmap(null);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        switch (position) {
                            case 0:
                                open();
                                break;
                            case 1:
                                synchronizationScan();
                                break;
                            case 2:
                                continuousScan();
                                break;
                            case 3:
                                noFullScreenScan();
                                break;
                            case 4:
                                returnBitmapScan();
                                break;
                            case 5:
                                noBoxScan();
                                break;
                            case 6:
                                scanFormat();
                                break;
                            case 7:
                                customScanWindow();
                                break;
                            case 8:
                                close();
                                break;
                        }
                    }
                }).start();

            }
        });
    }

    private void open() {
        try {
            boolean result = scannerDevice.open(mContext);
            runOnUiThread(() -> {
                mTv.setText("open result = " + result);
            });

        } catch (DeviceException e) {
            e.printStackTrace();
        }
    }

    private void close() {
        try {
            scannerDevice.close();
            runOnUiThread(() -> {
                mTv.setText("close success");
            });
        } catch (DeviceException e) {
            e.printStackTrace();
        }
    }

    /**
     * Synchronized scanning
     */
    private void synchronizationScan() {
        ScanParameter parameter = new ScanParameter();
        try {
            ScanResult result = scannerDevice.scanBarcode(parameter);
            sendResultMessage(result);
        } catch (DeviceException e) {
            e.printStackTrace();
        }
    }

    private void sendResultMessage(ScanResult result) {
        Message message = new Message();
        message.what = 0;
        message.obj = result;
        handler.sendMessage(message);
    }

    /**
     * Continuous scanning
     */

    private void continuousScan() {
        ScanParameter parameter = new ScanParameter();
        try {
            scannerDevice.startScan(parameter, new IScanCallBack() {
                @Override
                public void foundBarcode(ScanResult scanResult) {
                    sendResultMessage(scanResult);
                    try {
                        scannerDevice.stopScan();
                    } catch (DeviceException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (DeviceException e) {
            e.printStackTrace();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    ScanResult obj = (ScanResult) msg.obj;
                    mTv.setText("Result : \n" + obj);
                    break;
            }
        }
    };

    /**
     * Not full screen scanning
     */
    private void noFullScreenScan() {
        ScanParameter parameter = new ScanParameter();
        parameter.set(ScanParameter.KEY_UI_WINDOW_TOP, 20);
        parameter.set(ScanParameter.KEY_UI_WINDOW_LEFT, 20);
        parameter.set(ScanParameter.KEY_UI_WINDOW_WIDTH, 300);
        parameter.set(ScanParameter.KEY_UI_WINDOW_HEIGHT, 400);
        parameter.set(ScanParameter.KEY_SCAN_SECTION_WIDTH, 200);
        parameter.set(ScanParameter.KEY_SCAN_SECTION_HEIGHT, 200);
        try {
            ScanResult result = scannerDevice.scanBarcode(parameter);
            sendResultMessage(result);
        } catch (DeviceException e) {
            e.printStackTrace();
        }
    }

    /**
     * Scan and return image
     */
    private void returnBitmapScan() {
        ScanParameter parameter = new ScanParameter();
        parameter.set(ScanParameter.KEY_ENABLE_RETURN_IMAGE, true);
        try {
            ScanResult result = scannerDevice.scanBarcode(parameter);
            Bitmap decodeBitmap = result.getBitmap();
            mImage.setImageBitmap(decodeBitmap);

        } catch (DeviceException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove scanning box
     */
    private void noBoxScan() {
        ScanParameter parameter = new ScanParameter();
        parameter.set(ScanParameter.KEY_ENABLE_SCAN_SECTION, false);
        try {
            ScanResult result = scannerDevice.scanBarcode(parameter);
            sendResultMessage(result);
        } catch (DeviceException e) {
            e.printStackTrace();
        }
    }

    /**
     * Only scan qr code or code128
     */
    private void scanFormat() {
        ScanParameter parameter = new ScanParameter();
        parameter.set(ScanParameter.KEY_DECODEFORMAT, "QR, Code128");
        try {
            ScanResult result = scannerDevice.scanBarcode(parameter);
            sendResultMessage(result);
        } catch (DeviceException e) {
            e.printStackTrace();
        }
    }

    /**
     * Custom scanning window
     */
    private void customScanWindow() {
        ScanParameter parameter = new ScanParameter();
        parameter.set(ScanParameter.KEY_SCAN_SECTION_BORDER_COLOR, Color.rgb(0xFF, 0x00, 0x00));
        parameter.set(ScanParameter.KEY_SCAN_SECTION_CORNER_COLOR, Color.rgb(0x00, 0xFF, 0x00));
        parameter.set(ScanParameter.KEY_SCAN_SECTION_LINE_COLOR, Color.rgb(0x00, 0x00, 0xFF));
        parameter.set(ScanParameter.KEY_DISPLAY_SCAN_LINE, "no");
        parameter.set(ScanParameter.KEY_SCAN_TIP_TEXT, "WizarPOS Shanghai");
        parameter.set(ScanParameter.KEY_SCAN_TIP_TEXTCOLOR, Color.rgb(0xFF, 0x00, 0x00));
        parameter.set(ScanParameter.KEY_SCAN_TIP_TEXTMARGIN, 0);
        parameter.set(ScanParameter.KEY_SCAN_TIP_TEXTSIZE, 25);
        try {
            ScanResult result = scannerDevice.scanBarcode(parameter);
            sendResultMessage(result);
        } catch (DeviceException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        close();
        super.onDestroy();
    }
}
