package com.stan.mpstar.ui.planning_ds

import android.app.DatePickerDialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.stan.mpstar.R
import com.stan.mpstar.save.FilesIO
import java.text.SimpleDateFormat
import java.util.*

class PlanningDsFragment : Fragment() {

    //<editor-fold desc="Variables">
    private lateinit var filesIO: FilesIO

    private val c = Calendar.getInstance()
    private val year = c.get(Calendar.YEAR)
    private val month = c.get(Calendar.MONTH)
    private val day = c.get(Calendar.DAY_OF_MONTH)
    private val dt = SimpleDateFormat("dd/MM", Locale.US)
    private val dtmd = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    private val dayOfWeek = SimpleDateFormat("EEE", Locale.US)
    //</editor-fold>


    //<editor-fold desc="Load DS from date sent by the DatePicker">
    private fun loadPlanningDs(selectedDate :Calendar) {
        val dsList = filesIO.readDSList()
        var i=0
        while (i<dsList.size && dsList[i].myDate != selectedDate.time) {i+=1}
        val dsText1 = activity!!.findViewById<TextView>(R.id.dsText1)
        val dsText2 = activity!!.findViewById<TextView>(R.id.dsText2)
        val dsText3 = activity!!.findViewById<TextView>(R.id.dsText3)
        val dsText4 = activity!!.findViewById<TextView>(R.id.dsText4)
        dsText1.text = getString(R.string.empty)
        dsText2.text = getString(R.string.empty)
        dsText3.text = getString(R.string.empty)
        dsText4.text = getString(R.string.empty)
        if (dsList.isEmpty()) {
            dsText1.text = getString(R.string.data_error)
        }
        else if (i == dsList.size) { //if no ds found
            dsText1.text = getString(R.string.no_ds_found)
        } else { //if ds found
            val selectedDS = dsList[i]
            if (selectedDS.myDiscipline == "HOLIDAYS") {
                dsText1.text = getString(R.string.holidays)
            } else {
                if (listOf("A","E","I","O","U").contains(selectedDS.myDiscipline[0].toString())) {
                    val tempString = "Devoir d'"+selectedDS.myDiscipline
                    dsText1.text = tempString
                } else {
                    val tempString = "Devoir de "+selectedDS.myDiscipline
                    dsText1.text = tempString
                }
                val tempString2 = "Durée : "+selectedDS.myDuration
                dsText2.text = tempString2
                if (selectedDS.mySecondDiscipline != "FALSE") {
                    if (listOf("A","E","I","O","U").contains(selectedDS.mySecondDiscipline[0].toString())) {
                        val tempString ="Devoir d'"+selectedDS.mySecondDiscipline
                        dsText3.text = tempString
                    } else {
                        val tempString = "Devoir de "+selectedDS.mySecondDiscipline
                        dsText3.text = tempString
                    }
                    val tempString3 = "Durée : "+selectedDS.mySecondDuration
                    dsText4.text = tempString3
                }
            }
        }
    }
    //</editor-fold>


    //<editor-fold desc="DatePicker popup">
    private fun selectDate() {
        val selectedDateView = activity?.findViewById<TextView>(R.id.dsSelectedDate)
        val datePicker = DatePickerDialog(context!!, { _, syear, smonth, sday ->
            val selectedDate = Calendar.getInstance()
            val ssmonth = smonth+1
            selectedDate.time = dtmd.parse("$sday/$ssmonth/$syear")!!
            while (dayOfWeek.format(selectedDate.time) != "Sat") {
                selectedDate.add(Calendar.DAY_OF_MONTH, 1)
            }
            val tempString = "Devoir du "+dt.format(selectedDate.time)
            selectedDateView?.text = tempString
            loadPlanningDs(selectedDate)
        }, year, month, day)
        datePicker.show()
    }
    //</editor-fold>


    //<editor-fold desc="onCreateView and onResume">
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInsatnceState: Bundle?): View? {
        Objects.requireNonNull<FragmentActivity>(activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        c.time = Date()
        while (dayOfWeek.format(c.time) != "Sat") {
            c.add(Calendar.DAY_OF_MONTH, 1)
        }
        c.time = dtmd.parse(dtmd.format(c.time))!!

        filesIO = FilesIO(context!!)

        return inflater.inflate(R.layout.fragment_planning_ds, container, false)
    }

    override fun onResume() {
        super.onResume()

        val selectDateButton = activity!!.findViewById<Button>(R.id.dsSelectDateButton)
        selectDateButton.setOnClickListener{
            selectDate()
        }
        val collesSelectedDate = activity!!.findViewById<TextView>(R.id.dsSelectedDate)
        val tempString = "Devoir du "+dt.format(c.time)
        collesSelectedDate.text = tempString
        loadPlanningDs(c)
    }
    //</editor-fold>
}
