/* Begin: Modified by sunrise for BlackList 2012/06/01 */

/**
 * 
 */
package com.ahong.blackcall;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @since adapter class to record list.
 * @author sunrise.L 2012-4-28
 */
public class AhongBlackCallRecordAdapter extends BaseAdapter
{
    private LayoutInflater                     mInflater  = null;
    private ArrayList<HashMap<String, String>> mArrayList = null;

    public AhongBlackCallRecordAdapter(Context context,
            ArrayList<HashMap<String, String>> arrayList)
    {
        mArrayList = arrayList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount()
    {
        return mArrayList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mArrayList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @SuppressWarnings("rawtypes")
    @Override
    /**
     * fill record list view and get each item data then pass to UI.
     * */
    public View getView(int position, View convertView, ViewGroup parent)
    {
        HolderView holder;

        //set itemView in adapter
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.record_list_item, null);
            holder = new HolderView();

            holder.name = (TextView) convertView
                    .findViewById(R.id.record_item_name);
            holder.datetime = (TextView) convertView
                    .findViewById(R.id.record_item_date_time);
            holder.attach = (TextView) convertView
                    .findViewById(R.id.record_item_attach);

            convertView.setTag(holder);
        }
        else
        {
            holder = (HolderView) convertView.getTag();
        }

        //set data of each itemView
        //should append the display name to this number from contacts.
        HashMap item = (HashMap) getItem(position);
        holder.name.setText(item.get("number").toString());
        holder.datetime.setText(item.get("datetime").toString());
        holder.attach.setText(item.get("attach").toString());

        return convertView;
    }

    private class HolderView
    {
        TextView name;
        TextView datetime;
        TextView attach;
    }

}
/* End: Modified by sunrise for BlackList 2012/06/01 */
