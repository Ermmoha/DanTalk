package com.example.data.chat.impl.mapper

import com.example.data.chat.api.model.Message
import com.example.data.chat.impl.entity.MessageEntity

internal fun Message.toEntity() =
    MessageEntity(
        sender = sender,
        message = message,
        read = read,
        pending = isPending,
        edited = isEdited,
        photo = isPhoto,
        voice = isVoice,
        mediaDurationMillis = mediaDurationMillis,
        mediaSizeBytes = mediaSizeBytes,
        replyToMessageId = replyToMessageId,
        replyToSender = replyToSender,
        replyToText = replyToText,
        sentAt = sentAt
    )

internal fun MessageEntity.toDomain(id: String) =
    Message(
        id = id,
        sender = sender,
        message = message,
        read = read,
        isPending = pending,
        isEdited = edited,
        isPhoto = photo,
        isVoice = voice,
        mediaDurationMillis = mediaDurationMillis,
        mediaSizeBytes = mediaSizeBytes,
        replyToMessageId = replyToMessageId,
        replyToSender = replyToSender,
        replyToText = replyToText,
        sentAt = sentAt
    )
