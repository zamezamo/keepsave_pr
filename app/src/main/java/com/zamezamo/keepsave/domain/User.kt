package com.zamezamo.keepsave.domain

data class User (

    val email: String? = "",
    val name: String? = email?.substringBefore('@')

)