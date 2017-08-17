# GreenDao3Test
对GreenDao3的使用进行基本的封装。(便于开发)

# greenDAO3 数据库配置、增删改查、升级

## 配置

* 1、添加依赖

项目的gradle脚本：
```javascript
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:2.2.1'
        classpath 'org.greenrobot:greendao-gradle-plugin:3.2.2'
    }
}
```

module的gradle：
```javascript
apply plugin: 'com.android.application'
apply plugin: 'org.greenrobot.greendao'

android {
    compileSdkVersion 24
    buildToolsVersion "24.0.3"

    defaultConfig {
        applicationId "com.inst.greendao3_demo"
        minSdkVersion 14
        targetSdkVersion 24
        versionCode 1
        versionName "1.0"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}

//greendao配置
greendao {
    schemaVersion 1                             //版本号，升级时可配置
    daoPackage'com.liangjing.greendao3'     //包名
    targetGenDir'src/main/java'                 //生成目录
}

dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'org.greenrobot:greendao:3.2.2'
```

这三个字段的意思是：

```javascript
 schemaVersion： 数据库schema版本，可在这里升级数据库版本 
   daoPackage：设置DaoMaster、DaoSession、Dao包名 
   targetGenDir：设置DaoMaster、DaoSession、Dao目录
```
配置好后，同步下，然后编译，就会生成 Dao。

* 2、添加实体类

这里是Student实体类，和普通的bean有个区别，添加 @Entity 注解
```javascript
@Entity
public class Student {

    @Id
    public Long id;
    public String name;
    public String age;
    public String number;
    public String score;
}
```
注意到，变量 id 添加了 @Id 注解，这个就是主键

## 生成DAO
上面配置好后，同步，编译，即可自动生成DAO, 并自动补全实体类 Student 的getter setter 等方法。

## 增删改查
上面的操作就完成了数据库的创建，下面开始对数据库操作，常用的增删改查。(这里对基本操作进行了封装)

1) 编写一个核心辅助类DbCore, 用于获取DaoMaster和DaoSession
```javascript
public static DaoMaster getDaoMaster() {
    if (daoMaster == null) {
        //此处不可用 DaoMaster.DevOpenHelper, 那是开发辅助类，我们要自定义一个，方便升级
        DaoMaster.OpenHelper helper = new MyOpenHelper(mContext, DB_NAME);
        daoMaster = new DaoMaster(helper.getWritableDatabase());
    }
    return daoMaster;
}
```
这里需要注意的是 getDaoMaster 时的 helper 不可用 DaoMaster.DevOpenHelper，我们需要自定义一个：
因为该类这样提示我们：
```javascript
/** WARNING: Drops all table on Upgrade! Use only during development. */ 
public static class DevOpenHelper extends OpenHelper
```

自定义也很简单：
```javascript
public class MyOpenHelper extends DaoMaster.OpenHelper {
    public MyOpenHelper(Context context, String name) {
        super(context, name);
    }
}
```

2)基础的泛型 BaseDbHelper, 封装基本增删改查方法，具体看代码.
3)实现类，有几个实体类就有几个实现类，这里是 StudentHelper, 仅仅一个构造方法即可
4) 创建一个工具类 DbUtil获取Helper
5)在 application 里初始化

```javascript
public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化数据库
        DbCore.getInstance().init(this);
    }
}
```
6)测试－－MainActivity(对基本的增删改查功能进行测试)

运行程序，添加几个数据，我们看下界面及数据库内容：

![效果图](https://github.com/liangjingdev/GreenDao3Test/raw/master/img/1.png)
![效果图](https://github.com/liangjingdev/GreenDao3Test/raw/master/img/2.png)

## 升级

这里对数据库的升级，仅仅是添加字段，添加表。对于删除，修改字段这里不多讲，因为sqlite数据库不适合此操作：
```javascript
SQLite supports a limited subset of ALTER TABLE. The ALTER TABLE command in SQLite allows the user to rename a table 
or to add a new column to an existing table. It is not possible to rename a column, remove a column, or add or remove 
constraints from a table.
```
简单讲就是 SQlite 数据库仅能重命名表及增加字段，其他不支持，如果一定要操作，也是可以的，看这个链接：
* [SQLite如何删除，修改、重命名列](http://www.2cto.com/database/201110/106835.html)

### 1) 升级版本号

在 gradle 里修改 schemaVersion 即可，现在设置为2，编译下，可以看到 DaoMaster 里的schema变为2：
```javascript
public static final int SCHEMA_VERSION = 2;
```

### 2)实体添加字段

比如给 Student 添加一个 score 字段， 这个可以直接写到 Student 里：
```javascript
public String score;
```
编译后即可生成完整的 Student 实体及 DAO

### 3)重写onUpgrade方法升级

这个就是重写 MyOpenHelper 的 onUpgrade 方法，该方法只在 schema 升级时执行一次.
在该方法里添加 score 字段即可
```javascript
@Override
public void onUpgrade(Database db, int oldVersion, int newVersion) {
    KLog.w("db version update from " + oldVersion + " to " + newVersion);

    switch (oldVersion) {
        case 1:
            //不能先删除表，否则数据都没了
//                StudentDao.dropTable(db, true);
            StudentDao.createTable(db, true);
            // 加入新字段 score
            db.execSQL("ALTER TABLE 'STUDENT' ADD 'SCORE' TEXT;");

            break;
    }

}
```

### 4)测试
运行代码后，再添加三个数据，我们看下界面及数据库内容：

![效果图](https://github.com/liangjingdev/GreenDao3Test/raw/master/img/3.png)
![效果图](https://github.com/liangjingdev/GreenDao3Test/raw/master/img/4.png)
