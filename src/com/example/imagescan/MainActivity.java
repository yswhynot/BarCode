package com.example.imagescan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.ericssonlabs.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class MainActivity extends Activity {
    private static final int SCAN_OK = 1;
    private static final int LOGO_BITMAP = 0;
    private static final int BACK_CLICK = 2;
    private HashMap<String, List<String>> mGruopMap = new HashMap<String, List<String>>();
    private List<ImageBean> list = new ArrayList<ImageBean>();
    private ProgressDialog mProgressDialog;
    private GroupAdapter adapter;
    private GridView mGroupGridView;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCAN_OK:
                    //�رս�����
                    mProgressDialog.dismiss();

                    adapter = new GroupAdapter(MainActivity.this, list = subGroupOfImage(mGruopMap), mGroupGridView);
                    mGroupGridView.setAdapter(adapter);
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGroupGridView = (GridView) findViewById(R.id.main_grid);

        getImages();

        mGroupGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                List<String> childList = mGruopMap.get(list.get(position).getFolderName());

                Intent mIntent = new Intent(MainActivity.this, ShowImageActivity.class);
                mIntent.putStringArrayListExtra("data", (ArrayList<String>) childList);
                startActivityForResult(mIntent, 0);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (resultCode) {
            case LOGO_BITMAP:

                Bundle resultBundle = intent.getExtras();
                Bitmap logoBitmap = resultBundle.getParcelable("LogoBitmap");
                if (logoBitmap != null) {
                    Log.i("main_activity", "get logo");
                    setResult(LOGO_BITMAP, intent);
                    finish();
                }
                break;
            case BACK_CLICK:
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = getIntent();
        resultIntent.putExtra("LogoBitmap", "BACK");
        setResult(BACK_CLICK, resultIntent);
        Log.i("pick", "back_click");
        finish();
    }

    /**
     * ����ContentProviderɨ���ֻ��е�ͼƬ���˷��������������߳���
     */
    private void getImages() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//			Toast.makeText(this, "�����ⲿ�洢", Toast.LENGTH_SHORT).show();
            return;
        }

        //��ʾ������
        mProgressDialog = ProgressDialog.show(this, null, "图片读取中...");

        new Thread(new Runnable() {

            @Override
            public void run() {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = MainActivity.this.getContentResolver();

                //ֻ��ѯjpeg��png��ͼƬ
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);

                while (mCursor.moveToNext()) {
                    //��ȡͼƬ��·��
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));

                    //��ȡ��ͼƬ�ĸ�·����
                    String parentName = new File(path).getParentFile().getName();


                    //���ݸ�·������ͼƬ���뵽mGruopMap��
                    if (!mGruopMap.containsKey(parentName)) {
                        List<String> chileList = new ArrayList<String>();
                        chileList.add(path);
                        mGruopMap.put(parentName, chileList);
                    } else {
                        mGruopMap.get(parentName).add(path);
                    }
                }

                mCursor.close();

                //֪ͨHandlerɨ��ͼƬ���
                mHandler.sendEmptyMessage(SCAN_OK);

            }
        }).start();

    }


    /**
     * ��װ�������GridView������Դ����Ϊ����ɨ���ֻ���ʱ��ͼƬ��Ϣ����HashMap��
     * ������Ҫ����HashMap��������װ��List
     *
     * @param mGruopMap
     * @return
     */
    private List<ImageBean> subGroupOfImage(HashMap<String, List<String>> mGruopMap) {
        if (mGruopMap.size() == 0) {
            return null;
        }
        List<ImageBean> list = new ArrayList<ImageBean>();

        Iterator<Map.Entry<String, List<String>>> it = mGruopMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<String>> entry = it.next();
            ImageBean mImageBean = new ImageBean();
            String key = entry.getKey();
            List<String> value = entry.getValue();

            mImageBean.setFolderName(key);
            mImageBean.setImageCounts(value.size());
            mImageBean.setTopImagePath(value.get(0));//��ȡ����ĵ�һ��ͼƬ

            list.add(mImageBean);
        }

        return list;

    }


}
