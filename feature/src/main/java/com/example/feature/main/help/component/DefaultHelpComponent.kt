package com.example.feature.main.help.component

import com.arkivanov.decompose.ComponentContext

class DefaultHelpComponent(
    componentContext: ComponentContext,
    private val onBack: () -> Unit
) : ComponentContext by componentContext, HelpComponent {
    override fun navigateBack() {
        onBack()
    }
}
