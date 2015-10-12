package common.list

fun <T> List<T>.random(): T = this[Math.floor(Math.random() * size().toDouble()).toInt()]