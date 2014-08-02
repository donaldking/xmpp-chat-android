package co.uk.tusksolutions.tchat.android.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import co.uk.tusksolutions.tchat.android.R;

public class CreateChatRoomActivity extends ActionBarActivity {

	

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_chatroom);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		getSupportActionBar().setTitle("Schedule Chatroom");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	/*	MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.chat_activity_menu, menu);
	*/	return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
	
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return true;
	}
}
