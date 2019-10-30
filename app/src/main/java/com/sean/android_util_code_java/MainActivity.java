package com.sean.android_util_code_java;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.sean.lib_code_java.ParseUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String testStr = "10";
        int parsed = ParseUtil.parseStringToInt(testStr, -1);
        Log.d("DEBUG", "TestParsed ========> " + parsed);
    }
}
