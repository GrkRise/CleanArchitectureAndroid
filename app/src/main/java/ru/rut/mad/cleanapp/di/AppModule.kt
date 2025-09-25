package ru.rut.mad.cleanapp.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.rut.mad.cleanapp.main.vm.MainViewModel

val appModule = module {
    // Ключевое слово 'viewModel' сообщает Koin,
    // что это ViewModel и его жизненный цикл должен управляться соответственно.
    viewModel { MainViewModel() }
}