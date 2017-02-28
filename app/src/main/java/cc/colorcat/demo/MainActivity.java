package cc.colorcat.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import cc.colorcat.netbird2.request.SimpleRequestListener;
import cc.colorcat.netbird2.request.Method;
import cc.colorcat.netbird2.request.Request;
import okhttp3.OkHttpClient;


/**
 * Created by mic on 16-2-29.
 * xx.ch@outlook.com
 */
public class MainActivity extends Activity {
    // 请求网址:http://www.imooc.com/api/teacher?type=4&num=30
    public static final String HOST = "http://www.imooc.com/api";

    private List<Course> mList = new ArrayList<>();
    private BaseAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new OkHttpClient();
        initListView();
        initData();
    }

    @Override
    protected void onDestroy() {
        ApiService.cancelAll();
        super.onDestroy();
    }

    private void initListView() {
        ListView listView = (ListView) findViewById(R.id.lv_main);
        mAdapter = new CommonBaseAdapter<Course>(this, mList, R.layout.adapter_layout) {
            @Override
            public void convert(ViewHolder holder, Course courseBean) {
                holder.setText(R.id.tv_name, courseBean.getName())
                        .setText(R.id.tv_description, courseBean.getDescription());

                final ImageView imageView = holder.getView(R.id.iv_icon);
                ApiService.display(imageView, courseBean.getPicBig());
            }
        };
        listView.setAdapter(mAdapter);
    }

    private void initData() {
        TypeToken<Result<List<Course>>> token = new TypeToken<Result<List<Course>>>() {};
        final Request<Result<List<Course>>> request = new Request.Builder<>(new GsonParser<>(token))
                .url(HOST)
                .path("/teacher")
                .method(Method.GET)
                .add("type", Integer.toString(4))
                .add("num", Integer.toString(30))
                .listener(new SimpleRequestListener<Result<List<Course>>>() {
                    @Override
                    public void onSuccess(@NonNull Result<List<Course>> result) {
                        mList.clear();
                        mList.addAll(result.getData());
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int code, @NonNull String msg) {

                    }
                }).build();
        ApiService.call(request);
    }
}
