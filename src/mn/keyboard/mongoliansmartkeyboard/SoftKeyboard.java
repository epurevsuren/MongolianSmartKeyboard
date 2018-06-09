package mn.keyboard.mongoliansmartkeyboard;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.method.MetaKeyKeyListener;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

public class SoftKeyboard extends InputMethodService implements KeyboardView.OnKeyboardActionListener {
	
	static final boolean DEBUG = false;
	
	private StringBuilder mComposing;
	private long mLastShiftTime;
	private boolean mCapsLock;
	private String mWordSeparators;
	private boolean mPredictionOn;
	private KeyboardView mInputView;
	private MNSmartKeyboard mQwertyKeyboard;
	private MNSmartKeyboard mngKeyboard;
	private MNSmartKeyboard mSymbolsKeyboard;
	private MNSmartKeyboard mSymbolsShiftedKeyboard;
	private long mMetaState;
	private boolean mCompletionOn;
	private Resources mResources;
	private CandidateView mCandidateView;
	private CompletionInfo[] mCompletions;
	private MNSmartKeyboard mCurKeyboard;
	private MNSmartKeyboard mMNSmartKeyboarda1;
	private MNSmartKeyboard mMNSmartKeyboarda2;
	private MNSmartKeyboard mMNSmartKeyboarda3;
	private MNSmartKeyboard mMNSmartKeyboarda4;
	private MNSmartKeyboard mMNSmartKeyboardb1;
	private MNSmartKeyboard mMNSmartKeyboardb2;
	private MNSmartKeyboard mMNSmartKeyboardc1;
	private MNSmartKeyboard mMNSmartKeyboardc2;
	private MNSmartKeyboard mMNSmartKeyboardc3;
	private MNSmartKeyboard mMNSmartKeyboardc4;
	private MNSmartKeyboard mMNSmartKeyboardc5;
	private MNSmartKeyboard mMNSmartKeyboardd1;
	private MNSmartKeyboard mMNSmartKeyboardd2;
	private MNSmartKeyboard mMNSmartKeyboardd3;
	private MNSmartKeyboard mMNSmartKeyboarde1;
	private MNSmartKeyboard mMNSmartKeyboarde2;
	private MNSmartKeyboard mMNSmartKeyboarde3;
	private MNSmartKeyboard mMNSmartKeyboarde4;
	private int mLastDisplayWidth;
	
	public SoftKeyboard() {
		this.mComposing = new StringBuilder();
	}
	
	private void checkToggleCapsLock() {
		long now = System.currentTimeMillis();
		if (this.mLastShiftTime + 800 > now) {
			this.mCapsLock = !this.mCapsLock;
			this.mLastShiftTime = 0;
		} else {
			this.mLastShiftTime = now;
		}
	}
	
	private void commitTyped(InputConnection inputConnection) {
		if (this.mComposing.length() > 0) {
			inputConnection.commitText(this.mComposing, 1);		// mComposing.length()
			mComposing.setLength(0);
			this.updateCandidates();
		}
    }
	
	private String getWordSeparators() {
		return this.mWordSeparators;
	}

	public void handleBackspace() {
		final int length = this.mComposing.length();
		if (length > 1) {
			this.mComposing.delete(length - 1, length);
			this.getCurrentInputConnection().setComposingText(this.mComposing, 1);
			this.updateCandidates();
		} else if (length > 0) {
			this.mComposing.setLength(0);
			this.getCurrentInputConnection().commitText("", 0);
			this.updateCandidates();
		} else {
			this.keyDownUp(KeyEvent.KEYCODE_DEL);
		}
		this.updateShiftKeyState(this.getCurrentInputEditorInfo());
	}
	
	private void handleCharacter(int primaryCode, int[] keyCodes) {
		if (isInputViewShown()) {
			if (this.mInputView.isShifted()) {
				primaryCode = Character.toUpperCase(primaryCode);
			}
		}
		if (this.isAlphabet(primaryCode) && this.mPredictionOn) {
			this.mComposing.append((char) primaryCode);
			this.getCurrentInputConnection().setComposingText(this.mComposing, 1);
            this.updateShiftKeyState(this.getCurrentInputEditorInfo());
            this.updateCandidates();
        } else {
            this.mComposing.append((char) primaryCode);
            this.getCurrentInputConnection().setComposingText(this.mComposing, 1);
        }
    }
	
	private void handleClose() {
		commitTyped(this.getCurrentInputConnection());
		this.requestHideSelf(0);
		this.mInputView.closing();
	}
	
