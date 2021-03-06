package com.moodswing.widget;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moodswing.R;
import com.moodswing.mvp.mvp.model.Comment;

import java.util.List;

/**
 * Created by Matthew on 2017-03-18.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    private List<Comment> commentList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView _commentText;
        public TextView _commentName;
        public ImageView _commentPic;

        public MyViewHolder(View view) {
            super(view);
            _commentText = (TextView) view.findViewById(R.id.comment_text);
            _commentName = (TextView) view.findViewById(R.id.comment_name);
            _commentPic = (ImageView) view.findViewById(R.id.comment_pic);
        }
    }


    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder._commentText.setText(comment.getText());
        holder._commentName.setText(comment.getDisplayName());
        if (comment.getHasImage()){
            holder._commentPic.setBackgroundResource(android.R.color.transparent);
            holder._commentPic.setImageBitmap(comment.getImage());
        }else{
            holder._commentPic.setBackgroundResource(R.drawable.empty_profile_pic);
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }
}
