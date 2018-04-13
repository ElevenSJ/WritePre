package com.video.recored;


import java.io.File;

public class FileUtil {

    public static String createFilePath(String folder, String subfolder, String uniqueId) {
        File dir = new File(folder);
        if (subfolder != null) {
            dir = new File(dir, subfolder);
        }
        dir.mkdirs();
        String fileName = Constants.FILE_START_NAME + uniqueId + Constants.VIDEO_EXTENSION;
        return new File(dir, fileName).getAbsolutePath();
    }
}
