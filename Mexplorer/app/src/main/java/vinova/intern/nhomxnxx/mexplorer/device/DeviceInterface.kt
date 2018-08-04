package vinova.intern.nhomxnxx.mexplorer.device

import android.content.Context
import vinova.intern.nhomxnxx.mexplorer.baseInterface.BasePresenter
import vinova.intern.nhomxnxx.mexplorer.baseInterface.BaseView
import vinova.intern.nhomxnxx.mexplorer.model.Devices

interface DeviceInterface {
    interface View: BaseView<DeviceInterface.Presenter>{
        fun showList(devices :MutableList<Devices>?)
        fun refresh()
        fun logoutSuccess()
    }
    interface Presenter : BasePresenter{
        fun getDevice(token: String?)
        fun deleteDevice(token: String, id: String)
    }
}