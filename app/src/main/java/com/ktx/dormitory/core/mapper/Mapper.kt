package com.ktx.dormitory.core.mapper

/**
 * Interface for mapping between different layers (DTO, Entity, Domain).
 */
interface Mapper<I, O> {
    fun map(input: I): O
}

/**
 * Interface for mapping to and from different layers.
 */
interface BidirectionalMapper<I, O> : Mapper<I, O> {
    fun mapBack(output: O): I
}
