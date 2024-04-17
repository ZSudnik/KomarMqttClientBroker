package io.zibi.komar.mclient.core

sealed interface ProcessorResult {
    data object RESULT_SUCCESS: ProcessorResult
    data object RESULT_FAIL: ProcessorResult
}