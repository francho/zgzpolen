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
import org.francho.apps.zgzpolen.widget.PollenImage;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

public class PollenWidget extends AppWidgetProvider {
	
	protected String getWidgetPlantName() {
		// @TODO get from config
		return "pino";
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		// Build the widget update for today
		RemoteViews updateViews = buildUpdate(context);

		if (updateViews == null) {
			return;
		}

		// Push update for this widget to the home screen
		ComponentName thisWidget = new ComponentName(context,
				PollenWidget.class);
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		manager.updateAppWidget(thisWidget, updateViews);
	}

	

	public RemoteViews buildUpdate(Context context) {
		final ContentResolver contentResolver = context.getContentResolver();

		final String[] projection = new String[] { 
				Pollen.PLANT, // 0
				Pollen.LEVEL // 1
		};

		final String selection = Pollen.PLANT + " LIKE ?";
		final String[] selectionArgs = new String[] { getWidgetPlantName() };
		final Cursor cursor = contentResolver.query(Pollen.getPollenUri(),
				projection, selection, selectionArgs, null);

		RemoteViews updateViews = null;

		try {
			if (!cursor.moveToFirst()) {
				return null;
			}

			final String plantName = cursor.getString(0);
			final String plantLevel = cursor.getString(1);
			
			updateViews = new RemoteViews(context.getPackageName(),
					R.layout.widget);

			updateViews.setTextViewText(R.id.widget_name, plantName);
			updateViews.setTextViewText(R.id.widget_level, plantLevel);
			updateViews.setImageViewResource(R.id.widget_image,
					PollenImage.getPlantDrawable(context, plantName));
			
			// Onclick
			Intent intent = new Intent(context, PollenListActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			updateViews.setOnClickPendingIntent(R.id.widget_image, pendingIntent);
			
		} finally {
			cursor.close();
		}
		
		return updateViews;
	}

}