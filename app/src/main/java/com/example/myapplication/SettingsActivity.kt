package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivitySettingsBinding
import com.example.myapplication.utils.LanguageManager

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun attachBaseContext(newBase: Context) {
        val language = LanguageManager.getLanguage(newBase)
        val context = LanguageManager.setLocale(newBase, language)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupLanguageSettings()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupLanguageSettings() {
        val currentLanguage = LanguageManager.getLanguage(this)

        // Set current selection
        when (currentLanguage) {
            LanguageManager.LANGUAGE_SYSTEM -> binding.radioSystem.isChecked = true
            LanguageManager.LANGUAGE_ENGLISH -> binding.radioEnglish.isChecked = true
            LanguageManager.LANGUAGE_CHINESE -> binding.radioChinese.isChecked = true
        }

        binding.radioGroupLanguage.setOnCheckedChangeListener { _, checkedId ->
            val selectedLanguage = when (checkedId) {
                R.id.radioSystem -> LanguageManager.LANGUAGE_SYSTEM
                R.id.radioEnglish -> LanguageManager.LANGUAGE_ENGLISH
                R.id.radioChinese -> LanguageManager.LANGUAGE_CHINESE
                else -> LanguageManager.LANGUAGE_SYSTEM
            }

            if (selectedLanguage != currentLanguage) {
                LanguageManager.saveLanguage(this, selectedLanguage)

                Toast.makeText(this, getString(R.string.language_changed), Toast.LENGTH_SHORT).show()

                // Restart the app to apply language change
                restartApp()
            }
        }
    }

    private fun restartApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}