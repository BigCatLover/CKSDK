package com.game.sdk.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class DimensionUtil {
	/**
	 * 获取屏幕的宽度
	 * 
	 * @param
	 * @return
	 */
	public static int getWidth(Context ctx) {
		return ctx.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * 获取屏幕的高度
	 * 
	 * @param
	 * @return
	 */
	public static int getHeight(Context ctx) {
		return ctx.getResources().getDisplayMetrics().heightPixels;
	}

	/**
	 * @param activity
	 * @return
	 */
	public static float density(Activity activity) {
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics.density;
	}

	public static int dip2px(Context ctx, int dpValue) {
		float scale = ctx.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(Context ctx, int pxValue) {
		float scale = ctx.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
