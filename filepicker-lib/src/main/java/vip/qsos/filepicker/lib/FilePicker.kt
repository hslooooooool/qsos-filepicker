package vip.qsos.filepicker.lib

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import java.text.SimpleDateFormat
import java.util.*


/**
 * @author : 华清松
 * 媒体文件获取，提供系统的相机拍照、录制，系统的录音，系统的文件选取等功能的实现
 */
class FilePicker : Fragment() {
    /**选择单张*/
    private var publishSubject: OnTListener<Uri>? = null
    /**选择多张*/
    private var publishMultipleSubject: OnTListener<List<Uri>>? = null

    /**是否为多选*/
    private var isMultiple = false
    /**选择方式*/
    private var mTakeType: Int = Sources.ONE
    /**选择文件种类
     * 0 图片 1 视频 2 音频 3 文件
     * */
    private var mFileType: Int = 0
    /**选择界面标题 Sources.CHOOSER 时生效*/
    private var mChooserTitle: String? = "选择"
    /**默认限制录制时长为10秒*/
    private var mLimitTime: Int = 10000
    /**默认文件类型*/
    private var mMimeTypes: Array<String> = arrayOf("*/*")
        set(value) {
            field = value
            mMimeType = if (mMimeTypes.isEmpty()) "*/*" else mMimeTypes[0]
        }
    private var mMimeType: String = "*/*"

    /**图片选择*/
    fun takeImage(@Sources.Type type: Int = Sources.CHOOSER, chooserTitle: String = "图片选择", listener: OnTListener<Uri>) {
        this.mFileType = 0
        this.isMultiple = false
        this.mTakeType = type
        this.mMimeTypes = arrayOf("image/*")
        this.mChooserTitle = chooserTitle
        this.publishSubject = listener
        requestPick()
    }

    /**视频选择*/
    fun takeVideo(@Sources.Type type: Int = Sources.CHOOSER, limitTime: Int = 10000, chooserTitle: String = "视频选择", listener: OnTListener<Uri>) {
        this.mFileType = 1
        this.isMultiple = false
        this.mTakeType = type
        this.mMimeTypes = arrayOf("video/*")
        this.mLimitTime = limitTime
        this.mChooserTitle = chooserTitle
        this.publishSubject = listener
        requestPick()
    }

    /**音频选择*/
    fun takeAudio(@Sources.Type type: Int = Sources.CHOOSER, limitTime: Int = 10000, chooserTitle: String = "音频选择", listener: OnTListener<Uri>) {
        this.mFileType = 2
        this.isMultiple = false
        this.mTakeType = type
        this.mMimeTypes = arrayOf("audio/*")
        this.mLimitTime = limitTime
        this.mChooserTitle = chooserTitle
        this.publishSubject = listener
        requestPick()
    }

    /**文件选择*/
    fun takeFile(mimeTypes: Array<String> = arrayOf("*/*"), chooserTitle: String = "文件选择", listener: OnTListener<Uri>) {
        this.mFileType = 3
        this.isMultiple = false
        this.mTakeType = Sources.ONE
        this.mMimeTypes = mimeTypes
        this.mChooserTitle = chooserTitle
        this.publishSubject = listener
        requestPick()
    }

