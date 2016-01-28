package de.kuschku.util.keyboardutils;

import android.content.DialogInterface;
import android.view.KeyEvent;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * A util class that automatically handles <code>enter</code> and <code>esc</code> in dialogs
 * properly: By calling onPositive or onNeutral
 */
public class DialogKeyboardUtil implements DialogInterface.OnKeyListener {
    MaterialDialog dialog;

    public DialogKeyboardUtil(MaterialDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public boolean onKey(DialogInterface d, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
            dialog.getActionButton(DialogAction.POSITIVE).callOnClick();
            dialog.dismiss();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_ESCAPE) {
            dialog.dismiss();
            return true;
        }
        return false;
    }
}
