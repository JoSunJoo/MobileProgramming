package com.example.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;

public class ModifyBookMark extends AppCompatActivity { //파일을 수정하는 액티비티
    Button edit;//버튼의 객체 저장
    EditText startPoint, endPoint; //출발지와 도착지를 저장

    @Override
    public void onCreate(Bundle savedInstanceState) {//액티비티 실행 시 먼저 시작되는 함수
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_book);//xml파일 설정

        Intent intent = getIntent();//인텐트를 받아옴
        final String old_name = intent.getStringExtra("name");//인텐트에서 데이터를 받아옴

        startPoint = (EditText)findViewById(R.id.new_start);//출발지 저장
        endPoint = (EditText)findViewById(R.id.new_end);//도착지 저장

        String[] point = old_name.split("->");//'->'을 기준으로 문자열 분할
        startPoint.setText(point[0]);//화면의 출발지 입력란에 기존에 입력한 출발지를 입력
        endPoint.setText(point[1]);//화면의 도착지 입력란에 기존에 입력한 도착지를 입력

        edit = (Button)findViewById(R.id.new_add_btn);//저장버튼과 객체를 연동
        edit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){//버튼을 클릭할 경우 이벤트 정의
                if(startPoint.getText().length() == 0 || endPoint.getText().length() == 0){ //만약 입력칸이 비어있는 경우
                    Toast.makeText(getApplicationContext(), "모든 칸에 정보를 입력하세요!", Toast.LENGTH_SHORT).show(); //데이터를 입력하라는 메세지 띄움
                }
                else{
                    String fileName = startPoint.getText().toString() + "->" + endPoint.getText().toString();//출발지->도착지를 저장하는 string정의하기

                    File f = getFileStreamPath(old_name);//수정할 파일을 찾음
                    File newF = new File(getFilesDir(), fileName);//새로운 파일을 정의함
                    f.renameTo(newF);//파일의 이름 변경
                    setResult(RESULT_OK);//인텐트 반환
                    finish();//종료
                }
            }
        });

    }
}
