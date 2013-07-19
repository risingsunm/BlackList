/* Begin: Modified by sunrise for BlackList 2012/06/01 */

/**
 * this file provide interface for other applications to access black database
 * via content provider.
 */
package com.ahong.blackcall;

/**
 * This file is about to provide interface to access Black databases when
 * another application want to add black number or black record to it.
 * */
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;

import java.security.AccessControlException;
import java.util.HashMap;

/**
 * @author Sunrise.L 2012-5-2
 */
public class AhongBlackCallProvider extends ContentProvider
{
    public static final int               BLACK_NUMBER      = 1;
    public static final int               BLACK_RECORD      = 2;
    public static final int               BLACK_NUMBER_ITEM = 3;
    /* Begin: Modified by sunrise for AddContactToBlackNumber 2012/06/08 */
    public static final int               CONTACT_ID          = 4;
    public static final String            NAME_RAW_CONTACT_ID = "name_raw_contact_id";
    /* End: Modified by sunrise for AddContactToBlackNumber 2012/06/08 */
    public static final String            TABLE_ERROR       = "TABLE_ERROR";
    public static UriMatcher              mUriMatcher       = null;
    public static HashMap<String, String> mProjectionMap    = null;
    private AhongBlackCallDBUtils         dbu               = null;
    private Context                       mContext          = null;
    static
    {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(AhongBlackCallConst.AUTHORITY, "black_number",
                BLACK_NUMBER);
        mUriMatcher.addURI(AhongBlackCallConst.AUTHORITY, "black_number/#",
                BLACK_NUMBER_ITEM);
        mUriMatcher.addURI(AhongBlackCallConst.AUTHORITY, "black_record",
                BLACK_RECORD);
        /*Begin: Modified by sunrise for AddContactToBlackNumber 2012/06/08*/
        mUriMatcher.addURI(AhongBlackCallConst.AUTHORITY, "black_number/contactid/#",
                CONTACT_ID);
        /*End: Modified by sunrise for AddContactToBlackNumber 2012/06/08*/

        mProjectionMap = new HashMap<String, String>();
        mProjectionMap.put(AhongBlackCallConst.BlackTable.BLACK_NUMBER,
                AhongBlackCallConst.BlackTable.BLACK_NUMBER);
        /*Begin: Modified by sunrise for AddContactToBlackNumber 2012/06/08*/
        mProjectionMap.put(AhongBlackCallConst.BlackTable.COLUMN_ID,
                AhongBlackCallConst.BlackTable.COLUMN_ID);
        /*End: Modified by sunrise for AddContactToBlackNumber 2012/06/08*/

    }

    @Override
    public boolean onCreate()
    {
        System.out.println("black provicer onCreate");
        setmContext(getContext());
        if (dbu == null)
        {
            dbu = new AhongBlackCallDBUtils(getmContext());
        }
        return false;
    }

    @Override
    public String getType(Uri uri)
    {
        switch (mUriMatcher.match(uri))
        {
            case BLACK_NUMBER:
            {
                return AhongBlackCallConst.BLACK_NUMBER_TABLE;
            }
            case BLACK_RECORD:
            {
                return AhongBlackCallConst.BLACK_RECORD_TABLE;
            }
            case BLACK_NUMBER_ITEM:
            {
                return TABLE_ERROR;
            }
            /*Begin: Modified by sunrise for AddContactToBlackNumber 2012/06/08*/
            case CONTACT_ID:
            {
                return NAME_RAW_CONTACT_ID;
            }
            default:
                return TABLE_ERROR;
               // throw new IllegalArgumentException("Unknows URI:" + uri);
            /*End: Modified by sunrise for AddContactToBlackNumber 2012/06/08*/
        }

    }

    @Override
    /*Begin: Modified by sunrise for AddContactToBlackNumber 2012/06/08*/
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        String strTable = getType(uri);
        int result = 0;
        if (dbu == null)
        {
            dbu = new AhongBlackCallDBUtils(getContext());
        }
        if (!strTable.equals(TABLE_ERROR))
        {
            result = dbu.dbHelper.getWritableDatabase().delete(strTable,
                    selection, selectionArgs);
        }

