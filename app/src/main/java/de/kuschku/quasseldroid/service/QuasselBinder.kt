package de.kuschku.quasseldroid.service

import android.os.Binder
import de.kuschku.libquassel.session.Backend

class QuasselBinder(val backend: Backend) : Binder()
