package co.uk.tusksolutions.tchat.android.activities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import co.uk.tusksolutions.tchat.android.R;
import co.uk.tusksolutions.tchat.android.TChatApplication;
import co.uk.tusksolutions.tchat.android.api.APICreateChatrooms;
import co.uk.tusksolutions.tchat.android.models.ChatRoomsModel;
import co.uk.tusksolutions.tchat.android.tasks.CreateChatroomAsyncTask;
import co.uk.tusksolutions.tchat.android.tasks.CreateChatroomAsyncTask.OnCreateChatroomListener;

public class CreateChatRoomActivity extends ActionBarActivity implements
		OnCreateChatroomListener {

	private EditText chatroomName;
	private EditText inputDate;
	private EditText inputStartTime;
	private EditText inputEndTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_chatroom);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		getSupportActionBar().setTitle("Schedule Chatroom");

		chatroomName = (EditText) findViewById(R.id.chatroom_name);
		inputDate = (EditText) findViewById(R.id.start_date);
		inputStartTime = (EditText) findViewById(R.id.start_time);
		inputEndTime = (EditText) findViewById(R.id.end_time);
		inputDate.setKeyListener(null);
		inputStartTime.setKeyListener(null);
		inputEndTime.setKeyListener(null);
		inputDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showStartDateDialog(v);
			}
		});
		inputStartTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				hideKeyboard();
				showStartTimeDialog(v);
			}
		});
		inputEndTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				hideKeyboard();
				showEndTimeDialog(v);
			}
		});
	}

	public void showStartDateDialog(View v) {
		FragmentManager fm = getSupportFragmentManager();
		DialogFragment dialogFragment = new StartDatePicker();
		dialogFragment.show(fm, "start_date_picker");
	}

	public void showStartTimeDialog(View v) {
		FragmentManager fm = getSupportFragmentManager();
		DialogFragment dialogFragment = new StartTimePicker();
		dialogFragment.show(fm, "start_time_picker");
	}

	public void showEndTimeDialog(View v) {
		FragmentManager fm = getSupportFragmentManager();
		DialogFragment dialogFragment = new EndTimePicker();
		dialogFragment.show(fm, "end_time_picker");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.chatroom_create_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.submit_done:
			if ((chatroomName.getText().toString() != null)
					&& (!chatroomName.getText().toString().equalsIgnoreCase(""))) {
				String chatroom = chatroomName.getText().toString();

				new CreateChatroomAsyncTask(this, chatroom, this).execute();
			}
			break;
		}
		return true;
	}

	Calendar c = Calendar.getInstance();
	int startYear = c.get(Calendar.YEAR);
	int startMonth = c.get(Calendar.MONTH);
	int startDay = c.get(Calendar.DAY_OF_MONTH);
	int hour = c.get(Calendar.HOUR_OF_DAY);
	int minutes = c.get(Calendar.MINUTE);
	int endhours = c.get(Calendar.HOUR_OF_DAY);
	int endminutes = c.get(Calendar.MINUTE);

	class StartDatePicker extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			// Use the current date as the default date in the picker
			DatePickerDialog dialog = new DatePickerDialog(
					CreateChatRoomActivity.this, this, startYear, startMonth,
					startDay);
			return dialog;

		}

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			// Do something with the date chosen by the user
			startYear = year;
			startMonth = monthOfYear;
			startDay = dayOfMonth;
			updateStartDateDisplay();

		}
	}

	class StartTimePicker extends DialogFragment implements
			TimePickerDialog.OnTimeSetListener {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			// Use the current date as the default date in the picker

			TimePickerDialog dialog = new TimePickerDialog(
					CreateChatRoomActivity.this, this, hour, minutes, false);

			return dialog;

		}

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub
			hour = hourOfDay;
			minutes = minute;
			updateStartTimeDisplay();
		}

	}

	class EndTimePicker extends DialogFragment implements
			TimePickerDialog.OnTimeSetListener {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			// Use the current date as the default date in the picker

			TimePickerDialog dialog = new TimePickerDialog(
					CreateChatRoomActivity.this, this, endhours, endminutes,
					false);

			return dialog;

		}

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub
			endhours = hourOfDay;
			endminutes = minute;
			updateEndTimeDisplay();
		}

	}

	public void updateStartDateDisplay() {
		// TODO Auto-generated method stub
		Calendar c = Calendar.getInstance();
		c.set(startYear, startMonth, startDay);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String formattedDate = sdf.format(c.getTime());
		inputDate.setText(formattedDate);

	}

	public void updateStartTimeDisplay() {
		// TODO Auto-generated method stub

		SimpleDateFormat sdf = new SimpleDateFormat("hh:ss");
		Date date = null;
		try {
			date = sdf.parse(hour + ":" + minutes);
		} catch (ParseException e) {
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);

		String formattedTate = sdf.format(c.getTime());
		inputStartTime.setText(formattedTate);

	}

	public void updateEndTimeDisplay() {
		// TODO Auto-generated method stub

		SimpleDateFormat sdf = new SimpleDateFormat("hh:ss");
		Date date = null;
		try {
			date = sdf.parse(endhours + ":" + endminutes);
		} catch (ParseException e) {
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);

		String formattedTate = sdf.format(c.getTime());
		inputEndTime.setText(formattedTate);

	}

	long componentTimeToTimestamp(int year, int month, int day, int hour,
			int minute) {

		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		c.set(Calendar.HOUR, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		return (c.getTimeInMillis());
	}

	@Override
	public void onCreatechatroomSuccess(String chatroom_name, String chatroomjid) {

		Log.v("CreateChatroom", "Successfully chatroom created "
				+ chatroom_name + " JID " + chatroomjid);
		long start_timestamp = componentTimeToTimestamp(startYear, startMonth,
				startDay, hour, minutes);
		long end_timestamp = componentTimeToTimestamp(startYear, startMonth,
				startDay, endhours, endminutes);
		long created_at = System.currentTimeMillis();
		String status = "future";
		String max_guest = "20";

		APICreateChatrooms apiCreateChatrooms = new APICreateChatrooms();
		apiCreateChatrooms.doPostChatroom(chatroomjid, chatroom_name,
				TChatApplication.getUserModel().getUsername(),
				String.valueOf(start_timestamp), String.valueOf(end_timestamp),
				status, max_guest, String.valueOf(created_at));
		ChatRoomsModel chatRoomsModel=new ChatRoomsModel();
		chatRoomsModel.saveCreatedRoomInDB(chatroomjid, chatroom_name, TChatApplication.getUserModel().getUsername(), String.valueOf(start_timestamp), String.valueOf(end_timestamp), status, max_guest, String.valueOf(created_at));
		finish();
	}

	@Override
	public void onCreateChatroomFailed(boolean alreadyExists, String message) {

		Log.v("CreateChatroom", "Failed chatroom " + message);
		Toast.makeText(CreateChatRoomActivity.this, "Chatroom creation failed",
				Toast.LENGTH_SHORT).show();
	}
	public void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(inputDate.getWindowToken(), 0);
	}
}
