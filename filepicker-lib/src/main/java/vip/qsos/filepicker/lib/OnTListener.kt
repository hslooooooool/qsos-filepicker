package vip.qsos.filepicker.lib

/**
 * @author : 华清松
 *
 * 泛型回调
 */
interface OnTListener<T> {

    /**获取回调对象*/
    fun back(t: T)

}
