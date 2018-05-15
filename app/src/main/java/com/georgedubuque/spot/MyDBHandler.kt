package com.georgedubuque.spot

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDBHandler(context: Context,
                  name: String,
                  factory: SQLiteDatabase.CursorFactory,
                  version: Int) : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_PRODUCTS_TABLE = ("CREATE TABLE "
                + TABLE_NAME
                + "("
                + COLUMN_ID
                + " INTEGER PRIMARY KEY,"
                + COLUMN_SPOTNAME
                + " TEXT,"
                + COLUMN_SPOTYPE
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
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addProduct(spot: Spot) {
        val values = ContentValues()
        values.put(COLUMN_SPOTNAME, spot.name)
        values.put(COLUMN_SPOTYPE, spot.type)
        values.put(COLUMN_RATING, spot.rating)
        values.put(COLUMN_LAT, spot.lat)
        values.put(COLUMN_LNG, spot.lng)

        val db = this.writableDatabase

        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun findProduct(productname: String): Spot? {
        val query = ("Select * FROM "
                + TABLE_NAME
                + " WHERE "
                + COLUMN_PRODUCTNAME
                + " =  \""
                + productname
                + "\"")
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var product: Product? = Product()
        if (cursor.moveToFirst()) {
            cursor.moveToFirst()
            product!!.setID(Integer.parseInt(cursor.getString(0)))
            product!!.setProductName(cursor.getString(1))
            product!!.setQuantity(Integer.parseInt(cursor.getString(2)))
            cursor.close()
        } else {
            product = null
        }
        db.close()
        return product
    }

    fun deleteProduct(productname: String): Boolean {
        var result = false
        val query = ("Select * FROM " + TABLE_PRODUCTS + " WHERE "
                + COLUMN_PRODUCTNAME + " =  \"" + productname + "\"")
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        val product = Product()
        if (cursor.moveToFirst()) {
            product.setID(Integer.parseInt(cursor.getString(0)))
            db.delete(TABLE_PRODUCTS, "$COLUMN_ID = ?",
                    arrayOf(String.valueOf(product.getID())))
            cursor.close()
            result = true
        }
        db.close()
        return result
    }

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "productDB.db"
        val TABLE_NAME = "spots"
        val COLUMN_ID = "_id"
        val COLUMN_SPOTNAME = "spotname"
        val COLUMN_SPOTYPE = "spottype"
        val COLUMN_RATING = "rating"
        val COLUMN_LAT = "lat"
        val COLUMN_LNG = "lng"
    }
}
