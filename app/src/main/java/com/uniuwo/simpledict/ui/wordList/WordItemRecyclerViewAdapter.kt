package com.uniuwo.simpledict.ui.wordList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uniuwo.simpledict.databinding.FragmentWordListCardBinding
import com.uniuwo.simpledict.databus.SimpleDataBus
import com.uniuwo.simpledict.models.WordEntry
import com.uniuwo.simpledict.models.WordListViewModel


class WordItemRecyclerViewAdapter(
    private val values: List<WordEntry>,
    private val onItemClickListener: View.OnClickListener?
) : RecyclerView.Adapter<WordItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentWordListCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.entry.word
        holder.contentView.text = item.entry.content

        holder.itemView.setOnClickListener {
            WordListViewModel.currentItem = item
            onItemClickListener?.onClick(it)
        }

        holder.favoriteView.isActivated = SimpleDataBus.isFavorite(item.entry.word)
        holder.favoriteView.setOnClickListener {
            val state = !it.isActivated
            it.isActivated = state
            SimpleDataBus.saveFavorite(item.entry.word, state)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentWordListCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemTitle
        val contentView: TextView = binding.itemContent

        val favoriteView: ImageView = binding.favorite

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}