package com.example.navigation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    TextFileManager mFileMgr = new TextFileManager(this);//파일에 대한 관리를 하는 객체
    private ListView m_ListView;//리스트뷰 객체
    private ArrayAdapter<String> m_Adapter;//어댑터 객체
    private ArrayList<String> values = new ArrayList<>();//파일의 이름 데이터를 저장하는 ArrayList

    @Override
    protected void onCreate(Bundle savedInstanceState) {//액티비티 실행시 먼저 실행되는 함수
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//레이아웃 설정

        values.addAll(Arrays.asList(mFileMgr.showAllFiles()));//저장된 폴더 내의 모든 파일의 이름을 가져와 value에 저장

        m_Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values);//어댑터 만들기
        m_ListView = findViewById(R.id.list);//해당 리스트의 id를 가져옴
        m_ListView.setAdapter(m_Adapter);//어댑터 연결
        m_ListView.setOnItemClickListener(onClickListItem);//어댑터 클릭 시 실행됨

        registerForContextMenu(m_ListView);
    }

    //해당 항목의 리스트를 클릭할 경우 이벤트 처리
    private AdapterView.OnItemClickListener onClickListItem = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            String name = values.get(position);//해당 리스트의 폴더명을 가져옴
            String[] point = name.split("->");//'->'을 기준으로 문자열 분할
            String url = "https://www.google.com/maps/dir/" + point[0] + "/" + point[1]; //출발지와 도착지를 넣은 url 생성
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));//암시적 intent를 통해 검색
            startActivity(intent);//인텐트 실행
        }
    };

    @Override
    protected void onResume() {//다른 액티비티가 종료된 후 해당 액티비티가 앞으로 올 경우 실행됨
        super.onResume();
        //value의 값을 갱신함
        values.clear();//value값 초기화
        values.addAll(Arrays.asList(mFileMgr.showAllFiles()));//다시 모든 파일의 이름을 가져와 value에 대입

        m_Adapter.notifyDataSetChanged();//어댑터의 리스트항목을 업데이트해줌
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){ //메뉴 만들기
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){//메뉴의 버튼을 클릭할 때 이벤트 처리
        if(item.getItemId() == R.id.add){//add를 클릭할 경우
            Intent intent = new Intent(getApplicationContext(), AddBookmark.class);//경로 추가 페이지로 이동
            startActivityForResult(intent, 1);//인텐트 시작
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuinfo){//컨텍스트메뉴 생성
        super.onCreateContextMenu(menu, v, menuinfo);
        menu.setHeaderTitle("메뉴");//메뉴의 타이틀 설정
        menu.add(0, 1, 0, "수정");//수정 버튼 생성
        menu.add(0, 2, 0, "삭제");//삭제 버튼 생성
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){//컨텍스트 메뉴 선택시 이벤트 처리
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();//컨텍스트메뉴의 데이터 가져오기

        int index = info.position;//해당 메뉴의 인덱스 가져오기

        switch(item.getItemId()){
            case 1: //수정버튼을 누른 경우
                Intent intent = new Intent(this, ModifyBookMark.class);//수정페이지로 이동
                intent.putExtra("name", values.get(index));//인텐트에 해당 리스트의 데이터 삽입
                startActivityForResult(intent, 2);//인텐트 시작
                break;
            case 2: //삭제버튼을 누른 경우
                mFileMgr.delete(values.get(index));//해당 파일 삭제
                values.clear();//데이터 초기화
                values.addAll(Arrays.asList(mFileMgr.showAllFiles()));//파일의 이름을 다시 가져와서 value에 저장함
                m_Adapter.notifyDataSetChanged();//리스트 뷰를 업데이트해줌
                break;
        }
        return true;
    }
}
