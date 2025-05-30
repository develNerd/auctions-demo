package org.auctions.klaravik.data.usecases

import org.auctions.klaravik.data.repositories.FlowApiResult


abstract class BaseUseCase<in Input,out T>(){
    abstract suspend fun execute(input: Input): T
}

abstract class FlowBaseUseCase<in Input,out T : Any>(){
    abstract suspend fun execute(input: Input):  FlowApiResult<T>
}



typealias StateFlowResult<T> = FlowApiResult<T>