package com.example.musicplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class MusicService extends Service {

    private final IBinder mBinder = new MusicBinder(); //바인더 객체
    MediaPlayer mediaPlayer = new MediaPlayer();//미디어플레이어 객체
    String path = new String();//현재 실행중인 음악 파일 경로

    NotificationManager notificationManager;
    NotificationChannel mChannel;
    Notification noti;
    Notification.Builder notiBuilder;
    int nowId = -1;


    private void preparePlayer(String p) { //음악 플레이어 준비
        try {
            if(!path.equals(p)) {
                path = p; //경로를 변경
                mediaPlayer.reset(); //음악 플레이어 초기화
                mediaPlayer.setLooping(true);
                mediaPlayer.setDataSource(path); //데이터셋을 받아온 경로로 설정
                mediaPlayer.prepare(); //실행 준비
            }
        } catch (IOException e) { //입출력 에러가 발생할 셩우
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){//startService()실행 시 동작함
        mediaPlayer.setLooping(true);
        preparePlayer(intent.getStringExtra("path"));//음악 실행 준비
        nowId = intent.getIntExtra("id", -1);
        mediaPlayer.start();//실행
        runNotification();
        return START_NOT_STICKY;
    }

    public void changeMusic(String path, int id){
        nowId = id;
        preparePlayer(path);//음악 실행 준비
        mediaPlayer.start();//실행
        runNotification();
    }

    public int returnId(){//해당 실행중인 노래의 인덱스를 반환함
        return nowId;
    }

    public boolean isPlaying(){//노래 실행중인지 여부를 반환함
        return mediaPlayer.isPlaying();
    }
    public void onPause(String path){//일시중지 버튼 누를 경우
        preparePlayer(path);//음악실행 준비
        mediaPlayer.pause();//일시중지
    }

    @Override
    public void onDestroy() { //stopService를 실행할 경우
        super.onDestroy();

        mediaPlayer.release(); //미디어 플레이어를 메모리에서 제거
        mediaPlayer = null; //mediaPlayer에 null 대입

    }

    @Override
    public IBinder onBind(Intent intent) {//바인더를 리턴함
        return mBinder;
    }

    public class MusicBinder extends Binder { //바인더 클래스
        MusicService getService() { return MusicService.this; } //콜백 함수
    }


    public void runNotification(){
        // Android version이 8.0 Oreo 이상이면
        if(Build.VERSION.SDK_INT >= 26){
            // NotificationChannel(String id, CharSequence name, int importance);
            mChannel = new NotificationChannel("music_service_channel_id",
                    "music_service_channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);// 노티피케이션 매니저 초기화
            notificationManager.createNotificationChannel(mChannel);// 노티피케이션 채널 생성
            notiBuilder = new Notification.Builder(this, mChannel.getId()); // 노티피케이션 빌더 객체 생성
        }
        else{// 노티피케이션 빌더 객체 생성
            notiBuilder = new Notification.Builder(this);
        }

        // Intent 객체 생성 - MainActivity 클래스를 실행하기 위한 Intent 객체
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN) .addCategory(Intent.CATEGORY_LAUNCHER) .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//노티피케이션을 클릭했을때, 액티비티의 중복을 막기위하여 설정

        // Intent 객체를 이용하여 PendingIntent 객체를 생성 - Activity를 실행하기 위한 PendingIntent
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Notification.Builder 객체를 이용하여 Notification 객체 생성
        noti = notiBuilder.setContentTitle("Music App")
                .setContentText("현재 음악이 재생중입니다")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .build();

        startForeground(123, noti);// foregound service 설정
    }

}
