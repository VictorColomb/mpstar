package com.stan.mpstar.sheets

import android.annotation.SuppressLint
import com.stan.mpstar.model.*
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.services.sheets.v4.Sheets
import io.reactivex.Observable
import io.reactivex.Single
import java.text.SimpleDateFormat
import java.util.*

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
                            myColumn = it[3].toString().toInt()
                    )
                }
                .toList()
    }

    fun readSpreadsheetDate(spreadsheetId: String,spreadsheetRange: String) :Observable<String> {
        return Observable
                .fromCallable {
                    sheetsAPI.spreadsheets().values().get(spreadsheetId,spreadsheetRange).execute().getValues()
                }
                .flatMapIterable { it }
                .map{
                    it[0].toString()
                }
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

    fun readSpreadSheetCustom(spreadsheetId: String,
                          spreadsheetRange: String): Single<List<Notif>> {
        val df = SimpleDateFormat("yyyy-mm-dd hh:mm:ss", Locale.US)
        return Observable
                .fromCallable{
                    val response = sheetsAPI.spreadsheets().values()
                            .get(spreadsheetId, spreadsheetRange)
                            .execute()
                    response.getValues() }
                .flatMapIterable { it }
                .map {
                    Notif (
                            myTitle = it[0].toString(),
                            myTxt = it[1].toString(),
                            myTime = df.parse(it[2].toString())!!,
                            myId = it[3].toString().toInt()
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

    @SuppressLint("SimpleDateFormat")
    fun readSpreadSheetCM(spreadsheetId: String,
                                spreadsheetRange: String): Single<List<List<Any>>> {
        return Observable
                .fromCallable{
                    val response = sheetsAPI.spreadsheets().values()
                            .get(spreadsheetId, spreadsheetRange)
                            .execute()
                    response.getValues() }
                .flatMapIterable {
                    listOf(it[0],it[1],it[2],it[3],it[4],it[5],it[6],it[7],it[8],it[9],it[10],it[11],it[12],it[13]) }
                .toList()
    }

    @SuppressLint("SimpleDateFormat")
    fun readSpreadSheetEDT(spreadsheetId: String,
                          spreadsheetRange: String): Single<List<List<Any>>> {
        return Observable
                .fromCallable{sheetsAPI.spreadsheets().get(spreadsheetId).isNullOrEmpty()
                    val response = sheetsAPI.spreadsheets().values()
                            .get(spreadsheetId, spreadsheetRange)
                            .execute()
                    response.getValues() }
                .flatMapIterable {
                    listOf(it[0],it[1],it[2],it[3],it[4],it[5])}
                .toList()
    }
/*
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