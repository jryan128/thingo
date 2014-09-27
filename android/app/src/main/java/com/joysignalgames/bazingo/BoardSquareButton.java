package com.joysignalgames.bazingo;

import android.content.Context;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.CompoundButton;
import com.joysignalgames.bazingo.R;

import java.lang.reflect.Array;
import java.util.Arrays;

public class BoardSquareButton extends CompoundButton {

    public final Handler handler = new Handler();
    private String description = "No description.";
    private static final int[] STATE_PATTERN_SELECTED = {R.attr.state_pattern_selected};
    private int isPatternSelected = 0;

    public BoardSquareButton(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.boardSquareStyle);
        setTextColor(getResources().getColor(R.color.board_text));
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

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;

        super.onRestoreInstanceState(ss.getSuperState());
        description = ss.description;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, description);
    }

    static class SavedState extends BaseSavedState {
        private final String description;

        SavedState(Parcel source) {
            super(source);
            this.description = source.readString();
        }

        SavedState(Parcelable superState, String description) {
            super(superState);
            this.description = description;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(description);
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isPatternSelected > 0) {
            mergeDrawableStates(drawableState, STATE_PATTERN_SELECTED);
        }
        return drawableState;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPatternSelected(boolean isPatternSelected) {
        if (isPatternSelected) {
            this.isPatternSelected += 1;
        } else {
            this.isPatternSelected -= 1;
        }
        refreshDrawableState();
    }
}
