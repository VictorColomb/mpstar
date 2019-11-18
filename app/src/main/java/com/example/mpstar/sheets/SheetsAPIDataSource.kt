package com.example.mpstar.sheets

import com.example.mpstar.model.Personal
import com.example.mpstar.model.Student
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.services.sheets.v4.Sheets
import io.reactivex.Observable
import io.reactivex.Single
import java.text.SimpleDateFormat
import java.time.LocalDate

class SheetsAPIDataSource(private val authManager : AuthenticationManager,
                          private val transport : HttpTransport,
                          private val jsonFactory: JsonFactory) {

    private val sheetsAPI : Sheets
        get() {
            return Sheets.Builder(transport,
                    jsonFactory,
                    authManager.googleAccountCredential)
                    .setApplicationName("test")
                    .build()
        }

    fun readSpreadSheet(spreadsheetId: String,
                                 spreadsheetRange: String): Single<List<Student>> {
        return Observable
                .fromCallable{
                    val response = sheetsAPI.spreadsheets().values()
                            .get(spreadsheetId, spreadsheetRange)
                            .execute()
                    response.getValues() }
                .flatMapIterable { it }
                .map {
                    Student(
                            myName = it[0].toString(),
                            myLastName = it[0].toString(),
                            myPoints = it[1].toString().toFloat(),
                            myRow = it[2].toString().toInt(),
                            myColumn = it[3].toString().toInt(),
                            myPair = it[4].toString().toBoolean()
                    )
                }
                .toList()
    }

    fun readSpreadSheetPersonal(spreadsheetId: String,
                        spreadsheetRange: String): Single<List<Personal>> {
        val df = SimpleDateFormat("MM/dd")
        return Observable
                .fromCallable{
                    val response = sheetsAPI.spreadsheets().values()
                            .get(spreadsheetId, spreadsheetRange)
                            .execute()
                    response.getValues() }
                .flatMapIterable { it }
                .map {
                    Personal(
                            myName = it[0].toString(),
                            myLastName = it[1].toString(),
                            myBirthday = df.parse(it[2].toString())!!,
                            myOption = it[3].toString(),
                            myLanguage = it[4].toString(),
                            myGroup = it[5].toString().toInt()
                    )
                }
                .toList()
    }

    companion object {
        val KEY_ID = "spreadsheetId"
        val KEY_URL = "spreadsheetUrl"
    }
}