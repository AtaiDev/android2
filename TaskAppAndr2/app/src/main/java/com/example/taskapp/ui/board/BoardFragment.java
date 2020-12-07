package com.example.taskapp.ui.board;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.example.taskapp.R;


public class BoardFragment extends Fragment {


    private ViewPager2 viewPager;
    private LinearLayout boardingIndicator;
    private BoardAdapter adapter;
    private Button btnSkip;
    private Button btnGetIn;


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
        boardingIndicator = view.findViewById(R.id.bordingIndicators);
        btnSkip = view.findViewById(R.id.btnSkipViewPager);
        btnGetIn = view.findViewById(R.id.btnGetInViewPager);
        initView();
        boardingIndicator();
        pageChangeCallBack();
        buttonOperations();

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        requireActivity().finish();
                    }
                });
    }

    private void buttonOperations() {
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_boardFragment_to_navigation_home);
            }
        });

        btnGetIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_boardFragment_to_navigation_home);
            }
        });

    }

    private void pageChangeCallBack() {
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                /*here we're trucking the page and calling method to set active image appropriately*/
                setActivePageIndicator(position);
                if (position == 2) {
                    btnGetIn.setVisibility(View.VISIBLE);
                    btnSkip.setVisibility(View.GONE);
                } else {
                    btnGetIn.setVisibility(View.GONE);
                    btnSkip.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    /*gets the total number of pages and sets the view to LinerLayout.LayoutParams
     * fori which is down below goes through of the pages which we have and sets the images which we created for inactive dots*/
    private void boardingIndicator() {
//        //Returns the total number of items in the data set held by the adapter.
        ImageView[] indicators = new ImageView[adapter.getItemCount()];
//        //followed code tells linerlayout height and width how much to be , this case ---> WRAP_CONTENT
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
//        // here gives a margin between the dots , in order left,top,right,bottom
        layoutParams.setMargins(80, 0, 80, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(requireContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.onboarding_dots_inactive));
            indicators[i].setLayoutParams(layoutParams);
            boardingIndicator.addView(indicators[i]);
        }
    }

    private void setActivePageIndicator(int index) {
        // assigning the count of which holds boardingIndicator;
        int countPage = boardingIndicator.getChildCount();
        for (int i = 0; i < countPage; i++) {
            ImageView imageView = (ImageView) boardingIndicator.getChildAt(i);//<---- gets the specific position which page at viewpager;
            // obvious one , if current page equals to index than set  onboarding_dots_active esle onboarding_dots_inactive
            if (i == index)
                imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.onboarding_dots_active));
            else
                imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.onboarding_dots_inactive));
        }
    }

    private void initView() {
        adapter = new BoardAdapter();
        viewPager.setAdapter(adapter);
    }

}