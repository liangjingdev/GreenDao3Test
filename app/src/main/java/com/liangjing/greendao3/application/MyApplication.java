package com.liangjing.greendao3.application;

import android.app.Application;

import com.liangjing.greendao3.BuildConfig;
import com.liangjing.greendao3.db.DbCore;
import com.socks.library.KLog;

/**
 * Created by liangjing on 2017/8/17.
 *
 * function:自定义Application
 */

public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化数据库
        DbCore.getInstance().init(this);
        //开启调试log
        DbCore.enableQueryBuilderLog();

        //log管理
        KLog.init(BuildConfig.DEBUG);
    }

}
