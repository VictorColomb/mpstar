package com.example.mpstar.ui.emploi_du_temps

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mpstar.MainActivity

import com.example.mpstar.R
import com.example.mpstar.save.FilesIO
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class EmploiDuTempsFragment : Fragment() {

    //<editor-fold desc="Variables">
    private lateinit var filesIO :FilesIO

    private val c = Calendar.getInstance()
    private val year = c.get(Calendar.YEAR)
    private val month = c.get(Calendar.MONTH)
    private val day = c.get(Calendar.DAY_OF_MONTH)
    private val dt = SimpleDateFormat("yyyy/MM/dd", Locale.FRANCE)
    private val dtmd = SimpleDateFormat("dd/MM", Locale.FRANCE)
    private val dayOfWeek = SimpleDateFormat("EEE", Locale.US)
    private val weekOfYear = SimpleDateFormat("ww", Locale.US)
    //</editor-fold>


    //<editor-fold desc="Load timetable">
    private fun timeToString(time :Int) :String{
        return if (time%2 == 0) {
            val timeString = time/2 + 8
            timeString.toString()+"h00"
        } else {
            val timeString = (time-1)/2 +8
            timeString.toString()+"h30"
        }
    } // time in Int (0 -> 22) to time in String (8h00 -> 19h30)

    @SuppressLint("SetTextI18n")
    private fun loadTimetableDisplay(edtToday :Map<Int, String>, monday :Date, dayString :String) {
        // fetch personal info
        val preferences = activity!!.getSharedPreferences("mySharedPreferences", 0)
        val namePreference = preferences.getString("perso_name", null)
        val personal = filesIO.readPersonalList()
        var i=0
        while (i<personal.size && personal[i].myName != namePreference.toString()) {i+=1}
        val personalInfo = personal[i]

        // fetch colle info of the week
        val colleurs = filesIO.readColleursList()
        val collesMaths = filesIO.readCollesMathsList()
        i=0
        while (i<collesMaths.size && collesMaths[i].myGroup != personalInfo.myGroup) {i+=1}
        val colleMaths = collesMaths[i].myColles[monday]
        i=0
        while (i<colleurs.size && colleurs[i].myId != colleMaths) {i+=1}
        val colleMathsData = colleurs[i]
        val collesAutre = filesIO.readCollesAutreList()
        i=0
        while (i<collesAutre.size && collesAutre[i].myGroup != personalInfo.myGroup) {i+=1}
        val colleAutre = collesAutre[i].myColles[monday]
        i=0
        while (i<colleurs.size && colleurs[i].myId != colleAutre) {i+=1}
        val colleAutreData = colleurs[i]

        i=0                            // time counter
        var j=0                        // background color counter
        var begin: Int                 // lesson begin time
        var discipline :String?        // lesson discipline
        while (i<edtToday.size) {
            begin = i
            discipline = edtToday[i]
            while (edtToday[i] == discipline) {
                val textView = activity!!.findViewById<TextView>(resources.getIdentifier("edt_case$i", "id", activity!!.packageName))
                val rowView = activity!!.findViewById<TableRow>(resources.getIdentifier("edt_row$i", "id", activity!!.packageName))
                if (colleMathsData.myDay == dayString && colleMathsData.myTime == i) { // if colle maths
                    j+=1
                    textView.text = timeToString(i)+" - "+timeToString(i+2)+" : Colle "+colleMathsData.mySubject
                    if (j%2 == 0) {
                        rowView.setBackgroundColor(Color.parseColor("#eeeeee"))
                    } else {
                        rowView.setBackgroundColor(Color.parseColor("#e1e1e1"))
                    }
                    i+=1
                    val edtCaseColle2Text = activity!!.findViewById<TextView>(resources.getIdentifier("edt_case$i", "id", activity!!.packageName))
                    edtCaseColle2Text.text = " ("+colleMathsData.myName+ ", "+colleMathsData.myPlace+")"
                    val edtCaseColle2  = activity!!.findViewById<TableRow>(resources.getIdentifier("edt_row$i", "id", activity!!.packageName))
                    if (j%2 == 0) {
                        edtCaseColle2.setBackgroundColor(Color.parseColor("#eeeeee"))
                    } else {
                        edtCaseColle2.setBackgroundColor(Color.parseColor("#e1e1e1"))
                    }
                    i+=1
                    break
                }
                if (colleAutreData.myDay == dayString && colleAutreData.myTime == i) { // if colle autre
                    j+=1
                    textView.text = timeToString(i)+" - "+timeToString(i+2)+" : Colle "+colleAutreData.mySubject
                    if (j%2 == 0) {
                        rowView.setBackgroundColor(Color.parseColor("#eeeeee"))
                    } else {
                        rowView.setBackgroundColor(Color.parseColor("#e1e1e1"))
                    }
                    i+=1
                    val edtCaseColle2Text = activity!!.findViewById<TextView>(resources.getIdentifier("edt_case$i", "id", activity!!.packageName))
                    edtCaseColle2Text.text = " ("+colleAutreData.myName+ ", "+colleAutreData.myPlace+")"
                    val edtCaseColle2  = activity!!.findViewById<TableRow>(resources.getIdentifier("edt_row$i", "id", activity!!.packageName))
                    if (j%2 == 0) {
                        edtCaseColle2.setBackgroundColor(Color.parseColor("#eeeeee"))
                    } else {
                        edtCaseColle2.setBackgroundColor(Color.parseColor("#e1e1e1"))
                    }
                    i+=1
                    break
                }
                if (j%2 == 0) {
                    rowView.setBackgroundColor(Color.parseColor("#eeeeee"))
                } else {
                    rowView.setBackgroundColor(Color.parseColor("#e1e1e1"))
                }
                i+=1
            }
            j+=1 // change color for next discipline
            val edtCaseBegin = activity!!.findViewById<TextView>(resources.getIdentifier("edt_case$begin", "id", activity!!.packageName))
            val timeString = timeToString(begin)+" - "+timeToString(i)+" : "
            when (edtToday[begin]) {
                "NOTHING" -> {}
                "Option" -> {
                    edtCaseBegin.text = timeString+personalInfo.myOption
                }
                "TP Info" -> {
                    if (personalInfo.myOption == "Info" && weekOfYear.format(monday).toInt()%2 == personalInfo.myGroupInfo) {
                        edtCaseBegin.text = timeString+"TP Info"
                    }
                }
                "Langue vivante" -> {
                    edtCaseBegin.text = timeString+personalInfo.myLanguage
                }
                "TD1" -> {
                    if (personalInfo.myGroupTD == 1) {
                        edtCaseBegin.text = timeString+"TP/TD Physique"
                    } else {
                        edtCaseBegin.text = timeString+"TD Maths"
                    }
                }
                "TD2" -> {
                    if (personalInfo.myGroupTD == 0) {
                        edtCaseBegin.text = timeString+"TP/TD Physique"
                    } else {
                        edtCaseBegin.text = timeString+"TD Maths"
                    }
                }
                else -> {
                    edtCaseBegin.text = timeString+edtToday[begin]
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadTimetable(selectedDate :Date) {
        //clear out textViews
        var i=0
        while (i <= 22) {
            val textView = activity!!.findViewById<TextView>(resources.getIdentifier("edt_case$i", "id", activity!!.packageName))
            val rowView = activity!!.findViewById<TableRow>(resources.getIdentifier("edt_row$i", "id", activity!!.packageName))
            i+=1
            textView.text = getString(R.string.empty)
            rowView.setBackgroundColor(Color.parseColor("#fafafa"))
        }

        val edt = filesIO.readEdtList()
        Log.i("FUCK", edt!!.toString())
        val dsList = filesIO.readDSList()

        //get monday of the week
        val selectedDateMonday = Calendar.getInstance()
        selectedDateMonday.time = selectedDate
        selectedDateMonday.add(Calendar.DAY_OF_MONTH, 1)
        while (dayOfWeek.format(selectedDateMonday.time) != "Mon") {
            selectedDateMonday.add(Calendar.DAY_OF_MONTH, -1)
        }

        //get DS info of the week
        val selectedDateCalendarSat = Calendar.getInstance()
        selectedDateCalendarSat.time = selectedDate
        while (dayOfWeek.format(selectedDateCalendarSat.time) != "Sat") {
            selectedDateCalendarSat.add(Calendar.DAY_OF_MONTH, 1)
        }
        var j=0
        while (j<dsList.size && dsList[j].myDate != selectedDateCalendarSat.time) {j+=1}

        Log.i("Emploi du temps", "Samedi de la semaine : "+dt.format(selectedDateCalendarSat.time)+". C'est un "+dayOfWeek.format(selectedDateCalendarSat.time))

        val edtCase0 = activity!!.findViewById<TextView>(R.id.edt_case0)
        if (j == dsList.size || edt == null) {
            edtCase0.text = getString(R.string.edt_out_bounds)
        } else {
            val dsWeek = dsList[j]
            if(dsWeek.myDiscipline == "HOLIDAYS") {
                edtCase0.text = getString(R.string.edt_holidays)
                val edtCase1 = activity!!.findViewById<TextView>(R.id.edt_case1)
                edtCase1.text = getString(R.string.edt_holidays2)
            } else {
                when (dayOfWeek.format(selectedDate.time)) {
                    "Mon" -> { // monday
                        loadTimetableDisplay(edt.myMonday, selectedDateMonday.time, "Mon")
                    }
                    "Tue" -> { // tuesday
                        loadTimetableDisplay(edt.myTuesday, selectedDateMonday.time, "Tue")
                    }
                    "Wed" -> { // wednesday
                        loadTimetableDisplay(edt.myWednesday, selectedDateMonday.time, "Wed")
                    }
                    "Thu" -> { // thursday
                        loadTimetableDisplay(edt.myThursday, selectedDateMonday.time, "Thu")
                    }
                    "Fri" -> { // friday
                        loadTimetableDisplay(edt.myFriday, selectedDateMonday.time, "Fri")
                    }
                    "Sat" -> { // saturday
                        if (listOf("A","E","I","O","U").contains(dsWeek.myDiscipline[0].toString())) {
                            edtCase0.text = "Devoir d'"+dsWeek.myDiscipline+" ("+dsWeek.myDuration+")"
                        } else {
                            edtCase0.text = "Devoir de "+dsWeek.myDiscipline
                        }
                        val edtCase3 = activity!!.findViewById<TextView>(R.id.edt_case3)
                        if (dsWeek.mySecondDiscipline != "FALSE") {
                            if (listOf("A", "E", "I", "O", "U").contains(dsWeek.mySecondDiscipline[0].toString())) {
                                edtCase3.text = "Devoir d'" + dsWeek.mySecondDiscipline + " (" + dsWeek.mySecondDuration + ")"
                            } else {
                                edtCase3.text = "Devoir de " + dsWeek.mySecondDiscipline + " (" + dsWeek.mySecondDuration + ")"
                            }
                        }
                    }
                    else -> { // sunday
                        edtCase0.text = getString(R.string.edt_sunday)
                    }
                }
            }
        }
    }
    //</editor-fold>


    //<editor-fold desc="Select date popup">
    @SuppressLint("SetTextI18n")
    private fun selectDate() {
        val selectedDateView = activity?.findViewById<TextView>(R.id.selected_date)
        var selectedDate :Date?
        val datePicker = DatePickerDialog(context!!, { _, syear, smonth, sday ->
            val smonthCorr = smonth+1
            selectedDate = dt.parse("$syear/$smonthCorr/$sday")
            if (selectedDate != null) {
                Log.i("mpstar", "Selected date : "+dtmd.format(selectedDate)+". Generating timetable...")
                selectedDateView?.text = "Emploi du temps du "+dtmd.format(selectedDate)
                loadTimetable(selectedDate as Date)
            } else {
                Log.i("mpstar", "No date selected !")
            }
        }, year, month, day)
        datePicker.show()
    }
    //</editor-fold>


    //<editor-fold desc="onCreate, onHiddenChanged and onResume">
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInsatnceState: Bundle?): View? {
        setHasOptionsMenu(true)
        activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        filesIO = FilesIO(context!!)
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
        val monthCorr = (month+1).toString()
        selectedDateView?.text = "Emploi du temps du $day/$monthCorr"

        loadTimetable(dt.parse("$year/$monthCorr/$day"))
    }
    //</editor-fold>


    //<editor-fold desc="Menu items">
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_turn_screen, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_orientation_turnScreen -> {
                val orientation = resources.configuration.orientation
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                } else {
                    activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    //</editor-fold>

}
