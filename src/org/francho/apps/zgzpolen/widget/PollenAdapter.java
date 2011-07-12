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
package org.francho.apps.zgzpolen.widget;

import org.francho.apps.zgzpolen.R;
import org.francho.apps.zgzpolen.provider.PollenContract.Pollen;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class PollenAdapter extends CursorAdapter {

	private int resourceItem;

	public PollenAdapter(Context context, Cursor c, int resItem) {
		super(context, c);
		
		this.resourceItem = resItem;
	}

	public PollenAdapter(Context context, Cursor c) {
		super(context, c);
	}

	/**
	 * bind the data into the views
	 */
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		final ViewHolder holder = (ViewHolder) view.getTag();
		
		final String plantName = cursor.getString(cursor.getColumnIndex(Pollen.PLANT));
		final String plantLevel = cursor.getString(cursor.getColumnIndex(Pollen.LEVEL));
		final int plantLevelNumber = cursor.getInt(cursor.getColumnIndex(Pollen.LEVEL_NUMBER));
		
		holder.image.setPlant(plantName);
		holder.name.setText(plantName);
		holder.level.setText(plantLevel);
		
		holder.level.setTextColor(getLevelTextColor(context, plantLevelNumber));
		
		
		final Drawable bg = view.getBackground();
		bg.setLevel(plantLevelNumber);
	}

	private int getLevelTextColor(Context context, int plantLevel) {
		final Resources res = context.getResources();
		
		switch(plantLevel) {
		case 0:
			return res.getColor(R.color.text_level_0);
		case 1:
			return res.getColor(R.color.text_level_1);
		case 2:
			return res.getColor(R.color.text_level_2);
		case 3:
			return res.getColor(R.color.text_level_3);
		case 4:
			return res.getColor(R.color.text_level_4);
		default:
			return res.getColor(R.color.text);
		}
	}

	/**
	 * Create a new view and cached his views
	 */
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(resourceItem, parent, false);
		
		ViewHolder holder = new ViewHolder();
		holder.image = (PollenImage) v.findViewById(R.id.item_image);
		holder.name = (TextView) v.findViewById(R.id.item_name);
		holder.level = (TextView) v.findViewById(R.id.item_level);
		
		
		v.setTag(holder);
		
		bindView(v, context, cursor);
		return v;
	}
	
	/**
	 * cache the views 
	 * @author francho
	 *
	 */
	class ViewHolder {
		public TextView name;
		public TextView level;
		public PollenImage image;
	}

}
