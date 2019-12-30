package com.sean.lib_code_java;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.security.auth.callback.Callback;

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

    public static boolean isFileExist(String fileName) {

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

    /**
     * 디렉토리와 피해야 할 확장자명을 가진 파일을 제외한 파일들 중 지정한 범위의 날짜가 포함된 파일명을 가진 파일들 삭제
     * @param folderPath : 삭제하고자 하는 파일이 있는 폴더
     * @param deleteFromPreDay : 삭제하고자 하는 날짜의 시작 (예 : 30을 주면 오늘부터 30일 전부터 이전 파일들 삭제)
     * @param deleteToPreDay : 삭제하고 싶은 날짜 갯수 (예 : 10을 주면 삭제하고자 하는 날짜부터 10일 전까지 제목에 날짜를 포함하는 파일 모두 삭제)
     * @param avoidExtensions : 삭제하기 원하는 않는 파일 확장자명들
     */
    public static List<String> deleteFileByDateFileter(final String folderPath, final int deleteFromPreDay, final int deleteToPreDay, String[] avoidExtensions) {

        String[] agoDays = new String[deleteToPreDay];
        for (int i = deleteFromPreDay; i < deleteToPreDay; i++) {
            String agoDay = getDayAgo(i+1);
            agoDays[i] = agoDay;
        }

        List<String> deletedFileNames = null;

        final File folder = new File(folderPath);
        final File[] files = folder.listFiles();

        if(files != null && files.length > 0) {

            List<File> filteredFiles = new ArrayList<>();
            deletedFileNames = new ArrayList<>();

            for (int j = 0; j < agoDays.length; j++) {

                filteredFiles.clear();
                final String deleteFilter = agoDays[j];

                for (int i = 0; i < files.length; i++) {
                    String fileName = files[i].getName();

                    try {
                        String canonicalPath = files[i].getCanonicalPath();
                        final int firstIndex = canonicalPath.indexOf(".");
                        String fileExt = canonicalPath.substring(firstIndex + 1).toLowerCase();

                        boolean isAvoidExtIncluded = false;
                        for (int k = 0; k < avoidExtensions.length; k++) {
                            if(fileExt.contains(avoidExtensions[k])) {
                                isAvoidExtIncluded = true;
                                break;
                            }
                        }

                        boolean isDirectory = files[i].isDirectory();
                        if(fileName.contains(deleteFilter) && !isAvoidExtIncluded && !isDirectory){
                            filteredFiles.add(files[i]);
                        }

                    } catch (Exception e) {
                    }
                }

                if(filteredFiles.size() > 0) {
                    for (int i = 0; i < filteredFiles.size(); i++) {
                        String fileToDelete = filteredFiles.get(i).getName();

                        if(filteredFiles.get(i).delete())
                            deletedFileNames.add(fileToDelete);
                    }
                }
            }
        }
        return deletedFileNames;
    }

    static String getDayAgo(int dayAgo) {
        long DAY_IN_MS = 1000 * 60 * 60 * 24;
        Date date = new Date(System.currentTimeMillis() - (dayAgo * DAY_IN_MS));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
}
