package com.myfirstmapgoogle.fiestamap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class PopUpActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup);

        Button adjust = findViewById(R.id.popup_adjust);
        Button del = findViewById(R.id.popup_del);
        Intent i = getIntent();
        final int num = i.getIntExtra("ORDER",-1);

        adjust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("ORDER",num);
                setResult(RESULT_OK,intent);
                finish();

            }
        });
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("ORDER",num);
                setResult(RESULT_CANCELED,intent);
                finish();
            }
        });
    }

    /**
     * 바깥레이어 클릭시 안닫히게 해줌
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return true;
        }
        return true;
    }

    /**
     * 기기 백버튼 막기
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(1,intent);
        finish();
        return;
    }

}