package com.SmartPOS;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.ericssonlabs.R;

public class ProductDetailPage extends Activity {

    public class itemDetail {
        public String Name;
        public String Price;
        public String Details;
        public int imagesrc;
        public double monthlySale;
        public int itemID;

        public itemDetail(Item item) {
            this.Name = item.itemName;
            this.Price = item.itemPrice;
            this.itemID = item.itemID;

            switch (item.itemID) {
                case 3: //fanta
                    this.Details = "Fanta is a global brand of fruit-flavored carbonated soft drinks created by The Coca-Cola Company. " +
                            "There are over 100 flavors worldwide. The drink originated in Nazi Germany under trade embargo for Coca-Cola ingredients in 1940.";
                    this.imagesrc = R.drawable.fanta;
                    this.monthlySale = 1231485;
                    break;
                case 4: //cream soda
                    this.Details = "Cream soda is a sweet carbonated soft drink, often flavored with vanilla.";
                    this.imagesrc = R.drawable.cream_soda;
                    this.monthlySale =  2152316;
                    break;
                default:
                    this.Details = "";
                    this.imagesrc = R.drawable.albums_bg;
                    this.monthlySale = 0;
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail_page);

        Bundle bundle = getIntent().getExtras();
        Item item = bundle.getParcelable("item");

        ImageView image = (ImageView)findViewById(R.id.product_img);

        itemDetail chosenItem = new itemDetail(item);
        image.setImageResource(chosenItem.imagesrc);

        TextView product_name = (TextView)findViewById(R.id.product_name);
        TextView product_price = (TextView)findViewById(R.id.product_price);
        TextView product_detail = (TextView)findViewById(R.id.product_detail);
        TextView product_sale = (TextView)findViewById(R.id.product_sale);
        product_name.setText(chosenItem.Name);
        product_price.setText(chosenItem.Price);
        product_detail.setText(chosenItem.Details);
        product_sale.setText("Monthly sale: " + Double.toString(chosenItem.monthlySale));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_product_detail_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