	private void handleShift() {
		if (this.mInputView == null) {
			return;
		}

		Keyboard currentKeyboard = this.mInputView.getKeyboard();
		if (this.mQwertyKeyboard == currentKeyboard) {
        	
			this.checkToggleCapsLock();
			this.mInputView.setShifted(this.mCapsLock || !this.mInputView.isShifted());
            
		}
		else if (this.mngKeyboard == currentKeyboard) {
        	
			this.checkToggleCapsLock();
			this.mInputView.setShifted(this.mCapsLock || !this.mInputView.isShifted());
            
		}
		else if (currentKeyboard == this.mSymbolsKeyboard) {
        	
			this.mSymbolsKeyboard.setShifted(true);
			this.mInputView.setKeyboard(this.mSymbolsShiftedKeyboard);
			this.mSymbolsShiftedKeyboard.setShifted(true);
            
		} else if (currentKeyboard == this.mSymbolsShiftedKeyboard) {
        	
			this.mSymbolsShiftedKeyboard.setShifted(false);
			this.mInputView.setKeyboard(this.mSymbolsKeyboard);
			this.mSymbolsKeyboard.setShifted(false);
            
		}
	}
	
	private boolean isAlphabet(int code) {
		return Character.isLetter(code);
    }
	
	private void keyDownUp(int keyEventCode) {
        getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }
	
	private void sendKey(int keyCode) {
		switch (keyCode) {
			case '\n':
				keyDownUp(KeyEvent.KEYCODE_ENTER);
				break;
			default:
				if (keyCode >= '0' && keyCode <= '9') {
					keyDownUp(keyCode - '0' + KeyEvent.KEYCODE_0);
				} else {
					getCurrentInputConnection().commitText(String.valueOf((char) keyCode), 1);
				}
				break;
		}
    }
	
