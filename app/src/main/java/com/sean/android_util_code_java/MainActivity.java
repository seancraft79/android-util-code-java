package com.sean.android_util_code_java;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.sean.lib_code_java.FileUtil;
import com.sean.lib_code_java.ParseUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**   TEST FILEUTIL **/
//        FileUtil.createFileInExternalStorage(getApplicationContext(), "TestFolder10", "mytest2.txt", "");
        Log.d("DEBUG", "isSeanFolder? : " + FileUtil.isFileExist(getApplicationContext(), "MySean"));
        FileUtil.createFolderInExternalStorage(getApplicationContext(), "MySean");
        Log.d("DEBUG", "isSeanFolder2? : " + FileUtil.isFileExist(getApplicationContext(), "MySean"));


    }
}
