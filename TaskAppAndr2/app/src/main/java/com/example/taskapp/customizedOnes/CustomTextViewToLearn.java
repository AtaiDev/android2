package com.example.taskapp.customizedOnes;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

public class CustomTextView extends AppCompatEditText {
    float paddingLeft = -1;
    public CustomTextView(@NonNull Context context) {
        super(context);
    }

    public CustomTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //this used for giving the padding exactly with width of the tag which was mentioned
        // in the phoneFragment.hml was given the customizedOnes
        calculate();
    }

        /*padding left was given manually "prefix" was assined as tag
        * widths prefix each latter
        * and getPaint its for the special for the text to paint the view
        * via forech we adding width to the variable textWidth by+=
        * and setting the padding according to the size of tag in the layout*/

    private void calculate() {
            if (paddingLeft == -1){
                String prefix = (String) getTag();
                float[] widths = new float[prefix.length()];
                getPaint().getTextWidths(prefix,widths);
                float textWidth = 0;
                for (float w:widths) {
                    textWidth +=w;
                }
                paddingLeft = getCompoundPaddingLeft();
                setPadding((int)(textWidth+paddingLeft),getPaddingRight(),getPaddingTop(),getPaddingBottom());
            }
    }

    // method which can draw the edit text ot display as we needed
    //Draw the text, with origin at (x,y), using the specified paint. The origin is interpreted
    // based on the Align setting in the paint.
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        String prefix = (String) getTag();
        canvas.drawText(prefix,paddingLeft,getLineBounds(0,null),getPaint());
    }
}
