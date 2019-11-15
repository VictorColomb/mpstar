package com.example.mpstar

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Layout
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.DialogFragment
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.mpstar.model.Student
import com.example.mpstar.save.FilesIO
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.navigation.NavigationView
import com.jack.royer.kotlintest2.ui.read.ReadSpreadsheetActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.IllegalStateException

import androidx.drawerlayout.widget.DrawerLayout

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.preference.ListPreference
import com.example.mpstar.model.Personal
import com.example.mpstar.model.Student
import com.example.mpstar.save.FilesIO
import java.io.File
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var names :Array<String>

    private var mAppBarConfiguration: AppBarConfiguration? = null
    lateinit var readSpreadsheetActivity: ReadSpreadsheetActivity
    private lateinit var filesIO: FilesIO
    private lateinit var students: List<Student>
    private lateinit var preferences : SharedPreferences
    private lateinit var perso: Personal

    private fun launchAuthentication(client: GoogleSignInClient) {
        Log.i("kotlin test","Lauching authenthication")
        startActivityForResult(client.signInIntent, ReadSpreadsheetActivity.RQ_GOOGLE_SIGN_IN)
        Log.i("kotlin test","finished authenthication")
    }

    fun matchPersonal(personals: MutableList<Personal>){
        val preferences = getSharedPreferences("mySharedPreferences", 0)
        val my_name = preferences.getString("perso_name", "JEFF")
        for (personal in personals){
            if(personal.myName == my_name){
                perso = personal
                val td = Date()
                val duration : Long = td.time - perso.myBirthday.time
                Toast.makeText(this, TimeUnit.DAYS.convert(duration, TimeUnit.MILLISECONDS).toString(), Toast.LENGTH_SHORT).show()
                return
            }
        }
    }

    fun showRefreshed(){
        students = readSpreadsheetActivity.presenter.students.toList()
        filesIO.writeStudentList(students)
        showClassPlan()
    }

    private fun showClassPlan(){
        val r = resources
        val name = preferences.getString("perso_name", "JEFF")
        for (student in students){
            val textView: TextView = findViewById(r.getIdentifier("seat" + student.myRow.toString() + student.myColumn.toString(), "id", packageName))
            textView.text = student.myName
            if (student.myName == name) {
                textView.setBackgroundColor(Color.parseColor("#3f51b5"))
                textView.setTextColor(Color.parseColor("#ffffff"))
            }
        }
    }

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


        //NOT NEEDED YET val model =ViewModelProviders.of(this)[MyViewModel::class.java]
        filesIO = FilesIO(this)
        readSpreadsheetActivity = ReadSpreadsheetActivity(::launchAuthentication, ::showRefreshed, ::matchPersonal)
    }

    fun resumePlan() {
        students = filesIO.readStudentList()
        showClassPlan()
    }

    @SuppressLint("ApplySharedPref")
    private fun showPopup() {
        Log.i("mpstar", "Generating name selection dialog")
        var selected_name: String? = null
        names = filesIO.readNamesList().toTypedArray()
        val namesAdapter = ArrayAdapter(this, 0, names)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Nom").setSingleChoiceItems(names, -1) { _, which ->
            selected_name = names[which]
        }.setPositiveButton(R.string.ok) { _, _ ->
            val editor = preferences.edit()
            editor.putString("perso_name", selected_name)
            editor.putBoolean("perso_name_isset", true)
            editor.commit()
            Log.i("mpstar", "Name selection done. Name preference set to : $selected_name")
            resumePlan()
        }.setNeutralButton(R.string.not_again) { dialog, which ->
            val editor = preferences.edit()
            editor.putBoolean("perso_name_isset", true)
            editor.commit()
            Log.i("mpstar", "Name selection never again")
        }
        builder.create().show()
    }

    override fun onResume() {
        super.onResume()
        resumePlan()

        val nameSet = preferences.getBoolean("perso_name_isset", false)
        if (!nameSet) {
            showPopup()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        return NavigationUI.navigateUp(navController, mAppBarConfiguration!!) || super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("kotlin test", "$resultCode")
        if (requestCode == RQ_GOOGLE_SIGN_IN) {
            Log.i("kotlin test", "requested sign in")
            if (resultCode == Activity.RESULT_OK) {
                readSpreadsheetActivity.presenter.loginSuccessful(this)
                Log.i("kotlin test", "Login successful")
            } else {
                Log.i("kotlin test", "Login failed")
            }
        }
    }

    fun getThis() :Context {
        return this
    }

    companion object{
        const val RQ_GOOGLE_SIGN_IN = 999
    }
}
