package net.micode.fileexplorer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import java.util.List;


public class FileListAdapter extends ArrayAdapter<FileInfo> {

	private LayoutInflater mInflater;
	private FileViewInteractionHub mFileViewInteractionHub;
	private FileListItem listItem;
	private FileInfo lFileInfo;
	private boolean isShowDelete;
	private boolean isShowXq;
	private List<FileInfo> mFileInfos;
	public FileListAdapter(Context context, int resource, List<FileInfo> objects, FileViewInteractionHub f) {
		super(context, resource, objects);
		mInflater = LayoutInflater.from(context);
		mFileViewInteractionHub = f;
		mFileInfos = objects;
	}
	public void setIsShowXq(boolean isShowXq) {
		this.isShowXq = isShowXq;
		notifyDataSetChanged();
	}
	public void setIsShowDelete(boolean isShowDelete) {
		this.isShowDelete = isShowDelete;
		notifyDataSetChanged();
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView != null) {
			view = convertView;
		} else {
			view = mInflater.inflate(R.layout.file_browse_item, parent, false);
		}
		listItem = (FileListItem) view;
		lFileInfo = mFileInfos.get(position);
		Log.d("FileListAdapter", " position = " + position);
		listItem.bind(lFileInfo, mFileViewInteractionHub,isShowDelete,isShowXq);
		return view;
	}
}
