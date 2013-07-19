/* Begin: Modified by sunrise for BlackList 2012/06/01 */

/**
 * this utility file will provide some function to access DabaBase,
 * SharedPreference. Such as insert,delete,query record base on different
 * conditions. More than one table or file will share this utility file.
 */
package com.ahong.blackcall;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
/*Begin: Modified by sunrise for AddDatabase 2012/06/21*/
import android.content.ContentUris;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
/*End: Modified by sunrise for AddDatabase 2012/06/21*/

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Sunrise.L 2012-5-2
 */
public class AhongBlackCallDBUtils
{
    AhongBlackCallDBHelper dbHelper     = null;
    private static boolean mBlackServer = false;
    private static String  mNoticer     = null;
    private Context        mContext     = null;
    private final String   mLog         = "[AhongBlackCallDBUtils]";

    /*Begin: Modified by sunrise for blackNumber 2012/08/10*/
    private static String mAllNumber = null;
    /*End: Modified by sunrise for blackNumber 2012/08/10*/

    public AhongBlackCallDBUtils(Context c)
    {
        //                SharedPreferences sp = c.getSharedPreferences(
        //                        "com.ahong.blackcall_preferences.xml", Context.MODE_PRIVATE);
        mContext = c;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        mBlackServer = sp.getBoolean("black_filter_server", false);
        mNoticer = sp.getString("call_black_filter_notice", "0");
        /*Begin: Modified by sunrise for blackNumber 2012/08/10*/
        mAllNumber = sp.getString("call_black_filter_mode", "0");
        /*End: Modified by sunrise for blackNumber 2012/08/10*/
        dbHelper = new AhongBlackCallDBHelper(c);
    }

    /**
     * @param number
     *            ,you want to add it to black number table
     * @return nResult,the number of rows affected. Sunrise.L 2012-5-4
     */
    public long AddBlackNumber(String number)
    {
        ContentValues cv = new ContentValues();
        cv.put("number", number);
        cv.put("attach", GetAttach(number));
        cv.put("name", GetName(number));
        if (dbHelper == null)
        {
            //            dbHelper = new AhongBlackCallDBUtils();
        }
        long nResult = dbHelper.getWritableDatabase().insert(
                AhongBlackCallConst.BLACK_NUMBER_TABLE, null, cv);
        //test:get name from contact
        //AddBlackRecord(number);
        return nResult;
    }

    /* Begin: Modified by sunrise for AddContactToBlackNumber 2012/06/08 */
    public long AddBlackNumber(ContentValues value)
    {
        if (dbHelper == null)
        {
            //            dbHelper = new AhongBlackCallDBUtils();
        }
        long nResult = dbHelper.getWritableDatabase().insert(
                AhongBlackCallConst.BLACK_NUMBER_TABLE, null, value);
        return nResult;
    }
    /* End: Modified by sunrise for AddContactToBlackNumber 2012/06/08 */

    /**
     * @param number
     *            , incoming number of black record table
     * @param date
     *            , incoming date of black record table
     * @return long, the number of rows affected. Sunrise.L 2012-5-4
     */
    public long AddBlackRecord(String number)
    {
        ContentValues cv = new ContentValues();
        cv.put("number", number);

        Date dateTag = new Date(System.currentTimeMillis());
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd HH:MM");
        String DateTime = new String(dateformat.format(dateTag));
        cv.put("datetime", DateTime);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(AhongBlackCallConst.BLACK_NUMBER_TABLE,
                new String[] { "name", "attach" }, "number = ?",
                new String[] { number }, null, null, null, null);

        if (c.moveToFirst())
        {
            cv.put("attach", c.getString(c.getColumnIndex("attach")));
            cv.put("name", c.getString(c.getColumnIndex("name")));
        }
        else
        {
            cv.put("attach",
            //"UNknow");
                    mContext.getResources().getString(R.string.unknow_attach));
        }

        db.close();
        c.close();

        long nResult = dbHelper.getWritableDatabase().insert(
                AhongBlackCallConst.BLACK_RECORD_TABLE, null, cv);
        return nResult;
    }

    public void CloseDataBase()
    {
        dbHelper.close();
    }

