package com.example.weatherapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.activity.MainActivity
import com.example.weatherapp.databinding.ItemCityBinding
import com.example.weatherapp.model.CityResponse

class CityAdapter : RecyclerView.Adapter<CityAdapter.ViewHolder>() {

    private lateinit var binding: ItemCityBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemCityBinding.inflate(inflater, parent, false)
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: CityAdapter.ViewHolder, position: Int) {
        val binding = ItemCityBinding.bind(holder.itemView)
        binding.tvCity.text = differ.currentList[position].name
        binding.root.setOnClickListener {
            val intent = Intent(binding.root.context, MainActivity::class.java)
            intent.putExtra("lat", differ.currentList[position].lat)
            intent.putExtra("lng", differ.currentList[position].lon)
            intent.putExtra("name", differ.currentList[position].name)
            binding.root.context.startActivity(intent)
        }

    }

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val differCallback = object : DiffUtil.ItemCallback<CityResponse.CityResponseItem>() {
        override fun areItemsTheSame(
            oldItem: CityResponse.CityResponseItem,
            newItem: CityResponse.CityResponseItem
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: CityResponse.CityResponseItem,
            newItem: CityResponse.CityResponseItem
        ): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

}