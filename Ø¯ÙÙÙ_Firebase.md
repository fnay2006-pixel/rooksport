# 🔥 دليل ربط Firebase (خطوة بخطوة) — لتطبيقي ROOK Sport

Firebase هو قاعدة البيانات السحابية المجانية التي تربط **تطبيق الأدمن** بـ**تطبيق المستخدم**.
الأدمن يضيف/يعدّل مباراة → تُحفظ في Firebase → تظهر فوراً لكل المستخدمين.

> ⚠️ **مطلوب قبل بناء أي APK**: يجب إنشاء مشروع Firebase وتنزيل ملف `google-services.json`
> لكل تطبيق، ووضعه في مكانه. بدونه لن يعمل البناء.

---

## 🧭 نظرة عامة
1. أنشئ مشروع Firebase (مجاني).
2. سجّل تطبيقين: المستخدم `com.rook.sport` والأدمن `com.rook.sport.admin`.
3. نزّل ملف `google-services.json` لكل واحد وضعه في مكانه.
4. فعّل قاعدة بيانات Firestore.
5. اضبط قواعد الأمان.

---

## ✅ الخطوة 1: إنشاء مشروع Firebase
1. افتح **https://console.firebase.google.com** وسجّل دخول بحساب Google.
2. اضغط **Add project / إضافة مشروع**.
3. سمّه مثلاً `rook-sport` → التالي → يمكنك إيقاف Google Analytics → **Create project**.

## ✅ الخطوة 2: تسجيل تطبيق المستخدم
1. داخل المشروع، اضغط أيقونة **Android** 🤖.
2. في **Android package name** اكتب بالضبط:
   ```
   com.rook.sport
   ```
3. اضغط **Register app**.
4. اضغط **Download google-services.json** ونزّل الملف.
5. ضع هذا الملف في المسار:
   ```
   app/google-services.json
   ```

## ✅ الخطوة 3: تسجيل تطبيق الأدمن
1. ارجع لإعدادات المشروع (⚙️ Project settings) → **Add app** → **Android**.
2. في package name اكتب بالضبط:
   ```
   com.rook.sport.admin
   ```
3. نزّل ملف **google-services.json** الخاص به.
4. ضعه في المسار:
   ```
   admin/google-services.json
   ```

> 🔴 مهم: كل تطبيق له ملف `google-services.json` خاص به. لا تخلط بينهما.

## ✅ الخطوة 4: تفعيل قاعدة بيانات Firestore
1. في القائمة الجانبية: **Build → Firestore Database**.
2. اضغط **Create database**.
3. اختر **Start in test mode** (للتجربة) → التالي → اختر المنطقة → **Enable**.

## ✅ الخطوة 5: ضبط قواعد الأمان (مهم)
في تبويب **Rules** داخل Firestore، الصق التالي:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // الجميع يقرأ (تطبيق المستخدم)، الكتابة مسموحة للتجربة
    match /{document=**} {
      allow read: if true;
      allow write: if true;   // ⚠️ للتجربة فقط
    }
  }
}
```

> ⚠️ **للحماية لاحقاً**: استبدل `allow write: if true` بنظام تحقق (Firebase Auth)
> حتى لا يكتب إلا الأدمن. هذا كافٍ للبداية لأن تطبيق الأدمن خاص بك ولا تنشره.

---

## 🗂️ بنية قاعدة البيانات (تُنشأ تلقائياً عند أول حفظ من الأدمن)
```
Firestore
├── matches/        ← المباريات (كل وثيقة = مباراة)
├── channels/       ← القنوات
└── settings/
    └── general     ← إعداد notice (شريط الإعلان)
```
لا تحتاج إنشاءها يدوياً — تطبيق الأدمن ينشئها عند أول إضافة.

---

## 🔑 كلمة مرور الأدمن
افتح الملف:
```
admin/src/main/java/com/rook/sport/admin/data/AdminAuth.kt
```
وغيّر:
```kotlin
const val PASSWORD = "rook2026"   // ← ضع كلمتك السرية
```

---

## 🚀 بعد ذلك: ابنِ التطبيقين
ارفع المشروع على GitHub (مع ملفّي google-services.json) → تبويب Actions → Run workflow.
ستحصل على ملفين:
- **ROOK-Sport-USER-APK** → انشره للمستخدمين.
- **ROOK-Sport-ADMIN-APK** → احتفظ به لك وحدك.

---

## ❓ أسئلة شائعة
| سؤال | جواب |
|------|------|
| هل Firebase مجاني؟ | نعم، الخطة المجانية (Spark) تكفي لآلاف المستخدمين. |
| هل أحتاج بطاقة بنكية؟ | لا، الخطة المجانية لا تتطلب ذلك. |
| التعديل يظهر فوراً؟ | نعم، بمجرد أن يحفظ الأدمن، يراه المستخدم عند فتح/تحديث التطبيق. |
| نسيت كلمة مرور الأدمن؟ | غيّرها من ملف AdminAuth.kt وأعد البناء. |
