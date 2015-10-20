package com.ericssonlabs;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.zxing.activity.CaptureActivity;
import com.zxing.encoding.EncodingHandler;

public class BarCodeTestActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    private TextView resultTextView;
    private EditText qrStrEditText;
    private ImageView qrImgImageView;
    private boolean isQRCode = false;
    private Bitmap bitmapForEditing;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        resultTextView = (TextView) this.findViewById(R.id.tv_scan_result);
        qrStrEditText = (EditText) this.findViewById(R.id.et_qr_string);
        qrImgImageView = (ImageView) this.findViewById(R.id.iv_qr_image);


        Button scanBarCodeButton = (Button) this.findViewById(R.id.btn_scan_barcode);
        scanBarCodeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent openCameraIntent = new Intent(BarCodeTestActivity.this, CaptureActivity.class);
                startActivityForResult(openCameraIntent, 0);
            }
        });

/**
 * 生成二维码      
 */
        Button generateQRCodeButton = (Button) this.findViewById(R.id.btn_add_qrcode);
        generateQRCodeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    String contentString = qrStrEditText.getText().toString();
                    if (!contentString.equals("")) {
                        Bitmap qrCodeBitmap = EncodingHandler.createQRCode(contentString, 300);
                        qrImgImageView.setImageBitmap(qrCodeBitmap);
                        bitmapForEditing = qrCodeBitmap;
                        InputMethodManager imm = (InputMethodManager) getSystemService(BarCodeTestActivity.this.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                        isQRCode = true;
                    } else {
                        Toast.makeText(BarCodeTestActivity.this, "Text can not be empty", Toast.LENGTH_SHORT).show();
                    }

                } catch (WriterException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        /**
         * 生成条形码
         */
        Button generateBarCodeButton = (Button) this.findViewById(R.id.btn_add_barcode);
        generateBarCodeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String contentString = qrStrEditText.getText().toString();
                if (!contentString.equals("")) {
                    if (isNumeric(contentString)) {

                        Bitmap barCodeBitmap = EncodingHandler.creatBarCode(BarCodeTestActivity.this, contentString, 1000, 300, true);
                        qrImgImageView.setImageBitmap(barCodeBitmap);
                        InputMethodManager imm = (InputMethodManager) getSystemService(BarCodeTestActivity.this.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                        isQRCode = false;
                    } else {
                        Toast.makeText(BarCodeTestActivity.this, "Text must be a string of number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BarCodeTestActivity.this, "Text can not be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        qrImgImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (!isQRCode)
                    return;
                Log.d("qrcode", "click");
                Intent editQRCodeIntent = new Intent(BarCodeTestActivity.this, QRCodeEditActivity.class);
                editQRCodeIntent.putExtra("QRCode", bitmapForEditing);
                startActivity(editQRCodeIntent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            resultTextView.setText(scanResult);
        }
    }

    public boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}