package vinova.intern.nhomxnxx.mexplorer.databaseSQLite

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DatabaseHandler(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val script = ("CREATE TABLE If not exists " + DBTable.USER.TABLE_NAME + "("
                + DBTable.USER.TOKEN.COLUMN_NAME + " TEXT PRIMARY KEY," + DBTable.USER.FIRST_NAME.COLUMN_NAME + " TEXT,"
                + DBTable.USER.LAST_NAME.COLUMN_NAME + " TEXT," + DBTable.USER.EMAIL.COLUMN_NAME + " TEXT,"
                + DBTable.USER.TYPE.COLUMN_NAME + " TEXT," + DBTable.USER.STATUS.COLUMN_NAME + " TEXT" +")")
        // Chạy lệnh tạo bảng.
        db.execSQL(script)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val drop_students_table = String.format("DROP TABLE IF EXISTS %s", DBTable.USER.TABLE_NAME)
        db.execSQL(drop_students_table)
        onCreate(db)
    }

    companion object {
        private val DATABASE_NAME = "database"
        private val DATABASE_VERSION = 1
        val LOGGING_IN = 1
        val NOT_LOGGING_IN = 0
        val GOOGLE = "GOOGLE"
        val FACEBOOK = "FACEBOOK"
        val NORMAL = "NORMAL"
    }

    fun insertUserData(token:String?,email: String?, first_name: String?, last_name: String?, type: String, status: Int) {
        val db = this.writableDatabase
        val values_user = ContentValues()
        values_user.put(DBTable.USER.TOKEN.COLUMN_NAME,token)
        values_user.put(DBTable.USER.EMAIL.COLUMN_NAME, email)
        values_user.put(DBTable.USER.FIRST_NAME.COLUMN_NAME, first_name)
        values_user.put(DBTable.USER.LAST_NAME.COLUMN_NAME, last_name)
        values_user.put(DBTable.USER.TYPE.COLUMN_NAME, type)
        values_user.put(DBTable.USER.STATUS.COLUMN_NAME, status)

        db!!.insert(DBTable.USER.TABLE_NAME, null, values_user)
    }

    fun updateUserStatus(username: String, status: Int): Boolean {
        val values = ContentValues()
        val db = this.writableDatabase

        values.put(DBTable.USER.STATUS.COLUMN_NAME, status)
        return try {
            db!!.update(
                    DBTable.USER.TABLE_NAME,
                    values,
                    DBTable.USER.EMAIL.COLUMN_NAME + " = '" + username + "'", null
            )
            true
        } catch (ex: Exception) {
            ex.printStackTrace()
            false
        }

    }

    fun deleteUserData(token: String?): Boolean {
        val db = this.writableDatabase

        return try {
            db!!.delete(
                    DBTable.USER.TABLE_NAME,
                    DBTable.USER.TOKEN.COLUMN_NAME + " = '" + token + "'", null
            )
            true
        } catch (ex: Exception) {
            ex.printStackTrace()
            false
        }

    }

    @SuppressLint("Recycle")
    fun userIsLogingIn(): Boolean {
        val db = this.writableDatabase
        val cursor = db!!.rawQuery(
                "SELECT * FROM " + DBTable.USER.TABLE_NAME, null
        )
        cursor.moveToFirst()
        return cursor.count > 0 && cursor.getInt(DBTable.USER.STATUS.COLUMN_NUMBER) == DatabaseHandler.LOGGING_IN
    }

    @SuppressLint("Recycle")
            /**
             * Get username in user table
             * @return username or null if no user logging in
             */
    fun getUserLoggedIn(): String? {
        val db = this.writableDatabase
        val cursor = db?.rawQuery(
                "SELECT * FROM " + DBTable.USER.TABLE_NAME, null
        )
        cursor?.moveToFirst()
        return if (cursor?.count!! > 0) {
            cursor.getString(DBTable.USER.EMAIL.COLUMN_NUMBER)
        } else null
    }

    @SuppressLint("Recycle")
    fun getToken(): String? {
        val db = this.writableDatabase
        val cursor = db?.rawQuery(
                "SELECT * FROM " + DBTable.USER.TABLE_NAME, null
        )
        cursor?.moveToFirst()
        return if (cursor?.count!! > 0) {
            cursor.getString(DBTable.USER.TOKEN.COLUMN_NUMBER)
        } else null
    }


}