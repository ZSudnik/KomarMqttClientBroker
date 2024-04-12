package com.zibi.mod.common.navigation

import androidx.lifecycle.ViewModel

interface NavigationViewModel {
  fun <T : Any> store(destination: Destination, value: T)
  fun <T : Any> retrieve(destination: Destination): T?
}

class NavigationViewModelImpl constructor(
  private val dataStorage: NavigationDataStorage
) : ViewModel(), NavigationViewModel {

  private val storedDestinations: MutableSet<Destination> = mutableSetOf()

  override fun <T : Any> store(destination: Destination, value: T) {
    storedDestinations.add(destination)
    dataStorage.store(destination, value)
  }

  override fun <T : Any> retrieve(destination: Destination): T? {
    return dataStorage.retrieve<T>(destination)?.also {
      dataStorage.remove(destination)
    }
  }

  override fun onCleared() {
    super.onCleared()
    storedDestinations.forEach { destination ->
      dataStorage.remove(destination)
    }
  }
}