    /**
     * @param strTable
     *            clear the table passed in.
     */
    public void DelAllItems(String strTable)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try
        {
            db.execSQL("delete from " + strTable);
        }
        catch (Exception e)
        {
            Log.e(mLog, " delete from " + strTable);
        }
        finally
        {
            db.close();
        }

    }

    /**
     * @param strTable
     * @param strID
     * @return nResult,the number of rows affected Sunrise.L 2012-5-4
     */
    public int DelItem(String strTable, String strIdSet)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //        int nResult = db.delete(strTable, "id = ?", new String[] { strID });
        int nResult = db.delete(strTable, "id in (" + strIdSet + ")", null);
        db.close();
        return nResult;
    }

    /**
     * @param table
     * @return ArrayList<HashMap<String,String>> Sunrise.L 2012-5-4
     */
    public ArrayList<HashMap<String, String>> GetAllBlackNumber(String table)
    {
        ArrayList<HashMap<String, String>> ListData = new ArrayList<HashMap<String, String>>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(table, null, null, null, null, null, null);

        String name = null;
        while (c.moveToNext())
        {
            HashMap<String, String> item = new HashMap<String, String>();
            item.put("id", c.getString(c.getColumnIndex("id")));

            name = c.getString(c.getColumnIndex("name"));
            if (name != null)
            {
                //                item.put("NameNumber", name + "[" + c.getString(c.getColumnIndex("number")) + "]");
                item.put("NameNumber", c.getString(c.getColumnIndex("number")));
            }
            else
            {
                item.put("NameNumber", c.getString(c.getColumnIndex("number")));
            }

            item.put("TypeAttach", c.getString(c.getColumnIndex("attach")));
            ListData.add(item);
        }

        c.close();
        db.close();
        return ListData;
    }

    /**
     * @param table
     * @return ArrayList<HashMap<String,String>> Sunrise.L 2012-5-4
     */
    public ArrayList<HashMap<String, String>> GetAllRecord(String table)
    {
        ArrayList<HashMap<String, String>> ListData = new ArrayList<HashMap<String, String>>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(table, null, null, null, null, null, null);

        while (c.moveToNext())
        {
            HashMap<String, String> item = new HashMap<String, String>();
            item.put("id", c.getString(c.getColumnIndex("id")));
            item.put("number", c.getString(c.getColumnIndex("number")));
            item.put("name", c.getString(c.getColumnIndex("name")));
            item.put("datetime", c.getString(c.getColumnIndex("datetime")));
            item.put("attach", c.getString(c.getColumnIndex("attach")));
            ListData.add(item);
        }

        c.close();
        db.close();
        return ListData;
    }

    /**
     * @param mContext
     * @param number
     * @return sunrise.l String 2012-5-28
     */
    public static String getNameFormNumber(Context mContext, String number)
    {
        String name = null;

        Uri uri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI, number);
        Cursor cur = mContext.getContentResolver().query(uri,
                new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME },
                null, null, null);
        if (cur != null && cur.moveToFirst())
        {
            int nameIndex = cur
                    .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
            name = cur.getString(nameIndex);
        }
        cur.close();
        return name;
    }

    /**
     * @param context
     * @param number
     * @return sunrise.l String 2012-5-28
     */
    public static String GetAttach(Context context, String number)
    {
        String result = null;
        Uri uri = Uri
                .parse("content://com.khong.provider.phonemanager/address/"
                        + number);
        Cursor cursor = context.getContentResolver().query(uri, null, null,
                null, null);
        if (cursor != null && cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            String city = cursor.getString(0);
            String province = cursor.getString(1);
            if (province.equals(city))
            {
                result = province;
            }
            else
            {
                result = province + city;
            }
        }
        else
        {
            result = context.getResources().getString(R.string.unknow_attach);
        }
        if (cursor != null)
        {
            cursor.close();
        }

        return result;

    }

    /**
     * @param number
     * @return String,attach area.
     *         get attach area from number pass in.
     */
    public String GetAttach(String number)
    {
        String result = null;
        Uri uri = Uri
                .parse("content://com.khong.provider.phonemanager/address/"
                        + number);
        Cursor cursor = mContext.getContentResolver().query(uri, null, null,
                null, null);
        if (cursor != null && cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            String city = cursor.getString(0);
            String province = cursor.getString(1);
            if (province.equals(city))
            {
                result = province;
            }
            else
            {
                result = province + city;
            }
        }
        else
        {
            result = mContext.getResources().getString(R.string.unknow_attach);
        }
        if (cursor != null)
        {
            cursor.close();
        }

        return result;
    }

    /**
     * @param number
     * @return name
     *         get name of the give number if this name-number store in contacts
     *         database.
     */
    public String GetName(String number)
    {
        String name = null;

        Uri uri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI, number);
        Cursor cur = mContext.getContentResolver().query(uri,
                new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME },
                null, null, null);
        if (cur != null && cur.moveToFirst())
        {
            int nameIndex = cur
                    .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
            name = cur.getString(nameIndex);
        }
        cur.close();
        return name;
    }

    /**
     * @param strNumber
     * @return boolean
     *         check out the given number is a blackNumber or not. return True
     *         if exist in black_number table,others false. Please
     *         CareFully,External Application want to judge
     */
    public boolean isBlackNumber(String strNumber)
    {
        if (!isBlackServerOn())
        {
            return false;
        }
        else
        {
            /*Begin: Modified by sunrise for blackNumber 2012/08/10*/
            if (isAllNumberOn())
            {
                return true;
            }
            else
            {
                return isNumberExist(strNumber);
            }
            /*End: Modified by sunrise for blackNumber 2012/08/10*/
        }
    }

    /*Begin: Modified by sunrise for blackNumber 2012/08/10*/
    public static boolean isAllNumberOn()
    {
        System.out.println("mAllNumber:" + mAllNumber);
        if (mAllNumber.equals("1"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    /*End: Modified by sunrise for blackNumber 2012/08/10*/

    /**
     * this function used to judge BlackServer is on or off.if checkBox is
     * selected,this value is true, others is false.
     * @return boolean
     */
    /* Begin: Modified by sunrise for DataBaseBootInit 2012/07/04 */
    public static boolean isBlackServerOn()
    /* End: Modified by sunrise for DataBaseBootInit 2012/07/04 */
    {
        System.out.println("mBlackServer:" + mBlackServer);
        return mBlackServer;
    }

    /**
     * Whether send notify to user while other applications filter incoming call
     * base on this return value.Please keep silence if return false.
     * @return boolean
     */
    /* Begin: Modified by sunrise for DataBaseBootInit 2012/07/04 */
    public static boolean isNoticeToInfoBar()
    /* End: Modified by sunrise for DataBaseBootInit 2012/07/04 */
    {
        if (mNoticer.equals("1"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * @param strNumber
     * @return boolean query the given number is exist or not in black number
     *         table.
     */
    public boolean isNumberExist(String strNumber)
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(AhongBlackCallConst.BLACK_NUMBER_TABLE,
                /*Begin: Modified by sunrise for AddBlackNumberCrash 2012/06/04*/
                //new String[] { "number" }, "number = " + strNumber, null, null,
              new String[] { "number" }, "number = ?", new String[]{strNumber}, null,
               /*End: Modified by sunrise for AddBlackNumberCrash 2012/06/04*/
                null, null);

        if (c.moveToNext())
        {
            c.close();
            db.close();
            return true;
        }
        else
        {
            c.close();
            db.close();
            return false;
        }

    }

    /**
     * @param strTable
     * @param values
     * @param whereClause
     * @param whereArgs
     * @return nResult,the number of rows affected Sunrise.L 2012-5-4
     */
    public int UpdateItem(String strTable, ContentValues values,
            String whereClause, String[] whereArgs)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int nResult = db.update(strTable, values, whereClause, whereArgs);
        db.close();
        return nResult;
    }
    /* Begin: Modified by sunrise for AddDatabase 2012/06/21 */
    public static void divideFile() throws IOException
    {
        String path = "F:/ar/";
        String base = "phoneAttribution";
        String ext = ".db";

        int split = 800 * 1024;
        byte[] buf = new byte[1024];
        int num = 1;

        File inFile = new File(path + base + ext);
        FileInputStream fis = new FileInputStream(inFile);
        while (true)
        {
            FileOutputStream fos = new FileOutputStream(new File(path + base
                    + num + ext));
            for (int i = 0; i < split / buf.length; i++)
            {
                int read = fis.read(buf);
                fos.write(buf, 0, buf.length);
                if (read < buf.length)
                {
                    fis.close();
                    fos.close();
                    return;
                }
            }
            fos.close();
            num++;
        }
    }

    public static void mergeFile(Context c) throws IOException
    {
        final String dst = "/data/data/com.ahong.blackcall/databases/phoneAttribution.db";
        final String part1 = "phoneAttribution1.db";
        final String part2 = "phoneAttribution2.db";
        String part[] = new String[] { part1, part2 };

        System.out.println("mergeFile");
        File file = new File(dst);
        if (!file.exists())
        {
            System.out.println("init... " + dst);
            OutputStream out = new FileOutputStream(dst);
            System.out.println(out.toString());
            byte[] buffer = new byte[1024];
            InputStream in;
            int readLen = 0;
            for (int i = 0; i < part.length; i++)
            {
                in = c.getResources().getAssets().open(part[i]);
                while ((readLen = in.read(buffer)) != -1)
                {
                    out.write(buffer, 0, readLen);
                }
                out.flush();
                in.close();
            }
            out.close();
        }

        /* Begin: Modified by sunrise for DataBaseBootInit 2012/07/04 */
        Process p;
        int status = 0;
        p = Runtime.getRuntime().exec("chmod 777 " + dst);
        try
        {
            status = p.waitFor();
            System.out.println("--=chmod database 777 is: " + status);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        /* End: Modified by sunrise for DataBaseBootInit 2012/07/04 */

    }
    /* End: Modified by sunrise for AddDatabase 2012/06/21 */
}
/* End: Modified by sunrise for BlackList 2012/06/01 */
