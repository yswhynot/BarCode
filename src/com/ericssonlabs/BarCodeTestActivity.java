package com.ericssonlabs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.SmartPOS.ProductDetailPage;
import com.zxing.activity.CaptureActivity;
import com.SmartPOS.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BarCodeTestActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    //    private EditText qrStrEditText;
    //    private ImageView qrImgImageView;
    private boolean isQRCode = false;
    private Bitmap bitmapForEditing;
    private static final Map<String, Item> itemInfoList = new HashMap<String, Item>();

    private ArrayList<Item> itemList;
    private ItemsAdapter adapter;

    public class ItemsAdapter extends ArrayAdapter<Item> {
        public ItemsAdapter(Context context, ArrayList<Item> items) {
            super(context, 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Item item = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_layout, parent, false);
            }
            // Lookup view for data population
            TextView itemName = (TextView) convertView.findViewById(R.id.itemName);
            TextView itemPrice = (TextView) convertView.findViewById(R.id.itemPrice);
            // Populate the data into the template view using the data object
            itemName.setText(item.itemName);
            itemPrice.setText(item.itemPrice);
            // Return the completed view to render on screen
            return convertView;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        itemList = new ArrayList<Item>();
        final ListView listView = (ListView) findViewById(R.id.list);
        adapter = new ItemsAdapter(this, itemList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // ListView Clicked item index
                int itemPosition = position;
                // ListView Clicked item value
                Item itemValue = (Item)listView.getItemAtPosition(position);
                // Show Alert
//                Toast.makeText(getApplicationContext(), "Position :" + itemPosition + "  ListItem : " + itemValue.toString(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getBaseContext(), ProductDetailPage.class);
                intent.putExtra("item", itemValue);
                startActivity(intent);
            }
        });

        //resultTextView.setText("");
//        qrStrEditText = (EditText) this.findViewById(R.id.et_qr_string);
//        qrImgImageView = (ImageView) this.findViewById(R.id.iv_qr_image);


        Button scanBarCodeButton = (Button) this.findViewById(R.id.btn_scan_barcode);
        scanBarCodeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent openCameraIntent = new Intent(BarCodeTestActivity.this, CaptureActivity.class);
                startActivityForResult(openCameraIntent, 0);
            }
        });

        itemInit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            Item itemScan = getItemInfo(scanResult);
            SetResultView(itemScan);

//            resultTextView.append(scanResult + "\n\n");
//            resultTextView.setText(scanResult + "\n");
        }
    }

    /*public boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }*/

    /**
     * initialize item list
     */
    private void itemInit() {
        // add bar code here
        itemInfoList.put("9787121102073", new Item("Book", "12.5 HKD", 1));
        itemInfoList.put("9771234567003", new Item("qazQAZ", "0.01 HKD", 2));
        itemInfoList.put("4890008123308", new Item("Fanta", "6 HKD", 3));
        itemInfoList.put("4890008400300", new Item("CreamSoda", "6 HKD", 4));
    }

    /**
     * Return item information from scanned result
     */
    public Item getItemInfo(String scanResult) {
        Item itemInfo = itemInfoList.get(scanResult);
        if (itemInfo == null)
            itemInfo = new Item("Not exist", "", 0);
        return itemInfo;
    }

    public void SetResultView(Item itemScan) {
        itemList.add(itemScan);
        adapter.notifyDataSetChanged();
    }
}