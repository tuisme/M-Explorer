package vinova.intern.nhomxnxx.mexplorer.device

import android.content.Context
import vinova.intern.nhomxnxx.mexplorer.baseInterface.BaseActivity
import vinova.intern.nhomxnxx.mexplorer.baseInterface.BaseView
import vinova.intern.nhomxnxx.mexplorer.home.HomeInterface
import vinova.intern.nhomxnxx.mexplorer.model.Devices
import vinova.intern.nhomxnxx.mexplorer.model.ListDevice

interface DeviceInterface {
    interface View: BaseView<HomeInterface.Presenter>{
        fun showList(devices : List<Devices>?)
    }
    interface Presenter{
        fun getDevice(token: String?)
    }
}