package com.myfirstmapgoogle.fiestamap;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class InfoEnterActivity extends Activity {
    private EditText et_objectName;
    private EditText et_objectLocation;
    private EditText et_memo;
    private Geocoder geocoder;

    protected void onCreate(Bundle savedInstanceState) {
        final double Latitude;
        final double Longitude;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.infoenteractivity);

        Button btn_bike = findViewById(R.id.btn_bike);
        Button btn_book = findViewById(R.id.btn_book);
        Button btn_labtop = findViewById(R.id.btn_laptop);
        Button btn_car = findViewById(R.id.btn_car);
        Button btn_add_ok = findViewById(R.id.btn_add_ok); // 확인 버튼
        Button btn_add_cancel = findViewById(R.id.btn_add_cancel); // 취소 버튼
        et_objectName = findViewById(R.id.et_objectName);
        et_objectLocation = findViewById(R.id.et_objectLocation);
        et_memo = findViewById(R.id.et_memo);
        Button btn_locationNow = findViewById(R.id.btn_locationNow); // 현재위치 버튼


        final TextView tv_dateNow; //현재 시간
        long now = System.currentTimeMillis();    // 현재시간을 msec 으로 구한다.
        Date date = new Date(now);    // 현재시간을 date 변수에 저장한다.
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm");    // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        String formatDate = sdfNow.format(date);     // nowDate 변수에 값을 저장한다.
        tv_dateNow = (TextView) findViewById(R.id.tv_dateNow);
        tv_dateNow.setText(formatDate);


        Intent intent = getIntent();
        Longitude = intent.getDoubleExtra("Longitude",0);
        Latitude=intent.getDoubleExtra("Latitude",0);

        geocoder = new Geocoder(this);
        btn_locationNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Address> list = null;
                try {
                    list = geocoder.getFromLocation(Latitude,Longitude, 10);
                } catch (IOException e) {
                    Log.e("test", "주소변환 에러");
                }
                if (list != null) {
                    if (list.size() == 0) {
                        et_objectLocation.setHint("주소가 없음");
                    } else {
                        et_objectLocation.setText(list.get(0).getAddressLine(0).toString());
                    }
                }
            }
        });

        //확인 버튼
        btn_add_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et_objectName.getText().toString();
                String place = et_objectLocation.getText().toString();
                String memo = et_memo.getText().toString();
                String time = tv_dateNow.getText().toString();
                et_objectName.setText("");
                et_objectLocation.setText("");
                et_memo.setText("");
                Intent i = new Intent(InfoEnterActivity.this,MapsActivity.class);
                i.putExtra("name",name);
                i.putExtra("place",place);
                i.putExtra("time",time);
                i.putExtra("memo",memo);
                setResult(RESULT_OK,i);
                finish();

            }
        });
        //취소 버튼
        btn_add_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_objectName.setText("");
                et_objectLocation.setText("");
                et_memo.setText("");
                Intent i = new Intent(InfoEnterActivity.this,MapsActivity.class);
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        //자전거 버튼
        btn_bike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_objectName.setText("자전거");
            }
        });
        //롱 클릭 시 이름 따로 지정
//        btn_bike.setOnLongClickListener(new View.OnLongClickListener(){
//            @Override
//            public  boolean onLongClick(View v){
//
//            }
//        });
        //책 버튼
        btn_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_objectName.setText("책");
            }
        });
        //노트북 버튼
        btn_labtop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_objectName.setText("노트북");
            }
        });
        //자동차 버튼
        btn_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_objectName.setText("자동차");
            }
        });
        //더보기 버튼
//        btn_more.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                et_objectName.setText("미구현");
//            }
//        });

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

}


