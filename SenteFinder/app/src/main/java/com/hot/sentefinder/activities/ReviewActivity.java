package com.hot.sentefinder.activities;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.hot.sentefinder.R;
import com.hot.sentefinder.models.FeedBack;
import com.hot.sentefinder.network.APIClientInterface;
import com.hot.sentefinder.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ReviewActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RatingBar ratingBar;
    private EditText commentText;
    private Button submitButton;
    float ratingValue;
    String commentValue= "";
    private static final String TAG_FSP_ID = "TAG_FSP_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(R.string.review);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ratingBar = (RatingBar)findViewById(R.id.fsp_rating);
        commentText = (EditText)findViewById(R.id.fsp_comment);
        submitButton = (Button)findViewById(R.id.fsp_review_submit_button);

        Bundle bundle = getIntent().getExtras();
        final long fspId = bundle.getLong(TAG_FSP_ID);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingValue = rating;

            }
        });

        commentText.addTextChangedListener(new AppTextWatcher(commentText));

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ratingValue > 0 || !commentValue.isEmpty()){
                    FeedBack feedBack = new FeedBack(fspId, (int)ratingValue, commentValue);
                    sendFeedBack(feedBack);
                    hideKeyboard(ReviewActivity.this);
                }else{
                    requestFocus(commentText);
                }
            }
        });


    }

    private void sendFeedBack(FeedBack feedBack) {
        APIClientInterface retrofitClient = RetrofitClient.getClient().create(APIClientInterface.class);
        Call<FeedBack> call = retrofitClient.sendFeedBack(feedBack);
        call.enqueue(new Callback<FeedBack>() {
            @Override
            public void onResponse(Call<FeedBack> call, Response<FeedBack> response) {
                if(response.isSuccessful()){
                    Toast.makeText(ReviewActivity.this, "Your review has been posted", Toast.LENGTH_LONG).show();
                    ReviewActivity.this.finish();
                }else{
                    if(response.body() != null){
                        Toast.makeText(ReviewActivity.this, response.message(), Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<FeedBack> call, Throwable t) {
                Toast.makeText(ReviewActivity.this, "Unable to send review, try again later", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void hideKeyboard(Activity activity) {
        if (activity == null || activity.getCurrentFocus() == null) {
            return;
        }

        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    private class AppTextWatcher implements TextWatcher {
        private View view;

        private AppTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (view.getId() == R.id.fsp_comment) {
                commentValue = commentText.getText().toString();
            }
        }

    }
}
