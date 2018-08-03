package vinova.intern.nhomxnxx.mexplorer.utils

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import java.io.File


object FileUtils {

	//replace this with your authority
	val AUTHORITY = "com.ianhanniballake.localstorage.documents"

	/**
	 * TAG for log messages.
	 */
	internal val TAG = "FileUtils"
	private val DEBUG = false // Set to true to enable logging


	/**
	 * @return Whether the URI is a local one.
	 */
	fun isLocal(url: String?): Boolean {
		return if (url != null && !url.startsWith("http://") && !url.startsWith("https://")) {
			true
		} else false
	}


	fun isLocalStorageDocument(uri: Uri): Boolean {
		return AUTHORITY == uri.getAuthority()
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 * @author paulburke
	 */
	fun isExternalStorageDocument(uri: Uri): Boolean {
		return "com.android.externalstorage.documents" == uri.getAuthority()
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 * @author paulburke
	 */
	fun isDownloadsDocument(uri: Uri): Boolean {
		return "com.android.providers.downloads.documents" == uri.getAuthority()
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 * @author paulburke
	 */
	fun isMediaDocument(uri: Uri): Boolean {
		return "com.android.providers.media.documents" == uri.getAuthority()
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	fun isGooglePhotosUri(uri: Uri): Boolean {
		return "com.google.android.apps.photos.content" == uri.getAuthority()
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context       The context.
	 * @param uri           The Uri to query.
	 * @param selection     (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 * @author paulburke
	 */
	fun getDataColumn(context: Context, uri: Uri?, selection: String?,
	                  selectionArgs: Array<String>?): String? {

		var cursor: Cursor? = null
		val column = "_data"
		val projection = arrayOf(column)

		try {
			cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
			if (cursor != null && cursor.moveToFirst()) {
				if (DEBUG)
					DatabaseUtils.dumpCursor(cursor)

				val column_index = cursor.getColumnIndexOrThrow(column)
				return cursor.getString(column_index)
			}
		} finally {
			if (cursor != null)
				cursor.close()
		}
		return null
	}

	@SuppressLint("ObsoleteSdkInt")
			/**
	 * Get a file path from a Uri. This will get the the path for Storage Access
	 * Framework Documents, as well as the _data field for the MediaStore and
	 * other file-based ContentProviders.<br></br>
	 * <br></br>
	 * Callers should check whether the path is local before assuming it
	 * represents a local file.
	 *
	 * @param context The context.
	 * @param uri     The Uri to query.
	 * @author paulburke
	 * @see .isLocal
	 * @see .getFile
	 */
	fun getPath(context: Context, uri: Uri): String? {


		val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// LocalStorageProvider
			if (isLocalStorageDocument(uri)) {
				// The path is the id
				return DocumentsContract.getDocumentId(uri)
			}
			else if (isExternalStorageDocument(uri)) {
				val docId = DocumentsContract.getDocumentId(uri)
				val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
				val type = split[0]

				if ("primary".equals(type, ignoreCase = true)) {
					return "${Environment.getExternalStorageDirectory()}" + "/" + split[1]
				}
				return "${Environment.getExternalStorageDirectory()}" + "/" + split[1]

			}
			else if (isDownloadsDocument(uri)) {

				val id = DocumentsContract.getDocumentId(uri)
				val contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))

				return getDataColumn(context, contentUri, null, null)
			}
			else if (isMediaDocument(uri)) {
				val docId = DocumentsContract.getDocumentId(uri)
				val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
				val type = split[0]

				var contentUri: Uri? = null
				if ("image" == type) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
				} else if ("video" == type) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
				} else if ("audio" == type) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
				}

				val selection = "_id=?"
				val selectionArgs = arrayOf(split[1])

				return getDataColumn(context, contentUri, selection, selectionArgs)
			}// MediaProvider
			// DownloadsProvider
			// ExternalStorageProvider
		} else if ("content".equals(uri.getScheme(), ignoreCase = true)) {

			// Return the remote address
			return if (isGooglePhotosUri(uri)) uri.getLastPathSegment() else getDataColumn(context, uri, null, null)

		} else if ("file".equals(uri.getScheme(), ignoreCase = true)) {
			return uri.getPath()
		}// File
		// MediaStore (and general)

		return null
	}

	/**
	 * Convert Uri into File, if possible.
	 *
	 * @return file A local file that the Uri was pointing to, or null if the
	 * Uri is unsupported or pointed to a remote resource.
	 * @author paulburke
	 * @see .getPath
	 */
	fun getFile(context: Context, uri: Uri?): File? {
		if (uri != null) {
			val path = getPath(context, uri)
			if (path != null && isLocal(path)) {
				return File(path)
			}
		}
		return null
	}


}//private constructor to enforce Singleton pattern