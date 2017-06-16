package org.worshipsongs.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.CommonConstants;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.activity.CustomYoutubeBoxActivity;
import org.worshipsongs.activity.PresentSongActivity;
import org.worshipsongs.activity.SongContentViewActivity;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.domain.ServiceSong;
import org.worshipsongs.domain.Setting;
import org.worshipsongs.domain.Song;
import org.worshipsongs.dialog.ListDialogFragment;
import org.worshipsongs.service.CustomTagColorService;
import org.worshipsongs.service.UserPreferenceSettingService;
import org.worshipsongs.utils.PropertyUtils;
import org.worshipsongs.worship.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Author : Madasamy, Vignesh Palanisamy
 * Version : 2.x
 */

//TODO: Remove duplicate code
@Deprecated
public class ServiceSongAdapter extends ArrayAdapter<ServiceSong>
{

    private String serviceName;
    private AppCompatActivity activity;
    private ArrayList<String> songWithContent;
    private ArrayList<ServiceSong> serviceSongs;
    private UserPreferenceSettingService preferenceSettingService;
    private CustomTagColorService customTagColorService;

    public ServiceSongAdapter(AppCompatActivity aciivity, ArrayList<ServiceSong> songs, String serviceName)
    {
        super(aciivity.getApplicationContext(), R.layout.songs_listview_content, songs);
        this.activity = aciivity;
        this.serviceName = serviceName;
        serviceSongs = new ArrayList<ServiceSong>(songs);
        songWithContent = new ArrayList<>();
        for (ServiceSong serviceSong : songs) {
            if (serviceSong.getSong() != null) {
                songWithContent.add(serviceSong.getTitle());
            }
        }
        preferenceSettingService = new UserPreferenceSettingService();
        customTagColorService = new CustomTagColorService();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(R.layout.songs_listview_content, null);
        }
        ServiceSong serviceSong = getItem(position);
        if (serviceSong != null) {
            setTextView(view, serviceSong, position);
        }
        if (serviceSong.getSong() != null) {
            setPlayImageView(view, serviceSong, activity.getSupportFragmentManager());
            setImageView(view, serviceSong, activity.getSupportFragmentManager());
        }
        return view;
    }

    private void setTextView(View view, final ServiceSong serviceSong, final int position)
    {
        final TextView textView = (TextView) view.findViewById(R.id.tamil_title);
        textView.setText((preferenceSettingService.isTamil() && serviceSong.getSong().getTamilTitle().length() > 0) ?
                serviceSong.getSong().getTamilTitle() : serviceSong.getSong().getTitle());
        textView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                final Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(15);
                final AlertDialog alertDialog = getDeleteAlertDialogBuilder(serviceSong).create();
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener()
                {
                    @Override
                    public void onShow(DialogInterface dialog)
                    {
                        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                        negativeButton.setTextColor(activity.getResources().getColor(R.color.accent_material_light));
                    }
                });
                alertDialog.show();
                return false;
            }
        });
        Song presentingSong = Setting.getInstance().getSong();
        if (presentingSong != null && presentingSong.getTitle().equals(serviceSong)) {
            textView.setTextColor(getContext().getResources().getColor(R.color.light_navy_blue));
        } else {
            textView.setTextColor(Color.BLACK);
        }

        textView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (serviceSong.getSong() != null) {
                    Intent intent = new Intent(activity, SongContentViewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList(CommonConstants.TITLE_LIST_KEY, songWithContent);
                    bundle.putInt(CommonConstants.POSITION_KEY, position);
                    Setting.getInstance().setPosition(position);
                    intent.putExtras(bundle);
                    activity.startActivity(intent);
                } else {
                    final AlertDialog alertDialog = getAlertDialogBuilder(textView.getText().toString()).create();
                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener()
                    {
                        @Override
                        public void onShow(DialogInterface dialog)
                        {
                            Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                            negativeButton.setTextColor(activity.getResources().getColor(R.color.accent_material_light));
                        }
                    });
                    alertDialog.show();

                }
            }
        });

    }

    private AlertDialog.Builder getAlertDialogBuilder(final String title)
    {
        LayoutInflater li = LayoutInflater.from(activity);
        View promptsView = li.inflate(R.layout.delete_confirmation_dialog, null);
        TextView deleteMsg = (TextView) promptsView.findViewById(R.id.deleteMsg);
        deleteMsg.setText(String.format(getContext().getString(R.string.message_song_not_available), "\"" + title + "\""));
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.MyDialogTheme));
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(false).setNegativeButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
            }
        });
        return alertDialogBuilder;
    }

    private AlertDialog.Builder getDeleteAlertDialogBuilder(final ServiceSong serviceSong)
    {
        LayoutInflater li = LayoutInflater.from(activity);
        View promptsView = li.inflate(R.layout.delete_confirmation_dialog, null);
        TextView deleteMsg = (TextView) promptsView.findViewById(R.id.deleteMsg);
        deleteMsg.setText(R.string.message_delete_song);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.MyDialogTheme));
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(false).setPositiveButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                removeSong(serviceSong);

                Toast.makeText(activity, "Song " + serviceSong.getTitle() + " Deleted...!", Toast.LENGTH_LONG).show();
            }
        }).setNegativeButton(activity.getString(R.string.cancel), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
            }
        });
        return alertDialogBuilder;
    }

    private void removeSong(ServiceSong serviceSong)
    {
        try {
            String propertyValue = "";
            System.out.println("Preparing to remove service:" + serviceSong.getTitle());
            File serviceFile = PropertyUtils.getPropertyFile(activity, CommonConstants.SERVICE_PROPERTY_TEMP_FILENAME);
            String property = PropertyUtils.getProperty(serviceName, serviceFile);
            String propertyValues[] = property.split(";");
            System.out.println("File:" + serviceFile.getAbsolutePath());
            for (int i = 0; i < propertyValues.length; i++) {
                System.out.println("Property length: " + propertyValues.length);
                Log.i(this.getClass().getSimpleName(), "Property value  " + propertyValues[i]);
                if (StringUtils.isNotBlank(propertyValues[i]) && !propertyValues[i].equalsIgnoreCase(serviceSong.getTitle())) {
                    Log.i(this.getClass().getSimpleName(), "Append property value" + propertyValues[i]);
                    propertyValue = propertyValue + propertyValues[i] + ";";
                }
            }
            Log.i(this.getClass().getSimpleName(), "Property value after removed  " + propertyValue);
            PropertyUtils.setProperty(serviceName, propertyValue, serviceFile);
            serviceSongs.remove(serviceSong);
            remove(serviceSong);
            notifyDataSetChanged();
        } catch (Exception e) {
            Log.e(this.getClass().getName(), "Error occurred while parsing verse", e);
        }
    }

    private void setPlayImageView(final View rowView, ServiceSong serviceSong, FragmentManager fragmentManager)
    {
        ImageView imageView = (ImageView) rowView.findViewById(R.id.play_imageview);
        final String urlKey = serviceSong.getSong().getUrlKey();
        if (urlKey != null && urlKey.length() > 0 && preferenceSettingService.isPlayVideo()) {
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
        }
        imageView.setOnClickListener(onClickPopupListener(serviceSong, fragmentManager));
    }

    private void setImageView(View rowView, final ServiceSong serviceSong, final FragmentManager fragmentManager)
    {
        ImageView imageView = (ImageView) rowView.findViewById(R.id.optionMenuIcon);
        imageView.setOnClickListener(onClickPopupListener(serviceSong, fragmentManager));
    }

    private View.OnClickListener onClickPopupListener(final ServiceSong song, final FragmentManager fragmentManager)
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showPopupmenu(view, song, fragmentManager, true);
            }
        };
    }

    public void showPopupmenu(View view, final ServiceSong serviceSong, final FragmentManager fragmentManager, boolean hidePlay)
    {
        Context wrapper = new ContextThemeWrapper(WorshipSongApplication.getContext(), R.style.PopupMenu_Theme);
        final PopupMenu popupMenu;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            popupMenu = new PopupMenu(wrapper, view, Gravity.RIGHT);
        } else {
            popupMenu = new PopupMenu(wrapper, view);
        }
        popupMenu.getMenuInflater().inflate(R.menu.favourite_share_option_menu, popupMenu.getMenu());
        final Song song = serviceSong.getSong();
        final String urlKey = song.getUrlKey();
        MenuItem menuItem = popupMenu.getMenu().findItem(R.id.play_song);
        menuItem.setVisible(urlKey != null && urlKey.length() > 0 && preferenceSettingService.isPlayVideo() && hidePlay);
        MenuItem favouriteMenuItem = popupMenu.getMenu().findItem(R.id.addToList);
        favouriteMenuItem.setVisible(false);
        MenuItem presentSongItem = popupMenu.getMenu().findItem(R.id.present_song);
        presentSongItem.setVisible(false);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            public boolean onMenuItemClick(final MenuItem item)
            {
                switch (item.getItemId()) {
                    case R.id.addToList:
                        ListDialogFragment listDialogFragment = ListDialogFragment.newInstance(serviceSong.getTitle());
                        listDialogFragment.show(fragmentManager, "ListDialogFragment");
                        return true;
                    case R.id.share_whatsapp:
                        shareSongInSocialMedia(serviceSong.getTitle(), song);
                        return true;
                    case R.id.play_song:
                        showYouTube(urlKey, serviceSong.getTitle());
                        return true;
                    case R.id.present_song:
                        startPresentActivity(serviceSong.getTitle());
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    private void shareSongInSocialMedia(String songName, Song song)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(songName).append("\n").append("\n");
        for (String content : song.getContents()) {
            builder.append(customTagColorService.getFormattedLines(content));
            builder.append("\n");
        }
        builder.append(WorshipSongApplication.getContext().getString(R.string.share_info));
        Intent textShareIntent = new Intent(Intent.ACTION_SEND);
        textShareIntent.putExtra(Intent.EXTRA_TEXT, builder.toString());
        textShareIntent.setType("text/plain");
        Intent intent = Intent.createChooser(textShareIntent, "Share " + songName + " with...");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        WorshipSongApplication.getContext().startActivity(intent);
    }

    private void showYouTube(String urlKey, String songName)
    {
        Log.i(this.getClass().getSimpleName(), "Url key: " + urlKey);
        Intent youTubeIntent = new Intent(WorshipSongApplication.getContext(), CustomYoutubeBoxActivity.class);
        youTubeIntent.putExtra(CustomYoutubeBoxActivity.KEY_VIDEO_ID, urlKey);
        youTubeIntent.putExtra("title", songName);
        youTubeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        WorshipSongApplication.getContext().startActivity(youTubeIntent);
    }

    private void startPresentActivity(String title)
    {
        Intent intent = new Intent(WorshipSongApplication.getContext(), PresentSongActivity.class);
        intent.putExtra(CommonConstants.TITLE_KEY, title);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        WorshipSongApplication.getContext().startActivity(intent);
    }
}
