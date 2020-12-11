package com.example.cardtask

import android.widget.ImageView
import android.widget.TextView

interface IPublisher {
    fun sendUserInfoChanged()
    fun addSubscriber(subscriber: IObserver)
    fun removeSubscriber(subscriber: IObserver)
}

interface IObserver {
    fun update()
}