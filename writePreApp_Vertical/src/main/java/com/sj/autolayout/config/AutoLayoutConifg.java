package com.sj.autolayout.config;

import com.easier.writepre.R;
import com.sj.autolayout.utils.L;
import com.sj.autolayout.utils.ScreenUtils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * Created by zhy on 15/11/18.
 */
public class AutoLayoutConifg
{

    private static AutoLayoutConifg sIntance = new AutoLayoutConifg();


    private static final String KEY_DESIGN_WIDTH = "design_width";
    private static final String KEY_DESIGN_HEIGHT = "design_height";

    private int mScreenWidth;
    private int mScreenHeight;

    private int mDesignWidth;
    private int mDesignHeight;

    private boolean useDeviceSize;


    private AutoLayoutConifg()
    {
    }

    public void checkParams()
    {
        if (mDesignHeight <= 0 || mDesignWidth <= 0)
        {
            throw new RuntimeException(
                    "you must set " + KEY_DESIGN_WIDTH + " and " + KEY_DESIGN_HEIGHT + "  in your manifest file.");
        }
    }

    public AutoLayoutConifg useDeviceSize()
    {
        useDeviceSize = true;
        return this;
    }


    public static AutoLayoutConifg getInstance()
    {
        return sIntance;
    }


    public int getScreenWidth()
    {
        return mScreenWidth;
    }

    public int getScreenHeight()
    {
        return mScreenHeight;
    }

    public int getDesignWidth()
    {
        return mDesignWidth;
    }

    public int getDesignHeight()
    {
        return mDesignHeight;
    }


    public void init(Context context)
    {
        getMetaData(context);

        int[] screenSize = ScreenUtils.getScreenSize(context, useDeviceSize);
        mScreenWidth = screenSize[0];
        mScreenHeight = screenSize[1];
        L.e(" screenWidth =" + mScreenWidth + " ,screenHeight = " + mScreenHeight);
    }

    private void getMetaData(Context context)
    {
//        PackageManager packageManager = context.getPackageManager();
//        ApplicationInfo applicationInfo;
        try
        {
        	mDesignWidth = context.getResources().getDimensionPixelSize(R.dimen.design_width);
        	mDesignHeight = context.getResources().getDimensionPixelSize(R.dimen.design_height);
//            applicationInfo = packageManager.getApplicationInfo(context
//                    .getPackageName(), PackageManager.GET_META_DATA);
//            if (applicationInfo != null && applicationInfo.metaData != null)
//            {
//                mDesignWidth = (Integer) applicationInfo.metaData.get(KEY_DESIGN_WIDTH);
//                mDesignHeight = (Integer) applicationInfo.metaData.get(KEY_DESIGN_HEIGHT);
//            }
        } catch (Exception e)
        {
            throw new RuntimeException(
                    "you must set " + KEY_DESIGN_WIDTH + " and " + KEY_DESIGN_HEIGHT + "  in your dimen file.", e);
        }

        L.e(" designWidth =" + mDesignWidth + " , designHeight = " + mDesignHeight);
    }


}
