package vinova.intern.nhomxnxx.mexplorer.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import vinova.intern.nhomxnxx.mexplorer.signIn.SignInFragment
import vinova.intern.nhomxnxx.mexplorer.signUp.SignUpFragment

class PageAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {

	override fun getItem(position: Int): Fragment {
		val frag : Fragment
		when(position){
			0 -> frag = SignInFragment()
			1 -> frag = SignUpFragment()
			else -> frag = SignInFragment()
		}
		return frag
	}

	override fun getCount(): Int {
		return 2
	}

	override fun getPageTitle(position: Int): CharSequence? {
		var title : String
		when(position){
			0 -> title = "Sign In"
			1 -> title = "Sign Up"
			else -> title = "Sign In"
		}
		return title
	}
}