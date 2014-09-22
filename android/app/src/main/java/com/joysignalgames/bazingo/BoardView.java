package com.joysignalgames.bazingo;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class BoardView extends ViewGroup {
    public BoardView(Context context) {
        super(context);
        setId(R.id.boardView); // have to set an id, or we won't get saving
        createBoardSquares();
    }

    private void createBoardSquares() {
        for (int i = 0; i < 25; i++) {
            BoardSquareButton square = new BoardSquareButton(getContext(), null);
            // FIXME: find out if setting the id to a number like this is really okay
            // the only thing I can imagine going wrong is if the ids conflict with
            // something else and the saved state reloading goes wrong
            square.setId(i);
            addView(square);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount();
        int n = getNumberOfRowsAndCols(childCount);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (n > 0) {
            int w = width / n;
            int h = height / n;
            for (int i = 0; i < childCount; ++i) {
                // tell the child exactly what size it needs to be, screw you
                getChildAt(i).measure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
            }
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO: entire method could probably be more efficient
        int n = getNumberOfRowsAndCols(getChildCount());
        if (n > 0) {
            int w = getMeasuredWidth() / n;
            int h = getMeasuredHeight() / n;
            for (int row = 0; row < n; ++row) {
                for (int col = 0; col < n; ++col) {
                    // TODO: should use values from onMeasure, but who cares, we know what they are
                    // TODO: some calcs probably could be made more efficient with addition?
                    if (row == (n - 1) && h >= w) {
                        getChildAt((row * n) + col).layout(w * col, h * row, w * (col + 1), b);
                    } else if (col == (n - 1) && w >= h) {
                        getChildAt((row * n) + col).layout(w * col, h * row, r, h * (row + 1));
                    } else {
                        getChildAt((row * n) + col).layout(w * col, h * row, w * (col + 1), h * (row + 1));
                    }
                }
            }
        }
    }

    private int getNumberOfRowsAndCols(int totalChildCount) {
        return (int) Math.floor(Math.sqrt(totalChildCount));
    }

    // FIXME: this is a fucking mess
    public static class BoardController {
        public static void setupBoardSquareButtonListeners(final Context context, final BoardView boardView, final Patterns patterns, final BoardActivity.PointsKeeper pointsKeeper) {
            CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
                Toast previousToast = null;

                private void showPoints(String patternName) {
                    CharSequence text = String.format("%s!\nYou have %s points.", patternName,
                            Integer.toString(pointsKeeper.points));
                    showToast(text);
                }

                private void subtractPoints() {
                    CharSequence text = String.format("You have %s points.", Integer.toString(pointsKeeper.points));
                    showToast(text);
                }

                private void showToast(CharSequence text) {
                    if (previousToast != null) {
                        previousToast.cancel();
                    }

                    Toast toast = previousToast = Toast.makeText(context, text, Toast.LENGTH_LONG);
                    toast.show();
                }

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Set<Patterns.Pattern> selectedPatterns = patterns.squareSelected(buttonView.getId());
                        String delim = "";
                        for (Patterns.Pattern pattern : selectedPatterns) {
                            pointsKeeper.points += pattern.points;

                            final List<BoardSquareButton> buttons = new ArrayList<BoardSquareButton>();
                            for (Integer squareNumber : pattern.squares) {
                                BoardSquareButton button = (BoardSquareButton) boardView.getChildAt(squareNumber);
                                buttons.add(button);
                                button.setPatternSelected(true);
                            }

                            Timer myTimer = new Timer();
                            myTimer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    for (final BoardSquareButton button : buttons) {
                                        button.handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                button.setPatternSelected(false);
                                            }
                                        });
                                    }
                                }
                            }, 3500); // 3500 pulled from Toast file defaults
                        }
                        if (!selectedPatterns.isEmpty()) {
                            showPoints(patternNames.toString());
                        }
                    } else {
                        Set<Patterns.Pattern> unselectedPatterns = patterns.squareUnselected(buttonView.getId());
                        for (Patterns.Pattern pattern : unselectedPatterns) {
                            pointsKeeper.points -= pattern.points;
                        }
                        if (unselectedPatterns.size() > 0) {
                            subtractPoints();
                        }
                    }
                }
            };

            final OnLongClickListener descriptionListener = new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    BoardSquareButton button = (BoardSquareButton) v;
                    Toast toast = Toast.makeText(context, button.getDescription(), Toast.LENGTH_LONG);
                    toast.show();
                    return true;
                }
            };

            for (int i = 0; i < 25; i++) {
                BoardSquareButton button = (BoardSquareButton) boardView.getChildAt(i);
                button.setOnCheckedChangeListener(listener);
                button.setOnLongClickListener(descriptionListener);
            }
        }
    }
}
