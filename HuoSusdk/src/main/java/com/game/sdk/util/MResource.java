package com.game.sdk.util;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import com.game.sdk.SdkConstant;
import com.kymjs.rxvolley.toolbox.FileUtils;

import java.io.File;

/**
 * 根据资源的名字获取其ID值
 * 
 * @author janecer
 * 
 */
public class MResource {
	public static final String LAYOUT="layout";
	public static final String ID="id";
	public static final String DRAWABLE="drawable";

	public static String baseFilePath = FileUtils.getSDCardPath()+File.separator+"huounion"+File.separator;

	public static String PATH_FILE_ICON_LOGO = baseFilePath + SdkConstant.PROJECT_CODE+"_"+"icon_logo.png";
	public static String PATH_FILE_ICON_FLOAT = baseFilePath + SdkConstant.PROJECT_CODE+"_"+"icon_float.png";
	public static String PATH_FILE_ICON_FLOAT_LEFT= baseFilePath + SdkConstant.PROJECT_CODE+"_"+"icon_float_left.png";
	public static String PATH_FILE_ICON_FLOAT_RIGHT= baseFilePath + SdkConstant.PROJECT_CODE+"_"+"icon_float_right.png";


	public static int getIdByName(Context context, String className, String name) {
		return context.getResources().getIdentifier(name,className,context.getPackageName());
	}
	public static int getIdByName(Context context,String id) {
		String[] split = id.split("\\.");
		return context.getResources().getIdentifier(split[2],split[1],context.getPackageName());

	}

	public static void loadImgFromSDCard(ImageView imageView,String imgPath){
		if (imageView == null || TextUtils.isEmpty(imgPath) || ! new File(imgPath).exists()){
			return;
		}

		try{
			imageView.setImageURI(Uri.fromFile(new File(imgPath)));
		}catch (Exception e){

			System.err.print(e.getMessage());
		}
	}

}
