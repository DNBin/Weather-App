package com.example.weatherapp.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.adapter.ForecastAdapter
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.model.CurrentResponse
import com.example.weatherapp.model.ForecastResponse
import com.example.weatherapp.viewmodel.WeatherViewModel
import com.github.matteobattilana.weather.PrecipType
import eightbitlab.com.blurview.RenderScriptBlur
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val weatherViewModel: WeatherViewModel by viewModels()
    private val calendar by lazy { Calendar.getInstance() }
    private val forecastAdapter by lazy { ForecastAdapter() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }

        binding.apply {

            var lat = intent.getDoubleExtra("lat", 0.0)
            var lng = intent.getDoubleExtra("lng", 0.0)
            var name = intent.getStringExtra("name")

            if(lat==0.0) {
                lat = 51.50
                lng = -0.12
                name = "London"
            }

            btnAddCity.setOnClickListener {
                startActivity(Intent(this@MainActivity, CityActivity::class.java))
            }

            tvCity.text = name
            progressBar.visibility = View.VISIBLE

            weatherViewModel.loadCurrentWeather(lat, lng, "metric")
                .enqueue(object : Callback<CurrentResponse> {
                    override fun onResponse(
                        call: Call<CurrentResponse>,
                        response: Response<CurrentResponse>
                    ) {
                        if (response.isSuccessful) {
                            val data = response.body()
                            progressBar.visibility = View.GONE
                            detailLayout.visibility = View.VISIBLE
                            data?.let {
                                tvStatus.text = it.weather?.get(0)?.main ?: "-"
                                tvWind.text =
                                    it.wind?.speed?.let { Math.round(it).toString() } + "Km"
                                tvHumidity.text = it.main?.humidity.toString() + "%"
                                tvCurrentTemp.text =
                                    it.main?.temp?.let { Math.round(it).toString() } + "°"
                                tvMaxTemp.text =
                                    it.main?.tempMax?.let { Math.round(it).toString() } + "°"
                                tvMinTemp.text =
                                    it.main?.tempMin?.let { Math.round(it).toString() } + "°"

                                val drawable = if (isNightNow()) R.drawable.night_bg
                                else {
                                    setDynamicallyWallpaper(it.weather?.get(0)?.icon ?: "-")
                                }
                                ivBackground.setImageResource(drawable)
                                setEffectRainSnow(it.weather?.get(0)?.icon ?: "-")
                            }
                        }
                    }

                    override fun onFailure(call: Call<CurrentResponse>, t: Throwable) {
                        Log.d("binbin", t.message.toString())
                        Toast.makeText(this@MainActivity, t.message.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }

                })


            var radius = 10f
            val decorView = window.decorView
            val rootView = (decorView.findViewById(android.R.id.content) as ViewGroup?)
            val windowBackground = decorView.background

            rootView?.let {
                blurView.setupWith(it, RenderScriptBlur(this@MainActivity))
                    .setFrameClearDrawable(windowBackground)
                    .setBlurRadius(radius)
                blurView.outlineProvider = ViewOutlineProvider.BACKGROUND
                blurView.clipToOutline = true
            }

            weatherViewModel.getForecastWeather(lat, lng, "metric").enqueue(object : Callback<ForecastResponse> {
                override fun onResponse(
                    call: Call<ForecastResponse>,
                    response: Response<ForecastResponse>
                ) {
                    if(response.isSuccessful){
                        val data = response.body()
                        blurView.visibility = View.VISIBLE
                        data?.let {
                            forecastAdapter.differ.submitList(it.list)
                            rvForecast.apply {
                                layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
                                adapter = forecastAdapter
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {

                }

            })
        }
    }

    private fun isNightNow(): Boolean {
        return calendar.get(Calendar.HOUR_OF_DAY) >= 18
    }

    private fun setDynamicallyWallpaper(icon: String): Int {
        return when (icon.dropLast(1)) {
            "01" -> {
                initWeatherView(PrecipType.CLEAR)
                R.drawable.snow_bg
            }

            "02", "03", "04" -> {
                initWeatherView(PrecipType.CLEAR)
                R.drawable.cloudy_bg
            }

            "09", "10", "11" -> {
                initWeatherView(PrecipType.RAIN)
                R.drawable.rainy_bg
            }

            "13" -> {
                initWeatherView(PrecipType.SNOW)
                R.drawable.snow_bg
            }

            "50" -> {
                initWeatherView(PrecipType.CLEAR)
                R.drawable.haze_bg
            }

            else -> 0
        }
    }

    private fun setEffectRainSnow(icon: String) {
        when (icon.dropLast(1)) {
            "01" -> {
                initWeatherView(PrecipType.CLEAR)
            }

            "02", "03", "04" -> {
                initWeatherView(PrecipType.CLEAR)
            }

            "09", "10", "11" -> {
                initWeatherView(PrecipType.RAIN)
            }

            "13" -> {
                initWeatherView(PrecipType.SNOW)
            }

            "50" -> {
                initWeatherView(PrecipType.CLEAR)
            }
        }
    }

    private fun initWeatherView(type: PrecipType) {
        binding.weatherView.apply {
            setWeatherData(type)
            angle = 20
            emissionRate = 100.0f
        }
    }
}