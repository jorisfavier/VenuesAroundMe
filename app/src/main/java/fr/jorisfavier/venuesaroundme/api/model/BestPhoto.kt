package fr.jorisfavier.venuesaroundme.api.model

data class BestPhoto(
    val id: String,
    val createdAt: Int,
    val prefix: String,
    val suffix: String,
    val width: Int,
    val height: Int,
    val visibility: String
)
