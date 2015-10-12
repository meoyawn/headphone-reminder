package common.view

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

class RecyclerViewHolder(v: View) : RecyclerView.ViewHolder(v)
class RecyclerViewAdapter(val cnt: () -> Int,
                          val itemId: (position: Int) -> Long,
                          val inflate: (parent: ViewGroup, viewType: Int) -> View,
                          val bind: (vh: RecyclerView.ViewHolder, position: Int) -> Unit,
                          val clicks: (position: Int) -> Unit) : RecyclerView.Adapter<RecyclerViewHolder>() {
  override fun getItemId(position: Int): Long =
      itemId(position)

  override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int): Unit =
      bind(holder, position)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder =
      inflate(parent, viewType).let {
        RecyclerViewHolder(it).apply {
          it.setOnClickListener {
            clicks(adapterPosition)
          }
        }
      }

  override fun getItemCount(): Int = cnt()
}

class MutableRef<T>(var get: T)