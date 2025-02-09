package app.bellonime.jabreak.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import app.bellonime.jabreak.ui.dashboard.DashboardFragment
import app.bellonime.jabreak.ui.home.HomeFragment

class MyPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    // Kita menampilkan 2 halaman: Home dan Dashboard
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> DashboardFragment()
            else -> HomeFragment()
        }
    }
}
