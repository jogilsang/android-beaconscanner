package help.monitor.beaconscanner;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

public class MainActivity extends Activity implements
        TextToSpeech.OnInitListener {

    BluetoothAdapter mBluetoothAdapter;

    BluetoothLeScanner mBluetoothLeScanner;

    BluetoothLeAdvertiser mBluetoothLeAdvertiser;

    private static final int PERMISSIONS = 100;

    Vector<Beacon> beacon;

    BeaconAdapter beaconAdapter;

    ListView beaconListView;

    ScanSettings.Builder mScanSettings;

    List<ScanFilter> scanFilters;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.KOREAN);

    // 뷰 선언
    TextView txtContent;
    Button btnTel;

    // TTS
    public TextToSpeech tts;

    // 번호
    public String telNum ="tel:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 권한요청
        getPermission();

        // 뷰 설정
        initView();

        // 리스너 설정
        setListener();

        // beaconListView = (ListView) findViewById(R.id.beaconListView);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        beacon = new Vector<>();
        mScanSettings = new ScanSettings.Builder();
        mScanSettings.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
        // 스캔 주기를 2초, 디폴트 10초

        // 특정 MAC주소 기기에 한해서 스캔, 없을시 모든 기기 감지
        // ScanSettings scanSettings = mScanSettings.build();
        // scanFilters = new Vector<>();
        // ScanFilter.Builder scanFilter = new ScanFilter.Builder();

        // scanFilter.setDeviceAddress(getString(R.string.beacon_1)); //ex) 00:00:00:00:00:00
        // scanFilter.setDeviceAddress(getString(R.string.beacon_2)); //ex) 00:00:00:00:00:00
        // scanFilter.setDeviceAddress(getString(R.string.beacon_3)); //ex) 00:00:00:00:00:00

        // ScanFilter scan = scanFilter.build();
        // scanFilters.add(scan);

        // filter와 settings 기능을 사용하지 않을 때는, 하단 사용
        mBluetoothLeScanner.startScan(mScanCallback);

        // filter와 settings 기능을 사용할시, 하단 사용
        //mBluetoothLeScanner.startScan(scanFilters, scanSettings, mScanCallback);
    }

    private void setListener() {

        // 담당자 전화 번호 클릭시
        btnTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 내용이 비었음
                if(telNum.equals("tel:")) {

                }
                // 내용이 있음
                else {

                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Toast.makeText(MainActivity.this, getString(R.string.toast_tel), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent("android.intent.action.CALL", Uri.parse(telNum)));

                            dialog.dismiss();     //닫기
                        }
                    });
                    alert.setMessage("긴급전화만 연결할 수 있습니다");
                    alert.show();

                }

            }
        });

    }

    private void initView() {

        tts = new TextToSpeech(MainActivity.this, this);

        btnTel = (Button) findViewById(R.id.btn_tel);
        txtContent = (TextView) findViewById(R.id.txt_content);

    }

    public void getPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CALL_PHONE
                },
                PERMISSIONS);
    }

    // 스캔을 하게된다면
    ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            try {
                ScanRecord scanRecord = result.getScanRecord();
                Log.d("getTxPowerLevel()", scanRecord.getTxPowerLevel() + "");
                Log.d("onScanResult()", result.getDevice().getAddress() + "\n" + result.getRssi() + "\n" + result.getDevice().getName()
                        + "\n" + result.getDevice().getBondState() + "\n" + result.getDevice().getType());

                // MAC주소 : result.getDevice().getAddress()
                final ScanResult scanResult = result;
                final String scannedMacAddr = result.getDevice().getAddress();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                // TODO : 비콘인식시 화면에 텍스트 보이게하기
                                // TODO : TTS 적용시키기

                                //TODO : beacon 1, 출입구
                                if (scannedMacAddr.equals(getString(R.string.beacon_1))) {
                                    txtContent.setText(getString(R.string.text_1));
                                    speakJust(getString(R.string.text_1));
                                    Toast.makeText(MainActivity.this, getString(R.string.location_1) + " 비콘이 인식됬습니다.", Toast.LENGTH_SHORT).show();
                                    telNum = getString(R.string.tel_1);

                                }

                                //TODO : beacon 2, 계단
                                else if (scannedMacAddr.equals(getString(R.string.beacon_2))) {
                                    txtContent.setText(getString(R.string.text_2));
                                    speakJust(getString(R.string.text_2));
                                    Toast.makeText(MainActivity.this, getString(R.string.location_2) + " 비콘이 인식됬습니다.", Toast.LENGTH_SHORT).show();
                                    telNum = getString(R.string.tel_2);
                                }

                                //TODO : beacon 3, 건강지원실
                                else if (scannedMacAddr.equals(getString(R.string.beacon_3))) {
                                    txtContent.setText(getString(R.string.text_3));
                                    speakJust(getString(R.string.text_3));
                                    Toast.makeText(MainActivity.this, getString(R.string.location_3) + " 비콘이 인식됬습니다.", Toast.LENGTH_SHORT).show();
                                    telNum = getString(R.string.tel_3);

                                }

                                // beacon.add(0, new Beacon(scanResult.getDevice().getAddress(), scanResult.getRssi(), simpleDateFormat.format(new Date())));
                                // beaconAdapter = new BeaconAdapter(beacon, getLayoutInflater());
                                // beaconListView.setAdapter(beaconAdapter);
                                // beaconAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }).start();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.d("onBatchScanResults", results.size() + "");
        }

        // 스캔 실패시
        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.d("onScanFailed()", errorCode + "");
        }
    };

    // 앱 종료시 등
    @Override
    protected void onDestroy() {

        // Don't forget to shutdown!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

        super.onDestroy();
        mBluetoothLeScanner.stopScan(mScanCallback);


    }


    // TODO : TTS
    public void speakJust(String text) {

        // String text = layout_2_edit.getText().toString();

        if(!tts.isSpeaking()) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }

    }

    @Override
    public void onInit(int status) {
        // TODO Auto-generated method stub

        if (status == TextToSpeech.SUCCESS) {

            // to be changed
            // 한국어로 설정
            // 스마트폰 사용자 국적으로 받는걸로 바꿀예정임
            int result = tts.setLanguage(Locale.KOREAN);

            // tts.setPitch(5); // set pitch level
            // tts.setSpeechRate(2); // set speech speed rate

            // 설정한 언어가 지원이 안되는거라면...~
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language is not supported");
            } else {

                speakJust(getString(R.string.app_name) + "에 오신것을 환영합니다");
            }

        } else {
            Log.e("TTS", "Initilization Failed");
        }
    }

}
