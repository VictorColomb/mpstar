package com.example.mpstar.sheets

import android.annotation.SuppressLint
import com.example.mpstar.model.*
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.services.sheets.v4.Sheets
import io.reactivex.Observable
import io.reactivex.Single
import java.text.SimpleDateFormat

class SheetsAPIDataSource(private val authManager : AuthenticationManager,
                          private val transport : HttpTransport,
                          private val jsonFactory: JsonFactory) {

    private val sheetsAPI : Sheets
        get() {
            return Sheets.Builder(transport,
                    jsonFactory,
                    authManager.googleAccountCredential)
                    .setApplicationName("Mp Star")
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

    @SuppressLint("SimpleDateFormat")
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
                            myGroup = it[5].toString().toInt(),
                            myGroupInfo = it[6].toString().toInt(),
                            myGroupTD = it[7].toString().toInt()
                    )
                }
                .toList()
    }

    @SuppressLint("SimpleDateFormat")
    fun readSpreadSheetDS(spreadsheetId: String,
                                spreadsheetRange: String): Single<List<DS>> {
        val df = SimpleDateFormat("MM/dd/yyyy")
        return Observable
                .fromCallable{
                    val response = sheetsAPI.spreadsheets().values()
                            .get(spreadsheetId, spreadsheetRange)
                            .execute()
                    response.getValues() }
                .flatMapIterable { it }
                .map {
                    DS (
                            myDate = df.parse(it[0].toString())!!,
                            myDiscipline = it[1].toString(),
                            myDuration = it[2].toString(),
                            mySecondDiscipline = it[3].toString(),
                            mySecondDuration = it[4].toString()
                    )
                }
                .toList()
    }

    //WIP
    @SuppressLint("SimpleDateFormat")
    fun readSpreadSheetEDT(spreadsheetId: String,
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
                            myGroup = it[5].toString().toInt(),
                            myGroupInfo = it[6].toString().toInt(),
                            myGroupTD = it[7].toString().toInt()
                    )
                }
                .toList()
    }

    fun readSpreadSheetColleurs(spreadsheetId: String,
                                spreadsheetRange: String): Single<List<Colleurs>> {
        return Observable
                .fromCallable{
                    val response = sheetsAPI.spreadsheets().values()
                            .get(spreadsheetId, spreadsheetRange)
                            .execute()
                    response.getValues() }
                .flatMapIterable { it }
                .map {
                    Colleurs(
                            myId = it[0].toString(),
                            mySubject = it[1].toString(),
                            myName = it[2].toString(),
                            myDay = it[3].toString(),
                            myTime = it[4].toString().toInt(),
                            myPlace = it[5].toString()
                    )
                }
                .toList()
    }

    /* WIP
    @SuppressLint("SimpleDateFormat")
    fun readSpreadSheetCM(spreadsheetId: String,
                                spreadsheetRange: String): Single<List<Colles>> {
        return Observable
                .fromCallable{
                    val response = sheetsAPI.spreadsheets().values()
                            .get(spreadsheetId, spreadsheetRange)
                            .execute()
                    response.getValues() }
                .flatMapIterable { it }
                .map {
                    Colles(

                    )
                }
                .toList()
    }

    @SuppressLint("SimpleDateFormat")
    fun readSpreadSheetCA(spreadsheetId: String,
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
                            myGroup = it[5].toString().toInt(),
                            myGroupInfo = it[6].toString().toInt(),
                            myGroupTD = it[7].toString().toInt()
                    )
                }
                .toList()
    }
     */
}