package com.seaky.netframe;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.seaky.netframe.bean.DemoBean;
import com.seaky.netframe.build.HttpObserver;
import com.seaky.netframe.core.ApiService;
import com.seaky.netframe.exception.ApiException;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        sendReq();
    }

    private void sendReq() {
        HttpCenter.send(HttpCenter.getApi(ApiService.class).getDemo(), this,
                new HttpObserver<DemoBean>() {
                    @Override
                    protected void onFailure(ApiException e) {
                        Toast.makeText(TestActivity.this,e.getCode()+e.getMsg(),Toast.LENGTH_LONG).show();
                    }

                    @Override
                    protected void onSuccess(DemoBean demoBean) {
                        Toast.makeText(TestActivity.this,demoBean.getAuthor() + demoBean.isOriginal(),Toast.LENGTH_LONG).show();
                    }
                });
    }
}
