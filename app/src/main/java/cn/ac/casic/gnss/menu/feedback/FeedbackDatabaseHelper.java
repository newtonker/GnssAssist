package cn.ac.casic.gnss.menu.feedback;

import cn.ac.casic.gnss.constant.Constant;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FeedbackDatabaseHelper extends SQLiteOpenHelper
{
	private static FeedbackDatabaseHelper mDatabaseHelper;

	public FeedbackDatabaseHelper(Context context, int version)
	{
		//使用默认的CursorFactory
		super(context, Constant.FEEDBACK_DATEBASE_FILE, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		//第一次使用数据库时自动建表
		db.execSQL(Constant.CREATE_TABLE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{

	}

	//创建单例模式
	public static FeedbackDatabaseHelper getInstance(Context context, int version)
	{
		if(null == mDatabaseHelper)
		{
			mDatabaseHelper = new FeedbackDatabaseHelper(context, version);
		}
		return mDatabaseHelper;
	}

}
