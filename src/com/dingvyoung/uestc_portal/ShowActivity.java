package com.dingvyoung.uestc_portal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ShowActivity extends ListActivity{

	private Intent intent;
	private ArrayList<String> classname;
	private ListView listView;
	private SimpleAdapter adapter;
	private List<Map<String, Object>> data;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		listView = getListView();
		setContentView(listView);
		intent = getIntent();
		String str = intent.getStringExtra("str");
		Pattern pattern = Pattern.compile("<p>(.+?)</p>");
		Matcher matcher = pattern.matcher(str);
		classname = new ArrayList<String>();
		while(matcher.find()){
			classname.add(matcher.group(1));
		}
		data = new ArrayList<Map<String, Object>>();
		getData(data);
		adapter = new SimpleAdapter(this, data, R.layout.timetable,
				new String[] {"classname"},new int[] {R.id.classname});
		setListAdapter(adapter);
	}
	
	private List<Map<String, Object>> getData(List<Map<String, Object>> list) {
		list.clear();
		for (int i = 0; i < classname.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("classname", classname.get(i));
			list.add(map);
		}
		return list;
	}

	
}
