package com.lab.tb.distributed.base

import android.app.Activity
import java.lang.ref.WeakReference

object AndroidComponents {
    private var weakCurrentActivity: WeakReference<Activity>? = null

    fun setCurrentActivity(activity: Activity) {
        weakCurrentActivity = WeakReference(activity)
    }

    fun getCurrentActivity(): Activity? = weakCurrentActivity?.get()
}
