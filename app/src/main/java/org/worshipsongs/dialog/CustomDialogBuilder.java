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
 * Version : 3.x
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
        editText.setVisibility(dialogConfiguration.isEditTextVisibility() ? View.VISIBLE : View.GONE);
    }

    public EditText getEditText()
    {
        return editText;
    }


}
