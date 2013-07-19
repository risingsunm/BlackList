/* Begin: Modified by sunrise for BlackList 2012/06/01 */

package com.ahong.blackcall;

import java.util.ArrayList;
import java.util.HashMap;

import android.R.color;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class AhongBlackCallListAdapter extends BaseAdapter
{
    private LayoutInflater                     mInflater  = null;
    private ArrayList<HashMap<String, String>> mArrayList = null;

    public AhongBlackCallListAdapter(Context context,
            ArrayList<HashMap<String, String>> arrayList)
    {
        mArrayList = arrayList;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount()
    {
        return mArrayList.size();
    }

    public Object getItem(int position)
    {
        return mArrayList.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    /**
     * get each item data then pass to UI to fill black number list .
     * */
    public View getView(int position, View convertView, ViewGroup parent)
    {
        HolderView holder;

        //set itemView in adapter
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.black_list_item, null);
            holder = new HolderView();

            holder.NameNumber = (TextView) convertView
                    .findViewById(R.id.black_list_item_name_number);
            holder.TypeAttach = (TextView) convertView
                    .findViewById(R.id.black_list_item_type_attach);

            convertView.setTag(holder);
        }
        else
        {
            holder = (HolderView) convertView.getTag();
        }

        //set data of each itemView
        @SuppressWarnings("unchecked")
        HashMap<String, String> item = (HashMap<String, String>) getItem(position);
        holder.NameNumber.setText(item.get("NameNumber").toString());
        holder.TypeAttach.setText(item.get("TypeAttach").toString());

        return convertView;
    }

    private class HolderView
    {
        TextView NameNumber;
        TextView TypeAttach;
    }

}
/* End: Modified by sunrise for BlackList 2012/06/01 */
