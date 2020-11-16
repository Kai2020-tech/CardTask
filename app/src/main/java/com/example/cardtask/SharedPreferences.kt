package com.example.cardtask

import android.content.Context

class SharedPreferences(context: Context) {
    //    透過 Context 的 getSharedPreferences 方法取得實例
    private val pref = context.getSharedPreferences("Total", Context.MODE_PRIVATE)

//    實例化 SharedPreferences.Editor,Editor 可以用來編輯 SharedPreferences 檔案的內容(新增,刪除)
//    且在執行 Editor 的 commit 或 apply 方法後,Editor 編輯的內容才會被應用至 SharedPreferences 檔案
    private val editor = pref.edit()

//    寫入資料  editor.putString("資料的tag標籤", 要存的資料)

    fun saveData(data: String) {
        editor.putString("key", data).apply()
    }

    //    取出資料  editor.getString("資料的tag標籤", 若找無資料時要回傳的值)
    fun getData(): String? {
        return pref.getString("key", null)
    }

    //    清空 SharedPreferences 檔案中的所有資料
    fun delete() {
        editor.clear().commit()
    }
//    變更應用可用apply()或commit(),apply()先寫入內存異步速度較快,但不會回傳失敗或成功,適合一般資料
//    重要資料可用commit(),會同步儲存至SharedPreferences檔案,並回傳結果
}