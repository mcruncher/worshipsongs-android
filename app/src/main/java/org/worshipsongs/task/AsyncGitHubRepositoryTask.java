package org.worshipsongs.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.CommitService;
import org.worshipsongs.CommonConstants;
import org.worshipsongs.utils.PropertyUtils;

import java.io.File;
import java.util.Collection;

/**
 * @Author : Madasamy
 * @Version : 2.0
 */
public class AsyncGitHubRepositoryTask extends AsyncTask<String, Void, Boolean>
{
    public static final String LATEST_CHANGE_SET = "latestChangeSet";
    private final Context context;

    public AsyncGitHubRepositoryTask(Context context)
    {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(String... params)
    {
        try {
            File commonPropertyFile = PropertyUtils.getPropertyFile(context, CommonConstants.COMMON_PROPERTY_TEMP_FILENAME);
            String latestChangesetInPropertyFile = PropertyUtils.getProperty(LATEST_CHANGE_SET, commonPropertyFile);
            Log.i(this.getClass().getSimpleName(), "Latest changeset in property file: " + latestChangesetInPropertyFile);
            final RepositoryId repo = new RepositoryId("crunchersaspire", "worshipsongs-db");

            final CommitService commitService = new CommitService();
            PageIterator<RepositoryCommit> repositoryCommits = commitService.pageCommits(repo, 1);
            Collection<RepositoryCommit> repositoryCommitCollection = repositoryCommits.iterator().next();
            RepositoryCommit repositoryCommit = repositoryCommitCollection.iterator().next();

            String latestChangeSet = repositoryCommit.getSha();
            Log.i(this.getClass().getSimpleName(), "Latest changeset in repository: " + latestChangeSet);
            if (latestChangesetInPropertyFile == null || !(latestChangesetInPropertyFile.equalsIgnoreCase(latestChangeSet))) {
                PropertyUtils.setProperty(LATEST_CHANGE_SET, latestChangeSet, commonPropertyFile);
                Log.i(this.getClass().getSimpleName(), "Changeset are different");
                return true;
            } else {
                Log.i(this.getClass().getSimpleName(), "Changeset are same");
                return false;
            }
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "Error occurred while checking new changeset" + e);
            return false;
        }
    }
}
