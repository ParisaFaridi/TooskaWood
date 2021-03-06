package com.example.pottery.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pottery.R
import com.example.pottery.databinding.ItemViewBinding
import com.example.pottery.room.Item

typealias ItemClickHandler = (Item) -> Unit
typealias ItemEditClickHandler = (Item) ->Unit

class ItemAdapter(private val clickHandler: ItemClickHandler,private val editClickHandler: ItemEditClickHandler):
    ListAdapter<Item, ItemAdapter.ItemHolder>(ItemDiffCallBack) {

    object ItemDiffCallBack: DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == newItem.id
        }
    }
    class ItemHolder(val binding: ItemViewBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val binding: ItemViewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_view,
            parent,
            false
        )
        return ItemHolder(binding)
    }
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.binding.item = getItem(position)
        holder.binding.btnDelete.setOnClickListener {
            clickHandler.invoke(getItem(position))
        }
        holder.binding.btnEdit.setOnClickListener {
            editClickHandler.invoke(getItem(position))
        }
    }
}