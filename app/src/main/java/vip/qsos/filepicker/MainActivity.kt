package vip.qsos.filepicker

import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.main_activity.*
import vip.qsos.filepicker.lib.FilePicker
import vip.qsos.filepicker.lib.OnTListener
import vip.qsos.filepicker.lib.Sources

/**
 * @author : 华清松
 */
class MainActivity : AppCompatActivity() {

    private val mModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        if (mModel.mData.value == null) {
            mModel.mData.value = arrayListOf()
        }

        file_list.layoutManager = GridLayoutManager(this, 3)
        file_list.adapter = FileAdapter(this, mModel.mData.value!!, object : OnTListener<Int> {
            override fun back(t: Int) {
                mModel.mData.value!!.removeAt(t)
                file_list.adapter?.notifyDataSetChanged()
            }
        })

        file_camera.setOnClickListener {
            FilePicker.with(this.supportFragmentManager).takeImage(Sources.DEVICE, listener = object : OnTListener<Uri> {
                override fun back(t: Uri) {
                    mModel.mData.value!!.add(t)
                    file_list.adapter?.notifyDataSetChanged()
                }
            })
        }
        file_album.setOnClickListener {
            FilePicker.with(this.supportFragmentManager).takeImage(Sources.ONE, listener = object : OnTListener<Uri> {
                override fun back(t: Uri) {
                    mModel.mData.value!!.add(t)
                    file_list.adapter?.notifyDataSetChanged()
                }
            })
        }
        file_video.setOnClickListener {
            FilePicker.with(this.supportFragmentManager).takeVideo(Sources.DEVICE, listener = object : OnTListener<Uri> {
                override fun back(t: Uri) {
                    mModel.mData.value!!.add(t)
                    file_list.adapter?.notifyDataSetChanged()
                }
            })
        }
        file_video2.setOnClickListener {
            FilePicker.with(this.supportFragmentManager).takeVideo(Sources.ONE, listener = object : OnTListener<Uri> {
                override fun back(t: Uri) {
                    mModel.mData.value!!.add(t)
                    file_list.adapter?.notifyDataSetChanged()
                }
            })
        }
        file_audio.setOnClickListener {
            FilePicker.with(this.supportFragmentManager).takeAudio(Sources.DEVICE, listener = object : OnTListener<Uri> {
                override fun back(t: Uri) {
                    mModel.mData.value!!.add(t)
                    file_list.adapter?.notifyDataSetChanged()
                }
            })
        }
        file_audio2.setOnClickListener {
            FilePicker.with(this.supportFragmentManager).takeAudio(Sources.ONE, listener = object : OnTListener<Uri> {
                override fun back(t: Uri) {
                    mModel.mData.value!!.add(t)
                    file_list.adapter?.notifyDataSetChanged()
                }
            })
        }
        file_file.setOnClickListener {
            FilePicker.with(this.supportFragmentManager).takeFile(listener = object : OnTListener<Uri> {
                override fun back(t: Uri) {
                    mModel.mData.value!!.add(t)
                    file_list.adapter?.notifyDataSetChanged()
                }
            })
        }
        file_file2.setOnClickListener {
            FilePicker.with(this.supportFragmentManager).takeFiles(listener = object : OnTListener<List<Uri>> {
                override fun back(t: List<Uri>) {
                    mModel.mData.value!!.addAll(t)
                    file_list.adapter?.notifyDataSetChanged()
                }
            })
        }

        mModel.mData.observe(this, Observer {
            file_list.adapter?.notifyDataSetChanged()
        })
    }

}