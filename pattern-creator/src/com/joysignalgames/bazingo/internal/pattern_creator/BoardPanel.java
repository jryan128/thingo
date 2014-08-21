package com.joysignalgames.bazingo.internal.pattern_creator;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BoardPanel extends JPanel {
    private final List<BoardSquare> boardSquares = new ArrayList<>(25);

    public BoardPanel() {
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(256, 256));

        createBoardSquares();
        createSpacers();
    }

    private void createBoardSquares() {
        for (int y=1; y <= 5; ++y) {
            for (int x=1; x <= 5; ++x) {
                GridBagConstraints gbc = new GridBagConstraints();
                BoardSquare boardSquare = new BoardSquare();
                gbc.gridx = x;
                gbc.gridy = y;
                gbc.weightx = 1.0;
                gbc.weighty = 1.0;
                gbc.fill = GridBagConstraints.BOTH;
                add(boardSquare, gbc);
                boardSquares.add(boardSquare);
            }
        }
    }

    public List<BoardSquare> getBoardSquares() {
        return boardSquares;
    }

    public void setSquares(Set<Integer> squares) {
        for (BoardSquare boardSquare : boardSquares) {
            boardSquare.setSelected(false);
        }
        for (Integer square : squares) {
            boardSquares.get(square).setSelected(true);
        }
    }

    public final Set<Integer> getSquares() {
        Set<Integer> ret = new HashSet<>();
        int i = 0;
        for (BoardSquare square : boardSquares) {
            if (square.isSelected()) {
                ret.add(i);
            }
            i += 1;
        }
        return ret;
    }

    private void createSpacers() {
        GridBagConstraints gbc;
        final JPanel spacer8 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.add(spacer8, gbc);
        final JPanel spacer9 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.add(spacer9, gbc);
        final JPanel spacer10 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        this.add(spacer10, gbc);
        final JPanel spacer11 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.VERTICAL;
        this.add(spacer11, gbc);
    }
}
