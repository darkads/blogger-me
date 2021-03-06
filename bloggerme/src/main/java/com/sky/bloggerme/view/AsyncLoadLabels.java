package com.sky.bloggerme.view;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.services.blogger.model.Post;
import com.google.api.services.blogger.model.PostList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Class AsyncLoadLabels.
 */
public class AsyncLoadLabels extends AsyncTask<Post, Void, List<String>>
{
	
	/** The Constant TAG. */
	private static final String TAG = "AsyncLoadLabels";

	/** The editor activity. */
	private final SettingsActivity settingsActivity;
	
	/** The dialog. */
//	private final ProgressDialog dialog;
	
	/** The service. */
	private com.google.api.services.blogger.Blogger service;

	/**
	 * Instantiates a new async load labels.
	 *
	 * @param settingsActivity the settings activity
	 */
	AsyncLoadLabels(SettingsActivity settingsActivity)
	{
		Log.v(TAG, "start of LoadLabels async task");
		this.settingsActivity = settingsActivity;
		service = settingsActivity.service;
//		dialog = new ProgressDialog(editorActivity);
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute()
	{
		Log.v(TAG, "Popping up waiting dialog");
//		dialog.setMessage("Getting Label...");
//		dialog.show();
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected List<String> doInBackground(Post... params)
	{
		try
		{
			List<String> result = new ArrayList<String>();
			com.google.api.services.blogger.Blogger.Posts.List postsListAction = service.posts().list(settingsActivity.getBlogID()).setFields("items/labels,nextPageToken");
			PostList posts = postsListAction.execute();

			boolean label_exist = false;

			// Set the total number of labels
			int totallabels = 0;

			while (posts.getItems() != null && !posts.getItems().isEmpty())
			{
				// Iterate through the all the posts
				for (int i = 0; i < posts.getItems().size(); i++)
				{
					// Iterate through the list of labels of each indivdual post
					for (String label : posts.getItems().get(i).getLabels())
					{
						if (totallabels == 0)
						{
							result.add(label);
							totallabels++;
						}
						else
						{
							// Compare with each label in the result list by iterating through the result list
							for (int j = 0; j < totallabels; j++)
							{
								if (label.equals(result.get(j)))
								{
									label_exist = true;
									break;
								}
								label_exist = false;
							}
							if (!label_exist)
							{
								result.add(label);
								totallabels++;
							}
						}
					}
				}
				// Pagination logic
				String pageToken = posts.getNextPageToken();
				if (pageToken == null)
				{
					break;
				}
				postsListAction.setPageToken(pageToken);
				posts = postsListAction.execute();
			}
			return result;
		}
		catch (IOException e)
		{
			Log.e(TAG, e.getMessage());
			settingsActivity.handleGoogleException(e);
			return Collections.emptyList();
		}
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(List<String> result)
	{
		if (!result.isEmpty())
		{
			Log.v(TAG, "Async complete, pulling down dialog");
		}
		else
		{
//			createAlertDialog("Error", "Unable to retrieve labels. Trying again...");
			Log.v(TAG, "Async complete, pulling down dialog");
		}
//		dialog.dismiss();
		settingsActivity.setModel(result);
	}

	// private void createAlertDialog(String title, String message)
	// {
	// final AlertDialog alertDialog = new AlertDialog.Builder(editorActivity).create();
	// alertDialog.setTitle(title);
	// alertDialog.setMessage(message);
	// alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new Dialog.OnClickListener()
	// {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which)
	// {
	// alertDialog.dismiss();
	// editorActivity.onRequestCompleted("Error");
	// }
	// });
	// alertDialog.show();
	//
	// }
}
