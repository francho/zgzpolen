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
import org.francho.apps.zgzpolen.provider.PollenDatabase.Tables;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class PollenProvider extends ContentProvider {
	/** helper to connect to the database **/
	private PollenDatabase mOpenHelper;
	
	/** matcher for the supported uris **/
	private static final UriMatcher mUriMatcher = buildUriMatcher();
	
	/** internal uri type codes **/
	private static final int TYPE_POLLEN = 500;
	private static final int TYPE_POLLEN_BY_ID = 501;


	/**
	 * Generate the list of valid uris
	 * 
	 * @return
	 */
	private static UriMatcher buildUriMatcher() {
		final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		
		uriMatcher.addURI(PollenContract.CONTENT_AUTHORITY, PollenContract.PATH_POLLEN, TYPE_POLLEN);
		uriMatcher.addURI(PollenContract.CONTENT_AUTHORITY, PollenContract.PATH_POLLEN + "/*", TYPE_POLLEN_BY_ID);
		
		return uriMatcher;
	}
	
	@Override
	public boolean onCreate() {
		final Context context = getContext();
		mOpenHelper = new PollenDatabase(context);
		return true;
	}
	
	/**
	 * Return the content type for an uri
	 */
	@Override
	public String getType(Uri uri) {
		final int match = mUriMatcher.match(uri);
		switch (match) {
		case TYPE_POLLEN: 
			return Pollen.CONTENT_TYPE;
		case TYPE_POLLEN_BY_ID: 
			return Pollen.CONTENT_ITEM_TYPE;	
		default:
            throw new UnsupportedOperationException("Unknown uri: " + uri);
		}	
	}

	/**
	 * insert a row into database
	 */
	@Override
	public Uri insert(Uri uri, ContentValues contentValues) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = mUriMatcher.match(uri);
		
		switch (match) {
		case TYPE_POLLEN: 
			final long id = db.insertOrThrow(Tables.POLLEN, null, contentValues);
			return Pollen.getPollenUri(id);
		default: 
			throw new UnsupportedOperationException("unsupported uri: " + uri);
		}
	}

	/**
	 * delete one or more rows from database
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = mUriMatcher.match(uri);
		
		switch (match) {
		case TYPE_POLLEN: 
			return db.delete(Tables.POLLEN, selection, selectionArgs);
		default: 
			throw new UnsupportedOperationException("unsupported uri: " + uri);
		}
	}

	/**
	 * Selects...
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		
		final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		final SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();

		final int match = mUriMatcher.match(uri);
		
		switch(match) {
			case TYPE_POLLEN_BY_ID:
				sqlBuilder.appendWhere(Pollen._ID + " = " + uri.getPathSegments().get(1) );
			case TYPE_POLLEN:
				sqlBuilder.setTables(Tables.POLLEN);;
				return sqlBuilder.query(db, projection, selection, selectionArgs,
						null, null, sortOrder);
			default: 
				throw new UnsupportedOperationException("unsupported uri: " + uri);
		}
	}

	@Override
	public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException("unsupported uri: " + uri);
	}

}
