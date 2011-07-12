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
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.francho.apps.zgzpolen.provider.PollenContract;
import org.francho.apps.zgzpolen.provider.PollenContract.Pollen;
import org.francho.apps.zgzpolen.utils.HttpHelper;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.RemoteException;
import android.text.format.DateUtils;
import android.util.Log;

public class PollenService extends IntentService {
	private static final String POLEN_XML_URL = "http://www.zaragoza.es/datos/movil/include/polen.xml";

	private static final String POLLEN_SERVICE_PREFS = "PollenServicePreferences";
	private static final String PREF_POLLEN_DATE = "pollenDate";
	
	public static String TAG = "PollenUpdateService";
	private XmlPullParserFactory sFactory;
	
	public PollenService()	 {
		super(TAG);
		
		
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		Log.d(TAG, "service started");
		
		if(isUpdated()) {
			Log.d(TAG, "skip: today the database has been updated yet");
			return;
		}
		
		try {
			final HttpGet httpRequest = new HttpGet(POLEN_XML_URL);
			execute(httpRequest);
		} catch (PollenServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Log.d(TAG, "service ended");
	}
	

	/**
     * Execute this {@link HttpUriRequest}, passing a valid response and save it in the database
     *
	 * @param request
	 * @throws PollenServiceException
	 */
    public void execute(HttpUriRequest request) throws PollenServiceException {
        try {
            final HttpResponse resp = HttpHelper.getHttpClient(this).execute(request);
            final int status = resp.getStatusLine().getStatusCode();
            if (status != HttpStatus.SC_OK) {
                throw new PollenServiceException("Unexpected server response " + resp.getStatusLine()
                        + " for " + request.getRequestLine());
            }

            final InputStream input = resp.getEntity().getContent();
            
            try {
            	
            	final XmlPullParser parser =  XmlPullParserFactory.newInstance().newPullParser();
            	parser.setInput(input, null);
               	
                ZgzPollenXmlParser pollenParser = new ZgzPollenXmlParser();
                final ArrayList<ContentProviderOperation> operations = pollenParser.parse(parser);
                
                getContentResolver().applyBatch(PollenContract.CONTENT_AUTHORITY, operations);
                
                setPollenDate(pollenParser.getPolenDate());
                
            	
            } catch (XmlPullParserException e) {
                throw new PollenServiceException("Malformed response for " + request.getRequestLine(), e);
            } catch (RemoteException e) {
            	throw new PollenServiceException("Problem parsing response for " + request.getRequestLine(), e);
			} catch (OperationApplicationException e) {
				throw new PollenServiceException("Problem saving response for " + request.getRequestLine(), e);
			} finally {
                if (input != null) input.close();
            }
        } catch (PollenServiceException e) {
            throw e;
        } catch (IOException e) {
            throw new PollenServiceException("Problem reading remote response for "
                    + request.getRequestLine(), e);
        }
    }
    
    /**
     * save the current xml date (to see later if the database should be updated)
     * 
     * @param polenDate
     */
    private void setPollenDate(Long polenDate) {
    	final SharedPreferences prefs = getSharedPreferences(POLLEN_SERVICE_PREFS, Context.MODE_PRIVATE);
    	
    	final Editor editor = prefs.edit();
    	editor.putLong(PREF_POLLEN_DATE, polenDate);
    	editor.commit();
	}

	/**
     * is the database udpdated?
     * 
     * @return
     */
	private boolean isUpdated() {
		Cursor c = getContentResolver().query(Pollen.getPollenUri(), null, null, null, null);
		try {
			if(c.getCount() < 1) {
				return false;
			}
		} finally {
			c.close();
		}
		
		final SharedPreferences prefs = getSharedPreferences(POLLEN_SERVICE_PREFS, Context.MODE_PRIVATE);
		long lastTimeStamp = prefs.getLong(PREF_POLLEN_DATE, 0);
		
		Log.d(TAG, "" + System.currentTimeMillis() + " " + lastTimeStamp + ":" + (System.currentTimeMillis()-lastTimeStamp));
		
		return DateUtils.isToday(lastTimeStamp);
	}

}
