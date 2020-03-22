package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;

import com.github.axet.k2pdfopt.K2PdfOpt;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        FloatingActionButton fab = findViewById(R.id.fab);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeResource(getResources(),R.raw.zorich92, options);

        options.inSampleSize = calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        Bitmap icon = BitmapFactory.decodeResource(getResources(),R.raw.zorich99, options);

        K2PdfOpt opt = new K2PdfOpt();
        opt.create(width, height, 400);

        opt.load(icon);
        icon.recycle();


        int count = opt.getCount();

        List<Bitmap> bitmaps = new ArrayList<>();

        double ratio = ((double)width / height);
        int bitmapWidth = 0;

        int totalHeight = 0;
        for (int i=0; i<count; i++) {
            Bitmap bm = opt.renderPage(i);
            bm = Bitmap.createScaledBitmap(bm,width, (int)(height / ratio), true);
            bitmaps.add(bm);
            totalHeight += bm.getHeight();
            bitmapWidth = bm.getWidth();
        }

        Bitmap b3 = Bitmap.createBitmap(bitmapWidth, totalHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(b3);

        int drawHeight = 0;
        for (int i=0; i<count; i++) {
            Bitmap b = bitmaps.get(i);
            if (i==0) {
                canvas.drawBitmap(b, new Matrix(), new Paint());
            } else {
                canvas.drawBitmap(b, 0, drawHeight, new Paint());
            }
            drawHeight += b.getHeight();
            Paint p = new Paint();
            p.setColor(Color.BLACK);
            canvas.drawLine(0, drawHeight, width, drawHeight, p);
        }

        ImageView image = (ImageView) findViewById(R.id.imageView);
        image.setImageBitmap(b3);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
