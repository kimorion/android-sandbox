package com.kimorion.ep1

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kimorion.ep1.databinding.ActivityMainBinding
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var appViewModel: AppViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(activityMainBinding.root)
        val model: AppViewModel by viewModels()
        appViewModel = model

        val alignmentTextView = activityMainBinding.alignmentTextView
        val styleTextView = activityMainBinding.styleTextView

        model.currentAlignment.observe(this, {
            alignmentTextView.gravity = it ?: alignmentTextView.gravity
        })
        model.currentStyle.observe(this, {
            val newTypeFace = if (it == Typeface.NORMAL) null else styleTextView.typeface
            styleTextView.setTypeface(newTypeFace, it ?: styleTextView.typeface.style)
        })
        model.currentColor.observe(this, {
            alignmentTextView.setTextColor(it ?: alignmentTextView.textColors)
            styleTextView.setTextColor(it ?: styleTextView.textColors)
        })
        model.currentSize.observe(this, {
            alignmentTextView.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                it ?: alignmentTextView.textSize
            )
            styleTextView.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                it ?: styleTextView.textSize
            )
        })

        registerForContextMenu(alignmentTextView)
        registerForContextMenu(styleTextView)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)

        return true
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = menuInflater

        when (v!!.id) {
            R.id.styleTextView -> {
                inflater.inflate(R.menu.style_context_menu, menu)
            }
            R.id.alignmentTextView -> {
                inflater.inflate(R.menu.alignment_context_menu, menu)
            }
            else -> throw Exception("There is no context menu for given view")
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.left_option -> {
                appViewModel.currentAlignment.postValue(Gravity.START)
            }
            R.id.center_option -> {
                appViewModel.currentAlignment.postValue(Gravity.CENTER)
            }
            R.id.right_option -> {
                appViewModel.currentAlignment.postValue(Gravity.END)
            }
            R.id.bold_option -> {
                appViewModel.currentStyle.postValue(Typeface.BOLD)
            }
            R.id.italic_option -> {
                appViewModel.currentStyle.postValue(Typeface.ITALIC)
            }
            R.id.normal_option -> {
                appViewModel.currentStyle.postValue(Typeface.NORMAL)
            }

            else -> return super.onContextItemSelected(item)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val colorToggleButton = activityMainBinding.colorToggleButton
        val sizeToggleButton = activityMainBinding.sizeToggleButton
        
        when (item.itemId) {
            R.id.apply_settings -> {
                if (colorToggleButton.isChecked) {
                    appViewModel.currentColor.postValue(ColorStateList.valueOf(Color.BLUE))
                } else {
                    appViewModel.currentColor.postValue(ColorStateList.valueOf(Color.GRAY))
                }

                if (sizeToggleButton.isChecked) {
                    appViewModel.currentSize.postValue(32f)
                } else {
                    appViewModel.currentSize.postValue(24f)
                }
            }
            R.id.reset_settings -> {
                appViewModel.currentColor.postValue(ColorStateList.valueOf(Color.GRAY))
                appViewModel.currentStyle.postValue(Typeface.NORMAL)
                appViewModel.currentAlignment.postValue(Gravity.CENTER)
                appViewModel.currentSize.postValue(24f)

                colorToggleButton.isChecked = false
                sizeToggleButton.isChecked = false
            }
        }

        return true
    }
}

class AppViewModel : ViewModel() {
    var currentColor: MutableLiveData<ColorStateList?> = MutableLiveData()
    var currentSize: MutableLiveData<Float?> = MutableLiveData()
    var currentAlignment: MutableLiveData<Int?> = MutableLiveData()
    var currentStyle: MutableLiveData<Int?> = MutableLiveData()
}

