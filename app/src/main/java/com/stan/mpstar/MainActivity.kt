@file:Suppress("DEPRECATION")

package com.stan.mpstar

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.TableLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.stan.mpstar.model.*
import com.stan.mpstar.read.ReadSpreadsheetPresenter
import com.stan.mpstar.save.FilesIO
import com.stan.mpstar.sheets.AuthenticationManager
import com.stan.mpstar.sheets.SheetsAPIDataSource
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {

    //<editor-fold desc="Variables">
    // name of all the students
    private lateinit var names :Array<String>

    // shit with drawer VICTOR
    private var mAppBarConfiguration: AppBarConfiguration? = null

    // class for reading and writing data to storage
    private lateinit var filesIO: FilesIO

    //class for interacting with google sheets
    private lateinit var presenter : ReadSpreadsheetPresenter

    // VICCCCCCCCCCCCCCCCCCCCCCCCCCTTTTTTTTTTTTTTTTTTTTOOOOOOOOOOOOOOOOOOOOOOR
    private lateinit var preferences : SharedPreferences
    private val timeFromPreference = mapOf("5h00" to 18000000, "5h15" to 18900000, "5h30" to 19800000, "5h45" to 20700000, "6h00" to 21600000, "6h15" to 22500000, "6h30" to 23400000, "6h45" to 24300000, "7h00" to 25200000, "7h15" to 26100000, "7h30" to 27000000, "7h45" to 27900000, "8h00" to 28800000, "8h15" to 29700000, "8h30" to 30600000, "8h45" to 31500000, "9h00" to 32400000, "9h15" to 33300000, "9h30" to 34200000, "9h45" to 35100000, "10h00" to 36000000, "10h15" to 36900000, "10h30" to 37800000, "10h45" to 38700000, "11h00" to 39600000, "11h15" to 40500000, "11h30" to 41400000, "11h45" to 42300000, "12h00" to 43200000)

    // list of all the students in the class
    private lateinit var students: List<Student>
    private var planCreatedOn :Date? = null

    // user's personal info
    private lateinit var perso: Personal

    // is user signed in
    var signedIn = false

    //date time
    private val dtmd = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    //</editor-fold>


    //<editor-fold desc="Authentication">
    private fun requestSignIn(requestCode: Int = RQ_GOOGLE_SIGN_IN) {
        presenter.startLogin(requestCode)
    }

    // Starts Login
    fun launchAuthentication(client: GoogleSignInClient, requestCode: Int) {
        startActivityForResult(client.signInIntent, requestCode)
    }

    // Called after the Login Popup has been closed
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RQ_GOOGLE_SIGN_IN -> {
                if (resultCode == Activity.RESULT_OK) {
                    presenter.loginSuccessful()
                    val file = File(filesDir,"mpStarPlan.dat")
                    if (!file.exists()) {
                        refreshAll()
                    }
                }
            }

            RQ_REFRESH_PLAN -> {
                if (resultCode == Activity.RESULT_OK) {
                    presenter.loginSuccessful()
                    try{
                        presenter.startReadingSpreadsheetStudents()
                    }
                    catch (e:Exception){
                        showError(e.toString())
                    }
                }
            }

            RQ_REFRESH_ALL -> {
                if (resultCode == Activity.RESULT_OK) {
                    presenter.loginSuccessful()
                    try{
                        presenter.startReadingSpreadsheetStudents()
                        presenter.startReadingSpreadsheetPersonal()
                        presenter.startReadingSpreadsheetDS()
                        presenter.startReadingSpreadsheetColleurs()
                        presenter.startReadingSpreadsheetColleM()
                        presenter.startReadingSpreadsheetColleA()
                        presenter.startReadingSpreadsheetEDT()
                    }
                    catch (e:Exception){
                        showError(e.toString())
                    }
                }
            }
        }
    }

    private fun initDependencies() {
        val signInOptions: GoogleSignInOptions =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        //.requestScopes(Scope(SheetsScopes.SPREADSHEETS_READONLY))
                        //.requestScopes(Scope(SheetsScopes.SPREADSHEETS))
                        //.requestScopes(Drive.SCOPE_FILE)
                        .requestEmail()
                        .build()
        val googleSignInClient = GoogleSignIn.getClient(this, signInOptions)
        val googleAccountCredential = GoogleAccountCredential
                .usingOAuth2(this, listOf(*AuthenticationManager.SCOPES))
                .setBackOff(ExponentialBackOff())
        val authManager =
                AuthenticationManager(
                        lazyOf(this),
                        googleSignInClient,
                        googleAccountCredential)
        val sheetsApiDataSource =
                SheetsAPIDataSource(authManager,
                        AndroidHttp.newCompatibleTransport(),
                        JacksonFactory.getDefaultInstance())
        presenter = ReadSpreadsheetPresenter(this, authManager, sheetsApiDataSource)

    }
    //</editor-fold>


    //<editor-fold desc="Create and Resume">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferences = getSharedPreferences("mySharedPreferences", 0)

        //UI shit
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = AppBarConfiguration.Builder(
                R.id.nav_plan_de_classe, R.id.nav_planning_ds, R.id.nav_planning_colles, R.id.nav_emploi_du_temps)
                .setDrawerLayout(drawer)
                .build()
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration!!)
        NavigationUI.setupWithNavController(navigationView, navController)

        // notifications
        createNotificationChannel()

        // initializes late-init classes
        initDependencies()
        filesIO = FilesIO(this)

        try{requestSignIn()}catch (ex:Exception){showError(ex.toString())}
    }

    override fun onStop() {
        prochainesColles()
        nextBDay()
        super.onStop()
    }

    private fun makeNotification(notificationTitle: String,notificationContent: String, notificationBigText: String, notificationTime: Long, notificationID : Int, navDestination: Int){
        val service = Intent(this, NotificationService::class.java)
        service.putExtra("Title", notificationTitle)
        service.putExtra("Content", notificationContent)
        service.putExtra("BigText", notificationBigText)
        service.putExtra("Time", notificationTime)
        service.putExtra("ID", notificationID)
        service.putExtra("NavDestination", navDestination)
        startService(service)
    }

    private fun nextBDay(){
        if (!preferences.getBoolean("notifications_birthdays", false)) {return}

        val personal = filesIO.readPersonalList()
        val notificationTimePreference = timeFromPreference[preferences.getString("notifications_time", "7h30")]

        // fetch personal data
        val namePreference = preferences.getString("perso_name", "HERVEEEEEEEEE")

        for (e in personal) {
            if (e.myBirthday.time >= Date().time-notificationTimePreference!!) {
                val timeUntilBday = dtmd.parse(dtmd.format(e.myBirthday))!!.time - Date().time + notificationTimePreference
                if (timeUntilBday < 2592000000) {
                    if (e.myName == namePreference) {
                        makeNotification("Joyeux anniversaire ${e.myName}!", "", "", timeUntilBday, e.myId+3, R.id.nav_plan_de_classe)
                    } else {
                        makeNotification("C'est l'anniversaire de ${e.myName} !", "", "", timeUntilBday, e.myId+3, R.id.nav_plan_de_classe)
                    }
                }
            }
        }
    }

    private fun timeToString(time :Int) :String{
        return if (time%2 == 0) {
            val timeString = time/2 + 8
            timeString.toString()+"h00"
        } else {
            val timeString = (time-1)/2 +8
            timeString.toString()+"h30"
        }
    }

    private fun prochainesColles() {
        if (!preferences.getBoolean("notifications_colles", false)) {return}

        //fetch colles data
        val colleurs = filesIO.readColleursList()
        val collesMaths = filesIO.readCollesMathsList()
        val collesAutre = filesIO.readCollesAutreList()
        val personalAll = filesIO.readPersonalList()

        // fetch personal data
        val namePreference = preferences.getString("perso_name", null)

        // gets list of user's colles
        val personal = personalAll[
                personalAll.map{personal ->  personal.myName}.indexOf(namePreference)
        ]

        val mesCollesMath = collesMaths[
                collesMaths.map { colles -> colles.myGroup }.indexOf(personal.myGroup)
        ]
        val mesCollesAutres = collesAutre[
                collesAutre.map { colles -> colles.myGroup }.indexOf(personal.myGroup)
        ]

        val c = GregorianCalendar.getInstance(Locale.FRANCE)
        c.time = Date()
        c.add(Calendar.DAY_OF_MONTH, 2)
        val today = c.get(Calendar.DAY_OF_WEEK)
        c.add(Calendar.DAY_OF_WEEK, -today + Calendar.MONDAY)
        val thisMonday = dtmd.parse(dtmd.format(c.time))

        if (mesCollesMath.myColles.containsKey(thisMonday!!)) {
            val maColleMaths = mesCollesMath.myColles[thisMonday]
            val maColleAutres = mesCollesAutres.myColles[thisMonday]

            val colleMathsData = colleurs[
                    colleurs.map { colleur -> colleur.myId }.indexOf(maColleMaths)
            ]
            val colleAutreData = colleurs[
                    colleurs.map { colleur -> colleur.myId }.indexOf(maColleAutres)
            ]

            val days = mapOf("Mon" to 0, "Tue" to 1, "Wed" to 2, "Thu" to 3, "Fri" to 4)

            val notificationTimePreference = timeFromPreference[preferences.getString("notifications_time", "7h30")]

            c.time = Date()
            c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            c.add(Calendar.DAY_OF_WEEK, days.getValue(colleMathsData.myDay))
            var myColleTime = dtmd.parse(dtmd.format(c.time))
            var timeUntilColle = myColleTime!!.time - Date().time + notificationTimePreference!!
            if (timeUntilColle > 0) {
                val content = "Colle avec " + colleMathsData.myName + " en " + colleMathsData.myPlace + " à " + timeToString(colleMathsData.myTime)
                makeNotification("Colle", "Colle de Maths aujourd'hui", content, timeUntilColle, 1, R.id.nav_planning_colles)
            }

            c.time = Date()
            c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            c.add(Calendar.DAY_OF_WEEK, days.getValue(colleAutreData.myDay))
            myColleTime = dtmd.parse(dtmd.format(c.time))
            timeUntilColle = myColleTime!!.time - Date().time + notificationTimePreference
            if (timeUntilColle > 0) {
                val content = "Colle avec " + colleAutreData.myName + " en " + colleAutreData.myPlace + " à " + timeToString(colleAutreData.myTime)
                makeNotification("Colle", "Colle de "+ colleAutreData.mySubject +" aujourd'hui", content, timeUntilColle, 2, R.id.nav_planning_colles)
            }
        }
    }
    //</editor-fold>


    //<editor-fold desc="Asynchronous">
    fun finishedReadingPersonal(){
        filesIO.writePersonalList(presenter.personal)
        matchPersonal(presenter.personal)
    }

    fun finishedReadingRed(notifs:MutableList<Notif>){
        for (notif in notifs){
            val timeLeft = notif.myTime.time - Date().time
            if(timeLeft > 0){
                makeNotification(notif.myTitle,notif.myTxt,notif.myTxt,timeLeft,notif.myId, R.id.nav_plan_de_classe)
            }
        }
    }

    fun finishedReadingColleurs(colleurs: MutableList<Colleurs>){
        filesIO.writeColleursList(colleurs)
    }

    fun finishedReadingDS(ds: MutableList<DS>){
        filesIO.writeDSList(ds)
    }

    fun finishedReadingStudents(){
        showRefreshed()
    }

    fun finishedReadingCollesM(sheet :List<List<Any>>){
        val df = SimpleDateFormat("MM/dd/yyyy", Locale.US)
        val collesM = mutableListOf<Colles>()
        for(i in 1 until sheet.size){
            val dict : MutableMap<Date, String> = HashMap()
            for (j in sheet[i].indices){
                dict[df.parse(sheet[j][0].toString())!!] = 'M'+sheet[j][i].toString()
            }
            collesM.add(Colles(i,dict.toMap()))
        }
        filesIO.writeCollesMathsList(collesM)
    }

    fun finishedReadingCollesA(sheet :List<List<Any>>){
        val df = SimpleDateFormat("MM/dd/yyyy", Locale.US)
        val collesA = mutableListOf<Colles>()
        for(i in 1 until sheet.size){
            val dict : MutableMap<Date, String> = HashMap()
            for (j in sheet[i].indices){
                dict[df.parse(sheet[j][0].toString())!!] = sheet[j][i].toString()
            }
            collesA.add(Colles(i,dict.toMap()))
        }
        filesIO.writeCollesAutreList(collesA)
    }

    fun finishedReadingEDT(sheet :List<List<Any>>){
        val monday = mutableMapOf<Int,String>()
        val tuesday = mutableMapOf<Int,String>()
        val wednesday = mutableMapOf<Int,String>()
        val thursday = mutableMapOf<Int,String>()
        val friday = mutableMapOf<Int,String>()

        for (j in sheet[0].indices){
            val myIndex = sheet[0][j].toString().toInt()
            monday[myIndex] = sheet[1][j].toString()
            tuesday[myIndex] = sheet[2][j].toString()
            wednesday[myIndex] = sheet[3][j].toString()
            thursday[myIndex] = sheet[4][j].toString()
            friday[myIndex] = sheet[5][j].toString()
        }

        filesIO.writeEDTList(EDT(monday,tuesday,wednesday,thursday,friday))
    }

    fun refreshFailed(error :Throwable) {
        Log.e("REFRESH FAILED", error.toString())
        val builder = AlertDialog.Builder(this)
        builder
                .setTitle(getString(R.string.refresh_error))
                .setMessage(getString(R.string.refresh_error_message))
                .setPositiveButton(getString(R.string.ok)) { _, _ ->}
        builder.create().show()
    }

    //</editor-fold>


    //<editor-fold desc="Modify User Interface">
    // adds the user welcome message
    private fun showWelcomeMessage(){
        val welcome = findViewById<TextView>(R.id.welcome_string)
        val tempString = "Salutations\n" + perso.myName
        welcome.text = tempString
    }

    // DEPRECATED
    private fun showRefreshed(){
        students = presenter.students.toList()
        planCreatedOn = presenter.planCreatedOn
        filesIO.writeStudentList(Pair(students,planCreatedOn))
        showClassPlan()
    }

    // shows class plan
    @SuppressLint("SetTextI18n")
    private fun showClassPlan(){
        val name = preferences.getString("perso_name", "JEFF")
        val nameSet = preferences.getBoolean("perso_name_isset", false)
        if (!nameSet) {
            showPopup()
        }
        for (student in students){
            val textView: TextView = findViewById(resources.getIdentifier("seat" + student.myRow.toString() + student.myColumn.toString(), "id", packageName))
            textView.text = student.myName
            if (student.myName == name) {
                textView.background = getDrawable(R.drawable.rounded_corner_inv)
                textView.setTextColor(Color.parseColor("#ffffff"))
            } else {
                textView.setTextColor(Color.parseColor("#3f51b5"))
                textView.background = getDrawable(R.drawable.rounded_corner)
            }
        }

        if (planCreatedOn != null) {
            val dt = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            val textView = findViewById<TextView>(R.id.pcd_createdOn)
            textView.text = "Plan de classe crée le "+dt.format(planCreatedOn!!)
        }
    }

    //</editor-fold>


    //<editor-fold desc="Notification">
    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= 26){
            Log.i("NOTIFICATION SERVICE", "Creating notification channel")
            val name = getString(R.string.channel_name)
            val desc = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = desc
            }
            val notificationManager : NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    //</editor-fold>


    //<editor-fold desc="Read Google Sheets">
    // Fetches Personal Info from List of all Info
    private fun matchPersonal(personals: MutableList<Personal>){
        val myName = preferences.getString("perso_name", "JEFF")
        for (personal in personals){
            if(personal.myName == myName){
                perso = personal
                showWelcomeMessage()
                return
            }
        }
    }


    // Refreshes everything
    fun refreshAll(){
        Log.e("SATAN", "ALL HELL IS ABOUT TO BREAK LOOSE")
        if (signedIn) {
            try {
                presenter.startReadingSpreadsheetStudents()
                presenter.startReadingSpreadsheetPersonal()
                presenter.startReadingSpreadsheetDS()
                presenter.startReadingSpreadsheetColleurs()
                presenter.startReadingSpreadsheetColleM()
                presenter.startReadingSpreadsheetColleA()
                presenter.startReadingSpreadsheetEDT()
            }
            catch(e: Exception){
                showError(e.toString())
            }
        }
        else {
            Log.i("mpstar", "Not signed in, attempting to log in...")
            requestSignIn(RQ_REFRESH_ALL)
        }
    }

    //</editor-fold>


    //<editor-fold desc="Enter your name popup">
    @SuppressLint("ApplySharedPref")
    private fun showPopup() {
        var selectedName: String? = null
        names = filesIO.readNamesList().toTypedArray()
        if (names.isNotEmpty()) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Nom").setSingleChoiceItems(names, -1) { _, which ->
                selectedName = names[which]
            }.setPositiveButton(R.string.ok) { _, _ ->
                if (selectedName != null) {
                    val editor = preferences.edit()
                    editor.putString("perso_name", selectedName)
                    editor.putBoolean("perso_name_isset", true)
                    editor.commit()
                    resumePlan()
                }
            }.setNeutralButton(R.string.not_again) { _, _ ->
                val editor = preferences.edit()
                editor.putBoolean("perso_name_isset", true)
                editor.commit()
            }
            builder.create().show()
        }
    }
    //</editor-fold>


    //<editor-fold desc="Share PDC">
    private fun getBitmapFromView(view :View) :Bitmap {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height,Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable!=null) {
            bgDrawable.draw(canvas)
        }   else{
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return returnedBitmap
    }

    @SuppressLint("SetWorldReadable", "InflateParams")
    fun sharePDC() {
        //plan de classe unhighlight cases
        val pdcCases = students.map {
            Pair(it.myRow,it.myColumn)
        }
        for (case in pdcCases) {
            val textView = findViewById<TextView>(resources.getIdentifier("seat${case.first}${case.second}", "id", packageName))
            textView.background = getDrawable(R.drawable.rounded_corner)
            textView.setTextColor(Color.parseColor("#3f51b5"))
        }

        val contentView = findViewById<TableLayout>(R.id.PCD_tableLayout)
        val bitmap = getBitmapFromView(contentView)
        try {
            if(Build.VERSION.SDK_INT>=24){
                try{
                    val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                    m.invoke(null)
                }catch(e :Exception){
                    Log.e("SHARE INTENT", e.toString())
                }
            }
            val file = File(this.externalCacheDir,"planDeClasse.png")
            val fOut = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
            fOut.flush()
            fOut.close()
            file.setReadable(true, false)
            val intent = Intent(Intent.ACTION_SEND)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
            intent.type = "image/png"
            startActivity(Intent.createChooser(intent, "Partager le plan de classe"))
        }
        catch (ex :Exception) {
            Log.e("SHARE INTENT", ex.toString())
        }

        showClassPlan()
    }
    //</editor-fold>


    fun resumePlan() {
        val studentList = filesIO.readStudentList()
        students = studentList.first
        planCreatedOn = studentList.second
        showClassPlan()
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        return NavigationUI.navigateUp(navController, mAppBarConfiguration!!) || super.onSupportNavigateUp()
    }


    //<editor-fold desc="Miscellaneous">
    //Shows the error
    private fun showError(error: String) {
        Log.e("ERROR MAIN", error)
    }

    companion object{
        const val CHANNEL_ID = "10001"
        const val RQ_GOOGLE_SIGN_IN = 999
        const val RQ_REFRESH_PLAN = 998
        const val RQ_REFRESH_ALL = 997
    }
    //</editor-fold>
}
