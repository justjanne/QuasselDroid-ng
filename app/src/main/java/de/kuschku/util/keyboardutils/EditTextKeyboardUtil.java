package de.kuschku.util.keyboardutils;

import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

//TODO: FIND A WAY TO TEST THIS – THE EMULATOR DOESN'T WORK
public class EditTextKeyboardUtil implements AppCompatEditText.OnKeyListener {
    AppCompatEditText editText;
    boolean reverse;

    public EditTextKeyboardUtil(AppCompatEditText editText) {
        this.editText = editText;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        Log.e("DEBUG", keyCode + " : " + event.toString());

        if (event.isShiftPressed()) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (editText.getSelectionStart() == editText.getSelectionEnd())
                        reverse = true;

                    if (reverse) {
                        int start = Math.max(0, editText.getSelectionStart() - 1);
                        editText.setSelection(start, editText.getSelectionEnd());
                    } else {
                        int end = Math.min(editText.length(), editText.getSelectionEnd() - 1);
                        editText.setSelection(end, end);
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (editText.getSelectionStart() == editText.getSelectionEnd())
                        reverse = false;

                    if (reverse) {
                        int start = Math.max(0, editText.getSelectionStart() + 1);
                        editText.setSelection(start, editText.getSelectionEnd());
                    } else {
                        int end = Math.min(editText.length(), editText.getSelectionEnd() + 1);
                        editText.setSelection(end, end);
                    }
                    break;
            }
        } else {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    int start = Math.max(0, editText.getSelectionStart() - 1);
                    editText.setSelection(start, start);
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    int end = Math.min(editText.length(), editText.getSelectionEnd() + 1);
                    editText.setSelection(end, end);
                    break;
            }
        }
        return false;
    }
}
