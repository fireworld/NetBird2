package cc.colorcat.demo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import cc.colorcat.netbird2.BitmapParser;
import cc.colorcat.netbird2.MRequest;
import cc.colorcat.netbird2.Method;

/**
 * Created by cxx on 17-3-8.
 * xx.ch@outlook.com
 */
public class DemoActivity extends AppCompatActivity {
    public static final String IMAGE_URL = "http://pic36.nipic.com/20131202/2457331_111916042339_2.jpg";
    private ImageView mImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        mImageView = (ImageView) findViewById(R.id.iv_image);
        mImageView.setOnClickListener(mClick);
    }

    private View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showImage(IMAGE_URL);
        }
    };

    private void showImage(String url) {
        int width = mImageView.getWidth();
        int height = mImageView.getHeight();
        MRequest<Bitmap> request = new MRequest.Builder<>(BitmapParser.create(width, height))
                .url(url)
                .method(Method.GET)
                .listener(new MRequest.SimpleListener<Bitmap>() {
                    @Override
                    public void onSuccess(Bitmap result) {
                        mImageView.setImageBitmap(result);
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        LogUtils.e("Demo", "code = " + code + ", msg = " + msg);
                    }
                }).build();
        ApiService.send(request);
    }
}
