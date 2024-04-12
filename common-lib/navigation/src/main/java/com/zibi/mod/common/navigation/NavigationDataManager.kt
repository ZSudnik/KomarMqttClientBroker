package com.zibi.mod.common.navigation

interface NavigationDataRetriever {
  fun <T : Any> retrieve(destination: Destination): T?

  fun <T : Any> retrieveLastData(): T?
}

interface NavigationDataStorage : NavigationDataRetriever {

  fun <T : Any> store(destination: Destination, value: T)

  fun remove(destination: Destination)
}

@Suppress("UNCHECKED_CAST")
class NavigationDataManager : NavigationDataStorage {

  private val data: MutableMap<String, Any> = mutableMapOf()

  private var lastDestination: Destination? = null

  override fun <T : Any> retrieve(destination: Destination): T? {
    return data[destination.route] as? T
  }

  override fun <T : Any> retrieveLastData(): T? {
    return lastDestination?.let { destination ->
      retrieve(destination)
    }
  }

  override fun <T : Any> store(destination: Destination, value: T) {
    lastDestination = destination
    data[destination.route] = value
  }

  override fun remove(destination: Destination) {
    data.remove(destination.route)
  }
}