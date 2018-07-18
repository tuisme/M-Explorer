package vinova.intern.nhomxnxx.mexplorer.forget_pass

import vinova.intern.best_trip.baseInterface.BaseView

interface ForgetInterface {
	interface View: BaseView<Presenter>{
		fun requestSuccess()
	}
	interface Presenter{
		fun sendRequestReset(email:String?)
	}
}