package org.worshipsongs.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.worshipsongs.domain.DialogConfiguration;
import org.worshipsongs.worship.R;

/**
 * Author : Madasamy
 * Version : x.x.x
 */

public class CustomDialogBuilder
{

    private final Context context;
    private final DialogConfiguration dialogConfiguration;
    private EditText editText;
    private AlertDialog.Builder builder;

    public CustomDialogBuilder(Context context, DialogConfiguration dialogConfiguration)
    {
        this.context = context;
        this.dialogConfiguration = dialogConfiguration;
        setBuilder();
    }

    private void setBuilder()
    {
        builder = new AlertDialog.Builder(context);
        builder.setView(getCustomView(context, dialogConfiguration));
        builder.setTitle(dialogConfiguration.getTitle());
    }

    public AlertDialog.Builder getBuilder()
    {
        return builder;
    }

    private View getCustomView(Context context, DialogConfiguration dialogConfiguration)
    {
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.custom_dialog, null);
        setMessage(view, dialogConfiguration);
        setEditText(view, dialogConfiguration);
        return view;
    }

    private void setMessage(View promptsView, DialogConfiguration dialogConfiguration)
    {
        TextView messageTextView = (TextView) promptsView.findViewById(R.id.message);
        messageTextView.setText(dialogConfiguration.getMessage());
        messageTextView.setVisibility(dialogConfiguration.getMessage().isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void setEditText(View promptsView, DialogConfiguration dialogConfiguration)
    {
        editText = (EditText) promptsView.findViewById(R.id.name_edit_text);
       // editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        editText.setVisibility(dialogConfiguration.isEditTextVisibility() ? View.VISIBLE : View.GONE);

//        final InputMethodManager mImm = (InputMethodManager)
//                context.getSystemService(Context.INPUT_METHOD_SERVICE);
//        mImm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
//        editText.setFocusable(true);
//        editText.setOnFocusChangeListener(new View.OnFocusChangeListener()
//        {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus)
//            {
//                if (hasFocus)
//                    mImm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
//                else
//                    mImm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
//            }
//        });
    }

    public EditText getEditText()
    {
        return editText;
    }


}
