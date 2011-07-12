/**
 *  ZgzPolen - Zaragoza's Pollen status
 *  Copyright (C) 2011 Francho Joven - http://francho.org
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.francho.apps.zgzpolen.ui;

import org.francho.apps.zgzpolen.R;
import org.francho.apps.zgzpolen.provider.PollenContract.Pollen;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

public class PollenInfoActivity extends Activity {
	private TextView mPlantName;
	private TextView mPlantLevel;
	private WebView mPlantMore;

	/**
	 * onCreate
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.pollen_info);
		
		mPlantName = (TextView) findViewById(R.id.pollen_name);
		mPlantLevel = (TextView) findViewById(R.id.pollen_level);
		mPlantMore = (WebView) findViewById(R.id.pollen_moreinfo);
	}
	
	/**
	 * onStart
	 */
	@Override
	protected void onStart() {
		super.onStart();
		
		final Uri data = getIntent().getData();
		loadData(data);
	}

	/**
	 * Load the info from database and show it into the views
	 */
	private void loadData(Uri data) {
		String[] proyection = new String[]{ 
				Pollen.LEVEL, // 0
				Pollen.PLANT  // 1
			};
		Cursor cursor = managedQuery(data, proyection, null, null, null);
		
		if(cursor.moveToFirst()) {
			mPlantName.setText(cursor.getString(0));
			mPlantLevel.setText(cursor.getString(1));
			
			mPlantMore.loadUrl(getWikipediaUrl(cursor.getString(1)));
		}
		
		cursor.close();
	}

	/**
	 * 
	 * @param name plant name
	 * @return an url from a webpage with more info
	 */
	private String getWikipediaUrl(String name) {
		return "http://es.m.wikipedia.org/wiki/" + name;
	}

}