	private void showOptionsMenu() {
		((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).showInputMethodPicker();
	}
	
	private boolean translateKeyDown(int keyCode, KeyEvent event) {
		this.mMetaState = MetaKeyKeyListener.handleKeyDown(this.mMetaState, keyCode, event);
		int c = event.getUnicodeChar(MetaKeyKeyListener.getMetaState(this.mMetaState));
        this.mMetaState = MetaKeyKeyListener.adjustMetaAfterKeypress(mMetaState);
        InputConnection ic = this.getCurrentInputConnection();
        
        if (c == 0 || ic == null) {
            return false;
        }

		boolean dead = false;

		if ((c & KeyCharacterMap.COMBINING_ACCENT) != 0) {
            dead = true;
            c = c & KeyCharacterMap.COMBINING_ACCENT_MASK;
        }

        if (this.mComposing.length() > 0) {
            char accent = this.mComposing.charAt(this.mComposing.length() - 1);
            int composed = KeyEvent.getDeadChar(accent, c);

            if (composed != 0) {
                c = composed;
                this.mComposing.setLength(this.mComposing.length() - 1);
            }
        }

        this.onKey(c, null);

        return true;
    }
	
	private void updateCandidates() {
		if (!mCompletionOn) {
			if (mComposing.length() > 0) {
				ArrayList<String> list = new ArrayList<String>();
				list.add(mComposing.toString());
				this.setSuggestions(list, true, true);
			} else {
				this.setSuggestions(null, false, false);
			}
		}
	}
	
	private void updateShiftKeyState(EditorInfo editorInfo) {
		if (editorInfo != null && mInputView != null && mQwertyKeyboard == mInputView.getKeyboard()) {
			int caps = 0;
			EditorInfo ei = getCurrentInputEditorInfo();
			if (ei != null && ei.inputType != EditorInfo.TYPE_NULL) {
				caps = getCurrentInputConnection().getCursorCapsMode(editorInfo.inputType);
			}
			mInputView.setShifted(mCapsLock || caps != 0);
		}
		if (editorInfo != null && mInputView != null && mngKeyboard == mInputView.getKeyboard()) {
			int caps = 0;
			EditorInfo ei = getCurrentInputEditorInfo();
			if (ei != null && ei.inputType != EditorInfo.TYPE_NULL) {
				caps = getCurrentInputConnection().getCursorCapsMode(editorInfo.inputType);
			}
			mInputView.setShifted(mCapsLock || caps != 0);
		}
	}
	
	public boolean isWordSeparator(int code) {
		String separators = getWordSeparators();
		return separators.contains(String.valueOf((char) code));
    }
	
	public void pickDefaultCandidate() {
		this.pickSuggestionManually(0);
	}
	
	public void pickSuggestionManually(int index) {
		if (this.mCompletionOn && this.mCompletions != null && index >= 0 && index < this.mCompletions.length) {
			CompletionInfo ci = mCompletions[index];
			this.getCurrentInputConnection().commitCompletion(ci);
            
			if (this.mCandidateView != null) {
                this.mCandidateView.clear();
            }
            
			this.updateShiftKeyState(this.getCurrentInputEditorInfo());
        } else if (this.mComposing.length() > 0) {
            this.commitTyped(this.getCurrentInputConnection());
        }
    }
	
	public void setSuggestions(List<String> suggestions, boolean completions, boolean typedWordValid) {
		if (suggestions != null && suggestions.size() > 0) {
			this.setCandidatesViewShown(true);
		} else if (isExtractViewShown()) {
			this.setCandidatesViewShown(true);
		}
		
		if (this.mCandidateView != null) {
			this.mCandidateView.setSuggestions(suggestions, completions, typedWordValid);
		}
    }
	
	public void changeMNSmartKeyboard(MNSmartKeyboard[] MNSmartKeyboard) {
		int j = 0;
    	for(int i=0; i<MNSmartKeyboard.length; i++) {
    		if (MNSmartKeyboard[i] == this.mInputView.getKeyboard()) {
    			j = i;
    			break;
    		}
    	}
    	
    	if (j + 1 >= MNSmartKeyboard.length) {
    		this.mInputView.setKeyboard(MNSmartKeyboard[0]);
    	}else{
    		this.mInputView.setKeyboard(MNSmartKeyboard[j + 1]);
    	}		
	}
	
	public void changeMNSmartKeyboardReverse(MNSmartKeyboard[] MNSmartKeyboard) {
		int j = MNSmartKeyboard.length - 1;
		for(int i=MNSmartKeyboard.length - 1; i>=0; i--) {
			if (MNSmartKeyboard[i] == this.mInputView.getKeyboard()) {
				j = i;
				break;
			}
		}
		
		if (j - 1 < 0) {
    		this.mInputView.setKeyboard(MNSmartKeyboard[MNSmartKeyboard.length - 1]);
    	}else{
    		this.mInputView.setKeyboard(MNSmartKeyboard[j - 1]);
    	}
	}
	
	public void onCreate() {
		super.onCreate();
		this.mResources = getResources();
		this.mWordSeparators = getResources().getString(R.string.word_separators);
	}
	
	public View onCreateCandidatesView() {
		this.mCandidateView = new CandidateView(this);
		this.mCandidateView.setService(this);
		return this.mCandidateView;
	}
	
	public View onCreateInputView() {
		this.mInputView = (KeyboardView) this.getLayoutInflater().inflate(R.layout.input, null);
		this.mInputView.setOnKeyboardActionListener(this);
		this.mInputView.setKeyboard(this.mQwertyKeyboard);
		return this.mInputView;
	}
	
	public void onDisplayCompletions(CompletionInfo[] completions) {
		if (this.mCompletionOn) {
            this.mCompletions = completions;
            if (completions == null) {
                this.setSuggestions(null, false, false);
                return;
            }

            List<String> stringList = new ArrayList<String>();
            for (int i = 0; i < (completions != null ? completions.length : 0); i++) {
                CompletionInfo ci = completions[i];
                if ((ci != null) && (ci.getText() != null))
                    stringList.add(ci.getText().toString());
            }
            this.setSuggestions(stringList, true, true);
        }
    }
	
	public void onFinishInput() {
		super.onFinishInput();

        this.mComposing.setLength(0);
		this.updateCandidates();
		this.setCandidatesViewShown(false);

        this.mCurKeyboard = mQwertyKeyboard;
        if (this.mInputView != null) {
            this.mInputView.closing();
        }
        
        this.mCurKeyboard = mngKeyboard;
        if (this.mInputView != null) {
            this.mInputView.closing();
        }
    }
	
	public void onInitializeInterface() {
		if (this.mQwertyKeyboard != null) {
			int displayWidth = getMaxWidth();
			
			if (displayWidth == mLastDisplayWidth) {
                return;
			}
			
			mLastDisplayWidth = displayWidth;
		}
		else if (this.mngKeyboard != null) {
			int displayWidth = getMaxWidth();
			
			if (displayWidth == mLastDisplayWidth) {
                return;
			}
			
			mLastDisplayWidth = displayWidth;
		}
		
		this.mQwertyKeyboard = new MNSmartKeyboard(this, R.xml.qwerty);
		this.mngKeyboard = new MNSmartKeyboard(this, R.xml.mng);
		this.mSymbolsKeyboard = new MNSmartKeyboard(this, R.xml.symbols);
		this.mSymbolsShiftedKeyboard = new MNSmartKeyboard(this, R.xml.symbols_shift);
		
		this.mMNSmartKeyboarda1 = new MNSmartKeyboard(this, R.xml.mnsmart_a1);
		this.mMNSmartKeyboarda2 = new MNSmartKeyboard(this, R.xml.mnsmart_a2);
		this.mMNSmartKeyboarda3 = new MNSmartKeyboard(this, R.xml.mnsmart_a3);
		this.mMNSmartKeyboarda4 = new MNSmartKeyboard(this, R.xml.mnsmart_a4);
		
		this.mMNSmartKeyboardb1 = new MNSmartKeyboard(this, R.xml.mnsmart_b1);
		this.mMNSmartKeyboardb2 = new MNSmartKeyboard(this, R.xml.mnsmart_b2);
		
		this.mMNSmartKeyboardc1 = new MNSmartKeyboard(this, R.xml.mnsmart_c1);
		this.mMNSmartKeyboardc2 = new MNSmartKeyboard(this, R.xml.mnsmart_c2);
		this.mMNSmartKeyboardc3 = new MNSmartKeyboard(this, R.xml.mnsmart_c3);
		this.mMNSmartKeyboardc4 = new MNSmartKeyboard(this, R.xml.mnsmart_c4);
		this.mMNSmartKeyboardc5 = new MNSmartKeyboard(this, R.xml.mnsmart_c5);

		this.mMNSmartKeyboardd1 = new MNSmartKeyboard(this, R.xml.mnsmart_d1);
		this.mMNSmartKeyboardd2 = new MNSmartKeyboard(this, R.xml.mnsmart_d2);
		this.mMNSmartKeyboardd3 = new MNSmartKeyboard(this, R.xml.mnsmart_d3);

		this.mMNSmartKeyboarde1 = new MNSmartKeyboard(this, R.xml.mnsmart_e1);
		this.mMNSmartKeyboarde2 = new MNSmartKeyboard(this, R.xml.mnsmart_e2);
		this.mMNSmartKeyboarde3 = new MNSmartKeyboard(this, R.xml.mnsmart_e3);
		this.mMNSmartKeyboarde4 = new MNSmartKeyboard(this, R.xml.mnsmart_e4);
	}
	
	@Override
	public void onKey(int primaryCode, int[] keyCodes) {
		Log.d("Main", "Primary Code: " + primaryCode);
		
		if (this.isWordSeparator(primaryCode)) {
			if (this.mComposing.length() > 0) {
				this.commitTyped(this.getCurrentInputConnection());
			}
			this.sendKey(primaryCode);
			this.updateShiftKeyState(this.getCurrentInputEditorInfo());
		} else if (primaryCode == Keyboard.KEYCODE_DELETE) {
			this.handleBackspace();
        } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
			this.handleShift();
        } else if (primaryCode == Keyboard.KEYCODE_CANCEL) {
        	handleClose();
            return;
        } else if (primaryCode == MNSmartKeyboardView.KEYCODE_OPTIONS) {
        	this.showOptionsMenu();
        } else if (primaryCode == MNSmartKeyboardView.KEYCODE_SYMBOL && this.mInputView != null) {
        	this.mInputView.setKeyboard(this.mSymbolsKeyboard);
        	this.mInputView.setShifted(false);
        } else if (primaryCode == MNSmartKeyboardView.KEYCODE_ABC && this.mInputView != null) {
        	this.mInputView.setKeyboard(this.mQwertyKeyboard);
        }
        else if (primaryCode == MNSmartKeyboardView.KEYCODE_MNG && this.mInputView != null) {
        	this.mInputView.setKeyboard(this.mngKeyboard);
        }else if (primaryCode == MNSmartKeyboardView.KEYCODE_MNSmart && this.mInputView != null) {
        	this.mInputView.setKeyboard(this.mMNSmartKeyboarda1);
        } else if (primaryCode == MNSmartKeyboardView.KEYCODE_MNSmart_1 && this.mInputView != null) {
        	this.changeMNSmartKeyboard(new MNSmartKeyboard[] {
        		this.mMNSmartKeyboarda1,
        		this.mMNSmartKeyboarda2,
        		this.mMNSmartKeyboarda3,
        		this.mMNSmartKeyboarda4
        	});
        } else if (primaryCode == MNSmartKeyboardView.KEYCODE_MNSmart_2 && this.mInputView != null) {
        	this.changeMNSmartKeyboard(new MNSmartKeyboard[] {
            	this.mMNSmartKeyboardb1,
            	this.mMNSmartKeyboardb2
            });
        } else if (primaryCode == MNSmartKeyboardView.KEYCODE_MNSmart_3 && this.mInputView != null) {
        	this.changeMNSmartKeyboard(new MNSmartKeyboard[] {
            	this.mMNSmartKeyboardc1,
            	this.mMNSmartKeyboardc2,
            	this.mMNSmartKeyboardc3,
            	this.mMNSmartKeyboardc4,
            	this.mMNSmartKeyboardc5
            });
        } else if (primaryCode == MNSmartKeyboardView.KEYCODE_MNSmart_4 && this.mInputView != null) {
        	this.changeMNSmartKeyboard(new MNSmartKeyboard[] {
            	this.mMNSmartKeyboardd1,
            	this.mMNSmartKeyboardd2,
            	this.mMNSmartKeyboardd3
            });
        } else if (primaryCode == MNSmartKeyboardView.KEYCODE_MNSmart_5 && this.mInputView != null) {
        	this.changeMNSmartKeyboard(new MNSmartKeyboard[] {
            	this.mMNSmartKeyboarde1,
            	this.mMNSmartKeyboarde2,
            	this.mMNSmartKeyboarde3,
            	this.mMNSmartKeyboarde4
            });
        } else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE && this.mInputView != null) {
        	Keyboard current = mInputView.getKeyboard();
        	
        	if (current == this.mSymbolsKeyboard || current == this.mSymbolsShiftedKeyboard || current==this.mngKeyboard) {
                current = this.mQwertyKeyboard;
            }
        	else if (current == this.mSymbolsKeyboard || current == this.mSymbolsShiftedKeyboard || current==this.mQwertyKeyboard) {
                current = this.mngKeyboard;
            }
        	else {
                current = this.mSymbolsKeyboard;
            }
        	
        	this.mInputView.setKeyboard(current);
        	
            if (current == mSymbolsKeyboard) {
                current.setShifted(false);
            }
        } else {
			this.handleCharacter(primaryCode, keyCodes);
        }
	}

	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:		// 4
				if (event.getRepeatCount() == 0 && this.mInputView != null) {
					if (this.mInputView.handleBack()) {
						return true;
					}
				}
				break;
			case KeyEvent.KEYCODE_DEL:		// 64
				if (this.mComposing.length() > 0) {
					this.onKey(Keyboard.KEYCODE_DELETE, null);
					return true;
				}
				break;
			case KeyEvent.KEYCODE_ENTER:	// 67
				return false;
			default:
				if (this.mPredictionOn && this.translateKeyDown(keyCode, event)) {
					return true;
				}
				break;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (this.mPredictionOn) {
			this.mMetaState = MetaKeyKeyListener.handleKeyUp(this.mMetaState, keyCode, event);
        }

        return super.onKeyUp(keyCode, event);
    }
	
	@Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
		super.onStartInput(attribute, restarting);

		this.mComposing.setLength(0);
		this.updateCandidates();

        if (!restarting) {
			this.mMetaState = 0;
        }

		this.mPredictionOn = false;
		this.mCompletionOn = false;
		this.mCompletions = null;

        switch (attribute.inputType & EditorInfo.TYPE_MASK_CLASS) {
        	case EditorInfo.TYPE_CLASS_NUMBER:		// 2
				this.mCurKeyboard.setImeOptions(getResources(), attribute.imeOptions);
        		break;
        	case EditorInfo.TYPE_CLASS_DATETIME:	// 4
				this.mCurKeyboard = this.mSymbolsKeyboard;
        		break;
        	case EditorInfo.TYPE_CLASS_PHONE:		// 3
        		this.mCurKeyboard = this.mSymbolsKeyboard;
        		break;
        	case EditorInfo.TYPE_CLASS_TEXT:		// 1
        	{
        		if(this.mCurKeyboard==this.mngKeyboard)
        		this.mCurKeyboard = this.mQwertyKeyboard;
        		else
        		this.mCurKeyboard = this.mngKeyboard;	
        	}
        		this.mPredictionOn = true;
        		
        		int variation = attribute.inputType & EditorInfo.TYPE_MASK_VARIATION;
        		if (variation == EditorInfo.TYPE_TEXT_VARIATION_PASSWORD || variation == EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
        			this.mPredictionOn = false;
        		}
        		
        		if (variation == EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS || variation == EditorInfo.TYPE_TEXT_VARIATION_URI || variation == EditorInfo.TYPE_TEXT_VARIATION_FILTER) {
        			this.mPredictionOn = false;
        		}
        		
        		if ((attribute.inputType & EditorInfo.TYPE_TEXT_FLAG_AUTO_COMPLETE) != 0) {
					this.mPredictionOn = false;
					this.mCompletionOn = this.isFullscreenMode();
                }
        		
        		updateShiftKeyState(attribute);
        		break;
        	default:
				this.mCurKeyboard = this.mQwertyKeyboard;
				this.updateShiftKeyState(attribute);
        		break;
        }
	}

	@Override
	public void onStartInputView(EditorInfo attribute, boolean restarting) {
		super.onStartInputView(attribute, restarting);
		this.mInputView.setKeyboard(this.mCurKeyboard);
		this.mInputView.closing();
    }
	
	@Override
	public void onText(CharSequence text) {
		InputConnection ic = getCurrentInputConnection();
		
		if (ic == null)
            return;
        
		ic.beginBatchEdit();
        
		if (this.mComposing.length() > 0) {
			this.commitTyped(ic);
		}
		
		ic.commitText(text, 0);
		ic.endBatchEdit();
		
		this.updateShiftKeyState(this.getCurrentInputEditorInfo());
    }
	
	@Override
	public void onUpdateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int candidatesStart, int candidatesEnd) {
		super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd);

		if (this.mComposing.length() > 0 && (newSelStart != candidatesEnd || newSelEnd != candidatesEnd)) {
			this.mComposing.setLength(0);
			this.updateCandidates();
			
			InputConnection ic = getCurrentInputConnection();
			
			if (ic != null) {
				ic.finishComposingText();
			}
		}
	}
	
	@Override
	public void onPress(int primaryCode) {
	}

	@Override
	public void onRelease(int primaryCode) {
	}

	@Override
	public void swipeDown() {
		this.handleClose();	
	}

	@Override
	public void swipeLeft() {
		Log.d("Main", "swipe left");
		this.changeMNSmartKeyboard(new MNSmartKeyboard[] {
			this.mQwertyKeyboard, this.mngKeyboard, this.mSymbolsKeyboard, this.mSymbolsShiftedKeyboard,
        	this.mMNSmartKeyboarda1, this.mMNSmartKeyboarda2, this.mMNSmartKeyboarda3, this.mMNSmartKeyboarda4,
        	this.mMNSmartKeyboardb1, this.mMNSmartKeyboardb2,
        	this.mMNSmartKeyboardc1, this.mMNSmartKeyboardc2, this.mMNSmartKeyboardc3, this.mMNSmartKeyboardc4, this.mMNSmartKeyboardc5,
        	this.mMNSmartKeyboardd1, this.mMNSmartKeyboardd2, this.mMNSmartKeyboardd3,
        	this.mMNSmartKeyboarde1, this.mMNSmartKeyboarde2, this.mMNSmartKeyboarde3, this.mMNSmartKeyboarde4,
        });
	}

	@Override
	public void swipeRight() {
		Log.d("Main", "swipe right");
		this.changeMNSmartKeyboardReverse(new MNSmartKeyboard[] {
			this.mQwertyKeyboard, this.mngKeyboard,this.mSymbolsKeyboard, this.mSymbolsShiftedKeyboard,
	        this.mMNSmartKeyboarda1, this.mMNSmartKeyboarda2, this.mMNSmartKeyboarda3, this.mMNSmartKeyboarda4,
	        this.mMNSmartKeyboardb1, this.mMNSmartKeyboardb2,
	        this.mMNSmartKeyboardc1, this.mMNSmartKeyboardc2, this.mMNSmartKeyboardc3, this.mMNSmartKeyboardc4, this.mMNSmartKeyboardc5,
	        this.mMNSmartKeyboardd1, this.mMNSmartKeyboardd2, this.mMNSmartKeyboardd3,
	        this.mMNSmartKeyboarde1, this.mMNSmartKeyboarde2, this.mMNSmartKeyboarde3, this.mMNSmartKeyboarde4,
		});
	}

	@Override
	public void swipeUp() {
		// TODO Auto-generated method stub
		
	}

}
