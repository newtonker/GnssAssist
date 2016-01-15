package com.tonker.gnss.file;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.tonker.gnss.R;
import com.tonker.gnss.constant.Constant;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 这一个类用来创建ListView列表，显示系统的文件夹和文件
 *
 * @author newtonker
 * @date 2014-07-01
 */
public class FileSelectView extends ListView implements OnItemClickListener
{
    //获取当前路径
    private String path;

    //设置相应文件对应的图标，获取相应路径下的所有文件并添加到列表中
    private Map<String, Integer> mImageMap;
    private File[] files;
    private List<Map<String, Object>> list;

    // 回调接口，当其他应用程序打开文件时，回调这一接口是对话框消失
    private OpenFileDialogCallback mOpenFileDialogCallback;

    //constructor
    public FileSelectView(Context context)
    {
        super(context);
    }

    public FileSelectView(Context context, String path, OpenFileDialogCallback openFileDialogCallback)
    {
        super(context);

        this.path = path;
        this.mOpenFileDialogCallback = openFileDialogCallback;
        this.setOnItemClickListener(this);

        //创建可识别的文件与图标映射
        mImageMap = new HashMap<String, Integer>();
        mImageMap.put(Constant.FILE_ROOT, R.drawable.filedialog_root);
        mImageMap.put(Constant.FILE_PARENT, R.drawable.filedialog_folder_up);
        mImageMap.put(Constant.FILE_FOLDER, R.drawable.filedialog_folder);
        mImageMap.put(Constant.FILE_BMP, R.drawable.filedialog_bmp);
        mImageMap.put(Constant.FILE_DOC, R.drawable.filedialog_doc);
        mImageMap.put(Constant.FILE_JPG, R.drawable.filedialog_jpg);
        mImageMap.put(Constant.FILE_MP3, R.drawable.filedialog_mp3);
        mImageMap.put(Constant.FILE_MP4, R.drawable.filedialog_mp4);
        mImageMap.put(Constant.FILE_PPT, R.drawable.filedialog_ppt);
        mImageMap.put(Constant.FILE_TXT, R.drawable.filedialog_txt);
        mImageMap.put(Constant.FILE_WAV, R.drawable.filedialog_wav);
        mImageMap.put(Constant.FILE_ZIP, R.drawable.filedialog_zip);
        mImageMap.put(Constant.FILE_3GP, R.drawable.filedialog_3gp);
        mImageMap.put(Constant.FILE_CAB, R.drawable.filedialog_cab);
        mImageMap.put(Constant.FILE_TIF, R.drawable.filedialog_tif);
        mImageMap.put(Constant.FILE_SWF, R.drawable.filedialog_swf);
        mImageMap.put(Constant.FILE_CHM, R.drawable.filedialog_chm);
        mImageMap.put(Constant.FILE_APK, R.drawable.filedialog_apk);
        //无法识别的后缀时
        mImageMap.put(Constant.FILE_UNKNOWN, R.drawable.filedialog_unknown);
        refreshFileList();
    }

    /**
     * 获取文件的后缀
     *
     * @param filename
     * @return
     */
    private String getSuffix(String filename)
    {
        int index = filename.lastIndexOf(".");
        if (index < 0)
        {
            return "";
        }
        else
        {
            return filename.substring(index + 1).toLowerCase(Locale.getDefault());
        }
    }

    /**
     * 获取后缀所对应的图标
     *
     * @param s
     * @return
     */
    private int getImageId(String s)
    {
        if (null == mImageMap)
        {
            return 0;
        }
        else if (mImageMap.containsKey(s))
        {
            return mImageMap.get(s);
        }
        else
        {
            return mImageMap.get(Constant.FILE_UNKNOWN);
        }
    }

