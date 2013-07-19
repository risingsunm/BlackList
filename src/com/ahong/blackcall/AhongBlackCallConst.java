/* Begin: Modified by sunrise for BlackList 2012/06/01 */
package com.ahong.blackcall;

import android.net.Uri;
import android.provider.BaseColumns;

public class AhongBlackCallConst
{

    public static final String AUTHORITY          = "com.ahong.blackcall.AhongBlackCallProvider";
    public static final String DATA_BASE_NAME     = "AhongBlack.db";
    public static final String BLACK_NUMBER_TABLE = "black_number";
    public static final String BLACK_RECORD_TABLE = "black_record";
    public static final int    DATA_BASE_VERSION  = 1;

    public static final class BlackTable implements BaseColumns
    {
        /*Begin: Modified by sunrise for AddContactToBlackNumber 2012/06/08*/
        public static final String NAME_RAW_CONTACT_ID      = "name_raw_contact_id";
        /*End: Modified by sunrise for AddContactToBlackNumber 2012/06/08*/
        public static final String BLACK_NUMBER             = "number";
        public static final String NUMBER_CONTENT_TYPE      = "vnd.android.cursor.dir/vnd.AhongBlack.black_number";
        public static final String RECORD_CONTENT_TYPE      = "vnd.android.cursor.dir/vnd.AhongBlack.black_record";
        public static final String NULBER_CONTENT_TYPE_ITEM = "vnd.android.cursor.item/vnd.AhongBlack.black_number";
        public static final String RECORD_CONTENT_TYPE_ITEM = "vnd.android.cursor.item/vnd.AhongBlack.black_record";
        public static final String DEFAULT_SORT_ORDER       = "id desc";
        public static final String COLUMN_ID                = "id";

        //called by others applications
        public static final Uri    RECORD_CONTENT_URI       = Uri.parse("content://"
                                                                    + AUTHORITY
                                                                    + "/black_record");
        public static final Uri    NUMBER_CONTENT_URI       = Uri.parse("content://"
                                                                    + AUTHORITY
                                                                    + "/black_number");
    }

    /**
     * inner interface to black recored fragment and black number fragment
     */
    public interface UpdateViewIF
    {
        /**
         * Hint result message for operate database.
         * */
        public void BlackCallToast(int resID);

        /**
         * refresh listView
         * */
        public void UpdateListView();
    }
}
/* End: Modified by sunrise for BlackList 2012/06/01 */
