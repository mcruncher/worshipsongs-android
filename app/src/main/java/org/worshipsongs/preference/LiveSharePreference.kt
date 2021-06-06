package org.worshipsongs.preference;

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import androidx.preference.DialogPreference
import androidx.preference.PreferenceManager
import org.worshipsongs.CommonConstants
import org.worshipsongs.R

/**
 * @author Madasamy
 * @since 3.3
 */
class LiveSharePreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs) {

    private var defaultSharedPreferences: SharedPreferences? = null
    private var liveSharePath: String? = null

    init {
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        liveSharePath = defaultSharedPreferences!!.getString(CommonConstants.LIVE_SHARE_PATH_KEY, "")
    }

    override fun onClick() {
        val customDialogView = LayoutInflater.from(context).inflate(R.layout.live_share_preference,
                null)
        val liveSharePathEditText = customDialogView.findViewById<EditText>(R.id.live_share_path_edit_text)
        liveSharePathEditText.setText(liveSharePath)
        val dialogBuilder = AlertDialog.Builder(context)
                .setView(customDialogView)
                .setTitle(context.getString(R.string.live_share_title));
        dialogBuilder.setPositiveButton(R.string.ok) { dialog, which ->
            setLiveSharePath(liveSharePathEditText)
            dialog.cancel()
        }
        dialogBuilder.show()
    }

    private fun setLiveSharePath(liveSharePathEditText: EditText) {
        liveSharePath = liveSharePathEditText.text.toString()
        defaultSharedPreferences!!.edit().putString(CommonConstants.LIVE_SHARE_PATH_KEY,
                liveSharePathEditText.text.toString()).apply()
    }
}
