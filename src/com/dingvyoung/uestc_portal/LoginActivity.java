package com.dingvyoung.uestc_portal;


import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	
	private EditText schoolNO;
	private EditText password;
	private Button loginButton;
	private Button exitButton;
	private ArrayList<String> list = new ArrayList<String>();
	
	
	final int EMPTY_SCHOOLNO = 1;
	final int EMPTY_PASSWORD = 2;
	final int LOGINING = 3;
	
	private Handler h = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
				case EMPTY_SCHOOLNO:
					Toast.makeText(LoginActivity.this,"学号不能为空！", Toast.LENGTH_SHORT).show();
					break;
				case EMPTY_PASSWORD:
					Toast.makeText(LoginActivity.this,"密码不能为空！", Toast.LENGTH_SHORT).show();
					break;
				case LOGINING:
					String a = list.get(0).toString();
					String b = list.get(1).toString();
					list.clear();
					Intent intent = new Intent(LoginActivity.this, ChooseActivity.class);
					intent.putExtra("a", a);
					intent.putExtra("b", b);
					startActivity(intent);
					break;
			}
			super.handleMessage(msg);
		}
	};
	

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        schoolNO = (EditText)findViewById(R.id.editText1);
        password = (EditText)findViewById(R.id.editText2);
        loginButton = (Button)findViewById(R.id.button1);
        exitButton = (Button)findViewById(R.id.button2);
        loginButton.setOnClickListener(new loginListener());
        exitButton.setOnClickListener(new exitListener());
	}
	
	

	class loginListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			loginThread t1 = new loginThread();
			t1.start();
		}
		
	}
	
	class exitListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			finish();
		}
		
	}
	
	class loginThread extends Thread{
		
		public void run(){
			String S_schoolNO = schoolNO.getText().toString();
			String S_password = password.getText().toString();
			list.add(S_schoolNO);
			list.add(S_password);
			Message msg = h.obtainMessage();
			if(S_schoolNO.equals("")){
				msg.what = EMPTY_SCHOOLNO;
				h.sendMessage(msg);
				return;
			}
			if(S_password.equals("")){
				msg.what = EMPTY_PASSWORD;
				h.sendMessage(msg);
				return;
			}
			msg.what = LOGINING;
			h.sendMessage(msg);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 1, R.string.about);
		menu.add(0, 2, 2, R.string.exit);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==1){
			Intent intent = new Intent(this,AboutActivity.class);
			startActivity(intent);
		}
		if(item.getItemId()==2){
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy() {
		android.os.Process.killProcess(android.os.Process.myPid());
		super.onDestroy();
	}
}
