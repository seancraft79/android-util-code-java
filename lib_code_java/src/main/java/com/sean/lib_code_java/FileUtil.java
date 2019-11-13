package com.sean.lib_code_java;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Create by Sean 2019/11/13
 */

public class FileUtil {

    public static boolean storeProperty(Context context, String filePath, Map<String, String> property, String storeComment) throws Exception {
        if(property == null || property.isEmpty()) return false;
        
        Properties properties = new Properties();
        for (Map.Entry<String,String> entry : property.entrySet()) {
            properties.setProperty(entry.getKey(), entry.getValue());
        }
        return storeProperty(context, filePath, properties, storeComment);
    }

    public static boolean storeProperty(Context context, String filePath, Properties properties, String storeComment) throws Exception {
        String dir = getFileDirectory(context);
        if(dir.isEmpty()) return false;

        FileOutputStream fos = new FileOutputStream(dir + filePath, false);

        properties.store(fos, storeComment);
        return true;
    }

    public static boolean createFolderInExternalStorage(Context context, String folderPath) {
        File file = new File(getFileDirectory(context) + File.separator + folderPath);

        if (!file.exists())
        {
            return file.mkdirs();
        }
        return true;
    }

    public static void createFileInExternalStorage(Context context, String folderPath, String fileName, String data) {
        File file = new File(getFileDirectory(context) + File.separator + folderPath);

        if (!file.exists())
        {
            file.mkdirs();
        }

        File genFile = new File(file
                + File.separator + fileName);

        try {
            FileOutputStream fos = new FileOutputStream(genFile);
            fos.write(data.getBytes());
            fos.close();
        } catch (Exception e) {e.printStackTrace();}
    }

    public static boolean isExternalStorageMounted() {
        String ext = Environment.getExternalStorageState();
        if (ext.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    public static boolean isFileExist(Context context, String filePath) {

        String dir = getFileDirectory(context);
        File file = new File(dir, filePath);
        return file.exists();
    }

    public static String getFileDirectory(Context context) {
        String dir = "";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dir = context.getExternalFilesDir(null).getAbsolutePath();
        }
        else {
            dir = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return dir;
    }
}
