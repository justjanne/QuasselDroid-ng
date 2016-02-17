/*
 * QuasselDroid - Quassel client for Android
 * Copyright (C) 2016 Janne Koschinski
 * Copyright (C) 2016 Ken Børge Viktil
 * Copyright (C) 2016 Magnus Fjell
 * Copyright (C) 2016 Martin Sandsmark <martin.sandsmark@kde.org>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.util.buffermetadata;

import android.content.Context;

public class SQLiteBufferMetaDataManager implements BufferMetaDataManager {
    private BufferMetaDataHelper helper;

    public SQLiteBufferMetaDataManager(Context context) {
        helper = new BufferMetaDataHelper(context);
    }

    @Override
    public int markerline(String coreid, int bufferid) {
        return helper.markerLine(coreid, bufferid);
    }

    @Override
    public int hiddendata(String coreid, int bufferid) {
        return helper.hiddenData(coreid, bufferid);
    }

    @Override
    public void setMarkerline(String coreid, int bufferid, int markerline) {
        helper.storeMarkerline(coreid, bufferid, markerline);
    }

    @Override
    public void setHiddendata(String coreid, int bufferid, int hiddendata) {
        helper.storeHiddenData(coreid, bufferid, hiddendata);
    }

    @Override
    public void removeBuffer(String coreid, int bufferid) {
        helper.deleteBuffer(coreid, bufferid);
    }

    @Override
    public void removeCore(String coreid) {
        helper.deleteCore(coreid);
    }
}
