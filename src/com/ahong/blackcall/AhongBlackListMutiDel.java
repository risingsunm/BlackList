/* Begin: Modified by sunrise for BlackList 2012/06/01 */
package com.ahong.blackcall;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * This file refer to items that multi-selected to be deleted.
 */
public class AhongBlackListMutiDel extends Activity implements OnClickListener,
        OnItemClickListener
{
    private final String                       mLog            = "AhongBlackListMutiDel:";
    private AhongBlackCallDBUtils              mDataBaseUtils  = null;
    private ArrayList<HashMap<String, String>> mBlackListData  = null;
    private MultiDelAdapter                    mDelAdapter     = null;
    private LinearLayout                       LayoutSelectAll = null;
    private LinearLayout                       LayoutDelBtn    = null;
    private LinearLayout                       mEmpytView      = null;
    private TreeSet<Integer>                   mDelTreeSet     = null;
    private Button                             btnDelDone      = null;
    private Button                             btnDelCancel    = null;
    private ListView                           mListView       = null;
    private CheckBox                           mBoxAll         = null;
    private boolean                            isSelectAll     = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.black_list_fragment);
        mDelTreeSet = new TreeSet<Integer>();

        ActionBar delBar = getActionBar();
        delBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        delBar.setTitle(getResources().getString(R.string.delete_multi));
        /*Begin: Modified by sunrise for BlackListFine 2012/06/01*/
        /* Begin: Modified by sunrise for ReplaceImg 2012/06/08 */
        delBar.setIcon(R.drawable.item_del);
        /* End: Modified by sunrise for ReplaceImg 2012/06/08 */
        /*End: Modified by sunrise for BlackListFine 2012/06/01*/
        /*Begin: Modified by sunrise for ReplaceImg 2012/06/08*/
        delBar.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.del_all_bg));
        /*End: Modified by sunrise for ReplaceImg 2012/06/08*/
        delBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        @SuppressWarnings("unchecked")
        ArrayList<HashMap<String, String>> serializableExtra = (ArrayList<HashMap<String, String>>) intent
                .getSerializableExtra(AhongBlackCallConst.BLACK_NUMBER_TABLE);
        mBlackListData = serializableExtra;

        setmDelAdapter(new MultiDelAdapter(this, mBlackListData));
        mListView = (ListView) findViewById(R.id.black_list);
        mListView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mListView.setAdapter(getmDelAdapter());
        mListView.setOnItemClickListener(this);
        mListView.setItemsCanFocus(true);

        mBoxAll = (CheckBox) findViewById(R.id.checkbox_select_all);
        mBoxAll.setOnClickListener(this);
        mBoxAll.setClickable(false);
        mBoxAll.setChecked(false);

        mEmpytView = (LinearLayout) findViewById(R.id.numberEmpty);
        mEmpytView.setVisibility(View.GONE);
        LayoutSelectAll = (LinearLayout) findViewById(R.id.layout_select_all);
        LayoutSelectAll.setVisibility(View.VISIBLE);
        LayoutSelectAll.setFocusable(true);
        LayoutSelectAll.setClickable(true);
        LayoutSelectAll.setOnClickListener(this);

        LayoutDelBtn = (LinearLayout) findViewById(R.id.layout_delete_btn);
        LayoutDelBtn.setVisibility(View.VISIBLE);
        btnDelDone = (Button) findViewById(R.id.delete_done);
        btnDelDone.setOnClickListener(this);
        btnDelCancel = (Button) findViewById(R.id.delete_cancel);
        btnDelCancel.setOnClickListener(this);
        updateBtnState();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (mDelTreeSet == null)
        {
            mDelTreeSet = new TreeSet<Integer>();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (mDelTreeSet != null)
        {
            mDelTreeSet.clear();
        }
    }

    @Override
    protected void onStop()
    {
        mDelTreeSet.clear();
        super.onStop();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.checkbox_select_all:
            {
                Log.i(mLog, "checkbox_select_all " + mBoxAll.isChecked());
                break;
            }
            case R.id.layout_select_all:
            {
                Log.i(mLog, "layout_select_all-" + mBoxAll.isChecked());
                if (mBoxAll.isChecked())
                {
                    mBoxAll.setChecked(false);
                    btnDelDone.setEnabled(false);
                    setSelectAllFlag(false);
                }
                else
                {
                    mBoxAll.setChecked(true);
                    btnDelDone.setEnabled(true);
                    setSelectAllFlag(true);
                }

                updateDelSet(getSelectAllFlag());
                mListView.setAdapter(getmDelAdapter());

                break;
            }
            case R.id.delete_done:
            {
                if (mDataBaseUtils == null)
                {
                    mDataBaseUtils = new AhongBlackCallDBUtils(this);
                }

                if (getSelectAllFlag())
                {
                    mDataBaseUtils
                            .DelAllItems(AhongBlackCallConst.BLACK_NUMBER_TABLE);
                    finish();
                }
                else
                {
                    StringBuffer sb = new StringBuffer();
                    for (Iterator<Integer> itr = mDelTreeSet.iterator(); itr
                            .hasNext();)
                    {
                        if (sb.length() != 0)
                        {
                            sb.append(", ");
                        }
                        sb.append(String.valueOf(itr.next()));
                    }

                    try
                    {
                        mDataBaseUtils.DelItem(
                                AhongBlackCallConst.BLACK_NUMBER_TABLE,
                                sb.toString());
                    }
                    catch (Exception e)
                    {
                        Log.i(mLog, "delete balck number error!");
                    }
                    finally
                    {
                        /* Begin: Modified by sunrise for AddBlackNumberMulDel 2012/06/04 */
                       if (mBlackListData.size() == mDelTreeSet.size())
                        /* End: Modified by sunrise for AddBlackNumberMulDel 2012/06/04 */
                        {
                            finish();
                        }
                        else
                        {
                            mBlackListData.clear();
                            /* Begin: Modified by sunrise for AddBlackNumberMulDel 2012/06/04 */
                            mDelTreeSet.clear();
                             /* End: Modified by sunrise for AddBlackNumberMulDel 2012/06/04 */
                            mBlackListData = mDataBaseUtils
                                    .GetAllBlackNumber(AhongBlackCallConst.BLACK_NUMBER_TABLE);
                            setmDelAdapter(new MultiDelAdapter(this,
                                    mBlackListData));
                            mListView.setAdapter(getmDelAdapter());
                        }
                    }
                }

                break;
            }
            case R.id.delete_cancel:
            {
                finish();
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id)
    {
        HolderView hv = (HolderView) view.getTag();
        hv.mCheckBox.toggle();
        //        HashMap<String, String> itemAtPosition = extracted(parent, position);
        //        final HashMap<String, String> item = itemAtPosition;
        //
        //        updateDelSet(hv.mCheckBox.isChecked(), item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            //for go back
            case android.R.id.home:
            {
                finish();
                return true;
            }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setSelectAllFlag(final boolean selectAll)
    {
        isSelectAll = selectAll;
    }

    @SuppressWarnings("unused")
    private boolean getSelectAllFlag()
    {
        return isSelectAll;
    }

    private HashMap<String, String> extracted(AdapterView<?> parent,
            int position)
    {
        return (HashMap<String, String>) parent.getItemAtPosition(position);
    }

    private void updateBtnState()
    {
        if (mDelTreeSet == null || mDelTreeSet.size() == 0)
        {
            btnDelDone.setEnabled(false);
            //            btnDelDone.setBackgroundDrawable(getResources().getDrawable(
            //                    R.drawable.tw_button_default_normal));
        }
        else
        {
            btnDelDone.setEnabled(true);
            //btnDelDone.setTextColor(R.color.list_primary_color);
        }
    }

    private void updateDelSet(final boolean selectAllFlag)
    {
        if (selectAllFlag)
        {
            for (Iterator<HashMap<String, String>> itr = mBlackListData
                    .iterator(); itr.hasNext();)
            {
                HashMap<String, String> item = (HashMap<String, String>) itr
                        .next();
                updateDelSet(selectAllFlag, item);
            }
        }
        else
        {
            mDelTreeSet.clear();
        }

        updateBtnState();
        //        System.out.println(mLog + mDelTreeSet.toString());
    }

    private void updateDelSet(boolean isChecked, HashMap<String, String> item)
    {
        int id = Integer.parseInt(item.get("id"));
        Log.i(mLog, "[updateDelSet]isChecked: " + isChecked + " ,id: " + id);
        if (isChecked)
        {
            mDelTreeSet.add(id);

            /* Begin: Modified by sunrise for MultiDelBtnUpdate 2012/06/05 */
            if (mDelTreeSet.size() == mBlackListData.size())
            {
                mBoxAll.setChecked(true);
                setSelectAllFlag(true);
            }
            /* End: Modified by sunrise for MultiDelBtnUpdate 2012/06/05 */

        }
        else
        {
            if (mDelTreeSet.contains(id))
            {
                mDelTreeSet.remove(id);
            }

            mBoxAll.setChecked(false);
            setSelectAllFlag(false);
            btnDelDone.setEnabled(mDelTreeSet.size() > 0 ? true : false);
        }

        updateBtnState();
    }

    private MultiDelAdapter getmDelAdapter()
    {
        return mDelAdapter;
    }

    private void setmDelAdapter(MultiDelAdapter adapter)
    {
        mDelAdapter = adapter;
    }

    /**
     * class of adapter to multi-delete activity.
     */
    private class MultiDelAdapter extends BaseAdapter
    {
        private LayoutInflater                     mInflater  = null;
        private ArrayList<HashMap<String, String>> mArrayList = null;

        public MultiDelAdapter(Context context,
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

        @SuppressWarnings("unchecked")
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            HolderView holder;

            if (convertView == null)
            {
                convertView = mInflater.inflate(R.layout.black_list_item, null);
                holder = new HolderView();

                holder.NameNumber = (TextView) convertView
                        .findViewById(R.id.black_list_item_name_number);
                holder.TypeAttach = (TextView) convertView
                        .findViewById(R.id.black_list_item_type_attach);
                holder.mCheckBox = (CheckBox) convertView
                        .findViewById(R.id.delete_tag);
                holder.mCheckBox.setVisibility(View.VISIBLE);
                holder.mCheckBox.setClickable(false);

                convertView.setTag(holder);
            }
            else
            {
                holder = (HolderView) convertView.getTag();
            }

            final HashMap<String, String> item = (HashMap<String, String>) getItem(position);
            holder.NameNumber.setText(item.get("NameNumber").toString());
            holder.TypeAttach.setText(item.get("TypeAttach").toString());

            holder.mCheckBox
                    .setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
                    {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                boolean isChecked)
                        {
                            updateDelSet(isChecked, item);
                        }
                    });

            if (getSelectAllFlag())
            {
                holder.mCheckBox.setChecked(true);
            }
            else
            {
                holder.mCheckBox.setChecked(false);
            }

            return convertView;
        }

    }

    private class HolderView
    {
        TextView NameNumber;
        TextView TypeAttach;
        CheckBox mCheckBox;
    }
}
/* End: Modified by sunrise for BlackList 2012/06/01 */
