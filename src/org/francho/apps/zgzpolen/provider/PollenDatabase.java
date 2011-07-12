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
package org.francho.apps.zgzpolen.provider;


import org.francho.apps.zgzpolen.provider.PollenContract.Pollen;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PollenDatabase extends SQLiteOpenHelper {

	private static final String DB_FILE = "polen.db";
	private static final int VERSION = 3;

	public PollenDatabase(Context context) {
		super(context, DB_FILE, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		if(db.isReadOnly()) { db=getWritableDatabase(); }
		
		createTablePolen(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(db.isReadOnly()) { db=getWritableDatabase(); }
		
		switch(oldVersion) {
		case 2:
			db.execSQL("ALTER TABLE " + Tables.POLLEN + " ADD " + Pollen.LEVEL_NUMBER + " INTEGER " );
			break;
		}
	}
	
	private void createTablePolen(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + Tables.POLLEN + "(" +
				Pollen._ID + " integer PRIMARY KEY AUTOINCREMENT, " +
				Pollen.PLANT + " String, " +
				Pollen.LEVEL + " String, " +
				Pollen.LEVEL_NUMBER + " integer " +
				")"
		);
	}
	
	interface Tables {
		String POLLEN = "polen";
	}

}
