package com.lianyi.paimonsnotebook.util

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.lianyi.paimonsnotebook.R
import com.lianyi.paimonsnotebook.bean.account.UserBean
import com.lianyi.paimonsnotebook.databinding.PopConfirmBinding
import com.lianyi.paimonsnotebook.databinding.PopLoadingBinding
import com.lianyi.paimonsnotebook.databinding.PopSuccessBinding
import com.lianyi.paimonsnotebook.lib.information.Constants
import com.lianyi.paimonsnotebook.lib.information.JsonCacheName
import com.lianyi.paimonsnotebook.lib.listener.AnimatorFinished
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.unit.Subunits
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


class PaiMonsNoteBook : Application(){
    companion object{
        lateinit var context: Context
        lateinit var loadingWindow:AlertDialog
    }
    override fun onCreate() {
        super.onCreate()
        context = baseContext

        //初始化自适应
        AutoSizeConfig.getInstance()
            .setCustomFragment(true)
            .unitsManager
            .supportSubunits = Subunits.MM

        //设置mainUser
        mainUser = GSON.fromJson(usp.getString(JsonCacheName.MAIN_USER_NAME,""), UserBean::class.java)

        if(mainUser ==null) mainUser = UserBean()
    }
}

fun loadImage(imageView: ImageView, url:String?){
    Glide.with(imageView).load(url?:"").into(imageView)
}

var mainUser: UserBean? = null
    get() = field

//数据缓存
val sp: SharedPreferences
get() = PaiMonsNoteBook.context.getSharedPreferences(Constants.SP_NAME,Context.MODE_PRIVATE)
//用户信息缓存
val usp:SharedPreferences
get() = PaiMonsNoteBook.context.getSharedPreferences(Constants.USP_NAME,Context.MODE_PRIVATE)
//角色缓存
val csp:SharedPreferences
get() = PaiMonsNoteBook.context.getSharedPreferences(Constants.CSP_NAME,Context.MODE_PRIVATE)
//武器缓存
val wsp:SharedPreferences
get() = PaiMonsNoteBook.context.getSharedPreferences(Constants.WSP_NAME,Context.MODE_PRIVATE)


val Int.dp
    get() = PaiMonsNoteBook.context.resources.displayMetrics.density * this +0.5f

val Int.sp
    get() = (this * PaiMonsNoteBook.context.resources.displayMetrics.scaledDensity + 0.5f)

val Int.px2dp
    get() = (this /  PaiMonsNoteBook.context.resources.displayMetrics.density +0.5f)

fun String.show(){
    val toast = Toast(PaiMonsNoteBook.context)
    Toast.makeText(PaiMonsNoteBook.context,this,Toast.LENGTH_SHORT).show()
}

fun String.showLong(){
    Toast.makeText(PaiMonsNoteBook.context,this,Toast.LENGTH_LONG).show()
}

inline fun <reified T>goA(){
    val intent = Intent(PaiMonsNoteBook.context,T::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    PaiMonsNoteBook.context.startActivity(intent)
}

fun View.gone(){
    this.visibility = View.GONE
}
fun View.show(){
    this.visibility = View.VISIBLE
}

//tab layout选中回调
fun TabLayout.tab(block:(Int)->Unit){
    this.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
        override fun onTabSelected(p0: TabLayout.Tab?) {
            block(p0!!.position)
        }

        override fun onTabUnselected(p0: TabLayout.Tab?) {
        }

        override fun onTabReselected(p0: TabLayout.Tab?) {
        }
    })
}

//动画完成回调
fun Animation.onFinished(block: () -> Unit){
    this.setAnimationListener(object :Animation.AnimationListener{
        override fun onAnimationStart(p0: Animation?) {
        }
        override fun onAnimationEnd(p0: Animation?) {
            block()
        }

        override fun onAnimationRepeat(p0: Animation?) {
        }
    })
}

//checkbox选中时回调
fun AppCompatCheckBox.select(block: (Boolean) -> Unit){
    this.setOnCheckedChangeListener { p0, p1 -> block(p1) }
}

//关闭加载窗口
fun loadingWindowDismiss(){
    PaiMonsNoteBook.loadingWindow.dismiss()
}

