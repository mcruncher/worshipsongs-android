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
import org.worshipsongs.R;
import org.worshipsongs.activity.FavouriteSongsActivity;
import org.worshipsongs.adapter.TitleAdapter;
import org.worshipsongs.listener.SongContentViewListener;
import org.worshipsongs.registry.ITabFragment;
import org.worshipsongs.service.FavouriteService;
import org.worshipsongs.service.PopupMenuService;
import org.worshipsongs.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public class ServicesFragment extends Fragment implements TitleAdapter.TitleAdapterListener<String>, AlertDialogFragment.DialogListener, ITabFragment
{
    private FavouriteService favouriteService = new FavouriteService();
    private List<String> services = new ArrayList<>();
    private Parcelable state;
    private ListView serviceListView;
    private TitleAdapter<String> titleAdapter;
    private TextView infoTextView;
    private PopupMenuService popupMenuService = new PopupMenuService();

    public static ServicesFragment newInstance()
    {
        return new ServicesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        favouriteService = new FavouriteService();
        if (savedInstanceState != null) {
            state = savedInstanceState.getParcelable(CommonConstants.STATE_KEY);
        }
        setHasOptionsMenu(true);
        initSetUp();
    }

    private void initSetUp()
    {
        services = favouriteService.findNames();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.songs_layout, container, false);
        setInfoTextView(view);
        setListView(view);
        return view;
    }

    private void setInfoTextView(View view)
    {
        infoTextView = (TextView) view.findViewById(R.id.info_text_view);
        infoTextView.setText(getString(R.string.favourite_info_message_));
        infoTextView.setLineSpacing(0, 1.2f);
        infoTextView.setVisibility(services.isEmpty() ? View.VISIBLE : View.GONE);
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
        refreshListView();
    }

    //Adapter listener methods
    @Override
    public void setViews(Map<String, Object> objects, final String text)
    {
        TextView titleTextView = (TextView) objects.get(CommonConstants.TITLE_KEY);
        titleTextView.setText(text);
        titleTextView.setOnLongClickListener(new TextViewLongClickListener(text));
        titleTextView.setOnClickListener(new TextViewOnClickListener(text));

        ImageView optionsImageView = (ImageView) objects.get(CommonConstants.OPTIONS_IMAGE_KEY);
        optionsImageView.setVisibility(View.VISIBLE);
        optionsImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                popupMenuService.shareFavouritesInSocialMedia(view, text);
            }
        });
    }


    //Dialog Listener method
    @Override
    public void onClickPositiveButton(Bundle bundle, String tag)
    {
        String favouriteName = bundle.getString(CommonConstants.NAME_KEY, "");
        favouriteService.remove(favouriteName);
        services.clear();
        initSetUp();
        titleAdapter.addObjects(services);
        infoTextView.setVisibility(services.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClickNegativeButton()
    {
        // Do nothing
    }

    //Tab choices and reorder methods
    @Override
    public int defaultSortOrder()
    {
        return 4;
    }

    @Override
    public String getTitle()
    {
        return "playlists";
    }

    @Override
    public boolean checked()
    {
        return true;
    }

    @Override
    public void setListenerAndBundle(SongContentViewListener songContentViewListener, Bundle bundle)
    {

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
            Intent intent = new Intent(getActivity(), FavouriteSongsActivity.class);
            intent.putExtra(CommonConstants.SERVICE_NAME_KEY, serviceName);
            startActivity(intent);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            hideKeyboard();
            initSetUp();
            refreshListView();
        }
    }

    private void hideKeyboard()
    {
        if (getActivity() != null) {
            CommonUtils.hideKeyboard(getActivity());
        }
    }

    private void refreshListView()
    {
        if (state != null) {
            serviceListView.onRestoreInstanceState(state);
        } else if (titleAdapter != null) {
            titleAdapter.addObjects(services);
            infoTextView.setVisibility(services.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }
}
