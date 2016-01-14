package cn.ac.casic.gnss.file;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

/**
 * 这一个类用来创建打开文件时的对话框
 * @author newtonker
 * @date 2014-07-01
 */
public class OpenFileDialog
{
	private AlertDialog mOpenFileDialog;

	public OpenFileDialog(Context context, String folderPath)
	{
		if(null == folderPath)
		{
			Toast.makeText(context, "访问的文件夹不存在", Toast.LENGTH_SHORT).show();
		}
		else
		{
			mOpenFileDialog = new AlertDialog.Builder(context)
					.setView(new FileSelectView(context, folderPath, mOpenFileDialogCallback))
					.setTitle("打开文件夹")
					.create();
			mOpenFileDialog.show();
		}
	}

	private OpenFileDialogCallback mOpenFileDialogCallback = new OpenFileDialogCallback()
	{
		@Override
		public void dismissDialog()
		{
			if(null == mOpenFileDialog)
			{
				return;
			}
			else
			{
				mOpenFileDialog.dismiss();
			}
		}
	};

	public OpenFileDialogCallback getmOpenFileDialogCallback()
	{
		return mOpenFileDialogCallback;
	}
}