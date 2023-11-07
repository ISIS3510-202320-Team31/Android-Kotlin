package com.example.hive.util

class FormProgressCache<K, V>(private val maxSize: Int) {
    private val cache: LinkedHashMap<K, V> = object : LinkedHashMap<K, V>(maxSize, 0.75f, true) {
        override fun removeEldestEntry(eldest: Map.Entry<K, V>): Boolean {
            return size > maxSize
        }
    }

    fun get(key: K): V? {
        return synchronized(this) {
            return cache[key]
        }
    }

    fun put(key: K, value: V) {
        synchronized(this) {
            cache[key] = value
        }
    }

    fun remove(key: K) {
        cache.remove(key)
    }
}