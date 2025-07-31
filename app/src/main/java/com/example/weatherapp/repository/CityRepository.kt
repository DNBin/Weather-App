package com.example.weatherapp.repository

import com.example.weatherapp.server.ApiServices

class CityRepository(val api: ApiServices) {
    fun getCities(q: String, limit: Int) =
        api.getCityList(q, limit, "b7e07a13e6d6801098ab2e24497ce363")

}