package net.micode.fileexplorer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class FileOperationHelper {
	private final String LOG_TAG = "FileOperation";
	private ArrayList<FileInfo> mCurFileNameList = new ArrayList<FileInfo>();
	private boolean mMoving;
	private IOperationProgressListener mOperationListener;
	private long msize;
	private long currentProgress;
	private int duqu_zhi;
	byte[] buffer;
	private FileInputStream in = null;
	private FileOutputStream out = null;
	private String caozuo;

	public interface IOperationProgressListener {
		void onFinish(String d);

		void onFinish2();

		void onFinish3();

		void setMessage(String d);
	}

	public FileOperationHelper(IOperationProgressListener l, Context context) {
		mOperationListener = l;
	}

	public void Copy(ArrayList<FileInfo> files) {
		copyFileList(files);
	}

	public boolean Paste(String path, long Size) {
		if (mCurFileNameList.size() == 0) {
			return false;
		}
		Log.d("FileOperationHelper_Paste", "   Size = " + Size);
		msize = Size;
		final String _path = path;
		asnycExecute(new Runnable() {
			public void run() {
				for (FileInfo f : mCurFileNameList) {
//					File file = ;
					CopyFile(new File(f.filePath), _path);
//					file = null;
					if (FileViewInteractionHub.CancelCopy) {
						Log.d("fzss", "run-CancelCopy-1 = " + FileViewInteractionHub.CancelCopy);
						break;
					}
				}
				caozuo = "F";
				clear();
			}
		});
		return true;
	}

	// paste2 异盘剪贴的先复制操作 -----(paste为正常的复制操作)
	public boolean Paste2(String path, long Size) {
		// TODO
		if (mCurFileNameList.size() == 0) {
			return false;
		}
		msize = Size;
		final String _path = path;
		asnycExecute2(new Runnable() {
			public void run() {
				for (FileInfo f : mCurFileNameList) {
					JQFile(f, _path);
					if (FileViewInteractionHub.CancelCopy) {
						break;
					}
				}
				caozuo = "F";
			}
		});
		return true;
	}

	public boolean canPaste() {
		return mCurFileNameList.size() != 0;
	}

	public void StartMove(ArrayList<FileInfo> files) {
		if (mMoving)
			return;
		mMoving = true;
		copyFileList(files);
	}

	public boolean isMoveState() {
		return mMoving;
	}

	public boolean setMovinr(boolean mm) {
		return mMoving = mm;
	}

	public void clear() {
		synchronized (mCurFileNameList) {
			mCurFileNameList.clear();
		}
	}

	public boolean EndMove1(String path) {
		if (!mMoving) {
			return false;
		}
		mMoving = false;
		clear();
		return true;
	}

	public boolean EndMove(String path) {
		if (!mMoving) {
			return false;
		}
		mMoving = false;
		final String _path = path;
		asnycExecute(new Runnable() {
			public void run() {
				for (FileInfo f : mCurFileNameList) {
					MoveFile(f, _path);
				}
				caozuo = "J";
				clear();
			}
		});
		return true;
	}

	public ArrayList<FileInfo> getFileList() {
		return mCurFileNameList;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void asnycExecute(Runnable r) {
		final Runnable _r = r;
		new AsyncTask() {
			protected Object doInBackground(Object... params) {
				synchronized (mCurFileNameList) {
					_r.run();
				}
				return null;
			}

			protected void onPostExecute(Object result) {
				if (mOperationListener != null) {
					mOperationListener.onFinish(caozuo);
					currentProgress = 0;
				}
			};
		}.execute();
	}

	@SuppressWarnings("unchecked")
	private void asnycExecute2(Runnable r) {
		final Runnable _r = r;
		new AsyncTask() {
			protected Object doInBackground(Object... params) {
				synchronized (mCurFileNameList) {
					_r.run();
				}
				return null;
			}

			protected void onPostExecute(Object result) {
				if (mOperationListener != null) {
					mOperationListener.onFinish2();
					currentProgress = 0;
				}

			};
		}.execute();
	}

	@SuppressWarnings("unchecked")
	private void asnycExecute3(Runnable r) {
		final Runnable _r = r;
		new AsyncTask() {

			protected Object doInBackground(Object... params) {
				synchronized (mCurFileNameList) {
					_r.run();
				}
				return null;
			}

			protected void onPostExecute(Object result) {
				if (mOperationListener != null) {
					mOperationListener.onFinish3();
				}
			};
		}.execute();
	}

	public boolean isFileSelected(String path) {
		synchronized (mCurFileNameList) {
			for (FileInfo f : mCurFileNameList) {
				if (f.filePath.equalsIgnoreCase(path))
					return true;
			}
		}
		return false;
	}

	public boolean Rename(FileInfo f, String newName, Boolean isdir) {
		if (f == null || newName == null) {
			Log.e(LOG_TAG, "Rename: null parameter");
			return false;
		}
		File file = new File(f.filePath);
		String newPath = Util.makePath(Util.getPathFromFilepath(f.filePath), newName);
		Log.d("chongm", " newPath = " + newPath);
		Log.d("chongm", " newName = " + newName);
		boolean ret = file.renameTo(new File(newPath));
		return ret;
	}

	public boolean Delete(final ArrayList<FileInfo> files) {
		asnycExecute(new Runnable() {
			public void run() {
				for (FileInfo f : files) {
					DeleteFile(new File(f.filePath));
				}
				caozuo = "D";
				clear();
			}
		});
		return true;
	}

	public void Delete2() {
		asnycExecute3(new Runnable() {
			public void run() {
				for (FileInfo f : mCurFileNameList) {
					DeleteFile(new File(f.filePath));
				}
				clear();
			}
		});
	}

	protected void DeleteFile(File f) {
		if (f == null) {
			Log.e(LOG_TAG, "DeleteFile: null parameter");
			return;
		}
		if (f.isDirectory()) {
			for (File child : f.listFiles()) {
				DeleteFile(child);
			}
		}
		f.delete();

	}

	// 文件集合点击后的文件路径
	@SuppressWarnings("unused")
	private void JQFile(FileInfo f, String dest) {
		if (f == null || dest == null) {
			Log.e(LOG_TAG, "CopyFile: null parameter");
			return;
		}

		File file = new File(f.filePath);
		if (file.isDirectory()) {
			String destPath = Util.makePath(dest, f.fileName);
			if (file.list().length == 0) {
				File dir = new File(destPath);
				dir.mkdirs();
			}
			for (File child : file.listFiles()) {
				if (!child.isHidden()) {
					JQFile(Util.GetFileInfo(child), destPath);
				}
				if (FileViewInteractionHub.CancelCopy) {
					break;
				}
			}
		} else {
			JianQieFile(f.filePath, dest);
		}
	}

	private void CopyFile(File f, String dest) {
		if (f.isDirectory()) {
			String destPath = Util.makePath(dest, f.getName());
			if (f.list().length == 0) {
				new File(destPath).mkdirs();
			}
			for (File child : f.listFiles()) {
				if (FileViewInteractionHub.CancelCopy) {
					Log.d("fzss", "run-CancelCopy-2 = " + FileViewInteractionHub.CancelCopy);
					break;
				}
				CopyFile(child, destPath);

			}
		} else {
			if (FileViewInteractionHub.CancelCopy) {
				return;
			}
			copyFile(f, dest);
		}
	}

	// 移动 && 同盘剪贴
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				String jindu = (String) msg.obj;
				mOperationListener.setMessage(jindu);
				break;
			default:
				break;
			}
		}

	};

	private String JianQieFile(String src, String dest) {
		File file = new File(src);
		if (!file.exists() || file.isDirectory()) {
			return null;
		}
		long xx = 0;
		long yy = 0;
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream(file);
			File destPlace = new File(dest);
			if (!destPlace.exists()) {
				if (!destPlace.mkdirs())
					return null;
			}
			String destPath = Util.makePath(dest, file.getName());
			File destFile = new File(destPath);
			out = new FileOutputStream(destFile);
			buffer = new byte[5120];// 每次最大读取的长度，字节，2的10次方=1MB，此长度为10MB
			int read = 0;
			while ((read = in.read(buffer)) != -1) {
				if (FileViewInteractionHub.CancelCopy) {
					in.close();
					out.close();
					Log.d("fzss", "read-CancelCopy = " + FileViewInteractionHub.CancelCopy);
					return destPath;
				}
				out.write(buffer, 0, read);
				currentProgress += read;
				xx = (currentProgress * 100) / msize;
				if (xx != yy) {
					yy = xx;
					Message msg = Message.obtain();
					String x = String.valueOf("正在剪贴 ... " + yy + "%");
					msg.what = 1;
					msg.obj = x;
					handler.removeMessages(msg.what);
					handler.sendMessage(msg);
				}
			}
			destFile = null;
			destPlace = null;
			file = null;
			in.close();
			out.close();
			in = null;
			out = null;
			return destPath;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
		} finally {
			try {
				if (in != null)
					in.close();
				in = null;
				if (out != null)
					out.close();
				out = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	private void copyFile(File src, String dest) {
		long xx = 0;
		long yy = 0;
		try {
			in = new FileInputStream(src);
			File destPlace = new File(dest);
			if (!destPlace.exists()) {
				if (!destPlace.mkdirs())
					return;
			}
			
			String destPath = Util.makePath(dest, src.getName());
			out = new FileOutputStream(new File(destPath));
			long ll = src.length();
			Log.d("FileOperationHelper_copyFile", "   cd = " + ll);
			if (ll == 0) {
				duqu_zhi = 1;
			} else if (ll < 10485760*2) {
				duqu_zhi = (int) ll;
				Log.d("FileOperationHelper_copyFile", "   duqu_zhi = " + duqu_zhi);
			} else {
				duqu_zhi = 1024;
			}
			buffer = new byte[duqu_zhi];
			int read = 0;
			while ((read = in.read(buffer)) != -1) {

				out.write(buffer);
				currentProgress += read;
				xx = (currentProgress * 100) / msize;
				if (xx != yy) {
					if (FileViewInteractionHub.CancelCopy) {
						Log.d("FileOperationHelper_copyFile", "run-CancelCopy-3 = " + FileViewInteractionHub.CancelCopy);
						break;
					}else {
						yy = xx;
						Message msg = Message.obtain();
						String x = String.valueOf("正在复制 ... " + xx + "%");
						msg.what = 1;
						msg.obj = x;
						handler.removeMessages(msg.what);
						handler.sendMessage(msg);
					}
				}
			}

		} catch (IOException e) {
			Log.d("FileOperationHelper_copyFile", " Error :" + e.getMessage().toString());
		} finally {
			try {
					in.close();
					out.close();
			} catch (IOException e) {
				Log.d("FileOperationHelper_copyFile", " Error :" + e.getMessage().toString());
			}
		}
	}

	private boolean MoveFile(FileInfo f, String dest) {

		if (f == null || dest == null) {
			return false;
		}
		File file = new File(f.filePath);
		String newPath = Util.makePath(dest, f.fileName);
		try {
			return file.renameTo(new File(newPath));
		} catch (SecurityException e) {
		}
		return false;
	}

	private void copyFileList(ArrayList<FileInfo> files) {
		synchronized (mCurFileNameList) {
			mCurFileNameList.clear();
			for (FileInfo f : files) {
				mCurFileNameList.add(f);
				Log.i("cut", "f.filePath000=" + f.filePath);
			}
		}
	}

}
