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

import java.security.InvalidParameterException;

import org.francho.apps.zgzpolen.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class PollenImage extends ImageView {

	public PollenImage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setDefaultImage(); 
	}

	public PollenImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDefaultImage();
	}

	public PollenImage(Context context) {
		super(context);
		setDefaultImage();
	}

	/**
	 * Load the plant image from the resources
	 * if no one is available, load the default
	 * 
	 * @param plantName
	 */
	public void setPlant(String plantName) {
		try {
			int resDrawable = getPlantDrawable(getContext(), plantName);
			if(resDrawable == 0) {
				throw new InvalidParameterException();
			}
			setImageResource(resDrawable);
			setAlpha(255);
		} catch(Exception e) {
			setDefaultImage();
		}
	}
	
	/**
	 * from plant name to resource id
	 * 
	 * @param context
	 * @param plantName
	 * @return
	 */
	public static int getPlantDrawable(Context context, String plantName) {
		String defPackage = context.getPackageName();
		String name = "plant_" + plantName.toLowerCase();
		String defType = "drawable";
		
		return context.getResources().getIdentifier(name, defType, defPackage);
	}
	
	private void setDefaultImage() {
		setAlpha(75);
		setImageResource(R.drawable.unknown_plant);
	}
}
