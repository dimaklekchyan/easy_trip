package ru.klekchyan.easytrip.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationManager @Inject constructor() {

    private val _commands = MutableStateFlow<BaseNav?>(null)
    val commands: StateFlow<BaseNav?> = _commands.asStateFlow()

    fun setNull() {
        _commands.value = null
    }
    fun navigate(command: BaseNav) {
        _commands.value = command
    }
    fun back() {
        _commands.value = NavigationBack
    }
}