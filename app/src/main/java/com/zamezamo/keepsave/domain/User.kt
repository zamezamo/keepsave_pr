package com.zamezamo.keepsave.domain

data class User (

    val email: String? = null,
    val name: String? = email?.substringBefore('@')

)