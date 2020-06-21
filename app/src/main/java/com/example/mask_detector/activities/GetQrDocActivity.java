package com.example.mask_detector.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mask_detector.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class GetQrDocActivity extends AppCompatActivity {
    String url;
    ProgressBar pb;
    ImageView ivDoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_qr_doc);
        if (getIntent().getExtras() != null)
            url = getIntent().getExtras().getString("url");
        pb = findViewById(R.id.pb);
        ivDoc = findViewById(R.id.document);
        loadDocument(url);
    }

    private void loadDocument(String url) {
        Picasso.get()
                .load(url)
                .fit()
                .centerCrop()
                .into(ivDoc, new Callback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(GetQrDocActivity.this, "Image Document loaded successfully", Toast.LENGTH_SHORT).show();
                        pb.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(GetQrDocActivity.this, "QR code must refer to an image", Toast.LENGTH_LONG).show();
                        Toast.makeText(GetQrDocActivity.this, "Showing dummy document for now", Toast.LENGTH_LONG).show();
                        ivDoc.setImageResource(R.drawable.wb);
                        pb.setVisibility(View.GONE);
                    }
                });
    }
}