package com.example.cardtask.api
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


data class CardResponse(
    @SerializedName("status")
    val status: Boolean = false, // true
    @SerializedName("user_data")
    val userData: UserData = UserData()
) {
    data class UserData(
        @SerializedName("created_at")
        val createdAt: String = "", // 2020-09-04T15:38:22.000000Z
        @SerializedName("email")
        val email: String = "", // ishida624@gmail.com
        @SerializedName("id")
        val id: Int = 0, // 1
        @SerializedName("image")
        val image: Any = Any(), // null
        @SerializedName("show_cards")
        val showCards: List<ShowCard> = listOf(),
        @SerializedName("updated_at")
        val updatedAt: String = "", // 2020-09-04T16:15:00.000000Z
        @SerializedName("username")
        val username: String = "" // admin
    ) {
        @Parcelize
        data class ShowCard(
            @SerializedName("card_name")
            var cardName: String = "", // hahaha
            @SerializedName("create_user")
            val createUser: String = "", // admin
            @SerializedName("created_at")
            val createdAt: String = "", // 2020-09-04T16:17:53.000000Z
            @SerializedName("id")
            val id: Int = 0, // 1
            @SerializedName("pivot")
            val pivot: Pivot = Pivot(),
            @SerializedName("private")
            val `private`: Boolean = false, // false
            @SerializedName("show_tasks")
            val showTasks: MutableList<ShowTask> = mutableListOf(),     //list改為mutableList
            @SerializedName("updated_at")
            val updatedAt: String = "" // 2020-09-04T16:17:53.000000Z
        ):Parcelable {
            @Parcelize
            data class Pivot(
                @SerializedName("card_id")
                val cardId: Int = 0, // 1
                @SerializedName("users_id")
                val usersId: Int = 0 // 1
            ):Parcelable

            @Parcelize
            data class ShowTask(
                @SerializedName("card_id")
                val cardId: Int = 0, // 1
                @SerializedName("create_user")
                val createUser: String = "", // admin
                @SerializedName("created_at")
                val createdAt: String = "", // 2020-09-04T16:22:13.000000Z
                @SerializedName("description")
                val description: String = "", // null
                @SerializedName("id")
                val id: Int = 0, // 1
                @SerializedName("image")
                val image: String, // null
                @SerializedName("status")
                val status: Boolean = false, // false
                @SerializedName("tag")
                val tag: String = "", // 顏色
                @SerializedName("title")
                val title: String = "", // task1
                @SerializedName("update_user")
                val updateUser: String = "", // admin
                @SerializedName("updated_at")
                val updatedAt: String = "" // 2020-09-04T16:22:13.000000Z
            ) : Parcelable
        }
    }
}

data class NewCardResponse(
    @SerializedName("card_data")
    val cardData: CardData = CardData(),
    @SerializedName("status")
    val status: Boolean = false // true
) {
    data class CardData(
        @SerializedName("card_name")
        val cardName: String = "", // card2
        @SerializedName("create_user")
        val createUser: String = "", // admin
        @SerializedName("created_at")
        val createdAt: String = "", // 2020-09-04T16:34:32.000000Z
        @SerializedName("id")
        val id: Int = 0, // 3
        @SerializedName("private")
        val `private`: Boolean = false, // true
        @SerializedName("updated_at")
        val updatedAt: String = "" // 2020-09-04T16:34:32.000000Z
    )
}

data class DeleteCardResponse(
    @SerializedName("error")
    val error: String? = "", // card serch not found
    @SerializedName("status")
    val status: Boolean = false // false
)

data class UpdateCardResponse(
    @SerializedName("card_data")
    val cardData: CardData = CardData(),
    @SerializedName("status")
    val status: Boolean = false // true
) {
    data class CardData(
        @SerializedName("card_name")
        val cardName: String = "", // card name
        @SerializedName("create_user")
        val createUser: String = "", // admin
        @SerializedName("created_at")
        val createdAt: String = "", // 2020-09-04T15:55:43.000000Z
        @SerializedName("id")
        val id: Int = 0, // 10
        @SerializedName("pivot")
        val pivot: Pivot = Pivot(),
        @SerializedName("private")
        val `private`: Boolean = false, // false
        @SerializedName("show_tasks")
        val showTasks: List<Any> = listOf(),
        @SerializedName("updated_at")
        val updatedAt: String = "" // 2020-09-07T05:16:49.000000Z
    ) {
        data class Pivot(
            @SerializedName("card_id")
            val cardId: Int = 0, // 10
            @SerializedName("users_id")
            val usersId: Int = 0 // 2
        )
    }
}

data class NewTaskResponse(
    @SerializedName("status")
    val status: String = "", // true
    @SerializedName("task_data")
    val taskData: TaskData = TaskData()
) {
    data class TaskData(
        @SerializedName("card_id")
        val cardId: String = "", // 12
        @SerializedName("create_user")
        val createUser: String = "", // admin
        @SerializedName("created_at")
        val createdAt: String = "", // 2020-09-07T08:36:31.000000Z
        @SerializedName("description")
        val description: Any = Any(), // null
        @SerializedName("id")
        val id: Int = 0, // 9
        @SerializedName("image")
        val image: Any = Any(), // null
        @SerializedName("status")
        val status: Boolean = false, // false
        @SerializedName("tag")
        val tag: String = "", // red
        @SerializedName("title")
        val title: String = "", // task5
        @SerializedName("update_user")
        val updateUser: String = "", // admin
        @SerializedName("updated_at")
        val updatedAt: String = "" // 2020-09-07T08:36:31.000000Z
    )
}

data class DeleteTaskResponse(
    @SerializedName("error")
    val error: String = "", // task search not found
    @SerializedName("status")
    val status: Boolean = false // true
)

data class UpdateTaskResponse(
    @SerializedName("status")
    val status: Boolean = false, // true
    @SerializedName("task_data")
    val taskData: TaskData = TaskData()
) {
    data class TaskData(
        @SerializedName("card_id")
        val cardId: String = "", // 10
        @SerializedName("create_user")
        val createUser: String = "", // admin
        @SerializedName("created_at")
        val createdAt: String = "", // 2020-09-09T03:24:48.000000Z
        @SerializedName("description")
        val description: String = "", // create test
        @SerializedName("id")
        val id: Int = 0, // 10
        @SerializedName("image")
        val image: Any = Any(), // null
        @SerializedName("status")
        val status: Boolean = false, // false
        @SerializedName("tag")
        val tag: String = "", // green
        @SerializedName("title")
        val title: String = "", // task7
        @SerializedName("update_user")
        val updateUser: String = "", // admin
        @SerializedName("updated_at")
        val updatedAt: String = "" // 2020-09-09T03:39:47.000000Z
    )
}