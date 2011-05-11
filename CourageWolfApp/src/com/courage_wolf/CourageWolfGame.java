package com.courage_wolf;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class CourageWolfGame extends Activity {

	private String ADMIN_ACCESS_CODE = "trogdor";
	private double energyRefreshTime = 1.0;
	private double cashRefreshTime = 7.0;
	private double cashRefreshAmt = 5.0;
	private double repMultiplier = 0.2;

	private boolean is_logged_in;
	private String user_id;
	private Button energyIndex;
	private Button cashIndex;
	private Button repIndex;
	private int energy;
	private double cash;
	private int rep;
	private DataBaseHelper dh;
	private SimpleDateFormat dateFormat;

	private Facebook fb = new Facebook("106878249397540");

	private double timeDifference(Date old_date, Date new_date){		
		Long diff = new Long(new_date.getTime() - old_date.getTime());
		return diff.doubleValue()/(1000.0 * 60.0 * 60.0 * 24.0);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		is_logged_in = false;

		dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ssSSS",Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		dh = new DataBaseHelper(this, dateFormat);

		energyIndex = (Button) findViewById(R.id.energy_ind);
		cashIndex = (Button) findViewById(R.id.cash_ind);
		repIndex = (Button) findViewById(R.id.rep_ind);

		energy = 0;
		cash = 0.0;
		rep = 0;


		final Button loginButton = (Button) findViewById(R.id.login_button);
		final Button planButton = (Button) findViewById(R.id.plan_button);
		final Button adminButton = (Button) findViewById(R.id.admin_button);
		final Button exitButton = (Button) findViewById(R.id.exit_button);
		final Button socialButton = (Button) findViewById(R.id.social_button);

		socialButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				if(is_logged_in){
					try{
						fb.authorize(CourageWolfGame.this, new String[]{"publish_stream","read_stream","offline_access"}, new DialogListener(){

							public void onComplete(Bundle values) {
								// TODO Auto-generated method stub
								if(values.isEmpty()){
									return;
								}								
							}

							public void onFacebookError(FacebookError e) {
								// TODO Auto-generated method stub
								Toast.makeText(CourageWolfGame.this, "Facebook Authorize Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
							}

							public void onError(DialogError e) {
								// TODO Auto-generated method stub
								Toast.makeText(CourageWolfGame.this, "Dialog Authorize Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
							}

							public void onCancel() {
								// TODO Auto-generated method stub

							}

						});

						Bundle params = new Bundle();
						params.putString("name","Courage Wolf App");
						params.putString("caption", String.format("Can you do better than Me?.\nI have %d rep and $ %.2f cash. Sweet!",rep,cash));
						params.putString("message", "Try out this Awesome new App. P.S. Vamsi HATES coding on the Android SDK with every fiber of his being !");
						params.putString("link", "http://johnhcstaton.com/teamcouragewolf/");
						params.putString("picture", "http://omega.uta.edu/~vxg3135/CWolf/master.png");
						fb.dialog(CourageWolfGame.this, "feed", params, new DialogListener(){

							public void onComplete(Bundle values) {
								// TODO Auto-generated method stub
								if(values.isEmpty()){
									return;
								}
								Toast.makeText(CourageWolfGame.this, "Facebook Posting Successful",Toast.LENGTH_LONG).show();
							}

							public void onFacebookError(FacebookError e) {
								// TODO Auto-generated method stub
								Toast.makeText(CourageWolfGame.this, "Facebook Post Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
							}

							public void onError(DialogError e) {
								// TODO Auto-generated method stub
								Toast.makeText(CourageWolfGame.this, "Dialog Post Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
							}

							public void onCancel() {
								// TODO Auto-generated method stub

							}

						});

					}
					catch(Exception ex){
						Toast.makeText(CourageWolfGame.this, "Fatal Error in Insert: "+ex.getMessage()+"\nType: "+ex.toString(), Toast.LENGTH_LONG).show();
					}

				}
				else {
					Toast.makeText(CourageWolfGame.this, "You Need to be Logged In to Play", Toast.LENGTH_LONG).show();
				}
			}

		});

		exitButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				if(is_logged_in){
					Toast.makeText(CourageWolfGame.this, String.format("Goodbye %s !", user_id),Toast.LENGTH_LONG).show();
				}
				dh.close();
				finish();				
			}

		});

		loginButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if(is_logged_in){
					Toast.makeText(CourageWolfGame.this, String.format("Already Logged In as %s !", user_id),Toast.LENGTH_LONG).show();
				}
				else{
					final Dialog dlg = new Dialog(CourageWolfGame.this);
					dlg.setContentView(R.layout.login_dialog);
					dlg.setTitle(R.string.login_string);
					dlg.setCancelable(true);

					final EditText uname = (EditText) dlg.findViewById(R.id.login_uname);
					final EditText passwd = (EditText) dlg.findViewById(R.id.login_passwd);

					Button okBtn = (Button) dlg.findViewById(R.id.login_ok_button);
					Button cancelBtn = (Button) dlg.findViewById(R.id.login_cancel_button);

					cancelBtn.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							is_logged_in = false;
							Toast.makeText(CourageWolfGame.this, "Login Cancelled",Toast.LENGTH_LONG).show();
							updateCounters(0, 0.0, 0);
							dlg.cancel();
						}
					});
					okBtn.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							String uid = uname.getText().toString().trim();
							String pwd = passwd.getText().toString().trim();
							short auth = dh.checkPassWord(uid, pwd);
							if (auth == 1) {								
								user_id = uid;
								UserData d = dh.getValues(user_id);
								if(d.containsData){									
									int tmp_energy = d.getEnergy();
									double tmp_cash = d.getCash();
									int tmp_rep = d.getRep();
									Date tmp_lastlog = d.getLastPlayed();
									is_logged_in = true;
									double diff = timeDifference(tmp_lastlog,new Date());
									if (diff > energyRefreshTime){
										tmp_energy = 30;
										if (diff > cashRefreshTime){
											tmp_cash = tmp_cash + (double)tmp_rep*repMultiplier;
										}
									}
									if (tmp_cash <= cashRefreshAmt){
										tmp_cash = tmp_cash + (double)tmp_rep*repMultiplier;
									}
									String toastmsg="Login Success. Welcome "+uid+"!\nLast Login was on: "+dateFormat.format(tmp_lastlog);
									if (diff >= 1){
										toastmsg = toastmsg + "\n"+String.valueOf(diff)+" days ago.";
									}
									Toast.makeText(CourageWolfGame.this,toastmsg, Toast.LENGTH_LONG).show();
									updateCounters(tmp_energy, tmp_cash, tmp_rep);
								}
								else{
									is_logged_in = false;
									Toast.makeText(CourageWolfGame.this,"FATAL ERROR: Obtaining Values", Toast.LENGTH_LONG).show();
									updateCounters(0, 0.0, 0);
								}
							} 
							else if (auth == 0){
								is_logged_in = false;
								Toast.makeText(CourageWolfGame.this,"Login Failed: Incorrect Password", Toast.LENGTH_LONG).show();
								updateCounters(0, 0.0, 0);
							}
							else if (auth == -1){
								is_logged_in = false;
								Toast.makeText(CourageWolfGame.this,"Login Failed: User Not Found", Toast.LENGTH_LONG).show();
								updateCounters(0, 0.0, 0);
							}
							else{
								is_logged_in = false;
								Toast.makeText(CourageWolfGame.this,"FATAL ERROR: Login Process", Toast.LENGTH_LONG).show();
								updateCounters(0, 0.0, 0);
							}
							dlg.dismiss();
						}
					});

					dlg.show();
				}
			}
		});

		adminButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				final Dialog dlg = new Dialog(CourageWolfGame.this);
				dlg.setContentView(R.layout.admin_access_dialog);
				dlg.setTitle(R.string.admin_string);
				dlg.setCancelable(true);

				Button clickBtn = (Button)dlg.findViewById(R.id.admin_pass_ok_button);

				final EditText accessCode= (EditText)dlg.findViewById(R.id.admin_passwd);

				clickBtn.setOnClickListener(new OnClickListener(){

					public void onClick(View v) {
						if(accessCode.getText().toString().trim().compareToIgnoreCase(ADMIN_ACCESS_CODE)==0)
						{
							Toast.makeText(CourageWolfGame.this, "Admin Access Granted", Toast.LENGTH_LONG).show();
							final Dialog inDlg = new Dialog(CourageWolfGame.this);
							inDlg.setContentView(R.layout.admin_dialog);
							inDlg.setTitle(R.string.admin_string);
							inDlg.setCancelable(true);

							Button doneButton = (Button)inDlg.findViewById(R.id.admin_done_button);
							doneButton.setOnClickListener(new OnClickListener (){
								public void onClick(View v) {
									inDlg.dismiss();
								}								
							});

							Button newUsrBtn = (Button)inDlg.findViewById(R.id.admin_new_usr_button);
							newUsrBtn.setOnClickListener(new OnClickListener (){
								public void onClick(View v) {
									// TODO Auto-generated method stub
									String nUsrName = ((EditText)inDlg.findViewById(R.id.new_uname)).getText().toString().trim();
									String nPassWd = ((EditText)inDlg.findViewById(R.id.new_passwd)).getText().toString().trim();
									//Toast.makeText(CourageWolfGame.this, String.format("Scaffold: %s %s",nUsrName,nPassWd),Toast.LENGTH_LONG).show();//Code to add to database
									long r = dh.insert(nUsrName, nPassWd, 30, 25.0, 100);
									if(r == -1){
										Toast.makeText(CourageWolfGame.this, String.format("Insert Failed: Username %s may already exist",nUsrName),Toast.LENGTH_LONG).show();
									}
									else if(r == -2){
										Toast.makeText(CourageWolfGame.this, "FATAL ERROR: Insert Failed",Toast.LENGTH_LONG).show();
									}
								}								
							});

							Button rstBtn = (Button)inDlg.findViewById(R.id.admin_reset_button);
							rstBtn.setOnClickListener(new OnClickListener (){
								public void onClick(View v) {
									// TODO Auto-generated method stub
									Toast.makeText(CourageWolfGame.this, "Deleting All Entries",Toast.LENGTH_LONG).show();
									dh.deleteAll();
									Toast.makeText(CourageWolfGame.this, "Tables are now Empty",Toast.LENGTH_LONG).show();
								}
							});

							dlg.dismiss();
							inDlg.show();
						}
						else
						{
							Toast.makeText(CourageWolfGame.this, "Admin Access Denied", Toast.LENGTH_LONG).show();
							dlg.dismiss();
						}
					}

				});

				dlg.show();

			}

		});

		planButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (is_logged_in)
				{
					if(energy == 0)
					{
						Toast.makeText(CourageWolfGame.this, String.format("You are Too Tierd. Try Again after %f days",energyRefreshTime), Toast.LENGTH_LONG).show();
					}
					else if(rep <=0)
					{
						Toast.makeText(CourageWolfGame.this, "Your Parents have Grounded You. You Lose!", Toast.LENGTH_LONG).show();
					}
					else
					{
						int energyIncr = 0, repIncr = 0;
						double cashIncr = 0.0;
						//Code to do various things
						updateCounters(energyIncr,cashIncr,repIncr);
					}										
				}
				else
				{
					Toast.makeText(CourageWolfGame.this, "You Need to be Logged In to Play", Toast.LENGTH_LONG).show();
				}
			}

		});
	}

	private void updateCounters(int energy_increment, double cash_increment, int rep_increment) {
		energy += energy_increment;
		cash += cash_increment;
		rep += rep_increment;
		if(rep > 100)
		{
			double bonus = ((double)(rep - 100)) * repMultiplier;
			Toast.makeText(CourageWolfGame.this, String.format("Wow! You maxed Favor. Your Parents Give you %.2f cash bonus", bonus),Toast.LENGTH_LONG).show();
			cash += bonus;
			rep = 100;
		}
		energyIndex.setText(String.format("Energy:\n%d", energy));
		cashIndex.setText(String.format("Cash:\n$%.2f", cash));
		repIndex.setText(String.format("Favor:\n$%d", rep));
		if(is_logged_in)
		{
			if (dh.update(user_id, energy, cash, rep)==-1)
				Toast.makeText(CourageWolfGame.this, "FATAL ERROR: No Update", Toast.LENGTH_LONG).show();
		}
	}
}