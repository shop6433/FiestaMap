package com.myfirstmapgoogle.fiestamap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

public class EditObjectName extends Activity {
    EditText et_setName;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 바 없애기

        setContentView(R.layout.editobjectname);

        et_setName = (EditText)findViewById(R.id.et_setName); // 팝업창의 editText 객체 생성

        //데이터 가져오기
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        et_setName.setText(data); // 기본 설정되있던 값으로 표기

    }
    public void activityClose(View v){ //확인 버튼 클릭시
        String name = et_setName.getText().toString(); // editText의 내용을 string으로 저장
        Intent intent = new Intent();
        intent.putExtra("result",  name);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 바깥레이어 클릭 시 안닫히게 해줌
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

    /**
     * 기기 백 버튼 막기
     */
    @Override
    public void onBackPressed() {
        return;
    }
}
