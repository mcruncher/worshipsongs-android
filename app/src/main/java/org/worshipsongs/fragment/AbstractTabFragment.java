package org.worshipsongs.fragment;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.CommonConstants;

/**
 * Author : Madasamy
 * Version : 3.2.x
 */
public class AbstractTabFragment extends Fragment
{
    protected void setCountView(TextView countTextView, String count)
    {
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) countTextView.getLayoutParams();
        if (count.toCharArray().length == 3) {
            params.width = 130;
            countTextView.setLayoutParams(params);
        } else if (count.toCharArray().length == 2) {
            params.width = 100;
            countTextView.setLayoutParams(params);
        } else if (count.toCharArray().length == 1) {
            params.width = 70;
            countTextView.setLayoutParams(params);
        } else {
            params.width = 200;
            countTextView.setLayoutParams(params);
        }
        countTextView.setText(count);
        countTextView.setVisibility(StringUtils.isNotBlank(countTextView.getText()) ? View.VISIBLE : View.GONE);
    }
}
