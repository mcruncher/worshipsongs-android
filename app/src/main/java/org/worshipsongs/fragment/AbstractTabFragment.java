package org.worshipsongs.fragment;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.R;

/**
 * Author : Madasamy
 * Version : 3.2.x
 */
public class AbstractTabFragment extends Fragment
{
    protected void setCountView(TextView countTextView, String count)
    {
        countTextView.setText(getString(R.string.no_of_songs, count));
        countTextView.setVisibility(StringUtils.isNotBlank(countTextView.getText()) ? View.VISIBLE : View.GONE);
    }
}
