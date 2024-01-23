package be.quodlibet.dndgenerate;

import java.io.File;

public class UniqueFilenameGenerator {

    public static String getUniqueFilename(String folderPath, String originalFilename) {
        File file = new File(folderPath, originalFilename);
        String filenameWithoutExtension = getFilenameWithoutExtension(originalFilename);
        String extension = getFileExtension(originalFilename);
        int count = 0;

        while (file.exists()) {
            count++;
            String newFilename = String.format("%s(%d)%s", filenameWithoutExtension, count, extension);
            file = new File(folderPath, newFilename);
        }

        return file.getName();
    }

    private static String getFilenameWithoutExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(0, dotIndex);
        }
        return filename;
    }

    private static String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex); // includes the dot
        }
        return "";
    }

    
}
