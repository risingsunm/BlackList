/* Begin: Modified by sunrise for BlackList 2012/06/01 */
package com.ahong.blackcall;

/**
 * this file is the entry of BlackList module.Three tab will be add to a
 * actionBar when this application start.
 * */
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
/* Begin: Modified by xiepengfei for change the layout 2012/05/10 */
import android.content.Intent;
import com.ahong.locationsearch.NumberSearchActivity;
/* End: Modified by xiepengfei for change the layout 2012/05/10 */
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
/* Begin: Modified by sunrise for BlackListTab 2012/06/13 */
import android.graphics.Color;
import android.widget.TextView;
/* End: Modified by sunrise for BlackListTab 2012/06/13 */
/*Begin: Modified by sunrise for AddDatabase 2012/06/21*/
import java.io.IOException;
/*End: Modified by sunrise for AddDatabase 2012/06/21*/

public class AhongBlackActivity extends Activity
{
    //member Fragments' Tag
    private static final String  m_strCallBlackRecordTag = "CallBlackRecord";
    private static final String  m_strCallBlackListTag   = "CallBlackList";
    private static final String  m_strCallBlackFilterTag = "CallBlackFilter";

    //member Fragments
    AhongBlackCallRecordFragment m_recordFragment        = null;
    AhongBlackCallListFragment   m_listFragment          = null;              ;
    AhongBlackCallSetFragment    m_setFragment           = null;              ;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.black_fragment);

        //add actionBar and add Tab to it.
        ActionBar actionBar = getActionBar();
        if (actionBar != null)
        {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            /* Begin: Modified by sunrise for BlackListTab 2012/06/13 */
            //actionBar.setStackedBackgroundDrawable(getResources().getDrawable(
            //        R.drawable.actionbar_tab_bg_normal));
            /* End: Modified by sunrise for BlackListTab 2012/06/13 */
            //            actionBar.setSplitBackgroundDrawable(getResources().getDrawable(
            //                    R.drawable.my_ab_bottom_opaque_dark_holo));
        }

        m_recordFragment = new AhongBlackCallRecordFragment();
        actionBar.addTab(actionBar
                .newTab()
                .setTag(m_strCallBlackRecordTag)
                .setCustomView(R.layout.record_table_view)
                .setTabListener(
                        new TabListener<AhongBlackCallRecordFragment>(this,
                                m_strCallBlackRecordTag,
                                AhongBlackCallRecordFragment.class)));

        m_listFragment = new AhongBlackCallListFragment();
        actionBar.addTab(actionBar
                .newTab()
                .setTag(m_strCallBlackListTag)
                .setCustomView(R.layout.list_table_view)
                .setTabListener(
                        new TabListener<AhongBlackCallListFragment>(this,
                                m_strCallBlackListTag,
                                AhongBlackCallListFragment.class)));

        m_setFragment = new AhongBlackCallSetFragment();
        actionBar.addTab(actionBar
                .newTab()
                .setTag(m_strCallBlackFilterTag)
                .setCustomView(R.layout.set_table_view)
                .setContentDescription(R.string.call_black_set)
                .setTabListener(
                        new TabListener<AhongBlackCallSetFragment>(this,
                                m_strCallBlackFilterTag,
                                AhongBlackCallSetFragment.class)));

        if (savedInstanceState != null)
        {
            actionBar.setSelectedNavigationItem(savedInstanceState.getInt(
                    "tab", 0));
        }

        // initialize databases for this application.
        AhongBlackCallDBHelper DBhelper = new AhongBlackCallDBHelper(
                AhongBlackActivity.this);
        SQLiteDatabase db = DBhelper.getReadableDatabase();
        db.close();
        /*Begin: Modified by sunrise for AddDatabase 2012/06/21*/
        try
        {
            AhongBlackCallDBUtils.mergeFile(getApplication());
        }
        catch (IOException e)
        {
            System.out.println("--------phoneAttribution.db init ERROR!-----------");
            e.printStackTrace();
        }
        /*End: Modified by sunrise for AddDatabase 2012/06/21*/
    }

    /*Begin: Modified by sunrise for BlackLauncher 2012/08/14*/
    @Override
    protected void onPause()
    {
        // TODO Auto-generated method stub
        /*Begin: Modified by sunrise for appExit 2012/08/24*/
        //finish();
        /*Begin: Modified by sunrise for appExit 2012/08/24*/
        super.onPause();
    }
    /*End: Modified by sunrise for BlackLauncher 2012/08/14*/

    /**
     * inner template class as listernerAdapter of actionBar.
     */
    public static class TabListener<T extends Fragment> implements
            ActionBar.TabListener
    {
        private final Activity mActivity;
        private final String   mTag;
        private final Class<T> mClass;
        private final Bundle   mArgs;
        private Fragment       mFragment;
        private ImageView      iv = null;
        /* Begin: Modified by sunrise for BlackListTab 2012/06/13 */
        private TextView       tv = null;
        /* End: Modified by sunrise for BlackListTab 2012/06/13 */
        public TabListener(Activity activity, String tag, Class<T> clz)
        {
            this(activity, tag, clz, null);
        }

        public TabListener(Activity activity, String tag, Class<T> clz,
                Bundle args)
        {
            mActivity = activity;
            mTag = tag;
            mClass = clz;
            mArgs = args;

            // Check to see if we already have a fragment for this tab, probably
            // from a previously saved state. If so, deactivate it, because our
            // initial state is that a tab isn't shown.
            mFragment = mActivity.getFragmentManager().findFragmentByTag(mTag);
            if (mFragment != null && !mFragment.isDetached())
            {
                FragmentTransaction ft = mActivity.getFragmentManager()
                        .beginTransaction();
                ft.detach(mFragment);
                ft.commit();
            }
        }

        public void onTabSelected(Tab tab, FragmentTransaction ft)
        {
            if (mFragment == null)
            {
                mFragment = Fragment.instantiate(mActivity, mClass.getName(),
                        mArgs);
                ft.add(R.id.fragment_black, mFragment, mTag);
            }
            else
            {
                ft.attach(mFragment);
            }

            if (mTag == m_strCallBlackRecordTag)
            {
                iv = (ImageView) tab.getCustomView().findViewById(
                        R.id.recordImage);
                iv.setImageResource(R.drawable.record_history_press);
                /* Begin: Modified by sunrise for BlackListTab 2012/06/13 */
                tv = (TextView) tab.getCustomView().findViewById(
                        R.id.recordText);
                tv.setTextColor(Color.WHITE);
                /* End: Modified by sunrise for BlackListTab 2012/06/13 */
            }
            else if (mTag == m_strCallBlackListTag)
            {
                iv = (ImageView) tab.getCustomView().findViewById(
                        R.id.listImage);
                iv.setImageResource(R.drawable.black_number_list_press);
                /* Begin: Modified by sunrise for BlackListTab 2012/06/13 */
                tv = (TextView) tab.getCustomView().findViewById(R.id.listText);
                tv.setTextColor(Color.WHITE);
                /* End: Modified by sunrise for BlackListTab 2012/06/13 */
            }
            else if (mTag == m_strCallBlackFilterTag)
            {
                iv = (ImageView) tab.getCustomView()
                        .findViewById(R.id.setImage);
                iv.setImageResource(R.drawable.black_set_press);
                /* Begin: Modified by sunrise for BlackListTab 2012/06/13 */
                tv = (TextView) tab.getCustomView().findViewById(R.id.setText);
                tv.setTextColor(Color.WHITE);
                /* End: Modified by sunrise for BlackListTab 2012/06/13 */
            }
            else
            {
                throw new IllegalArgumentException("Unknows Tag:" + mTag);
            }
        }

        public void onTabUnselected(Tab tab, FragmentTransaction ft)
        {
            if (mFragment != null)
            {
                ft.detach(mFragment);
            }

            if (mTag == m_strCallBlackRecordTag)
            {
                iv = (ImageView) tab.getCustomView().findViewById(
                        R.id.recordImage);
                iv.setImageResource(R.drawable.record_history);
                /* Begin: Modified by sunrise for BlackListTab 2012/06/13 */
                tv = (TextView) tab.getCustomView().findViewById(
                        R.id.recordText);
                tv.setTextColor(Color.GRAY);
                /* End: Modified by sunrise for BlackListTab 2012/06/13 */
            }
            else if (mTag == m_strCallBlackListTag)
            {
                iv = (ImageView) tab.getCustomView().findViewById(
                        R.id.listImage);
                iv.setImageResource(R.drawable.black_number_list);
                /* Begin: Modified by sunrise for BlackListTab 2012/06/13 */
                tv = (TextView) tab.getCustomView().findViewById(R.id.listText);
                tv.setTextColor(Color.GRAY);
                /* End: Modified by sunrise for BlackListTab 2012/06/13 */
            }
            else if (mTag == m_strCallBlackFilterTag)
            {
                iv = (ImageView) tab.getCustomView()
                        .findViewById(R.id.setImage);
                iv.setImageResource(R.drawable.black_set);
                /* Begin: Modified by sunrise for BlackListTab 2012/06/13 */
                tv = (TextView) tab.getCustomView().findViewById(
                        R.id.setText);
                tv.setTextColor(Color.GRAY);
                /* End: Modified by sunrise for BlackListTab 2012/06/13 */
            }
            else
            {
                throw new IllegalArgumentException("Unknows Tag:" + mTag);
            }
        }

        public void onTabReselected(Tab tab, FragmentTransaction ft)
        {
            //nothing to do.
            //Toast.makeText(mActivity, "Reselected!", Toast.LENGTH_SHORT).show();
        }
    }

}
/* End: Modified by sunrise for BlackList 2012/06/01 */
