package com.ahong.blackcall;

/* Begin: Modified by sunrise for DataBaseBootInit 2012/07/23 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;

public class AhongBootCompleteReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        // TODO Auto-generated method stub
        System.out.println("AhongBootCompleteReceiver onReceive");
        AhongBlackCallDBHelper DBhelper = new AhongBlackCallDBHelper(context);
        SQLiteDatabase db = DBhelper.getReadableDatabase();
        db.close();

        try
        {
            AhongBlackCallDBUtils.mergeFile(context);
        }
        catch (IOException e)
        {
            System.out.println("------phoneAttribution.db init ERROR!------");
            e.printStackTrace();
        }

        if (AhongBlackCallDBUtils.isBlackServerOn())
        {
            context.startService(new Intent(context,
                    AhongBlackCallService.class));
            System.out.println("------startService------");
        }
    }

}
/* End: Modified by sunrise for DataBaseBootInit 2012/07/23 */
