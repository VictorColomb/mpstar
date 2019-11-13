package com.example.mpstar

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.InetAddresses
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.text.Layout
import com.google.android.gms.auth.api.signin.GoogleSignInClient

import android.util.Log
import android.view.*
import android.widget.LinearLayout
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI

import com.google.android.material.navigation.NavigationView
import com.jack.royer.kotlintest2.ui.read.ReadSpreadsheetActivity

import androidx.drawerlayout.widget.DrawerLayout

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.preference.ListPreference
import com.example.mpstar.model.Student
import com.example.mpstar.save.FilesIO
import java.io.File
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private var mAppBarConfiguration: AppBarConfiguration? = null
    lateinit var readSpreadsheetActivity: ReadSpreadsheetActivity
    private lateinit var filesIO: FilesIO
    private lateinit var students: List<Student>

    private fun launchAuthentication(client: GoogleSignInClient) {
        Log.i("kotlin test","Lauching authenthication")
        startActivityForResult(client.signInIntent, ReadSpreadsheetActivity.RQ_GOOGLE_SIGN_IN)
        Log.i("kotlin test","finished authenthication")
    }

    fun showRefreshed(){
        students = readSpreadsheetActivity.presenter.students.toList()
        filesIO.writeStudentList(students)
        showClassPlan()
    }

    private fun showClassPlan(){
        val r = resources
        val preferences = getSharedPreferences("mySharedPreferences", 0)
        val my_name = preferences.getString("perso_name", "JEFF")
        for (student in students){
            val textView: TextView = findViewById(r.getIdentifier("seat" + student.myRow.toString() + student.myColumn.toString(), "id", packageName))
            textView.text = student.myName
            if(student.myName == my_name){
                textView.setBackgroundColor(Color.parseColor("#3f51b5"))
                textView.setTextColor(Color.parseColor("#ffffff"))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        readSpreadsheetActivity = ReadSpreadsheetActivity(::launchAuthentication, ::showRefreshed)
    }

    fun resumePlan() {
        students = filesIO.readStudentList()
        showClassPlan()
    }

    override fun onResume() {
        super.onResume()
        resumePlan()
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
                readSpreadsheetActivity!!.presenter.loginSuccessful(this)
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
