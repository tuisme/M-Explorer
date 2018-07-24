package vinova.intern.nhomxnxx.mexplorer.databaseSQLite


class DBTable {

    object USER {
        val TABLE_NAME = "user"

        object TOKEN {
            val COLUMN_NAME = "Token"
            val COLUMN_NUMBER = 0
        }
        object FIRST_NAME {
            val COLUMN_NAME = "first_name"
            val COLUMN_NUMBER = 1
        }

        object LAST_NAME {
            val COLUMN_NAME = "last_name"
            val COLUMN_NUMBER = 2
        }

        object EMAIL {
            val COLUMN_NAME = "email"
            val COLUMN_NUMBER = 3
        }

        object TYPE {
            val COLUMN_NAME = "Type"
            val COLUMN_NUMBER = 4
        }

        object STATUS {
            val COLUMN_NAME = "Status"
            val COLUMN_NUMBER = 5
        }

        object AVATAR{
            val COLUMN_NAME = "avatar"
            val COLUMN_NUMBER = 6
        }

        object USED{
            val COLUMN_NAME = "used"
            val COLUMN_NUMBER = 7
        }

        object ISVIP{
            val COLUMN_NAME = "isVip"
            val COLUMN_NUMBER = 8
        }

        object VERI{
            val COLUMN_NAME = "verified"
            val COLUMN_NUMBER = 9
        }
    }

}
