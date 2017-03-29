package com.moodswing.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.moodswing.MoodSwingApplication;
import com.moodswing.R;
import com.moodswing.injector.component.ApplicationComponent;
import com.moodswing.injector.component.CaptureComponentOther;
import com.moodswing.injector.component.DaggerCaptureComponentOther;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.module.CaptureModuleOther;
import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.mvp.model.Comment;
import com.moodswing.mvp.mvp.presenter.CapturePresenterOther;
import com.moodswing.mvp.mvp.view.CaptureViewOther;
import com.moodswing.widget.CommentAdapterOther;
import com.moodswing.widget.DateBlock;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject2;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;

import static android.text.TextUtils.isEmpty;

/**
 * Created by Matthew on 2017-03-12.
 */

public class CaptureActivityOther extends AppCompatActivity implements CaptureViewOther {

    @Inject2
    CapturePresenterOther _capturePresenter;

    @Inject2
    SharedPreferencesManager _sharedPreferencesManager;

    @BindView(R.id.captureImageother)
    ImageView _capImage;

    @BindView(R.id.cap_nameother)
    TextView _capName;

    @BindView(R.id.cap_dateother)
    TextView _capDate;

    @BindView(R.id.cap_titleother)
    TextView _capTitle;

    @BindView(R.id.cap_textother)
    TextView _capText;

    @BindView(R.id.post_commentother)
    TextView _postComment;

    @BindView(R.id.commentother)
    EditText _comment;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private CaptureComponentOther _captureComponentOther;

    String title;
    String date;
    String text;
    String displayName;
    String dateID;
    String capID;
    private List<Comment> commentList = new ArrayList<>();
    private RecyclerView commentRecyclerView;
    private CommentAdapterOther commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captureother);
        ButterKnife.setDebug(true);
        ButterKnife.bind(this);
        ApplicationComponent applicationComponent = ((MoodSwingApplication) getApplication()).getApplicationComponent();

        commentRecyclerView = (RecyclerView) findViewById(R.id.comments_recycler_viewother);

        commentAdapter = new CommentAdapterOther(commentList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext()){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        commentRecyclerView.setLayoutManager(mLayoutManager);
        commentRecyclerView.setItemAnimator(new DefaultItemAnimator());
        commentRecyclerView.setAdapter(commentAdapter);

        _captureComponentOther = DaggerCaptureComponentOther.builder()
                .captureModuleOther(new CaptureModuleOther())
                .activityModule(new ActivityModule(this))
                .applicationComponent(applicationComponent)
                .build();
        _captureComponentOther.inject(this);


        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        initializePresenter();
        initializePostComment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        title = getIntent().getStringExtra("EXTRA_TITLE");
        date = getIntent().getStringExtra("EXTRA_DATE");
        text = getIntent().getStringExtra("EXTRA_TEXT");
        displayName = getIntent().getStringExtra("EXTRA_DISPLAYNAME");
        dateID = getIntent().getStringExtra("EXTRA_DATEID");
        capID = getIntent().getStringExtra("EXTRA_CAPID");

        setTitle(title);

        _capName.setText(displayName);
        _capDate.setText(date);
        _capTitle.setText(title);
        _capText.setText(text);
        _comment.setHint("Write a comment...");
        getPic();
        getComments();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initializePresenter() {
        _capturePresenter.attachView(this);
        _capturePresenter.attachSharedPreferencesManager(_sharedPreferencesManager);
    }


    public void initializePostComment(){
        _postComment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String comment = _comment.getText().toString();
                if(!isEmpty(comment)){
                    postComment(comment);
                }
                else{
                    Toast.makeText(getBaseContext(), "Empty Comment", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void postComment(String comment) {
        _capturePresenter.postComment(comment, dateID);
    }

    private void getComments() {
        commentList.clear();
        _capturePresenter.getComments(dateID);
    }

    private void getPic(){
        _capturePresenter.getPic(capID);
    }

    @Override
    public void showEntryPic(ResponseBody picture){
        if (picture.contentLength() == 0) {
            _capImage.setVisibility(View.GONE);
        } else {
            Bitmap bitmap = BitmapFactory.decodeStream(picture.byteStream());
            _capImage.setBackgroundResource(android.R.color.transparent);
            _capImage.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onPostCommentSuccess() {
        getComments();
        _comment.setText("");
        _comment.clearFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(_comment.getWindowToken(), 0);
    }

    @Override
    public void showComments(DateBlock dateBlock) {
        List<Comment> comments = dateBlock.getComments();
        for(Comment c: comments){
            Comment comment = new Comment(c.getDisplayName(), c.getText());
            commentList.add(comment);
        }
        commentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPostCommentFailure() {
        String message = "Post comment failure";
        showToast(message);
    }

    @Override
    public void onGetCommentFailure() {
        String message = "Get comment failure";
        showToast(message);
    }

    @Override
    public void showError1() {
        String message = "Post comment error";
        showToast(message);
    }

    @Override
    public void showError2() {
        String message = "Get comment error";
        showToast(message);
    }

    private void showToast(String s) {
        Toast.makeText(CaptureActivityOther.this, s, Toast.LENGTH_LONG).show();
    }
}