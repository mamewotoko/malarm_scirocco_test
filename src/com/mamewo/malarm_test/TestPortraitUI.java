package com.mamewo.malarm_test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

import junit.framework.Assert;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import com.jayway.android.robotium.solo.Solo;
import com.mamewo.malarm24.*;

import com.mamewo.malarm24.R;

public class TestPortraitUI
	extends ActivityInstrumentationTestCase2<MalarmActivity>
{
	static final
	private int PORT = 3333;
	//TODO: get from device
	static final
	private int SCREEN_HEIGHT = 800;
	static final
	private int SCREEN_WIDTH = 480;

	private final static String PACKAGE_NAME = "malarm_test";
	protected Solo solo_;
	private BufferedWriter _bw;
	private Socket _sock;
	private String _hostname = "192.168.0.20";
	//set true to capture screen (it requires CaptureServer in mimicj)
	private boolean _support_capture = false;
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
		return -1;
	}
	
	public void initCapture() throws IOException {
		if (!_support_capture) {
			return;
		}
		_sock = new Socket(_hostname, PORT);
		_bw = new BufferedWriter(new OutputStreamWriter(_sock.getOutputStream()));
	}
	
	public void finalizeCapture() {
		if (!_support_capture) {
			return;
		}
		Log.i("malam_test", "finalizeCapture is called");
		try {
			_bw.close();
			_sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void captureScreen(String filename) {
		if (!_support_capture) {
			return;
		}
		try {
			_bw.write(filename + "\n");
			_bw.flush();
			//TODO: wait until captured
			Thread.sleep(1000);
		} catch (IOException e) {
			Log.i ("malarm_test", "capture failed: " + filename);
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public TestPortraitUI() {
		super("com.mamewo.malarm24", MalarmActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		Log.i("malarm_test", "setUp is called " + PREF_TABLE);
		solo_ = new Solo(getInstrumentation(), getActivity());
		initCapture();
		//static field is cleared to null, why?
		PREF_TABLE = new Name2Index[] {
			new Name2Index("site", 0),
			new Name2Index("default_time", 3),
			new Name2Index("sleep_volume", 5),
			new Name2Index("sleep_playlist", 8),
			new Name2Index("help", 13),
			new Name2Index("version", 14)
		};
	}

	@Override
	public void tearDown() throws Exception {
		System.out.println("tearDown is called");
		try {
			//Robotium will finish all the activities that have been opened
			solo_.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		finalizeCapture();
		getActivity().finish();
		super.tearDown();
	} 

	//name of test case MUST begin with "test"
	@Smoke
	public void testSetAlarm() {
		//olo.clickOnMenuItem(solo.getString(R.string.pref_menu));
		//use MediaPlayer
		//solo.getCurrentCheckBoxes().get(0).setChecked(false);
		//solo.goBack();
		Date now = new Date(System.currentTimeMillis() + 60 * 1000);
		solo_.setTimePicker(0, now.getHours(), now.getMinutes());
		solo_.clickOnButton(solo_.getString(R.string.set_alarm));
		Assert.assertTrue("check label", solo_.getText(0).getText().length() > 0);
		//TODO: go home screen
		solo_.goBack();
		solo_.sleep(61 * 1000);
		Assert.assertTrue("Switch alarm button wording", solo_.searchToggleButton(solo_.getString(R.string.stop_alarm)));
		Assert.assertTrue("Correct alarm toggle button state", solo_.isToggleButtonChecked(solo_.getString(R.string.stop_alarm)));
		//TODO: check music?
		//TODO: check vibration
		solo_.clickOnButton(solo_.getString(R.string.stop_alarm));
		solo_.sleep(1000);
		captureScreen("test_setAlarmTest.png");
		Assert.assertTrue("Alarm stopped", !solo_.isToggleButtonChecked(solo_.getString(R.string.set_alarm)));
	}
	
	@Smoke
	public void testSetNow() {
		Calendar now = new GregorianCalendar();
		TimePicker picker = solo_.getCurrentTimePickers().get(0);
		solo_.clickOnButton(solo_.getString(R.string.set_now_short));
		//umm... yield to target activity
		solo_.sleep(200);
		Assert.assertTrue("picker current hour", now.get(Calendar.HOUR_OF_DAY) == picker.getCurrentHour());
		Assert.assertTrue("picker current min", now.get(Calendar.MINUTE) == picker.getCurrentMinute());
		captureScreen("test_setNow.png");
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
		Log.i("malrm_test", "LongPressNext: text = " + text);
		Assert.assertTrue(text != null);
		//TODO: check preference value...
		Assert.assertTrue(text.startsWith("60"));
		solo_.clickOnMenuItem(solo_.getString(R.string.stop_music));
		solo_.sleep(2000);
		String afterText = view.getText().toString();
		Assert.assertTrue(afterText == null || afterText.length() == 0);
	}

	/////////////////
	//test menu
	public void testStopVibrationMenu() {
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
	public void testSitePreference() {
		solo_.clickOnMenuItem(solo_.getString(R.string.pref_menu));
		//select site configuration
		//TODO: make function...
		solo_.clickInList(lookup(PREF_TABLE, "site"));
		Assert.assertTrue(true);
		captureScreen("test_Preference.png");
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
		solo_.clickInList(lookup(PREF_TABLE, "vibrate"));
		solo_.sleep(1000);
		solo_.clickInList(lookup(PREF_TABLE, "vibrate"));
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
	
	public void testHelp() {
		solo_.clickOnMenuItem(solo_.getString(R.string.pref_menu));
		solo_.clickInList(lookup(PREF_TABLE, "help"));
		solo_.sleep(4000);
		captureScreen("test_Help.png");
	}
	
	public void testVersion() {
		solo_.clickOnMenuItem(solo_.getString(R.string.pref_menu));
		solo_.clickInList(lookup(PREF_TABLE, "version"));
		captureScreen("test_Version.png");
	}

	public void testPlaylistLong() {
		solo_.clickOnMenuItem(solo_.getString(R.string.pref_menu));
		solo_.clickInList(lookup(PREF_TABLE, "sleep_playlist"));
		solo_.sleep(500);
		solo_.clickLongInList(0);
		captureScreen("test_playlistlong.png");
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