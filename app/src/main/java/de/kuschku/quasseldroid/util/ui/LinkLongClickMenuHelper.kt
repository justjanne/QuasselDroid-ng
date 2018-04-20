package de.kuschku.quasseldroid.util.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.support.v7.widget.PopupMenu
import android.widget.TextView
import de.kuschku.quasseldroid.R
import me.saket.bettermovementmethod.BetterLinkMovementMethod

class LinkLongClickMenuHelper :
  BetterLinkMovementMethod.OnLinkLongClickListener,
  ((TextView, String) -> Boolean) {
  private var linkMenu: PopupMenu? = null

  override fun invoke(anchor: TextView, url: String) = onLongClick(anchor, url)
  override fun onLongClick(anchor: TextView, url: String?): Boolean {
    if (linkMenu == null) {
      linkMenu = PopupMenu(anchor.context, anchor).also { menu ->
        linkMenu?.dismiss()
        menu.menuInflater.inflate(R.menu.context_link, menu.menu)
        menu.setOnMenuItemClickListener {
          when (it.itemId) {
            R.id.action_copy  -> {
              val clipboard = anchor.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              val clip = ClipData.newPlainText(null, url)
              clipboard.primaryClip = clip
              menu.dismiss()
              linkMenu = null
              true
            }
            R.id.action_share -> {
              val intent = Intent(Intent.ACTION_SEND)
              intent.type = "text/plain"
              intent.putExtra(Intent.EXTRA_TEXT, url)
              anchor.context.startActivity(
                Intent.createChooser(intent, anchor.context.getString(R.string.label_share))
              )
              menu.dismiss()
              linkMenu = null
              true
            }
            else              -> false
          }
        }
        menu.setOnDismissListener {
          linkMenu = null
        }
        menu.show()
      }
    }
    return true
  }
}
