package com.example.mpstar.ui.planning_ds

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

import com.example.mpstar.R
import com.example.mpstar.ui.planning_colles.Colle1Fragment
import com.example.mpstar.ui.planning_colles.Colle2Fragment
import com.example.mpstar.ui.planning_colles.TabAdapter
import java.text.SimpleDateFormat
import java.util.*

class planning_dsFragment : Fragment() {

    private val c = Calendar.getInstance()
    private val year = c.get(Calendar.YEAR)
    private val month = c.get(Calendar.MONTH)
    private val day = c.get(Calendar.DAY_OF_MONTH)
    private val dt = SimpleDateFormat("dd/MM", Locale.US)
    private val dtmd = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    private val dayOfWeek = SimpleDateFormat("EEE", Locale.US)

    fun loadPlanningDs(selectDate :Calendar) {
        Log.i("mpstar", "Would load planning de colles for "+dtmd.format(selectDate.time))
    }

    @SuppressLint("SetTextI18n")
    private fun selectDate() {
        val selectedDateView = activity?.findViewById<TextView>(R.id.dsSelectedDate)
        val datePicker = DatePickerDialog(context!!, { _, syear, smonth, sday ->
            val selectedDate = Calendar.getInstance()
            val ssmonth = smonth+1
            selectedDate.time = dtmd.parse("$sday/$ssmonth/$syear")!!
            while (dayOfWeek.format(selectedDate.time) != "Sat") {
                selectedDate.add(Calendar.DAY_OF_MONTH, 1)
            }
            selectedDateView?.text = "Devoir du "+dt.format(selectedDate.time)
            loadPlanningDs(selectedDate)
        }, year, month, day)
        datePicker.show()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInsatnceState: Bundle?): View? {
        Objects.requireNonNull<FragmentActivity>(activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        c.time = Date()
        while (dayOfWeek.format(c.time) != "Sat") {
            c.add(Calendar.DAY_OF_MONTH, 1)
        }

        return inflater.inflate(R.layout.fragment_planning_ds, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        val selectDateButton = activity!!.findViewById<Button>(R.id.dsSelectDateButton)
        selectDateButton.setOnClickListener{
            selectDate()
        }
        val collesSelectedDate = activity!!.findViewById<TextView>(R.id.dsSelectedDate)
        collesSelectedDate.text = "Devoir du "+dt.format(c.time)


    }
}
