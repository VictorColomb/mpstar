package com.stan.mpstar.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.preference.*
import com.stan.mpstar.R
import com.stan.mpstar.save.FilesIO
import java.util.*

class SettingsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        Objects.requireNonNull<FragmentActivity>(activity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragmentInside())
                .commit()
        return root
    }

    class SettingsFragmentInside : PreferenceFragmentCompat() {
        private var filesIO: FilesIO? = null
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceManager.sharedPreferencesName = "mySharedPreferences"
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val preferences = Objects.requireNonNull<FragmentActivity>(activity).getSharedPreferences("mySharedPreferences", 0)
            filesIO = FilesIO(Objects.requireNonNull<Context>(context))
            val names: List<*> = filesIO!!.readNamesList()
            val namesCs = arrayOfNulls<CharSequence>(names.size)
            var index = 0
            for (name in names) {
                namesCs[index++] = name.toString()
            }
            val preferencePersoName = findPreference<ListPreference>("perso_name")
            if (preferencePersoName != null) {
                preferencePersoName.entries = namesCs
                preferencePersoName.entryValues = namesCs
            }
            val previouslySelectedName = preferences.getString("perso_name", null)
            if (previouslySelectedName != null && preferencePersoName != null) {
                preferencePersoName.value = previouslySelectedName
            }
            //disable individual notification settings if global notifications are disabled
            val preferenceNotificationsActivated = findPreference<SwitchPreference>("notifications_activated")
            val preferenceNotificationsColles = findPreference<CheckBoxPreference>("notifications_colles")
            val preferenceNotificationsBirthdays = findPreference<CheckBoxPreference>("notifications_birthdays")
            if (preferenceNotificationsActivated != null) {
                preferenceNotificationsActivated.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, _ ->
                    val checked = (preference as SwitchPreference).isChecked
                    if (checked) {
                        if (preferenceNotificationsBirthdays != null) preferenceNotificationsBirthdays.isChecked = false
                        if (preferenceNotificationsColles != null) preferenceNotificationsColles.isChecked = false
                    }
                    true
                }
            }
        }
    }
}