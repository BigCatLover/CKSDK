package mytest.zl.com.paytest;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.game.sdk.SdkConstant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class DBHelper extends SQLiteOpenHelper {

	public static final String OUT_DB_NAME="outdbName.db";
	public static boolean dbCanUse=true;
	private Context mContext;
	private String mName;
	public static final String DB_FILE_DIR = Environment.getExternalStorageDirectory().toString()
			+ File.separator + "system_hs" + File.separator+ SdkConstant.PROJECT_CODE+File.separator;
	public DBHelper(Context context, CursorFactory factory,
                    int version) {
		super(context, OUT_DB_NAME, null, version);
		this.mContext=context;
		this.mName=OUT_DB_NAME;
		//由于app不正常数据库升级导致数据库表被删除，如果发现表不存在则进行重新创建
		SQLiteDatabase writableDatabase = getWritableDatabase();
		onCreate(writableDatabase);
		writableDatabase.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 保存登录的用户信息
		db.execSQL("create table if not exists " + LoginTestActivity.TABLENAME
				+ "(_id integer primary key autoincrement,"
				+ LoginTestActivity.USERNAME + " String,"
				+ LoginTestActivity.PASSWORD + " String)");
		db.execSQL("create table if not exists " + LoginTestActivity.TABLENAME_LAST
				+ "(_id integer primary key autoincrement,"
				+ LoginTestActivity.USERNAME + " String,"
				+ LoginTestActivity.PASSWORD + " String)");
		db.execSQL("create table if not exists " + MainActivity.TABLENAME
				+ "(_id integer primary key autoincrement,"
				+ LoginTestActivity.USERNAME + " String,"
				+ LoginTestActivity.PASSWORD + " String)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(newVersion!=oldVersion){
			db.delete(LoginTestActivity.TABLENAME, null, null);
			db.delete(LoginTestActivity.TABLENAME_LAST, null, null);
			onCreate(db);
		}
	}

	@Override
	public SQLiteDatabase getWritableDatabase() {
		SQLiteDatabase writableDatabase = super.getWritableDatabase();
		if(dbCanUse){//外部db可以使用
			SQLiteDatabase realDb = getRealDb();
			if(realDb!=null){
				return realDb;
			}else{
//				Toast.makeText(mContext,"外部存储卡不可用，分包功能不可用！",Toast.LENGTH_SHORT).show();
				Log.e("DBHelper","外部存储卡不可用，分包功能不可用！");
			}
		}
		return writableDatabase;
	}
	@Override
	public SQLiteDatabase getReadableDatabase() {
		SQLiteDatabase readableDatabase = super.getReadableDatabase();
		SQLiteDatabase realDb = getRealDb();
		if(dbCanUse) {//外部db可以使用
			if(realDb!=null){
				return realDb;
			}else{
				Log.e("DBHelper","外部存储卡不可用，分包功能不可用！");
//				Toast.makeText(mContext,"外部存储卡不可用，分包功能不可用！",Toast.LENGTH_SHORT).show();
			}
		}
		return readableDatabase;
	}
	private SQLiteDatabase getRealDb(){
		try {
			if(Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)){
				File fileDir=new File(DB_FILE_DIR);
				if(!fileDir.exists()){//创建目录
					fileDir.mkdirs();
				}
				File tarFilew=new File(DB_FILE_DIR,mName);
				if(!tarFilew.exists()){
					//创建数据库后，拷贝到公开目录下
					File srcData=mContext.getDatabasePath(mName);
					copyFile(srcData, tarFilew);
				}
				return SQLiteDatabase.openOrCreateDatabase(tarFilew,null);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		dbCanUse=false;
		return null;
	}
	/**
	 * 复制文件
	 *
	 * @param from
	 * @param to
	 */
	public static void copyFile(File from, File to) {
		if (null == from || !from.exists()) {
			Log.i("hongliangsdk", "file(from) is null or is not exists!!");
			return;
		}
		if (null == to) {
			return;
		}

		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(from);
			if (!to.exists()) {
				to.createNewFile();
				to.setReadable(true);
				to.setWritable(true);
			}
			os = new FileOutputStream(to);

			byte[] buffer = new byte[1024];
			int len = 0;
			while (-1 != (len = is.read(buffer))) {
				os.write(buffer, 0, len);
			}
			os.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
