package vinova.intern.best_trip.baseInterface

interface BaseView<T> {
    fun setPresenter(presenter: T)

    fun showLoading(isShow: Boolean)

    fun showError(message: String)
}
