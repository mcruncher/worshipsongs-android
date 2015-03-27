package org.worshipsongs.page.component.fragment;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import org.worshipsongs.activity.SongListActivity;
import org.worshipsongs.dao.AuthorDao;
import org.worshipsongs.dao.AuthorSongDao;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.domain.Author;
import org.worshipsongs.domain.AuthorSong;
import org.worshipsongs.domain.Song;
import org.worshipsongs.domain.Verse;
import org.worshipsongs.parser.VerseParser;
import org.worshipsongs.worship.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seenivasan on 3/24/2015.
 */
public class AuthorListFragment extends Fragment {

}
