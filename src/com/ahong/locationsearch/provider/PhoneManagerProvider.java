package com.ahong.locationsearch.provider;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Pattern;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/* Begin: Modified by xiepengfei for share phone number address 2012/04/9 */
/**
 * @author xiepengfei 2012.4.9 for share phone number address
 */
public class PhoneManagerProvider extends ContentProvider
{

    private final static String            TAG             = "PhoneManagerProvider";
    private Context                        mContext;

    private static HashMap<String, String> mProjectMap;
    private static final UriMatcher        mUriMatcher;
    private SQLiteDatabase                 db, db_wr;

    public static final String             ADDRESS_DB_NAME = "phoneAttribution.db";

    //private static final int DEFAULT_ADDRESS = 1;
    /**
     * phone number address
     */
    private static final int               ADDRESS         = 2;
    /**
     * phone number type
     */
    private static final int               TYPE            = 3;
    /**
     * phone number area code
     */
    private static final int               AREACODE        = 4;

    private static final int               LACATION        = 5;

    static
    {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        //mUriMatcher.addURI(PhoneManager.AUTHORITY, "addressandcode/#", DEFAULT_ADDRESS);
        mUriMatcher.addURI(PhoneManager.AUTHORITY, "address/#", ADDRESS);
        mUriMatcher.addURI(PhoneManager.AUTHORITY, "type/#", TYPE);
        mUriMatcher.addURI(PhoneManager.AUTHORITY, "areacode/#", AREACODE);
        mUriMatcher.addURI(PhoneManager.AUTHORITY, "location/#", LACATION);

        mProjectMap = new HashMap<String, String>();

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        // TODO Auto-generated method stub
        int count;
        String _id = uri.getPathSegments().get(1);
        count = db_wr.delete("call_location", "_id "
                + "in ("
                + _id
                + ")"
                + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')'
                        : ""), selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri)
    {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        long rowId;
        /* Begin: Modified by sunrise for DataBaseBootInit 2012/07/04 */
        if (db_wr == null)
        {
            return null;
        }
        /* End: Modified by sunrise for DataBaseBootInit 2012/07/04 */

        rowId = db_wr.insert("call_location", null, values);
        if (rowId > 0)
        {
            Uri noteUri = ContentUris.withAppendedId(
                    PhoneManager.PhoneManagerNotes.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }
        return null;
    }

    public boolean onCreate()
    {
        mContext = getContext();
        File file = mContext.getDatabasePath(ADDRESS_DB_NAME);
        System.out.println("xxx " + file.getAbsolutePath());
        /* Begin: Modified by xiepengfei for the db file inexistence 2012/05/10 */
        //        db = SQLiteDatabase.openDatabase(file.getAbsolutePath(),
        //                null, SQLiteDatabase.OPEN_READONLY);
        //        db_wr = SQLiteDatabase.openDatabase(file.getAbsolutePath(),
        //                null, SQLiteDatabase.OPEN_READWRITE);
        if (!file.exists())
            return false;
        try
        {
            db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null,
                    SQLiteDatabase.OPEN_READONLY);
            db_wr = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null,
                    SQLiteDatabase.OPEN_READWRITE);
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
            return false;
        }
        /* End: Modified by xiepengfei for the db file inexistence 2012/05/10 */
        return false;
    }

    public String queryAddress(String incomingNumber)
    {
        return incomingNumber;
    }

    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder)
    {
        Cursor mResult = null;
        Log.d(TAG, "----" + uri.getPathSegments().size() + "----");
        if (uri.getPathSegments().size() == 2)
        {
            /* Begin: Modified by sunrise for AttachQuery 2012/05/29 */
            String number = NumberCheck(uri.getPathSegments().get(1));
            Uri modUri = null;
            //            System.out.println("sunrise:" + number);
            //            log(uri.toString());
            if (number == null)
            {
                return null;
            }
            else
            {
                int nEnd = uri.toString().lastIndexOf(
                        uri.getPathSegments().get(1)) - 1;
                modUri = Uri.parse(uri.toString().substring(0, nEnd) + "/"
                        + number);
            }
            //            log(modUri.toString());
            /* End: Modified by sunrise for AttachQuery 2012/05/29 */

            //            switch (mUriMatcher.match(uri))
            switch (mUriMatcher.match(modUri))
            {
                case ADDRESS:
                    if (db != null && db.isOpen())
                    {
                        if (Pattern.compile("^1[3458]\\d{5,9}$")
                                .matcher(number).matches())
                        {// mobile telephone   
                            String subNumber7 = number.substring(0, 7);
                            mResult = db
                                    .rawQuery(
                                            "SELECT C.name as city, P.name as province "
                                                    + "FROM attribute as A "
                                                    + "INNER JOIN city as C ON A.city_id=C._id "
                                                    + "INNER JOIN province as P ON C.province_id=P._id "
                                                    + "WHERE A.number<=? AND A.number>=?+1-A.count",
                                            new String[] { subNumber7,
                                                    subNumber7 });

                        }
                        else if (number.length() > 3)
                        {
                            // fixed telephone
                            String subNumber4 = number.substring(0, 4);
                            String subNumber3 = number.substring(0, 3);
                            mResult = db
                                    .rawQuery(
                                            "SELECT C.name as city, P.name as province "
                                                    + "FROM city as C "
                                                    + "INNER JOIN province as P ON C.province_id=P._id "
                                                    + "WHERE C.code in(?,?) "
                                                    + "ORDER BY C._id ASC LIMIT 1 ",
                                            new String[] { subNumber4,
                                                    subNumber3 });

                        }
                        else if (number.length() == 3)
                        {
                            String subNumber3 = number.substring(0, 3);
                            mResult = db
                                    .rawQuery(
                                            "SELECT C.name as city, P.name as province "
                                                    + "FROM city as C "
                                                    + "INNER JOIN province as P ON C.province_id=P._id "
                                                    + "WHERE C.code = ? "
                                                    + "ORDER BY C._id ASC LIMIT 1 ",
                                            new String[] { subNumber3 });

                        }
                        else
                        {
                            mResult = null;
                        }

                    }
                    //db.close();
                    break;
                case TYPE:
                    if (db != null && db.isOpen())
                    {
                        if (Pattern.compile("^1[3458]\\d{5,9}$")
                                .matcher(number).matches())
                        {
                            String subNumber7 = number.substring(0, 7);
                            mResult = db
                                    .rawQuery(
                                            "SELECT C.name as type "
                                                    + "FROM attribute as A "
                                                    + "INNER JOIN cardType as C ON A.card_id=C._id "
                                                    + "WHERE A.number<=? AND A.number>=?+1-A.count",
                                            new String[] { subNumber7,
                                                    subNumber7 });
                        }
                        else
                        {
                            mResult = null;
                        }

                    }
                    //db.close();
                    break;
                case AREACODE:
                    if (db != null && db.isOpen())
                    {
                        if (Pattern.compile("^1[3458]\\d{5,9}$")
                                .matcher(number).matches())
                        {
                            String subNumber7 = number.substring(0, 7);
                            mResult = db
                                    .rawQuery(
                                            "SELECT C.code "
                                                    + "FROM city as C "
                                                    + "INNER JOIN attribute as A ON A.city_id = C._id "
                                                    + "WHERE A.number<=? AND A.number>=?+1-A.count "
                                                    + "ORDER BY C._id ASC LIMIT 1 ",
                                            new String[] { subNumber7,
                                                    subNumber7 });
                        }
                        else
                        {
                            if (number.length() >= 4)
                            {
                                String subNumber4 = number.substring(0, 4);
                                String subNumber3 = number.substring(0, 3);
                                mResult = db
                                        .rawQuery(
                                                "SELECT C.code as city "
                                                        + "FROM city as C "
                                                        + "WHERE C.code in(?,?) "
                                                        + "ORDER BY C._id ASC LIMIT 1 ",
                                                new String[] { subNumber4,
                                                        subNumber3 });

                            }
                            else if (number.length() == 3)
                            {
                                String subNumber3 = number.substring(0, 3);
                                mResult = db.rawQuery("SELECT C.code as city "
                                        + "FROM city as C "
                                        + "WHERE C.code = ? "
                                        + "ORDER BY C._id ASC LIMIT 1 ",
                                        new String[] { subNumber3 });
                            }
                            else
                            {
                                mResult = null;
                            }
                        }
                    }
                    //db.close();
                    break;
                case LACATION:
                    if (db == null)
                        return null;
                    {
                        mResult = db.rawQuery("SELECT * "
                                + "FROM call_location " + "WHERE number = ? ",
                                new String[] { number });
                        //Log.d(TAG,"-----mResult.getCount()="+mResult.getCount()+"-number="+number+"---");
                        break;
                    }
                default:
                {
                    throw new IllegalArgumentException("Unknown URI " + uri);
                }
            }
        }
        else if (uri.getPathSegments().size() == 1)
        {

            try
            {
                mResult = db.rawQuery("SELECT * " + "FROM call_location", null);
            }
            catch (Exception e)
            {
                System.out.println(e);
                return null;
            }

        }
        else
        {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        return mResult;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /* Begin: Modified by sunrise for AttachQuery 2012/05/29 */
    /**
     * to obtain correct number, we should remove whitespace, "-", some net
     * prefix as "+86" etc, if included. And it must be not short than 3.
     * */
    private String NumberCheck(String number)
    {
        String NumberModified = null;
        log(number);
        if (number != null)
        {
            NumberModified = number.trim().replace(" ", "").replace("-", "");
            if (NumberModified.length() < 3)
            {
                return null;
            }

            if (NumberModified.startsWith("+86"))
            {
                NumberModified = NumberModified.substring(3);
            }

            if (!NumberModified.matches("^\\d{3,}$"))
            {
                return null;
            }

            if (NumberModified.startsWith("17951"))
            {
                NumberModified = NumberModified.substring(5);
            }
            if (NumberModified.startsWith("12593"))
            {
                NumberModified = NumberModified.substring(5);
            }
            if (NumberModified.startsWith("12530"))
            {
                NumberModified = NumberModified.substring(5);
            }
            if (NumberModified.startsWith("106"))//17951
            {
                NumberModified = NumberModified.substring(3);
            }

            if (!NumberModified.matches("^\\d{3,}$"))
            {
                return null;
            }
        }
        else
        {
            NumberModified = null;
        }
        return NumberModified;
    }

    /* End: Modified by sunrise for AttachQuery 2012/05/29 */
    private void log(String s)
    {
        Log.v(TAG, s);
    }
}

/* End: Modified by xiepengfei for share phone number address 2012/04/9 */
