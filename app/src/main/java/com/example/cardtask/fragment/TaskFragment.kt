package com.example.cardtask.fragmentimport android.app.Activityimport android.content.Intentimport android.os.Bundleimport android.util.Logimport androidx.fragment.app.Fragmentimport android.view.LayoutInflaterimport android.view.Viewimport android.view.ViewGroupimport com.bumptech.glide.Glideimport com.example.cardtask.*import com.example.cardtask.api.*import kotlinx.android.synthetic.main.fragment_ed_task.*import kotlinx.android.synthetic.main.fragment_ed_task.view.*import kotlinx.android.synthetic.main.fragment_task.*import kotlinx.android.synthetic.main.fragment_task.view.*import okhttp3.MediaTypeimport okhttp3.MultipartBodyimport okhttp3.RequestBodyimport retrofit2.Callimport retrofit2.Responseimport java.io.Fileclass TaskFragment : Fragment(){    private lateinit var rootView: View    private var fileUri: File? = null    var cardId = 0    var cardPosition = 0    private val newTaskRequestCode = 555    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        arguments?.let {            cardPosition = it.getInt("pos")            cardId = it.getInt("id")        }    }    override fun onCreateView(        inflater: LayoutInflater, container: ViewGroup?,        savedInstanceState: Bundle?    ): View? {        // Inflate the layout for this fragment        rootView = inflater.inflate(R.layout.fragment_task, container, false)        return rootView    }    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {        super.onViewCreated(view, savedInstanceState)        var taskTitle: String        var colorSelected = ""        var taskDescription = ""// radio button Task選擇顏色        rootView.radio_group.setOnCheckedChangeListener { group, checkedId ->            when (checkedId) {                R.id.radio_btn_red -> colorSelected = "red"                R.id.radio_btn_green -> colorSelected = "green"                R.id.radio_btn_yellow -> colorSelected = "yellow"                R.id.radio_btn_pink -> colorSelected = "pink"                R.id.radio_btn_blue -> colorSelected = "blue"            }        }//      拍照        rootView.img_task.setOnClickListener {            fileUri = photoDialog(activity)        }//      確認新增        rootView.btn_taskConfirm.setOnClickListener {            taskTitle = ed_taskTitle.text.toString()            taskDescription = ed_taskDescription.text.toString()            val file = File(fileUri?.path ?: "")            var imageBody: MultipartBody.Part? = if (file.exists()) {   //如果file存在才,                val requestFile = RequestBody.create(MediaType.parse("image/jpg"), file)                MultipartBody.Part.createFormData("image", file.name, requestFile)            } else null            Api.retrofitService.createTask(                userToken = token,                card_id = cardId,                image = imageBody,                taskName = MultipartBody.Part.createFormData("title", taskTitle),                color = MultipartBody.Part.createFormData("tag", colorSelected),                description = MultipartBody.Part.createFormData("description", taskDescription)            ).enqueue(object : MyCallback<NewTaskResponse>() {                override fun onSuccess(call: Call<NewTaskResponse>, response: Response<NewTaskResponse>) {                    showToast("Task [$taskTitle] 已新增")                    Log.d("Success!", "New task OK")                    backEdCard()                    hideKeyboard(tv_taskOfCardId)                }            })        }        rootView.tv_taskOfCardId.text = this.cardId.toString()    }    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {        super.onActivityResult(requestCode, resultCode, data)        if (requestCode == CardFragment.cameraRequestCode && resultCode == Activity.RESULT_OK) {            Glide.with(this).load(fileUri).into(img_task)            Log.d("uriResultPath", fileUri?.path)            img_task.background = null        } else if (requestCode == CardFragment.albumRequestCode) {            if (data?.data != null) {                Glide.with(this).load(data?.data).into(img_task)                img_task.background = null            }        }    }     private fun backEdCard(){         targetFragment?.onActivityResult(newTaskRequestCode, Activity.RESULT_OK, null)         requireFragmentManager().popBackStack()    }}