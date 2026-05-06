package com.example.data.storage.impl

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.example.data.storage.api.StorageRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import io.ktor.http.ContentType
import java.io.File
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal class StorageRepositoryImpl(
    private val client: SupabaseClient,
    private val context: Context,
) : StorageRepository {

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun postAvatarImage(uri: Uri): String {
        val bucket = client.storage.from("avatar")
        val extension = context.resolveExtension(uri, fallback = "jpg")
        val response = bucket.upload("${Uuid.random()}.$extension", uri) {
            upsert = false
            context.resolveMimeType(uri, fallbackExtension = "jpg")?.let { mimeType ->
                runCatching { contentType = ContentType.parse(mimeType) }
            }
        }
        return bucket.publicUrl(response.path)
    }

    override suspend fun downloadAvatarImage(url: String): ByteArray {
        val (bucketName, path) = url.toPublicStoragePath()
        return client.storage.from(bucketName).downloadPublic(path)
    }

    override suspend fun postMessageImage(uri: Uri): Result<String> =
        runCatching { uploadPublicMedia(bucketName = "photos", uri = uri, fallbackExtension = "jpg") }

    override suspend fun postMessageVoice(uri: Uri): Result<String> =
        runCatching { uploadPublicMedia(bucketName = "photos", uri = uri, fallbackExtension = "m4a") }

    @OptIn(ExperimentalUuidApi::class)
    private suspend fun uploadPublicMedia(
        bucketName: String,
        uri: Uri,
        fallbackExtension: String
    ): String {
        val bucket = client.storage.from(bucketName)
        val extension = context.resolveExtension(uri, fallbackExtension)
        val path = "${Uuid.random()}.$extension"
        val response = if (uri.scheme == "file") {
            bucket.upload(path, File(uri.path.orEmpty())) {
                upsert = false
                context.resolveMimeType(uri, fallbackExtension)?.let { mimeType ->
                    runCatching { contentType = ContentType.parse(mimeType) }
                }
            }
        } else {
            bucket.upload(path, uri) {
                upsert = false
                context.resolveMimeType(uri, fallbackExtension)?.let { mimeType ->
                    runCatching { contentType = ContentType.parse(mimeType) }
                }
            }
        }
        return bucket.publicUrl(response.path)
    }

    private fun String.toPublicStoragePath(): Pair<String, String> {
        val marker = "/storage/v1/object/public/"
        val publicPath = substringAfter(marker, missingDelimiterValue = "")
        val parts = publicPath
            .split("/")
            .filter { it.isNotBlank() }

        if (parts.size >= 2) {
            return parts.first() to parts.drop(1).joinToString("/")
        }

        return "avatar" to split("/").last()
    }

    private fun Context.resolveMimeType(uri: Uri, fallbackExtension: String? = null): String? {
        if (uri.scheme == "file") {
            val extension = uri.path
                ?.substringAfterLast('.', missingDelimiterValue = "")
                ?.takeIf { it.isNotBlank() }
            return extension
                ?.let { MimeTypeMap.getSingleton().getMimeTypeFromExtension(it) }
                ?: fallbackExtension?.let {
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension(it)
                }
        }

        return contentResolver.getType(uri)
    }

    private fun Context.resolveExtension(uri: Uri, fallback: String): String {
        if (uri.scheme == "file") {
            return uri.path
                ?.substringAfterLast('.', missingDelimiterValue = "")
                ?.takeIf { it.isNotBlank() }
                ?: fallback
        }

        val mimeType = resolveMimeType(uri) ?: return fallback
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(mimeType)
            ?.takeIf { it.isNotBlank() }
            ?: fallback
    }
}
