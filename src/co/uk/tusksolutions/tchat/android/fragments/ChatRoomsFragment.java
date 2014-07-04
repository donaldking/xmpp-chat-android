package co.uk.tusksolutions.tchat.android.fragments;

import co.uk.tusksolutions.tchat.android.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ChatRoomsFragment extends Fragment {
	
	private View rootView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/**
		 * Load ChatRooms fromUser DB
		 */
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 rootView = (View) inflater.inflate(R.layout.fragment_chatrooms,
				container, false);

		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	 
	}
}
