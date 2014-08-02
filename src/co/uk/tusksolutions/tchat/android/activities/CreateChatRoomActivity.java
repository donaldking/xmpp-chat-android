package co.uk.tusksolutions.tchat.android.activities;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import co.uk.tusksolutions.tchat.android.R;

public class CreateChatRoomActivity extends ActionBarActivity {

	private EditText chatroomName;
	private EditText inputDate;
	private EditText inputTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_chatroom);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		getSupportActionBar().setTitle("Schedule Chatroom");

		chatroomName = (EditText) findViewById(R.id.chatroom_name);
		inputDate = (EditText) findViewById(R.id.date_chatroom);
		inputTime = (EditText) findViewById(R.id.time_chatroom);
		inputDate.setKeyListener(null);
		inputTime.setKeyListener(null);

		inputDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showStartDateDialog(v);
			}
		});
		inputTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showStartTimeDialog(v);
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
					CreateChatRoomActivity.this, 1, this, hour, minutes, false);

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
	            date = sdf.parse(hour+":"+minutes);
	        } catch (ParseException e) {
	        }
	        Calendar c = Calendar.getInstance();
	        c.setTime(date);
		
		
		
		String formattedTate = sdf.format(c.getTime());
		inputTime.setText(formattedTate);

	}
}
