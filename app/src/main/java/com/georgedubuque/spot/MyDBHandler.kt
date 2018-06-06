package com.georgedubuque.spot

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/*
purpose:    handles pulling and pushing data to the database, able to insert, delete, search and get
            all objects from the database
source:     Jianna Zhang SQLite Database example and George Dubuque
 */

class MyDBHandler(context: Context,
                  name: String?,
                  factory: SQLiteDatabase.CursorFactory?,
                  version: Int) : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_PRODUCTS_TABLE = ("CREATE TABLE "
                + TABLE_SPOTS
                + "("
                + COLUMN_ID
                + " INTEGER PRIMARY KEY,"
                + COLUMN_SPOTNAME
                + " TEXT,"
                + COLUMN_SPOTYPE
                + " TEXT,"
                + COLUMN_SPOTNUM
                + " INTEGER,"
                + COLUMN_IMG
                + " TEXT,"
                + COLUMN_RATING
                + " INTEGER,"
                + COLUMN_LAT
                + " INTEGER,"
                + COLUMN_LNG
                + " INTEGER"
                + ")")
        db.execSQL(CREATE_PRODUCTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase,
                           oldVersion: Int,
                           newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SPOTS")
        onCreate(db)
    }

    fun addSpot(spot: Spot) {
        val values = ContentValues()
        values.put(COLUMN_SPOTNAME, spot.name)
        values.put(COLUMN_SPOTYPE, spot.type)
        values.put(COLUMN_SPOTNUM, spot.typeNum)
        values.put(COLUMN_IMG, spot.img)
        values.put(COLUMN_RATING, spot.rating)
        values.put(COLUMN_LAT, spot.lat)
        values.put(COLUMN_LNG, spot.lng)

        val db = this.writableDatabase

        db.insert(TABLE_SPOTS, null, values)
        db.close()
    }

    fun findSpot(spotName: String): Spot? {
        val query = ("Select * FROM "
                + TABLE_SPOTS
                + " WHERE "
                + COLUMN_SPOTNAME
                + " =  \""
                + spotName
                + "\"")
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var spot: Spot? = Spot()
        if (cursor.moveToFirst()) {
            cursor.moveToFirst()
            spot!!.setId(Integer.parseInt(cursor.getString(0)))
            spot!!.name = cursor.getString(1)
            spot!!.type = cursor.getString(2)
            spot!!.typeNum = cursor.getInt(3)
            spot!!.img = cursor.getString(4)
            spot!!.rating = cursor.getFloat(5)
            spot!!.lat = cursor.getDouble(6)
            spot!!.lng = cursor.getDouble(7)

            cursor.close()
        } else {
            spot = null
        }
        db.close()
        return spot
    }

    fun getSpots() : ArrayList<Spot>{

        val spots = ArrayList<Spot>()
        val query = ("Select * FROM "
                + TABLE_SPOTS)

        val db = this.writableDatabase
        val cursor = db.rawQuery(query,null)
        var spot = Spot()

        while(cursor.moveToNext()){

            spot = Spot()
            spot!!.setId(cursor.getString(0).toInt())
            spot!!.name = cursor.getString(1)
            Log.d("found spot",spot.name)
            spot!!.type = cursor.getString(2)
            spot!!.typeNum = cursor.getInt(3)
            spot!!.img = cursor.getString(4)
            spot!!.rating = cursor.getFloat(5)
            spot!!.lat = cursor.getDouble(6)
            spot!!.lng = cursor.getDouble(7)
            spots.add(spot)
        }

        for(i in spots.indices){

            Log.d("repeating spots found", spots[i].name)
        }

        return spots
    }

    fun deleteSpot(spotname: String): Boolean {
        var result = false
        val query = ("Select * FROM " + TABLE_SPOTS + " WHERE "
                + COLUMN_SPOTNAME + " =  \"" + spotname + "\"")
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        val spot = Spot()
        if (cursor.moveToFirst()) {
            spot.setId(cursor.getString(0).toInt())
            db.delete(TABLE_SPOTS, "$COLUMN_ID = ?",
                    arrayOf(spot.spotId.toString()))
            cursor.close()
            result = true
        }
        db.close()
        return result
    }

    companion object {
        private val DATABASE_VERSION = 7
        private val DATABASE_NAME = "productDB.db"
        val TABLE_SPOTS = "spots"
        val COLUMN_ID = "_id"
        val COLUMN_SPOTNAME = "spotname"
        val COLUMN_SPOTYPE = "spottype"
        val COLUMN_SPOTNUM = "spotnum"
        val COLUMN_RATING = "rating"
        val COLUMN_IMG = "image"
        val COLUMN_LAT = "lat"
        val COLUMN_LNG = "lng"
    }
}
