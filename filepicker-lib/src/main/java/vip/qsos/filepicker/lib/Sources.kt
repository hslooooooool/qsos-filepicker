package vip.qsos.filepicker.lib

import androidx.annotation.IntDef

/**
 * @author : 华清松
 * 图片选择方式
 */
open class Sources {
    companion object {
        /**设备*/
        const val DEVICE: Int = 1
        /**单类型单选*/
        const val ONE: Int = 2
        /**多类型选择*/
        const val MULTI: Int = 3
        /**自选操作，设备或单选*/
        const val CHOOSER: Int = 4

        /**值设置是否超出范围*/
        fun overNumber(num: Int): Boolean {
            return num < 1 || num > 4
        }
    }

    @IntDef(value = [DEVICE, ONE, CHOOSER])
    @Retention(AnnotationRetention.SOURCE)
    annotation class Type
}

