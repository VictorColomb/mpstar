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
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.navigation.NavigationView
import com.jack.royer.kotlintest2.ui.read.ReadSpreadsheetActivity

class MainActivity : AppCompatActivity() {

    // name of all the students
    private lateinit var names :Array<String>

    private var mAppBarConfiguration: AppBarConfiguration? = null
    lateinit var readSpreadsheetActivity: ReadSpreadsheetActivity
    private lateinit var filesIO: FilesIO
    private lateinit var students: List<Student>
    private lateinit var preferences : SharedPreferences
    private lateinit var perso: Personal

    // is user signed in
    var signedIn = false


    //<editor-fold desc="Authentication">
    // Initiates Login process
    fun requestSignIn(){

    }

    // Starts Login
    private fun launchAuthentication(client: GoogleSignInClient) {
        Log.i("mpstar", "Authenticating...")
        startActivityForResult(client.signInIntent, RQ_GOOGLE_SIGN_IN)
    }

    // Called after the Login Popup has been closed
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RQ_GOOGLE_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {

            }
        }
    }

    fun refreshAll() {
        //fucking code that shit...
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


        filesIO = FilesIO(this)
        readSpreadsheetActivity = ReadSpreadsheetActivity(::launchAuthentication, ::showRefreshed, ::matchPersonal)
    }

    override fun onResume() {
        super.onResume()
        resumePlan()
    }
    //</editor-fold>


    //<editor-fold desc="Modify User Interface">
    // adds the user welcome message
    private fun showWelcomeMessage(){
        val welcome = findViewById<TextView>(R.id.welcome_string)
        val tempString = "Salutations\n" + perso.myName
        welcome.text = tempString
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
        if (signedIn){
            try {
                readSpreadsheetActivity.presenter.loginSuccessful(this)
            }
            catch(e: Exception){
                Log.e("ERROR REFRESH PLAN", e.toString())
            }
        }
        else{
            Toast.makeText(this, "Chacal commence par Sign In", Toast.LENGTH_LONG).show()
        }
    }
    //</editor-fold>


    //<editor-fold desc="Write Google Sheets"

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


    private fun showRefreshed(){
        students = readSpreadsheetActivity.presenter.students.toList()
        filesIO.writeStudentList(students)
        showClassPlan()
    }

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

    companion object{
        const val RQ_GOOGLE_SIGN_IN = 999
    }
}
