package com.example.mpstar.ui.emploi_du_temps

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mpstar.MainActivity
import com.example.mpstar.NetworkInformation

import com.example.mpstar.R
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class EmploiDuTempsFragment : Fragment() {

    private val c = Calendar.getInstance()
    private val year = c.get(Calendar.YEAR)
    private val month = c.get(Calendar.MONTH)
    private val day = c.get(Calendar.DAY_OF_MONTH)
    private val dt = SimpleDateFormat("yyyy/mm/dd", Locale.FRANCE)
    private val dtmd = SimpleDateFormat("dd/mm", Locale.FRANCE)

    private fun loadTimetable() {

    }

    @SuppressLint("SetTextI18n")
    private fun selectDate() {
        val selectedDateView = activity?.findViewById<TextView>(R.id.selected_date)
        var selectedDate :Date?
        val datePicker = DatePickerDialog(context!!, { _, syear, smonth, sday ->
            selectedDate = dt.parse("$syear/$smonth/$sday")
            if (selectedDate != null) {
                Log.i("mpstar", "Selected date : "+dtmd.format(selectedDate)+". Generating timetable...")
                selectedDateView?.text = "Emploi du temps du "+dtmd.format(selectedDate)
                loadTimetable()
            } else {
                Log.i("mpstar", "No date selected !")
            }
        }, year, month, day)
        datePicker.show()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInsatnceState: Bundle?): View? {
        setHasOptionsMenu(true)
        activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        return inflater.inflate(R.layout.fragment_emploi_du_temps, container, false)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden and isResumed){
            val mainActivity: MainActivity = this.activity as MainActivity
            mainActivity.resumePlan()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        val selectDateButton = activity!!.findViewById<Button>(R.id.selectDateButton)
        selectDateButton.setOnClickListener{
            selectDate()
        }

        val selectedDateView = activity?.findViewById<TextView>(R.id.selected_date)
        selectedDateView?.text = "Emploi du temps du $day/$month"

        loadTimetable()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_plan_de_classe, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_orientation -> {
                val orientation = resources.configuration.orientation
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                } else {
                    activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                }
                return true
            }
            R.id.action_refresh -> {
                if (NetworkInformation.isNetworkAvailable(context)) {
                    val mainActivity: MainActivity = this.activity as MainActivity
                    try {
                        mainActivity.requestSignIn()
                    } catch (ex :Exception) {
                        Toast.makeText(context, "Refresh failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Pas de connection à internet...", Toast.LENGTH_SHORT).show()
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

}
