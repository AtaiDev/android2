package com.example.taskapp.ui.board;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.taskapp.R;

import java.util.ArrayList;


public class BoardAdapter  extends RecyclerView.Adapter<BoardAdapter.ViewHolder>{

    private ArrayList<String> titleList = new ArrayList<>();
    private ArrayList<String> txtDescription = new ArrayList<>();
    private ArrayList<Integer> imageList = new ArrayList<>();

    public BoardAdapter() {

        titleList.add("Fast");
        titleList.add("Powerful");
        titleList.add("Successes");


        txtDescription.add("This data set consists of reviews of fine foods from Amazon. The data span a period of more than 10 years, " +
                "including all ~500,000 reviews up to October 2012. Reviews include product and user information, ratings, and a plaintext review. We also have " +
                "reviews from all other Amazon categories. Reviews include product and user information, ratings, and a plaintext review.");
        txtDescription.add("Just like ViewPager, ViewPager2 needs an adapter to populate it with pages. Any RecyclerView.Adapter will suffice for simple use cases, " +
                "when your pages do not have state that needs to be maintained across the Activity lifecycle. The top level View that you will inflate for your " +
                "pages must have its layout_width and layout_height set to match_parent. " +
                "If your pages do need to save state across lifecycles, " +
                "make your adapter implement the StatefulAdapter interface, or manage your own state saving (e.g., using ViewModel).");
        txtDescription.add("If you need to wait until a swipe or page transition has finished, there are two strategies you can employ: create an IdlingResource that is idle whenever the ViewPager's scroll state is idle, or create a CountDownLatch that counts down when ViewPager2 transitions to idle. " +
                "An example of an IdlingResource has been implemented in ViewPagerIdleWatcher" +
                "Some useful Espresso extensions can be found in ViewInteractions and ViewPagerActions.");

        /*resources for imageView */
        imageList.add(R.drawable.alien);
        imageList.add(R.drawable.logimage);
        imageList.add(R.drawable.avatar);


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_pager_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return titleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView textTitle;
        private TextView textDesc;
        private ImageView imageBoard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.text_title);
            textDesc = itemView.findViewById(R.id.text_description);
            imageBoard = itemView.findViewById(R.id.imageView);
        }

        public void bind(int position) {
            textTitle.setText(titleList.get(position));
            textDesc.setText(txtDescription.get(position));
            Glide.with(itemView.getContext())
                    .load(imageList.get(position))
                    .apply(RequestOptions.centerCropTransform())
                    .into(imageBoard);
        }
    }
}
