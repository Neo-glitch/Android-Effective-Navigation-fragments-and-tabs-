package com.neo.implementingeffectivenavigation.util;

import java.io.File;
import java.util.ArrayList;


/**
 * gets list of dir path from path sent to it
 */
public class FileSearch {

    private static final String TAG = "FileSearch";

    /**
     * Search a directory and return a list of all **directories** contained inside
     * @param directory
     * @return
     */
    public static ArrayList<String> getDirectoryPaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = null;
        File[] listfiles = null;
        try{
            file = new File(directory);
            listfiles = file.listFiles();
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            for(int i = 0; i < listfiles.length; i++){
                if(listfiles[i].isDirectory()){
                    pathArray.add(listfiles[i].getAbsolutePath());
                }
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return pathArray;
    }

    /**
     * Search a directory and return a list of all **files** contained inside
     * used inf GalleryFragment where images might be on the device # not cam
     * @param directory
     * @return
     */
    public static ArrayList<String> getFilePaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = null;
        File[] listfiles = null;
        try{
            file = new File(directory);
            listfiles = file.listFiles();
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            for(int i = 0; i < listfiles.length; i++){
                if(listfiles[i].isFile()){
                    pathArray.add(listfiles[i].getAbsolutePath());
                }
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return pathArray;
    }
}












