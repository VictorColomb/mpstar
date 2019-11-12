package com.example.mpstar;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.Log;
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

import com.example.mpstar.model.Student;
import com.example.mpstar.save.FilesIO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

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
    }


    public static class SettingsFragment extends PreferenceFragmentCompat {

        FilesIO filesIO;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            SharedPreferences pref = this.getActivity().getSharedPreferences("myPreferences", 0);
            final SharedPreferences.Editor pref_editor = pref.edit();

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

            //name from and to sharedPreferences
            filesIO = new FilesIO(getContext());
            List names = filesIO.readNamesList();
            CharSequence[] namesCs = new CharSequence[names.size()];
            int index = 0;
            for (Object name : names) {
                namesCs[index++] = name.toString();
            }

            final ListPreference preference_perso_name = findPreference("perso_name");
            if (preference_perso_name != null) {
                preference_perso_name.setEntries(namesCs);
                preference_perso_name.setEntryValues(namesCs);
                //preference_perso_name.setValue(pref.getString("perso_name", null));

                //FUCKING PROBLEM... sharedPreference is set to the preference's value before the change...........
                preference_perso_name.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (preference_perso_name.getValue() != "") {
                            pref_editor.putString("perso_name", preference_perso_name.getValue());
                            pref_editor.apply();
                            Log.i("mpstar_preferences", "SharedPreference perso_name set to : "+preference_perso_name.getValue());
                        }
                        return true;
                    }
                });
            }
        }
    }
}