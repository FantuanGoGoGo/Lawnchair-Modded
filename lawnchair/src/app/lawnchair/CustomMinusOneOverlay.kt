package app.lawnchair

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.isVisible
import app.lawnchair.minusone.MinusOneScreen
import com.android.launcher3.views.BaseDragLayer
import com.android.systemui.plugins.shared.LauncherOverlayManager
import app.lawnchair.preferences2.PreferenceManager2

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
        // Register this overlay with the workspace
        launcher.setLauncherOverlay(this)
        val prefs = PreferenceManager2.getInstance(launcher)
        if (prefs.enableFeed.get()) {
            // Show minus one screen immediately if the feed is enabled
            openOverlay()
        } else {
            overlayView.visibility = View.GONE
        }
    }

    override fun onDetachedFromWindow() {
        overlayView.visibility = View.GONE
        launcher.setLauncherOverlay(null)
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

    override fun openOverlay() {
        overlayView.visibility = View.VISIBLE
        overlayView.translationX = 0f
    }

    override fun hideOverlay(animate: Boolean) {
        overlayView.visibility = View.GONE
    }

    override fun hideOverlay(duration: Int) {
        overlayView.visibility = View.GONE
    }

    override fun setOverlayCallbacks(callbacks: LauncherOverlayCallbacks?) {
        this.callbacks = callbacks
    }

    companion object {
        fun minusOneAvailable(context: Context) = true
    }
}
