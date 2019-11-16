package com.example.navigation;

import android.content.Context;

import java.io.File;

public class TextFileManager {//파일 관리를 돕는 객체 정의
    private Context mContext;//context관리

    public TextFileManager(Context _context){
        mContext = _context;
    } //생성자

    public String[] showAllFiles() { //저장된 모든 파일의 파일명을 반환하는 함수
        File f = mContext.getFilesDir();//해당 context내의 경로 받아오기
        String[] s = f.list();//해당 폴더 내의 모든 파일의 파일명을 string[]타입으로 저장
        return s;//파일명 반환
    }

    public void delete(String name){
        mContext.deleteFile(name);
    }//원하는 파일을 삭제하는 함수

}
