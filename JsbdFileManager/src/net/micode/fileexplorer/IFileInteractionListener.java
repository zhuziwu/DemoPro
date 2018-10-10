package net.micode.fileexplorer;

import android.content.Context;
import android.view.View;

import java.util.Collection;

public interface IFileInteractionListener {

	public View getViewById(int id);

	public Context getContext();

	public void onDataChanged();

	public FileInfo getItem(int pos);

	public void sortCurrentList(FileSortHelper sort);

	public Collection<FileInfo> getAllFiles();

	public boolean onRefreshFileList(String path, FileSortHelper sort);

	public boolean onRefreshQueryList(String path, FileSortHelper sort, String keyword);

	public int getItemCount();

	public void refreshll();
	
}
