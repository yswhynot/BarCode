package com.example.imagescan;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.ericssonlabs.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

//import android.content.DialogInterface.OnClickListener;

public class ShowImageActivity extends Activity {
    private static final int LOGO_BITMAP = 0;
    private static final int BACK_CLICK = 2;
    private GridView mGridView;
    private List<String> list;
    private ChildAdapter adapter;

    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 重新设置Bitmap大小
     */
    public static Bitmap resizeBitMapImage(Bitmap bgimage, double newWidth, double newHeight) {

        float width = bgimage.getWidth();
        float height = bgimage.getHeight();

        Matrix matrix = new Matrix();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);

        return bitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_image_activity);

        mGridView = (GridView) findViewById(R.id.child_grid);
        list = getIntent().getStringArrayListExtra("data");

        adapter = new ChildAdapter(this, list, mGridView);
        mGridView.setAdapter(adapter);

        Button pickImage = (Button) this.findViewById(R.id.btn_pick_image);
        pickImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (1 != adapter.getSelectItems().size()) {
                    Toast.makeText(ShowImageActivity.this, "Please select just one image", Toast.LENGTH_LONG).show();
                    return;
                }
                Log.i("pick", "click" + adapter.getSelectItems().get(0));
                Bitmap bitmap = getLoacalBitmap((String) adapter.getItem(adapter.getSelectItems().get(0)));
                Log.i("pick", "height" + bitmap.getHeight());
                Log.i("pick", "width" + bitmap.getWidth());
                bitmap = resizeBitMapImage(bitmap, 220, 220);
                Intent resultIntent = getIntent();
                resultIntent.putExtra("LogoBitmap", bitmap);
                setResult(LOGO_BITMAP, resultIntent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "select " + adapter.getSelectItems().size() + " item", Toast.LENGTH_LONG).show();
//		super.onBackPressed();
        Intent resultIntent = getIntent();
        resultIntent.putExtra("LogoBitmap", "BACK");
        setResult(BACK_CLICK, resultIntent);
        Log.i("pick", "back_click");
        finish();
    }

}
