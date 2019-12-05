package com.example.musicplayer;

public class Music { //음악 클래스
    private String name; //이름
    private String path; //경로

    public String getName() {//name 반환
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }//name 설정

    public String getPath() {
        return path;
    }//경로 반환

    public void setPath(String path) {//경로 설정
        this.path = path;
    }
    public String toString() {//리스트에 파일명을 띄우기위함
        return name;
    }
}
