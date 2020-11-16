package com.example.cardtask

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
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
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.cardtask.api.CardResponse
import com.example.cardtask.fragment.CardFragment.Companion.albumRequestCode
import com.example.cardtask.fragment.CardFragment.Companion.cameraRequestCode
import com.example.cardtask.fragment.EdTaskFragment
import kotlinx.android.synthetic.main.dialog_photo.view.*
import java.io.File

fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun Fragment.hideKeyboard(view: View, nextFocusView: View = view.rootView) {
    val imm = view.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
    view.clearFocus()
    nextFocusView.requestFocus()
}

fun Fragment.photoDialog(activity: FragmentActivity?): File? {
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
    val fileProvider = FileProvider.getUriForFile(requireContext(), "com.example.cardtask.fileprovider", photoFile)
    Log.d("upload", fileProvider.toString())

    photoDialog.btn_camera.setOnClickListener {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, cameraRequestCode)
        } else {
            Toast.makeText(requireContext(), "unable to open camera", Toast.LENGTH_LONG).show()
        }
        dialog.dismiss()
    }

    photoDialog.btn_gallery.setOnClickListener {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, albumRequestCode)
        dialog.dismiss()
    }

    return photoFile
}

private fun Fragment.getPhotoFile(fileName: String): File {
    if(!File(requireContext().filesDir,"images").exists()){
        File(requireContext().filesDir,"images").mkdirs()
    }
    return File(requireContext().filesDir, "images"+File.separator+(System.currentTimeMillis())+fileName)
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
        putParcelable("task",showTask)
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

fun String.valid(): Boolean {    //當使用字串時,可以"string".valid的方式呼叫
    return this.length > 5
}