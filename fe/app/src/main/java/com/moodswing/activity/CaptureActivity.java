package com.moodswing.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.moodswing.mvp.mvp.model.Comment;
import com.moodswing.mvp.mvp.presenter.CapturePresenter;
import com.moodswing.mvp.mvp.view.CaptureView;
import com.moodswing.widget.CommentAdapter;
import com.moodswing.widget.DateBlock;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    String title;
    String date;
    String text;
    String displayName;
    String dateID;
    String capID;
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
//        initializeBottomNavigationView();

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
        Boolean hasImage = true;
        try {
            if (picture.contentLength() == 0){
                hasImage = false;
            }
            String cId = capID; //TODO: fix this
            String[] type = picture.contentType().toString().split("/");
            File entryPictureFile = new File(getExternalFilesDir(null) + File.separator + cId + "captureactivity." + type[1]);
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];
                long fileSize = picture.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = picture.byteStream();
                outputStream = new FileOutputStream(entryPictureFile);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                }
                //SET PICTURE TO IMAGEVIEW
                Uri uri = Uri.fromFile(entryPictureFile);
                if (hasImage){
                    _capImage.setBackgroundResource(android.R.color.transparent);
                    _capImage.setImageURI(uri);
                }else{
                    _capImage.setBackgroundResource(R.drawable.empty_profile_pic);
                }
                outputStream.flush();
            } catch (IOException e) {

            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {

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
                                    // TODO change to Bitmap in Capture
                                    Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
                                    SharePhoto photo = new SharePhoto.Builder()
                                            .setBitmap(image)
                                            .build();

                                    SharePhotoContent sharePhotoContent= new SharePhotoContent.Builder()
                                            .addPhoto(photo)
                                            .setShareHashtag(new ShareHashtag.Builder()
                                                    .setHashtag("#MoodSwing")
                                                    .build())
                                            .build();
                                    shareDialog.show(sharePhotoContent);
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
}
