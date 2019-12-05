package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    boolean isClick = false; //true : pause아이콘 화면(실행중)   / false : start아이콘 화면(정지상태)
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    boolean isPermitted = false;//권한설정이 되어있는지 판단하는 변수
    boolean isFirst = true;//음악을 처음 실행하는지 판단하는 변수

    private ListView m_ListView;//리스트뷰 객체
    private ArrayAdapter<Music> m_Adapter;//어댑터 객체
    public static ArrayList<Music> values = new ArrayList<>();//파일의 이름 데이터를 저장하는 ArrayList
    int nowId=0;//나의 현재 value의 id
    ImageButton btn;
    TextView Title;

    MusicService musicService; //음악 서비스
    boolean bound = false; //바인드 유무

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (ImageButton)findViewById(R.id.start);
        Title = (TextView)findViewById(R.id.title);

        m_Adapter = new ArrayAdapter<Music>(this, android.R.layout.simple_list_item_1, values);//어댑터 만들기
        m_ListView = findViewById(R.id.list);//해당 리스트의 id를 가져옴
        m_ListView.setAdapter(m_Adapter);//어댑터 연결
        m_ListView.setOnItemClickListener(onClickListener);

        requestRuntimePermission();
        if(isPermitted) {//권한이 승인된 경우, list띄우기
            list();
        }

        Intent intent = new Intent(getApplicationContext(), MusicService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE); //서비스 바인드
    }

    private AdapterView.OnItemClickListener onClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {//리스트 내의 곡을 선택한 경우
            nowId = i;//현재의 id를 갱신함
            playMusic();//음악 실행
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(bound) { //꺼질 경우 바인드를 해제
            unbindService(mConnection);
            bound = false;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() { //서비스 연결
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) { //서비스 연결 성공할 경우
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service; //서비스를 클래스에 삽입
            musicService = binder.getService(); //서비스를 MusicService 클래스에 대입

            nowId = musicService.returnId();//서비스로부터 인덱스 값을 받아옴

            if(nowId != -1){//인덱스가 -1이 아니면 곡을 실행한 경험이 있음!
                Title.setText(values.get(nowId).getName());//노래제목 설정
                if(musicService.isPlaying())//노래를 실행중이라면
                    btn.setImageResource(R.drawable.pause);//아이콘 변경
            }
            else{//곡을 실행한 적이 없으면
                nowId = 0;//id값 초기화
            }

            bound = true; //바인드를 연결했다고 표시
        }

        // Service 연결 해제되었을 때 호출되는 callback 메소드
        @Override
        public void onServiceDisconnected(ComponentName name) { //서비스 연결 실패할 경우
            bound = false;
        }
    };

    private void requestRuntimePermission() { //권한 설정
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) { //퍼미션이 있을 경우 실행

            if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) { //권한이 없을 경우 권한 요청

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        } else {
            isPermitted = true; //권한이 있을 경우 권한에 true 대입
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) { //권한 응답
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: { //외부 쓰기 권한
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //외부 접근 권한 획득 시
                    isPermitted = true; //권한에 true 대입
                    list(); //음악 리스트 동기화

                } else {
                    isPermitted = false; //권한에 false 대입
                }
                return;
            }

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){ //메뉴 만들기
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){//메뉴의 버튼을 클릭할 때 이벤트 처리
        if(item.getItemId() == R.id.finish){//finish를 클릭할 경우
            stopService(new Intent(getApplicationContext(), MusicService.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // button이 눌렸을 때 호출되는 onClick 메소드 구현
    public void onClick(View view) {
        nowId = musicService.returnId();
        if(nowId == -1) nowId = 0;
        // view의 id가 무엇인지에 따라 MusicService 시작/중지
        switch(view.getId()) {
            case R.id.start:
                if(isClick) {//노래가 실행중이면
                    //노래 멈추기 + 화면을 start로 바꿔주기
                    isClick = false;
                    btn.setImageResource(R.drawable.play);
                    musicService.onPause(values.get(nowId).getPath());
                }
                else //노래가 멈춰있으면
                if(values.size() == 0)
                    Toast.makeText(getApplicationContext(), "곡이 존재하지 않습니다.", Toast.LENGTH_LONG).show();
                else {
                    playMusic();//음악 실행하기
                }
                break;
            case R.id.previous_btn://이전곡 재생 버튼을 누른 경우

                if (nowId >= 1 && nowId < values.size()) { //id가 1 이상일 경우 실행
                    nowId--;//values에서 1칸 앞의 곡으로 실행
                    playMusic();//음악 실행하기
                }
                else{
                    Toast.makeText(getApplicationContext(), "앞의 곡이 존재하지 않습니다.", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.next_btn:
                if (nowId >= 0 && nowId < values.size()-1) { //id가 1 이상일 경우 실행
                    nowId++;//values에서 1칸 다음의 곡으로 실행
                    playMusic();//음악 실행하기
                }
                else{
                    Toast.makeText(getApplicationContext(), "다음 곡이 존재하지 않습니다.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    String str;

    private void playMusic(){
        isClick = true;//노래 실행 변수 초기화
        btn.setImageResource(R.drawable.pause);//아이콘 변경
        str = values.get(nowId).getName();
        Title.setText(str);//노래제목으로 타이틀 변경

        if(isFirst) {//처음 실행하는 경우
            isFirst = false;
            Intent intent = new Intent(this, MusicService.class);
            intent.putExtra("path", values.get(nowId).getPath());
            intent.putExtra("id", nowId);
            startService(intent); //startService 실행
        }
        else{//실행한 전적이 있는 경우
            musicService.changeMusic(values.get(nowId).getPath(), nowId);//boundservice 사용
        }
    }

    private void list(){ //음악 list를 가져옴
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);//외부 저장소에서 음악파일의 경로를 가져옴
        File[] data = folder.listFiles();//음악파일의 정보를 가져옴
        values.clear();
        for (File f : data) {//데이터를 일일히 values에 대입함
            if (f.getName().endsWith("mp3")) {
                Music music = new Music();
                music.setName(f.getName());
                music.setPath(f.getPath());
                values.add(music);
            }
        }
        m_Adapter.notifyDataSetChanged();
    }

}
