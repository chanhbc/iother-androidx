package com.chanhbc.iother

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.NinePatchDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Vibrator
import android.provider.Settings
import android.util.TypedValue
import android.widget.Toast
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

    val versionCode: Long
        get() {
            try {
                val packageName = mContext.packageName
                val info = mContext.packageManager.getPackageInfo(packageName, 0)
                return info.longVersionCode
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
    @Deprecated("As of {@link android.os.Build.VERSION_CODES#O}")
    @SuppressLint("DefaultLocale")
    fun checkServiceRunning(str: String): Boolean {
        val manager = mContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val services = manager.getRunningServices(Integer.MAX_VALUE)
        for (info in services) {
            if (info.service.className.equals(str, ignoreCase = true)) {
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

    fun checkPermission(context: Context, permission: String): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    fun isFirstOpenNewVersion(): Boolean {
        return isFirstOpenNewVersion("is_first_open_new_version")
    }

    fun isFirstOpenNewVersion(key: String): Boolean {
        val versionNameOld = IShared.getInstance(mContext).getString(key)
        if (versionName != versionNameOld) {
            IShared.getInstance(mContext).putString(key, versionName)
            return true
        }
        return false
    }

    fun getBitmapNineFromAsset(filePath: String): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val inputStream = mContext.assets.open(filePath)
            val drawable = Drawable.createFromStream(inputStream, null)
            if (drawable is NinePatchDrawable) {
                drawable.setBounds(0, 0, 200, 200)
            }
            bitmap = drawableToBitmap(drawable)
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }

    fun checkPermissionAutoStart(contextActivity: Context) {
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

    @Deprecated("No used. Callers should migrate to inserting items directly into {@link MediaStore }")
    fun refreshGallery(path: String) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        mediaScanIntent.data = Uri.fromFile(File(path))
        mContext.sendBroadcast(mediaScanIntent)
    }

    fun getPathFile(folder: String?): String? {
        return mContext.getExternalFilesDir(folder)?.absolutePath
    }

    fun toast(vararg s: Any?) {
        toast(IConstant.TT_TIME.SHORT, *s)
    }

    fun toast(type: IConstant.TT_TIME, vararg s: Any?) {
        val time = if (type == IConstant.TT_TIME.SHORT) {
            Toast.LENGTH_SHORT
        } else {
            Toast.LENGTH_LONG
        }
        Toast.makeText(mContext, arrayToString(*s), time).show()
    }

    fun about() {
        toast(versionName)
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

    fun shareBitmapCache() {
        val imagePath = File(mContext.cacheDir, "images")
        val fileShare = File(imagePath, "image_cache.png")
        if (fileShare.exists()) {
            shareBitmapCache(fileShare)
        }
    }

    fun shareBitmapCache(filePath: String) {
        val fileShare = File(filePath)
        if (fileShare.exists()) {
            shareBitmapCache(fileShare)
        }
    }

    fun shareBitmapCache(fileShare: File) {
        val contentUri = FileProvider.getUriForFile(mContext, mContext.packageName, fileShare)
        if (contentUri != null) {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            shareIntent.setDataAndType(
                contentUri,
                mContext.contentResolver.getType(contentUri)
            )
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
            this.startActivity(Intent.createChooser(shareIntent, "Choose an app: "))
        }
    }

    fun saveBitmapCache(
        bitmap: Bitmap, compressFormat: Bitmap.CompressFormat,
        callback: (isDone: Boolean, path: String?) -> Unit
    ) {
        saveBitmapCache(bitmap, IConstant.IMAGE_CACHE_FILE, compressFormat, callback)
    }

    fun saveBitmapCachePNG(
        bitmap: Bitmap,
        callback: (isDone: Boolean, path: String?) -> Unit
    ) {
        saveBitmapCache(bitmap, IConstant.IMAGE_CACHE_FILE, Bitmap.CompressFormat.PNG, callback)
    }

    fun saveBitmapCache(
        bitmap: Bitmap, fileName: String, compressFormat: Bitmap.CompressFormat,
        callback: (isDone: Boolean, path: String?) -> Unit
    ) {
        val filePath = File(mContext.cacheDir, IConstant.IMAGE_FOLDER)
        saveBitmap(bitmap, filePath, fileName, compressFormat, callback)
    }

    fun saveBitmapCachePNG(
        bitmap: Bitmap, fileName: String,
        callback: (isDone: Boolean, path: String?) -> Unit
    ) {
        val filePath = File(mContext.cacheDir, IConstant.IMAGE_FOLDER)
        saveBitmap(bitmap, filePath, fileName, Bitmap.CompressFormat.PNG, callback)
    }

    fun saveBitmapCache(
        bitmap: Bitmap, folder: String, fileName: String, compressFormat: Bitmap.CompressFormat,
        callback: (isDone: Boolean, path: String?) -> Unit
    ) {
        val filePath = File(mContext.cacheDir, folder)
        saveBitmap(bitmap, filePath, fileName, compressFormat, callback)
    }

    fun saveBitmapCachePNG(
        bitmap: Bitmap, folder: String, fileName: String,
        callback: (isDone: Boolean, path: String?) -> Unit
    ) {
        val filePath = File(mContext.cacheDir, folder)
        saveBitmap(bitmap, filePath, fileName, Bitmap.CompressFormat.PNG, callback)
    }

    fun getDirCacheImage(fileName: String, compressFormat: Bitmap.CompressFormat): String {
        return getDirCacheFile(IConstant.IMAGE_FOLDER, fileName, getExtensionName(compressFormat))
    }

    fun getDirCacheImagePNG(fileName: String): String {
        return getDirCacheImage(fileName, Bitmap.CompressFormat.PNG)
    }

    fun getDirCacheFile(folder: String, fileName: String, fileNameExtension: String): String {
        val filePath = File(mContext.cacheDir, folder)
        val file = File(filePath.toString() + IConstant.SLASH + fileName + fileNameExtension)
        return file.absolutePath
    }

    fun getRawIdFromName(name: String): Int {
        return getResIdFromName(name, IConstant.RAW)
    }

    fun getDimenIdFromName(name: String): Int {
        return getResIdFromName(name, IConstant.DIMEN)
    }

    fun getStringIdFromName(name: String): Int {
        return getResIdFromName(name, IConstant.STRING)
    }

    fun getDrawableIdFromName(name: String): Int {
        return getResIdFromName(name, IConstant.DRAWABLE)
    }

    fun getStyleIdFromName(name: String): Int {
        return getResIdFromName(name, IConstant.STYLE)
    }

    fun getResIdFromName(name: String, folder: String): Int {
        return getResIdFromName(name, folder, mContext.packageName)
    }

    fun getResIdFromName(name: String, folder: String, packageName: String): Int {
        return mContext.resources.getIdentifier(name, folder, packageName)
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

        /*
        * value from [0:255]
        * hex: value [00:FF]
        * */
        fun convertAlphaToHex(alpha: Int): String {
            var hex = Integer.toHexString(alpha)
            if (hex.length < 2) {
                hex = "0" + Integer.toHexString(alpha)
            }
            return hex
        }

        /*
        * value from [0:100]
        * hex: value [00:FF]
        * */
        fun convertPercentToHex(percent: Float): String {
            val alpha: Int = (255 * when {
                percent < 0 -> {
                    0F
                }
                percent > 100 -> {
                    100F
                }
                else -> {
                    percent
                }
            }).toInt()
            return convertAlphaToHex(alpha)
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

        fun saveBitmapPNG(
            bitmap: Bitmap,
            callback: (isDone: Boolean, path: String?) -> Unit
        ) {
            saveBitmap(bitmap, Bitmap.CompressFormat.PNG, callback)
        }

        fun saveBitmap(
            bitmap: Bitmap, compressFormat: Bitmap.CompressFormat,
            callback: (isDone: Boolean, path: String?) -> Unit
        ) {
            val path = (Environment.getExternalStorageDirectory().toString()
                    + IConstant.SLASH + Environment.DIRECTORY_PICTURES + IConstant.SLASH)
            val filePath = File(path)

            @SuppressLint("SimpleDateFormat")
            val sdf = SimpleDateFormat("_HH_mm_ss_dd_MM_yyyy")
            val currentDateAndTime = sdf.format(Date())
            val fileName = "IMG$currentDateAndTime"
            saveBitmap(bitmap, filePath, fileName, compressFormat, callback)
        }

        fun saveBitmapPNG(
            bitmap: Bitmap, fileName: String,
            callback: (isDone: Boolean, path: String?) -> Unit
        ) {
            saveBitmap(bitmap, fileName, Bitmap.CompressFormat.PNG, callback)
        }

        fun saveBitmap(
            bitmap: Bitmap, fileName: String, compressFormat: Bitmap.CompressFormat,
            callback: (isDone: Boolean, path: String?) -> Unit
        ) {
            val path = (Environment.getExternalStorageDirectory().toString()
                    + IConstant.SLASH + Environment.DIRECTORY_PICTURES + IConstant.SLASH)
            val filePath = File(path)
            saveBitmap(bitmap, filePath, fileName, compressFormat, callback)
        }

        fun saveBitmapPNG(
            bitmap: Bitmap, folder: String, fileName: String,
            callback: (isDone: Boolean, path: String?) -> Unit
        ) {
            saveBitmap(bitmap, folder, fileName, Bitmap.CompressFormat.PNG, callback)
        }

        fun saveBitmap(
            bitmap: Bitmap,
            folder: String,
            fileName: String,
            compressFormat: Bitmap.CompressFormat,
            callback: (isDone: Boolean, path: String?) -> Unit
        ) {
            val path = (Environment.getExternalStorageDirectory().toString()
                    + IConstant.SLASH + folder + IConstant.SLASH)
            val filePath = File(path)
            saveBitmap(bitmap, filePath, fileName, compressFormat, callback)
        }

        fun saveBitmapPNG(
            bitmap: Bitmap, filePath: File, fileName: String,
            callback: (isDone: Boolean, path: String?) -> Unit
        ) {
            saveBitmap(bitmap, filePath, fileName, Bitmap.CompressFormat.PNG, callback)
        }

        fun saveBitmap(
            bitmap: Bitmap,
            filePath: File,
            fileName: String,
            compressFormat: Bitmap.CompressFormat,
            callback: (isDone: Boolean, path: String?) -> Unit
        ) {
            Thread {
                try {
                    if (!filePath.exists()) {
                        if (!filePath.mkdirs()) {
                            ILog.e("create directory fail")
                        }
                    }
                    val file =
                        File(filePath.toString() + IConstant.SLASH + fileName + getExtensionName(compressFormat))
                    if (file.exists()) {
                        if (!file.delete()) {
                            ILog.e("delete \"$fileName\" error")
                        }
                    }
                    val stream = FileOutputStream(file)
                    bitmap.compress(compressFormat, 100, stream)
                    stream.close()
                    callback.invoke(true, file.absolutePath)
                } catch (e: IOException) {
                    e.printStackTrace()
                    callback.invoke(false, null)
                }
            }.start()
        }

        fun getExtensionName(compressFormat: Bitmap.CompressFormat): String{
            return when(compressFormat) {
                Bitmap.CompressFormat.JPEG -> {
                    ".jpg"
                }
                Bitmap.CompressFormat.PNG -> {
                    ".png"
                }
                else -> {
                    ".webp"
                }
            }
        }

        fun getBitmapResize(bitmap: Bitmap, max: Int, filter: Boolean): Bitmap {
            val wb = bitmap.width
            val hb = bitmap.height
            val maxWb = if (wb > hb) wb else hb
            val r = maxWb.toFloat() / max
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
            val h: Long
            val s: Long = tmMilli % 60
            var m: Long = (tmMilli - s) / 60
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
            var m: Long
            val h: Long
            val s: Long = tmMilli % 60
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
