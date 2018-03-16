package com.game.sdk.log;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.game.sdk.domain.NotProguard;

/**
 * Toast统一管理类
 * @author: 刘红亮
 * @类   说   明:
 * @version 1.1
 */
@NotProguard
public class T {
	// Toast
	private static Toast toast;

    /**
     *  debug状态L.debug=true的时候才吐司
     */

    public static void ds(Context context, CharSequence message) {
        if (null == toast) {
            toast = Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            toast.setText(message);
        }
        if (L.isDebug()) {
            toast.show();
        }
    }
    /**
     *  短时间显示Toast
     */

	public static void s(Context context, CharSequence message) {
		if (null == toast) {
			toast = Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			toast.setText(message);
		}
		toast.show();
	}

	public static void s(Context context,int resMsg) {
		if (null == toast) {
			toast = Toast.makeText(context.getApplicationContext().getApplicationContext(), resMsg, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			toast.setText(resMsg);
		}
		toast.show();
	}

	/**
	 * 短时间显示Toast
	 * 
	 * @param context
	 * @param message
	 */
	public static void showShort(Context context, int message) {
		if (null == toast) {
			toast = Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT);
			// toast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			toast.setText(message);
		}
		toast.show();
	}

	/**
	 * 长时间显示Toast
	 * 
	 * @param context
	 * @param message
	 */
	public static void showLong(Context context, CharSequence message) {
		if (null == toast) {
			toast = Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_LONG);
			// toast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			toast.setText(message);
		}
		toast.show();
	}

	/**
	 * 长时间显示Toast
	 * 
	 * @param context
	 * @param message
	 */
	public static void showLong(Context context, int message) {
		if (null == toast) {
			toast = Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_LONG);
			// toast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			toast.setText(message);
		}
		toast.show();
	}

	/**
	 * 自定义显示Toast时间
	 * 
	 * @param context
	 * @param message
	 * @param duration
	 */
	public static void show(Context context, CharSequence message, int duration) {
		if (null == toast) {
			toast = Toast.makeText(context.getApplicationContext(), message, duration);
			// toast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			toast.setText(message);
		}
		toast.show();
	}

	/**
	 * 自定义显示Toast时间
	 * 
	 * @param context
	 * @param message
	 * @param duration
	 */
	public static void show(Context context, int message, int duration) {
		if (null == toast) {
			toast = Toast.makeText(context.getApplicationContext(), message, duration);
			// toast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			toast.setText(message);
		}
		toast.show();
	}

	/** Hide the toast, if any. */
	public static void hideToast() {
		if (null != toast) {
			toast.cancel();
		}
	}

    /**
     * 带图片消息提示
     * @param context
     * @param ImageResourceId
     * @param text
     * @param duration
     */
    public static void ImageToast(Context context,int ImageResourceId,CharSequence text,int duration){
        //创建一个Toast提示消息
        toast = Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_LONG);
        //设置Toast提示消息在屏幕上的位置
        toast.setGravity(Gravity.CENTER, 0, 0);
        //获取Toast提示消息里原有的View
        View toastView = toast.getView();
        //创建一个ImageView
        ImageView img = new ImageView(context);
        img.setImageResource(ImageResourceId);
        //创建一个LineLayout容器
        LinearLayout ll = new LinearLayout(context);
        //向LinearLayout中添加ImageView和Toast原有的View
        ll.addView(img);
        ll.addView(toastView);
        //将LineLayout容器设置为toast的View
        toast.setView(ll);
        //显示消息
        toast.show();
    }

}
