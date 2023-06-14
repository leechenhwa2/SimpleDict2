package com.uniuwo.simpledict.ui.wordFavorite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uniuwo.simpledcit.core.databus.FavoriteEntry
import com.uniuwo.simpledcit.core.databus.SimpleDataBus
import com.uniuwo.simpledcit.core.models.WordHolder
import com.uniuwo.simpledcit.core.models.WordListViewModel
import com.uniuwo.simpledict.databinding.FragmentWordFavoriteCardBinding

class FavoriteItemRecyclerViewAdapter(
    private val values: List<FavoriteEntry>,
    private val onItemClickListener: View.OnClickListener?
) : RecyclerView.Adapter<FavoriteItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentWordFavoriteCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.word
        holder.contentView.text = ""

        val dictItems = WordListViewModel.findByWord(item.word)
        val dictItem = if (dictItems.isNotEmpty()) dictItems[0] else null
        if (dictItem != null) {
            val content = dictItems.map { it.entry.content }.joinToString("; ")
            holder.contentView.text = content
        }

        holder.itemView.setOnClickListener {
            WordListViewModel.currentItem = WordHolder(item.word)
            onItemClickListener?.onClick(it)
        }

        holder.favoriteView.isActivated = true
        holder.favoriteView.setOnClickListener {
            val state = !it.isActivated
            it.isActivated = state
            SimpleDataBus.saveFavorite(item.word, state)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentWordFavoriteCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemTitle
        val contentView: TextView = binding.itemContent

        val favoriteView: ImageView = binding.favorite

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}