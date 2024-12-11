package com.example.perceivo.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.perceivo.R
import com.example.perceivo.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize NavController
        navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Setup BottomNavigationView
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_analytics, R.id.nav_history, R.id.nav_profile)
        )
        binding.navView.setupWithNavController(navController)

        // Handle Toolbar and Navigation Changes
        setupToolbarAndNavigation()

        // Check for Fragment Target from Intent
        handleFragmentTargetFromIntent()
    }

    /**
     * Setup toolbar visibility, back button, and icons dynamically based on destination.
     */
    private fun setupToolbarAndNavigation() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.toolbar.visibility = View.VISIBLE
            binding.btnBack.visibility = View.GONE

            // Default toolbar title and icons
            resetNavigationIcons()

            // Configure toolbar and icons based on destination
            when (destination.id) {
                R.id.nav_home -> {
                    binding.tvToolbarTitle.text = getString(R.string.nav_home)
                    setActiveIcon(R.id.nav_home, R.drawable.ic_home_fill)
                }
                R.id.nav_analytics -> {
                    binding.tvToolbarTitle.text = getString(R.string.nav_analytics)
                    setActiveIcon(R.id.nav_analytics, R.drawable.ic_analytics_fill)
                }
                R.id.nav_history -> {
                    binding.tvToolbarTitle.text = getString(R.string.nav_history)
                    setActiveIcon(R.id.nav_history, R.drawable.ic_task_fill)
                }
                R.id.nav_profile -> {
                    binding.tvToolbarTitle.text = getString(R.string.nav_profile)
                    setActiveIcon(R.id.nav_profile, R.drawable.ic_profile_fill)
                }
                else -> {
                    // Show back button for other fragments
                    binding.toolbar.visibility = View.VISIBLE
                    binding.btnBack.visibility = View.VISIBLE
                }
            }
        }

        // Handle back button click
        binding.btnBack.setOnClickListener {
            navController.navigateUp()
        }
    }

    /**
     * Handle navigation to a specific fragment based on Intent data.
     */
    private fun handleFragmentTargetFromIntent() {
        val fragmentTarget = intent.getStringExtra("fragment_target")
        when (fragmentTarget) {
            "HistoryFragment" -> navController.navigate(R.id.nav_history)
            "AnalyticsFragment" -> navController.navigate(R.id.nav_analytics)
            "ProfileFragment" -> navController.navigate(R.id.nav_profile)
            else -> {
                // Default to Home if no fragment target specified
                navController.navigate(R.id.nav_home)
            }
        }
    }

    /**
     * Reset navigation icons to default state.
     */
    private fun resetNavigationIcons() {
        binding.navView.menu.findItem(R.id.nav_home).setIcon(R.drawable.ic_home)
        binding.navView.menu.findItem(R.id.nav_analytics).setIcon(R.drawable.ic_analytics)
        binding.navView.menu.findItem(R.id.nav_history).setIcon(R.drawable.ic_tasks)
        binding.navView.menu.findItem(R.id.nav_profile).setIcon(R.drawable.ic_profile)
    }

    /**
     * Set active icon for selected menu item.
     */
    private fun setActiveIcon(menuItemId: Int, iconRes: Int) {
        binding.navView.menu.findItem(menuItemId).setIcon(iconRes)
    }
}