        return result;
    }
    /*End: Modified by sunrise for AddContactToBlackNumber 2012/06/08*/

    @Override
    /**
     * URI of insert to black number:
     *      content://com.ahong.blackcall.AhongBlackCallProvider/black_number
     * URI of insert to black record:
     *      content://com.ahong.blackcall.AhongBlackCallProvider/black_record
     * values should include number.
     */
    public Uri insert(Uri uri, ContentValues values)
    {
        String number;
        if (values.get("number") == null)
        {
            throw new IllegalArgumentException("number is null");
        }
        else if (values.get("number").toString().isEmpty())
        {
            throw new IllegalArgumentException("number is empty");
        }
        else
        {
            number = values.get("number").toString();
        }

        if (dbu == null)
        {
            dbu = new AhongBlackCallDBUtils(getContext());
        }

        long rowID = 0;
        Uri insertUserUri = null;
        String strTable = getType(uri);
        if (strTable.equals(AhongBlackCallConst.BLACK_NUMBER_TABLE))
        {
            rowID = dbu.AddBlackNumber(number);
            insertUserUri = ContentUris.withAppendedId(
                    AhongBlackCallConst.BlackTable.NUMBER_CONTENT_URI, rowID);
        }
        else if (strTable.equals(AhongBlackCallConst.BLACK_RECORD_TABLE))
        {
            rowID = dbu.AddBlackRecord(number);
            insertUserUri = ContentUris.withAppendedId(
                    AhongBlackCallConst.BlackTable.RECORD_CONTENT_URI, rowID);
        }
        /*Begin: Modified by sunrise for AddContactToBlackNumber 2012/06/08*/
        else if (strTable.equals(AhongBlackCallConst.BlackTable.NAME_RAW_CONTACT_ID))
        {
            values.put("attach", dbu.GetAttach(number));
            values.put("name", dbu.GetName(number));
            System.out.println("provider insert :" + values.toString());
            //insertUserUri = dbu.dbHelper.getWritableDatabase().insert(AhongBlackCallConst.BLACK_NUMBER_TABLE, null, values);
            rowID = dbu.AddBlackNumber(values);
            insertUserUri = ContentUris.withAppendedId(
                    AhongBlackCallConst.BlackTable.NUMBER_CONTENT_URI, rowID);

        }
        /*End: Modified by sunrise for AddContactToBlackNumber 2012/06/08*/
        else
        {
            throw new AccessControlException("table access error--->strTable:"
                    + strTable);
        }

        //another methods to insert database
        /*
         * ContentValues item = new ContentValues(); SQLiteDatabase db =
         * dbu.dbHelper.getWritableDatabase(); db.insert(table, nullColumnHack,
         * values);
         */

        getContext().getContentResolver().notifyChange(insertUserUri, null);
        return insertUserUri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder)
    {
        System.out.println("query " + uri.toString());
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (mUriMatcher.match(uri))
        {
            case BLACK_NUMBER_ITEM:
            {
                qb.setTables(AhongBlackCallConst.BLACK_NUMBER_TABLE);
                qb.setProjectionMap(mProjectionMap);
                qb.appendWhere(AhongBlackCallConst.BlackTable.BLACK_NUMBER
                        + "=" + uri.getPathSegments().get(1));
                break;
            }
            /*Begin: Modified by sunrise for AddContactToBlackNumber 2012/06/08*/
            case CONTACT_ID:
            {
                qb.setTables(AhongBlackCallConst.BLACK_NUMBER_TABLE);
                qb.setProjectionMap(mProjectionMap);
                qb.appendWhere(AhongBlackCallConst.BlackTable.NAME_RAW_CONTACT_ID
                        + "=" + uri.getPathSegments().get(2));
                break;
            }
            default:
                break;
            /*End: Modified by sunrise for AddContactToBlackNumber 2012/06/08*/
        }

        String orderBy = null;
        if (TextUtils.isEmpty(sortOrder))
        {
            orderBy = AhongBlackCallConst.BlackTable.DEFAULT_SORT_ORDER;
        }
        else
        {
            orderBy = sortOrder;
        }

        if (dbu == null)
        {
            dbu = new AhongBlackCallDBUtils(getContext());
        }

        SQLiteDatabase db = dbu.dbHelper.getWritableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null,
                null, orderBy);
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs)
    {
        return 0;
    }

    /**
     * @return the mContext
     */
    public Context getmContext()
    {
        return mContext;
    }

    /**
     * @param mContext
     *            the mContext to set
     */
    public void setmContext(Context mContext)
    {
        this.mContext = mContext;
    }

    private class observer extends ContentObserver
    {

        /**
         * @param handler
         */
        public observer(Handler handler)
        {
            super(handler);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onChange(boolean selfChange)
        {
            // TODO Auto-generated method stub
            super.onChange(selfChange);
        }

    }

}
/* End: Modified by sunrise for BlackList 2012/06/01 */
