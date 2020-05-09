package fr.jorisfavier.venuesaroundme.api.model

data class Category(
    val id: String,
    val name: String,
    val pluralName: String,
    val primary: Boolean,
    val shortName: String
)
