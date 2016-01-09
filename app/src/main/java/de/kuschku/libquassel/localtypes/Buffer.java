package de.kuschku.libquassel.localtypes;

import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import de.kuschku.libquassel.primitives.types.BufferInfo;

public interface Buffer {
    BufferInfo getInfo();

    String getName();

    IDrawerItem getDrawerElement();
}
