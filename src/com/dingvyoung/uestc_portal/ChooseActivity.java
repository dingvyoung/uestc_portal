package com.dingvyoung.uestc_portal;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class ChooseActivity extends Activity {
	
	private String postURL = "http://portal.uestc.edu.cn/userPasswordValidate.portal";//登录地址
	private String getURL1 = "http://portal.uestc.edu.cn/index.portal?.nctp=true";//获取姓名的地址
	private String getURL2 = "http://ea.uestc.edu.cn/";//获取cookie的地址
	private String S_truename;
	private String S_schoolNO;
	private String S_password;
	private DefaultHttpClient client = new DefaultHttpClient();
	// Create a local instance of cookie store
	private CookieStore cookieStore = new BasicCookieStore();
    // Create local HTTP context
	private HttpContext localContext = new BasicHttpContext();
	private ProgressDialog progressDialog;
	private Spinner spinner1 = null;
	private Spinner spinner2 = null;
	private Button button1 = null;
	private Button button2 = null;
	//private Button button3 = null;
	private static final String p1[] = {"星期一","星期二","星期三","星期四","星期五"};
	private static final String p2[] = {"第1,2节","第3,4节","第5,6节","第7,8节","第9,10,11节","第9,10节"};
	private ArrayAdapter<String> adapter1;
	private ArrayAdapter<String> adapter2;
	private int q1 = 0;
	private int q2 = 0;
	private boolean flag1 = false;
	private boolean flag2 = false;
	
	final int PROGRESS_CANCEL = 0;
	final int EMPTY_SCHOOLNO = 1;
	final int EMPTY_PASSWORD = 2;
	final int ERROR_LOGIN = 3;
	final int SUCCESS_LOGIN = 4;
	final int LOGINING = 5;
	final int ERROR_NETWORK = 6;
	final int QUERY_ING = 7;
	final int QUERY_SUCCESS = 8;
	
	
	Handler h = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case EMPTY_SCHOOLNO:
				Toast.makeText(ChooseActivity.this,"学号不能为空！", Toast.LENGTH_SHORT).show();
				break;
			case EMPTY_PASSWORD:
				Toast.makeText(ChooseActivity.this,"密码不能为空！", Toast.LENGTH_SHORT).show();
				break;
			case ERROR_LOGIN:
				progressDialog.dismiss();
				Toast.makeText(ChooseActivity.this,"用户名或密码错误！", Toast.LENGTH_SHORT).show();
//				Intent intent = new Intent(ChooseActivity.this, LoginActivity.class);
//				startActivity(intent);
				finish();
				break;
			case SUCCESS_LOGIN:
				Toast.makeText(ChooseActivity.this,"welcome！", Toast.LENGTH_SHORT).show();
				progressDialog.dismiss();
				break;
			case LOGINING:
				progressDialog = ProgressDialog.show(ChooseActivity.this, "正在登录", "请稍候...", true, false);
				break;
			case ERROR_NETWORK:
				progressDialog.dismiss();
				Toast.makeText(ChooseActivity.this,"网络故障！", Toast.LENGTH_SHORT).show();
				finish();
				break;
			case QUERY_ING:
				progressDialog = ProgressDialog.show(ChooseActivity.this, "正在查询", "请稍候...", true, true);
				break;
			case QUERY_SUCCESS:
				progressDialog.dismiss();
				break;
		}
			super.handleMessage(msg);
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent = this.getIntent();
		S_schoolNO = intent.getStringExtra("a");
		S_password = intent.getStringExtra("b");
		System.out.println(S_schoolNO+S_password);
		loginThread t1 = new loginThread();
		t1.start();
		
		setContentView(R.layout.activity_choose);
		spinner1 = (Spinner)findViewById(R.id.spinner1);
		spinner2 = (Spinner)findViewById(R.id.spinner2);
		button1 = (Button)findViewById(R.id.button1);
		button2 = (Button)findViewById(R.id.button2);
		//button3 = (Button)findViewById(R.id.button3);
		adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,p1);
		adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,p2);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner1.setAdapter(adapter1);
		spinner2.setAdapter(adapter2);
		spinner1.setOnItemSelectedListener(new Spinner1SelectedListener());
		spinner2.setOnItemSelectedListener(new Spinner2SelectedListener());
		spinner1.setVisibility(View.VISIBLE);
		spinner2.setVisibility(View.VISIBLE);
		button1.setOnClickListener(new classListener());
		button2.setOnClickListener(new libraryListener());
		super.onCreate(savedInstanceState);
	}
	
	class loginThread extends Thread{
		public void run(){
			Message msg = h.obtainMessage();
			Message msg1 = h.obtainMessage();
			
			msg1.what = LOGINING;
			h.sendMessage(msg1);
			
			HttpPost loginPOST = new HttpPost(postURL);
			HttpGet getContent1 = new HttpGet(getURL1);
			HttpGet getContent2 = new HttpGet(getURL2);
			
			localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
			client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
			client.getParams().setParameter(ClientPNames.COOKIE_POLICY,CookiePolicy.BROWSER_COMPATIBILITY);
			
			List <NameValuePair> params=new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("Login.Token1",S_schoolNO));
			params.add(new BasicNameValuePair("Login.Token2",S_password));
			
			HttpResponse contentResponse = null;
			try {
				loginPOST.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));//设置参数
				contentResponse=client.execute(loginPOST,localContext);//登录
				String strResult = null;
				if(contentResponse.getStatusLine().getStatusCode()==200){
					strResult=EntityUtils.toString(contentResponse.getEntity());
					Pattern pattern = Pattern.compile("Successed");
					Matcher matcher = pattern.matcher(strResult);
					if(matcher.find()){
					}
					else{
						msg.what = ERROR_LOGIN;
						h.sendMessage(msg);
						return;
					}
				}
				else{
					msg.what = ERROR_NETWORK;
					h.sendMessage(msg);
					return;
				}
			} catch (UnsupportedEncodingException e2) {
				e2.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				contentResponse = null;
				contentResponse = client.execute(getContent2,localContext);//获取cookie
				contentResponse = null;
				contentResponse = client.execute(getContent1,localContext);//获取姓名
				
				if(contentResponse.getStatusLine().getStatusCode()==200){
					String strResult=EntityUtils.toString(contentResponse.getEntity());
					Pattern pattern = Pattern.compile("<li>欢迎您：(.+?)</li>");
					Matcher matcher = pattern.matcher(strResult);
					if(matcher.find()){
					  if(matcher.group(1)!=null){
						  S_truename = matcher.group(1);
					  }
					  else
					  {
						  S_truename = "UNKNOWN";
					  }
					}
					else 
					{
						S_truename = "UNKNOWN";
					}
				}
				else{
					msg.what = ERROR_NETWORK;
					h.sendMessage(msg);
					return;
				}
			} catch (ClientProtocolException e) {
				System.out.println("GET ERROR 1");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("GET ERROR 2");
				e.printStackTrace();
			}
			msg.what = SUCCESS_LOGIN;
			msg.obj = S_truename;
			h.sendMessage(msg);
		}
	}
	
	class libraryThread extends Thread{
		public void run(){
			flag2 = true;
			try{
				Message msg = h.obtainMessage();
				Message msg2 = h.obtainMessage();
				msg.what = QUERY_ING;
				h.sendMessage(msg);
				getURL2 = "http://idata.uestc.edu.cn/index.aspx";
				localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
				HttpResponse contentResponse = null;
				HttpGet getContent2 = new HttpGet(getURL2);
				contentResponse = client.execute(getContent2, localContext);//获取课表/图书借阅信息
				String strResult = null;
				if(contentResponse.getStatusLine().getStatusCode()==200){
					strResult=EntityUtils.toString(contentResponse.getEntity());
					msg2.what = QUERY_SUCCESS;
					h.sendMessage(msg2);
					Intent intent = new Intent();
					intent.setClass(ChooseActivity.this, LibraryActivity.class);
					intent.putExtra("str", strResult);
					startActivity(intent);
					System.out.println(strResult);
				}
				else{
					System.out.println("POST: ERROR!\n");
				}
			} catch (ClientProtocolException e) {
				System.out.println("GET ERROR 1");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("GET ERROR 2");
				e.printStackTrace();
			}
			flag2 = false;
		}
	}
	
	class classThread extends Thread{
		public void run(){
			flag1 = true;
			Message msg = h.obtainMessage();
			Message msg2 = h.obtainMessage();
			msg.what = QUERY_ING;
			h.sendMessage(msg);
			String getURL = "http://www.math.uestc.edu.cn/timetable.php?p1="+q1+"&p2="+q2;
			HttpGet get1 = new HttpGet(getURL);
			HttpResponse res = null;
			String returnStr = null;
			try {
				res = client.execute(get1);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(res.getStatusLine().getStatusCode()==200){
				try {
					String str = EntityUtils.toString(res.getEntity());
					returnStr = new String(str.getBytes("iso-8859-1"),"utf-8");
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			//System.out.println(returnStr);
			msg2.what = QUERY_SUCCESS;
			h.sendMessage(msg2);
			Intent intent = new Intent();
			intent.setClass(ChooseActivity.this, ShowActivity.class);
			intent.putExtra("str", returnStr);
			startActivity(intent);
			flag1 = false;
		}
	}
	
	class classListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			if(!flag1){
				classThread t1 = new classThread();
				t1.start();
			}
			else{
				progressDialog = ProgressDialog.show(ChooseActivity.this, "正在查询", "请稍候...", true, true);
			}
			
		}

		
	}

	class libraryListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			if(!flag2){
				libraryThread t1 = new libraryThread();
				t1.start();
			}
			else{
				progressDialog = ProgressDialog.show(ChooseActivity.this, "正在查询", "请稍候...", true, true);
			}
			
		}
		
	}
	
	class  Spinner1SelectedListener implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			q1 = arg2+1;
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
		
	}
	
	class  Spinner2SelectedListener implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			q2 = arg2+1;
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
		
	}

	
}
