package com.example.aerosports.mainui.dialog

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aerosports.R
import kotlinx.android.synthetic.main.item_device.view.*

class DeviceBluetoothAdapter(private var items: List<BluetoothDevice>, private val context: Context, onclick: CallBackDevice) : RecyclerView.Adapter<ViewHolder>() {
    private val mOnclick: CallBackDevice = onclick

    fun updateData(items: List<BluetoothDevice>){
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_device, parent, false))
    }

    override fun getItemCount(): Int {
        if (items == null) return 0
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < 0 || position >= items.size) return
        holder.bindData(items?.get(position))
        holder.itemView.setOnClickListener { mOnclick.onclickItem(items?.get(position)) }
    }
}

interface CallBackDevice {
    fun onclickItem(item: BluetoothDevice)
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bindData(item: BluetoothDevice){
        item.let {
            itemView.tvNameDevice.text = item.name
            itemView.tvCodeDevice.text = item.address
        }
    }
}