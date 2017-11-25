package io.jryan.thingo;

import android.content.Context;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.CompoundButton;

public class BoardSquareButton extends CompoundButton {

//    private String description = "No description."; // TODO: remove if unused

    public BoardSquareButton(Context context, int i) {
        super(context, null, R.attr.boardSquareStyle);
        setId(i); // each board square gets an id for which square it on the Board
        setFreezesText(true); // without this, the button's text is not frozen, and all the buttons lose their phrases
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
    //    @Override
//    public void onRestoreInstanceState(Parcelable state) {
//        SavedState ss = (SavedState) state;
//
//        super.onRestoreInstanceState(ss.getSuperState());
//        description = ss.description;
//    }
//
//    @Override
//    public Parcelable onSaveInstanceState() {
//        Parcelable superState = super.onSaveInstanceState();
//        return new SavedState(superState, description);
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    static class SavedState extends BaseSavedState {
//        public static final Parcelable.Creator<SavedState> CREATOR
//                = new Parcelable.Creator<SavedState>() {
//            public SavedState createFromParcel(Parcel in) {
//                return new SavedState(in);
//            }
//
//            public SavedState[] newArray(int size) {
//                return new SavedState[size];
//            }
//        };
//        private final String description;
//
//        SavedState(Parcel source) {
//            super(source);
//            this.description = source.readString();
//        }
//
//        SavedState(Parcelable superState, String description) {
//            super(superState);
//            this.description = description;
//        }
//
//        @Override
//        public void writeToParcel(Parcel destination, int flags) {
//            super.writeToParcel(destination, flags);
//            destination.writeString(description);
//        }
//    }
}