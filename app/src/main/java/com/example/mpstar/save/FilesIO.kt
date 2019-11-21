package com.example.mpstar.save

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.mpstar.model.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.jack.royer.kotlintest2.ui.read.ReadSpreadsheetActivity
import org.json.JSONException
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*

class FilesIO(
        val context: Context
){

    //<editor-fold desc="Read and Write Student data">
    fun writeStudentList(students: List<Student>){
        Log.i("mpstar", "Writing student list to save file")
        try {
            val fileContents = serializer(students)

            // Writes parsed data to internal storage
            try {
                val fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE)
                val outputStreamWriter = OutputStreamWriter(fileOutputStream)
                outputStreamWriter.write(fileContents)
                outputStreamWriter.flush()
                outputStreamWriter.close()
            }
            catch (ex: Exception){
                Toast.makeText(context,"Error Saving Data", Toast.LENGTH_SHORT).show()
                Log.e("FILE ERROR", ex.toString())
            }
        }
        catch (ex: JSONException){
            Toast.makeText(context,"Error JSON", Toast.LENGTH_SHORT).show()
            Log.e("JSON ERROR", ex.toString())
        }
    }

    fun readStudentList() : List<Student>{
        try {
            val stringBuffer = StringBuffer("")

            // Reads parsed data from internal storage
            val fileInputStream = context.openFileInput(filename)
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)

            var readString = bufferedReader.readLine()

            while(readString != null){
                stringBuffer.append(readString)
                readString = bufferedReader.readLine()
            }

            val fileContents = stringBuffer.toString()


            fileInputStream.close()
            try {
                return deserializer(fileContents)
            }
            catch (ex : Exception){
                Toast.makeText(context,"Error JSON", Toast.LENGTH_SHORT).show()
                Log.e("JSON ERROR", ex.toString())
            }
        }
        catch (ex : Exception)
        {
            Toast.makeText(context,"Error Reading Data", Toast.LENGTH_SHORT).show()
            Log.e("FILE ERROR", ex.toString())
        }
        return listOf()
    }

    fun readNamesList() :List<String>{
        val studentsTemp = readStudentList()
        return studentsTemp.map {
            it.myName
        }
    }
    //</editor-fold>


    //<editor-fold desc="Read and Write DS data">
    fun readDSList() :List<DS>{ // Read DS list from file
        // FOR TESTING PURPOSES
        val dt = SimpleDateFormat("yyyy/MM/dd", Locale.US)
        return listOf(DS(dt.parse("2019/11/23")!!, "Option", "4h", "FALSE", "0"))
    }
    //</editor-fold>


    //<editor-fold desc="Read and Write EDT data">
    fun readEdtList() : EDT? { // Read EDT data from file
        // EDT? temporary, remove "?"
        return null
    }
    //</editor-fold>


    //<editor-fold desc="Read and Write Personal data">
    fun readPersonalList() :List<Personal>? {
        // "?" temporary, remove when function coded
        return null
    }
    //</editor-fold>


    //<editor-fold desc="Read and Write Colleurs data">
    fun readColleursList() :List<Colleurs>? {
        return null
    }
    //</editor-fold>


    //<editor-fold desc="Read and Write Colles data">
    fun readCollesMathsList() :List<Colles>? {
        return null
    }

    fun readCollesAutreList() :List<Colles>? {
        return null
    }
    //</editor-fold>


    //<editor-fold desc="JSON shit">
    private fun serializer(students: List<Student>): String{
        val mapper = jacksonObjectMapper()
        return mapper.writeValueAsString(students)
    }

    private fun deserializer(fileContents: String): List<Student>{
        val mapper = jacksonObjectMapper()
        return mapper.readValue(fileContents)
    }
    //</editor-fold>

    companion object{
        const val filename : String = "mpStar.dat"
    }
}