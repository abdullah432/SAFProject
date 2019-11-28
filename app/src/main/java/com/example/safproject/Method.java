7package com.example.safproject;

import java.io.File;

public class Method {
    // Retrieving files from memory
    public static void  load_Directory_Files(File directory) {
        //we can'nt do videoArrayList.clear(). Because method is called inside of method
        //clearing videoArrayList before call from MainActivity.java solve the problem

        //Get all file in storage
        File[] fileList = directory.listFiles();
        //check storage is empty or not
        if(fileList != null && fileList.length > 0)
        {
            for (int i=0; i<fileList.length; i++)
            {
                boolean restricted_directory = false;
                //check file is directory or other file
                if(fileList[i].isDirectory())
                {
                    for (String path : Constant.removePath){
                        if (path.equals(fileList[i].getPath())) {
                            restricted_directory = true;
                            break;
                        }
                    }
                    if (!restricted_directory)
                        load_Directory_Files(fileList[i]);
                }
                else
                {
                    String name = fileList[i].getName().toLowerCase();
                    for (String ext : Constant.videoExtensions){
                        //Check the type of file
                        if(name.endsWith(ext))
                        {
                            Constant.allMediaList.add(fileList[i]);

                            //When we found extension from videoExtension array we will break it.
                            break;

                        }

                    }
                }
            }
        }
    }
}
