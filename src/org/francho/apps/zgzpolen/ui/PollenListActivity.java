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
import org.francho.apps.zgzpolen.service.PollenService;
import org.francho.apps.zgzpolen.widget.PollenAdapter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class PollenListActivity extends Activity implements OnItemClickListener {
	
//    private static final int LAYOUT = R.layout.pollen_list;
    private static final int LAYOUT = R.layout.pollen_grid;
//    private static final int LAYOUT = R.layout.pollen_gallery;
    private static final int LAYOUT_ITEM = R.layout.pollen_item2;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(LAYOUT);
        
        Intent service = new Intent(this, PollenService.class);
		startService(service);
		
		initAdapter();
    } 

	private void initAdapter() {
		AdapterView adapterView = (AdapterView) findViewById(android.R.id.list);
		
		final String[] projection = null;
		final String selection = null;
		final String[] selectionArgs = null;
		final String sortOrder = Pollen.LEVEL_NUMBER + " DESC, " + Pollen.PLANT + " ASC";
		
		Cursor cursor = managedQuery(Pollen.getPollenUri(), projection, selection, selectionArgs, sortOrder);
		
		PollenAdapter adapter = new PollenAdapter(this, cursor, LAYOUT_ITEM);
		
		adapterView.setAdapter(adapter);
		adapterView.setEmptyView(findViewById(android.R.id.empty));
		adapterView.setOnItemClickListener(this);
	}

	public void onItemClick(AdapterView<?> l, View v, int position, long id) {
		final Uri data = Pollen.getPollenUri(id);
		
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(data);
		
		startActivity(intent);
	}

	
	
	
}