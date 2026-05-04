package com.shieldindia.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermissionsIfNeeded()

        val btnScan = findViewById<Button>(R.id.btnScanSms)
        val tvResult = findViewById<TextView>(R.id.tvScanResult)
        val listView = findViewById<ListView>(R.id.lvScamLog)

        btnScan.setOnClickListener {
            tvResult.text = "🔍 Scanning recent SMS..."
            val results = scanRecentSms()

            if (results.isEmpty()) {
                tvResult.text = "✅ No scams detected in recent messages."
            } else {
                tvResult.text = "🚨 ${results.size} scam(s) detected!"
            }

            listView.adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                results
            )
        }
    }

    private fun scanRecentSms(): List<String> {
        val results = mutableListOf<String>()
        val analyzer = ScamAnalyzer()

        val cursor = contentResolver.query(
            android.provider.Telephony.Sms.CONTENT_URI,
            arrayOf("address", "body"),
            null,
            null,
            "date DESC LIMIT 50"
        ) ?: return results

        cursor.use {
            while (it.moveToNext()) {
                val sender = it.getString(0) ?: "Unknown"
                val body = it.getString(1) ?: ""
                val threat = analyzer.analyze(sender, body)
                if (threat != null) results.add(threat)
            }
        }
        return results
    }

    private fun requestPermissionsIfNeeded() {
        val needed = mutableListOf(
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            needed.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        val missing = needed.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missing.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this, missing.toTypedArray(), PERMISSIONS_REQUEST
            )
        }
    }
}
