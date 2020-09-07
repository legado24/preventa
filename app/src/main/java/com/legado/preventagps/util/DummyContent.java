package com.legado.preventagps.util;


import com.legado.preventagps.R;
import com.legado.preventagps.modelo.DummyModel;

import java.util.ArrayList;
import java.util.Random;

public class DummyContent {
	
	/* This method gives us just a dummy content - array list
	 * of ImageGalleryCategoryModels. Every model has id that is
	 * need for some classes (e.g. DefaultAdapter.java).
	 * Favourites are randomly chosen to be true or false.
	 * Last category is randomly added to the list so you could
	 * see when there are even or odd numbers of categories in
	 * ImageGalleryActivity.
	 */


	public static ArrayList<DummyModel> getDummyModelList() {
		ArrayList<DummyModel> list = new ArrayList<>();

		list.add(new DummyModel(0, "http://pengaja.com/uiapptemplate/newphotos/profileimages/0.jpg", "Isaac Reid", R.string.fontello_heart_empty));
		list.add(new DummyModel(1, "http://pengaja.com/uiapptemplate/newphotos/profileimages/1.jpg", "Jason Graham", R.string.fontello_heart_empty));
		list.add(new DummyModel(2, "http://pengaja.com/uiapptemplate/newphotos/profileimages/2.jpg", "Abigail Ross", R.string.fontello_heart_empty));
		list.add(new DummyModel(3, "http://pengaja.com/uiapptemplate/newphotos/profileimages/3.jpg", "Justin Rutherford", R.string.fontello_heart_empty));
		list.add(new DummyModel(4, "http://pengaja.com/uiapptemplate/newphotos/profileimages/4.jpg", "Nicholas Henderson", R.string.fontello_heart_empty));
		list.add(new DummyModel(5, "http://pengaja.com/uiapptemplate/newphotos/profileimages/5.jpg", "Elizabeth Mackenzie", R.string.fontello_heart_empty));
		list.add(new DummyModel(6, "http://pengaja.com/uiapptemplate/newphotos/profileimages/6.jpg", "Melanie Ferguson", R.string.fontello_heart_empty));
		list.add(new DummyModel(7, "http://pengaja.com/uiapptemplate/newphotos/profileimages/7.jpg", "Fiona Kelly", R.string.fontello_heart_empty));
		list.add(new DummyModel(8, "http://pengaja.com/uiapptemplate/newphotos/profileimages/8.jpg", "Nicholas King", R.string.fontello_heart_empty));
		list.add(new DummyModel(9, "http://pengaja.com/uiapptemplate/newphotos/profileimages/9.jpg", "Victoria Mitchell", R.string.fontello_heart_empty));
		list.add(new DummyModel(10, "http://pengaja.com/uiapptemplate/newphotos/profileimages/10.jpg", "Sophie Lyman", R.string.fontello_heart_empty));
		list.add(new DummyModel(11, "http://pengaja.com/uiapptemplate/newphotos/profileimages/11.jpg", "Carl Ince", R.string.fontello_heart_empty));
		list.add(new DummyModel(12, "http://pengaja.com/uiapptemplate/newphotos/profileimages/12.jpg", "Michelle Slater", R.string.fontello_heart_empty));
		list.add(new DummyModel(13, "http://pengaja.com/uiapptemplate/newphotos/profileimages/13.jpg", "Ryan Mathis", R.string.fontello_heart_empty));
		list.add(new DummyModel(14, "http://pengaja.com/uiapptemplate/newphotos/profileimages/14.jpg", "Julia Grant", R.string.fontello_heart_empty));
		list.add(new DummyModel(15, "http://pengaja.com/uiapptemplate/newphotos/profileimages/15.jpg", "Hannah Martin", R.string.fontello_heart_empty));
		
		return list;
	}

}
