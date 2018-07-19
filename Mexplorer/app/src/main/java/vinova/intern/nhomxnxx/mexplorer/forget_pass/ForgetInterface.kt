package vinova.intern.nhomxnxx.mexplorer.forget_pass

import vinova.intern.nhomxnxx.mexplorer.baseInterface.BaseView

interface ForgetInterface {
	interface View: BaseView<Presenter> {
		fun requestSuccess()
	}
	interface Presenter{
		fun sendRequestReset(email:String?)
	}
}