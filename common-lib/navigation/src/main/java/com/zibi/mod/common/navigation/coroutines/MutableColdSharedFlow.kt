package com.zibi.mod.common.navigation.coroutines

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class)
class MutableColdSharedFlow<T> : Flow<T> {

  private val _sharedFlow = MutableSharedFlow<T>(replay = 1)
  private val sharedFlow = _sharedFlow.onEach {
    _sharedFlow.resetReplayCache()
  }

  override suspend fun collect(collector: FlowCollector<T>) {
    sharedFlow.collect(collector)
  }

  suspend fun emit(value: T) {
    _sharedFlow.emit(value)
  }

}
