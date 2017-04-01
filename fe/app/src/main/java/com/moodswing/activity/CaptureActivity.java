package com.moodswing.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.moodswing.MoodSwingApplication;
import com.moodswing.R;
import com.moodswing.injector.component.ApplicationComponent;
import com.moodswing.injector.component.CaptureComponent;
import com.moodswing.injector.component.DaggerCaptureComponent;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.module.CaptureModule;
import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.mvp.model.Capture;
import com.moodswing.mvp.mvp.model.Comment;
import com.moodswing.mvp.mvp.presenter.CapturePresenter;
import com.moodswing.mvp.mvp.view.CaptureView;
import com.moodswing.widget.CommentAdapter;
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

public class CaptureActivity extends AppCompatActivity implements CaptureView {

    @Inject2
    CapturePresenter _capturePresenter;

    @Inject2
    SharedPreferencesManager _sharedPreferencesManager;

    @BindView(R.id.captureImage)
    ImageView _capImage;

    @BindView(R.id.captureEmotion)
    ImageView _capEmoji;

    @BindView(R.id.cap_name)
    TextView _capName;

    @BindView(R.id.cap_date)
    TextView _capDate;

    @BindView(R.id.cap_title)
    TextView _capTitle;

    @BindView(R.id.cap_text)
    TextView _capText;

    @BindView(R.id.post_comment)
    TextView _postComment;

    @BindView(R.id.comment)
    EditText _comment;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private CaptureComponent _captureComponent;

    // Facebook
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;
    private Bitmap captureBitmap;

    String title;
    String date;
    String text;
    String displayName;
    String dateID;
    String capID;
    String capEmotion;
    String isEmotionNull;
    private List<Comment> commentList = new ArrayList<>();
    private RecyclerView commentRecyclerView;
    private CommentAdapter commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        ButterKnife.setDebug(true);
        ButterKnife.bind(this);
        ApplicationComponent applicationComponent = ((MoodSwingApplication) getApplication()).getApplicationComponent();

        commentRecyclerView = (RecyclerView) findViewById(R.id.comments_recycler_view);

        commentAdapter = new CommentAdapter(commentList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext()){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        commentRecyclerView.setLayoutManager(mLayoutManager);
        commentRecyclerView.setItemAnimator(new DefaultItemAnimator());
        commentRecyclerView.setAdapter(commentAdapter);

        _captureComponent = DaggerCaptureComponent.builder()
                .captureModule(new CaptureModule())
                .activityModule(new ActivityModule(this))
                .applicationComponent(applicationComponent)
                .build();
        _captureComponent.inject(this);


        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        initializePresenter();
        initializePostComment();

        // Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
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
        capEmotion = getIntent().getStringExtra("EXTRA_CAPEMOTION");

        if (capEmotion.isEmpty() || capEmotion.equals("UNKNOWN")){
            _capEmoji.setVisibility(View.GONE);
        }else{
            _capEmoji.setVisibility(View.VISIBLE);
            _capEmoji.setBackground(setEmoji(capEmotion));
        }

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
            captureBitmap = bitmap.copy(bitmap.getConfig(), true);
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
            commentList.add(c);
            _capturePresenter.getProfilePic(c.getCommenter(), c.getId());
        }
    }

    @Override
    public void showProfPic(ResponseBody picture, String commentId){
        Boolean hasImage = true;
        Bitmap bitmap = null;
        if (picture.contentLength() == 0) {
            hasImage = false;
        } else {
            bitmap = BitmapFactory.decodeStream(picture.byteStream());
        }
        updatePicture(bitmap, commentId, hasImage);
    }

    private void updatePicture(Bitmap bitmap, String commentId, Boolean hasImage) {
        for(Comment c: commentList){
            if (c.getId().equals(commentId)){
                c.setImage(bitmap);
                c.setHasImage(hasImage);
            }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();

        switch (id) {
            case R.id.action_share:
                View menuItemView = findViewById(R.id.action_share);
                PopupMenu popupMenu = new PopupMenu(this, menuItemView);
                popupMenu.inflate(R.menu.share_popup_menu);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.share_facebook:
                                if (ShareDialog.canShow(SharePhotoContent.class)) {
                                    SharePhoto photo = new SharePhoto.Builder()
                                            .setBitmap(captureBitmap)
                                            .build();

                                    SharePhotoContent sharePhotoContent= new SharePhotoContent.Builder()
                                            .addPhoto(photo)
                                            .setShareHashtag(new ShareHashtag.Builder()
                                                    .setHashtag("#MoodSwing")
                                                    .build())
                                            .build();
                                    shareDialog.show(sharePhotoContent);
                                    captureBitmap.recycle();
                                }
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void showToast(String s) {
        Toast.makeText(CaptureActivity.this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public Drawable setEmoji(String emotion) {
        switch (emotion) {
            case "RELAXED":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.relaxed_emoji, null);
            case "SMILEY":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.smiley_emoji, null);
            case "LAUGHING":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.laughing_emoji, null);
            case "WINK":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.wink_emoji, null);
            case "SMIRK":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.smirk_emoji, null);
            case "KISSING":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.kissing_emoji, null);
            case "STUCK_OUT_TONGUE":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.stuck_out_tongue_emoji, null);
            case "STUCK_OUT_TONGUE_WINKING_EYE":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.stuck_out_tongue_winking_eye_emoji, null);
            case "DISAPPOINTED":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.disappointed_emoji, null);
            case "RAGE":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.rage_emoji, null);
            case "SCREAM":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.scream_emoji, null);
            case "FLUSHED":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.flushed_emoji, null);
            default: // UNKNOWN
                return ResourcesCompat.getDrawable(getResources(), R.drawable.blank_emoji, null);
        }
    }
}
