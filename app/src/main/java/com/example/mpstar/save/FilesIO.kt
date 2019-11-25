package com.example.mpstar.save

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.mpstar.model.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.*


class FilesIO(
        val context: Context
){

    private fun write(fileContents: String, filename:String){
        // Writes parsed data to internal storage
        Log.i("FILESIO", "Writing data $filename")
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

    private fun read(filename: String): String?{
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

            return fileContents
        }
        //catch (ex : NoSuchFileException) {Log.i("FILESIO", "File not found")}
        catch (ex : Exception)
        {
            Toast.makeText(context,"Error Reading Data", Toast.LENGTH_SHORT).show()
            Log.e("FILE ERROR", ex.toString())
            return null
        }
    }

    //<editor-fold desc="Read and Write Student data">
    fun writeStudentList(students: Pair<List<Student>, Date?>){
        val fileContents = jacksonObjectMapper().writeValueAsString(students)
        write(fileContents, filenamePlan)
    }

    fun readStudentList() : Pair<List<Student>, Date?>{
        val fileContents = read(filenamePlan)
        return if (fileContents != null) {jacksonObjectMapper().readValue(fileContents)} else {Pair(listOf(),null)}
    }

    fun readNamesList() :List<String>{
        val studentsTemp = readStudentList()
        return studentsTemp.first.map {
            it.myName
        }
    }
    //</editor-fold>


    //<editor-fold desc="Read and Write DS data">
    fun writeDSList(listDS: List<DS>){
        val fileContents = jacksonObjectMapper().writeValueAsString(listDS)
        write(fileContents, filenameDS)
    }

    fun readDSList() :List<DS>{ // Read DS list from file
        val fileContents = read(filenameDS)
        return if (fileContents != null) {jacksonObjectMapper().readValue(fileContents)} else {listOf()}

        /* FOR TESTING PURPOSES
        val dt = SimpleDateFormat("yyyy/MM/dd", Locale.US)
        return listOf(DS(dt.parse("2019/11/23")!!, "Option", "4h", "FALSE", "0"))
         */
    }
    //</editor-fold>


    //<editor-fold desc="Read and Write EDT data">
    fun writeEDTList(edt: EDT){
        val fileContents = jacksonObjectMapper().writeValueAsString(edt)
        write(fileContents, filenameEDT)
    }

    fun readEdtList() : EDT? { // Read EDT data from file
        val fileContents = read(filenameEDT)
        return if (fileContents != null) {jacksonObjectMapper().readValue(fileContents)} else {null}
    }
    //</editor-fold>


    //<editor-fold desc="Read and Write Personal data">
    fun writePersonalList(perso : List<Personal>){
        val fileContents = jacksonObjectMapper().writeValueAsString(perso)
        write(fileContents, filenamePersonal)
    }

    fun readPersonalList() :List<Personal> {
        val fileContents = read(filenamePersonal)
        return if (fileContents != null) {jacksonObjectMapper().readValue(fileContents)} else {listOf()}
    }
    //</editor-fold>


    //<editor-fold desc="Read and Write Colleurs data">
    fun writeColleursList(colleurs: List<Colleurs>){
        val fileContents = jacksonObjectMapper().writeValueAsString(colleurs)
        write(fileContents, filenameColleur)
    }

    fun readColleursList() :List<Colleurs> {
        val fileContents = read(filenameColleur)
        return if (fileContents != null) {jacksonObjectMapper().readValue(fileContents)} else {listOf()}
    }
    //</editor-fold>


    //<editor-fold desc="Read and Write Colles data">
    fun writeCollesMathsList(colles: List<Colles>){
        val fileContents = jacksonObjectMapper().writeValueAsString(colles)
        write(fileContents, filenameCM)
    }

    fun writeCollesAutreList(colles: List<Colles>){
        val fileContents = jacksonObjectMapper().writeValueAsString(colles)
        write(fileContents, filenameCA)
    }

    fun readCollesMathsList() :List<Colles> {
        val fileContents = read(filenameCM)
        return if (fileContents != null) {jacksonObjectMapper().readValue(fileContents)} else {listOf()}
    }

    fun readCollesAutreList() :List<Colles> {
        val fileContents = read(filenameCA)
        return if (fileContents != null) {jacksonObjectMapper().readValue(fileContents)} else {listOf()}
    }
    //</editor-fold>

    companion object{
        const val filenamePlan : String = "mpStarPlan.dat"
        const val filenameDS : String = "mpStarDS.dat"
        const val filenameEDT : String = "mpStarEDT.dat"
        const val filenameColleur : String = "mpStarColleur.dat"
        const val filenamePersonal : String = "mpStarPerso.dat"
        const val filenameCM : String = "mpStarCM.dat"
        const val filenameCA : String = "mpStarCA.dat"
    }
}