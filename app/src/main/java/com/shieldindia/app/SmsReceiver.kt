package com.shieldindia.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        val analyzer = ScamAnalyzer()

        for (sms in messages) {
            val sender = sms.originatingAddress ?: "Unknown"
            val body = sms.messageBody ?: ""
            val threat = analyzer.analyze(sender, body)

            if (threat != null) {
                AlertHelper.showNotification(
                    context,
                    "⚠️ Scam SMS Detected!",
                    threat
                )
            }
        }
    }
}
