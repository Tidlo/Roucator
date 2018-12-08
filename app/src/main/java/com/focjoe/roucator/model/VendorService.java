package com.focjoe.roucator.model;

import android.support.annotation.NonNull;

import java.util.List;

public interface VendorService {
    @NonNull
    String findVendorName(String macAddress);

    @NonNull
    List<String> findMacAddresses(String vendorName);
}
