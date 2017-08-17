package com.liangjing.greendao3.db;

import android.content.Context;

import com.liangjing.greendao3.dao.DaoMaster;
import com.liangjing.greendao3.dao.DaoSession;

import org.greenrobot.greendao.query.QueryBuilder;

/**
 * Created by liangjing on 2017/8/17.
 * <p>
 * function:核心辅助类DbCore, 用于获取DaoMaster和DaoSession
 */

public class DbCore {

    private static final String DEFAULT_DB_NAME = "green-dao3.db"; //默认数据库名称
    private static DaoMaster mDaoMaster;
    private static DaoSession mDaoSession;
    private static Context mContext;
    private static String DB_NAME; //数据库名称
    private static volatile DbCore sDbCore = new DbCore();  //采用单例模式创建该对象

    private DbCore() {
    }

    public static DbCore getInstance() {
        if (sDbCore == null) {
            synchronized (DbCore.class) {
                if (sDbCore == null) {
                    sDbCore = new DbCore();
                }
            }
        }
        return sDbCore;
    }

    public static void init(Context context) {
        init(context,DEFAULT_DB_NAME);
    }

    /**
     * function:初始化
     * @param context
     * @param dbName
     */
    public static void init(Context context, String dbName) {
        if (context == null) {
            throw new IllegalArgumentException("context can't be null");
        }
        mContext = context.getApplicationContext();
        DB_NAME = dbName;
    }


    /**
     * function:获取DaoMaster对象
     * @return
     */
    public static DaoMaster getDaoMaster(){
        if (mDaoMaster ==null){
            //此处不可用 DaoMaster.DevOpenHelper, 那是开发辅助类，我们要自定义一个，方便升级
            DaoMaster.OpenHelper helper = new MyOpenHelper(mContext,DB_NAME);
            mDaoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return mDaoMaster;
    }


    /**
     * function:获取DaoSession对象
     * @return
     */
    public static DaoSession getDaoSession(){
        if (mDaoSession == null){
            if (mDaoMaster == null){
                mDaoMaster = getDaoMaster();
            }
            mDaoSession = mDaoMaster.newSession();
        }
        return mDaoSession;
    }

    public static void enableQueryBuilderLog(){
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
    }

}
