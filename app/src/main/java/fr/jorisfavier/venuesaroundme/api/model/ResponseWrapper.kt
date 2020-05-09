package fr.jorisfavier.venuesaroundme.api.model

data class ResponseWrapper<T>(
    val meta: Meta,
    val response: T
)
