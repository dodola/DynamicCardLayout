package com.dodola.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.tsz.afinal.FinalBitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.dodola.model.DuitangInfo;
import com.dodola.ui.LazyScrollView.OnScrollListener;

/**
 * 动态规划算法
 * 
 * @author la
 * 
 */
public class MainActivity extends Activity implements OnScrollListener {
	private static final int COLUMNCOUNT = 4;
	private int columnWidth = 250;// 每个item的宽度
	private int itemHeight = 0;
	private int rowCountPerScreen = 3;
	private int cols = 4;// 当前总列数
	private ArrayList<Integer> colYs = new ArrayList<Integer>();
	private ArrayList<View> currentViews = new ArrayList<View>();
	private LayoutInflater mInflater;
	private RelativeLayout rootView;
	private FinalBitmap fb;
	private List<DuitangInfo> infos = new ArrayList<DuitangInfo>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
		init();
		new ContentTask(this)
				.execute("http://www.duitang.com/album/1733789/masn/p/1/24/");

	}

	private void init() {
		rootView = (RelativeLayout) this.findViewById(R.id.rootView);
		mInflater = getLayoutInflater();
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		if (display.getOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
			rowCountPerScreen=3;
		} else {
			rowCountPerScreen=6;
		}
		columnWidth = width / COLUMNCOUNT;
		itemHeight = height / rowCountPerScreen;
		fb = FinalBitmap.create(this);
		for (int i = 0; i < 4; i++) {
			colYs.add(0);
		}
	}

	private void addView(View view, String uri) {
		placeBrick(view);
		ImageView picView = (ImageView) view.findViewById(R.id.imageView);

		rootView.addView(view);
		fb.display(picView, uri);
	}

	// 布局算法

	/**
	 * 原理：动态规划
	 * 
	 * @param view
	 */
	private void placeBrick(View view) {
		LayoutParams brick = (LayoutParams) view.getLayoutParams();
		int groupCount, j, colSpan;
		List<Integer> groupY = new ArrayList<Integer>();
		List<Integer> groupColY = new ArrayList<Integer>();
		colSpan = (int) Math.ceil(brick.width / this.columnWidth);// 计算跨几列
		colSpan = Math.min(colSpan, this.cols);// 取最小的列数
		Log.d("VideoShowActivity", "colSpan:" + colSpan);
		if (colSpan == 1) {
			groupY = this.colYs;
		} else {// 说明有跨列
			groupCount = this.cols + 1 - colSpan;// 添加item的时候列可以填充的列index
			for (j = 0; j < groupCount; j++) {
				// TODO:此处需要一个动态选择算法
				groupColY = this.colYs.subList(j, j + colSpan);
				groupY.add(j, Collections.max(groupColY));// 选择几个可添加的位置
			}
		}
		int minimumY;

		minimumY = Collections.min(groupY);
		int shortCol = 0;
		int len = groupY.size();
		for (int i = 0; i < len; i++) {
			if (groupY.get(i) == minimumY) {
				shortCol = i;
				break;
			}
		}
		int pTop = minimumY;// 这是放置的Top
		int pLeft = this.columnWidth * shortCol;// 放置的left
		Log.d("VideoShowActivity", "pTop:" + pTop + ",pLeft:" + pLeft);
		android.widget.RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				brick.width, brick.height);
		params.leftMargin = pLeft;
		params.topMargin = pTop;
		view.setLayoutParams(params);
		int setHeight = minimumY + brick.height, setSpan = this.cols + 1 - len;
		for (int i = 0; i < setSpan; i++) {
			this.colYs.set(shortCol + i, setHeight);
		}
	}

	private class ContentTask extends
			AsyncTask<String, Integer, List<DuitangInfo>> {

		private Context mContext;

		public ContentTask(Context context) {
			super();
			mContext = context;
		}

		@Override
		protected List<DuitangInfo> doInBackground(String... params) {
			try {
				return parseNewsJSON(params[0]);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<DuitangInfo> result) {
			// 动态计算ListView
			if (result != null) {
				Random r = new Random();

				for (int i = 0; i < result.size(); i++) {
					View v = mInflater.inflate(R.layout.weibo_text_item, null);
					int nextInt = r.nextInt(50);
					if (nextInt > 40) {
						android.widget.RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
								columnWidth * 2, itemHeight * 2);
						v.setLayoutParams(params);
					} else if (nextInt > 30) {
						android.widget.RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
								columnWidth, itemHeight * 2);

						v.setLayoutParams(params);
					} else if (nextInt > 25) {
						android.widget.RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
								columnWidth * 2, itemHeight);

						v.setLayoutParams(params);
					} else {
						android.widget.RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
								columnWidth, itemHeight);

						v.setLayoutParams(params);
					}
					addView(v, result.get(i).getIsrc());
				}
			}
		}

		@Override
		protected void onPreExecute() {
		}

		public List<DuitangInfo> parseNewsJSON(String url) throws IOException {
			List<DuitangInfo> duitangs = new ArrayList<DuitangInfo>();
			String json = "";
			if (Helper.checkConnection(mContext)) {
				try {
					json = Helper.getStringFromUrl(url);

				} catch (IOException e) {
					Log.e("IOException is : ", e.toString());
					e.printStackTrace();
					return duitangs;
				}
			}
			Log.d("MainActiivty", "json:" + json);

			try {
				if (null != json) {
					JSONObject newsObject = new JSONObject(json);
					JSONObject jsonObject = newsObject.getJSONObject("data");
					JSONArray blogsJson = jsonObject.getJSONArray("blogs");

					for (int i = 0; i < blogsJson.length(); i++) {
						JSONObject newsInfoLeftObject = blogsJson
								.getJSONObject(i);
						DuitangInfo newsInfo1 = new DuitangInfo();
						newsInfo1
								.setAlbid(newsInfoLeftObject.isNull("albid") ? ""
										: newsInfoLeftObject.getString("albid"));
						newsInfo1
								.setIsrc(newsInfoLeftObject.isNull("isrc") ? ""
										: newsInfoLeftObject.getString("isrc"));
						newsInfo1.setMsg(newsInfoLeftObject.isNull("msg") ? ""
								: newsInfoLeftObject.getString("msg"));
						duitangs.add(newsInfo1);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return duitangs;
		}
	}

	@Override
	public void onBottom() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAutoScroll(int l, int t, int oldl, int oldt) {
		// TODO Auto-generated method stub

	}

}
