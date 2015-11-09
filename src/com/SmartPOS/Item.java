package com.SmartPOS;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by v-shayi on 11/9/2015.
 */
public class Item implements Parcelable {
    public String itemName;
    public String itemPrice;
    public int itemID;

    public Item(String itemName, String itemPrice, int itemID) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemID = itemID;
    }
    @Override public String toString() { return "Name: " + itemName + ", Price: " + itemPrice; }

    protected Item(Parcel in) {
        itemName = in.readString();
        itemPrice = in.readString();
        itemID = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemName);
        dest.writeString(itemPrice);
        dest.writeInt(itemID);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}