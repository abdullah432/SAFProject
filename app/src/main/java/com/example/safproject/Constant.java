package com.example.safproject;

import java.io.File;
import java.util.ArrayList;

public class Constant {
    public static String[] videoExtensions = {".mp4",".ts",".mkv",".mov",".3gp",".mv2",".m4v",".webm",".mpeg1",".mpeg2",".mts",".ogm",".bup",
            ".dv",".flv",".m1v",".m2ts",".mpeg4",".vlc",".3g2",".avi",".mpeg",".mpg",".wmv",".asf"};

    public static String[] removePath = {"/storage/emulated/0/Android/data","/storage/emulated/0/Android/obb"};

    //loading all files here
    public static ArrayList<File> allMediaList = new ArrayList<>();
}
