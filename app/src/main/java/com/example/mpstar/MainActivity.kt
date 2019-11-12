package com.example.mpstar

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignInClient

import android.util.Log
import android.view.MenuItem
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI

import com.google.android.material.navigation.NavigationView
import com.jack.royer.kotlintest2.ui.read.ReadSpreadsheetActivity

import androidx.drawerlayout.widget.DrawerLayout

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

import android.view.Menu
import android.widget.TextView
import android.widget.Toast
import androidx.preference.ListPreference
import com.example.mpstar.model.Student
import com.example.mpstar.save.FilesIO
import java.io.File
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private var mAppBarConfiguration: AppBarConfiguration? = null
    private lateinit var readSpreadsheetActivity: ReadSpreadsheetActivity
    private lateinit var filesIO: FilesIO
    private lateinit var students: List<Student>

    private fun launchAuthentication(client: GoogleSignInClient) {
        Log.i("kotlin test","Lauching authenthication")
        startActivityForResult(client.signInIntent, ReadSpreadsheetActivity.RQ_GOOGLE_SIGN_IN)
        Log.i("kotlin test","finished authenthication")
    }

    private fun showRefreshed(){
        students = readSpreadsheetActivity.presenter.students.toList()
        filesIO.writeStudentList(students)
        showClassPlan()
    }

    private fun showClassPlan(){
        val r = resources
        for (student in students){
            val textView: TextView = findViewById(r.getIdentifier("seat" + student.myRow.toString() + student.myColumn.toString(), "id", packageName))
            textView.text = student.myName
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
                R.id.nav_plan_de_classe, R.id.nav_planning_ds, R.id.nav_planning_colles,
                R.id.nav_pokedex, R.id.nav_emploi_du_temps)
                .setDrawerLayout(drawer)
                .build()
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration!!)
        NavigationUI.setupWithNavController(navigationView, navController)


        //NOT NEEDED YET val model =ViewModelProviders.of(this)[MyViewModel::class.java]
        filesIO = FilesIO(this)
        readSpreadsheetActivity = ReadSpreadsheetActivity(::launchAuthentication, ::showRefreshed)

        //Display name in drawer
        val namePreference : SharedPreferences? = getSharedPreferences("myPreferences", 0)
        Log.i("mpstar","namePreference : "+ (namePreference?.getString("perso_name",null)))
        if (namePreference?.getString("perso_name",null) != "") {
            Log.i("mpstar", "Displaying name in drawer header")
        } else {
            Log.i("mpstar", "Name preference not set")
        }

    }

    fun resumePlan() {
        students = filesIO.readStudentList()
        showClassPlan()
    }

    override fun onResume() {
        super.onResume()
        resumePlan()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        return NavigationUI.navigateUp(navController, mAppBarConfiguration!!) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.action_refresh -> {
                // Refresh
                try {
                    readSpreadsheetActivity.init(this)
                }
                catch (ex : Exception){
                    Toast.makeText(applicationContext, "Refresh failed", Toast.LENGTH_SHORT).show()
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
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

    companion object{
        const val RQ_GOOGLE_SIGN_IN = 999
    }
}
