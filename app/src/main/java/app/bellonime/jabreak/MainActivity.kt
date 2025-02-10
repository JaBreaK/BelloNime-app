package app.bellonime.jabreak

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import app.bellonime.jabreak.adapter.MyPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.viewPager)
        bottomNav = findViewById(R.id.bottom_nav)

        // Setup ViewPager
        viewPager.adapter = MyPagerAdapter(this)

        // Link ViewPager dengan Bottom Navigation
        setupNavigation()
    }

    private fun setupNavigation() {
        // Handle bottom nav item clicks
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> viewPager.currentItem = 0
                R.id.dashboard -> viewPager.currentItem = 1
                R.id.notifications -> viewPager.currentItem = 2
            }
            true
        }

        // Update bottom nav saat ViewPager di-swap
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                bottomNav.menu.getItem(position).isChecked = true
            }
        })
    }
}