package com.example.mpstar.save

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.mpstar.model.Student
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.jack.royer.kotlintest2.ui.read.ReadSpreadsheetActivity
import org.json.JSONException
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class FilesIO(
        val context: Context
){

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

    private fun serializer(students: List<Student>): String{
        val mapper = jacksonObjectMapper()
        return mapper.writeValueAsString(students)
    }

    private fun deserializer(fileContents: String): List<Student>{
        val mapper = jacksonObjectMapper()
        return mapper.readValue(fileContents)
    }

    companion object{
        const val filename : String = "mpStar.dat"
    }
}