package com.example.cardtask

interface IPublisher {
    fun sendUserInfoChanged()
    fun addSubscriber(subscriber: IObserver)
    fun removeSubscriber(subscriber: IObserver)
}

interface IObserver {
    fun update()
}