/* Begin: Modified by sunrise for BlackList 2012/06/01 */

package com.ahong.blackcall;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class AhongBlackCallRecordFragment extends Fragment implements
        AhongBlackCallConst.UpdateViewIF, OnItemClickListener
{
    /**
     * Layout Control of record fragment
     * */
    private LinearLayout                       mRecordLinearLayout;
    private LinearLayout                       mEmptyView     = null;
    private ListView                           mRecordListView;
    private ArrayList<HashMap<String, String>> mRecordListData;

    // categories of item click.
    private final int                          RECORD_DELETE  = 0;
    private final int                          RECORD_TELTO   = 1;
    private final int                          RECORD_SMSTO   = 2;
    private final int                          RECORD_CLEAR   = 3;

    private Activity                           mActivity      = null;
    private AhongBlackCallRecordAdapter        mRecordAdapter = null;
    private AhongBlackCallDBUtils              mDataBaseUtils = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        mActivity = getActivity();
        BuildRecordListData();
        setmRecordAdapter(new AhongBlackCallRecordAdapter(mActivity,
                mRecordListData));
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause()
    {
        mDataBaseUtils.dbHelper.close();
        super.onPause();
    }

    @Override
    public void onResume()
    {
        UpdateListView();
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View RecordFragView = inflater.inflate(R.layout.black_record_fragment,
                container, false);
        mRecordLinearLayout = (LinearLayout) RecordFragView
                .findViewById(R.id.black_record_fragment);
        mEmptyView = (LinearLayout) RecordFragView
                .findViewById(R.id.recordEmpty);
        mRecordListView = (ListView) RecordFragView
                .findViewById(R.id.record_list);

        mRecordListView.setAdapter(getmRecordAdapter());
        mRecordListView.setScrollBarStyle(ListView.SCROLLBARS_OUTSIDE_OVERLAY);
        mRecordListView.setOnItemClickListener(this);
        mRecordListView.setItemsCanFocus(true);

        SetBackGround();

        return RecordFragView;
    }

    /**
     * build array list data of blocked record from DataBase
     * */
    private void BuildRecordListData()
    {
        if (mRecordListData == null)
        {
            mRecordListData = new ArrayList<HashMap<String, String>>();
        }
        else
        {
            mRecordListData.clear();
        }

        mDataBaseUtils = new AhongBlackCallDBUtils(mActivity);
        mRecordListData = mDataBaseUtils
                .GetAllRecord(AhongBlackCallConst.BLACK_RECORD_TABLE);

        //return mRecordListData;
    }

    /**
     * @since set background base on list view is empty or not.Now the empty
     *        background fill via widget(TextView) at moment, but we can set
     *        image to replace it also.
     * */
    private void SetBackGround()
    {
        int recordCount = mRecordListData.size();
        mRecordLinearLayout.setBackgroundDrawable(null);
        if (recordCount > 0)
        {
            /*Begin: Modified by sunrise for BlackListFine 2012/06/02*/
            //            getActivity().getActionBar().setTitle(
            //                    getResources().getString(R.string.call_black_title)
            //                            + "-have filter " + recordCount);
            /*End: Modified by sunrise for BlackListFine 2012/06/02*/
            mEmptyView.setVisibility(View.GONE);
        }
        else
        {
            //            Drawable noContact = getResources().getDrawable(
            //                    R.drawable.nodata_bg);
            //            mRecordLinearLayout.setBackgroundDrawable(noContact);
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    /**
     * set option menu again from another task entry.
     * */
    public void onPrepareOptionsMenu(Menu menu)
    {
        menu.findItem(R.id.clear_record).setVisible(
                mRecordListData.size() > 0 ? true : false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    /**
     * different operate menu entrance to recordList
     * */
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.recordfragment, menu);
        menu.findItem(R.id.clear_record).setVisible(
                mRecordListData.size() > 0 ? true : false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.clear_record:
            {
                AlertDialog.Builder ClearDlg = new AlertDialog.Builder(
                        mActivity);
                ClearDlg.setTitle(R.string.clear_msg)
                        /*Begin: Modified by sunrise for BlackListFine 2012/06/01*/
                        /* Begin: Modified by sunrise for ReplaceImg 2012/06/08 */
                        .setIcon(R.drawable.item_del)
                        /* End: Modified by sunrise for ReplaceImg 2012/06/08 */
                        /*End: Modified by sunrise for BlackListFine 2012/06/01*/
                        .setPositiveButton(R.string.global_ok,
                                new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which)
                                    {
                                        mDataBaseUtils
                                                .DelAllItems(AhongBlackCallConst.BLACK_RECORD_TABLE);
                                        UpdateListView();
                                    }

                                })
                        .setNegativeButton(R.string.global_no,
                                new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which)
                                    {
                                        dialog.cancel();
                                    }
                                }).show();

                break;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    /**
     * pop-up a dialog that could jump to other screen when item be clicked.
     * */
    public void onItemClick(final AdapterView<?> adapterView, View itemView,
            final int position, long id)
    {
        AlertDialog.Builder RecordItemDlg = new AlertDialog.Builder(
                getActivity());
        RecordItemDlg.setTitle(R.string.call_black_record);
        RecordItemDlg
                .setItems(R.array.RecordDlgItem,
                        new DialogInterface.OnClickListener()
                        {
                            @SuppressWarnings("rawtypes")
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which)
                            {
                                System.out.println("mRecordListData.size="
                                        + adapterView.getCount() + " position="
                                        + position);

                                final HashMap item = (HashMap) adapterView
                                        .getItemAtPosition(position);
                                System.out.println("item:" + item.toString()
                                        + " which:" + which);
                                switch (which)
                                {
                                    case RECORD_DELETE:
                                    {
                                        // pop-up to ensure to next step.
                                        AlertDialog.Builder popup = new AlertDialog.Builder(
                                                mActivity);
                                        popup.setTitle(R.string.delete_title)
                                                /*Begin: Modified by sunrise for BlackListFine 2012/06/01*/
                                                /*Begin: Modified by sunrise for ReplaceImg 2012/06/08*/
                                                .setIcon(R.drawable.item_del)
                                                /*End: Modified by sunrise for ReplaceImg 2012/06/08*/
                                                /*End: Modified by sunrise for BlackListFine 2012/06/01*/
                                                .setMessage(R.string.delete_msg)
                                                .setPositiveButton(
                                                        R.string.global_ok,
                                                        new DialogInterface.OnClickListener()
                                                        {
                                                            @Override
                                                            public void onClick(
                                                                    DialogInterface dialog,
                                                                    int which)
                                                            {
                                                                int rows = mDataBaseUtils
                                                                        .DelItem(
                                                                                AhongBlackCallConst.BLACK_RECORD_TABLE,
                                                                                item.get(
                                                                                        "id")
                                                                                        .toString());

                                                                if (rows > 0)
                                                                {
                                                                    UpdateListView();

                                                                }
                                                                else
                                                                {
                                                                    dialog.dismiss();
                                                                    BlackCallToast(R.string.del_fail);
                                                                }
                                                            }

                                                        })
                                                .setNegativeButton(
                                                        R.string.global_no,
                                                        new DialogInterface.OnClickListener()
                                                        {
                                                            @Override
                                                            public void onClick(
                                                                    DialogInterface dialog,
                                                                    int which)
                                                            {
                                                                dialog.cancel();
                                                            }
                                                        }).show();

                                        break;
                                    }
                                    case RECORD_TELTO:
                                    {
                                        final String number = item
                                                .get("number").toString();
                                        Uri DstNum = Uri.parse("tel:" + number);
                                        Intent telIntent = new Intent(
                                        //      Intent.ACTION_CALL, DstNum);
                                                Intent.ACTION_DIAL, DstNum);
                                        getActivity().startActivity(telIntent);
                                        break;
                                    }
                                    case RECORD_SMSTO:
                                    {
                                        final String number = item
                                                .get("number").toString();
                                        Uri DstNum = Uri.parse("smsto:"
                                                + number);
                                        Intent telIntent = new Intent(
                                                Intent.ACTION_SENDTO, DstNum);
                                        getActivity().startActivity(telIntent);
                                        break;
                                    }
                                    case RECORD_CLEAR:
                                    {
                                        // pop-up to ensure next step
                                        AlertDialog.Builder ClearDlg = new AlertDialog.Builder(
                                                mActivity);
                                        ClearDlg.setTitle(R.string.clear_msg)
                                                /*Begin: Modified by sunrise for BlackListFine 2012/06/01*/
                                                /* Begin: Modified by sunrise for ReplaceImg 2012/06/08 */
                                                .setIcon(R.drawable.item_del)
                                                /* End: Modified by sunrise for ReplaceImg 2012/06/08 */
                                                /*End: Modified by sunrise for BlackListFine 2012/06/01*/
                                                .setPositiveButton(
                                                        R.string.global_ok,
                                                        new DialogInterface.OnClickListener()
                                                        {
                                                            @Override
                                                            public void onClick(
                                                                    DialogInterface dialog,
                                                                    int which)
                                                            {
                                                                mDataBaseUtils
                                                                        .DelAllItems(AhongBlackCallConst.BLACK_RECORD_TABLE);
                                                                UpdateListView();
                                                            }

                                                        })
                                                .setNegativeButton(
                                                        R.string.global_no,
                                                        new DialogInterface.OnClickListener()
                                                        {
                                                            @Override
                                                            public void onClick(
                                                                    DialogInterface dialog,
                                                                    int which)
                                                            {
                                                                dialog.cancel();
                                                            }
                                                        }).show();

                                        break;

                                    }
                                    default:
                                        break;
                                }

                            }
                        })
                .setNegativeButton(getString(R.string.global_no),
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which)
                            {
                                dialog.dismiss();
                            }
                        }).show();

    }

    /**
     * @return the mRecordAdapter
     */
    public AhongBlackCallRecordAdapter getmRecordAdapter()
    {
        return mRecordAdapter;
    }

    /**
     * @param mRecordAdapter
     *            the mRecordAdapter to set
     */
    public void setmRecordAdapter(AhongBlackCallRecordAdapter mRecordAdapter)
    {
        this.mRecordAdapter = mRecordAdapter;
    }

    @Override
    public void BlackCallToast(int resID)
    {
        Toast.makeText(mActivity, getString(resID), Toast.LENGTH_LONG).show();
    }

    @Override
    public void UpdateListView()
    {
        BuildRecordListData();
        mRecordListView.setAdapter(new AhongBlackCallRecordAdapter(mActivity,
                mRecordListData));
        SetBackGround();//(mRecordAdapter.getCount());
    }

}
/* End: Modified by sunrise for BlackList 2012/06/01 */
