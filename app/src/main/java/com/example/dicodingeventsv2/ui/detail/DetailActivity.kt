package com.example.dicodingeventsv2.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.example.dicodingeventsv2.R
import com.example.dicodingeventsv2.data.local.entity.EventEntity
import com.example.dicodingeventsv2.databinding.ActivityDetailBinding
import com.example.dicodingeventsv2.ui.viewModels.MainViewModel
import com.example.dicodingeventsv2.ui.viewModels.ViewModelFactory
import com.squareup.picasso.Picasso

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel by viewModels<MainViewModel> { ViewModelFactory.getInstance(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getParcelableExtra<EventEntity>("EXTRA_EVENT")?.let { setupEventDetail(it) }
        supportActionBar?.title = "Detail Event"
    }

    private fun setupEventDetail(event: EventEntity) = with(binding) {
        changeFavoriteIcon(event.isFavorite)
        progressBar1.visibility = View.GONE
        eventName.text = event.name
        eventDescription.text = HtmlCompat.fromHtml(event.description, HtmlCompat.FROM_HTML_MODE_LEGACY)
        ownerName.text = event.ownerName
        eventStartTime.text = event.beginTime
        eventEndTime.text = event.endTime
        eventQuota.text = event.quota.toString()
        quotaLeft.text = (event.quota - event.registrants).toString()
        Picasso.get().load(event.mediaCover).into(ivMediaCoverEvent)

        btnRegister.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(event.link)))
        }

        favoriteFab.setOnClickListener {
            event.isFavorite = !event.isFavorite
            changeFavoriteIcon(event.isFavorite)
            handleFavoriteChange(event)
        }
    }

    private fun handleFavoriteChange(event: EventEntity) {
        val message = if (event.isFavorite) {
            viewModel.addEventToFavorite(event)
            "Successfully added the event to Favorites"
        } else {
            viewModel.removeEventFromFavorite(event)
            "Successfully removed the event from Favorites"
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun changeFavoriteIcon(isFavorite: Boolean) {
        binding.favoriteFab.setImageResource(
            if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        )
    }
}