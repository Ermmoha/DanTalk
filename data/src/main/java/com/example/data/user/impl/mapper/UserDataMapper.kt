package com.example.data.user.impl.mapper

import com.example.data.user.api.model.UserData
import com.example.data.user.impl.entity.UserDataEntity

internal fun UserDataEntity.toDomain(id: String) =
    UserData(
        id = id,
        avatar = avatar,
        email = email,
        username = username,
        firstname = firstname,
        lastname = lastname,
        patronymic = patronymic
    )

internal fun UserData.toEntity() =
    UserDataEntity(
        avatar = avatar,
        email = email,
        username = username,
        firstname = firstname,
        lastname = lastname,
        patronymic = patronymic
    )