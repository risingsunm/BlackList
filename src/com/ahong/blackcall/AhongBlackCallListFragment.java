/* Begin: Modified by sunrise for BlackList 2012/06/01 */

package com.ahong.blackcall;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This file about to tackle the black number screen.inlcude display detail info
 * of each item,add,edit,delete to database etc.
 */
public class AhongBlackCallListFragment extends Fragment implements
        AhongBlackCallConst.UpdateViewIF, OnItemClickListener
{
    /**
     * Layout Control of record fragment
     * */
    private LinearLayout                       mListLinearLayout;
    private LinearLayout                       mEmptyView;
    private ListView                           mBlackListView;
    private ArrayList<HashMap<String, String>> mBlackListData;
    @SuppressWarnings("unused")
    private CheckBox                           mBox;
    private Activity                           mActivity         = null;
    private AhongBlackCallListAdapter          mAdapter          = null;

    private final int                          BLACK_LIST_DELETE = 0;
    private final int                          BLACK_LIST_EDIT   = 1;

    //member variable to operate the DataBase
    private AhongBlackCallDBUtils              mDataBaseUtils    = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        mActivity = getActivity();
        initDataBase();
        setmAdapter(new AhongBlackCallListAdapter(mActivity, mBlackListData));
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View ListFragView = inflater.inflate(R.layout.black_list_fragment,
                container, false);
        mListLinearLayout = (LinearLayout) ListFragView
                .findViewById(R.id.black_list_fragment);
        mEmptyView = (LinearLayout) ListFragView.findViewById(R.id.numberEmpty);
        mBlackListView = (ListView) ListFragView.findViewById(R.id.black_list);

        mBlackListView.setScrollBarStyle(ListView.SCROLLBARS_OUTSIDE_OVERLAY);
        mBlackListView.setOnItemClickListener(this);
        mBlackListView.setItemsCanFocus(true);
        mBlackListView.setAdapter(getmAdapter());
        SetBackGround();
        return ListFragView;
    }

    @Override
    public void onResume()
    {
        UpdateListView();
        super.onResume();
    }

    private void setmAdapter(
            final AhongBlackCallListAdapter ahongBlackCallListAdapter)
    {
        mAdapter = ahongBlackCallListAdapter;
    }

    public AhongBlackCallListAdapter getmAdapter()
    {
        return mAdapter;
    }

    @Override
    /**
     * pop-up delete and edit dialog when click the item
     * */
    public void onItemClick(final AdapterView<?> adapterView, View view,
            final int position, long id)
    {
        AlertDialog.Builder ListItemDlg = new AlertDialog.Builder(mActivity);
        ListItemDlg
                .setItems(R.array.ListDlgItem,
                        new DialogInterface.OnClickListener()
                        {
                            @SuppressWarnings("rawtypes")
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which)
                            {
                                final HashMap item = (HashMap) adapterView
                                        .getItemAtPosition(position);

                                System.out.println("BlackListView.size="
                                        + adapterView.getCount() + " position:"
                                        + position + "\n item = "
                                        + item.toString());

                                switch (which)
                                {
                                    case BLACK_LIST_DELETE:
                                    {
                                        // popup for hint
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
                                                                                AhongBlackCallConst.BLACK_NUMBER_TABLE,
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
                                    case BLACK_LIST_EDIT:
                                    {
                                        LayoutInflater inflater = (LayoutInflater) mActivity
                                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        final View view = inflater.inflate(
                                                R.layout.dlg_add_black, null);

                                        final EditText BlackNumber = (EditText) view
                                                .findViewById(R.id.et_black_number);
                                        final String OldNumber = String
                                                .valueOf(item.get("NameNumber"));
                                        BlackNumber.setText(OldNumber);

                                        new AlertDialog.Builder(mActivity)
                                                .setTitle(
                                                        getString(R.string.edit_black_title))
                                                //                                                .setIcon(R.drawable.edit)
                                                .setView(view)
                                                .setPositiveButton(
                                                        R.string.global_ok,
                                                        new DialogInterface.OnClickListener()
                                                        {
                                                            public void onClick(
                                                                    DialogInterface dialog,
                                                                    int which)
                                                            {
                                                                String NewNumber = BlackNumber
                                                                        .getText()
                                                                        .toString()
                                                                        .trim();
                                                                // no number be
                                                                // input
                                                                if (NewNumber
                                                                        .length() == 0)
                                                                {
                                                                    BlackCallToast(R.string.input_black_number);
                                                                }
                                                                else if (mDataBaseUtils
                                                                        .isNumberExist(NewNumber))
                                                                {
                                                                    BlackCallToast(R.string.black_number_exist);
                                                                }
                                                                else
                                                                {
                                                                    ContentValues cv = new ContentValues();
                                                                    cv.put("number",
                                                                            NewNumber);
                                                                    //get attach from other database.
                                                                    cv.put("attach",
                                                                            mDataBaseUtils
                                                                                    .GetAttach(NewNumber));

                                                                    long id = mDataBaseUtils
                                                                            .UpdateItem(
                                                                                    AhongBlackCallConst.BLACK_NUMBER_TABLE,
                                                                                    cv,
                                                                                    "number = ?",
                                                                                    new String[] { OldNumber });
                                                                    if (id < 0)
                                                                    {
                                                                        BlackCallToast(R.string.save_fail);
                                                                    }
                                                                    else
                                                                    {
                                                                        UpdateListView();
                                                                        BlackCallToast(R.string.save_success);
                                                                    }

                                                                    // CloseDataBase();
                                                                }
                                                            }

                                                        })
                                                .setNegativeButton(
                                                        getString(R.string.global_no),
                                                        new DialogInterface.OnClickListener()
                                                        {
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

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        menu.findItem(R.id.delete_multi).setVisible(
                mBlackListData.size() > 0 ? true : false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.listfragment, menu);
        menu.findItem(R.id.delete_multi).setVisible(
                mBlackListData.size() > 0 ? true : false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.delete_multi:
            {
                Intent intent = new Intent();
                intent.putExtra(AhongBlackCallConst.BLACK_NUMBER_TABLE,
                        mBlackListData);
                intent.setClass(mActivity, AhongBlackListMutiDel.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(intent);
                break;
            }
            case R.id.add_number:
            {
                LayoutInflater inflater = (LayoutInflater) mActivity
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View view = inflater
                        .inflate(R.layout.dlg_add_black, null);
                new AlertDialog.Builder(mActivity)
                        .setTitle(getString(R.string.add_black_title))
                        /*Begin: Modified by sunrise for ReplaceImg 2012/06/08*/
                        .setIcon(R.drawable.ic_menu_add_member)
                        /*End: Modified by sunrise for ReplaceImg 2012/06/08*/
                        .setView(view)
                        .setPositiveButton(R.string.global_ok,
                                new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog,
                                            int which)
                                    {
                                        EditText BlackNumber = (EditText) view
                                                .findViewById(R.id.et_black_number);
                                        String number = BlackNumber.getText()
                                                .toString().trim();

                                        // no number be input
                                        if (number.length() == 0)
                                        {
                                            BlackCallToast(R.string.input_black_number);
                                        }
                                        else if (mDataBaseUtils
                                                .isNumberExist(number))
                                        {
                                            BlackCallToast(R.string.black_number_exist);
                                        }
                                        else
                                        {
                                            long id = mDataBaseUtils
                                                    .AddBlackNumber(number);
                                            if (id < 0)
                                            {
                                                BlackCallToast(R.string.add_fail);
                                            }
                                            else
                                            {
                                                UpdateListView();
                                                BlackCallToast(R.string.add_success);
                                            }

                                            // CloseDataBase();
                                        }
                                    }

                                })
                        .setNegativeButton(getString(R.string.global_no),
                                new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog,
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

        return super.onOptionsItemSelected(item);
    }

    /**
     * set empty backGround if not item in listView
     * */
    private void SetBackGround()
    {
        int ItemCount = mBlackListData.size();
        if (ItemCount > 0)
        {
            /*Begin: Modified by sunrise for BlackListFine 2012/06/02*/
            // getActivity().getActionBar().setTitle(
            // getResources().getString(R.string.call_black_title)
            // + "-have add " + listCount);
            /*End: Modified by sunrise for BlackListFine 2012/06/02*/
            mListLinearLayout.setBackgroundDrawable(null);
            mEmptyView.setVisibility(View.GONE);

        }
        else
        {
            //            Drawable noContact = getResources().getDrawable(
            //                    R.drawable.nodata_bg);
            //            mListLinearLayout.setBackgroundDrawable(noContact);
            mListLinearLayout.setBackgroundDrawable(null);
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    private void initDataBase()
    {
        if (mBlackListData == null)
        {
            mBlackListData = new ArrayList<HashMap<String, String>>();
        }
        else
        {
            mBlackListData.clear();
        }

        if (mDataBaseUtils == null)
        {
            mDataBaseUtils = new AhongBlackCallDBUtils(mActivity);
        }

        mBlackListData = mDataBaseUtils
                .GetAllBlackNumber(AhongBlackCallConst.BLACK_NUMBER_TABLE);
    }

    /**
     * refresh black number listView
     * */
    public void UpdateListView()
    {
        initDataBase();
        mBlackListView.setAdapter(new AhongBlackCallListAdapter(mActivity,
                mBlackListData));
        SetBackGround();
    }

    /**
     * Hint message only for add black number to database.
     * */
    public void BlackCallToast(int resID)
    {
        Toast.makeText(mActivity, getString(resID), Toast.LENGTH_LONG).show();
    }
}
/* End: Modified by sunrise for BlackList 2012/06/01 */
