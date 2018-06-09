package mn.keyboard.mongoliansmartkeyboard;

import android.content.Context;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;

public class MNSmartKeyboardView extends KeyboardView {
	
	static final int KEYCODE_OPTIONS = -100;
	static final int KEYCODE_MNSmart = -10;
	static final int KEYCODE_ABC = -11;
	static final int KEYCODE_SYMBOL = -12;
	static final int KEYCODE_MNG = -13;
	
	static final int KEYCODE_MNSmart_1 = -21;
	static final int KEYCODE_MNSmart_2 = -31;
	static final int KEYCODE_MNSmart_3 = -41;
	static final int KEYCODE_MNSmart_4 = -51;
	static final int KEYCODE_MNSmart_5 = -61;

	public MNSmartKeyboardView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public MNSmartKeyboardView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected boolean onLongPress(Key popupKey) {
		if (popupKey.codes[0] == 10) {
            getOnKeyboardActionListener().onKey(KEYCODE_OPTIONS, null);
            return true;
        }
		
		if (popupKey.codes[0] == KEYCODE_ABC) {
			getOnKeyboardActionListener().onKey(KEYCODE_OPTIONS, null);
			return true;
		}
		if (popupKey.codes[0] == KEYCODE_MNG) {
			getOnKeyboardActionListener().onKey(KEYCODE_OPTIONS, null);
			return true;
		}		
		if (popupKey.codes[0] == KEYCODE_SYMBOL) {
			getOnKeyboardActionListener().onKey(KEYCODE_OPTIONS, null);
			return true;
		}
		
		if (popupKey.codes[0] == KEYCODE_MNSmart) {
			getOnKeyboardActionListener().onKey(KEYCODE_OPTIONS, null);
			return true;
		}
		
		
		return super.onLongPress(popupKey);
	}

}
