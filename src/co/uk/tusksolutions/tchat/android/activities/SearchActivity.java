package co.uk.tusksolutions.tchat.android.activities;

import java.util.concurrent.CopyOnWriteArrayList;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
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
import co.uk.tusksolutions.tchat.android.models.RosterModel;

public class SearchActivity extends ActionBarActivity implements TextWatcher {

	public EditText searchView;
	public static ListView listView;
	public String TAG = "RosterFragment";
	private static RosterContentAdapter mAdapter;
	private static View mLodingStatusView;
	private static int shortAnimTime;
	private int SEARCH_ACTION = 3; // for search result
	private RosterModel mModel;
	private Button clear_text_search;
	public static CopyOnWriteArrayList<RosterModel> rosterModelCollection;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.search_messages_activity);

		mModel = new RosterModel();
		rosterModelCollection = new CopyOnWriteArrayList<RosterModel>();
		searchView = (EditText) findViewById(R.id.editTextSearch);
		searchView.addTextChangedListener(this);
		listView = (ListView) findViewById(R.id.list_view_search);
		clear_text_search = (Button) findViewById(R.id.clear_txt_search);
		clear_text_search.setVisibility(View.GONE);

		shortAnimTime = getResources().getInteger(
				android.R.integer.config_shortAnimTime);

		mLodingStatusView = (View) findViewById(R.id.roster_loading_view);

		listView.setVerticalScrollBarEnabled(false);
		listView.setHorizontalScrollBarEnabled(false);
		rosterModelCollection = mModel.queryAll();
		mAdapter = new RosterContentAdapter(TChatApplication.getContext(),
				SEARCH_ACTION);
		listView.setAdapter(mAdapter);
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
	 * private String getFirstImage(String htmlString) {
	 * 
	 * if (htmlString == null) return null;
	 * 
	 * String img = ""; Document doc = Jsoup.parse(htmlString); Elements imgs =
	 * doc.getElementsByTag("img");
	 * 
	 * for (Element imageElement : imgs) { if (imageElement != null) { // for
	 * each element get the srs url img = imageElement.attr("src").substring(4);
	 * return img; } }
	 * 
	 * return null; }
	 */

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
		/*
		 * if (s.length() > 0) { clear_text_search.setVisibility(View.VISIBLE);
		 * performSearch(s); } else {
		 * clear_text_search.setVisibility(View.GONE);
		 * listView.setVisibility(View.GONE); }
		 *
		Log.e("OntextChange", "Ontextchange called " + s);
		if (s.length() > 0) {
			mAdapter.getFilter().filter(s);
		} else {
			rosterModelCollection = mModel.queryAll();

			mAdapter = new RosterContentAdapter(TChatApplication.getContext(),
					SEARCH_ACTION);
			listView.setAdapter(mAdapter);
		}*/
		
		mAdapter.getFilter().filter(s);
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
