package com.courage_wolf;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;

public class CourageWolfGame extends Activity {

	private String ADMIN_ACCESS_CODE = "trogdor";

	private boolean is_logged_in;
	private String user_id;
	private Button energyIndex;
	private Button cashIndex;
	private Button repIndex;
	private int energy;
	private double cash;
	private int rep;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		is_logged_in = false;		

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

		exitButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				if(is_logged_in)
				{
					Toast.makeText(CourageWolfGame.this, String.format("Goodbye %s !", user_id),Toast.LENGTH_LONG).show();
				}
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
							// DBASE CODE FOR LOGIN GOES HERE
							if ((uid.compareTo("A") == 0)&& (pwd.compareTo("B") == 0)) {
								is_logged_in = true;
								Toast.makeText(CourageWolfGame.this,"Login Success", Toast.LENGTH_LONG).show();
								user_id = uid;
								//DBASE CODE TO READ INFO
								updateCounters(100, 10.5034, 100);							
							} 
							else {
								is_logged_in = false;
								Toast.makeText(CourageWolfGame.this,"Login Failed", Toast.LENGTH_LONG).show();
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
									Toast.makeText(CourageWolfGame.this, String.format("Scaffold: %s %s",nUsrName,nPassWd),Toast.LENGTH_LONG).show();//Code to add to database
								}								
							});

							Button rstBtn = (Button)inDlg.findViewById(R.id.admin_reset_button);
							rstBtn.setOnClickListener(new OnClickListener (){
								public void onClick(View v) {
									// TODO Auto-generated method stub
									Toast.makeText(CourageWolfGame.this, "Scaffold: RESET",Toast.LENGTH_LONG).show();//Code to build default data-set
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
						Toast.makeText(CourageWolfGame.this, "You are Too Tierd. Try Again Tomorrow", Toast.LENGTH_LONG).show();
					}
					else if(rep <=0)
					{
						Toast.makeText(CourageWolfGame.this, "Your Parents have Grounded You. You Lose!", Toast.LENGTH_LONG).show();
					}
					else
					{
						//Code to do various things
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
			double bonus = ((double)(rep - 100)) * 0.1;
			Toast.makeText(CourageWolfGame.this, String.format("Wow! You maxed Favor. Your Parents Give you %.2f cash bonus", bonus),Toast.LENGTH_LONG).show();
			cash += bonus;
			rep = 100;
		}
		energyIndex.setText(String.format("Energy:\n%d", energy));
		cashIndex.setText(String.format("Cash:\n$%.2f", cash));
		repIndex.setText(String.format("Favor:\n$%d", rep));
		if(is_logged_in)
		{
			//Code to Update Database
		}
	}
}