package com.ahong.locationsearch;

/*Begin: Modified by sunrise for ConstantNumber 2012/06/12*/
import com.ahong.blackcall.R;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
/*End: Modified by sunrise for ConstantNumber 2012/06/12*/
/*Begin: Modified by sunrise for ConstantDialog 2012/07/23*/
import android.content.DialogInterface.OnClickListener;
/*End: Modified by sunrise for ConstantDialog 2012/07/23*/

/* Begin: Modified by xiepengfei for Constant Number Activity 2012/05/21 */
/**
 * added by xiepengfei 2012.5.21 for display contant number
 */
public class ConstantNumberActivity extends Activity
{

    private LayoutInflater  mLayoutInflater;

    private int             mode    = 1;

    private String[]        number1 = { "110", "112", "114", "119", "120",
            "122", "999", "12117", "12121", "12580", "118114", "4008517517",
            "4008823823", "4008123123" };

    private String[]        type1;
    private String[]        number2 = { "95566", "95588", "95533", "95555",
            "95559", "95599", "95561", "95568", "95501", "95595", "95508",
            "95528", "95558", "4006699999", "95577", "95580", "96588", "95516",
            "96198", "4008888400", "95511", "95500", "95509", "95519", "95589",
            "95522", "95567", "95515", "95596", "95105768", "95518", "95502",
            "95590", "95569", "95585", "95512" };

    private String[]        type2;
    private String[]        number3 = { "10086", "1008611", "13800138000",
            "10010", "10011", "10015", "116114", "10000", "10050", "10070" };

    private String[]        type3;
    private String[]        number4 = { "11185", "4008861888", "4008209868",
            "4008208388", "4008111111", "4008108000", "4006789000", "95105366" };

    private String[]        type4;
    private String[]        number5 = { "160", "12315", "11185", "12321",
            "12348", "95598", "12369", "12365", "12358", "12366", "12345",
            "12333", "12320", "12319", "12318", "12312", "96118", "96178",
            "96310", "96100", "96102", "12380" };

    private String[]        type5;
    private String[]        number6 = { "4008206666", "4006885588",
            "4007997997", "4006640066", "4006767038", "4006869999",
            "4008108080", "4008883721", "4008189588", "4008840086",
            "4008116666", "4007051766", "4008100999", "4008986999",
            "4006100666", "95530", "95808", "95539", "4006695539",
            "8689866709315", "8008768999", "86898950718", "4006096777",
            "10105858", "95080", "4008808666", "4008300999", "95557",
            "4006006222", "4006100099", "8008100099", "4008899737",
            "4006503538"           };
    private String[]        type6;
    private static String[] number;
    private String[]        type;

    private Intent          intent;
    private ListView        mListView;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.number_seacrch_activity);

        mLayoutInflater = this.getLayoutInflater();
        getActionBar().setDisplayOptions(
                ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME
                        | ActionBar.DISPLAY_SHOW_TITLE);
        intent = getIntent();
        mode = intent.getIntExtra("mode", 1);
        getActionBar().setTitle(NumberSearchActivity.title[mode]);
        getActionBar().setIcon(NumberSearchActivity.icon[mode]);

        /* Begin: Modified by xiepengfei for add some array 2012/05/26 */
        type1 = this.getResources().getStringArray(R.array.type1);
        type2 = this.getResources().getStringArray(R.array.type2);
        type3 = this.getResources().getStringArray(R.array.type3);
        type4 = this.getResources().getStringArray(R.array.type4);
        type5 = this.getResources().getStringArray(R.array.type5);
        type6 = this.getResources().getStringArray(R.array.type6);

        /* End: Modified by xiepengfei for add some array 2012/05/26 */

        switch (mode)
        {
            case 1:
                number = number1;
                type = type1;
                break;
            case 2:
                number = number2;
                type = type2;
                break;
            case 3:
                number = number3;
                type = type3;
                break;
            case 4:
                number = number4;
                type = type4;
                break;
            case 5:
                number = number5;
                type = type5;
                break;
            case 6:
                number = number6;
                type = type6;
                break;
            default:
                finish();
                return;
        }

        MyAdapter ada = new MyAdapter();
        mListView = (ListView) findViewById(R.id.listview);
        mListView.setAdapter(ada);

        /*Begin: Modified by sunrise for ConstantNumber 2012/06/12*/
        mListView.setOnItemClickListener(new OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
            {
                /*Begin: Modified by sunrise for ConstantDialog 2012/07/23*/
                new AlertDialog.Builder(ConstantNumberActivity.this)
                        .setTitle(
                                ((TextView) view.findViewById(R.id.textView1))
                                        .getText()
                                        + "("
                                        + ((TextView) view
                                                .findViewById(R.id.textView2))
                                                .getText() + ")")
                        .setItems(R.array.constant_number_options,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case 0:
                                                Uri DstNum = Uri.parse("tel:" + number[position]);
                                                Intent telIntent = new Intent(
                                                        Intent.ACTION_CALL, DstNum);
                                                startActivity(telIntent);

                                                break;
                                            case 1:
                                                Uri DstNum2 = Uri.parse("smsto:"
                                                        + number[position]);
                                                Intent telIntent2 = new Intent(
                                                        Intent.ACTION_SENDTO, DstNum2);
                                                startActivity(telIntent2);
                                                break;
                                        }
                                        System.out.println("which:" + which + ", position:"
                                                + position);
                                    }
                                })
                                .setNegativeButton(R.string.global_no, new OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        // TODO Auto-generated method stub
                                        dialog.dismiss();
                                    }
                                }).show();
                /*End: Modified by sunrise for ConstantDialog 2012/07/23*/
            }
        });
        /*End: Modified by sunrise for ConstantNumber 2012/06/12*/
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

    private class MyAdapter extends BaseAdapter
    {

        public int getCount()
        {
            // TODO Auto-generated method stub
            return number.length;
        }

        public Object getItem(int position)
        {
            // TODO Auto-generated method stub
            return position;
        }

        public long getItemId(int position)
        {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            View view = mLayoutInflater.inflate(
                    R.layout.constant_number_list_item, null);
            ((TextView) view.findViewById(R.id.textView1))
                    .setText(number[position]);
            ((TextView) view.findViewById(R.id.textView2))
                    .setText(type[position]);
            return view;
        }

    }

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
/* End: Modified by xiepengfei for Constant Number Activity 2012/05/21 */
