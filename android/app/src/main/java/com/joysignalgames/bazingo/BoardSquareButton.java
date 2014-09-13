package com.joysignalgames.bazingo;

import android.content.Context;
import android.graphics.Color;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.CompoundButton;
import com.joysignalgames.bazingo.app.R;

public class BoardSquareButton extends CompoundButton {

    public BoardSquareButton(Context context) {
        super(context, null, R.attr.boardSquareStyle);
        setTextColor(Color.parseColor("white"));
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(BoardSquareButton.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(BoardSquareButton.class.getName());
    }
}
