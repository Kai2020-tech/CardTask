package com.example.cardtask.fragmentimport android.app.Activityimport android.app.Activity.RESULT_OKimport android.app.AlertDialogimport android.content.Intentimport android.net.Uriimport android.os.Buildimport android.os.Bundleimport android.util.Logimport android.view.LayoutInflaterimport android.view.Viewimport android.view.ViewGroupimport androidx.annotation.RequiresApiimport androidx.fragment.app.Fragmentimport com.bumptech.glide.Glideimport com.example.cardtask.*import com.example.cardtask.api.*import com.example.cardtask.fragment.MainFragment.Companion.GALLERY_REQUEST_CODEimport com.example.cardtask.fragment.MainFragment.Companion.CAMERA_REQUEST_CODEimport com.example.cardtask.fragment.MainFragment.Companion.cardListimport kotlinx.android.synthetic.main.fragment_ed_task.*import kotlinx.android.synthetic.main.fragment_ed_task.view.*import okhttp3.MultipartBodyimport retrofit2.Callimport retrofit2.Responseimport java.io.File//var _task: CardResponse.UserData.ShowCard.ShowTask? = nullclass EdTaskFragment() : Fragment() {    private lateinit var rootView: View    private var photoFile: File? = null    private var galleryUri: Uri? = null    var cardId = 0    var cardPosition = 0    lateinit var task: CardResponse.UserData.ShowCard.ShowTask    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        arguments?.let {            cardId = it.getInt("cardId")            task = it.getParcelable("task")!!        }    }    override fun onCreateView(        inflater: LayoutInflater, container: ViewGroup?,        savedInstanceState: Bundle?    ): View? {        // Inflate the layout for this fragment        rootView = inflater.inflate(R.layout.fragment_ed_task, container, false)        return rootView    }    @RequiresApi(Build.VERSION_CODES.N)    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {        super.onViewCreated(view, savedInstanceState)        var colorSelected = ""//radio button Task選擇顏色        rootView.radio_group2.setOnCheckedChangeListener { group, checkedId ->            when (checkedId) {                R.id.radio_btn_red2 -> colorSelected = "red"                R.id.radio_btn_orange2 -> colorSelected = "orange"                R.id.radio_btn_yellow2 -> colorSelected = "yellow"                R.id.radio_btn_green2 -> colorSelected = "green"                R.id.radio_btn_blue2 -> colorSelected = "blue"                R.id.radio_btn_darkBlue2 -> colorSelected = "darkBlue"                R.id.radio_btn_purple2 -> colorSelected = "purple"            }        }        when (task.tag) {   //載入Task顏色            "red" -> rootView.radio_btn_red2.isChecked = true            "orange" -> rootView.radio_btn_orange2.isChecked = true            "yellow" -> rootView.radio_btn_yellow2.isChecked = true            "green" -> rootView.radio_btn_green2.isChecked = true            "blue" -> rootView.radio_btn_blue2.isChecked = true            "darkBlue" -> rootView.radio_btn_darkBlue2.isChecked = true            "purple" -> rootView.radio_btn_purple2.isChecked = true        }//拍照或相簿        rootView.img_task2.setOnClickListener {            photoFile = getPhoto(activity)        }        rootView.ed_taskTitle2.setText(task.title)  //載入Task Title        rootView.ed_taskDescription2.setText(task.description) //載入Task內容//  載入圖片        if (!task.image.isNullOrEmpty()) {            Glide.with(this).asBitmap()                .load("https://storage.googleapis.com/gcs.gill.gq/" + task.image).into(rootView.img_task2)            img_task2.background = null        }//  刪除圖片        rootView.img_task2.setOnLongClickListener {            var imgIsDel = false            val delBuild = AlertDialog.Builder(activity)            delBuild                .setTitle("確認要刪除圖片")                .setPositiveButton("刪除") { _, _ ->                    imgIsDel = true                    updateTask(null,imgIsDel,colorSelected)                    showToast("已刪除")                    img_task2.visibility = View.INVISIBLE                }                .setNegativeButton("取消") { _, _ -> }                .show()            imgIsDel        }//  確認更新,傳api        rootView.btn_taskUpdate2.setOnClickListener {            val imgIsDel: Boolean? = null//            Log.d("uri", fileUri.toString())            val file = File(photoFile?.path ?: "")            val imageBody: MultipartBody.Part? =                if (galleryUri != null) createMultipartBody(galleryUri) else createMultipartBody(file)            updateTask(imageBody, imgIsDel, colorSelected)            hideKeyboard(ed_taskTitle2)        }        rootView.tv_taskOfCardId2.text = this.cardId.toString()    }    private fun updateTask(imageBody: MultipartBody.Part?, imgIsDel: Boolean?, colorSelected: String) {        val taskTitle: String = ed_taskTitle2.text.toString()        val taskDescription = ed_taskDescription2.text.toString()        Api.retrofitService.updateTask(            userToken = token,            card_id = cardId,            taskId = task.id,            image = imageBody,            del_img = imgIsDel,            taskName = MultipartBody.Part.createFormData("title", taskTitle),            color = MultipartBody.Part.createFormData("tag", colorSelected),            description = MultipartBody.Part.createFormData("description", taskDescription),            method = MultipartBody.Part.createFormData("_method", "PUT")        ).enqueue(object : MyCallback<UpdateTaskResponse>() {            override fun onSuccess(call: Call<UpdateTaskResponse>, response: Response<UpdateTaskResponse>) {                showToast("Task [$taskTitle] 已更新")                Log.d("Success!", "Update task OK")                getCards()            }        })    }    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {        super.onActivityResult(requestCode, resultCode, data)//相機, intent回來        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {            Glide.with(this).load(photoFile).into(img_task2)            Log.d("uri camera", photoFile?.path.toString())            img_task2.background = null//            MediaScannerConnection.scanFile(requireContext(), arrayOf(photoFile!!.path), null) { path, uri ->//                Log.i("ExternalStorage", "Scanned " + path + ":")//                Log.i("ExternalStorage", "-> uri=" + uri)//            }        }//相簿, intent回來        else if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {            galleryUri = data?.data!!            Log.d("gallery", "uri: $galleryUri")            Glide.with(this).load(galleryUri).into(img_task2)            img_task2.background = null        }    }    private fun getCards() {    //取得所有資料        Api.retrofitService.getCard(token)            .enqueue(object : MyCallback<CardResponse>() {                override fun onSuccess(call: Call<CardResponse>, response: Response<CardResponse>) {                    val res = response.body()                    updateCards(res)                    Log.d("Success!", "getCard OK")                    targetFragment?.onActivityResult(111, Activity.RESULT_OK, null)                    requireFragmentManager().popBackStack()                }            })    }    private fun updateCards(res: CardResponse?) {        cardList.clear()        res?.userData?.showCards?.forEach { card ->            cardList.add(card)        }    }}