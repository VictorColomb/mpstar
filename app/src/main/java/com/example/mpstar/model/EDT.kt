package com.example.mpstar.model

import java.util.*

data class EDT(
        val myMonday : Map<Int, String>,
        val myTuesday : Map<Int, String>,
        val myWednesday : Map<Int, String>,
        val myThursday : Map<Int, String>,
        val myFriday : Map<Int, String>
)