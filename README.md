# 表单组件说明文档

当前版本 `lib-version`
 
[![lib-version](https://www.jitpack.io/v/hslooooooool/qsos-filepicker.svg)](https://www.jitpack.io/#hslooooooool/qsos-filepicker)

引用:

```groovy
dependencies {
        implementation 'com.github.hslooooooool.qsos-filepicker:[lib-version]'
}
```

## 技术
- Kotlin
- AndroidX
- LiveData

## 功能介绍
提供的功能全部由安卓手机原生实现，包括：

- 拍照
```kotlin
FilePicker.with(this.supportFragmentManager).takeImage(Sources.DEVICE, listener = object : OnTListener<Uri> {
                override fun back(t: Uri) {
                   // 得到 Uri
                }
            })
```
- 相册选择
```kotlin
FilePicker.with(this.supportFragmentManager).takeImage(Sources.ONE, listener = object : OnTListener<Uri> {
                override fun back(t: Uri) {
                   // 得到 Uri
                }
            })
```
- 录像
```kotlin
FilePicker.with(this.supportFragmentManager).takeVideo(Sources.DEVICE, listener = object : OnTListener<Uri> {
                override fun back(t: Uri) {
                   // 得到 Uri
                }
            })
```
- 视频选择
```kotlin
FilePicker.with(this.supportFragmentManager).takeVideo(Sources.ONE, listener = object : OnTListener<Uri> {
                override fun back(t: Uri) {
                   // 得到 Uri
                }
            })
```
- 录音
```kotlin
FilePicker.with(this.supportFragmentManager).takeAudio(Sources.DEVICE, listener = object : OnTListener<Uri> {
                override fun back(t: Uri) {
                   // 得到 Uri
                }
            })
```
- 音频选择
```kotlin
FilePicker.with(this.supportFragmentManager).takeAudio(Sources.ONE, listener = object : OnTListener<Uri> {
                override fun back(t: Uri) {
                   // 得到 Uri
                }
            })
```
- 单个文件选择，可指定文件类型，如`"image/*"`表示所有图片类型
```kotlin
FilePicker.with(this.supportFragmentManager).takeFile(arrayOf("image/*"), listener = object : OnTListener<Uri> {
                override fun back(t: Uri) {
                   // 得到 Uri
                }
            })
```
- 多个文件选择，可指定文件类型，如`"image/*"`表示所有图片类型，进入原生选择界面长按即可触发多选
```kotlin
FilePicker.with(this.supportFragmentManager).takeFiles(listener = object : OnTListener<List<Uri>> {
                override fun back(t: List<Uri>) {
                   // 得到 List<Uri>
                }
            })
```
