package de.kuschku.util.backports;

import android.support.annotation.NonNull;

public interface BinaryFunction<A, B, C> {
    @NonNull
    C apply(A arg0, B arg1);
}
