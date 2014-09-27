package com.joysignalgames.bazingo;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class BoardController {
    private final Context context;
    private final BoardView boardView;
    private final Patterns patterns;
    private final BoardActivity.PointsKeeper pointsKeeper;

    public BoardController(Context context, BoardView boardView, Patterns patterns, BoardActivity.PointsKeeper pointsKeeper) {
        this.context = context;
        this.boardView = boardView;
        this.patterns = patterns;
        this.pointsKeeper = pointsKeeper;
    }

    public void setupBoardSquareButtonListeners() {
        CompoundButton.OnCheckedChangeListener checkedChangeListener = new OnBoardSquareCheckListener();

        final View.OnLongClickListener descriptionListener = new View.OnLongClickListener() {
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
            button.setOnCheckedChangeListener(checkedChangeListener);
            button.setOnLongClickListener(descriptionListener);
        }
    }

    /**
     * Handles all board square checking, pattern recognition,
     * and the reactions the GUI makes to newly made (or unmade) patterns.
     */
    private class OnBoardSquareCheckListener implements CompoundButton.OnCheckedChangeListener {
        private Toaster toaster = new Toaster(); // Teehee.
        private boolean isBoardEnabled = true; // The board is disabled when newly made patterns are being shown to the user.

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isButtonChecked) {
            if (isBoardEnabled) {
                if (isButtonChecked) {
                    displayAllNewlyMadePatterns(buttonView);
                } else {
                    handleAllNewlyLostPatterns(buttonView);
                }
            } else {
                // if the board is disabled, undo what the user did
                buttonView.setChecked(!isButtonChecked);
            }
        }

        private void displayAllNewlyMadePatterns(CompoundButton buttonView) {
            List<Patterns.Pattern> newlyMadePatterns = new ArrayList<Patterns.Pattern>(patterns.squareSelected(buttonView.getId()));
            if (!newlyMadePatterns.isEmpty()) {
                isBoardEnabled = false; // Disable the board, since we have patterns to show.

                Timer timer = new Timer();
                ShowMadePatternsTask showPatternsTask = new ShowMadePatternsTask(timer, newlyMadePatterns);

                // Run the ShowMadePatternsTask every 2 seconds, until it runs out of patterns
                // and cancels itself.
                timer.schedule(showPatternsTask, 0, 2000); // 2000 (LENGTH_SHORT) pulled from Toast file defaults
            }
        }

        private void handleAllNewlyLostPatterns(CompoundButton buttonView) {
            Set<Patterns.Pattern> unmadePatterns = patterns.squareUnselected(buttonView.getId());
            for (Patterns.Pattern pattern : unmadePatterns) {
                pointsKeeper.points -= pattern.points;
            }
            if (unmadePatterns.size() > 0) {
                toaster.toastUpdatedPoints();
            }
        }

        /**
         * Shows (in sequence) the patterns the user has just made (by checking a square) by highlighting
         * the squares that make up said pattern, displaying the name of the pattern, and showing
         * the user their points.
         */
        private class ShowMadePatternsTask extends TimerTask {
            private final Timer timer;
            private final Deque<Patterns.Pattern> patternQueue;
            private List<BoardSquareButton> previouslyHighlightedButtons = null;

            public ShowMadePatternsTask(Timer timer, List<Patterns.Pattern> newlyMadePatternsToShow) {
                this.timer = timer;
                patternQueue = new ArrayDeque<Patterns.Pattern>(newlyMadePatternsToShow);
            }

            @Override
            public void run() {
                revertPreviouslyHighlightedButtonsToNormal();

                Patterns.Pattern nextPatternToDisplay = patternQueue.pollFirst();
                if (nextPatternToDisplay != null) {
                    handleNextPatternDisplay(nextPatternToDisplay);
                } else {
                    stopPatternDisplay();
                }
            }

            private void revertPreviouslyHighlightedButtonsToNormal() {
                if (previouslyHighlightedButtons != null) {
                    for (final BoardSquareButton previousPatternButton : previouslyHighlightedButtons) {
                        previousPatternButton.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                previousPatternButton.setPatternSelected(false);
                            }
                        });
                    }
                }
            }

            private void handleNextPatternDisplay(final Patterns.Pattern pattern) {
                pointsKeeper.points += pattern.points;
                List<BoardSquareButton> newButtons = new ArrayList<BoardSquareButton>();
                for (Integer squareNumber : pattern.squares) {
                    final BoardSquareButton button = (BoardSquareButton) boardView.getChildAt(squareNumber);
                    button.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            button.setPatternSelected(true);
                        }
                    });
                    newButtons.add(button);
                }
                boardView.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        toaster.toastPatternAndPoints(pattern.name);
                    }
                });
                previouslyHighlightedButtons = newButtons;
            }

            private void stopPatternDisplay() {
                isBoardEnabled = true;
                timer.cancel();
                timer.purge();
            }
        }

        /**
         * Handles <code>Toast</code>ing the points and pattern names.
         */
        private class Toaster {  // Teehee
            private Toast previousToast = null;

            private void toastPatternAndPoints(String patternName) {
                displayToast(String.format("%s!\nYou have %s points.", patternName,
                        Integer.toString(pointsKeeper.points)));
            }

            private void toastUpdatedPoints() {
                displayToast(String.format("You have %s points.", Integer.toString(pointsKeeper.points)));
            }

            private void displayToast(String text) {
                if (previousToast != null) {
                    previousToast.cancel();
                }

                Toast toast = previousToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
}
