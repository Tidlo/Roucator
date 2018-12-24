

package com.focjoe.roucator.model;

import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.focjoe.roucator.R;
import com.focjoe.roucator.util.FileUtils;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

class VendorDB implements VendorService {
    private final Resources resources;
    private final Map<String, List<String>> vendors;
    private final Map<String, String> macs;
    private boolean loaded;

    VendorDB(@NonNull Resources resources) {
        this.resources = resources;
        this.vendors = new TreeMap<>();
        this.macs = new TreeMap<>();
        this.loaded = false;
    }

    @NonNull
    @Override
    public String findVendorName(String address) { //address is the MAC address aka BSSID
        String result = getMacs().get(VendorUtils.clean(address));
        return result == null ? StringUtils.EMPTY : result;
    }

    @NonNull
    @Override
    public List<String> findMacAddresses(String vendorName) {
        if (StringUtils.isBlank(vendorName)) {
            return new ArrayList<>();
        }
        List<String> results = getVendors().get(vendorName);

        return results == null ? new ArrayList<String>() : results;
    }

    @NonNull
    Map<String, List<String>> getVendors() {
        load(resources);
        return vendors;
    }

    @NonNull
    Map<String, String> getMacs() {
        load(resources);
        return macs;
    }

    private void load(@NonNull Resources resources) {
        if (!loaded) {
            loaded = true;
            String[] lines = FileUtils.readFile(resources, R.raw.data).split("\n");
            for (String data : lines) {
                if (data != null) {
                    String[] parts = data.split("\\|");
                    if (parts.length == 2) {
                        List<String> addresses = new ArrayList<>();
                        String name = parts[0];
                        vendors.put(name, addresses);
                        int length = parts[1].length();
                        for (int i = 0; i < length; i += VendorUtils.MAX_SIZE) {
                            String mac = parts[1].substring(i, i + VendorUtils.MAX_SIZE);
                            addresses.add(VendorUtils.toMacAddress(mac));
                            macs.put(mac, name);
                        }
                    }
                }
            }
        }
    }

    private class StringContains implements Predicate<String> {
        private final String filter;

        private StringContains(@NonNull String filter) {
            this.filter = filter;
        }

        @Override
        public boolean evaluate(String object) {
            return object.contains(filter);
        }
    }
}
