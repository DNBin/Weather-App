package com.example.weatherapp.activity

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.adapter.CityAdapter
import com.example.weatherapp.databinding.ActivityCityBinding
import com.example.weatherapp.model.CityResponse
import com.example.weatherapp.viewmodel.CityViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CityActivity : AppCompatActivity() {
    lateinit var binding: ActivityCityBinding
    private val cityAdapter by lazy { CityAdapter() }
    private val cityViewModel by lazy { CityViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }


        binding.apply {
            edtCity.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    progressBar2.visibility = View.VISIBLE
                    cityViewModel.loadCities(s.toString(), 10)
                        .enqueue(object : Callback<CityResponse> {
                            override fun onResponse(
                                call: Call<CityResponse>,
                                response: Response<CityResponse>
                            ) {
                                if (response.isSuccessful) {
                                    val data = response.body()
                                    data?.let {
                                        progressBar2.visibility = View.GONE
                                        cityAdapter.differ.submitList(it)
                                        rvCity.apply {
                                            layoutManager = LinearLayoutManager(
                                                this@CityActivity,
                                                LinearLayoutManager.HORIZONTAL,
                                                false
                                            )
                                            adapter = cityAdapter
                                        }
                                    }
                                }
                            }

                            override fun onFailure(call: Call<CityResponse>, t: Throwable) {

                            }

                        })
                }

            })
            btnBack.setOnClickListener{
                onBackPressed()
            }
        }

    }
}