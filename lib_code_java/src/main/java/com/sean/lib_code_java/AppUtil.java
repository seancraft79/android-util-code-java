package com.sean.lib_code_java;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

public class AppUtil {
    private static final String TAG = "AppUtil";

    /**
     * 특정 패키지명의 앱 실행(설치여부 확인후 실행필요)
     **/
    public static void executeLocalAppPackage(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 특정 패키지명의 앱 설치 여부 체크업
     **/
    public static boolean isPackageInstalled(Context context, String packageName) {
        boolean bExist = false;

        /** 패키지 정보 리스트 추출 **/
        PackageManager pkgMgr = context.getPackageManager();
        List<ResolveInfo> mAppList;
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mAppList = pkgMgr.queryIntentActivities(mainIntent, 0);

        /** 패키지 리스트 순회하면서 특정 패키지명 검색 **/
        if(mAppList != null && mAppList.size() > 0) {
            try {
                for (int i = 0; i < mAppList.size(); i++) {
                    if (mAppList.get(i).activityInfo.packageName.startsWith(packageName)) {
                        Log.d(TAG, "isPackageInstalled found : " + mAppList.get(i).activityInfo.packageName);
                        bExist = true;
                        break;
                    }
                }
            } catch (Exception e) {
                bExist = false;
            }
        }
        return bExist;
    }

    public int installApp(
            final Context context,
            final String folder,
            final String apkName,
            final String packageName,
            final InstallAppCallBack cb) throws IOException {

        Log.d("InstallAutoApp", "exec");

        int result = 0;

        File sdcard = Environment.getExternalStorageDirectory();
        File apkpath = new File(sdcard.getAbsolutePath() + File.separator + folder);

        String apkfile = apkpath.getAbsolutePath() + File.separator + apkName;
        Log.d("apkfile", apkfile);

        final String command = "pm install -r " + apkfile;
        Log.d("command", command);

        Handler mHandler2 = new Handler();
        Thread at = new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    java.lang.Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
                    proc.waitFor();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                    int read;
                    char[] buffer = new char[64];
                    StringBuffer output = new StringBuffer();
                    while ((read = reader.read(buffer)) > 0) {
                        output.append(buffer, 0, read);
                    }
                    reader.close();

                    if (output.toString().contains("Success")) {

                        PackageManager pm = context.getPackageManager();
                        Intent mStartActivity = pm.getLaunchIntentForPackage(
                                packageName
                        );
                        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //create a pending intent so the application is restarted after System.exit(0) was called.
                        // We use an AlarmManager to call this intent in 100ms
                        int mPendingIntentId = 223345;
                        PendingIntent mPendingIntent = PendingIntent
                                .getActivity(context, mPendingIntentId, mStartActivity,
                                        PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                        //kill the application
                        System.exit(0);
                        if(cb!=null)cb.onResult(1);

                    } else if(cb!=null)cb.onResult(0);
                } catch (Exception e) {
                    e.printStackTrace();
                    if(cb!=null)cb.onResult(1);
                }

            }
        });
        at.start();

        return result;
    }

    public static boolean installPackage(final String filePath) {
        Log.d(TAG, "installPackage filePath : " + filePath);

        if(StringUtil.isNullOrEmpty(filePath) || filePath.equalsIgnoreCase("null")) {
            Log.d(TAG, "installPackage apk file name is not valid");
            return false;
        }

        try {
            final String command = "pm install -r " + filePath;

            try {

                Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
                proc.waitFor();

                BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                int read;
                char[] buffer = new char[64];
                StringBuffer output = new StringBuffer();
                while ((read = reader.read(buffer)) > 0) {
                    output.append(buffer, 0, read);
                }
                reader.close();

                Log.d(TAG, "install app output : " + output.toString());
                if (output.toString().contains("Success")) {
                    return true;
                }

            } catch (Exception e) {
                Log.e(TAG, "installPackage error : " + e.getMessage());
            }

        } catch (Exception e) {
            Log.e(TAG, "installPackage error : " + e.getMessage());
        }

        return false;
    }

    public static boolean uninstallPackage(final String packageName) {
        Log.d(TAG, "uninstallPackage : " + packageName);
        try {

            final String command = "pm uninstall " + packageName;

            try {

                Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
                proc.waitFor();

                BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                int read;
                char[] buffer = new char[64];
                StringBuffer output = new StringBuffer();
                while ((read = reader.read(buffer)) > 0) {
                    output.append(buffer, 0, read);
                }
                reader.close();

                Log.d(TAG, "uninstall app output : " + output.toString());
                if (output.toString().contains("Success")) {
                    return true;

                } else {
                    Log.e(TAG, "uninstall app failed");
                }

            } catch (Exception e) {
                Log.e(TAG, "uninstall app failed");
            }

        } catch (Exception e) {
            Log.e(TAG, "uninstall app error : " + e.getMessage());
        }
        return false;
    }

    public static String writeApkToSdcardFolder(Context context, final String extFolderPath, final String apkFileName) {

        try {
            File folderPath = new File(extFolderPath);

            final String apkFilePath = folderPath.getAbsolutePath() + File.separator + apkFileName;

            File file = new File(apkFilePath);

            // 경로에 DB 가 없으면 리소스에서 가져와서 Write
            if(!file.exists()) {
                Log.d(TAG, "File doesnt exist create one");

                InputStream inputStream = context.getAssets().open(apkFileName);
                OutputStream outputStream = new FileOutputStream(apkFilePath);
                byte[] mBuffer = new byte[1024];
                int len;
                while ((len = inputStream.read(mBuffer)) > 0)
                    outputStream.write(mBuffer, 0, len);

                outputStream.flush();
                outputStream.close();
                inputStream.close();

                Log.d(TAG, "writeApkToSdcardFolder : " + apkFilePath);
                return apkFilePath;

            } else {
                Log.e(TAG,"writeApkToSdcardFolder apk already exist");
            }

        } catch (Exception e) {
            Log.e(TAG,"writeApkToSdcardFolder error : " + e.getMessage());
        }
        return null;
    }

    /** Interface **/
    public interface InstallAppCallBack {
        void onResult(int result);
    }

    public interface InstallPackageCallBack {
        void onResult(boolean result);
    }
}
