package com.joysignalgames.bazingo;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.CompoundButton;
import com.joysignalgames.bazingo.app.R;

public class BoardSquareButton extends CompoundButton {

    private String description = "";

    public BoardSquareButton(Context context) {
        super(context, null, R.attr.boardSquareStyle);
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
        SavedState ss = new SavedState(superState, description);
        return ss;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
