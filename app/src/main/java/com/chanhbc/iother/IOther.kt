package com.chanhbc.iother

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Vibrator
import android.provider.Settings
import android.util.TypedValue
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import java.io.*
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

@Suppress("unused", "MemberVisibilityCanBePrivate", "SameParameterValue")
class IOther private constructor(private val mContext: Context) {

    val appName: String
        get() {
            try {
                val packageManager = mContext.packageManager
                val applicationInfo = packageManager.getApplicationInfo(mContext.packageName, 0)
                return packageManager.getApplicationLabel(applicationInfo).toString()
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return ""
        }

    val versionName: String
        get() {
            try {
                val packageName = mContext.packageName
                val info = mContext.packageManager.getPackageInfo(packageName, 0)
                return info.versionName
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return ""
        }

    val versionCode: Int
        get() {
            try {
                val packageName = mContext.packageName
                val info = mContext.packageManager.getPackageInfo(packageName, 0)
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    info.longVersionCode.toInt()
                } else {
                    info.versionCode
                }
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return 0
        }

    val applicationDrawable: Drawable?
        get() {
            try {
                val packageManager = mContext.packageManager
                return packageManager.getApplicationIcon(mContext.packageName)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return null
        }

    val applicationBitmap: Bitmap?
        get() = applicationDrawable?.let { drawableToBitmap(it) }

    val statusBarHeight: Int
        get() {
            val resourceId =
                mContext.resources.getIdentifier("status_bar_height", "dimen", "android")
            return if (resourceId > 0) {
                mContext.resources.getDimensionPixelSize(resourceId)
            } else 0
        }

    val navigationBarHeight: Int
        get() {
            val isNavigationBar = mContext.resources.getBoolean(
                mContext.resources.getIdentifier(
                    "config_showNavigationBar",
                    "bool",
                    "android"
                )
            )
            val resourceId =
                mContext.resources.getIdentifier("navigation_bar_height", "dimen", "android")
            return if (resourceId > 0 && isNavigationBar) {
                mContext.resources.getDimensionPixelSize(resourceId)
            } else 0
        }

    @Deprecated("No longer used")
    @SuppressLint("MissingPermission")
    fun runVibrate(millisecond: Long) {
        val v = mContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        v.vibrate(millisecond)
    }

    /**
     * MyService.class.toString().replace("class ", "")
     */

    @SuppressLint("DefaultLocale")
    fun checkServiceRunning(str: String): Boolean {
        val manager = mContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val services = manager.getRunningServices(Integer.MAX_VALUE)
        for (info in services) {
            if (info.service.className.toUpperCase() == str.toUpperCase()) {
                return true
            }
        }
        return false
    }

    fun privacyPolicy(pack: String) {
        val uri = Uri.parse(pack)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        this.startActivity(intent)
    }

    fun getColorResource(id: Int): Int {
        return ResourcesCompat.getColor(mContext.resources, id, null)
    }

    fun isPackageInstalled(packageName: String): Boolean {
        try {
            mContext.packageManager.getPackageInfo(packageName, 0)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return false
    }

    fun openMarket() {
        val appPackageName = mContext.packageName
        openMarket(appPackageName)
    }

    fun openMarket(packageName: String) {
        val playStorePackageName = "com.android.vending"
        if (isPackageInstalled(playStorePackageName)) {
            try {
                this.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                    )
                )
            } catch (ignored: ActivityNotFoundException) {
                this.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$packageName")
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {
            try {
                this.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$packageName")
                    )
                )
            } catch (ignored: ActivityNotFoundException) {
                this.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    fun getJsonFromAssets(path: String): String {
        try {
            val inputStream = mContext.assets.open(path)
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            val stringBuilder = StringBuilder()
            var line: String? = bufferedReader.readLine()
            while (line != null) {
                stringBuilder.append(line)
                line = bufferedReader.readLine()
            }
            inputStream.close()
            inputStreamReader.close()
            bufferedReader.close()
            return stringBuilder.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return IConstant.EMPTY
    }

    private fun startActivity(activity: Intent) {
        try {
            activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            mContext.startActivity(activity)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun startActivityForResult(
        contextActivity: Context,
        activity: Intent,
        requestCode: Int
    ) {
        try {
            (contextActivity as Activity).startActivityForResult(activity, requestCode)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    fun checkPermission(contextActivity: Context) {
        val manufacturerXiaomi = "xiaomi"
        val manufacturerHuawei = "huawei"
        if (manufacturerXiaomi.equals(Build.MANUFACTURER, ignoreCase = true)) {
            if (!IShared.getInstance(mContext).getBoolean(IConstant.PERMISSION_AUTO_START, false)) {
                val alertDialog = AlertDialog.Builder(contextActivity)
                    .setTitle("Notification")
                    .setMessage("Device Xiaomi need auto start permission, you can turn on this permission?")
                    .setPositiveButton("Yes") { dialog, _ ->
                        dialog.dismiss()
                        val intent = Intent()
                        intent.component = ComponentName(
                            "com.miui.securitycenter",
                            "com.miui.permcenter.autostart.AutoStartManagementActivity"
                        )
                        this@IOther.startActivity(intent)
                        IShared.getInstance(mContext)
                            .putBoolean(IConstant.PERMISSION_AUTO_START, true)
                    }
                    .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }.create()
                alertDialog.show()
            }
        }
        if (manufacturerHuawei.equals(Build.MANUFACTURER, ignoreCase = true)) {
            if (!IShared.getInstance(mContext).getBoolean(IConstant.PERMISSION_AUTO_START, false)) {
                val alertDialog = AlertDialog.Builder(contextActivity)
                    .setTitle("Notification")
                    .setMessage("Device Huawei need protected permission, you can turn on this permission?")
                    .setPositiveButton("Yes") { dialog, _ ->
                        dialog.dismiss()
                        val intent = Intent()
                        intent.setClassName(
                            "com.huawei.systemmanager",
                            "com.huawei.systemmanager.optimize.process.ProtectActivity"
                        )
                        this@IOther.startActivity(intent)
                        IShared.getInstance(mContext)
                            .putBoolean(IConstant.PERMISSION_AUTO_START, true)
                    }
                    .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }.create()
                alertDialog.show()
            }
        }
    }

    fun checkDrawOverlaysPermission(contextActivity: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(mContext)) {
            val alertDialog = AlertDialog.Builder(contextActivity)
                .setTitle("Permission")
                .setMessage("Application need draw overlays permission, you can turn on this permission?")
                .setPositiveButton("Yes") { dialog, _ ->
                    dialog.dismiss()
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + mContext.packageName)
                    )
                    startActivityForResult(
                        contextActivity,
                        intent,
                        IConstant.REQUEST_CODE_DRAW_OVERLAY_PERMISSIONS
                    )
                }
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }.create()
            alertDialog.show()
            return false
        }
        return true
    }

    fun spToPx(px: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_PX,
            px.toFloat(),
            mContext.resources.displayMetrics
        ).toInt()
    }

    fun refreshGallery(path: String) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        mediaScanIntent.data = Uri.fromFile(File(path))
        mContext.sendBroadcast(mediaScanIntent)
    }

    fun getPathFile(folder: String?): String? {
        return mContext.getExternalFilesDir(folder)?.absolutePath
    }

    fun shareBitmapCache() {
        val imagePath = File(mContext.cacheDir, "images")
        if (imagePath.exists()) {
            val newFile = File(imagePath, "image_cache.png")
            if (newFile.exists()) {
                val contentUri = FileProvider.getUriForFile(mContext, mContext.packageName, newFile)
                if (contentUri != null) {
                    val shareIntent = Intent()
                    shareIntent.action = Intent.ACTION_SEND
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    shareIntent.setDataAndType(
                        contentUri,
                        mContext.contentResolver.getType(contentUri)
                    )
                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
                    this.startActivity(Intent.createChooser(shareIntent, "Choose an app"))
                }
            }
        }
    }

    enum class TT_TIME {
        SHORT,
        LONG
    }

    fun toast(vararg s: Any?) {
        toast(TT_TIME.SHORT, *s)
    }

    fun toast(type: TT_TIME, vararg s: Any?) {
        val time = if (type == TT_TIME.SHORT) {
            Toast.LENGTH_SHORT
        } else {
            Toast.LENGTH_LONG
        }
        Toast.makeText(mContext, arrayToString(*s.copyOfRange(1, s.size)), time).show()
    }

    fun about() {
        var version = ""
        try {
            version = mContext.packageManager.getPackageInfo(mContext.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        toast(version)
    }

    fun feedback(email: String) {
        feedback(appName, email, versionName)
    }

    fun feedback(app_name: String, supportEmail: String, version: String) {
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse("mailto:")
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmail))
        emailIntent.putExtra(
            Intent.EXTRA_SUBJECT, "Feedback App: " +
                    app_name + "(" + mContext.packageName + ", version: " + version + ")"
        )
        emailIntent.putExtra(Intent.EXTRA_TEXT, "")
        this.startActivity(Intent.createChooser(emailIntent, "Send mail Report App !"))
    }

    fun saveBitmapCache(bitmap: Bitmap, compressFormat: Bitmap.CompressFormat) {
        saveBitmapCache(bitmap, "image_cache", compressFormat)
    }

    fun saveBitmapCache(bitmap: Bitmap, fileName: String, compressFormat: Bitmap.CompressFormat) {
        val filePath = File(mContext.cacheDir, "images")
        saveBitmap(bitmap, filePath, fileName, compressFormat)
    }

    fun saveBitmapCachePNG(bitmap: Bitmap) {
        saveBitmapCache(bitmap, "image_cache", Bitmap.CompressFormat.PNG)
    }

    fun saveBitmapCachePNG(bitmap: Bitmap, fileName: String) {
        val filePath = File(mContext.cacheDir, "images")
        saveBitmap(bitmap, filePath, fileName, Bitmap.CompressFormat.PNG)
    }

    fun getDirCacheImage(fileName: String, compressFormat: Bitmap.CompressFormat): String {
        val fileNameExtension = when (compressFormat) {
            Bitmap.CompressFormat.JPEG -> {
                ".jpeg"
            }
            Bitmap.CompressFormat.WEBP -> {
                ".webp"
            }
            else -> {
                ".png"
            }
        }
        return getDirCacheFile("images", fileName, fileNameExtension)
    }

    fun getDirCacheImagePNG(fileName: String): String {
        return getDirCacheImage(fileName, Bitmap.CompressFormat.PNG)
    }

    fun getDirCacheFile(folder: String, fileName: String, fileNameExtension: String): String {
        val filePath = File(mContext.cacheDir, folder)
        val file = File(filePath.toString() + IConstant.SLASH + fileName + fileNameExtension)
        return file.absolutePath
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var instance: IOther

        fun getInstance(context: Context): IOther {
            if (!::instance.isInitialized) {
                instance = IOther(context)
            }
            return instance
        }

        fun convertPercentToHex(percent: Float): String {
            var hexInt = (percent * 255).toInt()
            if (hexInt > 255) {
                hexInt = 255
            }
            var hex = Integer.toHexString(hexInt)
            if (hex.length < 2) {
                hex = "0" + Integer.toHexString(hexInt)
            }
            return hex
        }

        fun arrayToString(vararg objects: Any?): String {
            if (objects.isEmpty()) {
                return ""
            }
            val result = StringBuilder()
            var i = 0
            while (true) {
                val objectTrim = objects[i].toString().trim { it <= ' ' }
                if (i == objects.size - 1) {
                    return result.append(objectTrim).toString()
                }
                result.append(objectTrim).append(IConstant.SPACE)
                i++
            }
        }

        fun drawableToBitmap(drawable: Drawable): Bitmap? {
            if (drawable is BitmapDrawable) {
                return drawable.bitmap
            }
            val bitmap: Bitmap?
            if (drawable.intrinsicWidth > 0 && drawable.intrinsicHeight > 0) {
                bitmap = Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bitmap!!)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
            } else {
                bitmap = null
            }
            return bitmap
        }

        fun saveBitmapPNG(bitmap: Bitmap) {
            saveBitmap(bitmap, Bitmap.CompressFormat.PNG)
        }

        fun saveBitmap(bitmap: Bitmap, compressFormat: Bitmap.CompressFormat) {
            val path = (Environment.getExternalStorageDirectory().toString()
                    + IConstant.SLASH + Environment.DIRECTORY_PICTURES + IConstant.SLASH)
            val filePath = File(path)

            @SuppressLint("SimpleDateFormat")
            val sdf = SimpleDateFormat("_HH_mm_ss_dd_MM_yyyy")
            val currentDateAndTime = sdf.format(Date())
            val fileName = "IMG$currentDateAndTime"
            saveBitmap(bitmap, filePath, fileName, compressFormat)
        }

        fun saveBitmapPNG(bitmap: Bitmap, fileName: String) {
            saveBitmap(bitmap, fileName, Bitmap.CompressFormat.PNG)
        }

        fun saveBitmap(bitmap: Bitmap, fileName: String, compressFormat: Bitmap.CompressFormat) {
            val path = (Environment.getExternalStorageDirectory().toString()
                    + IConstant.SLASH + Environment.DIRECTORY_PICTURES + IConstant.SLASH)
            val filePath = File(path)
            saveBitmap(bitmap, filePath, fileName, compressFormat)
        }

        fun saveBitmapPNG(bitmap: Bitmap, folder: String, fileName: String) {
            saveBitmap(bitmap, folder, fileName, Bitmap.CompressFormat.PNG)
        }

        fun saveBitmap(
            bitmap: Bitmap,
            folder: String,
            fileName: String,
            compressFormat: Bitmap.CompressFormat
        ) {
            val path = (Environment.getExternalStorageDirectory().toString()
                    + IConstant.SLASH + folder + IConstant.SLASH)
            val filePath = File(path)
            saveBitmap(bitmap, filePath, fileName, compressFormat)
        }

        fun saveBitmapPNG(bitmap: Bitmap, filePath: File, fileName: String) {
            saveBitmap(bitmap, filePath, fileName, Bitmap.CompressFormat.PNG)
        }

        fun saveBitmap(
            bitmap: Bitmap,
            filePath: File,
            fileName: String,
            compressFormat: Bitmap.CompressFormat
        ) {
            try {
                if (!filePath.exists()) {
                    if (!filePath.mkdirs()) {
                        ILog.e("create directory fail")
                    }
                }
                val fileNameExtension: String = when (compressFormat) {
                    Bitmap.CompressFormat.JPEG -> ".jpg"

                    Bitmap.CompressFormat.WEBP -> ".webp"

                    else -> ".png"
                }
                val file =
                    File(filePath.toString() + IConstant.SLASH + fileName + fileNameExtension)
                if (file.exists()) {
                    if (!file.delete()) {
                        ILog.e("delete \"$fileName\" error")
                    }
                }
                val stream = FileOutputStream(file)
                bitmap.compress(compressFormat, 100, stream)
                stream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun getBitmapResize(bitmap: Bitmap, max: Int, filter: Boolean): Bitmap {
            val wb = bitmap.width
            val hb = bitmap.height
            val maxb = if (wb > hb) wb else hb
            val r = maxb.toFloat() / max
            val w = (wb / r).toInt()
            val h = (hb / r).toInt()
            return Bitmap.createScaledBitmap(bitmap, w, h, filter)
        }

        fun unAccent(s: String): String {
            var str = s
            // option special 'Đ-đ' :))
            str = str.replace("Đ", "D").replace("đ", "d")
            val temp = Normalizer.normalize(str, Normalizer.Form.NFD)
            val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
            return pattern.matcher(temp).replaceAll("")
        }

        fun getTimeFormat(timeMilli: Long): String {
            var tmMilli = timeMilli
            tmMilli /= 1000 // milli second
            var tm = ""
            val s: Long
            var m: Long
            val h: Long
            s = tmMilli % 60
            m = (tmMilli - s) / 60
            if (m >= 60) {
                h = m / 60
                m %= 60
                tm += if (h < 10)
                    "0$h:"
                else
                    "$h:"
            }
            tm += if (m < 10)
                "0$m:"
            else
                "$m:"
            tm += if (s < 10)
                "0$s"
            else
                s.toString() + ""
            return tm
        }

        fun getTimeFormatMilliseconds(timeMilli: Long): String {
            var tmMilli = timeMilli
            val ml = (tmMilli % 1000).toInt()
            tmMilli /= 1000 // milli second
            var tm = ""
            val s: Long
            var m: Long
            val h: Long
            s = tmMilli % 60
            m = (tmMilli - s) / 60
            if (m >= 60) {
                h = m / 60
                m %= 60
                tm += if (h < 10)
                    "0$h:"
                else
                    "$h:"
            }
            tm += if (m < 10)
                "0$m:"
            else
                "$m:"
            tm += if (s < 10)
                "0$s"
            else
                s.toString() + ""
            return when {
                ml < 10 -> {
                    "$tm.00$ml"
                }
                ml < 100 -> {
                    "$tm.0$ml"
                }
                else -> {
                    "$tm.$ml"
                }
            }
        }
    }
}
