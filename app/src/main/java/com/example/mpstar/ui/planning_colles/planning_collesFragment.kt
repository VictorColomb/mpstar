package com.example.mpstar.ui.planning_colles

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
import androidx.viewpager.widget.ViewPager

import com.example.mpstar.R
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.*

class planning_collesFragment : Fragment() {

    //<editor-fold desc="Variables">
    private lateinit var adapter: TabAdapter
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private val c = Calendar.getInstance()
    private val year = c.get(Calendar.YEAR)
    private val month = c.get(Calendar.MONTH)
    private val day = c.get(Calendar.DAY_OF_MONTH)
    private val dt = SimpleDateFormat("dd/MM", Locale.US)
    private val dtmd = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    private val dayOfWeek = SimpleDateFormat("EEE", Locale.US)
    //</editor-fold>


    //<editor-fold desc="Load colles">
    private fun loadColles(selectedDate :Calendar) {
        Log.i("mpstar", "Would load colles of the week of "+dtmd.format(selectedDate.time))
    }
    //</editor-fold>


    //<editor-fold desc="Select date popup">
    @SuppressLint("SetTextI18n")
    private fun selectDate() {
        val selectedDateView = activity?.findViewById<TextView>(R.id.collesSelectedDate)
        val datePicker = DatePickerDialog(context!!, { _, syear, smonth, sday ->
            val selectedDate = Calendar.getInstance()
            val ssmonth = smonth+1
            selectedDate.time = dtmd.parse("$sday/$ssmonth/$syear")!!
            selectedDate.add(Calendar.DAY_OF_MONTH, 1)
            while (dayOfWeek.format(selectedDate.time) != "Mon") {
                selectedDate.add(Calendar.DAY_OF_MONTH, -1)
            }
            val selectedDateFri = selectedDate.clone() as Calendar
            selectedDateFri.add(Calendar.DAY_OF_MONTH, 5)
            selectedDateView?.text = "Semaine du "+dt.format(selectedDate.time)+" au "+dt.format(selectedDateFri.time)
            loadColles(selectedDate)
        }, year, month, day)
        datePicker.show()
    }
    //</editor-fold>


    //<editor-fold desc="onCreateView and onResume>
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInsatnceState: Bundle?): View? {
        Objects.requireNonNull<FragmentActivity>(activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        c.time = Date()
        c.add(Calendar.DAY_OF_MONTH, 1)
        while (dayOfWeek.format(c.time) != "Mon") {
            c.add(Calendar.DAY_OF_MONTH, -1)
        }
        return inflater.inflate(R.layout.fragment_planning_colles, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        val selectDateButton = activity!!.findViewById<Button>(R.id.collesSelectDateButton)
        selectDateButton.setOnClickListener{
            selectDate()
        }
        val collesSelectedDate = activity!!.findViewById<TextView>(R.id.collesSelectedDate)
        val cFri:Calendar = c.clone() as Calendar
        cFri.add(Calendar.DAY_OF_MONTH, 5)
        collesSelectedDate.text = "Semaine du "+dt.format(c.time)+" au "+dt.format(cFri.time)

        viewPager = activity!!.findViewById(R.id.colleViewPager)
        tabLayout = activity!!.findViewById(R.id.ColleLayout)
        adapter = TabAdapter(activity!!.supportFragmentManager)
        adapter.addFragment(Colle1Fragment(), "Colle 1")
        adapter.addFragment(Colle2Fragment(), "Colle 2")
        tabLayout.setupWithViewPager(viewPager)
    }
    //</editor-fold>
}
