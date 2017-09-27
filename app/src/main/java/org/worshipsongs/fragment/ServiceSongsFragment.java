package org.worshipsongs.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.R;
import org.worshipsongs.activity.SongContentViewActivity;
import org.worshipsongs.adapter.TitleAdapter;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.domain.ServiceSong;
import org.worshipsongs.domain.Setting;
import org.worshipsongs.domain.Song;
import org.worshipsongs.listener.SongContentViewListener;
import org.worshipsongs.service.ISongService;
import org.worshipsongs.service.PopupMenuService;
import org.worshipsongs.service.SongService;
import org.worshipsongs.service.UserPreferenceSettingService;
import org.worshipsongs.utils.CommonUtils;
import org.worshipsongs.utils.PropertyUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Author : Madasamy
 * Version : 4.x
 */

public class ServiceSongsFragment extends Fragment implements TitleAdapter.TitleAdapterListener<ServiceSong>, AlertDialogFragment.DialogListener
{
    private static final String CLASS_NAME = ServiceSongsFragment.class.getSimpleName();
    private ListView songListView;
    private TitleAdapter<ServiceSong> titleAdapter;
    private String serviceName;
    private SongDao songDao;
    private ISongService songService;
    private ArrayList<ServiceSong> serviceSongs;
    private ArrayList<String> titles = new ArrayList<>();
    private UserPreferenceSettingService preferenceSettingService = new UserPreferenceSettingService();
    private SongContentViewListener songContentViewListener;
    private PopupMenuService popupMenuService = new PopupMenuService();

    public static ServiceSongsFragment newInstance(Bundle bundle)
    {
        ServiceSongsFragment serviceSongsFragment = new ServiceSongsFragment();
        serviceSongsFragment.setArguments(bundle);
        return serviceSongsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        serviceName = getArguments().getString(CommonConstants.SERVICE_NAME_KEY);
        songService = new SongService(getActivity());
        setHasOptionsMenu(true);
        loadSongs();
    }