    /**文件多选*/
    fun takeFiles(mimeTypes: Array<String> = arrayOf("*/*"), listener: OnTListener<List<Uri>>) {
        this.isMultiple = true
        this.mTakeType = Sources.MULTI
        this.mMimeTypes = mimeTypes
        this.publishMultipleSubject = listener
        requestPick()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requestPick()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            var pass = true
            grantResults.forEach {
                if (it != PackageManager.PERMISSION_GRANTED) {
                    pass = false
                }
            }
            if (pass) startPick()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            /**选择结果回调*/
            when (requestCode) {
                Sources.DEVICE -> pushImage(temFileUri)
                Sources.ONE, Sources.MULTI -> handleGalleryResult(data)
                Sources.CHOOSER -> if (isCamera(data)) pushImage(temFileUri) else handleGalleryResult(data)
            }
        }
    }

    /**是否为相机*/
    private fun isCamera(data: Intent?): Boolean {
        return data == null || data.data == null && data.clipData == null
    }

    private fun requestPick() {
        if (isAdded) {
            startPick()
        }
    }

    /**图片选取方式判断*/
    private fun startPick() {
        /**类型超出返回 或 未授予权限*/
        if (Sources.overNumber(mTakeType) || !checkPermission()) {
            return
        }

        var chooseIntent: Intent? = null
        when {
            /**拍照*/
            mTakeType == Sources.DEVICE && mFileType == 0 -> {
                temFileUri = createImageUri()
                chooseIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
                    it.putExtra(MediaStore.EXTRA_OUTPUT, temFileUri)
                    grantWritePermission(context!!, it, temFileUri!!)
                }
            }
            /**拍照或选择*/
            mTakeType == Sources.CHOOSER && mFileType == 0 -> {
                temFileUri = createImageUri()
                chooseIntent = createImageChooserIntent()
            }

            /**视频拍摄*/
            mTakeType == Sources.DEVICE && mFileType == 1 -> {
                temFileUri = createVideoUri()
                chooseIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).also {
                    it.putExtra(MediaStore.EXTRA_DURATION_LIMIT, mLimitTime)
                    it.putExtra(MediaStore.EXTRA_OUTPUT, temFileUri)
                    grantWritePermission(context!!, it, temFileUri!!)
                }
            }
            /**视频拍摄或选择*/
            mTakeType == Sources.CHOOSER && mFileType == 1 -> {
                temFileUri = createVideoUri()
                chooseIntent = createVideoChooserIntent()
            }

            /**音频录制*/
            mTakeType == Sources.DEVICE && mFileType == 2 -> {
                temFileUri = createAudioUri()
                chooseIntent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION).also {
                    it.putExtra(MediaStore.EXTRA_DURATION_LIMIT, mLimitTime)
                    it.putExtra(MediaStore.EXTRA_OUTPUT, temFileUri)
                    grantWritePermission(context!!, it, temFileUri!!)
                }
            }
            /**音频录制或选择*/
            mTakeType == Sources.CHOOSER && mFileType == 2 -> {
                temFileUri = createAudioUri()
                chooseIntent = createAudioChooserIntent()
            }

            /**文件单选*/
            mTakeType == Sources.ONE -> {
                chooseIntent = createPickOne()
            }
            /**文件多选*/
            mTakeType == Sources.MULTI -> {
                chooseIntent = createPickMore()
            }
        }
        activity?.packageManager?.let {
            chooseIntent?.resolveActivity(it)?.let {
                startActivityForResult(chooseIntent, mTakeType)
            }
        }
    }

    /**构建文件单选Intent*/
    private fun createPickOne(): Intent {
        val pictureChooseIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        pictureChooseIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, isMultiple)
        pictureChooseIntent.type = mMimeType
        pictureChooseIntent.putExtra(Intent.EXTRA_MIME_TYPES, mMimeTypes)
        pictureChooseIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        /**临时授权app访问URI代表的文件所有权*/
        pictureChooseIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        return pictureChooseIntent
    }

    /**构建文件多选Intent*/
    private fun createPickMore(): Intent {
        val pictureChooseIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        pictureChooseIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, isMultiple)
        pictureChooseIntent.type = "*/*"
        pictureChooseIntent.putExtra(Intent.EXTRA_MIME_TYPES, mMimeTypes)
        pictureChooseIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        /**临时授权app访问URI代表的文件所有权*/
        pictureChooseIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        return pictureChooseIntent
    }

    /**构建图片选择器Intent*/
    private fun createImageChooserIntent(): Intent {
        val cameraIntents = ArrayList<Intent>()
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val packageManager = context!!.packageManager
        val camList = packageManager.queryIntentActivities(captureIntent, 0)
        for (res in camList) {
            val packageName = res.activityInfo.packageName
            val intent = Intent(captureIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(packageName)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, temFileUri)
            grantWritePermission(context!!, intent, temFileUri!!)
            cameraIntents.add(intent)
        }
        Intent.createChooser(createPickMore(), mChooserTitle).also {
            it.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toTypedArray())
            return it
        }
    }

    /**构建视频选择器Intent*/
    private fun createVideoChooserIntent(): Intent {
        val cameraIntents = ArrayList<Intent>()
        val captureIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        // 某些手机此设置是不生效的，需要自行封装解决
        captureIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, mLimitTime)
        val packageManager = context!!.packageManager
        val camList = packageManager.queryIntentActivities(captureIntent, 0)
        for (res in camList) {
            val packageName = res.activityInfo.packageName
            val intent = Intent(captureIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(packageName)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, temFileUri)
            grantWritePermission(context!!, intent, temFileUri!!)
            cameraIntents.add(intent)
        }
        Intent.createChooser(createPickMore(), mChooserTitle).also {
            it.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toTypedArray())
            return it
        }
    }

    /**构建音频选择器Intent*/
    private fun createAudioChooserIntent(): Intent {
        val cameraIntents = ArrayList<Intent>()
        val captureIntent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
        val packageManager = context!!.packageManager
        val camList = packageManager.queryIntentActivities(captureIntent, 0)
        for (res in camList) {
            val packageName = res.activityInfo.packageName
            val intent = Intent(captureIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(packageName)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, temFileUri)
            grantWritePermission(context!!, intent, temFileUri!!)
            cameraIntents.add(intent)
        }
        Intent.createChooser(createPickMore(), mChooserTitle).also {
            it.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toTypedArray())
            return it
        }
    }

    /**创建拍照保存路径*/
    private fun createImageUri(): Uri? {
        val timeStamp: String = "image_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val contentResolver = activity!!.contentResolver
        val cv = ContentValues()
        cv.put(MediaStore.Images.Media.TITLE, timeStamp)
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv)
    }

    /**创建录像保存路径*/
    private fun createVideoUri(): Uri? {
        val timeStamp: String = "video_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val contentResolver = activity!!.contentResolver
        val cv = ContentValues()
        cv.put(MediaStore.Video.Media.TITLE, timeStamp)
        return contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, cv)
    }

    /**创建录音保存路径*/
    private fun createAudioUri(): Uri? {
        val timeStamp: String = "audio_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val contentResolver = activity!!.contentResolver
        val cv = ContentValues()
        cv.put(MediaStore.Audio.Media.TITLE, timeStamp)
        return contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, cv)
    }

    /**检查权限*/
    private fun checkPermission(): Boolean {
        var pass = true
        for (p in permissions) {
            if (ContextCompat.checkSelfPermission(activity!!, p) != PackageManager.PERMISSION_GRANTED) {
                pass = false
            }
        }
        if (!pass) {
            ActivityCompat.requestPermissions(activity!!, permissions, 0)
        }
        return pass
    }

    /**申请文件读写权限*/
    private fun grantWritePermission(context: Context, intent: Intent, uri: Uri) {
        val resInfoList = context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    /**获取选择结果*/
    private fun handleGalleryResult(data: Intent?) {
        if (isMultiple) {
            val imageUris = ArrayList<Uri>()
            val clipData = data?.clipData
            if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    imageUris.add(clipData.getItemAt(i).uri)
                }
            } else {
                data?.data?.let { imageUris.add(it) }
            }
            pushImageList(imageUris)
        } else {
            pushImage(data?.data)
        }
    }

    /**传递选择的多图*/
    private fun pushImageList(uris: List<Uri>) {
        publishMultipleSubject?.back(uris)
    }

    /**传递选择的单图*/
    private fun pushImage(uri: Uri?) {
        uri?.let {
            publishSubject?.back(uri)
        }
    }

    companion object {

        private val TAG = FilePicker::class.java.name
        /**拍照存储URI*/
        private var temFileUri: Uri? = null
        /**工具所需权限*/
        var permissions = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        /**获取RxImagePicker实例*/
        fun with(fm: FragmentManager): FilePicker {
            var rxImagePickerFragment = fm.findFragmentByTag(TAG) as FilePicker?
            if (rxImagePickerFragment == null) {
                rxImagePickerFragment = FilePicker()
                fm.beginTransaction().add(rxImagePickerFragment, TAG).commit()
            }
            return rxImagePickerFragment
        }
    }

}
