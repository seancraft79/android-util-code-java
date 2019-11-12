package com.sean.lib_code_java;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class FileUtil {

    public static boolean isExternalStorageMounted() {
        String ext = Environment.getExternalStorageState();
        if (ext.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    public static boolean isFileExist(Context context, String fileName) {

        String dir = context.getExternalFilesDir(null).getAbsolutePath() + fileName;
        File file = new File(dir);
        return file.exists();
    }
}
