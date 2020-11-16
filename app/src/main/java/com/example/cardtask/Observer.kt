package com.example.cardtask

interface IPublisher {
    fun getChanged()
    fun add(subscriber: IObserver)
    fun remove(subscriber: IObserver)
}

interface IObserver {
    fun update()
}