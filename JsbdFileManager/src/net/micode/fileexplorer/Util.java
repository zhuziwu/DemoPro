package net.micode.fileexplorer;

import java.io.File;
import android.os.Environment;

public class Util {

	public static String makePath(String path1, String path2) {
		if (path1.endsWith(File.separator))
			return path1 + path2;
		return path1 + File.separator + path2;
	}

	public static String getSdDirectory() {
		return Environment.getExternalStorageDirectory().getPath();
	}

	public static FileInfo GetFileInfo(File f) {
		FileInfo lFileInfo = new FileInfo();
		lFileInfo.canWrite = f.canWrite();
		lFileInfo.fileName = f.getName();
		lFileInfo.IsDir = f.isDirectory();
		lFileInfo.filePath = f.getPath();
		return lFileInfo;
	}


	public static String getExtFromFilename(String filename) {
		int dotPosition = filename.lastIndexOf('.');
		if (dotPosition != -1) {
			return filename.substring(dotPosition + 1, filename.length());
		}
		return "";
	}

	public static String getNameFromFilename(String filename) {
		int dotPosition = filename.lastIndexOf('.');
		if (dotPosition != -1) {
			return filename.substring(0, dotPosition);
		}
		return filename;
	}

	public static String getPathFromFilepath(String filepath) {
		int pos = filepath.lastIndexOf('/');
		if (pos != -1) {
			return filepath.substring(0, pos);
		}
		return "";
	}
}
