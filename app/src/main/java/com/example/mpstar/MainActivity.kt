package com.example.mpstar

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.mpstar.model.Personal
import com.example.mpstar.model.Student
import com.example.mpstar.save.FilesIO
import com.example.mpstar.sheets.AuthenticationManager
import com.example.mpstar.sheets.SheetsAPIDataSource
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.jack.royer.kotlintest2.ui.read.ReadSpreadsheetPresenter

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

    // list of all the students in the class
    private lateinit var students: List<Student>

    // user's personal info
    private lateinit var perso: Personal

    // is user signed in
    var signedIn = false
    //</editor-fold>


    //<editor-fold desc="Authentication">
    fun requestSignIn(requestCode: Int = RQ_GOOGLE_SIGN_IN) {
        presenter.startLogin(requestCode)
    }

    // Starts Login
    fun launchAuthentication(client: GoogleSignInClient, requestCode: Int) {
        Log.i("INFORMATION MAIN", "Beginning Authentication")
        startActivityForResult(client.signInIntent, requestCode)
    }

    // Called after the Login Popup has been closed
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RQ_GOOGLE_SIGN_IN -> {
                if (resultCode == Activity.RESULT_OK) {
                    presenter.loginSuccessful()
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

        // initializes late-init classes
        initDependencies()
        filesIO = FilesIO(this)

        try{requestSignIn()}catch (ex:Exception){showError(ex.toString())}
    }
    //</editor-fold>


    //<editor-fold desc="Asynchronous">
    fun finishedReadingPersonal(){

    }

    fun finishedReadingStudents(){
        Log.i("INFORMATION MAIN", "Calling showRefreshed()")
        showRefreshed()
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
        filesIO.writeStudentList(students)
        showClassPlan()
    }

    // shows class plan
    private fun showClassPlan(){
        val name = preferences.getString("perso_name", "JEFF")
        for (student in students){
            val textView: TextView = findViewById(resources.getIdentifier("seat" + student.myRow.toString() + student.myColumn.toString(), "id", packageName))
            textView.text = student.myName
            if (student.myName == name) {
                textView.setBackgroundColor(Color.parseColor("#3f51b5"))
                textView.setTextColor(Color.parseColor("#ffffff"))
            }
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
                return
            }
        }
    }

    // Refresh's the Class Plan
    fun refreshPlan(){
        Log.i("MAIN REFRESH", "Attempting to refresh plan")
        if (signedIn){
            try {
                presenter.startReadingSpreadsheetStudents()
            }
            catch(e: Exception){
                showError(e.toString())
            }
        }
        else{
            Toast.makeText(this, "Attempting to Log In...", Toast.LENGTH_LONG).show()
            requestSignIn(RQ_REFRESH_PLAN)
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
                    Log.i("mpstar", "Setting name preference to : $selectedName")
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


    fun resumePlan() {
        students = filesIO.readStudentList()
        showClassPlan()
        val nameSet = preferences.getBoolean("perso_name_isset", false)
        if (!nameSet) {
            showPopup()
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        return NavigationUI.navigateUp(navController, mAppBarConfiguration!!) || super.onSupportNavigateUp()
    }


    //<editor-fold desc="Miscellaneous">
    //Shows the error
    fun showError(error: String) {
        Log.e("ERROR MAIN", error)
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    companion object{
        const val RQ_GOOGLE_SIGN_IN = 999
        const val RQ_REFRESH_PLAN = 998
        const val RQ_REFRESH_ALL = 997
    }
    //</editor-fold>
}
