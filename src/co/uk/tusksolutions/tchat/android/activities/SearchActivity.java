package co.uk.tusksolutions.tchat.android.activities;

import java.util.ArrayList;

import org.w3c.dom.ls.LSInput;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.adapters.RosterContentAdapter;
import co.uk.tusksolutions.tchat.android.constants.Constants;
import co.uk.tusksolutions.tchat.android.models.RosterModel;

public class SearchActivity extends ActionBarActivity implements TextWatcher {

	public EditText searchView;
	public static ListView listView;
	public String TAG = "RosterFragment";
	private View rootView;
	private static RosterContentAdapter mAdapter;
	private IntentFilter filter;
	private RosterReceiver mRosterReceiver;
	private static View mLodingStatusView;
	private static int shortAnimTime;
	private static int ALL_QUERY_ACTION = 1; // See adapter for notes
	private int ONLINE_QUERY_ACTION = 2; // See adapter for notes

	private int SEARCH_ACTION = 3; // for search result

	private Bundle instanceState;

	private RosterModel mModel;
	private int action;
	private Button clear_text_search;
	public static ArrayList<RosterModel> rosterModelCollection;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.search_messages_activity);

		mModel = new RosterModel();
		rosterModelCollection = new ArrayList<RosterModel>();
		searchView = (EditText) findViewById(R.id.editTextSearch);
		searchView.addTextChangedListener(this);
		listView = (ListView) findViewById(R.id.list_view_search);
		clear_text_search = (Button) findViewById(R.id.clear_txt_search);
		clear_text_search.setVisibility(View.GONE);
		instanceState = savedInstanceState;
		if (TChatApplication.CHAT_SECTION_QUERY_ACTION == 0) {
			TChatApplication.CHAT_SECTION_QUERY_ACTION = ALL_QUERY_ACTION;
		}

		shortAnimTime = getResources().getInteger(
				android.R.integer.config_shortAnimTime);

		mLodingStatusView = (View) findViewById(R.id.roster_loading_view);

		listView.setVerticalScrollBarEnabled(false);
		listView.setHorizontalScrollBarEnabled(false);
		
		if (clear_text_search.getVisibility() != View.VISIBLE) {
			clear_text_search.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
                    clear_text_search.setVisibility(View.GONE);
					searchView.setText("");
					
				}
			});
		}
		
	}

	/*
	 * Broad cast fromUser that gets called When we receive new data form cloud
	 * TO_USER db
	 */
	private class RosterReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			/*
			 * Load friends fromUser db and reload the list view
			 */
			if (intent.getAction().equalsIgnoreCase(Constants.ROSTER_UPDATED)) {

				if (intent.getExtras() != null) {
					// int inserts = intent.getExtras().getInt("inserts");
					showProgress(true);
					if (TChatApplication.CHAT_SECTION_QUERY_ACTION == ALL_QUERY_ACTION) {
						prepareListView(ALL_QUERY_ACTION);
					}
				} else if (TChatApplication.CHAT_SECTION_QUERY_ACTION == ONLINE_QUERY_ACTION) {
					prepareListView(ONLINE_QUERY_ACTION);
				}

			} else if (intent.getAction().equalsIgnoreCase(
					Constants.ROSTER_EMPTY)) {
				showProgress(false);
				Log.i(TAG, "Online Roster Empty!");
			}
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private static void showProgress(final boolean show) {

		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs TO_USER
		// fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {

			mLodingStatusView.setVisibility(View.VISIBLE);
			mLodingStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLodingStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.*/
			mLodingStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
		}
	}

	private static void prepareListView(final int queryInt) {

		/**
		 * Load Friends fromUser DB
		 */

		TChatApplication.CHAT_SECTION_QUERY_ACTION = queryInt;
		mAdapter = new RosterContentAdapter(TChatApplication.getContext(),
				queryInt);

		if (mAdapter.getCount() == 0) {
			if (TChatApplication.CHAT_SECTION_QUERY_ACTION == 2) {
				showProgress(false);
			} else {
				showProgress(true);
			}
			listView.setVisibility(View.GONE);
		} else {
			// showProgress(false);
			listView.setAdapter(mAdapter);
			if (listView.getVisibility() != View.VISIBLE) {
				listView.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt("currentQueryAction",
				TChatApplication.CHAT_SECTION_QUERY_ACTION);
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		if (s.length() > 0) {
			clear_text_search.setVisibility(View.VISIBLE);
			performSearch(s);
		} else {
			clear_text_search.setVisibility(View.GONE);
			listView.setVisibility(View.GONE);
		}
	}

	public void performSearch(CharSequence s) {
		rosterModelCollection = mModel.querySearch(s.toString());
		mAdapter = new RosterContentAdapter(TChatApplication.getContext(),
				SEARCH_ACTION);
		Log.d("TCHAT", "result Size " + mAdapter.getCount());
		if (mAdapter.getCount() == 0) {
			if (TChatApplication.CHAT_SECTION_QUERY_ACTION == 2) {
				showProgress(false);
			} else {
				showProgress(true);
			}
			Toast.makeText(SearchActivity.this, "No User Found",
					Toast.LENGTH_SHORT).show();
			listView.setVisibility(View.GONE);
		} else {
			showProgress(false);
			listView.setAdapter(mAdapter);
			if (listView.getVisibility() != View.VISIBLE) {
				listView.setVisibility(View.VISIBLE);
			}
		}
	}
}
