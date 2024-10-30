package com.example.dicodingeventsv2.ui.settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.dicodingeventsv2.R
import com.example.dicodingeventsv2.databinding.FragmentSettingsBinding
import com.example.dicodingeventsv2.ui.notification.MyWorker
import com.example.dicodingeventsv2.ui.viewModels.MainViewModel
import com.example.dicodingeventsv2.ui.viewModels.ViewModelFactory
import java.util.concurrent.TimeUnit

class SettingsFragment : PreferenceFragmentCompat() {
    private var _binding: FragmentSettingsBinding? = null
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val themeButton = findPreference<SwitchPreference>("themeKey")
        val notificationButton = findPreference<SwitchPreference>("notificationKey")

        viewModel.getThemeSettings().observe(viewLifecycleOwner) { isDark ->
            themeButton?.isChecked = isDark
        }
        viewModel.getNotificationSetting().observe(viewLifecycleOwner) { isActive ->
            notificationButton?.isChecked = isActive
        }

        themeButton?.setOnPreferenceChangeListener { _, newValue ->
            val isDarkMode = newValue as Boolean
            viewModel.saveThemeSetting(isDarkMode)
            setTheme(isDarkMode)
            true
        }

        notificationButton?.setOnPreferenceChangeListener { _, newValue ->
            val isEnabled = newValue as Boolean
            viewModel.saveNotificationSetting(isEnabled)
            if (isEnabled) {
                startPeriodicTask()
            } else {
                WorkManager.getInstance(requireContext()).cancelUniqueWork(MyWorker.NOTIFICATION_ID.toString())
            }
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }

    private fun setTheme(isDark: Boolean) {
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun startPeriodicTask() {
        val workRequest: WorkRequest = PeriodicWorkRequestBuilder<MyWorker>(1, TimeUnit.DAYS)
            .build()
        WorkManager.getInstance(requireContext()).enqueue(workRequest)
    }
}