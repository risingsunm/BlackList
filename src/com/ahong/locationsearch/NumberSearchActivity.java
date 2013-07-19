package com.ahong.locationsearch;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ahong.blackcall.R;

/* Begin: Modified by xiepengfei for NumberSearchActivity 2012/05/21 */
public class NumberSearchActivity extends Activity
{
    public static String[] title;
    public static int[]    icon = { R.drawable.ic_icon0, R.drawable.ic_icon1,
            R.drawable.ic_icon2, R.drawable.ic_icon3, R.drawable.ic_icon4,
            R.drawable.ic_icon5, R.drawable.ic_icon6 };
    private ListView       mListView;
    private LayoutInflater mLayoutInflater;
    private MyAdapter      mAdapter;

    private ActionBar      mActionBar;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.number_seacrch_activity);
        mActionBar = getActionBar();

        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE);
        mActionBar.setTitle(R.string.number_search);
 
        /* Begin: Modified by xiepengfei for add some array 2012/05/26 */
        title = this.getResources().getStringArray(R.array.constant_title);
        /* End: Modified by xiepengfei for add some array 2012/05/26 */
        mLayoutInflater = this.getLayoutInflater();

        mListView = (ListView) findViewById(R.id.listview);
        mAdapter = new MyAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener()
        {

            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id)
            {
                if (position == 0)
                {
                    Intent intent = new Intent();
                    intent.setClass(NumberSearchActivity.this,
                            SearchActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent();
                    intent.setClass(NumberSearchActivity.this,
                            ConstantNumberActivity.class);
                    intent.putExtra("mode", position);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
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

    private class MyAdapter extends BaseAdapter
    {

        @Override
        public int getCount()
        {
            // TODO Auto-generated method stub
            return title.length;
        }

        @Override
        public Object getItem(int position)
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View view = mLayoutInflater.inflate(
                    R.layout.search_number_list_item, null);
            ((TextView) view.findViewById(R.id.textview))
                    .setText(title[position]);
            ((ImageView) view.findViewById(R.id.imageview))
                    .setImageResource(icon[position]);
            return view;
        }

    }
}
/* End: Modified by xiepengfei for NumberSearchActivity 2012/05/21 */
