package com.example.feature.main.component

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.example.feature.main.chat.component.ChatComponent
import com.example.feature.main.help.component.HelpComponent
import com.example.feature.main.home.component.HomeComponent
import com.example.feature.main.people.component.PeopleComponent
import com.example.dantalk.features.main.profile.component.ProfileComponent
import com.example.feature.main.search.component.SearchComponent
import com.example.feature.main.settings.component.SettingsComponent

interface MainComponent {
    val stack: Value<ChildStack<*, Child>>

    sealed interface Child {
        class Home(val component: HomeComponent) : Child
        class Search(val component: SearchComponent) : Child
        class Profile(val component: ProfileComponent) : Child
        class People(val component: PeopleComponent) : Child
        class Chat(val component: ChatComponent) : Child
        class Settings(val component: SettingsComponent) : Child
        class Help(val component: HelpComponent) : Child
    }
}
