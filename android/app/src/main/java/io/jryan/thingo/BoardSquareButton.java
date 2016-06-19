package io.jryan.thingo;

import android.content.Context;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.CompoundButton;

public class BoardSquareButton extends CompoundButton {

    private static final int[] STATE_PATTERN_SELECTED = {R.attr.state_pattern_selected};
    public final Handler handler = new Handler();
    private String description = "No description.";
    private boolean isPatternSelected = false;

    public BoardSquareButton(Context context) {
        super(context, null, R.attr.boardSquareStyle);
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

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isPatternSelected) {
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
        this.isPatternSelected = isPatternSelected;
        refreshDrawableState();
    }

    static class SavedState extends BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
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
        public void writeToParcel(Parcel destination, int flags) {
            super.writeToParcel(destination, flags);
            destination.writeString(description);
        }
    }
}