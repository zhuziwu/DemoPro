package net.micode.fileexplorer;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import Dialog.MyEventAddAlertDialog;
import Dialog.MyEventAddAlertDialog2;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import net.micode.fileexplorer.FileOperationHelper.IOperationProgressListener;
import net.micode.fileexplorer.FileSortHelper.SortMethod;
import net.micode.fileexplorer.FileViewActivity.SelectFilesCallback;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class FileViewInteractionHub implements IOperationProgressListener {
	private static final String LOG_TAG = "FileViewInteractionHub";
	private IFileInteractionListener mFileViewListener = null;
	private ArrayList<FileInfo> mCheckedFileNameList = new ArrayList<FileInfo>();
	private ArrayList<File> mrepeatFileNameList = new ArrayList<File>();
	private FileOperationHelper mFileOperationHelper;
	private FileSortHelper mFileSortHelper;
	private ProgressDialog progressDialog;
	private Context mContext;
	private String SelectFilePath = "sd";
	private String SelectFilePastePath = "sd";
	private String copypath;
	private String destPath2;
	private boolean df = false;
	private boolean df2 = false;
	public static boolean CancelCopy = false;
	private StatFs stat;
	private ListView mFileListView;
	private Boolean REname = false;
	private MyEventAddAlertDialog2 AlertDialog;
	private String mCurrentPath;
	private String mRoot;
	private SelectFilesCallback mSelectFilesCallback;
	private long filesize = 0;
	private FileInfo file;
	private long free;

	public FileViewInteractionHub(IFileInteractionListener fileViewListener) {
		assert (fileViewListener != null);
		mFileViewListener = fileViewListener;
		setupFileListView();
		mContext = mFileViewListener.getContext();
		mFileSortHelper = new FileSortHelper();
		mFileOperationHelper = new FileOperationHelper(this, mContext);
	}

	public void showProgress(String msg) {
		progressDialog = new ProgressDialog(mContext);
		progressDialog.setMessage(msg);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, mContext.getString(R.string.quxiao),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						CancelCopy = true;
						Log.d("fzss", "progressDialog-CancelCopy = " + CancelCopy);
					}
				});
		progressDialog.show();
	}

	public void sortCurrentList() {
		mFileViewListener.sortCurrentList(mFileSortHelper);
	}

	public ArrayList<FileInfo> getSelectedFileList() {
		return mCheckedFileNameList;
	}

	public boolean canPaste() {
		return mFileOperationHelper.canPaste();
	}

	public FileInfo getItem(int pos) {
		return mFileViewListener.getItem(pos);
	}

	public boolean isInSelection() {
		return mCheckedFileNameList.size() > 0;
	}

	public boolean isMoveState() {
		return mFileOperationHelper.isMoveState() || mFileOperationHelper.canPaste();
	}

	public void onOperationSelectAllOrCancel() {
		if (!isSelectedAll()) {
			onOperationSelectAll();
		} else {
			clearSelection();
		}
	}

	public void onOperationSelectAll() {
		mCheckedFileNameList.clear();
		for (FileInfo f : mFileViewListener.getAllFiles()) {
			f.Selected = true;
			mCheckedFileNameList.add(f);
		}
		mFileViewListener.onDataChanged();
	}

	public boolean isRoot() {
		if (mCurrentPath.equals(mRoot)) {
			return true;
		}
		return false;
	}

	public boolean onOperationUpLevel() {
		if (!mCurrentPath.equals(mRoot)) {
			mCurrentPath = new File(mCurrentPath).getParent();
			refreshFileList();
			return true;
		}
		return false;
	}

	public void onOperationCreateFolder() {
		final MyEventAddAlertDialog alertDialog = new MyEventAddAlertDialog(mContext);
		final View floderNameSetView = (View) ((Activity) mContext).getLayoutInflater()
				.inflate(R.layout.floder_name_set, null);
		final EditText folderNameEditText = (EditText) floderNameSetView.findViewById(R.id.edittext_folder_name);

		alertDialog.builder(R.layout.dialog_them);
		alertDialog.setTitle(mContext.getString(R.string.newfoldertxt));
		alertDialog.setCancelable(true);
		alertDialog.setNegativeButton(mContext.getString(R.string.quxiao), new OnClickListener() {
			public void onClick(View v) {
				alertDialog.missMydialog();
			}
		});
		alertDialog.setPositiveButton(mContext.getString(R.string.baocun), new OnClickListener() {

			public void onClick(View v) {
				String ss = folderNameEditText.getText().toString();
				if (ss.length() > 25) {
					UtilToast.showToast(mContext, R.string.toast_onlyone_text);
				} else if (ss.length() == 0) {
					UtilToast.showToast(mContext, R.string.operation_rename_message);
				} else {
					doCreateFolder(ss);
					alertDialog.missMydialog();
				}
			}
		});
		alertDialog.setView(floderNameSetView);
		alertDialog.show();
	}

	public boolean doCreateFolder(String text) {
		if (TextUtils.isEmpty(text))
			return false;

		if (CreateFolder(mCurrentPath, text)) {
			// mFileViewListener.addSingleFile(Util.GetFileInfo(Util.makePath(mCurrentPath,
			// text)));
			// mFileListView.setSelection(mFileListView.getCount() - 1);
			refreshFileList();
		} else {
			if (REname) {
				new AlertDialog.Builder(mContext).setMessage(mContext.getString(R.string.fail_to_create_folder))
						.setPositiveButton(R.string.confirm, null).create().show();
				REname = false;
			} else {
				new AlertDialog.Builder(mContext).setMessage(mContext.getString(R.string.fail_to_create_folder2))
						.setPositiveButton(R.string.confirm, null).create().show();
			}

			return false;
		}
		return true;
	}

	public boolean CreateFolder(String path, String name) {
		Log.v(LOG_TAG, "CreateFolder >>> " + path + "," + name);

		File f = new File(Util.makePath(path, name));
		if (f.exists()) {
			Log.v(LOG_TAG, "zou");
			REname = true;
			return false;
		}
		return f.mkdir();
	}

	public void onSortChanged(SortMethod s) {
		if (mFileSortHelper.getSortMethod() != s) {
			mFileSortHelper.setSortMethog(s);
			sortCurrentList();
		}
	}

	public void onOperationCopy() {
		onOperationCopy(getSelectedFileList());
	}

	public void onOperationCopy(ArrayList<FileInfo> files) {
		SelectFile(); // 获取选择文件的存储环境
		mFileOperationHelper.Copy(files);
		// refreshFileList(); // 复制不做隐藏选中文件操作
	}

	private void SelectFile() {
		if (mCurrentPath.contains(GlobalConsts.SDCARD_PATH)) {
			SelectFilePath = "sd";
		} else if (mCurrentPath.contains(GlobalConsts.USB_PATH3)) {
			SelectFilePath = "usb2";
		} else {
			SelectFilePath = "usb1";
		}
		copypath = mCurrentPath;
	}

	public void onOperationMove() {
		SelectFile();
		mFileOperationHelper.StartMove(getSelectedFileList());
		// refreshFileList();
	}

	public void refreshFileList() {
		clearSelection();
		if (new File(mCurrentPath).exists()) {
			mFileViewListener.onRefreshFileList(mCurrentPath, mFileSortHelper);
		} else {
			UtilToast.showToast(mContext, R.string.toast_bukedu);
			if (mCurrentPath.contains(GlobalConsts.SDCARD_PATH)) {
				mCurrentPath = GlobalConsts.SDCARD_PATH;
			} else if (mCurrentPath.contains(GlobalConsts.USB_PATH3)) {
				mCurrentPath = GlobalConsts.USB_PATH3;
			} else {
				mCurrentPath = GlobalConsts.USB_PATH2;
			}
			mFileViewListener.onRefreshFileList(mCurrentPath, mFileSortHelper);
		}

	}

	public void refreshQueryList(String kString) {
		mFileViewListener.onRefreshQueryList(mCurrentPath, mFileSortHelper, kString);
	}

	public void onOperationRename() {

		if (getSelectedFileList().size() == 0)
			return;

		final FileInfo f = getSelectedFileList().get(0);
		clearSelection();
		final MyEventAddAlertDialog addAlertDialog = new MyEventAddAlertDialog(mContext);

		final View floderNameSetView1 = (View) ((Activity) mContext).getLayoutInflater()
				.inflate(R.layout.floder_name_set, null);
		final EditText folderNameEditText1 = (EditText) floderNameSetView1.findViewById(R.id.edittext_folder_name);
		if (f.IsDir) {
			folderNameEditText1.setText(f.fileName);
		} else {
			try {
				folderNameEditText1.setText(f.fileName.substring(0, f.fileName.lastIndexOf(".")));
			} catch (Exception e) {
				folderNameEditText1.setText(f.fileName);
			}

		}
		folderNameEditText1.setSelection(folderNameEditText1.getText().length());
		addAlertDialog.builder(R.layout.dialog_them);
		addAlertDialog.setTitle(mContext.getString(R.string.text_title_rename));
		addAlertDialog.setCancelable(true);
		addAlertDialog.setNegativeButton(mContext.getString(R.string.quxiao), new OnClickListener() {
			public void onClick(View v) {
				addAlertDialog.missMydialog();
			}
		});
		addAlertDialog.setPositiveButton(mContext.getString(R.string.baocun), new OnClickListener() {
			public void onClick(View v) {
				if (folderNameEditText1.getText().length() >= 30) {
					UtilToast.showToast(mContext, R.string.toast_onlyone_text);
				} else if (folderNameEditText1.getText().length() == 0) {
					UtilToast.showToast(mContext, R.string.operation_rename_message);
				} else {
					if (f.IsDir) {
						doRename(f, folderNameEditText1.getText().toString(), true);
					} else {
						doRename(f, folderNameEditText1.getText().toString()
								+ f.fileName.substring(f.fileName.lastIndexOf(".")), false);
					}
					addAlertDialog.missMydialog();
				}
			}

		});

		addAlertDialog.setView(floderNameSetView1);
		addAlertDialog.show();
	}

	public boolean doRename(final FileInfo f, String text, Boolean isdir) {
		if (TextUtils.isEmpty(text))
			return false;

		if (mFileOperationHelper.Rename(f, text, isdir)) {
			mFileViewListener.refreshll();
		} else {
			new AlertDialog.Builder(mContext).setMessage(mContext.getString(R.string.fail_to_rename))
					.setPositiveButton(R.string.confirm, null).create().show();
			return false;
		}

		return true;
	}

	public void onOperationDelete() {
		doOperationDelete(getSelectedFileList());
	}

	private void doOperationDelete(final ArrayList<FileInfo> selectedFileList) {
		if (selectedFileList.size() < 1) {
			UtilToast.showToast(mContext, R.string.delete_message);
		} else {
			Dialog dialog = new AlertDialog.Builder(mContext)
					.setMessage(mContext.getString(R.string.operation_delete_confirm_message))
					.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							if (mFileOperationHelper.Delete(selectedFileList)) {
								showProgress(mContext.getString(R.string.operation_deleting));

							}
						}
					}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							clearSelection();
						}
					}).create();
			dialog.show();
		}

	}

	public void onOperationInfo(FileInfo arrayList) {
		mCheckedFileNameList.add(arrayList);
		if (getSelectedFileList().size() == 0)
			return;
		file = getSelectedFileList().get(0);
		// if (file1.isDirectory()) {
		// file.fileSize = getFolderSize(file1);
		// }
		if (file == null)
			return;
		View fileDetailsView = (View) ((Activity) mContext).getLayoutInflater().inflate(R.layout.file_detail, null);
		// 文件名
		TextView txtfileName = (TextView) fileDetailsView.findViewById(R.id.txt_file_name);
		// 文件路径
		TextView txtfilePath = (TextView) fileDetailsView.findViewById(R.id.txt_file_path);
		// 文件大小
		TextView txtfileSize = (TextView) fileDetailsView.findViewById(R.id.txt_file_size);
		// 时间
		TextView txtfileDate = (TextView) fileDetailsView.findViewById(R.id.txt_file_date);
		// 可读
		TextView txtfileRead = (TextView) fileDetailsView.findViewById(R.id.txt_file_isread);
		// 可写
		TextView txtfileWrite = (TextView) fileDetailsView.findViewById(R.id.txt_file_iswrite);
		txtfileName.setText(file.fileName);
		txtfilePath.setText(file.filePath);
		txtfileSize.setText(formatFileSizeString(getFolderSize(new File(file.filePath))));
		txtfileDate.setText(formatDateString(mContext, new File(file.filePath).lastModified()));
		txtfileRead.setText(R.string.yes);
		txtfileWrite.setText(file.canWrite ? R.string.yes : R.string.no);
		if (AlertDialog == null) {
			AlertDialog = new MyEventAddAlertDialog2(mContext);
			Log.d("AlertDialog", "初始化！！");
		}
		AlertDialog.builder(R.layout.dialog_detail);
		AlertDialog.setTitle(mContext.getString(R.string.text_title_xiangxi));
		AlertDialog.setView(fileDetailsView);
		AlertDialog.show();
		clearSelection();
	}

	public void dismiss() {
		if (AlertDialog != null) {
			AlertDialog.dismiss();
		}
	}

	private String formatFileSizeString(long size) {
		String ret = "";
		if (size >= 1024) {
			ret = convertStorage(size);
			ret += (" (" + mContext.getResources().getString(R.string.file_size, size) + ")");
		} else {
			ret = mContext.getResources().getString(R.string.file_size, size);
		}
		return ret;
	}

	private String convertStorage(long size) {
		long kb = 1024;
		long mb = kb * 1024;
		long gb = mb * 1024;

		if (size >= gb) {
			return String.format("%.1f GB", (float) size / gb);
		} else if (size >= mb) {
			float f = (float) size / mb;
			return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
		} else if (size >= kb) {
			float f = (float) size / kb;
			return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
		} else
			return String.format("%d B", size);
	}

	public void onOperationButtonCancel() {
		mFileOperationHelper.clear();
		if (isSelectingFiles()) {
			mSelectFilesCallback.selected(null);
			mSelectFilesCallback = null;
			clearSelection();
		} else if (mFileOperationHelper.isMoveState()) {
			mFileOperationHelper.EndMove1(null);
			refreshFileList();
		} else {
			refreshFileList();
		}
	}

	public int getItemCount() {
		return mFileViewListener.getItemCount();
	}

	private void setupFileListView() {
		mFileListView = (ListView) mFileViewListener.getViewById(R.id.file_path_list);
		mFileListView.setLongClickable(true);
		mFileListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onListItemClick(parent, view, position, id);
			}
		});
	}

	public boolean isFileSelected(String filePath) {
		return mFileOperationHelper.isFileSelected(filePath);
	}

	public void onListItemClick(AdapterView<?> parent, View view, int position, long id) {
		FileInfo lFileInfo = mFileViewListener.getItem(position);
		if (lFileInfo == null) {
			Log.e(LOG_TAG, "file does not exist on position:" + position);
			return;
		}
		if (!lFileInfo.IsDir) {
			Log.d("orapk", "  lFileInfo.filePath = " + lFileInfo.filePath);
			viewFile(mContext, lFileInfo.filePath);
			return;
		}
		if (!lFileInfo.Selected) {
			mCurrentPath = getAbsoluteName(mCurrentPath, lFileInfo.fileName);
			refreshFileList();
		} else {
			UtilToast.showToast(mContext, R.string.copy_error);
		}

	}

	private void viewFile(Context context, final String filePath) {
		String[] a = filePath.split("[.]");
		if (a.length <= 1) {
			return;
		}
		if (a[1].equals("apk")) {
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(android.content.Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
			context.startActivity(intent);
		}
	}

	public void setRootPath(String path) {
		mRoot = path;
		mCurrentPath = path;
	}

	public String getRootPath() {
		return mRoot;
	}

	public String getCurrentPath() {
		return mCurrentPath;
	}

	public void setCurrentPath(String path) {
		mCurrentPath = path;
	}

	private String getAbsoluteName(String path, String name) {
		return path.equals(GlobalConsts.ROOT_PATH) ? path + name : path + File.separator + name;
	}

	public boolean onCheckItem(FileInfo f, View v) {
		if (isMoveState())
			return false;

		if (isSelectingFiles() && f.IsDir)
			return false;

		if (f.Selected) {
			mCheckedFileNameList.add(f);
		} else {
			mCheckedFileNameList.remove(f);
		}
		return true;
	}

	private boolean isSelectingFiles() {
		return mSelectFilesCallback != null;
	}

	public boolean isSelectedAll() {
		return mFileViewListener.getItemCount() != 0 && mCheckedFileNameList.size() == mFileViewListener.getItemCount();
	}

	public void clearSelection() {
		if (mCheckedFileNameList.size() > 0) {
			for (FileInfo f : mCheckedFileNameList) {
				if (f == null) {
					continue;
				}
				f.Selected = false;
			}
			mCheckedFileNameList.clear();
			mFileViewListener.onDataChanged();
		}
	}

	public boolean onBackPressed() {
		if (!onOperationUpLevel()) {
			return false;
		}
		return true;
	}

	public void moveFileFrom(ArrayList<FileInfo> files) {
		mFileOperationHelper.StartMove(files);
		refreshFileList();
	}

	public void startSelectFiles(SelectFilesCallback callback) {
		mSelectFilesCallback = callback;
	}

	public void onFinish(String d) {
		if (CancelCopy) {
			UtilToast.showToast(mContext, R.string.toast_bachu);
			progressDialog.dismiss();
		} else {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			if (d.equals("D")) {
				UtilToast.showToast(mContext, R.string.toast_delsucess);
			} else if (d.equals("J")) {
				UtilToast.showToast(mContext, R.string.toast_jianqie);
			} else if (d.equals("F")) {
				UtilToast.showToast(mContext, R.string.toast_fuzhi);
			}
		}
		Log.d("FileViewInteractionHub_onFinish", " d = " + d);
		CancelCopy = false;
		clearSelection();
		mFileViewListener.refreshll();
	}

	public void onFinish2() {
		mFileOperationHelper.Delete2();
	}

	public void onFinish3() {
		if (CancelCopy) {
			UtilToast.showToast(mContext, R.string.toast_bachu);
			progressDialog.dismiss();
		} else {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			UtilToast.showToast(mContext, R.string.toast_cutsucess);
		}

		CancelCopy = false;
		clearSelection();
		mFileViewListener.refreshll();
	}

	public void onOperationButtonConfirm() {
		Log.d("haoshi", " 复制点击事件执行！！！！！");
		if (mCurrentPath.contains(GlobalConsts.SDCARD_PATH)) {
			SelectFilePastePath = "sd";
		} else if (mCurrentPath.contains(GlobalConsts.USB_PATH3)) {
			SelectFilePastePath = "usb2";
		} else {
			SelectFilePastePath = "usb1";
		}
		if (isSelectingFiles()) {
			mSelectFilesCallback.selected(mCheckedFileNameList);
			mSelectFilesCallback = null;
			clearSelection();
		} else if (mFileOperationHelper.isMoveState()) {
			jianqie();
		} else {
			Copy();
		}
	}

	private void DeleteFile(File f) {
		if (f.isDirectory()) {
			for (File child : f.listFiles()) {
				DeleteFile(child);
				child = null;
			}
		}
		f.delete();
	}


	private void jianqie() {

		ArrayList<FileInfo> f = mFileOperationHelper.getFileList();
		for (int i = 0; i < f.size(); i++) {
			File file = new File(f.get(i).filePath);
			filesize += getFolderSize(file);
		}
		// 内部存储
		if (SelectFilePastePath.equals(SelectFilePath)) {
			if (!mCurrentPath.equals(copypath)) {
				SameClipThings();
				if (df2) {
					JieTieDialog();
				} else {
					if (mFileOperationHelper.EndMove(mCurrentPath)) {
						showProgress(mContext.getString(R.string.operation_moving));
					}
				}
			} else {
				mFileOperationHelper.setMovinr(false);
				mFileOperationHelper.clear();
				clearSelection();
				refreshFileList();
				mFileViewListener.refreshll();
				UtilToast.showToast(mContext, R.string.toast_cutsucess);
				return;
			}
		} else {
			CalculateSize();// 计算剩余空间
			if (free <= filesize) {
				mFileOperationHelper.setMovinr(false);
				NoSaveSpace();
				free = 0;
				filesize = 0;
			} else {
				SameClipThings();
				if (df2) {
					JieTieDialog();
				} else {
					mFileOperationHelper.Paste2(mCurrentPath, filesize);
					mFileOperationHelper.setMovinr(false);
					free = 0;
					filesize = 0;
				}

			}
		}
	}

	private void copyCopydialog() {
		Dialog dialog = new AlertDialog.Builder(mContext).setTitle(R.string.toast_zhantie)
				.setMessage(R.string.toast_zhantiefugai)
				.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mFileOperationHelper.clear();
						mrepeatFileNameList.clear();
						mFileViewListener.refreshll();
						free = 0;
						filesize = 0;
					}
				}).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						showProgress("正在复制...");
						for (File child : mrepeatFileNameList) {
							DeleteFile(child);
							child = null;
						}
						mrepeatFileNameList.clear();
						CopyStart();
					}
				}).create();
		dialog.show();
	}

	private void JieTieDialog() {
		Dialog dialog = new AlertDialog.Builder(mContext).setTitle(R.string.toast_zhantie)
				.setMessage(R.string.toast_zhantiefugai)
				.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mFileOperationHelper.clear();
						mFileOperationHelper.setMovinr(false);
						mrepeatFileNameList.clear();
						clearSelection();
						refreshFileList();
						mFileViewListener.refreshll();
						df2 = false;
						free = 0;
						filesize = 0;
						dialog.dismiss();
					}
				}).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						df2 = false;
						for (File child : mrepeatFileNameList) {
							DeleteFile(child);
						}
						if (SelectFilePastePath.equals(SelectFilePath)) {
							if (mFileOperationHelper.EndMove(mCurrentPath)) {
								showProgress(mContext.getString(R.string.operation_moving));
							}
						} else {
							mFileOperationHelper.Paste2(mCurrentPath, filesize); // 不同盘的剪切操作，先复制后删除
						}
						mrepeatFileNameList.clear();
						mFileOperationHelper.setMovinr(false);
						free = 0;
						filesize = 0;
					}
				}).create();
		dialog.show();
	}

	private void SameClipThings() {
		ArrayList<FileInfo> m = mFileOperationHelper.getFileList();
		for (FileInfo fio : m) {
			destPath2 = Util.makePath(mCurrentPath, fio.fileName);
			File destFile = new File(destPath2);
			if (destFile.exists()) {
				df2 = true;
				if (mrepeatFileNameList == null) {
					mrepeatFileNameList = new ArrayList<File>();
				}
				mrepeatFileNameList.add(destFile);
			}
			destFile = null;
		}
		m = null;
	}

	private void SameCopyThings() {
		Log.d("haoshi", " 检索文件是否重复--开始");
		for (FileInfo fio : mFileOperationHelper.getFileList()) {
			filesize += getFolderSize(new File(fio.filePath));
			File destFile = new File(Util.makePath(mCurrentPath, fio.fileName));
			if (destFile.exists()) {
				df = true;
				mrepeatFileNameList.add(destFile);
			}
			destFile = null;
		}
		Log.d("haoshi", " 检索文件是否重复--完成");
	}

	private void NoSaveSpace() {
		UtilToast.showToast(mContext, R.string.toast_kongjianbuzu);
		mFileOperationHelper.clear();
		mFileViewListener.refreshll();
	}

	private void Copy() {
		SameCopyThings();
		if (df) {
			copyCopydialog();
			df = false;
		} else {
			CopyStart();
		}
	}
	
	private void CopyStart(){
		CalculateSize();
		if (free <= filesize) {
			free = 0;
			filesize = 0;
			NoSaveSpace();
		} else {
			if (SelectFilePastePath.equals(SelectFilePath)) {
				if (mCurrentPath.equals(copypath)) {
					mFileOperationHelper.clear();
					mFileViewListener.refreshll();
					UtilToast.showToast(mContext, R.string.toast_fuzhi);
					free = 0;
					filesize = 0;
				} else {
					showProgress("正在复制...");
					mFileOperationHelper.Paste(mCurrentPath, filesize);
					free = 0;
					filesize = 0;
				}
			} else {
				showProgress("正在复制...");
				mFileOperationHelper.Paste(mCurrentPath, filesize);
				free = 0;
				filesize = 0;
			}
		}
		// mFileOperationHelper.Paste(mCurrentPath, filesize);
		// free = 0;
		// filesize = 0;
	}
	

	private void CalculateSize() {
		if (SelectFilePastePath.equals("usb1")) {
			stat = new StatFs(GlobalConsts.USB_PATH2);
			long x = stat.getAvailableBlocks();
			long y = stat.getBlockSize();
			free = x * y;
		} else if (SelectFilePastePath.equals("usb2")) {
			stat = new StatFs(GlobalConsts.USB_PATH3);
			long x = stat.getAvailableBlocks();
			long y = stat.getBlockSize();
			free = x * y;
		} else {
			stat = new StatFs(GlobalConsts.SDCARD_PATH);
			long x = stat.getAvailableBlocks();
			long y = stat.getBlockSize();
			free = x * y;
		}
		stat = null;
	}

	private long getFolderSize(File file) {
		long size = 0;
		if (file.isDirectory()) {
			File[] fileList = file.listFiles();
			for (File i : fileList) {
				if (i.isDirectory()) {
					size = size + getFolderSize(i);
				} else {
					size = size + i.length();
				}
			}
		} else {
			size = size + file.length();
		}
		return size;
	}

	private String formatDateString(Context context, long time) {
		DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
		DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
		Date date = new Date(time);
		return dateFormat.format(date) + " " + timeFormat.format(date);
	}

	
	@Override
	public void setMessage(String d) {
		if (progressDialog != null) {
			progressDialog.setMessage(d);
		} else {
			showProgress(d);
		}
	}
}
