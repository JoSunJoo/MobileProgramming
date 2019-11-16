package com.example.airplane;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    int grade = 0;//좌석 등급의 금액을 저장하는 변수
    int food = 0;//기내식의 금액을 저장하는 변수
    int position = 0;//좌석 위치의 금액을 저장하는 변수
    int tot = 0;//총 금액을 저장하는 변수
    int peopleNum = 1;//탑승객의 수를 저장하는 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {//activity가 실행될 떄 처음으로 실행되는 함수
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//activity_main 레이아웃을 선택

        TextView people = (TextView)findViewById(R.id.people);//탑승객의 수를 나타내는 people의 id를 가져옴
        people.setPaintFlags(people.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);//탑승객의 수에 밑줄을 그음
    }

    public void onClickGrade(View view){//좌석 등급을 선택할때 실행됨
        boolean isCheck = ((RadioButton)view).isChecked();//선택되었는지 확인
        TextView total = (TextView)findViewById(R.id.total);//총 금액의 id를 가져옴
        ImageView image = (ImageView)findViewById(R.id.image);//좌석 이미지의 id를 가져옴

        switch(view.getId()){//view의 id를 가지고 판단
            case R.id.first://id == first일 경우
                if(isCheck){
                    grade = 3000000;//좌석 등급의 금액 변경
                    image.setImageResource(R.drawable.korean_air_first_class);//이미지를 first클래스 사진으로 변경
                }
                break;
            case R.id.business://id == business일 경우
                if(isCheck) {
                    grade = 2000000;//좌석 등급의 금액 변경
                    image.setImageResource(R.drawable.korean_air_business);//이미지를 business클래스 사진으로 변경
                }
                break;
            case R.id.economy://id == economy일 경우
                if(isCheck){
                    grade = 1000000;//좌석 등급의 금액 변경
                    image.setImageResource(R.drawable.korean_air_economy);//이미지를 economy클래스 사진으로 변경
                }
                break;
        }
        tot = (grade + food + position)*peopleNum;//총 금액 계산
        total.setText(Integer.toString(tot));//정수를 문자열로 변환한 후 화면의 텍스트 변경
    }

    public void onClickFood(View view){//기내식을 선택할 경우
        boolean isCheck = ((CheckBox)view).isChecked();//선택되었는지 확인
        TextView total = (TextView)findViewById(R.id.total);//총 금액의 id를 가져옴
        if(isCheck) food += 15000;//선택될 경우, 기내식의 가격 + 15000
        else food -= 15000;//선택이 해제될 경우, 기내식의 가격 - 15000

        tot = (grade + food + position)*peopleNum;//총 금액 계산
        total.setText(Integer.toString(tot));//정수를 문자열로 변환한 후 화면의 텍스트 변경
    }

    public void onClickPosition(View view){//좌석위치를 선택할 경우
        boolean isCheck = ((RadioButton)view).isChecked();//선택되었는지 확인
        TextView total = (TextView)findViewById(R.id.total);//총 금액의 id를 가져옴

        switch(view.getId()){//view의 id를 가져옴
            case R.id.aisle://id == aisle일 경우
                if(isCheck) position = 20000;//좌석의 금액 변경
                break;
            case R.id.window://id == window일 경우
                if(isCheck) position = 0;//좌석의 금액 변경
                break;
        }
        tot = (grade + food + position)*peopleNum;//총 금액 계산
        total.setText(Integer.toString(tot));//정수를 문자열로 변환한 후 화면의 텍스트 변경
    }

    public void onClickBtn(View view){//+, -버튼 선택할 경우
        TextView total = (TextView)findViewById(R.id.total);//총 금액의 id를 가져옴
        TextView people = (TextView)findViewById(R.id.people);//인원수의 id를 가져옴

        switch(view.getId()){//view의 id를 가져옴
            case R.id.plus://'+'버튼이 눌릴 경우
                peopleNum++;//총 인원 +1
                people.setText(Integer.toString(peopleNum));//정수를 문자열로 변환한 후 인원의 텍스트 변경
                break;
            case R.id.minus://'-'버튼이 눌릴 경우
                if(peopleNum == 1) {//탑승객이 1명일떄
                    Toast.makeText(this, "최소 탑승 인원은 1명 입니다.", Toast.LENGTH_LONG).show();//경고 메세지 띄움
                    break;//종료
                }
                peopleNum--;//탑승객이 2명 이상일 때, 총 인원 - 1
                people.setText(Integer.toString(peopleNum));//정수를 문자열로 변환한 후 인원의 텍스트 변경
                break;//종료
        }
        tot = (grade + food + position)*peopleNum;//총 금액 계산
        total.setText(Integer.toString(tot));//정수를 문자열로 변환한 후 화면의 텍스트 변경
        people.setPaintFlags(people.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);//탑승객의 수에 밑줄을 그음
    }
}
