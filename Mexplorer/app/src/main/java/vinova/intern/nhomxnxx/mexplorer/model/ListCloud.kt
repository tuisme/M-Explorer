package vinova.intern.nhomxnxx.mexplorer.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class ListCloud (

	@SerializedName("time")
	@Expose
	var time: String? = null,
	@SerializedName("status")
	@Expose
	var status: String? = null,
	@SerializedName("message")
	@Expose
	var message: Any? = null,
	@SerializedName("user")
	@Expose
	var user: User? = null,
	@SerializedName("clouds")
	@Expose
	var clouds: List<Cloud>? = null
)

data class Cloud (
	@SerializedName("cid")
	@Expose
	var cid: String? = null,
	@SerializedName("cname")
	@Expose
	var cname: String? = null,
	@SerializedName("ctype")
	@Expose
	var ctype: String? = null,
	@SerializedName("used")
	@Expose
	var used: String? = null,
	@SerializedName("unused")
	@Expose
	var unused: String? = null
)