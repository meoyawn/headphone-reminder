package domain.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

class DrawingView : View {
  var drawF: (Canvas) -> Unit = {}

  constructor(c: Context) : super(c)

  constructor(c: Context, attrs: AttributeSet) : super(c, attrs)

  constructor(c: Context, attrs: AttributeSet, defStyle: Int) : super(c, attrs, defStyle)

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    drawF(canvas)
  }
}