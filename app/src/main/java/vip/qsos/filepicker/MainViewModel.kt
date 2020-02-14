package vip.qsos.filepicker

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * @author : 华清松
 */
class MainViewModel : ViewModel() {
    val mData: MutableLiveData<ArrayList<Uri>> = MutableLiveData()
}