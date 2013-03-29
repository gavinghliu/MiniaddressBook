package com.miniproject.miniaddressbook;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;

public class ContactManger {

	static String[] pinyin = { "a", "ai", "an", "ang", "ao", "ba", "bai",
			"ban", "bang", "bao", "bei", "ben", "beng", "bi", "bian", "biao",
			"bie", "bin", "bing", "bo", "bu", "ca", "cai", "can", "cang",
			"cao", "ce", "ceng", "cha", "chai", "chan", "chang", "chao", "che",
			"chen", "cheng", "chi", "chong", "chou", "chu", "chuai", "chuan",
			"chuang", "chui", "chun", "chuo", "ci", "cong", "cou", "cu",
			"cuan", "cui", "cun", "cuo", "da", "dai", "dan", "dang", "dao",
			"de", "deng", "di", "dian", "diao", "die", "ding", "diu", "dong",
			"dou", "du", "duan", "dui", "dun", "duo", "e", "en", "er", "fa",
			"fan", "fang", "fei", "fen", "feng", "fo", "fou", "fu", "ga",
			"gai", "gan", "gang", "gao", "ge", "gei", "gen", "geng", "gong",
			"gou", "gu", "gua", "guai", "guan", "guang", "gui", "gun", "guo",
			"ha", "hai", "han", "hang", "hao", "he", "hei", "hen", "heng",
			"hong", "hou", "hu", "hua", "huai", "huan", "huang", "hui", "hun",
			"huo", "ji", "jia", "jian", "jiang", "jiao", "jie", "jin", "jing",
			"jiong", "jiu", "ju", "juan", "jue", "jun", "ka", "kai", "kan",
			"kang", "kao", "ke", "ken", "keng", "kong", "kou", "ku", "kua",
			"kuai", "kuan", "kuang", "kui", "kun", "kuo", "la", "lai", "lan",
			"lang", "lao", "le", "lei", "leng", "li", "lia", "lian", "liang",
			"liao", "lie", "lin", "ling", "liu", "long", "lou", "lu", "lv",
			"luan", "lue", "lun", "luo", "ma", "mai", "man", "mang", "mao",
			"me", "mei", "men", "meng", "mi", "mian", "miao", "mie", "min",
			"ming", "miu", "mo", "mou", "mu", "na", "nai", "nan", "nang",
			"nao", "ne", "nei", "nen", "neng", "ni", "nian", "niang", "niao",
			"nie", "nin", "ning", "niu", "nong", "nu", "nv", "nuan", "nue",
			"nuo", "o", "ou", "pa", "pai", "pan", "pang", "pao", "pei", "pen",
			"peng", "pi", "pian", "piao", "pie", "pin", "ping", "po", "pu",
			"qi", "qia", "qian", "qiang", "qiao", "qie", "qin", "qing",
			"qiong", "qiu", "qu", "quan", "que", "qun", "ran", "rang", "rao",
			"re", "ren", "reng", "ri", "rong", "rou", "ru", "ruan", "rui",
			"run", "ruo", "sa", "sai", "san", "sang", "sao", "se", "sen",
			"seng", "sha", "shai", "shan", "shang", "shao", "she", "shen",
			"sheng", "shi", "shou", "shu", "shua", "shuai", "shuan", "shuang",
			"shui", "shun", "shuo", "si", "song", "sou", "su", "suan", "sui",
			"sun", "suo", "ta", "tai", "tan", "tang", "tao", "te", "teng",
			"ti", "tian", "tiao", "tie", "ting", "tong", "tou", "tu", "tuan",
			"tui", "tun", "tuo", "wa", "wai", "wan", "wang", "wei", "wen",
			"weng", "wo", "wu", "xi", "xia", "xian", "xiang", "xiao", "xie",
			"xin", "xing", "xiong", "xiu", "xu", "xuan", "xue", "xun", "ya",
			"yan", "yang", "yao", "ye", "yi", "yin", "ying", "yo", "yong",
			"you", "yu", "yuan", "yue", "yun", "za", "zai", "zan", "zang",
			"zao", "ze", "zei", "zen", "zeng", "zha", "zhai", "zhan", "zhang",
			"zhao", "zhe", "zhen", "zheng", "zhi", "zhong", "zhou", "zhu",
			"zhua", "zhuai", "zhuan", "zhuang", "zhui", "zhun", "zhuo", "zi",
			"zong", "zou", "zu", "zuan", "zui", "zun", "zuo" };
	
	private List<ContactInfo> newContactList = null; // 从联系人中直接读取的列表
	private List<ContactInfo> searchList = null; // 搜索列表
	private Context context;

	public ContactManger(Context context) {
		this.context = context;
		initList();
		getContactList();
	}

	public List<ContactInfo> getNewContactList() {
		return newContactList;
	}

	public void setNewContactList(List<ContactInfo> newContactList) {
		this.newContactList = newContactList;
	}

