package com.stan.mpstar.ui.planning_colles

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
import com.stan.mpstar.R
import com.stan.mpstar.model.Personal
import com.stan.mpstar.save.FilesIO
import java.text.SimpleDateFormat
import java.util.*

class planning_collesFragment : Fragment() {

    //<editor-fold desc="Variables">
    private lateinit var filesIO : FilesIO
    private var personal : Personal? = null
    private val c = Calendar.getInstance()
    private val year = c.get(Calendar.YEAR)
    private val month = c.get(Calendar.MONTH)
    private val day = c.get(Calendar.DAY_OF_MONTH)
    private val dt = SimpleDateFormat("dd/MM", Locale.US)
    private val dtmd = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    private val dayOfWeek = SimpleDateFormat("EEE", Locale.US)
    //</editor-fold>


    //<editor-fold desc="Load colles">
    private fun timeToString(time :Int) :String{
        return if (time%2 == 0) {
            val timeString = time/2 + 8
            "${timeString}h00"
        } else {
            val timeString = (time-1)/2 +8
            "${timeString}h30"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadColles(selectedDate :Date) {
        Log.i("mpstar", "Loading colles of the week of "+dtmd.format(selectedDate))
        val colleDisplay1 = activity!!.findViewById<TextView>(R.id.colles_display1)
        val colleDisplay2 = activity!!.findViewById<TextView>(R.id.colles_display2)
        val colleDisplay3 = activity!!.findViewById<TextView>(R.id.colles_display3)
        val colleDisplay4 = activity!!.findViewById<TextView>(R.id.colles_display4)
        val colleDisplay5 = activity!!.findViewById<TextView>(R.id.colles_display5)
        val colleDisplay6 = activity!!.findViewById<TextView>(R.id.colles_display6)

        //fetch colles data
        val colleurs = filesIO.readColleursList()
        val collesMaths = filesIO.readCollesMathsList()
        val collesAutre = filesIO.readCollesAutreList()
        if (colleurs.isEmpty() || collesMaths.isEmpty() || collesAutre.isEmpty()) {
            colleDisplay1.text = getString(R.string.data_error)
            colleDisplay2.text = getString(R.string.empty)
            colleDisplay3.text = getString(R.string.empty)
            colleDisplay4.text = getString(R.string.empty)
            colleDisplay5.text = getString(R.string.empty)
            colleDisplay6.text = getString(R.string.empty)
        }
        else {

            var i=0
            while (i<collesMaths.size && collesMaths[i].myGroup != personal!!.myGroup) {i+=1}
            if (collesMaths[i].myColles.containsKey(selectedDate)) {
                val colleMaths = collesMaths[i].myColles[selectedDate]
                i=0
                while (i<colleurs.size && colleurs[i].myId != colleMaths) {i+=1}
                val colleMathsData = colleurs[i]
                i=0
                while (i<collesAutre.size && collesAutre[i].myGroup != personal!!.myGroup) {i+=1}
                val colleAutre = collesAutre[i].myColles[selectedDate]
                i=0
                while (i<colleurs.size && colleurs[i].myId != colleAutre) {i+=1}
                val colleAutreData = colleurs[i]

                val days = mapOf("Mon" to "Lundi", "Tue" to "Mardi", "Wed" to "Mercredi", "Thu" to "Jeudi", "Fri" to "Vendredi")

                colleDisplay1.text = "Khôlle "+colleMathsData.mySubject
                colleDisplay2.text = days[colleMathsData.myDay] + " | " + timeToString(colleMathsData.myTime) + " - " + timeToString(colleMathsData.myTime + 2)
                colleDisplay3.text = colleMathsData.myName + " (" + colleMathsData.myPlace + ")"
                colleDisplay4.text = "Khôlle "+colleAutreData.mySubject
                colleDisplay5.text = days[colleAutreData.myDay] + " | " + timeToString(colleAutreData.myTime) + " - " + timeToString(colleAutreData.myTime + 2)
                colleDisplay6.text = colleAutreData.myName + " ("+colleAutreData.myPlace + ")"
            } else {
                colleDisplay1.text = getString(R.string.pas_colles)
                colleDisplay2.text = getString(R.string.empty)
                colleDisplay3.text = getString(R.string.empty)
                colleDisplay4.text = getString(R.string.empty)
                colleDisplay5.text = getString(R.string.empty)
                colleDisplay6.text = getString(R.string.empty)
            }
        }

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
            loadColles(selectedDate.time)
        }, year, month, day)
        datePicker.show()
    }
    //</editor-fold>


    //<editor-fold desc="onCreateView and onResume">
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInsatnceState: Bundle?): View? {
        Objects.requireNonNull<FragmentActivity>(activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        c.time = Date()
        c.add(Calendar.DAY_OF_MONTH, 1)
        while (dayOfWeek.format(c.time) != "Mon") {
            c.add(Calendar.DAY_OF_MONTH, -1)
        }

        filesIO = FilesIO(context!!)

        // fetch personal data
        val preferences = activity!!.getSharedPreferences("mySharedPreferences", 0)
        val namePreference = preferences.getString("perso_name", null)
        val personalAll = filesIO.readPersonalList()
        var i=0
        while (i < personalAll.size && personalAll[i].myName != namePreference) {i+=1}
        if (i < personalAll.size) {personal = personalAll[i]}

        return inflater.inflate(R.layout.fragment_planning_colles, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        if (personal != null) {
            val groupeColleTextview = activity!!.findViewById<TextView>(R.id.groupe_de_colle)
            groupeColleTextview.text = personal!!.myGroup.toString()
        }

        val selectDateButton = activity!!.findViewById<Button>(R.id.collesSelectDateButton)
        selectDateButton.setOnClickListener{
            selectDate()
        }
        val collesSelectedDate = activity!!.findViewById<TextView>(R.id.collesSelectedDate)
        val cFri:Calendar = c.clone() as Calendar
        cFri.add(Calendar.DAY_OF_MONTH, 5)
        collesSelectedDate.text = "Semaine du "+dt.format(c.time)+" au "+dt.format(cFri.time)
        loadColles(dtmd.parse(dtmd.format(c.time))!!)
    }
    //</editor-fold>
}
