package org.worshipsongs.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import org.worshipsongs.domain.DialogConfiguration;
import org.worshipsongs.worship.R;

import java.util.ArrayList;
import java.util.List;

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
    private ListView listView;
    private RadioButtonAdapter radioButtonAdapter;

    public CustomDialogBuilder(Context context, DialogConfiguration dialogConfiguration)
    {
        this.context = context;
        this.dialogConfiguration = dialogConfiguration;
        setBuilder();
    }

    private void setBuilder()
    {
        builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.MyDialogTheme));
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
        setListView(view, dialogConfiguration);
        return view;
    }

    private void setMessage(View promptsView, DialogConfiguration dialogConfiguration)
    {
        TextView messageTextView = (TextView) promptsView.findViewById(R.id.message);
        messageTextView.setText(Html.fromHtml(dialogConfiguration.getMessage()), TextView.BufferType.SPANNABLE);
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

    private void setListView(View view, DialogConfiguration dialogConfiguration)
    {
        listView = (ListView) view.findViewById(R.id.list_view);
        radioButtonAdapter = new RadioButtonAdapter(context, new ArrayList<String>());
        listView.setAdapter(radioButtonAdapter);
    }

    public void setSingleChoices(List<String> items, int checked) {
        listView.setVisibility(View.VISIBLE);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        radioButtonAdapter.clear();
        radioButtonAdapter.addAll(items);
        radioButtonAdapter.setSelectedItem(checked);
        radioButtonAdapter.notifyDataSetChanged();
    }

    public void setSingleChoiceOnClickListener(AdapterView.OnItemClickListener choiceOnClickListener) {
        listView.setOnItemClickListener(choiceOnClickListener);
    }

    private class RadioButtonAdapter extends ArrayAdapter<String>
    {
        private int selectedItem = -1;

        RadioButtonAdapter(Context context, List<String> objects)
        {
            super(context, R.layout.radiobutton_adapter, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View view = convertView;
            if (view == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                view = layoutInflater.inflate(R.layout.radiobutton_adapter, null);
            }
            setRadioButton(position, view);
            return view;
        }

        private void setRadioButton(int position, View view)
        {
            RadioButton radioButton = (RadioButton) view.findViewById(R.id.radioButton);
            if (selectedItem == position) {
                radioButton.setChecked(true);
            } else {
                radioButton.setChecked(false);
            }
        }

        public void setSelectedItem(int selectedItem)
        {
            this.selectedItem = selectedItem;
        }
    }
}