	public List<ContactInfo> getSearchList() {
		return searchList;
	}

	public void setSearchList(List<ContactInfo> searchList) {
		this.searchList = searchList;
	}

	// 初始化列表
	public void initList() {
		newContactList = new ArrayList<ContactInfo>();
		searchList = new ArrayList<ContactInfo>();
	}

	// 获取通讯录列表
	private void getContactList() {
		String[] selectField = { Phone.DISPLAY_NAME, Phone.NUMBER,
				Phone.PHOTO_ID, "sort_key" };
		Cursor cur = context.getContentResolver().query(Phone.CONTENT_URI,
				selectField, null, null,
				Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
		cur.moveToFirst();
		boolean addContact;
		while (cur.getCount() > cur.getPosition()) {
			ContactInfo contact = new ContactInfo(context);
			List<String> phone = new ArrayList<String>();
			String number = cur.getString(cur.getColumnIndex(Phone.NUMBER));
			String name = cur.getString(cur.getColumnIndex(Phone.DISPLAY_NAME));
			String photo_id = cur.getString(cur.getColumnIndex(Phone.PHOTO_ID));
			contact.setName(name);
			contact.setPhotoId(photo_id);
			phone.add(number);
			contact.setPhone(phone);
			addContact = true;
			for (int i = 0; i < newContactList.size(); i++) {
				if (newContactList.get(i).getName().equals(contact.getName())) {
					for (int k = 0; k < contact.getPhone().size(); k++)
						newContactList.get(i).addPhone(
								contact.getPhone().get(k));
					addContact = false;
					break;
				}
			}
			if (addContact)
				newContactList.add(contact);
			cur.moveToNext();
		}
		cur.close();
	}

	// 搜索联系人
	public List<ContactInfo> getSearchUser(String condition) {
		List<ContactInfo> tempList = null;
		tempList = new ArrayList<ContactInfo>();
		searchList.clear();
		if (condition == null || condition.equals(""))
			return null;
		String[] projection = { Phone.DISPLAY_NAME, Phone.NUMBER,
				Phone.PHOTO_ID, "sort_key" };
		String selection = Phone.NUMBER + " like '%" + condition + "%' or "
				+ Phone.DISPLAY_NAME + " like '%" + condition + "%' or "
				+ "sort_key" + " like '%" + getPYSearchRegExp(condition, "%")
				+ "%'";
		Cursor cur = context.getContentResolver().query(Phone.CONTENT_URI, projection,
				selection, null, Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
		cur.moveToFirst();
		while (cur.getCount() > cur.getPosition()) {
			ContactInfo contact = new ContactInfo(context);
			List<String> phoneList = new ArrayList<String>();
			String number = cur.getString(cur.getColumnIndex(Phone.NUMBER));
			String name = cur.getString(cur.getColumnIndex(Phone.DISPLAY_NAME));
			String photo_id = cur.getString(cur.getColumnIndex(Phone.PHOTO_ID));
			String sort_key = cur.getString(cur.getColumnIndex("sort_key"));
			boolean show = true;
			if (isPinYin(condition)) {
				if (containCn(sort_key)) {
					show = pyMatches(sort_key, condition.replaceAll(" ", ""));
				}
				// 如果sort_key 不包含中文 则需要用display_name包含匹配
				else {
					show = false;
					for (char c : condition.toCharArray()) {
						if (name != null
								&& name.toLowerCase().contains(
										(c + "").toLowerCase())) {
							show = true;
							break;
						}
					}
				}
			}
			// System.out.println("is show " + show);

			if (show) {
				contact.setName(name);
				contact.setSortKey(sort_key);
				contact.setPhotoId(photo_id);
				phoneList.add(number);
				contact.setPhone(phoneList);
				boolean addContact = true;
				for (int i = 0; i < tempList.size(); i++) {
					if (tempList.get(i).getName().equals(contact.getName())) {
						for (int k = 0; k < contact.getPhone().size(); k++)
							tempList.get(i).addPhone(contact.getPhone().get(k));
						addContact = false;
						break;
					}
				}
				if (addContact)
					tempList.add(contact);
			}
			cur.moveToNext();
		}
		cur.close();

		// 排序搜索序列，只包含英文或者数字的结果集排序
		for (ContactInfo contactInfo : tempList) {
			if (!containCn(contactInfo.getName())) {
				searchList.add(contactInfo);
			}
		}
		sortContactBySortKey(searchList, condition.substring(0, 1));

		// 只包含中文的结果集排序
		List<ContactInfo> tempSortList = new ArrayList<ContactInfo>();
		for (ContactInfo contactInfo : tempList) {
			if (containCn(contactInfo.getName())) {
				tempSortList.add(contactInfo);
				Log.d("tempSortList", contactInfo.getName());
			}
		}
		sortContactBySortKey(tempSortList, condition.substring(0, 1));
		searchList.addAll(tempSortList);
		return searchList;
	}

	// 按照sortkey排序联系人
	private void sortContactBySortKey(List<ContactInfo> contactList,
			String condition) {
		quickSort(contactList, 0, contactList.size() - 1, condition);
	}

	private void quickSort(List<ContactInfo> sortList, int low, int hight,
			String condition) {
		if (low < hight) {
			int middle = partition(sortList, low, hight, condition);
			quickSort(sortList, low, middle - 1, condition); // 对左半段排序
			quickSort(sortList, middle + 1, hight, condition); // 对右半段排序
		}
	}

	private int partition(List<ContactInfo> contactList, int low, int high,
			String condition) {

		// ContactInfo tmp = contactList.get(low);
		String name = contactList.get(low).getName();
		List<String> phone = contactList.get(low).getPhone();
		String photoId = contactList.get(low).getPhotoId();
		String sortKey = contactList.get(low).getSortKey();

		while (low < high) {
			while (low < high
					&& contactList.get(high).getSortKey().toLowerCase()
							.indexOf(condition.toLowerCase() + "") >= sortKey
							.toLowerCase()
							.indexOf(condition.toLowerCase() + "")) {
				high--;
			}
			contactList.get(low).setName(contactList.get(high).getName());
			contactList.get(low).setPhone(contactList.get(high).getPhone());
			contactList.get(low).setPhotoId(contactList.get(high).getPhotoId());
			contactList.get(low).setSortKey(contactList.get(high).getSortKey());

			while (low < high
					&& contactList.get(low).getSortKey().toLowerCase()
							.indexOf(condition.toLowerCase() + "") < sortKey
							.toLowerCase()
							.indexOf(condition.toLowerCase() + "")) {
				low++;
			}
			contactList.get(high).setName(contactList.get(low).getName());
			contactList.get(high).setPhone(contactList.get(low).getPhone());
			contactList.get(high).setPhotoId(contactList.get(low).getPhotoId());
			contactList.get(high).setSortKey(contactList.get(low).getSortKey());
		}

		contactList.get(low).setName(name);
		contactList.get(low).setPhone(phone);
		contactList.get(low).setPhotoId(photoId);
		contactList.get(low).setSortKey(sortKey);
		// Log.d("支点", name);
		return low;
	}
	
	/**
	 * 
	 * @param str
	 *            搜索字符串
	 * @param exp
	 *            追加的正则表达式
	 * @return 拼音搜索正则表达式
	 */
	public String getPYSearchRegExp(String str, String exp) {
		int start = 0;
		String regExp = "";
		str = str.toLowerCase();
		boolean isFirstSpell = true;
		for (int i = 0; i < str.length(); i++) {
			String tmp = str.substring(start, i + 1);
			isFirstSpell = binSearch(tmp) ? false : true;

			if (isFirstSpell) {
				regExp += str.substring(start, i) + exp;
				start = i;
			} else {
				isFirstSpell = true;
			}

			if (i == str.length() - 1)
				regExp += str.substring(start, i + 1) + exp;
		}
		return regExp;
	}
	
	/**
	 * 2分法查找拼音列表
	 * 
	 * @param str
	 *            拼音字符串
	 * @return 是否是存在于拼音列表
	 */
	public boolean binSearch(String str) {
		int mid = 0;
		int start = 0;
		int end = pinyin.length - 1;

		while (start < end) {
			mid = start + ((end - start) / 2);
			if (pinyin[mid].matches(str + "[a-zA-Z]*"))
				return true;

			if (pinyin[mid].compareTo(str) < 0)
				start = mid + 1;
			else
				end = mid - 1;
		}
		return false;
	}

	/**
	 * 拼音匹配
	 * 
	 * @param src
	 *            含有中文的字符串
	 * @param des
	 *            查询的拼音
	 * @return 是否能匹配拼音
	 */
	public boolean pyMatches(String src, String des) {
		if (src != null) {
			src = src.replaceAll("[^ a-zA-Z]", "").toLowerCase();
			src = src.replaceAll("[ ]+", " ");
			String condition = getPYSearchRegExp(des, "[a-zA-Z]* ");

			/*
			 * Pattern pattern = Pattern.compile(condition); Matcher m =
			 * pattern.matcher(src); return m.find();
			 */
			String[] tmp = condition.split("[ ]");
			String[] tmp1 = src.split("[ ]");

			for (int i = 0; i + tmp.length <= tmp1.length; ++i) {
				String str = "";
				for (int j = 0; j < tmp.length; j++)
					str += tmp1[i + j] + " ";
				if (str.matches(condition))
					return true;
			}
		}
		return false;
	}
	
	// 识别数字
	public boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	// 识别拼音
	public boolean isPinYin(String str) {
		Pattern pattern = Pattern.compile("[ a-zA-Z]*");
		return pattern.matcher(str).matches();
	}

	// 识别汉字
	public boolean containCn(String str) {
		Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5]");
		return pattern.matcher(str).find();
	}

}