    /**
     * 每点击一次刷新列表
     */
    private void refreshFileList()
    {
        try
        {
            files = new File(path).listFiles();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if (null == files)
        {
            Toast.makeText(getContext(), Constant.ACCESS_ERROR, Toast.LENGTH_SHORT).show();
            return;
        }
        if (null == list)
        {
            list = new ArrayList<Map<String, Object>>(files.length);
        }
        else
        {
            list.clear();
        }

        //创建文件夹列表和文件列表，保证文件夹优先显示
        ArrayList<Map<String, Object>> folderList = new ArrayList<Map<String, Object>>();
        ArrayList<Map<String, Object>> fileList = new ArrayList<Map<String, Object>>();

        Map<String, Object> tempMap;

        if (!path.equals(Constant.FILE_ROOT))
        {
            //添加根目录和返回上一层目录
            tempMap = new HashMap<String, Object>();
            tempMap.put("name", Constant.FILE_ROOT);
            tempMap.put("path", Constant.FILE_ROOT);
            tempMap.put("img", getImageId(Constant.FILE_ROOT));
            list.add(tempMap);

            tempMap = new HashMap<String, Object>();
            tempMap.put("name", Constant.FILE_PARENT);
            tempMap.put("path", path);
            tempMap.put("img", getImageId(Constant.FILE_PARENT));
            list.add(tempMap);
        }
        for (File file : files)
        {
            if (file.isDirectory() && file.listFiles() != null)
            {
                tempMap = new HashMap<String, Object>();
                tempMap.put("name", file.getName());
                tempMap.put("path", file.getPath());
                tempMap.put("img", getImageId(Constant.FILE_FOLDER));
                folderList.add(tempMap);
            }
            else if (file.isFile())
            {
                //获取后缀
                String suffix = getSuffix(file.getName());
                if (suffix.length() > 0)
                {
                    tempMap = new HashMap<String, Object>();
                    tempMap.put("name", file.getName());
                    tempMap.put("path", file.getPath());
                    tempMap.put("img", getImageId(suffix));
                    fileList.add(tempMap);
                }
            }
        }
        list.addAll(folderList);
        list.addAll(fileList);

        SimpleAdapter mSimpleAdapter = new SimpleAdapter(getContext(), list, R.layout.filedialog_item, new String[]{"img", "name", "path"}, new int[]{R.id.filedialog_item_img, R.id.filedialog_item_name, R.id.filedialog_item_path});
        this.setAdapter(mSimpleAdapter);
    }

    /**
     * 当Item点击时的侦听
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        //条目选择
        String mFilePath = (String) list.get(position).get("path");
        String mFileName = (String) list.get(position).get("name");
        //获取当前路径所表示的文件
        File tempFile = new File(mFilePath);

        //如果选择的是根目录或返回上层目录
        if (mFileName.equals(Constant.FILE_ROOT) || mFileName.equals(Constant.FILE_PARENT))
        {
            String tempParent = tempFile.getAbsoluteFile().getParent();
            if (null != tempParent)
            {
                path = tempParent;
            }
            else
            {
                path = Constant.FILE_ROOT;
            }
        }
        else
        {
            if (tempFile.isFile())
            {
                //如果是文件则进行相应操作
                openFile(tempFile, view);
                //回调接口，在文件打开后，对话框消失
                if (null != mOpenFileDialogCallback)
                {
                    mOpenFileDialogCallback.dismissDialog();
                }
            }
            else if (tempFile.isDirectory())
            {
                path = mFilePath;
            }
        }
        this.refreshFileList();
    }


    /**
     * 打开文件
     * @param file
     * @param view
     */
    private void openFile(File file, View view)
    {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        //获取文件file的MIME类型
        String type = getMIMEType(file);
        //设置intent的data和Type属性。
        intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
        //跳转
        view.getContext().startActivity(intent);
    }

    /**
     * 根据文件后缀名获得对应的MIME类型。
     *
     * @param file
     */
    private String getMIMEType(File file)
    {
        String type = "*/*";
        String fName = file.getName();
        // 获取文件的后缀名
        String end = getSuffix(fName);
        if (end.equals(""))
        {
            return type;
        }
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < Constant.MIME_MapTable.length; i++)
        {
            //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么
            if (end.equalsIgnoreCase(Constant.MIME_MapTable[i][0]))
            {
                type = Constant.MIME_MapTable[i][1];
            }
        }
        return type;
    }


}
