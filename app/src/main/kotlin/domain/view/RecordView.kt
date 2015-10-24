package domain.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class RecordView : View {
  val paint = Paint().apply {
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
    val halfHeight = height / 2f
    val widthRatio = width.toFloat() / (data?.size ?: 1)

    canvas.drawColor(Color.BLACK)

    // TODO make non allocating
    data?.forEachIndexed { i, d ->
      val x = i * widthRatio
      val downY = halfHeight - d.toFloat() * 100
      val upY = halfHeight
      canvas.drawLine(x, downY, x, upY, paint)
    }
  }
}