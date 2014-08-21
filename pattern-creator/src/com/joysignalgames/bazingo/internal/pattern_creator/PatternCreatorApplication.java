package com.joysignalgames.bazingo.internal.pattern_creator;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

// TODO: make class not huge
public class PatternCreatorApplication {
    private JPanel mainContentPane;
    private PatternList patternList;
    private JSpinner pointsSpinner;
    private JButton newPatternButton;
    private JTextField nameTextField;
    private BoardPanel boardPanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGui();
            }

            private void createAndShowGui() {
                // TODO: refactor frame into the application class
                JFrame frame = new JFrame("Bazingo Pattern Creator");
                PatternCreatorApplication app = new PatternCreatorApplication();
                frame.setContentPane(app.mainContentPane);
                frame.setJMenuBar(new MenuBar(frame, app));
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        mainContentPane = new JPanel();
        mainContentPane.setLayout(new BorderLayout(0, 0));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        panel1.setPreferredSize(new Dimension(200, 256));
        mainContentPane.add(panel1, BorderLayout.WEST);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        panel1.add(panel2, BorderLayout.NORTH);
        final JLabel label1 = new JLabel();
        label1.setText("Points:");
        label1.setDisplayedMnemonic('P');
        label1.setDisplayedMnemonicIndex(0);
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel2.add(label1, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(pointsSpinner, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(spacer1, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(spacer2, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(spacer3, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel2.add(spacer4, gbc);
        newPatternButton = new JButton();
        newPatternButton.setText("New Pattern");
        newPatternButton.setMnemonic('N');
        newPatternButton.setDisplayedMnemonicIndex(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(newPatternButton, gbc);
        final JPanel spacer5 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel2.add(spacer5, gbc);
        final JPanel spacer6 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel2.add(spacer6, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Name:");
        label2.setDisplayedMnemonic('A');
        label2.setDisplayedMnemonicIndex(1);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel2.add(label2, gbc);
        nameTextField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(nameTextField, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        panel1.add(panel3, BorderLayout.CENTER);
        final JScrollPane scrollPane1 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(scrollPane1, gbc);
        scrollPane1.setViewportView(patternList);
        final JPanel spacer7 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel3.add(spacer7, gbc);
        boardPanel = new BoardPanel();
        mainContentPane.add(boardPanel, BorderLayout.CENTER);
        label1.setLabelFor(pointsSpinner);
        label2.setLabelFor(nameTextField);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainContentPane;
    }

    private static class MenuBar extends JMenuBar {
        private class SaveAction extends AbstractAction {
            final PatternCreatorApplication app;

            private SaveAction(PatternCreatorApplication app) {
                super("Save");
                this.app = app;
            }

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                invoke();
            }

            public void invoke() {
                try {
                    PatternFileLoader.savePatternFile(app.getPatternCollection());
                } catch (Pattern.InvalidPatternArguments | IOException invalidPatternArguments) {
                    // TODO: error dialogs
                    invalidPatternArguments.printStackTrace();
                }
            }
        }

        // TODO: break things out into own methods
        private MenuBar(final JFrame frame, final PatternCreatorApplication app) {
            // file menu
            JMenu file = new JMenu("File");
            file.setMnemonic(KeyEvent.VK_F);
            add(file);

            // save
            JMenuItem save = new JMenuItem("Save");
            save.setMnemonic(KeyEvent.VK_S);
            final SaveAction saveAction = new SaveAction(app);
            save.setAction(saveAction);
            save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
            file.add(save);

            file.addSeparator();

            // exit
            JMenuItem exit = new JMenuItem("Exit");
            exit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    int result = JOptionPane.showOptionDialog(null, "Save before exiting?", "", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
                    if (JOptionPane.YES_OPTION == result) {
                        saveAction.invoke();
                    } else if (JOptionPane.NO_OPTION == result) {
                        frame.dispose();
                    }
                }

            });
            file.add(exit);
        }
    }

    private Collection<Pattern> getPatternCollection() {
        int size = patternList.getModel().getSize();
        Collection<Pattern> patterns = new ArrayList<>(size);
        for (int i = 0; i < size; ++i) {
            patterns.add(patternList.getModel().getElementAt(i));
        }
        return patterns;
    }

    public PatternCreatorApplication() {
        $$$setupUI$$$();
        createControllers();
    }

    private void createUIComponents() {
        try {
            patternList = new PatternList();
        } catch (IOException | PatternFileLoader.PatternFileParseException e) {
            throw new RuntimeException("Error loading pattern list.", e);
        }
        pointsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
    }

    private void createControllers() {
        // TODO: error dialogs
        createPatternListController();
        createBoardSquareController();
        createNewPatternButtonController();
        createNameFieldController();
        createPointsSpinnerController();
    }

    private void createPointsSpinnerController() {
        pointsSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                Pattern selectedPattern = patternList.getSelectedValue();
                if (selectedPattern != null) {
                    Integer val = (Integer) pointsSpinner.getValue();
                    try {
                        selectedPattern.setPoints(val);
                    } catch (Pattern.InvalidPatternArguments invalidPatternArguments) {
                        invalidPatternArguments.printStackTrace();
                        pointsSpinner.setValue(selectedPattern.getPoints());
                    }
                }
            }
        });
    }

    private void createNameFieldController() {
        nameTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                update();
            }

            private void update() {
                String name = nameTextField.getText();
                if (name.length() > 0) {
                    try {
                        patternList.getSelectedValue().setName(name);
                        patternList.getPatternListModel().fireContentsChanged(patternList.getSelectedIndex());
                    } catch (Pattern.InvalidPatternArguments invalidPatternArguments) {
                        invalidPatternArguments.printStackTrace();
                    }
                }
            }
        });
    }

    private void createNewPatternButtonController() {
        newPatternButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    patternList.getPatternListModel().addElement(new Pattern("New Pattern", new HashSet<Integer>(), 1));
                } catch (Pattern.InvalidPatternArguments invalidPatternArguments) {
                    invalidPatternArguments.printStackTrace();
                }
            }
        });
    }

    private void createPatternListController() {
        patternList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if (!listSelectionEvent.getValueIsAdjusting()) {
                    Pattern selectedValue = patternList.getSelectedValue();
                    if (selectedValue != null) {
                        pointsSpinner.setValue(selectedValue.getPoints());
                        nameTextField.setText(selectedValue.getName());
                        boardPanel.setSquares(selectedValue.getSquares());
                    }
                }
            }
        });

        patternList.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {

            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_DELETE) {
                    if (patternList.getSelectedValue() != null) {
                        patternList.getPatternListModel().removeElementAt(patternList.getSelectedIndex());
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {

            }
        });
    }

    private void createBoardSquareController() {
        for (BoardSquare boardSquare : boardPanel.getBoardSquares()) {
            boardSquare.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    Pattern selectedPattern = patternList.getSelectedValue();
                    if (selectedPattern != null) {
                        try {
                            selectedPattern.setSquares(boardPanel.getSquares());
                        } catch (Pattern.InvalidPatternArguments invalidPatternArguments) {
                            invalidPatternArguments.printStackTrace();
                        }
                    }
                }
            });
        }
    }

}
