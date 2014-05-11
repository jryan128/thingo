package com.joysignalgames.bazingo.app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class BoardSquare extends CompoundButton {

    public BoardSquare(Context context) {
        this(context, null);
    }

    public BoardSquare(Context context, AttributeSet attrs){
        this(context,attrs,R.attr.boardSquareStyle);
    }
    public BoardSquare(Context context, AttributeSet attrs, int defStyle){
        super(context,attrs,defStyle);
    }

    //make the height equal the width so it's square!!
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(BoardSquare.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(BoardSquare.class.getName());
    }

}
