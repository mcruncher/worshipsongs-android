package org.worshipsongs.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.activity.ServiceSongsActivity;
import org.worshipsongs.adapter.TitleAdapter;
import org.worshipsongs.utils.PropertyUtils;
import org.worshipsongs.worship.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Author : Madasamy
 * Version : 4.x
 */

public class ServicesFragment extends Fragment implements TitleAdapter.TitleAdapterListener<String>, AlertDialogFragment.DialogListener
{
    private List<String> services = new ArrayList<>();
    private Parcelable state;
    private ListView serviceListView;
    private TitleAdapter<String> titleAdapter;
    private View headerView;

    public static ServicesFragment newInstance()
    {
        return new ServicesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            state = savedInstanceState.getParcelable(CommonConstants.STATE_KEY);
        }
        setHasOptionsMenu(true);
        initSetUp();
    }

    private void initSetUp()
    {
        File serviceFileName = PropertyUtils.getPropertyFile(getActivity(), CommonConstants.SERVICE_PROPERTY_TEMP_FILENAME);
        services = PropertyUtils.getServices(serviceFileName);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = (View) inflater.inflate(R.layout.songs_layout, container, false);
        setListView(view);
        setHeaderView();
        return view;
    }

    private void setListView(View view)
    {
        serviceListView = (ListView) view.findViewById(R.id.song_list_view);
        titleAdapter = new TitleAdapter<String>((AppCompatActivity) getActivity(), R.layout.songs_layout);
        titleAdapter.setTitleAdapterListener(this);
        titleAdapter.addObjects(services);
        serviceListView.setAdapter(titleAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        if (this.isAdded()) {
            outState.putParcelable(CommonConstants.STATE_KEY, serviceListView.onSaveInstanceState());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        initSetUp();
        if (state != null) {
            serviceListView.onRestoreInstanceState(state);
        } else {
            titleAdapter.addObjects(services);
        }
        addHeaderView();
    }

    //Adapter listener methods
    @Override
    public void setTitleTextView(TextView textView, final String text)
    {
        textView.setText(text);
        textView.setOnLongClickListener(new TextViewLongClickListener(text));
        textView.setOnClickListener(new TextViewOnClickListener(text));
    }

    @Override
    public void setPlayImageView(ImageView imageView, String text, int position)
    {
        imageView.setVisibility(View.GONE);
    }

    @Override
    public void setOptionsImageView(ImageView imageView, String text, int position)
    {
        imageView.setVisibility(View.GONE);
    }

    //Dialog Listener method
    @Override
    public void onClickPositiveButton(Bundle bundle, String tag)
    {
        File serviceFile = PropertyUtils.getPropertyFile(getActivity(), CommonConstants.SERVICE_PROPERTY_TEMP_FILENAME);
        PropertyUtils.removeProperty(bundle.getString(CommonConstants.NAME_KEY), serviceFile);
        services.clear();
        initSetUp();
        titleAdapter.addObjects(services);
        addHeaderView();
    }

    private void addHeaderView()
    {
        if (services.isEmpty()) {
            serviceListView.removeHeaderView(headerView);
            serviceListView.addHeaderView(headerView);
        }
    }

    private void setHeaderView()
    {
        headerView = LayoutInflater.from(getContext()).inflate(R.layout.service_header, null);
        TextView infoTextView = (TextView) headerView.findViewById(R.id.info_text_view);
        infoTextView.setText(R.string.favourite_info_message_);
        infoTextView.setLineSpacing(0, 1.2f);
    }

    @Override
    public void onClickNegativeButton()
    {
        // Do nothing
    }

    private class TextViewLongClickListener implements View.OnLongClickListener
    {
        private String serviceName;

        TextViewLongClickListener(String serviceName)
        {
            this.serviceName = serviceName;
        }

        @Override
        public boolean onLongClick(View v)
        {
            Bundle bundle = new Bundle();
            bundle.putString(CommonConstants.TITLE_KEY, getString(R.string.delete));
            bundle.putString(CommonConstants.MESSAGE_KEY, getString(R.string.message_delete_playlist, serviceName));
            bundle.putString(CommonConstants.NAME_KEY, serviceName);
            AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(bundle);
            alertDialogFragment.setDialogListener(ServicesFragment.this);
            alertDialogFragment.show(getActivity().getFragmentManager(), "DeleteConfirmationDialog");
            return false;
        }
    }

    private class TextViewOnClickListener implements View.OnClickListener
    {
        private String serviceName;

        TextViewOnClickListener(String serviceName)
        {
            this.serviceName = serviceName;
        }

        @Override
        public void onClick(View v)
        {
            Intent intent = new Intent(getActivity(), ServiceSongsActivity.class);
            intent.putExtra(CommonConstants.SERVICE_NAME_KEY, serviceName);
            startActivity(intent);
        }
    }
}