//加载时弹窗
fun showLoading(context: Context){
    //加载布局
    val layout = LayoutInflater.from(context).inflate(R.layout.pop_loading,null)
    val item = PopLoadingBinding.bind(layout)
    val card = CardView(context)
    val win = androidx.appcompat.app.AlertDialog.Builder(context).setView(card).create()
    card.addView(layout)
    card.cardElevation = 0f
    card.radius = 10.dp

    //禁止通过点击返回关闭
    win.setOnKeyListener { p0, p1, p2 ->
        when (p1) {
            KeyEvent.KEYCODE_BACK -> true
            else -> false
        }
    }

    //禁止通过点击window外面的区域关闭
    win.setCancelable(false)
    PaiMonsNoteBook.loadingWindow = win

    if(PaiMonsNoteBook.loadingWindow.isShowing) loadingWindowDismiss()

    //设置弹窗的内部布局
    win.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    win.window?.decorView?.setPadding(10.dp.toInt(),0,10.dp.toInt(),0)
    win.window?.setLayout(PaiMonsNoteBook.context.resources.displayMetrics.widthPixels-20.dp.toInt(),
        ViewGroup.LayoutParams.WRAP_CONTENT)

    win.show()

    //设置动画
    val loadingWidth = PaiMonsNoteBook.context.resources.displayMetrics.widthPixels - 123.dp

    val animGo = ObjectAnimator.ofFloat(item.iconKlee,"translationX",10f,loadingWidth)
    animGo.duration = 1500L

    val animBack = ObjectAnimator.ofFloat(item.iconKlee,"translationX",loadingWidth,10f)
    animBack.duration = 1500L

    val rotateBack =  ObjectAnimator.ofFloat(item.iconKlee,"rotationY",0f,180f)
    rotateBack.duration = 500L

    val rotateGo =  ObjectAnimator.ofFloat(item.iconKlee,"rotationY",180f,0f)
    rotateGo.duration = 500L

    val animSet = AnimatorSet()
    animSet.play(animGo).before(rotateBack)
    animSet.play(rotateBack).before(animBack)
    animSet.play(animBack).before(rotateGo)
    animSet.start()

    animSet.addListener(AnimatorFinished{
        if(PaiMonsNoteBook.loadingWindow.isShowing){
            animSet.start()
        }
    })

}

//设置组件宽度 期间动画
fun openAndCloseAnimationHor(target:View, start:Int, end:Int, time:Long){
    val anim = ValueAnimator.ofInt(start.dp.toInt(), end.dp.toInt())
    anim.duration = time
    anim.addUpdateListener {
        target.layoutParams.width = it.animatedValue as Int
        target.requestLayout()
    }
    anim.start()
}

fun openAndCloseAnimationVer(target:View, start:Int, end:Int, time:Long){
    val anim = ValueAnimator.ofInt(start, end)
    anim.duration = time
    anim.addUpdateListener {
        target.layoutParams.height = it.animatedValue as Int
        target.requestLayout()
    }
    anim.start()
}

fun String.toMyRequestBody(): RequestBody {
    return this.toRequestBody("application/json;charset=utf-8".toMediaType())
}

fun String.substring(start:String, end:String):String{
    val from = if(start.isEmpty()){
        0
    }else{
        val index = this.indexOf(start)
        if(index==-1) 0 else index
    }
    val to = if(end.isEmpty()){
        0
    }else{
        val index = this.indexOf(end)
        if(index==-1) 0 else index
    }

    return this.substring(from,to)
}

fun Spinner.select(block: (position:Int,id:Long) -> Unit){
    this.onItemSelectedListener = object :OnItemSelectedListener{
        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            block(p2,p3)
        }
        override fun onNothingSelected(p0: AdapterView<*>?) {
        }
    }
}

fun showAlertDialog(context: Context,layout: View): AlertDialog {
    val win = AlertDialog.Builder(context).setView(layout).create()

    win.window?.setBackgroundDrawableResource(R.color.transparent)
    win.window?.decorView?.setPadding(10.dp.toInt(),0,10.dp.toInt(),0)
    win.window?.setLayout(PaiMonsNoteBook.context.resources.displayMetrics.widthPixels-20.dp.toInt(),ViewGroup.LayoutParams.WRAP_CONTENT)
    win.show()
    return win
}

fun showAlertDialog(context: Context,id:Int): AlertDialog {
    val layout = LayoutInflater.from(context).inflate(id,null)
    val win = AlertDialog.Builder(context).setView(layout).create()

    win.window?.setBackgroundDrawableResource(R.color.transparent)
    win.window?.decorView?.setPadding(10.dp.toInt(),0,10.dp.toInt(),0)
    win.window?.setLayout(PaiMonsNoteBook.context.resources.displayMetrics.widthPixels-20.dp.toInt(),ViewGroup.LayoutParams.WRAP_CONTENT)
    win.show()
    return win
}

fun showConfirmAlertDialog(context: Context,title:String="提示",content:String="你确定吗",block: (isConfirm:Boolean) -> Unit){
    val layout = PopConfirmBinding.bind(LayoutInflater.from(context).inflate(R.layout.pop_confirm,null))
    val win = showAlertDialog(context,layout.root)

    layout.title.text = title
    layout.content.text = content

    layout.confirm.setOnClickListener {
        block(true)
        win.dismiss()
    }

    layout.cancel.setOnClickListener {
        block(false)
        win.dismiss()
    }
}

fun showSuccessInformationAlertDialog(context: Context,title:String){
    val layout = PopSuccessBinding.bind(LayoutInflater.from(context).inflate(R.layout.pop_success,null))
    val win = AlertDialog.Builder(context).setView(layout.root).create()

    layout.close.setOnClickListener {
        win.dismiss()
    }

    layout.title.text = title

    win.window?.setBackgroundDrawableResource(R.color.transparent)
    win.window?.decorView?.setPadding(10.dp.toInt(),0,10.dp.toInt(),0)
    win.window?.setLayout(PaiMonsNoteBook.context.resources.displayMetrics.widthPixels-20.dp.toInt(),ViewGroup.LayoutParams.WRAP_CONTENT)
    win.show()
}




