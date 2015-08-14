package adeln.boilerplate

import android.os.IBinder

object Animations {
  val DISABLED = 0.0f
  val DEFAULT = 1.0f

  fun disableAll() = setSystemAnimationsScale(DISABLED)
  fun enableAll() = setSystemAnimationsScale(DEFAULT)

  fun setSystemAnimationsScale(animationScale: Float) {
    try {
      val windowManagerStubClazz = Class.forName("android.view.IWindowManager\$Stub")
      val asInterface = windowManagerStubClazz.getDeclaredMethod("asInterface", javaClass<IBinder>())
      val serviceManagerClazz = Class.forName("android.os.ServiceManager")
      val getService = serviceManagerClazz.getDeclaredMethod("getService", javaClass<String>())
      val windowManagerClazz = Class.forName("android.view.IWindowManager")
      val setAnimationScales = windowManagerClazz.getDeclaredMethod("setAnimationScales", javaClass<FloatArray>())
      val getAnimationScales = windowManagerClazz.getDeclaredMethod("getAnimationScales")
      val windowManagerBinder = getService.invoke(null, "window") as IBinder
      val windowManagerObj = asInterface.invoke(null, windowManagerBinder)
      val currentScales = getAnimationScales.invoke(windowManagerObj) as FloatArray
      setAnimationScales.invoke(windowManagerObj, currentScales.fill(animationScale))
    } catch (e: Exception) {
      throw RuntimeException(e)
    }
  }
}