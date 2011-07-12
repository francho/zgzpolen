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

import android.net.Uri;
import android.provider.BaseColumns;

public class PollenContract {
	public static final String CONTENT_AUTHORITY = "org.francho.zgzpolen.pollen";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    static final String PATH_POLLEN = "polen";
    
    
    public interface PolenColumns {
    	String PLANT = "plant";
    	String LEVEL = "level";
    	String LEVEL_NUMBER = "level_number";
    }
    
    public static class Pollen implements PolenColumns, BaseColumns {
    	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/org.francho.zgzpolen.pollen";
    	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/org.francho.zgzpolen.pollen";
		

		private Pollen() {}
		
		public static Uri getPollenUri() {
			return BASE_CONTENT_URI.buildUpon().appendPath(PATH_POLLEN).build();
		}

		public static Uri getPollenUri(long id) {
			return BASE_CONTENT_URI.buildUpon().appendPath(PATH_POLLEN).appendPath(""+id).build();
		}
    }
}
