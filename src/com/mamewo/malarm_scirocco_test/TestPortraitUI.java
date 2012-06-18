package com.mamewo.malarm_scirocco_test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import junit.framework.Assert;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import asia.sonix.scirocco.SciroccoSolo;

import com.jayway.android.robotium.solo.Solo;
import com.mamewo.malarm24.*;
import com.mamewo.malarm24.R;

public class TestPortraitUI
	extends ActivityInstrumentationTestCase2<MalarmActivity>
{
	//TODO: get from device
	static final
	private int SCREEN_HEIGHT = 800;
	static final
	private int SCREEN_WIDTH = 480;

	private final static String TAG = "malarm_scirocco_test";
	protected SciroccoSolo solo_;

	private static class Name2Index {
		String _name;
		int _index;
		
		public Name2Index(String name, int index) {
			_name = name;
			_index = index;
		}
	}

	private static Name2Index[] PREF_TABLE = null;

	private int lookup(Name2Index[] table, String name) {
		for (Name2Index entry : table) {
			if (entry._name.equals(name)) {
				return entry._index;
			}
		}
		throw new RuntimeException("lookup: " + name + " is not found");
	}
	
	public TestPortraitUI() {
		super("com.mamewo.malarm24", MalarmActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		Log.i("malarm_test", "setUp is called " + PREF_TABLE);
		solo_ = new SciroccoSolo(getInstrumentation(), getActivity(), "com.mamewo.malarm_test");
		//static field is cleared to null, why?
		//crete this table automatically
		PREF_TABLE = new Name2Index[] {
			new Name2Index("site", 0),
			new Name2Index("playlist_directory", 1),
			new Name2Index("sleep_playlist", 2),
			new Name2Index("wakeup_playlist", 3),
			new Name2Index("reload_playlist", 4),
			new Name2Index("create_playlist", 5),
			new Name2Index("sleep_time", 6),
			new Name2Index("default_time", 7),
			new Name2Index("vibration", 8),
			new Name2Index("sleep_volume", 9),
			new Name2Index("wakeup_volume", 10),
			new Name2Index("clear_webview_cache", 11),
			new Name2Index("help", 13),
			new Name2Index("version", 14)
		};
		solo_.sleep(1000);
	}

	@Override
	public void tearDown() throws Exception {
		System.out.println("tearDown is called");
		try {
			//Robotium will finish all the activities that have been opened
			solo_.finalize();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();
	} 

	//name of test case MUST begin with "test"
	@Smoke
	public void testSetAlarm() throws Exception {
		Date now = new Date(System.currentTimeMillis() + 60 * 1000);
		solo_.setTimePicker(0, now.getHours(), now.getMinutes());
		solo_.clickOnView(solo_.getView(R.id.alarm_button));
		solo_.sleep(500);
		TextView targetTimeLabel = (TextView)solo_.getView(R.id.target_time_label);
		TextView sleepTimeLabel = (TextView)solo_.getView(R.id.sleep_time_label);
		Assert.assertTrue("check wakeup label", targetTimeLabel.getText().length() > 0);
		Assert.assertTrue("check sleep label", sleepTimeLabel.getText().length() > 0);
		solo_.goBack();
		solo_.sleep(61 * 1000);
		Assert.assertTrue("Switch alarm button wording", solo_.searchToggleButton(solo_.getString(R.string.stop_alarm)));
		Assert.assertTrue("Correct alarm toggle button state", solo_.isToggleButtonChecked(solo_.getString(R.string.stop_alarm)));
		//TODO: check music?
		//TODO: check vibration
		//TODO: check notification
		solo_.clickOnButton(solo_.getString(R.string.stop_alarm));
		solo_.sleep(1000);
		solo_.takeScreenShot();
		Assert.assertTrue("check wakeup label", targetTimeLabel.getText().length() == 0);
		Assert.assertTrue("check sleep label", sleepTimeLabel.getText().length() == 0);
		Assert.assertTrue("Alarm stopped", !solo_.isToggleButtonChecked(solo_.getString(R.string.set_alarm)));
	}
	
	@Smoke
	public void testSetNow() throws Exception {
		//cannot get timepicker of Xperia acro...
		//TimePicker picker = solo_.getCurrentTimePickers().get(0);
		TimePicker picker = (TimePicker)solo_.getView(R.id.timePicker1);
		solo_.clickOnButton(solo_.getString(R.string.set_now_short));
		//umm... yield to target activity
		Calendar now = new GregorianCalendar();
		solo_.sleep(200);
		Assert.assertEquals((int)now.get(Calendar.HOUR_OF_DAY), (int)picker.getCurrentHour());
		Assert.assertEquals((int)now.get(Calendar.MINUTE), (int)picker.getCurrentMinute());
		solo_.takeScreenShot();
	}

	//TODO: voice button?
	public void testNextTuneShort() {
		View nextButton = solo_.getView(R.id.next_button);
		solo_.clickOnView(nextButton);
		solo_.sleep(2000);
		//speech recognition dialog
		//capture
		solo_.sendKey(Solo.DELETE);
	}

	public void testNextTuneLong() {
		View nextButton = solo_.getView(R.id.next_button);
		solo_.clickLongOnView(nextButton);
		solo_.sleep(2000);
		TextView view = (TextView)solo_.getView(R.id.sleep_time_label);
		String text = view.getText().toString();
		Log.i(TAG, "LongPressNext: text = " + text);
		Assert.assertTrue(text != null);
		//TODO: check preference value...
		Assert.assertTrue(text.length() > 0);
		solo_.clickOnMenuItem(solo_.getString(R.string.stop_music));
		solo_.sleep(2000);
		String afterText = view.getText().toString();
		Assert.assertTrue(afterText == null || afterText.length() == 0);
	}

	/////////////////
	//test menu
	public void testStopVibrationMenu() {
		//TODO: cannot select menu by japanese, why?
		solo_.clickOnMenuItem(solo_.getString(R.string.stop_vibration));
		solo_.sleep(2000);
	}
	
	public void testPlayMenu() {
		solo_.clickOnMenuItem(solo_.getString(R.string.play_wakeup));
		solo_.sleep(5000);
		solo_.clickOnMenuItem(solo_.getString(R.string.stop_music));
		solo_.sleep(1000);
	}
	
	/////////////////
	//config screen
	@Smoke
	public void testSitePreference() throws Exception {
		solo_.clickOnMenuItem(solo_.getString(R.string.pref_menu));
		//select site configuration
		//TODO: make function...
		solo_.clickInList(lookup(PREF_TABLE, "site"));
		Assert.assertTrue(true);
		solo_.takeScreenShot();
	}
	
	@Smoke
	public void testCreatePlaylists() {
		solo_.clickOnMenuItem(solo_.getString(R.string.pref_menu));
		solo_.clickInList(lookup(PREF_TABLE, "create_playlist"));
		solo_.sleep(5000);
		Assert.assertTrue(true);
	}
	
	@Smoke
	public void testSleepVolume() {
		solo_.clickOnMenuItem(solo_.getString(R.string.pref_menu));
		solo_.clickInList(lookup(PREF_TABLE, "sleep_volume"));
		//TODO: push plus/minus
		Assert.assertTrue(true);
	}

	@Smoke
	public void testWakeupVolume() {
		solo_.clickOnMenuItem(solo_.getString(R.string.pref_menu));
		solo_.clickInList(lookup(PREF_TABLE, "wakeup_volume"));
		//TODO: push plus/minus
		Assert.assertTrue(true);
	}

	//add double tap test of webview
	
	@Smoke
	public void testDefaultTimePreference() {
		solo_.clickOnMenuItem(solo_.getString(R.string.pref_menu));
		solo_.clickInList(lookup(PREF_TABLE, "default_time"));
		Assert.assertTrue(true);
	}

	@Smoke
	public void testVibration() {
		solo_.clickOnMenuItem(solo_.getString(R.string.pref_menu));
		solo_.clickInList(lookup(PREF_TABLE, "vibration"));
		solo_.sleep(1000);
		solo_.clickInList(lookup(PREF_TABLE, "vibration"));
	}
	
	public void testSleepPlaylist() {
		solo_.clickOnMenuItem(solo_.getString(R.string.pref_menu));
		solo_.clickInList(lookup(PREF_TABLE, "sleep_playlist"));
		solo_.scrollDown();
	}
	
	public void testClearCache() {
		solo_.clickOnMenuItem(solo_.getString(R.string.pref_menu));
		solo_.clickInList(lookup(PREF_TABLE, "clear_webview_cache"));
		solo_.sleep(500);
	}
	
	public void testHelp() throws Exception {
		solo_.clickOnMenuItem(solo_.getString(R.string.pref_menu));
		solo_.clickInList(lookup(PREF_TABLE, "help"));
		solo_.sleep(4000);
		solo_.takeScreenShot();
	}
	
	public void testVersion() throws Exception {
		solo_.clickOnMenuItem(solo_.getString(R.string.pref_menu));
		solo_.clickInList(lookup(PREF_TABLE, "version"));
		solo_.takeScreenShot();
	}

	public void testPlaylistLong() throws Exception {
		solo_.clickOnMenuItem(solo_.getString(R.string.pref_menu));
		solo_.clickInList(lookup(PREF_TABLE, "sleep_playlist"));
		solo_.sleep(500);
		solo_.clickLongInList(0);
		solo_.takeScreenShot();
	}
	
	//TODO: fix!
	public void testDoubleTouchLeft() {
		float x = (float)(SCREEN_WIDTH / 6);
		float y = (float)(SCREEN_HEIGHT - 100);
		solo_.clickLongOnView(solo_.getView(R.id.loading_icon));
		View webview = solo_.getView(R.id.webView1);
		int[] pos = new int[2];
		webview.getLocationOnScreen(pos);
		Log.i("malarm_test", "view pos: " + pos[0] + " " + pos[1]);
		
		x = pos[0] + 40;
		y = pos[1] + 40;
		solo_.sleep(1000);
		solo_.clickOnScreen(x, y);
		solo_.clickOnScreen(x, y);
		solo_.sleep(5000);
		//goto prev index
		solo_.finishOpenedActivities();
	}
	
	//TODO: fix!
	public void testDoubleTouchRight() {
		float x = (float)(SCREEN_WIDTH - (SCREEN_WIDTH / 6));
		float y = (float)(SCREEN_HEIGHT - 40);
		solo_.clickOnScreen(x, y);
		solo_.sleep(100);
		solo_.clickOnScreen(x, y);
		solo_.sleep(5000);
	}
	
	//TODO: default config test	
	//TODO: add test of widget
	//TODO: playlist edit
}