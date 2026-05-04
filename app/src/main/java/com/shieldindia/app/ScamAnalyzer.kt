package com.shieldindia.app

class ScamAnalyzer {

    private val scamKeywords = listOf(
        "kyc", "kyc update", "kyc expired", "kyc verify",
        "account will be blocked", "account suspended",
        "update your aadhar", "link aadhar", "aadhar verify",
        "share otp", "otp required", "enter otp",
        "upi blocked", "collect request", "accept payment",
        "you have won", "congratulations you won",
        "lottery winner", "prize money", "claim your prize",
        "lucky draw", "reward points expire",
        "income tax notice", "trai notice", "cbi notice",
        "your sim will be blocked", "cyber crime department",
        "arrest warrant", "legal action",
        "parcel held", "customs duty pending",
        "instant loan approved", "pre-approved loan",
        "guaranteed returns", "double your money",
        "click here to verify", "verify your account",
        "your bank account", "debit card blocked"
    )

    private val suspiciousUrlPatterns = listOf(
        Regex("""bit\.ly|tinyurl|rb\.gy|cutt\.ly"""),
        Regex("""(sbi|hdfc|icici|paytm|npci|uidai|irctc)[\.\-][a-z0-9\-]+\.(tk|ml|ga|cf|xyz|top)"""),
        Regex("""\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}""")
    )

    fun analyze(sender: String, body: String): String? {
        val lower = body.lowercase()

        val matched = scamKeywords.filter { lower.contains(it) }
        val hasScamUrl = suspiciousUrlPatterns.any { it.containsMatchIn(lower) }

        return when {
            matched.size >= 2 ->
                "🚨 HIGH RISK | From: $sender\nKeywords: ${matched.take(2).joinToString(", ")}"
            matched.isNotEmpty() && hasScamUrl ->
                "🚨 HIGH RISK | From: $sender\nSuspicious link + scam keyword detected"
            matched.isNotEmpty() ->
                "⚠️ SUSPICIOUS | From: $sender\nKeyword: ${matched.first()}"
            hasScamUrl ->
                "⚠️ SUSPICIOUS LINK | From: $sender"
            else -> null
        }
    }
}
