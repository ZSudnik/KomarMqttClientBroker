package io.zibi.komar.mclient

import kotlinx.coroutines.*

class Incrementor() {
    var sharedCounter: Int = 0
        private set

    fun updateCounterIfNecessary(shouldIActuallyIncrement: Boolean) {
        if (shouldIActuallyIncrement) {
            synchronized(this) {
                //only locks when needed, using the Incrementor`s instance as the lock.
                sharedCounter++
            }
        }
    }
}
fun main() = runBlocking {
    val incrementor = io.zibi.komar.mclient.Incrementor()
    val scope = CoroutineScope(newFixedThreadPoolContext(4, "synchronizationPool")) // We want our code to run on 4 threads
    scope.launch {
        val coroutines = 1.rangeTo(1000).map {
            //create 1000 coroutines (light-weight threads).
            launch {
                for (i in 1..1000) { // and in each of them, increment the sharedCounter 1000 times.
                    incrementor.updateCounterIfNecessary(it % 2 == 0)
                }
            }
        }
        coroutines.forEach { corotuine ->
            corotuine.join() // wait for all coroutines to finish their jobs.
        }
    }.join()
    println("The number of shared counter is ${incrementor.sharedCounter}")
}