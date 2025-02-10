package app.bellonime.jabreak.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import app.bellonime.jabreak.ui.dashboard.DashboardFragment
import app.bellonime.jabreak.ui.home.HomeFragment
import app.bellonime.jabreak.ui.notifications.NotificationsFragment

class MyPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> DashboardFragment()
            2 -> NotificationsFragment()
            else -> HomeFragment()
        }
    }
}
