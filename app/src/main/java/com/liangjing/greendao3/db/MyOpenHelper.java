package com.liangjing.greendao3.db;

import android.content.Context;

import com.liangjing.greendao3.dao.DaoMaster;
import com.liangjing.greendao3.dao.StudentDao;
import com.socks.library.KLog;

import org.greenrobot.greendao.database.Database;

/**
 * Created by liangjing on 2017/8/17.
 *
 * function:自定义一个OPenHelper辅助类继承自DaoMaster.OpenHelper,便于数据库升级
 */

public class MyOpenHelper extends DaoMaster.OpenHelper{


    public MyOpenHelper(Context context, String name) {
        super(context, name);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {

        //打印Log
        KLog.w(("db version update from " + oldVersion + " to " + newVersion));

        switch (oldVersion){
            case 1:

                //不能先删除表，否则数据都木了
                //  StudentDao.dropTable(db, true);

                StudentDao.createTable(db, true);

                // 加入新字段 score
                db.execSQL("ALTER TABLE 'STUDENT' ADD 'SCORE' TEXT;");

                break;
        }

    }
}
