package com.example.umorli;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ProgressBar mProgressBar;
    private TextView mTextView;
    RecyclerView mRecyclerView;
    List<UPost> mPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.textView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
        mPosts = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        UmoriliAdapter adapter = new UmoriliAdapter(mPosts);
        mRecyclerView.setAdapter(adapter);
    }
    public void OnClick(View view)    {
        mProgressBar.setVisibility(View.VISIBLE);
        UmoriliService umoriliService = UmoriliService.retrofit.create(UmoriliService.class);

        // Выводим для проверки только три поста
        final Call<List<UPost>> call = umoriliService.getData("bash", 3);

        call.enqueue((new Callback<List<UPost>>() {
            @Override
            public void onResponse(Call<List<UPost>> call, Response<List<UPost>> response) {
                // response.isSuccessfull() возвращает true если код ответа 2xx
                if (response.isSuccessful()) {
                    // Выводим посты по отдельности
                    for (int i = 0; i < response.body().size(); i++) {
                        mTextView.append(response.body().get(i).getElementPureHtml() + "\n");
                    }

                    mProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    int statusCode = response.code();
                    // Обрабатываем ошибку
                    ResponseBody errorBody = response.errorBody();
                    try {
                        mTextView.setText(errorBody.string());
                        mProgressBar.setVisibility(View.INVISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<UPost>> call, Throwable throwable) {
                mTextView.setText("Что-то пошло не так: " + throwable.getMessage());
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        }));

    }
}