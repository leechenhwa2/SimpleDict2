package com.uniuwo.simpledict.ui.wordList

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uniuwo.simpledict.databinding.FragmentWordListCardBinding
import com.uniuwo.simpledcit.core.databus.SimpleDataBus
import com.uniuwo.simpledcit.core.models.WordHolder
import com.uniuwo.simpledcit.core.models.WordListViewModel


class WordItemRecyclerViewAdapter(
    private val context: Activity,
    private val values: List<WordHolder>,
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
        holder.idView.text = item.word
        holder.contentView.text = ""

        //TODO
        Thread {
            val dictItems = WordListViewModel.findByWord(item.word)
            val dictItem = if (dictItems.isNotEmpty()) dictItems[0] else null
            if (dictItem != null) {
                val content = dictItems.map { it.entry.content }.joinToString("; ")

                context.runOnUiThread {
                    holder.contentView.text = content
                }
            }

            val isFavorite = SimpleDataBus.isFavorite(item.word)
            context.runOnUiThread {
                holder.favoriteView.isActivated = isFavorite
            }
        }.start()

        holder.itemView.setOnClickListener {
            WordListViewModel.currentItem = item
            onItemClickListener?.onClick(it)
        }

        holder.favoriteView.setOnClickListener {
            val state = !it.isActivated
            it.isActivated = state
            SimpleDataBus.saveFavorite(item.word, state)
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