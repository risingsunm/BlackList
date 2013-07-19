/* Begin: Modified by sunrise for BlackList 2012/06/01 */

package com.ahong.blackcall;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AhongBlackCallDBHelper extends SQLiteOpenHelper
{
    public AhongBlackCallDBHelper(Context context, String name,
            CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    public AhongBlackCallDBHelper(Context context)
    {
        this(context, AhongBlackCallConst.DATA_BASE_NAME, null,
                AhongBlackCallConst.DATA_BASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        System.out.println("db onCreate");

        String CreateTableBlackNumber = "create table "
                + AhongBlackCallConst.BLACK_NUMBER_TABLE
                + "(id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "number varchar(20) not null," + "name varchar(54) ,"
                /*Begin: Modified by sunrise for AddContactToBlackNumber 2012/06/08*/
                + "name_raw_contact_id varchar(48) default null, "
                /*End: Modified by sunrise for AddContactToBlackNumber 2012/06/08*/
                + "attach varchar(48))";
        db.execSQL(CreateTableBlackNumber);

        String CreateTableBlackRecord = "create table "
                + AhongBlackCallConst.BLACK_RECORD_TABLE
                + "(id Integer primary key autoincrement, "
                + "number varchar(20) not null, " + "name varchar(54), "
                + "datetime varchar(20) not null," + "attach varchar(48))";
        db.execSQL(CreateTableBlackRecord);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        System.out.println("db onUpdate");
    }

}
/* End: Modified by sunrise for BlackList 2012/06/01 */
