package org.worshipsongs.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.worshipsongs.activity.MainActivity;
import org.worshipsongs.service.IMobileNetworkService;
import org.worshipsongs.service.MobileNetworkService;
import org.worshipsongs.task.AsyncDownloadTask;
import org.worshipsongs.task.AsyncGitHubRepositoryTask;
import org.worshipsongs.worship.R;

import java.util.concurrent.ExecutionException;

/**
 * author:Madasamy
 * version:1.2.0
 */
public class CheckUpdateFragment extends Fragment
{
    private AsyncGitHubRepositoryTask asyncGitHubRepositoryTask;
    private IMobileNetworkService mobileNetworkService = new MobileNetworkService();
    private TextView textView;
    //private ProgressBar progressBar;
    private Button okButton;
    private Button cancelButton;
    private AlertDialog alertDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        FragmentActivity fragmentActivity = (FragmentActivity) super.getActivity();
        final LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.check_update_fragment, container, false);
        setHasOptionsMenu(true);

        textView = (TextView) linearLayout.findViewById(R.id.checkUpdateTextView);
        textView.setVisibility(View.INVISIBLE);

        okButton = (Button) linearLayout.findViewById(R.id.checkUpdateOk);
        okButton.setVisibility(View.INVISIBLE);
        cancelButton = (Button) linearLayout.findViewById(R.id.checkUpdateCancel);
        cancelButton.setVisibility(View.INVISIBLE);
        final Button checkUpdateButton = (Button) linearLayout.findViewById(R.id.checkUpdateButton);
        checkUpdateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                checkUpdateButton.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.VISIBLE);
                LayoutInflater li = LayoutInflater.from(getActivity());
                View promptsView = li.inflate(R.layout.add_service_dialog, null);

                try {
                    asyncGitHubRepositoryTask = new AsyncGitHubRepositoryTask(getActivity().getApplicationContext());
                    final AsyncDownloadTask asyncDownloadTask = new AsyncDownloadTask(getActivity().getApplicationContext());
                    if (mobileNetworkService.isMobileData(getActivity().getSystemService(Context.CONNECTIVITY_SERVICE)) ||
                            mobileNetworkService.isWifi(getActivity().getSystemService(Context.CONNECTIVITY_SERVICE))) {
                        asyncGitHubRepositoryTask.execute();
                        if (true) {
                            // progressBar.setVisibility(View.VISIBLE);
                            textView.setText(R.string.updateAvailableTitle);
                            textView.setVisibility(View.VISIBLE);
                            okButton.setVisibility(View.VISIBLE);
                            okButton.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    asyncDownloadTask.execute();
                                }
                            });
                            cancelButton.setVisibility(View.VISIBLE);
                            cancelButton.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    startActivity(new Intent(getActivity().getApplication(), MainActivity.class));
                                }
                            });
                            alertDialog.dismiss();

                            //Update is available
                        } else {
                            //Update is not available.
                            textView.setText(R.string.updateNotAvailableTitle);
                            textView.setVisibility(View.VISIBLE);
                            okButton.setVisibility(View.INVISIBLE);
                            cancelButton.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        //wifi or mobile data available;
                        textView.setText(R.string.noInternetConnectionWarningMessage);
                        textView.setVisibility(View.VISIBLE);
                        okButton.setVisibility(View.INVISIBLE);
                        cancelButton.setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e) {
                    Log.e(CheckUpdateFragment.class.getSimpleName(), "Error occurred " + e);
                }
            }
        });
        return linearLayout;
    }
}
