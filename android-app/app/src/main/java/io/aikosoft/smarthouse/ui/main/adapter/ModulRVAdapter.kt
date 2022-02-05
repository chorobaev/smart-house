package io.aikosoft.smarthouse.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.aikosoft.smarthouse.R
import io.aikosoft.smarthouse.data.models.Module
import io.aikosoft.smarthouse.data.models.ModuleSmartHouseLiz
import kotlinx.android.synthetic.main.item_module_sm_liz.view.*

class ModulRVAdapter : RecyclerView.Adapter<ModulRVAdapter.ViewHolder>() {
    private val modules = ArrayList<ModuleSmartHouseLiz>()
    private var onModuleClickListener: ((Module) -> Unit)? = null

    fun setOnModuleClickListener(listener: ((Module) -> Unit)?) {
        onModuleClickListener = listener
    }

    fun updateModules(modules: List<ModuleSmartHouseLiz>) {
        this.modules.run { clear(); addAll(modules) }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_module_sm_liz, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(modules[position])
    }

    override fun getItemCount(): Int {
        return modules.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(module: ModuleSmartHouseLiz) {
            with(itemView) {
                tv_module_name.text = module.name
            }
            itemView.setOnClickListener {
                try {
                    onModuleClickListener?.invoke(modules[adapterPosition])
                } catch (ignored: ArrayIndexOutOfBoundsException) {
                }
            }
        }
    }
}