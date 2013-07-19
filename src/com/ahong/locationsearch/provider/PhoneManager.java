package com.ahong.locationsearch.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/* Begin: Modified by xiepengfei for PhoneManager 2012/04/9 */
public class PhoneManager
{

    //contentprovider  uri
    public static final String AUTHORITY = "com.khong.provider.phonemanager";

    private PhoneManager()
    {
    }

    public static final class PhoneManagerNotes implements BaseColumns
    {
        private PhoneManagerNotes()
        {
        }

        public static final Uri    CONTENT_URI       = Uri.parse("content://"
                                                             + AUTHORITY
                                                             + "/address");

        public static final String CONTENT_TYPE      = "vnd.android.cursor.dir/vnd.khong.phonemanager";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.khong.phonemanager";

        public static final String ADDRESS           = "address";
    }
}
/* End: Modified by xiepengfei for PhoneManager 2012/04/9 */
