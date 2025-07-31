package com.example.weatherapp.repository

import com.example.weatherapp.server.ApiServices

class WeatherRepository(val api: ApiServices) {
    fun getCurrentWeather(lat: Double, lng: Double, units: String) =
        api.getCurrentWeather(lat, lng, units, "b7e07a13e6d6801098ab2e24497ce363")

    fun getForecastWeather(lat: Double, lng: Double, units: String) =
        api.getForecastWeather(lat, lng, units, "b7e07a13e6d6801098ab2e24497ce363")
}