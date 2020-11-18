package com.example.cardtask

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.cardtask.api.CardResponse
import com.example.cardtask.fragment.CardFragment.Companion.CAMERA_REQUEST_CODE
import com.example.cardtask.fragment.CardFragment.Companion.GALLERY_REQUEST_CODE
import com.example.cardtask.fragment.EdTaskFragment
import kotlinx.android.synthetic.main.dialog_photo.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*

fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun hideKeyboard(view: View, nextFocusView: View = view.rootView) {
    val imm = view.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
    view.clearFocus()
    nextFocusView.requestFocus()
}

fun Fragment.getPhoto(activity: FragmentActivity?): File? {
    val photoDialog: View = LayoutInflater.from(activity).inflate(R.layout.dialog_photo, null)
    val dialogBuilder = AlertDialog.Builder(activity)
    val dialog = dialogBuilder.setView(photoDialog)
        .setView(photoDialog)
        .setOnDismissListener {
            (photoDialog.parent as ViewGroup).removeView(photoDialog)
        }
        .show()
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent) //set dialog background transparent

    val fileName = "photo.jpg"
    val photoFile = getPhotoFile(fileName)

    photoDialog.btn_camera.setOnClickListener {

        val fileProvider = FileProvider.getUriForFile(requireContext(), "com.example.cardtask.fileprovider", photoFile!!)
        Log.d("upload", fileProvider.toString())
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
        } else {
            Toast.makeText(requireContext(), "unable to open camera", Toast.LENGTH_LONG).show()
        }
        dialog.dismiss()
    }

    photoDialog.btn_gallery.setOnClickListener {
        val intent = Intent(Intent.ACTION_PICK)

        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
        dialog.dismiss()

    }

    return photoFile
}

private fun Fragment.getPhotoFile(fileName: String): File {

//    val file: File = File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM).path + File.separator +fileName)
//    Log.d("getPhotoFile", "${Uri.fromFile(file)}")
//
//    return file

    if (!File(requireContext().filesDir, "images").exists()) {
        File(requireContext().filesDir, "images").mkdirs()
    }
    return File(requireContext().filesDir, "images" + File.separator + (System.currentTimeMillis()) + fileName)
//    "file:///data/data/com.example.cardtasj/file/images/photo.image"
//    "content://[authority]/internal_images/phto.image"
//    val storageDirectory = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//
//    return File.createTempFile("", ".jpg", storageDirectory)
}

fun Fragment.goToEdTask(showTask: CardResponse.UserData.ShowCard.ShowTask) {
    val edTaskFragment = EdTaskFragment()
    edTaskFragment.arguments = Bundle().apply {
        putInt("cardId", showTask.cardId)
        putParcelable("task", showTask)
    }
    /**將此fragment設爲target,接受回來的資料*/
    edTaskFragment.setTargetFragment(this, 111)
//    _task = showTask
    activity!!.supportFragmentManager?.beginTransaction()?.apply {
        setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
        add(R.id.frame_layout, edTaskFragment)
        addToBackStack("CardFragment")
        commit()
    }
}

fun Fragment.createMultipartBody(file: File, galleryUri: Uri?): MultipartBody.Part? {
    val imageStream: InputStream?
    var bmp: Bitmap
    var requestFile: RequestBody
    val baoS: ByteArrayOutputStream? = ByteArrayOutputStream()
    val fileUri = Uri.fromFile(file)
    return if (file.exists()) {
//                Log.d("edTaskFragment camera", "${file.absolutePath}")
//                Log.d("edTaskFragment camera", "${Uri.fromFile(file)}")
//        var compressedUri = compress(fileUri)
//        imageStream = requireContext().contentResolver.openInputStream(compressedUri!!)
        imageStream = requireContext().contentResolver.openInputStream(fileUri)
        bmp = BitmapFactory.decodeStream(imageStream)

        //將byteArrayOut進行品質壓縮 compress Quality 100->25
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baoS)
        requestFile = RequestBody.create(MediaType.parse("image/jpg"), baoS!!.toByteArray())
        Log.d("gallery", "requestFile Size ${requestFile.contentLength()}")
        MultipartBody.Part.createFormData("image", file.name, requestFile)
    } else {
        if (galleryUri != null) {
            imageStream = requireContext().contentResolver.openInputStream(galleryUri)
            bmp = BitmapFactory.decodeStream(imageStream)
            //將byteArrayOut進行品質壓縮 compress Quality 100->25
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baoS)
            requestFile = RequestBody.create(MediaType.parse("image/jpg"), baoS!!.toByteArray())
            Log.d("gallery", "requestFile Size ${requestFile.contentLength()}")
            MultipartBody.Part.createFormData("image", "sample.jpg", requestFile)
//en ping
//                requireContext().contentResolver.openInputStream(galleryUri).use {
//                    val reqFile = RequestBody.create(MediaType.parse("image/jpg"), it!!.readBytes())
//                    Log.d("gallery", "reqFile Size ${reqFile.contentLength()}")
//                    MultipartBody.Part.createFormData("image", "sample.jpg", reqFile)
//                }
        } else null
    }
}

