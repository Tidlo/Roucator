

package com.focjoe.roucator.model;

import android.content.res.Resources;
import android.support.annotation.NonNull;

public class VendorServiceFactory {
    private VendorServiceFactory() {
        throw new IllegalStateException("Factory class");
    }

    public static VendorService makeVendorService(@NonNull Resources resources) {
        return new VendorDB(resources);
    }
}
