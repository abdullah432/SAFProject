package com.example.safproject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    private TextView videoTitle;
    private LinearLayout deleteLayout;
    private int videoPosition;

    public interface bottomSheetListner{
        void deleteVideoFromList();
    }

    bottomSheetListner listner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheetlayout,container,false);

        videoTitle = view.findViewById(R.id.videotitle);
        deleteLayout = view.findViewById(R.id.deleteLayout);
        
        setBottomSheetTitle();

        deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.deleteVideoFromList();
                //if you want to dismiss dialog after action performed
                dismiss();
            }
        });

        return view;
    }

    private void setBottomSheetTitle() {
        //so know we can set title of video
        videoTitle.setText(Constant.allMediaList.get(videoPosition).getName());
    }

    void setVideoPosition(int position){
        videoPosition = position;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listner = (bottomSheetListner) context;
        }catch (ClassCastException e){
            throw  new ClassCastException(context.toString() + "must implement BottomSheetListner");
        }
    }
}