//private fun Fragment.compressPhoto(path: Uri):Bitmap {
//    val imageStream = requireContext().contentResolver.openInputStream(path)
//    var bmp = BitmapFactory.decodeStream(imageStream)
//    val baoS: ByteArrayOutputStream = ByteArrayOutputStream()
//    //將byteArrayOut進行品質壓縮 compress Quality 100->25
//    bmp.compress(Bitmap.CompressFormat.JPEG, 25, baoS)
//    val baiS = ByteArrayInputStream(baoS.toByteArray())
//    compressBmp = BitmapFactory.decodeStream(baiS)
//    val degree = readPictureDegree(path.toString())
//    if (degree != 0) {//旋轉照片角度
//        compressBmp = rotateBitmap(bmp, degree);
//    }
//}

//fun Fragment.compress(path: Uri): Uri? {
//    val imageStream = requireContext().contentResolver.openInputStream(path)
//    var bm: Bitmap = BitmapFactory.decodeStream(imageStream) ?: return null //压缩照片
//    bm = compressImage(bm)!!
//    Log.d("bitmap size", "")
//    val degree: Int = readPictureDegree(path.toString()) //读取照片旋转角度
//
//    if (degree > 0) {
//        bm = rotateBitmap(bm!!, degree) //旋转照片
//    }
//    val file = (Environment.getExternalStorageDirectory().absolutePath
//            + File.separator) + "temp" + File.separator
//    val f = File(file)
//    if (!f.exists()) {
//        f.mkdirs()
//    }
//    val picName = System.currentTimeMillis().toString() + ".jpg"
//    val resultFilePath = Uri.fromFile(f)
//    try {
//        val out = FileOutputStream(resultFilePath.toString())
//        bm?.compress(Bitmap.CompressFormat.JPEG, 90, out)
//        out.flush()
//        out.close()
//    } catch (e: Exception) {
//        e.printStackTrace()
//    }
//
//    return resultFilePath
//}
//
//fun getImage(srcPath: String?): Bitmap? {
//    val newOpts = BitmapFactory.Options()
//    //开始读入图片，此时把options.inJustDecodeBounds 设回true了
//    newOpts.inJustDecodeBounds = true
//    var bitmap = BitmapFactory.decodeFile(srcPath, newOpts) //此时返回bm为空
//    newOpts.inJustDecodeBounds = false
//    val w = newOpts.outWidth
//    val h = newOpts.outHeight
//    val hh = 800f //这里设置高度为800f
//    val ww = 480f //这里设置宽度为480f
//    //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
//    var be = 1 //be=1表示不缩放
//    if (w > h && w > ww) { //如果宽度大的话根据宽度固定大小缩放
//        be = (newOpts.outWidth / ww).toInt()
//    } else if (w < h && h > hh) { //如果高度高的话根据宽度固定大小缩放
//        be = (newOpts.outHeight / hh).toInt()
//    }
//    if (be <= 0) be = 1
//    newOpts.inSampleSize = be //设置缩放比例
//    //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
//    bitmap = BitmapFactory.decodeFile(srcPath, newOpts)
//    return compressImage(bitmap) //压缩好比例大小后再进行质量压缩
//}
//
//fun compressImage(image: Bitmap): Bitmap? {
//    val baos = ByteArrayOutputStream()
//    image.compress(Bitmap.CompressFormat.JPEG, 100, baos) //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
//    var options = 99 //这个只能是0-100之间不包括0和100不然会报异常
//    while (baos.toByteArray().size / 1024 > 100 && options > 0) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
//        baos.reset() //重置baos即清空baos
//        image.compress(Bitmap.CompressFormat.JPEG, options, baos) //这里压缩options%，把压缩后的数据存放到baos中
//        options -= 10 //每次都减少10
//    }
//    val isBm = ByteArrayInputStream(baos.toByteArray()) //把压缩后的数据baos存放到ByteArrayInputStream中
//    return BitmapFactory.decodeStream(isBm, null, null)
//}
//
//
//private fun readPictureDegree(path: String?): Int {
//    var degree = 0
//    try {
//        val exifInterface = ExifInterface(path)
//        val orientation: Int = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
//        when (orientation) {
//            ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
//            ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
//            ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
//        }
//    } catch (e: IOException) {
//        e.printStackTrace()
//    }
//    return degree
//}
//
//private fun rotateBitmap(bitmap: Bitmap, rotate: Int): Bitmap {
//    val w = bitmap.width;
//    val h = bitmap.height;
//    val matrix = Matrix()
//    matrix.postRotate(rotate.toFloat());
//    return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
//}


//fun String.valid(): Boolean {    //當使用字串時,可以"string".valid的方式呼叫
//    return this.length > 5
//}
