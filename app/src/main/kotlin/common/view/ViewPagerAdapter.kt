package common.view

import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import rx.Observer

class ViewPagerAdapter(val cnt: Int,
                       val detaches: Observer<View>,
                       val inflates: (ViewGroup, Int) -> View,
                       val titles: (Int) -> CharSequence) : PagerAdapter() {
  override fun isViewFromObject(view: View?, `object`: Any?): Boolean = view === `object`
  override fun getCount(): Int = cnt
  override fun instantiateItem(container: ViewGroup, position: Int): Any? =
      inflates(container, position).apply { container.addView(this) }

  override fun getPageTitle(position: Int): CharSequence? = titles(position)
  override fun destroyItem(container: ViewGroup, position: Int, obj: Any): Unit {
    if (obj is View) {
      container.removeView(obj)
      detaches.onNext(obj)
    }
  }
}