package com.example.navigation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileOutputStream;
import java.io.IOException;

public class AddBookmark extends AppCompatActivity { //파일을 추가하는 액티비티

    Button edit;//버튼객체
    EditText startPoint, endPoint; //출발지, 도착지
    @Override
    protected void onCreate(Bundle savedInstanceState) { //해당 액티비티 실행 시 먼저 실행되는 함수
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_book);//xml파일 설정

        edit = (Button)findViewById(R.id.addbtn);//추가하기 버튼과 객체를 연동
        edit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){ //추가하기 버튼을 클릭할 경우 이벤트 처리
                startPoint = (EditText)findViewById(R.id.start);//출발지 저장
                endPoint = (EditText)findViewById(R.id.end);//도착지 저장

                if(startPoint.getText().length() == 0 || endPoint.getText().length() == 0){ //만약 입력칸이 비어있는 경우
                    Toast.makeText(getApplicationContext(), "모든 칸에 정보를 입력하세요!", Toast.LENGTH_SHORT).show(); //데이터를 입력하라는 메세지 띄움
                }
                else{
                    String fileName = startPoint.getText().toString() + "->" + endPoint.getText().toString();//출발지->도착지를 저장하는 string정의하기
                    try{
                        FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);//파일 저장을 위해 FileOutputStream을 설정
                        fos.write(fileName.getBytes());//파일에 기록
                        fos.close();//파일 닫기
                        setResult(RESULT_OK);//인텐트 반환
                        finish();//종료
                    }
                    catch(IOException e){//오류 발생시
                        e.printStackTrace();//오류 메세지 출력
                    }
                }
            }
        });
    }
}
