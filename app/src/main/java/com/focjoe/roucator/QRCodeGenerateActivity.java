package com.focjoe.roucator;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.EnumMap;
import java.util.Map;


public class QRCodeGenerateActivity extends AppCompatActivity {

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_generate);
        imageView = (ImageView) findViewById(R.id.imgQRCode);
        EditText editText = (EditText) findViewById(R.id.txtQRCode);
        Button btn_reader = (Button) findViewById(R.id.btn_reader);
        assert btn_reader != null;
        btn_reader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });



        /*Parameter	Example	Description
            T	WPA 	Authentication type; can be WEP or WPA, or 'nopass' for no password. Or, omit for no password.
            S	mynetwork	Network SSID. Required. Enclose in double quotes if it is an ASCII name, but could be interpreted as hex (i.e. "ABCD")
            P	mypass	Password, ignored if T is "nopass" (in which case it may be omitted). Enclose in double quotes if it is an ASCII name, but could be interpreted as hex (i.e. "ABCD")
            H	true	Optional. True if the network SSID is hidden.
        * */

//        String wifi =encodeQRCodeWifi("WPA","shoniz","PassPass",false);
        String wifi = encodeQRCodeWifi("nopass", "GUET-Student", "", false);


        try {
            Bitmap bitmap = encodeAsBitmap(wifi, 512);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        assert editText != null;
//        editText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//
//
//            }
//        });
    }

    private String encodeQRCodeWifi(String type, String ssid, String pass, boolean hidden) {

        String wifiFormat = "WIFI:S:" + ssid + ";T:" + type + ";P:" + pass + ";" + hidden + ";";
        return wifiFormat.replace("\"", "\\\"").replace("\\", "\\\\");
    }

    Bitmap encodeAsBitmap(String str, int size) throws WriterException {

        Map<EncodeHintType, Object> hintMap = new EnumMap<>(EncodeHintType.class);
        hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hintMap.put(EncodeHintType.MARGIN, 1); /* default = 4 */
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, size, size, hintMap);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, size, 0, 0, w, h);
        return bitmap;
    }
}
