package com.ericssonlabs;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.imagescan.MainActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class QRCodeEditActivity extends Activity implements OnClickListener {

    private static final int BACK_CLICK = 2;
    private static final String TAG = "QRCodeEdit";
    private final int LOGO_BITMAP = 0;
    private View qrCodeView;

    private Bitmap qrCodeBitmap;
    private ImageView qrCodeImageView;
    private Bitmap logoBitmap;
    private Bitmap resultBitmap;
    private Bitmap textBitmap;
    private ImageView logoImageView;
    private StrokeTextView logoTextView;

    private Button addLogo;
    private Button addText;
    private EditText inputText;
    private Button cleanLogo;
    private Button cleanText;
    private Button saveCode;

    /**
     * 重新设置Bitmap大小
     */
    public static Bitmap resizeBitMapImage(Bitmap bgimage, double newWidth, double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.qrcode_editor);
        Bundle extras = getIntent().getExtras();
        qrCodeBitmap = extras.getParcelable("QRCode");
        qrCodeBitmap = resizeBitMapImage(qrCodeBitmap, 1100, 1100);
        qrCodeImageView = (ImageView) findViewById(R.id.qr_edit_image);
        qrCodeImageView.setImageBitmap(qrCodeBitmap);
        qrCodeImageView.setAdjustViewBounds(true);
        qrCodeImageView.getLayoutParams().height = qrCodeImageView.getLayoutParams().width;
        resultBitmap = qrCodeBitmap;


        //使透明底变白
        Bitmap qrCodeWhiteBg = Bitmap.createBitmap(qrCodeBitmap.getWidth(), qrCodeBitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(qrCodeWhiteBg);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(qrCodeBitmap, 0, 0, null);
        qrCodeBitmap = qrCodeWhiteBg;

        loadAllVariables();


    }

    private void loadAllVariables() {
        qrCodeView = (View) findViewById(R.id.qrcode_layout);

        addLogo = (Button) findViewById(R.id.add_logo);
        addLogo.setOnClickListener(this);
        inputText = (EditText) findViewById(R.id.input_text);
        //EditText 输入字数限制
        inputText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
        addText = (Button) findViewById(R.id.add_text);
        addText.setOnClickListener(this);
        cleanLogo = (Button) findViewById(R.id.clean_logo);
        cleanLogo.setOnClickListener(this);
        cleanText = (Button) findViewById(R.id.clean_text);
        cleanText.setOnClickListener(this);
        saveCode = (Button) findViewById(R.id.save_code);
        saveCode.setOnClickListener(this);

        logoImageView = (ImageView) findViewById(R.id.logo_imageview);
        logoTextView = (StrokeTextView) findViewById(R.id.logo_textview);

    }

    /**
     * 设置指定view的拖拽事件
     *
     * @param dragView
     */
    private void setDrag(final View dragView) {
        // TODO Auto-generated method stub
        dragView.setOnTouchListener(new OnTouchListener() {
            int startX;
            int startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:// 手指第一次触摸到屏幕
                        this.startX = (int) event.getRawX();
                        this.startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:// 手指移动
                        int newX = (int) event.getRawX();
                        int newY = (int) event.getRawY();

                        int dx = newX - this.startX;
                        int dy = newY - this.startY;

                        // 计算出来控件原来的位置
                        int l = dragView.getLeft();
                        int r = dragView.getRight();
                        int t = dragView.getTop();
                        int b = dragView.getBottom();

                        int newt = t + dy;
                        int newb = b + dy;
                        int newl = l + dx;
                        int newr = r + dx;

                        if ((newl < 0) || (newt < 0)
                                || (newr > qrCodeView.getWidth())
                                || (newb > qrCodeView.getHeight())) {
                            break;
                        }

                        // 更新iv在屏幕的位置.
                        dragView.layout(newl, newt, newr, newb);
                        this.startX = (int) event.getRawX();
                        this.startY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP: // 手指离开屏幕的一瞬间
                        int lastx = dragView.getLeft();
                        int lasty = dragView.getTop();
//						Editor editor = MainActivity.this.sp.edit();
//						editor.putInt( "lastx" , lastx );
//						editor.putInt( "lasty" , lasty );
//						editor.commit();
                        break;
                }
                return true;
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v == addLogo) {
            Intent intent = new Intent(QRCodeEditActivity.this, MainActivity.class);
            startActivityForResult(intent, 0);
            return;
        }
        if (v == cleanLogo) {
            qrCodeImageView.setImageBitmap(qrCodeBitmap);
            qrCodeImageView.invalidate();
            return;
        }
        if (v == saveCode) {
            saveImage(resultBitmap);
            return;
        }
        if (v == addText) {
            //收起键盘
            InputMethodManager imm = (InputMethodManager) getSystemService(QRCodeEditActivity.this.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            cleanInputText();

            String text = inputText.getText().toString();
            Log.i(TAG, text);
            logoTextView.setText(text);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) logoTextView
                    .getLayoutParams();
            lp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            logoTextView.setLayoutParams(lp);
            logoTextView.setTextSize(40);
            setDrag(logoTextView);

//	 			DrawText dt = new DrawText();
//	 			textBitmap = dt.getImage(400, 100, text, 60);
//
//	 			resultBitmap = mergeText();
//	 			qrCodeImageView.setImageBitmap(resultBitmap);
//				qrCodeImageView.invalidate();
            return;
        }
        if (v == cleanText) {
            inputText.setText("");
//	 			qrCodeImageView.setImageBitmap(beforeAddText);
//				qrCodeImageView.invalidate();
            cleanInputText();
            return;
        }
    }

    public void cleanInputText() {
        qrCodeImageView.setImageBitmap(qrCodeBitmap);
        qrCodeImageView.invalidate();
    }

    /**
     * 获取相册图片
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (resultCode) {
            case LOGO_BITMAP:
                Bundle resultBundle = intent.getExtras();
                logoBitmap = resultBundle.getParcelable("LogoBitmap");
                if (null != logoBitmap) {
                    Log.i("qrcode_editor", "get logo");
                    //设置logo最终大小
                    resultBitmap = qrCodeBitmap;
                    logoBitmap = resizeBitMapImage(logoBitmap, 250, 250);
                    logoImageView.setImageBitmap(logoBitmap);
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) logoImageView
                            .getLayoutParams();
                    lp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                    lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                    logoImageView.setLayoutParams(lp);
                    setDrag(logoImageView);

//					logoImageView.setVisibility(View.VISIBLE);
//					resultBitmap = mergeLogo(resultBitmap, logoBitmap);
//					Log.i("qrcode_editor", "height:"+resultBitmap.getHeight());
//					qrCodeImageView.setImageBitmap(resultBitmap);
//					qrCodeImageView.invalidate();
                }
                break;
            case BACK_CLICK:
                break;
            default:
                break;
        }
    }

    /**
     * 合成logo
     *
     * @return
     */
    public Bitmap mergeLogo(Bitmap bgBitmap, Bitmap fgBitmap) {
        int bgWidth = bgBitmap.getWidth();
        int bgHeight = bgBitmap.getHeight();
        int fgWidth = fgBitmap.getWidth();
        int fgHeight = fgBitmap.getHeight();
        Bitmap mResultBitmap = Bitmap.createBitmap(bgWidth, bgHeight, Config.ARGB_8888);
        Canvas canvas = new Canvas(mResultBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bgBitmap, 0, 0, null);
        canvas.drawBitmap(fgBitmap, (bgWidth - fgWidth) / 2, (bgHeight - fgHeight) / 2, null);
        return mResultBitmap;
    }

    public Bitmap mergeText() {
        int bgWidth = resultBitmap.getWidth();
        int bgHeight = resultBitmap.getHeight();
        int fgWidth = textBitmap.getWidth();
        int fgHeight = textBitmap.getHeight();
        Bitmap mResultBitmap = Bitmap.createBitmap(bgWidth, bgHeight, Config.ARGB_8888);
        Canvas canvas = new Canvas(mResultBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(resultBitmap, 0, 0, null);
        canvas.drawBitmap(textBitmap, (bgWidth - fgWidth) / 2, 575, null);
        return mResultBitmap;
    }


    /**
     * 保存图像
     *
     * @throws IOException
     */
    public void saveImage(Bitmap mBitmap) {
        long currentTime = System.currentTimeMillis();
        File f = new File("/sdcard/DCIM/Camera/QRCode" + currentTime + ".jpg");
        try {
            f.createNewFile();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
            Toast.makeText(this, "保存成功 QRCode" + currentTime + ".png", 1).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}	

	 	
