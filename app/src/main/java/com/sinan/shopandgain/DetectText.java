package com.sinan.shopandgain;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

public class DetectText {

    final static String[] products = {"gillette", "pantene", "orkid", "ipana","orchid","trafalgar","gilette"}; //This can be changed to better approach


    public static Boolean getText(Context context,Bitmap bitmap){   //Detects text

        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();

        if(!textRecognizer.isOperational()){
            return false;
        }else{
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();

            SparseArray<TextBlock> items = textRecognizer.detect(frame);

            StringBuilder sb = new StringBuilder();

           for(int i = 0 ; i<items.size();i++){
                TextBlock myItem = items.valueAt(i);
                sb.append(myItem.getValue());
            }

            return checkProducts(sb.toString());

        }
    }

    public static Boolean checkProducts(String str){

        str=str.toLowerCase();

        for(int i = 0 ; i<products.length;i++){
            if(str.contains(products[i])){
                return true;
            }
        }

        return false;
    }

}