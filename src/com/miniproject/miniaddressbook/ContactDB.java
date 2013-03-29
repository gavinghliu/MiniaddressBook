package com.miniproject.miniaddressbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class ContactDB {

	static String FILEPATH = "mincontactlist.txt";
	private Context context;
	private List<ContactInfo> oldContactList = null; // 从文件中读取的上一次程序的联系人列表

	public ContactDB(Context context) {
		this.context = context;
		oldContactList = new ArrayList<ContactInfo>();
		readData();
	}

	public List<ContactInfo> getOldContactList() {
		return oldContactList;
	}

	public void setOldContactList(List<ContactInfo> oldContactList) {
		this.oldContactList = oldContactList;
	}

	public void saveContactList(List<ContactInfo> contactList) {

		// StringBuilder contactJsonstr = new StringBuilder();
		JSONObject jsonObject = new JSONObject();
		JSONArray contactJsonList = new JSONArray();
		for (ContactInfo contact : contactList) {
			try {
				// 创建一个对象
				JSONObject contactJson = new JSONObject();
				// 第一个键phone的值是数组，所以需要创建数组对象
				JSONArray phone = new JSONArray();

				for (String phoneNumber : contact.getPhone()) {
					phone.put(phoneNumber);
				}
				contactJson.put("phone", phone);
				contactJson.put("name", contact.getName());
				contactJsonList.put(contactJson);
				jsonObject.put("contactList", contactJsonList);
			} catch (JSONException ex) {
				throw new RuntimeException(ex);
			}

		}
		writeData(jsonObject.toString());
	}

	public void writeData(String str) {
		try {
			FileOutputStream fOut = context.openFileOutput(FILEPATH,
					context.MODE_WORLD_READABLE);
			OutputStreamWriter osw = new OutputStreamWriter(fOut);
			osw.write(str);
			osw.flush();
			osw.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void readData() {
		try {
			FileInputStream fIn = context.openFileInput(FILEPATH);
			InputStreamReader isr = new InputStreamReader(fIn);
			char[] inputBuffer = new char[1024];
			String s = "";

			int charRead;
			while ((charRead = isr.read(inputBuffer)) > 0) {
				// ---convert the chars to a String---
				String readString = String
						.copyValueOf(inputBuffer, 0, charRead);
				s += readString;

				inputBuffer = new char[1024];
			}
			// Log.d("newContactList", s);
			JSONObject jsonObject = new JSONObject(s);
			JSONArray oldContactListJson = new JSONArray();
			oldContactListJson = jsonObject.getJSONArray("contactList");

			for (int i = 0; i < oldContactListJson.length(); i++) {
				try {
					JSONObject oldContactJson = (JSONObject) oldContactListJson
							.get(i);

					ContactInfo contact = new ContactInfo(context);
					List<String> phone = new ArrayList<String>();
					JSONArray number = oldContactJson.getJSONArray("phone");
					String name = oldContactJson.getString("name");
					for (int j = 0; j < number.length(); j++) {
						phone.add(number.getString(j));
					}
					contact.setName(name);
					contact.setPhone(phone);

					oldContactList.add(contact);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
}
