package com.liangjing.greendao3.db;

import com.liangjing.greendao3.dao.StudentDao;

/**
 * Created by liangjing on 2017/8/17.
 *
 * function: 获取表 Helper 的工具类
 */

public class DbUtil {

    private static StudentHelper sStudentHelper;

    private static StudentDao getStudentDao(){
        return DbCore.getDaoSession().getStudentDao();
    }

    public static StudentHelper getStudentHelper(){
        if (sStudentHelper == null){
            sStudentHelper = new StudentHelper(getStudentDao());
        }
        return sStudentHelper;
    }
}
