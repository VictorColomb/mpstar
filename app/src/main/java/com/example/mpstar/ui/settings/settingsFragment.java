package com.example.mpstar.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.example.mpstar.R;
import com.example.mpstar.save.FilesIO;

import java.util.List;
import java.util.Objects;

public class settingsFragment extends Fragment {


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragmentInside())
                .commit();

        return root;
    }



    public static class SettingsFragmentInside extends PreferenceFragmentCompat {

        FilesIO filesIO;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            getPreferenceManager().setSharedPreferencesName("mySharedPreferences");
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            SharedPreferences preferences = Objects.requireNonNull(getActivity()).getSharedPreferences("mySharedPreferences", 0);

            filesIO = new FilesIO(Objects.requireNonNull(getContext()));
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
            }
            String previously_selected_name = preferences.getString("perso_name", null);
            if (previously_selected_name != null && preference_perso_name != null) {
                preference_perso_name.setValue(previously_selected_name);
                Log.i("mpstar", "Preference name overwritten by SharedPreferences : "+previously_selected_name);
            }

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