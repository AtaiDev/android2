package com.example.taskapp.ui.board;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;

import com.example.taskapp.MainActivity;
import com.example.taskapp.R;

import com.example.taskapp.interfaces.CloseFragment;
import com.example.taskapp.utils.Prefs;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;


public class BoardFragment extends Fragment {


    private ViewPager2 viewPager;
    //    private LinearLayout boardingIndicator;
    private BoardAdapter adapter;
    private Button btnSkip;
    private TabLayout tabLayoutDots;
    private SpringDotsIndicator dotsIndicator;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_board, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = view.findViewById(R.id.viewPager);
//        boardingIndicator = view.findViewById(R.id.bordingIndicators); linerLayout
        btnSkip = view.findViewById(R.id.btnSkipViewPager);
//        tabLayoutDots = view.findViewById(R.id.tabDots);
        dotsIndicator = (SpringDotsIndicator) view.findViewById(R.id.spring_dots_indicator);

        initView();
//        boardingIndicator(); method same as the setActivePageIndicator , hand Made
        pageChangeCallBack();
        clickListener();

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        requireActivity().finish();
                    }
                });
    }

    private void clickListener() {
        getView().findViewById(R.id.btnSkipViewPager).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Prefs(requireContext()).showStatus();
                ((MainActivity) requireActivity()).closeFragment();
            }
        });

    }

    private void pageChangeCallBack() {
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                /*here we're trucking the page and calling method to set active image appropriately*/
//                setActivePageIndicator(position);/*related to the hand made dots*/
                if (position == 2) btnSkip.setVisibility(View.GONE);
                else btnSkip.setVisibility(View.VISIBLE);

            }
        });


    }


    /*gets the total number of pages and sets the view to LinerLayout.LayoutParams
     * fori which is down below goes through of the pages which we have and sets the images which we created for inactive dots*/
//    private void boardingIndicator() {
////        //Returns the total number of items in the data set held by the adapter.
//        ImageView[] indicators = new ImageView[adapter.getItemCount()];
////        //followed code tells linerlayout height and width how much to be , this case ---> WRAP_CONTENT
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
//        );
////        // here gives a margin between the dots , in order left,top,right,bottom
//        layoutParams.setMargins(80, 0, 80, 0);
//        for (int i = 0; i < indicators.length; i++) {
//            indicators[i] = new ImageView(requireContext());
//            indicators[i].setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.onboarding_dots_inactive));
//            indicators[i].setLayoutParams(layoutParams);
//            boardingIndicator.addView(indicators[i]);
//        }
//    }
//
//    private void setActivePageIndicator(int index) {
//        // assigning the count of which holds boardingIndicator;
//        int countPage = boardingIndicator.getChildCount();
//        for (int i = 0; i < countPage; i++) {
//            ImageView imageView = (ImageView) boardingIndicator.getChildAt(i);//<---- gets the specific position which page at viewpager;
//            // obvious one , if current page equals to index than set  onboarding_dots_active esle onboarding_dots_inactive
//            if (i == index)
//                imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.onboarding_dots_active));
//            else
//                imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.onboarding_dots_inactive));
//        }
//    }

    private void initView() {
        adapter = new BoardAdapter();
        viewPager.setAdapter(adapter);
        dotsIndicator.setViewPager2(viewPager);
        btnClickListener();



        /*this down here for the way of adding tablayout in XML layout */
//        new TabLayoutMediator(tabLayoutDots, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
//            @Override
//            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
//                tab.setIcon(R.drawable.tab_selector);
//            }
//        }).attach();
    }

    private void btnClickListener() {
        adapter.setClickListener(new CloseFragment() {
            @Override
            public void toCloseFragment() {
                new Prefs(requireContext()).showStatus();
                ((MainActivity) requireActivity()).closeFragment();
            }
        });
    }
}