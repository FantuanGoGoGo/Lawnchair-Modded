package app.lawnchair

import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.isVisible
import app.lawnchair.minusone.MinusOneScreen
import com.android.launcher3.views.BaseDragLayer
import com.android.systemui.plugins.shared.LauncherOverlayManager
import com.android.systemui.plugins.shared.LauncherOverlayManager.LauncherOverlayCallbacks;

class CustomMinusOneOverlay(private val launcher: LawnchairLauncher) :
    LauncherOverlayManager,
    LauncherOverlayManager.LauncherOverlay {

    private var callbacks: LauncherOverlayCallbacks? = null
    private val overlayView: ComposeView = ComposeView(launcher).apply {
        setContent { MinusOneScreen() }
        visibility = View.GONE
    }

    init {
        launcher.dragLayer.addView(
            overlayView,
            BaseDragLayer.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    override fun onAttachedToWindow() {
        overlayView.visibility = View.VISIBLE
    }

    override fun onDetachedFromWindow() {
        overlayView.visibility = View.GONE
    }

    override fun onScrollInteractionBegin() {}

    override fun onScrollInteractionEnd() {}

    override fun onScrollChange(progress: Float, rtl: Boolean) {
        val width = overlayView.width.toFloat()
        val direction = if (rtl) 1 else -1
        overlayView.translationX = width * (1 - progress) * direction
        if (!overlayView.isVisible && progress > 0f) overlayView.visibility = View.VISIBLE
        if (progress == 0f) overlayView.visibility = View.GONE
        callbacks?.onOverlayScrollChanged(progress)
    }

    override fun setOverlayCallbacks(callbacks: LauncherOverlayCallbacks?) {
        this.callbacks = callbacks
    }

    companion object {
        fun minusOneAvailable(context: android.content.Context) = true
    }
}
