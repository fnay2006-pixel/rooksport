package com.rook.sport.data

/**
 * ====================================================================
 *  ⭐ هذا هو الرابط الوحيد الذي يربط تطبيقك بلوحة التحكم عن بُعد ⭐
 * ====================================================================
 *
 *  ضع هنا رابط ملف JSON الذي تستضيفه (وهو الملف الذي تعدّله أنت).
 *
 *  أسهل طريقة مجانية:
 *   1) أنشئ مستودعاً على GitHub (مثلاً: rook-sport-config).
 *   2) ارفع فيه ملف "config.json" (نموذج جاهز في مجلد server/ هنا).
 *   3) افتح الملف على GitHub ثم اضغط "Raw" وانسخ الرابط، ويكون شكله:
 *      https://raw.githubusercontent.com/USERNAME/REPO/main/config.json
 *   4) ضعه بالأسفل.
 *
 *  بعدها: كلما عدّلت config.json (روابط m3u8 أو أضفت مباراة) واحفظته،
 *  سيظهر التحديث في التطبيق فوراً عند فتحه — بدون تحديث التطبيق نفسه.
 */
object RemoteConfigUrl {
    const val URL = "https://raw.githubusercontent.com/USERNAME/REPO/main/config.json"
}
