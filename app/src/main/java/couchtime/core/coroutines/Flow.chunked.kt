package couchtime.core.coroutines

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun <T> Flow<T>.chunked(chunkSize: Int): Flow<List<T>> =
    flow {
        val chunk = ArrayList<T>(chunkSize)
        this@chunked
            .collect { item ->
                chunk.add(item)
                if (chunk.size >= chunkSize) {
                    emit(chunk.toList())
                    chunk.clear()
                }
            }
        if (chunk.isNotEmpty()) {
            emit(chunk.toList())
        }
    }
