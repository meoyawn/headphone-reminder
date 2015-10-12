package common.recycler

import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView

fun <VH : RecyclerView.ViewHolder, LM : RecyclerView.LayoutManager, A : RecyclerView.Adapter<VH>>
    mkRecycler(recyclerView: RecyclerView, layoutManager: LM, adapter: A): Pair<LM, A> =
    recyclerView.let {
      it.setHasFixedSize(true)
      it.itemAnimator = DefaultItemAnimator()
      it.layoutManager = layoutManager
      it.adapter = adapter
      return Pair(layoutManager, adapter)
    }

fun <VH : RecyclerView.ViewHolder, A : RecyclerView.Adapter<VH>> A.stableIds(): A =
    apply { setHasStableIds(true) }

inline fun from(crossinline f: (Int) -> Int): GridLayoutManager.SpanSizeLookup =
    object : GridLayoutManager.SpanSizeLookup() {
      override fun getSpanSize(position: Int): Int = f(position)
    }

inline fun GridLayoutManager.spanSizeLookup(crossinline f: (Int) -> Int): GridLayoutManager =
    apply { spanSizeLookup = from(f) }