package com.example.ktor_test_client.data

import com.example.ktor_test_client.api.dtos.Track
import com.example.ktor_test_client.data.providers.DataProvider
import kotlinx.coroutines.runBlocking

class PlaylistContainer(
    private val tracksId: List<String>,
    private val dataProvider: DataProvider,
    private var currentIndex: Int = 0
) {
    val tracks = MutableList<Track?>(tracksId.size) { null }
    private lateinit var currentLinkedList: DoubleLinkedList<Lazy<Track>>

    fun addTracks(tracksId: List<String>) {
        tracks.addAll(List(tracksId.size) { null })
        currentLinkedList.append(tracksId.mapIndexed { index, item ->
            lazy {
                runBlocking {
                    dataProvider.getTrack(item)!!.also {
                        tracks[index] = it
                    }
                }
            }
        })
    }

    fun getPlaylist() = DoubleLinkedList(tracksId.mapIndexed { index, item ->
        lazy {
            runBlocking {
                dataProvider.getTrack(item)!!.also {
                    tracks[index] = it
                }
            }
        }
    }).also {
        currentLinkedList = it
    }
}

class DoubleLinkedList<T>(data: List<T>) {
    var first: DoubleLinkedListNode<T>? = null
    var last: DoubleLinkedListNode<T>? = null

    var size: Int = 0
        private set

    init {
        if (data.isNotEmpty()) {
            append(data)
        }
    }

    var currentNode: DoubleLinkedListNode<T>? = first
        private set

    fun forward() {
        forwardOn(1)
    }

    fun backward() {
        backwardOn(1)
    }

    fun forwardOn(count: Int) {
        repeat(count) {
            currentNode?.next?.let {
                currentNode = it
            }
        }
    }

    fun backwardOn(count: Int) {
        repeat(count) {
            currentNode?.prev?.let {
                currentNode = it
            }
        }
    }

    fun getNode(index: Int): DoubleLinkedListNode<T>? {
        if (index > size)
            throw IndexOutOfBoundsException()

        var currentNode: DoubleLinkedListNode<T>? = first
        repeat(index) {
            currentNode = currentNode?.next
        }

        return currentNode
    }

    fun append(collection: Collection<T>) {
        if (collection.isNotEmpty()) {
            collection.forEach {
                append(it)
            }
        }
    }

    fun append(data: T) {
        if (first == null) {
            first = DoubleLinkedListNode(data)
            last = first
        } else {
            val newNode = DoubleLinkedListNode(data)

            last?.next = newNode
            newNode.prev = last

            last = newNode
        }

        size++
    }

    fun remove(data: T): Boolean {
        val index = 0

        var targetNode: DoubleLinkedListNode<T>? = first
        while (index < size) {
            if (targetNode?.data == data)
                break

            targetNode = targetNode?.next
        }

        targetNode?.let {
            it.prev?.next = it.next
            it.next?.prev = it.prev

            return true
        }

        return false
    }
}

class DoubleLinkedListNode<T>(
    var data: T?,
    var next: DoubleLinkedListNode<T>? = null,
    var prev: DoubleLinkedListNode<T>? = null
)