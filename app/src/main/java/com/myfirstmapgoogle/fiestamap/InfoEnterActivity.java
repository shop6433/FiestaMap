package com.myfirstmapgoogle.fiestamap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.Feature;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class InfoEnterActivity extends Activity {
    private EditText et_objectName;
    private EditText et_objectLocation;
    private EditText et_memo;
    private Geocoder geocoder;

    private String bike = "자전거";
    private String book = "책";
    private String laptop = "노트북";
    private String car = "자동차";

    protected void onCreate(Bundle savedInstanceState) {
        final double Latitude;
        final double Longitude;
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.infoenteractivity);

        loadData(); // 기기에 저장된 바로가기 값을 불러옴

        //정보입력창의 바로가기 버튼
        Button btn_bike = findViewById(R.id.btn_bike);
        Button btn_book = findViewById(R.id.btn_book);
        Button btn_laptop = findViewById(R.id.btn_laptop);
        Button btn_car = findViewById(R.id.btn_car);

        Button btn_locationNow = findViewById(R.id.btn_locationNow); // 현재위치 버튼

        et_objectName = findViewById(R.id.et_objectName);
        et_objectLocation = findViewById(R.id.et_objectLocation);
        et_memo = findViewById(R.id.et_memo);

        Button btn_add_ok = findViewById(R.id.btn_add_ok); // 확인 버튼
        Button btn_add_cancel = findViewById(R.id.btn_add_cancel); // 취소 버튼

        /**
         * 현재시간을 구하기
         * */
        final TextView tv_dateNow; //현재 시간
        long now = System.currentTimeMillis();    // 현재시간을 msec 으로 구한다.
        Date date = new Date(now);    // 현재시간을 date 변수에 저장한다.
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm");    // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        String formatDate = sdfNow.format(date);     // nowDate 변수에 값을 저장한다.
        tv_dateNow = (TextView) findViewById(R.id.tv_dateNow);
        tv_dateNow.setText(formatDate);

        // MapsActivity의 위도 경도값을 가지고 오기
        Intent intent = getIntent();
        Longitude = intent.getDoubleExtra("Longitude",0);
        Latitude=intent.getDoubleExtra("Latitude",0);

        // 현재위치 버튼 클릭 시
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
                        if(list.get(0).getLocality() != null){ // get(0).getLocality() 가 구 인데 표기가 안되는 경우가 있어 조건문으로 표기
                        et_objectLocation.setText(list.get(0).getAdminArea() + " " + list.get(0).getLocality()+ " " + list.get(0).getThoroughfare()+ " " + list.get(0).getFeatureName());
                        } //getAdminArea = 대구광역시, getLocality = 동구 , getThorughfare = 입석동 , FeatureName = 123-45
                        else{
                        et_objectLocation.setText(list.get(0).getAdminArea() + " "  + list.get(0).getThoroughfare()+ " " + list.get(0).getFeatureName());
                        }
                    }
                }
            }
        });
        //확인 버튼 클릭 시
        btn_add_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = tv_dateNow.getText().toString(); // 입력 값을 추출 후
                String name = et_objectName.getText().toString();
                String place = et_objectLocation.getText().toString();
                String memo = et_memo.getText().toString();
                et_objectName.setText(""); // 입력 칸을 공백으로 채우고
                et_objectLocation.setText("");
                et_memo.setText("");
                Intent i = new Intent(InfoEnterActivity.this,MapsActivity.class);
                i.putExtra("time",time); // MapsActivity로 넘겨줌
                i.putExtra("name",name);
                i.putExtra("place",place);
                i.putExtra("memo",memo);
                setResult(RESULT_OK,i);
                finish();
            }
        });
        //취소 버튼 클릭 시
        btn_add_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_objectName.setText(""); // 입력 칸을 공백으로 채우고
                et_objectLocation.setText("");
                et_memo.setText("");
                Intent i = new Intent(InfoEnterActivity.this,MapsActivity.class);
                setResult(RESULT_CANCELED);
                finish(); // 종료
            }
        });
        //자전거 버튼 클릭
        btn_bike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_objectName.setText(bike);
            }
        });
        // 자전거 버튼 롱 클릭
        // 이름 변경 가능
        btn_bike.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public  boolean onLongClick(View v){
                Intent intent = new Intent(InfoEnterActivity.this, EditObjectName.class);
                intent.putExtra("data", bike); // data 이라는 이름으로 string 타입 bike를 넘겨줌
                startActivityForResult(intent,1);
                return true;  // true로 지정해줘야 onclick이 실행안됨.
            }
        });
        // 책 버튼 클릭
        btn_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_objectName.setText(book);
            }
        });
        // 책 버튼 롱 클릭
        btn_book.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public  boolean onLongClick(View v){
                Intent intent = new Intent(InfoEnterActivity.this, EditObjectName.class);
                intent.putExtra("data", book);
                startActivityForResult(intent,2);
                return true;
            }
        });
        // 노트북 버튼 클릭
        btn_laptop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_objectName.setText(laptop);
            }
        });
        // 노트북 버튼 롱 클릭
        btn_laptop.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public  boolean onLongClick(View v){
                Intent intent = new Intent(InfoEnterActivity.this, EditObjectName.class);
                intent.putExtra("data", laptop);
                startActivityForResult(intent,3);
                return true;
            }
        });
        //자동차 버튼
        btn_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_objectName.setText(car);
            }
        });
        // 자동차 버튼 롱 클릭
        btn_car.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public  boolean onLongClick(View v){
                Intent intent = new Intent(InfoEnterActivity.this, EditObjectName.class);
                intent.putExtra("data", car);
                startActivityForResult(intent,4);
                return true;
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

    /**
     * 액티비티가 종료 될 때
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 아이콘 이름변경 팝업 종료시
        if(requestCode==1){ // 자전거
            if(resultCode==RESULT_OK){
                //데이터 받기
                String result = data.getStringExtra("result");
                bike = result;
                et_objectName.setText(bike);
            }
        }
        else if(requestCode==2){ // 책
            if(resultCode==RESULT_OK){
                //데이터 받기
                String result = data.getStringExtra("result");
                book = result;
                et_objectName.setText(book);
            }
        }
        else if(requestCode==3){ // 노트북
            if(resultCode==RESULT_OK){
                //데이터 받기
                String result = data.getStringExtra("result");
                laptop = result;
                et_objectName.setText(laptop);
            }
        }
        else if(requestCode==4){ // 자동차
            if(resultCode==RESULT_OK){
                //데이터 받기
                String result = data.getStringExtra("result");
                car = result;
                et_objectName.setText(car);
            }
        }
        else{
        }
        //데이터 받은 후 저장
        FileOutputStream fos = null;
        try {
            String a = "\r\n";
            fos = openFileOutput("icon_name.txt", Context.MODE_PRIVATE);
            fos.write(bike.getBytes());
            fos.write(a.getBytes());
            fos.write(book.getBytes());
            fos.write(a.getBytes());
            fos.write(laptop.getBytes());
            fos.write(a.getBytes());
            fos.write(car.getBytes());
            fos.write(a.getBytes());
            fos.close();
            Toast.makeText(InfoEnterActivity.this, "저장완료", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *     어플실행시 데이터를 불러옴
     */
    public void loadData() {
        String data = null;
        FileInputStream fis = null;
        String bike = "";
        String book= "";
        String laptop = "";
        String car = "";
        int i = 0;
        try {
            fis = openFileInput("icon_name.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis));
            data = bufferedReader.readLine();
            while (data != null) {
                if (i == 0) bike = data;
                else if (i == 1) book = data;
                else if (i == 2) laptop = data;
                else if (i == 3) car = data;
                i++;
                if(i>3){
                    this.bike = bike;
                    this.book = book;
                    this.laptop = laptop;
                    this.car = car;
                }
                data = bufferedReader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 바깥레이어 클릭시 안닫히게 해줌
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }
}


