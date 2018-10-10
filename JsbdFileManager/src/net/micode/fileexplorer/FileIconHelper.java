package net.micode.fileexplorer;

import java.util.HashMap;
import android.widget.ImageView;

public class FileIconHelper {

	private static HashMap<String, Integer> fileExtToIcons = new HashMap<String, Integer>();
	private static HashMap<String, String> fileExtToIcons2 = new HashMap<String, String>();
	static {
		addItem(new String[] { "mp3" }, R.drawable.audio);
		addItem(new String[] { "m4r" }, R.drawable.audio);
		addItem(new String[] { "midi" }, R.drawable.audio);
		addItem(new String[] { "wav" }, R.drawable.audio);
		addItem(new String[] { "oga" }, R.drawable.audio);
		addItem(new String[] { "mp1" }, R.drawable.audio);
		addItem(new String[] { "ra" }, R.drawable.audio);
		addItem(new String[] { "flac" }, R.drawable.audio);
		addItem(new String[] { "dts" }, R.drawable.audio);
		addItem(new String[] { "mka" }, R.drawable.audio);
		addItem(new String[] { "m4a" }, R.drawable.audio);
		addItem(new String[] { "mp2" }, R.drawable.audio);
		addItem(new String[] { "amr" }, R.drawable.audio);
		addItem(new String[] { "mid" }, R.drawable.audio);
		addItem(new String[] { "ogg" }, R.drawable.audio);
		addItem(new String[] { "aac" }, R.drawable.audio);

		addItem2(new String[] { "mp3" }, "music");
		addItem2(new String[] { "m4r" }, "music");
		addItem2(new String[] { "midi" }, "music");
		addItem2(new String[] { "wav" }, "music");
		addItem2(new String[] { "oga" }, "music");
		addItem2(new String[] { "mp1" }, "music");
		addItem2(new String[] { "ra" }, "music");
		addItem2(new String[] { "flac" }, "music");
		addItem2(new String[] { "dts" }, "music");
		addItem2(new String[] { "mka" }, "music");
		addItem2(new String[] { "m4a" }, "music");
		addItem2(new String[] { "mp2" }, "music");
		addItem2(new String[] { "amr" }, "music");
		addItem2(new String[] { "mid" }, "music");
		addItem2(new String[] { "ogg" }, "music");
		addItem2(new String[] { "aac" }, "music");
		addItem2(new String[] { "mp4", "wmv", "mpeg", "m4v", "3gp", "3gpp", "3g2", "3gpp2", "divx", "mkv", "mov", "flv",
				"f4v", "avi", "vob", "ts", "m2ts", "asx" }, "video");
		addItem2(new String[] { "jpg", "jpeg", "gif", "png", "bmp", "ico", "tag" }, "picture");

		addItem(new String[] { "mp4", "wmv", "mpeg", "m4v", "3gp", "3gpp", "3g2", "3gpp2", "divx", "mkv", "mov", "flv",
				"f4v", "avi", "vob", "ts", "m2ts", "asx" }, R.drawable.video);
		addItem(new String[] { "jpg", "jpeg", "gif", "png", "bmp", "ico", "tag" }, R.drawable.icon_prcture);
		addItem(new String[] { "txt","TXT"}, R.drawable.text);
		// addItem(new String[] { "doc", "ppt", "docx", "pptx", "xsl", "xslx",
		// }, R.drawable.file_icon_office);
		addItem(new String[] { "pdf" }, R.drawable.pdf);
//		 addItem(new String[] { "zip" }, R.drawable.file_icon_zip);
		addItem(new String[] { "apk" }, R.drawable.ic_launcher0);
	}

	private static void addItem(String[] exts, int resId) {
		if (exts != null) {
			for (String ext : exts) {
				fileExtToIcons.put(ext.toLowerCase(), resId);
			}
		}
	}

	private static void addItem2(String[] exts, String resId) {
		if (exts != null) {
			for (String ext : exts) {
				fileExtToIcons2.put(ext.toLowerCase(), resId);
			}
		}
	}

	public static int getFileIcon(String ext) {
		Integer i = fileExtToIcons.get(ext.toLowerCase());
		if (i != null) {
			return i.intValue();
		} else {
			return R.drawable.file_icon_default;
		}

	}
	
	public static String getFileIcon2(String ext) {
		String i = fileExtToIcons2.get(ext.toLowerCase());
		if (i != null) {
			return i;
		} else {
			return "no";
		}

	}

	public static void setIcon(FileInfo fileInfo, ImageView fileImage) {
		String filePath = fileInfo.filePath;
		String extFromFilename = Util.getExtFromFilename(filePath);
		int id = getFileIcon(extFromFilename);
		fileImage.setImageResource(id);
	}
//	public static String setIcon2(FileInfo fileInfo) {
//		String filePath = fileInfo.filePath;
//		String extFromFilename = Util.getExtFromFilename(filePath);
//		String type = getFileIcon2(extFromFilename);
//		return type;
//	}
	
//	public static String setIcon3(File fileInfo) {
//		String extFromFilename = Util.getExtFromFilename(fileInfo.getPath());
//		String type = getFileIcon2(extFromFilename);
//		return type;
//	}
}
