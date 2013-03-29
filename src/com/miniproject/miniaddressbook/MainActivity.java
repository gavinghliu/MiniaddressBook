package com.miniproject.miniaddressbook;

import java.util.ArrayList;
import java.util.List;
import com.miniproject.miniaddressbook.ContactAdapter.ViewHolder;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemClickListener {

	public static int NORESULT = 20;
	private ContactDB contactDB = null; // 数据存储
	private ContactManger contactManger = null; // 内存管理
	private long exitTime = 0;
	private List<ContactInfo> lastContactList = null; // 显示出来的没有变化的联系人列表
	private List<ContactInfo> difContactList = null; // 显示出来的改变过的联系人列表

	/**
	 * 界面元素
	 */
	private EditText editSearch;
	private ImageView btnClearSearch;
	private ListView listView;
	private ListView newListView;
	private View headView;
	private ContactAdapter contactAdapter;
	private ContactAdapter newFriendContactAdapter;
	private TextView noResultTextView;
	private View coverView = null;
	private FrameLayout layout = null;
	private LinearLayout layoutSearchBar = null;
	private TranslateAnimation upAnimation;
	private TranslateAnimation downAnimation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		contactDB = new ContactDB(MainActivity.this);
		contactManger = new ContactManger(MainActivity.this);
		lastContactList = new ArrayList<ContactInfo>();
		difContactList = new ArrayList<ContactInfo>();
		headView = (View) findViewById(R.id.header_view);
		layoutSearchBar = (LinearLayout) findViewById(R.id.window);
		editSearch = (EditText) findViewById(R.id.edit_search);
		layout = (FrameLayout) findViewById(R.id.oldFriendFrameLayout);

		
		setSearchEditText();
		// 首次应用程序不检测新好友
		if (contactDB.getOldContactList().size() == 0)
			contactDB.setOldContactList(contactManger.getNewContactList());

		remindChange(contactManger.getNewContactList(),
				contactDB.getOldContactList());

		contactAdapter = new ContactAdapter(MainActivity.this, lastContactList);
		
		// 无结果界面
		noResultTextView = new TextView(MainActivity.this);
		noResultTextView.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		noResultTextView.setBackgroundColor(Color.parseColor("#FFFFFF"));
		noResultTextView.setGravity(Gravity.CENTER);
		noResultTextView.setTextSize(22);
		noResultTextView.setText("无结果");

		// 旧好友列表
		listView = (ListView) findViewById(R.id.oldFriendListView);
		listView.setAdapter(contactAdapter);
		listView.setOnItemClickListener(MainActivity.this);
		listView.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(MainActivity.this
						.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});

		// 清空搜索栏按钮
		btnClearSearch = (ImageView) findViewById(R.id.btn_clean_search);
		btnClearSearch.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				editSearch.setText("");
				btnClearSearch.setVisibility(View.GONE);
				contactAdapter.list = lastContactList;
				contactAdapter.notifyDataSetChanged();
				listView.invalidate();
			}
		});

		//变更提醒分栏设置
		if (difContactList.size() > 0) {
			TextView newFriendTv = (TextView) findViewById(R.id.newFriendTv);
			TextView friendTv = (TextView) findViewById(R.id.friend);
			newFriendTv.append("(" + difContactList.size() + ")");
			friendTv.append("(" + lastContactList.size() + ")");

			if (difContactList.size() > 2)
				findViewById(R.id.newFridendFrameLayout).setLayoutParams(
						new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
								dip2px(MainActivity.this, 140)));

			findViewById(R.id.newFriend_view).setVisibility(View.VISIBLE);
			findViewById(R.id.friend_view).setVisibility(View.VISIBLE);
			findViewById(R.id.newFridendFrameLayout).setVisibility(View.VISIBLE);
			newFriendContactAdapter = new ContactAdapter(MainActivity.this, difContactList);
		}
		
		// 新好友列表
		newListView = (ListView) findViewById(R.id.newFridendListView);
		newListView.setAdapter(newFriendContactAdapter);
		newListView.setOnItemClickListener(MainActivity.this);

		// 遮盖图层
		coverView = new TextView(MainActivity.this);
		coverView.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		coverView.setBackgroundColor(Color.parseColor("#86222222"));
		coverView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int[] location = new int[2];
				headView.setVisibility(View.VISIBLE);
				layoutSearchBar.getLocationInWindow(location);
				downAnimation = new TranslateAnimation(location[0],
						location[0], -1 * headView.getHeight(), 0);
				downAnimation.setDuration(100);
				downAnimation.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
						// TODO Auto-generated method stub
						if (coverView.getParent() != null)
							layout.removeView(coverView);
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						inputMethodManager.hideSoftInputFromWindow(
								MainActivity.this.getCurrentFocus()
										.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
					}
				});
				layoutSearchBar.setAnimation(downAnimation);
				
				if (difContactList.size() > 0 && editSearch.getText().length() == 0) {
					findViewById(R.id.newFriend_view).setVisibility(
							View.VISIBLE);
					findViewById(R.id.friend_view).setVisibility(View.VISIBLE);
					findViewById(R.id.newFridendFrameLayout).setVisibility(
							View.VISIBLE);
				}
			}
		});
	}
	
	//设置SearchEditText UI
	public void setSearchEditText(){
		editSearch.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (coverView.getParent() == null) {
						upAnimation = new TranslateAnimation(0, 0, 0, -1
								* headView.getHeight());
						upAnimation.setDuration(200);
						upAnimation.setFillEnabled(true);
						upAnimation
								.setAnimationListener(new AnimationListener() {

									@Override
									public void onAnimationStart(
											Animation animation) {
										// TODO Auto-generated method stub
									}

									@Override
									public void onAnimationRepeat(
											Animation animation) {
										// TODO Auto-generated method stub
									}

									@Override
									public void onAnimationEnd(
											Animation animation) {
										layout.addView(coverView);
										headView.setVisibility(View.GONE);
									}
								});
						layoutSearchBar.setAnimation(upAnimation);
						findViewById(R.id.newFriend_view).setVisibility(
								View.GONE);
						findViewById(R.id.friend_view).setVisibility(View.GONE);
						findViewById(R.id.newFridendFrameLayout).setVisibility(
								View.GONE);
					}
				}
			}
		});

		editSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (coverView.getParent() == null
						&& noResultTextView.getParent() == null
						&& contactManger.getSearchList().size() == 0) {
					upAnimation = new TranslateAnimation(0, 0, 0, -1
							* headView.getHeight());
					upAnimation.setDuration(200);
					upAnimation.setFillEnabled(true);
					upAnimation.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationStart(Animation animation) {
							// TODO Auto-generated method stub
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
							// TODO Auto-generated method stub
						}

						@Override
						public void onAnimationEnd(Animation animation) {
							layout.addView(coverView);
							headView.setVisibility(View.GONE);
						}
					});
					layoutSearchBar.setAnimation(upAnimation);
					findViewById(R.id.newFriend_view).setVisibility(View.GONE);
					findViewById(R.id.friend_view).setVisibility(View.GONE);
					findViewById(R.id.newFridendFrameLayout).setVisibility(
							View.GONE);
				}
			}
		});

		editSearch.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.length() != 0) {
					btnClearSearch.setVisibility(View.VISIBLE);
					contactManger.getSearchUser(s.toString());
					contactAdapter.list = contactManger.getSearchList();
					if (contactManger.getSearchList().size() > 0) {
						layout.removeView(noResultTextView);
						layout.removeView(coverView);
					} else {
						if (noResultTextView.getParent() == null)
							layout.addView(noResultTextView);

					}
				} else {
					contactManger.getSearchList().clear();
					if (difContactList.size() > 0
							&& coverView.getParent() == null
							&& headView.getVisibility() == View.VISIBLE) {
						findViewById(R.id.newFriend_view).setVisibility(
								View.VISIBLE);
						findViewById(R.id.friend_view).setVisibility(
								View.VISIBLE);
						findViewById(R.id.newFridendFrameLayout).setVisibility(
								View.VISIBLE);
					}
					if (noResultTextView.getParent() != null)
						layout.removeView(noResultTextView);
					btnClearSearch.setVisibility(View.GONE);
					contactAdapter.list = lastContactList;
					contactAdapter.isShowNum = false;
					if (coverView.getParent() == null) {
						layout.addView(coverView);
					}

				}
				contactAdapter.notifyDataSetChanged();
				listView.invalidate();
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	
	// 将像素转换成分辨率无关像素
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public void makeCall(String number) {
		Uri uri = Uri.parse("tel:" + number);
		// Intent it = new Intent(Intent.ACTION_CALL, uri);
		Intent it = new Intent(Intent.ACTION_DIAL, uri);
		startActivity(it);
	}

	// 把文件中的lsit和新读到的联系人列表做比较，提醒的内容包括新增和变更
	public void remindChange(List<ContactInfo> newList,
			List<ContactInfo> oldList) {
		Boolean isbreak = false;
		// true表示联系人已经被找到，否则相反
		Boolean[] flag = new Boolean[newList.size()];
		for (int i = 0; i < flag.length; i++)
			flag[i] = false;
		for (ContactInfo oldContact : oldList) {
			for (int i = 0; i < newList.size(); i++) {
				if (!flag[i]
						&& newList.get(i).getName()
								.equals(oldContact.getName())) {

					flag[i] = true;
					isbreak = false;
					// 电话数目不一样记录为变更
					if (oldContact.getPhone().size() != newList.get(i)
							.getPhone().size()) {
						difContactList.add(newList.get(i));
						isbreak = true;
					}

					// 电话内容不一样记录为变更
					for (String number : oldContact.getPhone()) {
						if (!newList.get(i).getPhone().contains(number)) {
							difContactList.add(newList.get(i));
							isbreak = true;
							break;
						}
					}

					if (isbreak)
						break;

					Log.d("lastContactList", "lastContactListadd"
							+ newList.get(i).getName());
					lastContactList.add(newList.get(i));
					break;
				}
			}
		}

		if (oldList.size() == 0)
			lastContactList = newList;

		for (int i = 0; i < flag.length; i++) {
			if (!flag[i])
				difContactList.add(newList.get(i));
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				contactDB.saveContactList(contactManger.getNewContactList());
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		ViewHolder viewHolder = (ViewHolder) arg1.getTag();
		ContactInfo selectContact = null;
		for (ContactInfo contactInfo : contactManger.getNewContactList()) {
			if (contactInfo.getName().equals(viewHolder.tvItemName.getText())) {
				selectContact = contactInfo;
				break;
			}
		}
		if (selectContact.getPhone().size() == 1)
			makeCall(selectContact.getPhone().get(0));

		if (selectContact.getPhone().size() > 1) {
			Builder builder = new AlertDialog.Builder(this);
			ListView numberList = new ListView(this);
			numberList.setAdapter(new PhoneNumberListAdapter(this,
					selectContact.getPhone()));
			final ContactInfo tempContact = selectContact;
			numberList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					makeCall(tempContact.getPhone().get(position));
				}
			});
			numberList.setLayoutParams(new FrameLayout.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			AlertDialog dialog = builder.create();
			dialog.setView(numberList, 0, 0, 0, 0);
			dialog.show();
		}
	}

}
