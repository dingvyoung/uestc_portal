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

public class LibraryActivity extends ListActivity{

	private Intent intent;
	private ArrayList<String> bookName;
	private ArrayList<String> endTime;
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
		Pattern pattern = Pattern.compile("<td>(.+?)</td>");
		Matcher matcher = pattern.matcher(str);
		int t = 0;
		bookName = new ArrayList<String>();
		endTime = new ArrayList<String>();
		while(matcher.find()) {
			t++;
			if (t > 2) {
				if (t % 2 == 1) {
					bookName.add(matcher.group(1));
				} else {
					endTime.add(matcher.group(1));
				}
			}
			//System.out.println(matcher.group(1));
		}
		for (int i = 0; i < bookName.size(); i++) {
			System.out.println("name: " + bookName.get(i));
			System.out.println("endTime: " + endTime.get(i));
		}
		data = new ArrayList<Map<String, Object>>();
		getData(data);
		adapter = new SimpleAdapter(this, data, R.layout.library,
				new String[]{"bookName", "endTime"},
				new int[]{R.id.bookName, R.id.endTime});
		setListAdapter(adapter);
	}
	private List<Map<String, Object>> getData(List<Map<String, Object>> list) {
		list.clear();
		for (int i = 0; i < bookName.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("bookName", bookName.get(i));
			map.put("endTime", endTime.get(i));
			list.add(map);
		}
		return list;
	}

}
