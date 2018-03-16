package com.game.sdk.util;

import android.content.Context;
import android.view.View;

import java.lang.reflect.Field;

/**
 * Created by zhanglei on 2017/5/5.
 */

public class ResourceUtils {
    private static Context mContext;

    protected static int getResourceId(String paramString1, String paramString2) {
        return mContext.getResources().getIdentifier(paramString2, paramString1,
                mContext.getPackageName());
    }

    public static String getString(Context context,String paramString) {
        mContext = context;
        return mContext.getResources().getString(getStringId(paramString));
    }

    public static String getString(String paramString, Object[] paramArrayOfObject) {
        return mContext.getResources().getString(getStringId(paramString),
                paramArrayOfObject);
    }

    public static int getStringId(String paramString) {
        return getResourceId("string", paramString);
    }

    public static View findViewByName(Context context,View paramView, String paramString) {
        mContext = context;
        return paramView.findViewById(getResourceId("id", paramString));
    }

    public static int getDimenId(Context context,String paramString) {
        mContext = context;
        return getResourceId("dimen", paramString);
    }

    public static int getColorId(String paramString) {
        return getResourceId("color", paramString);
    }

    public static int getColor(Context context,String paramString){
        mContext = context;
        return context.getResources().getColor(getColorId(paramString),context.getTheme());
    }

    public static int getDrawableId(Context context,String paramString) {
        mContext = context;
        return getResourceId("drawable", paramString);
    }

    public static int getThemeId(Context context,String paramString) {
        mContext = context;
        return getResourceId("style", paramString);
    }
    public static int getLayoutId(Context context,String paramString) {
        mContext = context;
        return getResourceId("layout", paramString);
    }
    public static int getResourceId(Context context,String paramString) {
        mContext = context;
        return getResourceId("id", paramString);
    }

    public static int getArrayId(Context context, String paramString) {
        mContext = context;
        return getResourceId("array", paramString);
    }

    private static Object getResourceId(Context context, String name,
                                        String type) {

        String className = context.getPackageName() + ".R";

        try {

            Class cls = Class.forName(className);

            for (Class childClass : cls.getClasses()) {

                String simple = childClass.getSimpleName();

                if (simple.equals(type)) {

                    for (Field field : childClass.getFields()) {

                        String fieldName = field.getName();

                        if (fieldName.equals(name)) {

                            System.out.println(fieldName);

                            return field.get(null);

                        }

                    }

                }

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return null;

    }

    public static int[] getStyleableArray(Context context, String name) {

        return (int[]) getResourceId(context, name, "styleable");

    }
    public static int getStyleable(Context context, String name) {

        return ((Integer)getResourceId(context, name,"styleable")).intValue();

    }

    public static int getInteger(Context context, String string) {
        mContext = context;
        return getResourceId("integer", string);
    }
}
