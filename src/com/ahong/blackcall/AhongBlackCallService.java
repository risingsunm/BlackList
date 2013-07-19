/* Begin: Modified by sunrise for BlackList 2012/06/01 */
package com.ahong.blackcall;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

public class AhongBlackCallService extends Service
{
    private TelephonyManager    TelMgr;
    private Context             mContext = null;
    private final static String mTag     = "AhongBlackCallService";

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        mContext = getApplicationContext();
        CallBlackListener cbListener = new CallBlackListener();
        TelMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        TelMgr.listen(cbListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private static ITelephony getITelephony(Context context)
    {
        TelephonyManager mTelephonyManager = (TelephonyManager) context
                .getSystemService(TELEPHONY_SERVICE);
        Class<TelephonyManager> c = TelephonyManager.class;
        Method getITelephonyMethod = null;
        try
        {
            //get declare method.
            getITelephonyMethod = c.getDeclaredMethod("getITelephony",
                    (Class[]) null);
            getITelephonyMethod.setAccessible(true);
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e)
        {
            Log.e(mTag, "NoSuchMethodException");
            e.printStackTrace();
        }
        catch (NullPointerException e)
        {
            Log.e(mTag, "NullPointerException");
        }

        ITelephony iTelephony = null;
        try
        {
            // get instance
            iTelephony = (ITelephony) getITelephonyMethod.invoke(
                    mTelephonyManager, (Object[]) null);
        }
        catch (Exception e)
        {
            Log.e(mTag, "get iTelephony error!");
            e.printStackTrace();
        }

        return iTelephony;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    private class CallBlackListener extends PhoneStateListener
    {
        private AhongBlackCallDBUtils dbu = new AhongBlackCallDBUtils(
                                                  getApplicationContext());

        @Override
        public void onCallStateChanged(int state, String incomingNumber)
        {
            super.onCallStateChanged(state, incomingNumber);

            switch (state)
            {
                case TelephonyManager.CALL_STATE_RINGING:
                {
                    System.out.println("----incoming call: " + incomingNumber);
                    if (dbu.isBlackNumber(incomingNumber))
                    {
                        //get telephony interface
                        //                        abortBroadCast();
                        ITelephony iTelephony = getITelephony(mContext);
                        if (iTelephony != null)
                        {
                            // hang up automatically.
                            try
                            {
                                iTelephony.endCall();
                                Log.i(mTag, "End Call automatically");

                                //send notice to bar, if possible
                                /*
                                 * if (dbu.isNoticeToInfoBar()) { Notification
                                 * nf = new Notification(); nf.icon =
                                 * R.drawable.ic_icon5; nf.defaults =
                                 * Notification.DEFAULT_VIBRATE;
                                 * nf.setLatestEventInfo(mContext, null, null,
                                 * null); NotificationManager NotifyMgr =
                                 * (NotificationManager)
                                 * getSystemService(NOTIFICATION_SERVICE);
                                 * NotifyMgr.notify(0, nf); }
                                 */
                                //reply to caller if possible
                                //to do
                            }
                            catch (RemoteException e)
                            {
                                e.printStackTrace();
                                System.out.println("end call error!");
                            }

                            //add this incoming number to database.
                            try
                            {
                                dbu.AddBlackRecord(incomingNumber);
                            }
                            catch (Exception e)
                            {
                                System.out.println("add black record error!");
                            }
                        }
                    }
                    else
                    {
                        System.out.println(incomingNumber
                                + " is not black number!");
                    }
                    break;
                }
                case TelephonyManager.CALL_STATE_IDLE:
                {
                    //                    Log.i(mTag, "TelephonyManager.CALL_STATE_IDLE");
                    break;
                }
                case TelephonyManager.CALL_STATE_OFFHOOK:
                {
                    //                    Log.i(mTag, "TelephonyManager.CALL_STATE_OFFHOOK");
                    break;
                }
                default:
                    break;
            }
        }

    }
}
/* End: Modified by sunrise for BlackList 2012/06/01 */
