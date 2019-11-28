package org.worshipsongs.service


import android.animation.ObjectAnimator
import android.content.Context
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.widget.ProgressBar

/**
 * Author : Madasamy
 * Version : 3.x
 */

interface ImportDatabaseService
{
    val name: String
    val order: Int
    fun loadDb(appCompatActivity: AppCompatActivity, objects: Map<String, Any>)

}
