package com.example.cardtask.fragmentimport android.app.Activityimport android.content.Intentimport android.net.Uriimport android.os.Buildimport android.os.Bundleimport android.util.Logimport androidx.fragment.app.Fragmentimport android.view.LayoutInflaterimport android.view.Viewimport android.view.ViewGroupimport androidx.annotation.RequiresApiimport com.bumptech.glide.Glideimport com.example.cardtask.*import com.example.cardtask.api.*import com.example.cardtask.fragment.MainFragment.Companion.CAMERA_REQUEST_CODEimport com.example.cardtask.fragment.MainFragment.Companion.GALLERY_REQUEST_CODEimport kotlinx.android.synthetic.main.fragment_ed_task.*import kotlinx.android.synthetic.main.fragment_new_task.*import kotlinx.android.synthetic.main.fragment_new_task.view.*import okhttp3.MultipartBodyimport retrofit2.Callimport retrofit2.Responseimport java.io.Fileclass NewTaskFragment : Fragment() {    private lateinit var rootView: View    private var photoFile: File? = null    private  var galleryUri: Uri? = null    var cardId = 0    var cardPosition = 0    private val newTaskRequestCode = 555    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        arguments?.let {            cardId = it.getInt("id")            cardPosition = it.getInt("pos")        }    }    override fun onCreateView(        inflater: LayoutInflater, container: ViewGroup?,        savedInstanceState: Bundle?    ): View? {        // Inflate the layout for this fragment        rootView = inflater.inflate(R.layout.fragment_new_task, container, false)        return rootView    }    @RequiresApi(Build.VERSION_CODES.N)    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {        super.onViewCreated(view, savedInstanceState)        //  背景圖用        gradientChart_newTaskFragment.chartValues = arrayOf(            10f, 30f, 25f, 32f, 13f, 5f, 18f, 36f, 20f, 30f, 28f, 27f, 29f        )        var taskTitle: String        var colorSelected = ""        var taskDescription = ""// radio button Task選擇顏色        rootView.radio_group.setOnCheckedChangeListener { group, checkedId ->            when (checkedId) {                R.id.radio_btn_red -> colorSelected = "red"                R.id.radio_btn_orange -> colorSelected = "orange"                R.id.radio_btn_yellow -> colorSelected = "yellow"                R.id.radio_btn_green -> colorSelected = "green"                R.id.radio_btn_blue -> colorSelected = "blue"                R.id.radio_btn_darkBlue -> colorSelected = "darkBlue"                R.id.radio_btn_purple -> colorSelected = "purple"            }        }//      拍照或相簿        rootView.img_task.setOnClickListener {            photoFile = getPhoto(activity)        }//      確認新增        rootView.btn_taskConfirm.setOnClickListener {            taskTitle = ed_taskTitle.text.toString()            taskDescription = ed_taskDescription.text.toString()            val file = File(photoFile?.path ?: "")            val imageBody: MultipartBody.Part? =                if (galleryUri != null) createMultipartBody(galleryUri) else createMultipartBody(file)            if(taskTitle.isNullOrEmpty()){                showToast("Task Title不能空白")            }else{                Api.retrofitService.createTask(                    userToken = token,                    card_id = cardId,                    image = imageBody,                    taskName = MultipartBody.Part.createFormData("title", taskTitle),                    color = MultipartBody.Part.createFormData("tag", colorSelected),                    description = MultipartBody.Part.createFormData("description", taskDescription)                ).enqueue(object : MyCallback<NewTaskResponse>() {                    override fun onSuccess(call: Call<NewTaskResponse>, response: Response<NewTaskResponse>) {                        showToast("Task [$taskTitle] 已新增")                        Log.d("Success!", "New task OK")                        backEdCard()                        hideKeyboard(tv_taskOfCardId)                    }                })            }        }        rootView.tv_taskOfCardId.text = this.cardId.toString()    }    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {        super.onActivityResult(requestCode, resultCode, data)        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {            Glide.with(this).load(photoFile).into(img_task)            Log.d("uriResultPath", photoFile?.path)            img_task.background = null        } else if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {            galleryUri = data?.data!!            Glide.with(this).load(galleryUri).into(img_task)            img_task.background = null        }    }    private fun backEdCard() {        targetFragment?.onActivityResult(newTaskRequestCode, Activity.RESULT_OK, null)        requireFragmentManager().popBackStack()    }}