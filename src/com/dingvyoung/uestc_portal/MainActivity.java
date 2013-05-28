package com.dingvyoung.uestc_portal;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //test network
        ConnectivityManager cManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()){
	        Intent intent = new Intent(this,LoginActivity.class);
	        startActivity(intent);
	        finish();
		}
		else{
			Toast.makeText(MainActivity.this,"无法联网！请检查您的网络设置", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
            startActivity(intent);
            finish();
		}
    }

}
