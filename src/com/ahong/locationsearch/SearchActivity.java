package com.ahong.locationsearch;

import android.app.ActionBar;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ahong.blackcall.R;

/* Begin: Modified by xiepengfei for search number address 2012/05/21 */

/**
 * added by xiepengfei 2012.5.21 for search number address
 */
public class SearchActivity extends Activity implements OnClickListener
{

    private EditText         numberEdit;
    private Button           sureButton;
    private TextView         numberText;
    private TextView         addressText;
    private TextView         typeText;
    private String           address;
    private static final int COLOR = android.graphics.Color.WHITE;

    /* Begin: Modified by xiepengfei for modify the actionbar 2012/05/21 */
    private ActionBar        mActionBar;

    /* End: Modified by xiepengfei for modify the actionbar 2012/05/21 */

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        numberEdit = (EditText) findViewById(R.id.et_search);
        sureButton = (Button) findViewById(R.id.btn_sure);
        numberText = (TextView) findViewById(R.id.tv_number);
        addressText = (TextView) findViewById(R.id.tv_address);
        typeText = (TextView) findViewById(R.id.tv_type);

        sureButton.setOnClickListener(this);

        getActionBar().setDisplayOptions(
                ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME
                        | ActionBar.DISPLAY_SHOW_TITLE);
        getActionBar().setTitle(NumberSearchActivity.title[0]);
        getActionBar().setIcon(NumberSearchActivity.icon[0]);
    }

    /*Begin: Modified by sunrise for BlackLauncher 2012/08/14*/
    @Override
    protected void onPause()
    {
        // TODO Auto-generated method stub
        finish();
        super.onPause();
    }
    /*End: Modified by sunrise for BlackLauncher 2012/08/14*/

    @Override
    public void onClick(View v)
    {
        String number = numberEdit.getText().toString();
        String areacode = getAreaCode(number);
        String address = getAddress(number);
        String type = getType(number);

        if (areacode == null
                || (number.trim().length() <= 3 && areacode.length() == 0)
                || address.length() == 0)
        {
            addressText.setText(null);
            typeText.setText(null);
            numberText.setText(null);
            /*Begin: Modified by sunrise for BlackListFine 2012/06/02*/
            Toast.makeText(this, getResources().getString(R.string.attach_no_match), 0).show();
            /*End: Modified by sunrise for BlackListFine 2012/06/02*/
            numberEdit.setText(null);
        }
        else
        {
            numberText.setText(number);
            addressText.setText(address + "(" + areacode + ")");
            typeText.setText(type);
            numberEdit.setText(null);
        }
    }

    private String getAddress(String number)
    {
        String result = null;
        Uri uri = Uri
                .parse("content://com.khong.provider.phonemanager/address/"
                        + number);
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            String city = cursor.getString(0);
            String province = cursor.getString(1);
            if (province.equals(city))
            {
                result = province;
            }
            else
            {
                result = province + city;
            }
        }
        else
        {
            result = null;
        }
        if (cursor != null)
            cursor.close();
        return result;
    }

    private String getType(String number)
    {
        String result = null;
        Uri uri = Uri.parse("content://com.khong.provider.phonemanager/type/"
                + number);
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            result = cursor.getString(0);
        }
        else
        {
            result = null;
        }
        if (cursor != null)
            cursor.close();
        return result;
    }

    private String getAreaCode(String number)
    {
        String result = null;
        Uri uri = Uri
                .parse("content://com.khong.provider.phonemanager/areacode/"
                        + number);
        //        Uri uri = Uri.parse("content://com.khong.provider.phonemanager/areacode/+8613590304512");

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            result = cursor.getString(0);
        }
        else
        {
            result = null;
        }
        if (cursor != null)
            cursor.close();
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
        }
        // TODO Auto-generated method stub
        return super.onOptionsItemSelected(item);
    }

}

/* End: Modified by xiepengfei for search number address 2012/05/21 */