    private void loadSongs()
    {
        Log.i(CLASS_NAME, "Preparing to find songs");
        File serviceFile = PropertyUtils.getPropertyFile(getActivity(), CommonConstants.SERVICE_PROPERTY_TEMP_FILENAME);
        String property = PropertyUtils.getProperty(serviceName, serviceFile);
        String propertyValues[] = property.split(";");
        songDao = new SongDao(getActivity());
        serviceSongs = new ArrayList<ServiceSong>();
        for (String title : propertyValues) {
            Song song = songDao.findContentsByTitle(title);
            serviceSongs.add(new ServiceSong(title, song));
            titles.add(title);
        }
        Log.i(CLASS_NAME, "No of songs " + serviceSongs);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.songs_layout, container, false);
        setListView(view);
        return view;
    }

    private void setListView(View view)
    {
        songListView = (ListView) view.findViewById(R.id.song_list_view);
        titleAdapter = new TitleAdapter<ServiceSong>((AppCompatActivity) getActivity(), R.layout.songs_layout);
        titleAdapter.setTitleAdapterListener(this);
        titleAdapter.addObjects(serviceSongs);
        songListView.setAdapter(titleAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.action_bar_menu, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(true);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint(getString(R.string.action_search));
        ImageView image = (ImageView) searchView.findViewById(R.id.search_close_btn);
        Drawable drawable = image.getDrawable();
        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextChange(String newText)
            {
                titleAdapter.addObjects(songService.filteredServiceSongs(newText, serviceSongs));
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query)
            {
                titleAdapter.addObjects(songService.filteredServiceSongs(query, serviceSongs));
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);
        menu.getItem(0).setVisible(false);
    }

    //Adapter listener methods
    @Override
    public void setTitleTextView(TextView titleTextView, TextView subTitleTextView, ServiceSong serviceSong)
    {
        titleTextView.setText(songService.getTitle(preferenceSettingService.isTamil(), serviceSong));
        titleTextView.setOnClickListener(new SongOnClickListener(serviceSong));
        titleTextView.setOnLongClickListener(new SongOnLongClickListener(serviceSong));
    }

    @Override
    public void setPlayImageView(ImageView imageView, ServiceSong serviceSong, int position)
    {
        imageView.setVisibility(isShowPlayIcon(serviceSong.getSong()) ? View.VISIBLE : View.GONE);
        imageView.setOnClickListener(imageOnClickListener(serviceSong.getSong(), serviceSong.getTitle()));
    }

    boolean isShowPlayIcon(Song song)
    {
        try {
            String urlKey = song.getUrlKey();
            return urlKey != null && urlKey.length() > 0 && preferenceSettingService.isPlayVideo();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void setOptionsImageView(ImageView imageView, ServiceSong serviceSong, int position)
    {
        imageView.setOnClickListener(imageOnClickListener(serviceSong.getSong(), serviceSong.getTitle()));
    }

    private View.OnClickListener imageOnClickListener(final Song song, final String title)
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (song != null) {
                    popupMenuService.showPopupmenu((AppCompatActivity) getActivity(), view, song.getTitle(), true);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString(CommonConstants.TITLE_KEY, getString(R.string.warning));
                    bundle.putString(CommonConstants.MESSAGE_KEY, getString(R.string.message_song_not_available, "\"" + title + "\""));
                    AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(bundle);
                    alertDialogFragment.setVisibleNegativeButton(false);
                    alertDialogFragment.show(getActivity().getFragmentManager(), "WarningDialogFragment");
                }
            }
        };
    }

    //Dialog listener methods
    @Override
    public void onClickPositiveButton(Bundle bundle, String tag)
    {
        if ("DeleteDialogFragment".equalsIgnoreCase(tag)) {
            removeSong(bundle.getString(CommonConstants.NAME_KEY));
        }
    }

    private void removeSong(String serviceSong)
    {
        try {
            File serviceFile = PropertyUtils.getPropertyFile(getActivity(), CommonConstants.SERVICE_PROPERTY_TEMP_FILENAME);
            PropertyUtils.removeSong(serviceFile, serviceName, serviceSong);
            serviceSongs.remove(getSongToBeRemoved(serviceSong, serviceSongs));
            titleAdapter.addObjects(serviceSongs);
        } catch (Exception e) {
            Log.e(this.getClass().getName(), "Error occurred while removing song", e);
        }
    }

    ServiceSong getSongToBeRemoved(String title, List<ServiceSong> serviceSongs)
    {
        for (ServiceSong serviceSong : serviceSongs) {
            if (serviceSong.getTitle().equalsIgnoreCase(title)) {
                return serviceSong;
            }
        }
        return null;
    }

    @Override
    public void onClickNegativeButton()
    {
        //Do nothing
    }

    private class SongOnClickListener implements View.OnClickListener
    {
        private ServiceSong serviceSong;

        SongOnClickListener(ServiceSong serviceSong)
        {
            this.serviceSong = serviceSong;
        }

        @Override
        public void onClick(View view)
        {
            if (serviceSong.getSong() != null) {
                if (CommonUtils.isPhone(getContext())) {
                    Intent intent = new Intent(getActivity(), SongContentViewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList(CommonConstants.TITLE_LIST_KEY, titles);
                    bundle.putInt(CommonConstants.POSITION_KEY, titleAdapter.getPosition(serviceSong));
                    Setting.getInstance().setPosition(titleAdapter.getPosition(serviceSong));
                    intent.putExtras(bundle);
                    getActivity().startActivity(intent);
                } else {
                    Setting.getInstance().setPosition(titleAdapter.getPosition(serviceSong));
                    songContentViewListener.displayContent(serviceSong.getTitle(), titles, titleAdapter.getPosition(serviceSong));
                }
            } else {
                Bundle bundle = new Bundle();
                bundle.putString(CommonConstants.TITLE_KEY, getString(R.string.warning));
                bundle.putString(CommonConstants.MESSAGE_KEY, getString(R.string.message_song_not_available, "\"" + serviceSong.getTitle() + "\""));
                AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(bundle);
                alertDialogFragment.setVisibleNegativeButton(false);
                alertDialogFragment.show(getActivity().getFragmentManager(), "WarningDialogFragment");
            }
        }
    }

    private class SongOnLongClickListener implements View.OnLongClickListener
    {
        private ServiceSong serviceSong;

        SongOnLongClickListener(ServiceSong serviceSong)
        {
            this.serviceSong = serviceSong;
        }

        @Override
        public boolean onLongClick(View view)
        {
            Bundle bundle = new Bundle();
            bundle.putString(CommonConstants.TITLE_KEY, getString(R.string.delete));
            bundle.putString(CommonConstants.MESSAGE_KEY, getString(R.string.message_delete_song));
            bundle.putString(CommonConstants.NAME_KEY, serviceSong.getTitle());
            AlertDialogFragment deleteAlertDialogFragment = AlertDialogFragment.newInstance(bundle);
            deleteAlertDialogFragment.setDialogListener(ServiceSongsFragment.this);
            deleteAlertDialogFragment.show(getActivity().getFragmentManager(), "DeleteDialogFragment");
            return true;
        }
    }

    public void setSongContentViewListener(SongContentViewListener songContentViewListener)
    {
        this.songContentViewListener = songContentViewListener;
    }
}
