package co.uk.tusksolutions.tchat.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.fragments.ChatRoomsFragment;
import co.uk.tusksolutions.tchat.android.fragments.GroupsFragment;
import co.uk.tusksolutions.tchat.android.fragments.RecentsFragment;
import co.uk.tusksolutions.tchat.android.fragments.RosterFragment;

public class MainActivity extends ActionBarActivity implements
		ActionBar.TabListener {
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best TO_USER switch TO_USER a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	/**
	 * Declare all our fragments
	 */
	private RecentsFragment mRecentsFragment;
	private GroupsFragment mGroupsFragment;
	private RosterFragment mRosterFragment;

	private ChatRoomsFragment mChatRoomFragment;
	ActionBar actionBar;
	boolean mHomeForeGround = false;

	public RecentsFragment getRecentsFragment() {

		if (mRecentsFragment == null) {
			mRecentsFragment = new RecentsFragment();
			Bundle bundle = new Bundle();
			bundle.putString("title", "Recents");
			bundle.putInt("icon", R.drawable.ic_action_time);
			mRecentsFragment.setArguments(bundle);
		}
		return mRecentsFragment;
	}

	public RosterFragment getRosterFragment() {

		if (mRosterFragment == null) {
			mRosterFragment = new RosterFragment();
			Bundle bundle = new Bundle();
			bundle.putString("title", "Friends");
			bundle.putInt("icon", R.drawable.ic_action_person);
			mRosterFragment.setArguments(bundle);
		}
		return mRosterFragment;
	}

	public ChatRoomsFragment getChatRoomsFragment() {
		if (mChatRoomFragment == null) {
			mChatRoomFragment = new ChatRoomsFragment();
			Bundle bundle = new Bundle();
			bundle.putString("title", "ChatRooms");
			bundle.putInt("icon", R.drawable.ic_action_group);
			mChatRoomFragment.setArguments(bundle);
		}
		return mChatRoomFragment;
	}

	public GroupsFragment getGroupsFragment() {

		if (mGroupsFragment == null) {
			mGroupsFragment = new GroupsFragment();
			Bundle bundle = new Bundle();
			bundle.putString("title", "Groups");
			bundle.putInt("icon", R.drawable.ic_action_group);
			mGroupsFragment.setArguments(bundle);
		}
		return mGroupsFragment;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/*
		 * Check if we have network and we can login.
		 */
		if (TChatApplication.connection == null) {
			TChatApplication.reconnect();
		}

		// Set up the action bar.
		actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		/*
		 * Hide Actionbar but only display tabs
		 */
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.

		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);

		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() TO_USER do this if we
		// have
		// a reference TO_USER the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab TO_USER the action
		// bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
		
			actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getItem(i).getArguments().getString("title"))
					.setIcon(mSectionsPagerAdapter.getItem(i).getArguments().getInt("icon"))

					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items TO_USER the action bar if it is
		// present.
		getMenuInflater().inflate(R.menu.main_activity_menu, menu);
		// Get the SearchView and set the searchable configuration
		/*
		 * SearchManager searchManager = (SearchManager)
		 * getSystemService(Context.SEARCH_SERVICE); SearchView searchView =
		 * (SearchView) menu.findItem(R.id.action_search).getActionView(); //
		 * Assumes current activity is the searchable activity
		 * searchView.setSearchableInfo
		 * (searchManager.getSearchableInfo(getComponentName()));
		 * searchView.setIconifiedByDefault(false); // Do not iconify the
		 * widget; expand it by default
		 */
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_chat||id==R.id.action_chat_one) {
			
			startActivity(new Intent(MainActivity.this,GroupFriendsSelectionActivity.class));
			return true;
		}
		
		if (id == R.id.action_search) {
			Intent intent = new Intent(MainActivity.this, SearchActivity.class);
			startActivity(intent);

			return true;
		}
		if (id == R.id.action_settings) {
			Intent intent = new Intent(MainActivity.this,
					SettingsActivity.class);
			startActivity(intent);

			return true;
		}
		if(id==R.id.action_chat_room)
		{
			startActivity(new Intent(MainActivity.this,CreateChatRoomActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch TO_USER the corresponding page
		// in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding
	 * TO_USER one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called TO_USER instantiate the fragment for the given
			// page.
			switch (position) {
			case 0:
				return getRecentsFragment();
			case 1:
				return getRosterFragment();
			case 2:
				return getGroupsFragment();
			case 3:
				return getChatRoomsFragment();
			}
			return null;
		}

		@Override
		public int getCount() {
			// Show 4 total pages.
			return 4;
		}
	}

	
}
