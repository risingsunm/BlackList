/* Begin: Modified by sunrise for BlackList 2012/06/01 */
package com.ahong.blackcall;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

/**
 * this file used to set the property of black number when incoming.
 **/
public class AhongBlackCallSetFragment extends PreferenceFragment implements
        OnPreferenceChangeListener, OnPreferenceClickListener
{
    SharedPreferences        m_SharedPref;
    SharedPreferences.Editor m_SharedPrefEdit;

    PreferenceScreen         m_BlackSetPrefScreen = null;
    ListPreference           m_ModeListPref       = null;
    ListPreference           m_RejectListPref     = null;
    ListPreference           m_NoticeListPref     = null;
    CheckBoxPreference       m_ServerCB           = null;

    private final String     SERVER_KEY           = "black_filter_server";
    private final String     MODE_KEY             = "call_black_filter_mode";
    private final String     REJECT_KEY           = "call_black_filter_reject";
    private final String     NOTICE_KEY           = "call_black_filter_notice";

    CharSequence[]           m_ModeSummaries;
    CharSequence[]           m_RejectSummaries;
    CharSequence[]           m_NoticeSummaries;
    CharSequence[]           m_ModeValues;
    CharSequence[]           m_RejectValues;
    CharSequence[]           m_NoticeValues;

    @SuppressWarnings("unused")
    private final String     SERVICE_ACTION       = "AhongBlackCallService";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.black_set_pref);

        m_BlackSetPrefScreen = getPreferenceScreen();
        m_SharedPref = getPreferenceManager().getSharedPreferences();

        m_ServerCB = (CheckBoxPreference) m_BlackSetPrefScreen
                .findPreference(SERVER_KEY);
        m_ModeListPref = (ListPreference) m_BlackSetPrefScreen
                .findPreference(MODE_KEY);
        m_RejectListPref = (ListPreference) m_BlackSetPrefScreen
                .findPreference(REJECT_KEY);
        m_NoticeListPref = (ListPreference) m_BlackSetPrefScreen
                .findPreference(NOTICE_KEY);

        m_ServerCB.setOnPreferenceClickListener(this);
        m_ModeListPref.setOnPreferenceChangeListener(this);
        m_RejectListPref.setOnPreferenceChangeListener(this);
        m_NoticeListPref.setOnPreferenceChangeListener(this);

        m_ModeSummaries = getResources().getTextArray(R.array.mode_list_disp);
        m_RejectSummaries = getResources().getTextArray(
                R.array.reject_list_disp);
        m_NoticeSummaries = getResources().getTextArray(
                R.array.notice_list_disp);
        m_ModeValues = getResources().getTextArray(R.array.mode_list_value);
        m_RejectValues = getResources().getTextArray(R.array.reject_list_value);
        m_NoticeValues = getResources().getTextArray(R.array.notice_list_value);

        initSetPrefScreenState(m_SharedPref);
    }

    /**
     * When start this screen/application, function below should initialize
     * first. This function should be execute after system boot up.
     * */
    private void initSetPrefScreenState(SharedPreferences sp)
    {
        // SharedPreferences.Editor spe = sp.edit();
        getActivity().getActionBar().setTitle(
                getResources().getString(R.string.call_black_title));
        int index = 0;

        index = Integer.parseInt(sp.getString(MODE_KEY, "0"));
        m_ModeListPref.setSummary(m_ModeSummaries[index].toString());

        index = Integer.parseInt(sp.getString(REJECT_KEY, "0"));
        m_RejectListPref.setSummary(m_RejectSummaries[index].toString());

        index = Integer.parseInt(sp.getString(NOTICE_KEY, "0"));
        m_NoticeListPref.setSummary(m_NoticeSummaries[index].toString());

        Boolean serverValue = sp.getBoolean(SERVER_KEY, false);
        if (serverValue)
        {
            m_BlackSetPrefScreen.setEnabled(true);
            RegisterCallBlackService();
        }
        else
        {
            UnregisterCallBlackService();
            m_ModeListPref.setEnabled(false);
            m_RejectListPref.setEnabled(false);
            m_NoticeListPref.setEnabled(false);
        }
    }

    // private void SetListPrefSummary()
    // {
    // if (key.equals(MODE_KEY))
    // {
    // index = Integer.parseInt(m_SharedPref.getString(MODE_KEY, "0"));
    // m_ModeListPref.setSummary(m_ModeSummaries[index].toString());
    //
    // }
    // }
    //
    @Override
    /**
     * update listPreference summary
     * */
    public boolean onPreferenceChange(Preference preference, Object newValue)
    {
        final String key = preference.getKey();
        final String value = newValue.toString();
        // int index = 0;
        //System.out.println(key + "--" + value);

        if (key.equals(MODE_KEY))
        {
            // index = Integer.parseInt(m_SharedPref.getString(MODE_KEY, "0"));
            // m_ModeListPref.setSummary(m_ModeSummaries[index].toString());

            for (int i = 0; i < m_ModeValues.length; i++)
            {
                if (value.equals(m_ModeValues[i].toString()))
                {
                    m_ModeListPref.setSummary(m_ModeSummaries[i].toString());
                    break;
                }
            }

        }
        else if (key.equals(REJECT_KEY))
        {
            for (int i = 0; i < m_RejectValues.length; i++)
            {
                if (value.equals(m_RejectValues[i].toString()))
                {
                    m_RejectListPref
                            .setSummary(m_RejectSummaries[i].toString());
                    break;
                }
            }

        }
        else if (key.equals(NOTICE_KEY))
        {
            for (int i = 0; i < m_NoticeValues.length; i++)
            {
                if (value.equals(m_RejectValues[i].toString()))
                {
                    m_NoticeListPref
                            .setSummary(m_NoticeSummaries[i].toString());
                    break;
                }
            }
        }

        return true;
    }

    /**
     * Register or unregister call black service based on CheckBoxPreference's
     * state.Register service if selected,and make the set screen enable. Else
     * unregister service and disable set screen
     * */
    @Override
    public boolean onPreferenceClick(Preference preference)
    {
        final String key = preference.getKey();
        System.out.println("click:" + key);
        if (key.equals(SERVER_KEY))
        {
            if (preference.getSharedPreferences().getBoolean(SERVER_KEY, false))
            {
                m_BlackSetPrefScreen.setEnabled(true);
                RegisterCallBlackService();
            }
            else
            {
                UnregisterCallBlackService();
                m_ModeListPref.setEnabled(false);
                m_RejectListPref.setEnabled(false);
                m_NoticeListPref.setEnabled(false);
            }

        }

        return false;
    }

    private void UnregisterCallBlackService()
    {
        // getActivity().stopService(new Intent(SERVICE_ACTION));
        getActivity().stopService(
                new Intent(getActivity(), AhongBlackCallService.class));
    }

    private void RegisterCallBlackService()
    {
        // getActivity().startService(new Intent(SERVICE_ACTION));
        getActivity().startService(
                new Intent(getActivity(), AhongBlackCallService.class));
    }

}
/* End: Modified by sunrise for BlackList 2012/06/01 */
