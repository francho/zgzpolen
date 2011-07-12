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
package org.francho.apps.zgzpolen.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.francho.apps.zgzpolen.provider.PollenContract.Pollen;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentProviderOperation;

public class ZgzPollenXmlParser {

	private Long polenDate = null;

	public Long getPolenDate() {
		return polenDate;
	}

	public void setPolenDate(String dateString) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		try {
			polenDate = dateFormat.parse(dateString).getTime();
		} catch (ParseException e) {
			polenDate = null;
		}
		
	}

	public ArrayList<ContentProviderOperation> parse(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		final ArrayList<ContentProviderOperation> batch = new ArrayList();

		int type;

		ContentProviderOperation.Builder builder;

		// delete the old records
		builder = ContentProviderOperation.newDelete(Pollen.getPollenUri());
		batch.add(builder.build());

		// save the new ones
		while ((type = parser.next()) != XmlPullParser.END_DOCUMENT) {
			builder = ContentProviderOperation.newInsert(Pollen.getPollenUri());

			String tag = parser.getName();

			if (type == XmlPullParser.START_TAG) {
				if (PollenTags.POLLEN.equalsIgnoreCase(tag)) {
					setPolenDate(parser.getAttributeValue(null, PollenTags.ATT_DATE));
				} else if(PollenTags.PLANT.equalsIgnoreCase(tag)) {
					// Process single node
					final PollenAlert entry = PollenAlert.fromParser(parser);

					builder.withValue(Pollen.PLANT, entry.get(PollenTags.NAME));
					
					final String levelName = entry.get(PollenTags.LEVEL);
					builder.withValue(Pollen.LEVEL, levelName);
					builder.withValue(Pollen.LEVEL_NUMBER, getLevelNumber(levelName));

					batch.add(builder.build());
				}
			}
		}

		return batch;
	}

	/**
	 * Convert from level name to level number
	 * 
	 * @param levelName
	 * @return
	 */
	private Integer getLevelNumber(String levelName) {
		int level = 0;
		if(levelName.equalsIgnoreCase("Nulo")) {
			level = 0;
		} else if(levelName.equalsIgnoreCase("Bajo")) {
			level = 1;
		} else if(levelName.equalsIgnoreCase("Moderado")) {
			level = 2;
		} else if(levelName.equalsIgnoreCase("Alto")) {
			level = 3;
		}
		return level;
	}

	/**
	 * Parse each node
	 * 
	 * @author francho
	 * 
	 */
	static class PollenAlert extends HashMap<String, String> {

		public static PollenAlert fromParser(XmlPullParser parser)
				throws XmlPullParserException, IOException {

			PollenAlert pollenAlert = new PollenAlert();

			final int depth = parser.getDepth();

			String tag = null;
			int type;
			while (((type = parser.next()) != XmlPullParser.END_TAG || parser
					.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {

				switch (type) {
				case XmlPullParser.START_TAG:
					tag = parser.getName();
					break;
				case XmlPullParser.TEXT:
					if (PollenTags.NAME.equalsIgnoreCase(tag)) {
						pollenAlert.put(PollenTags.NAME, parser.getText());
					} else if (PollenTags.LEVEL.equalsIgnoreCase(tag)) {
						pollenAlert.put(PollenTags.LEVEL, parser.getText());
					}
					break;
				case XmlPullParser.END_TAG:
					tag = null;
					break;
				}
			}

			return pollenAlert;

		}
	}

	/**
	 * Tags used in the xml
	 * 
	 * @author francho
	 * 
	 */
	interface PollenTags {
		String POLLEN = "polen";
		String PLANT = "planta";
		String NAME = "nombre";
		String LEVEL = "valor";
		
		String ATT_DATE = "fecha";
	}
}
