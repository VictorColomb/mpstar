package com.example.mpstar;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Switch;
import android.widget.TimePicker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.DialogPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //Search for current settings and en/disable individual settings
    }


    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            //disable individual notification settings if global notifications are disabled
            SwitchPreference preference_notifications_activated = findPreference("notifications_activated");
            final CheckBoxPreference preference_notifications_colles = findPreference("notifications_colles");
            final CheckBoxPreference preference_notifications_birthdays = findPreference("notifications_birthdays");
            if (preference_notifications_activated != null) {
                preference_notifications_activated.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object NewValue) {
                        boolean checked = ((SwitchPreference) preference).isChecked();
                        if (checked) {
                            if (preference_notifications_birthdays != null)
                                preference_notifications_birthdays.setChecked(false);
                            if (preference_notifications_colles != null)
                                preference_notifications_colles.setChecked(false);
                        }
                        return true;
                    }
                });
            }
        }
    }
}