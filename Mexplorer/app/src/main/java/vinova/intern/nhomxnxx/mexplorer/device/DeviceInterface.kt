package vinova.intern.nhomxnxx.mexplorer.device

import vinova.intern.nhomxnxx.mexplorer.baseInterface.BasePresenter
import vinova.intern.nhomxnxx.mexplorer.baseInterface.BaseView
import vinova.intern.nhomxnxx.mexplorer.model.Devices

interface DeviceInterface {
    interface View: BaseView<DeviceInterface.Presenter>{
        fun showList(devices : List<Devices>?)
    }
    interface Presenter : BasePresenter{
        fun getDevice(token: String?)
    }
}