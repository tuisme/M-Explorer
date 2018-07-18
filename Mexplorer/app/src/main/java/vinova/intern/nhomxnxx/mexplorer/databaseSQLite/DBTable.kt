package vinova.intern.nhomxnxx.mexplorer.databaseSQLite


class DBTable {

    object USER {
        val TABLE_NAME = "user"

        object FIRST_NAME {
            val COLUMN_NAME = "first_name"
            val COLUMN_NUMBER = 0
        }

        object LAST_NAME {
            val COLUMN_NAME = "last_name"
            val COLUMN_NUMBER = 0
        }

        object EMAIL {
            val COLUMN_NAME = "email"
            val COLUMN_NUMBER = 0
        }

        object TYPE {
            val COLUMN_NAME = "Type"
            val COLUMN_NUMBER = 1
        }

        object STATUS {
            val COLUMN_NAME = "Status"
            val COLUMN_NUMBER = 2
        }
        object TOKEN {
            val COLUMN_NAME = "Token"
            val COLUMN_NUMBER = 2
        }
    }

}
