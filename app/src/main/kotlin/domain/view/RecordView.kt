package domain.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class RecordView : View {
  private val paint = Paint().apply {
    color = Color.GREEN
    strokeWidth = 2f
  }

  var data: DoubleArray? = null
    set(value) {
      field = value
      invalidate()
    }

  constructor(c: Context) : super(c)

  constructor(c: Context, attrs: AttributeSet) : super(c, attrs)

  constructor(c: Context, attrs: AttributeSet, defStyle: Int) : super(c, attrs, defStyle)

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    canvas.drawColor(Color.BLACK)

    val ds = data
    if (ds != null) {
      val halfHeight = height / 2f
      val widthRatio = width.toFloat() / ds.size
      drawIter(ds, canvas, halfHeight, widthRatio, 0)
    }
  }

  tailrec private fun drawIter(ds: DoubleArray,
                               canvas: Canvas,
                               halfHeight: Float,
                               widthRatio: Float,
                               i: Int) {
    if (i < ds.size) {
      val x = i * widthRatio
      val downY = halfHeight - ds[i].toFloat() * 50
      val upY = halfHeight
      canvas.drawLine(x, downY, x, upY, paint)
      drawIter(ds, canvas, halfHeight, widthRatio, i + 1)
    }
  }
}