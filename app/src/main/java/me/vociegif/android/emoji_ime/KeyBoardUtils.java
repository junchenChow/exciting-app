package me.vociegif.android.emoji_ime;

import android.text.InputType;

public class KeyBoardUtils {

	public static boolean isEmailVariation(final int variation) {
		return variation == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
				|| isWebEmailAddressVariation(variation);
	}

	private static boolean isWebEmailAddressVariation(int variation) {
		return variation == InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS;
	}

}
