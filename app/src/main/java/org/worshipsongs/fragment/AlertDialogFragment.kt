package org.worshipsongs.fragment


import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import org.apache.commons.lang3.StringUtils
import org.worshipsongs.CommonConstants
import org.worshipsongs.R


/**
 * @author Madasamy
 * @version 3.x
 */

class AlertDialogFragment : DialogFragment()
{
    private var dialogListener: DialogListener? = null
    private var visiblePositiveButton = true
    private var visibleNegativeButton = true

    private val customTitleVIew: View
        get()
        {
            val inflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val titleView = inflater.inflate(R.layout.dialog_custom_title, null)
            val titleTextView = titleView.findViewById<View>(R.id.title) as TextView
            titleTextView.text = arguments!!.getString(CommonConstants.TITLE_KEY)
            titleTextView.visibility = if (StringUtils.isBlank(titleTextView.text.toString())) View.GONE else View.VISIBLE

            val messageTextView = titleView.findViewById<View>(R.id.subtitle) as TextView
            messageTextView.setTextColor(activity!!.resources.getColor(R.color.black_semi_transparent))
            messageTextView.text = arguments!!.getString(CommonConstants.MESSAGE_KEY)
            return titleView
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        val alertDialogBuilder = AlertDialog.Builder(ContextThemeWrapper(activity, R.style.DialogTheme))
        alertDialogBuilder.setCustomTitle(customTitleVIew)
        if (visibleNegativeButton)
        {
            alertDialogBuilder.setNegativeButton(R.string.cancel) { dialog, id ->
                dialog.cancel()
                if (dialogListener != null)
                {
                    dialogListener!!.onClickNegativeButton()
                }
            }
        }
        if (visiblePositiveButton)
        {
            alertDialogBuilder.setPositiveButton(R.string.ok) { dialog, which ->
                dialog.cancel()
                if (dialogListener != null)
                {
                    dialogListener!!.onClickPositiveButton(arguments, tag)
                }
            }
        }
        return alertDialogBuilder.create()
    }


    interface DialogListener
    {
        fun onClickPositiveButton(bundle: Bundle?, tag: String?)

        fun onClickNegativeButton()
    }

    fun setDialogListener(dialogListener: DialogListener)
    {
        this.dialogListener = dialogListener
    }

    fun setVisiblePositiveButton(visiblePositiveButton: Boolean)
    {
        this.visiblePositiveButton = visiblePositiveButton
    }

    fun setVisibleNegativeButton(visibleNegativeButton: Boolean)
    {
        this.visibleNegativeButton = visibleNegativeButton
    }


    override fun show(manager: FragmentManager, tag: String?)
    {
        try
        {
            val fragmentTransaction = manager.beginTransaction()
            fragmentTransaction.add(this, tag).addToBackStack(null)
            fragmentTransaction.commitAllowingStateLoss()
        } catch (e: IllegalStateException)
        {
            Log.e(AlertDialogFragment::class.java.simpleName, "Error", e)
        }
    }


    companion object
    {

        fun newInstance(bundle: Bundle): AlertDialogFragment
        {
            val fragment = AlertDialogFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

}