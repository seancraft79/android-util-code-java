package com.sean.lib_code_java;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Create by Sean Lee 2019/11/13
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
        String dir = getExternalFileDirectory(context);
        if(dir.isEmpty()) return false;

        FileOutputStream fos = new FileOutputStream(dir + filePath, false);

        properties.store(fos, storeComment);
        return true;
    }

    public static boolean createFolderInExternalStorage(Context context, String folderPath) {
        File file = new File(getExternalFileDirectory(context) + File.separator + folderPath);

        if (!file.exists())
        {
            return file.mkdirs();
        }
        return true;
    }

    public static void createFileInExternalStorage(Context context, String folderPath, String fileName, String data) {
        File file = new File(getExternalFileDirectory(context) + File.separator + folderPath);

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

    public static boolean isFullPathFileExistInExternalDirectory(String fileName) {

        File file = new File(fileName);
        return file.exists();
    }

    public static boolean isFileExistInExternalDirectory(Context context, String filePath) {

        String dir = getExternalFileDirectory(context);
        File file = new File(dir + filePath);
        return file.exists();
    }

    @SuppressWarnings("depricated")
    public static String getExternalFileDirectory(Context context) {
        String dir = "";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dir = context.getExternalFilesDir(null).getAbsolutePath();
        }
        else {
            dir = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return dir;
    }

    public static boolean renameFile(File filename, File newFilename) throws Exception {
        boolean flag = false;

        try {
            if (filename.exists()) {
                filename.renameTo(newFilename);
                flag = true;
            } else {
                flag = false;
            }
        } catch (Exception e) {
            e.getStackTrace();
            flag = false;
        }

        return flag;
    }

    private boolean isSDCardAvailable(boolean requireWriteAccess) throws Exception {
        String state = Environment.getExternalStorageState();
        boolean check = false;
        try {

            if (Environment.MEDIA_MOUNTED.equals(state)) {
                check = true;
            } else if (!requireWriteAccess &&
                    Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                check = true;
            }

        } catch (Exception e) {
            e.getStackTrace();
        }

        return check;
    }

}
