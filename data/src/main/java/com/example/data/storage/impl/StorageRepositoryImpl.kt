package com.example.data.storage.impl

import android.content.Context
import android.net.Uri
import com.example.core.util.SupabaseConst
import com.example.data.storage.api.StorageRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal class StorageRepositoryImpl(
    private val client: SupabaseClient,
    private val context: Context,
) : StorageRepository {

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun postAvatarImage(uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)!!
        val bytes = inputStream.readBytes()
        val id = Uuid.random().toString()
        val bucket = client.storage.from("avatar")
        bucket.upload("$id.jpg", bytes) {
            upsert = false
        }.let { response ->
            val storagePath = SupabaseConst.URL + "/storage/v1/object/public/avatar//"
            return storagePath + response.path
        }
    }

    override suspend fun downloadAvatarImage(url: String): ByteArray {
        val filename = url.split("/").last()
        val bucket = client.storage.from("avatar")
        val bytes = bucket.downloadPublic(filename)
        return bytes
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun postMessageImage(uri: Uri): Result<String> {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)!!
            val bytes = inputStream.readBytes()
            val id = Uuid.random().toString()
            val bucket = client.storage.from("photos")
            bucket.upload("$id.jpg", bytes) {
                upsert = false
            }.let { response ->
                val storagePath = SupabaseConst.URL + "/storage/v1/object/public/photos//"
                return Result.success(storagePath + response.path)
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}