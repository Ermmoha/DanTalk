package com.example.feature.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.example.core.design.AppTheme
import com.example.feature.main.chat.ChatContent
import com.example.feature.main.component.MainComponent
import com.example.feature.main.help.HelpContent
import com.example.feature.main.home.HomeContent
import com.example.feature.main.people.PeopleContent
import com.example.feature.main.profile.ProfileContent
import com.example.feature.main.search.SearchContent
import com.example.feature.main.settings.SettingsContent

@Composable
fun MainContent(
    component: MainComponent,
    appThemeState: State<AppTheme>
) {
    val stack = component.stack

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    LaunchedEffect(Unit) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if(ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                launcher.launch(permission)
        }
    }

    Children(
        stack = stack,
        animation = stackAnimation(fade() + scale())
    ) { child ->
        when (val instance = child.instance) {
            is MainComponent.Child.Home -> HomeContent(instance.component, appThemeState)
            is MainComponent.Child.Search -> SearchContent(instance.component)
            is MainComponent.Child.Profile -> ProfileContent(instance.component)
            is MainComponent.Child.People -> PeopleContent(instance.component)
            is MainComponent.Child.Chat -> ChatContent(instance.component)
            is MainComponent.Child.Settings -> SettingsContent(instance.component)
            is MainComponent.Child.Help -> HelpContent(instance.component)
        }
    }
}
