import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame implements ActionListener {

    private int size;
    private String[][] board;
    private JButton[][] buttons;
    private JComboBox<String> optionsList;
    private JButton start_button;
    private JButton quit_button;
    private JLabel infoLabel;
    private TicTacToe game;
    private final Font thick = new Font(Font.SANS_SERIF, Font.BOLD, 16);
    public GUI() {
        // Icon
        ImageIcon icon = new ImageIcon("icon.jpg");

        // The frame
        this.setTitle("TicTacToe");
        this.setLocationRelativeTo(null);
        this.setIconImage(icon.getImage());
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        this.getContentPane().add(startGUI());
    }

    private JPanel startGUI() {
        // The layout
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // The components
        JLabel label = new JLabel("TicTacToe");
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel hint = new JLabel("Select a board size:");
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] options = {"3x3", "4x4", "5x5", "6x6", "7x7"};
        optionsList = new JComboBox<>(options);
        optionsList.setSelectedIndex(0);
        optionsList.setAlignmentX(Component.CENTER_ALIGNMENT);
        optionsList.addActionListener(this);

        start_button = new JButton("Start");
        start_button.setAlignmentX(Component.CENTER_ALIGNMENT);
        start_button.addActionListener(this);

        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 50)));
        panel.add(hint);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(optionsList);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(start_button);

        this.getContentPane().add(panel);
        this.pack();

        return panel;
    }

    private JPanel gameGUI(int size) {
        // The panel
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(500, 600));

        // The menu
        JPanel menu = new JPanel();

        menu.setLayout(new BoxLayout(menu, BoxLayout.X_AXIS));
        menu.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        menu.setPreferredSize(new Dimension(500, 100));

        infoLabel = new JLabel("TicTacToe");
        quit_button = new JButton("Quit game");
        quit_button.addActionListener(this);

        menu.add(quit_button);
        menu.add(Box.createRigidArea(new Dimension(80, 0)));
        menu.add(infoLabel);

        // The game itself
        game = new TicTacToe(size);
        board = game.getBoard();
        buttons = new JButton[size][size];
        JPanel grid = new JPanel(new GridLayout(size, size));
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                buttons[i][j] = new JButton(TicTacToe.BLANK);
                grid.add(buttons[i][j]);
                buttons[i][j].setFocusable(false);
                buttons[i][j].addActionListener(this);
            }
        }

        panel.add(menu);
        panel.add(grid);
        return panel;
    }

    private void drawBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                String mark = board[i][j];
                if (mark.equals(TicTacToe.EX)) {
                    buttons[i][j].setText(board[i][j]);
                    buttons[i][j].setFont(thick);
                    buttons[i][j].setForeground(Color.RED);
                }
                if (mark.equals(TicTacToe.OH)) {
                    buttons[i][j].setText(board[i][j]);
                    buttons[i][j].setFont(thick);
                    buttons[i][j].setForeground(Color.BLUE);
                }
            }
        }
    }

    private void endGame(String info) {
        infoLabel.setText(info);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                buttons[i][j].setEnabled(false);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == start_button) {
            String option = (String) optionsList.getSelectedItem();
            assert option != null;
            size = Character.getNumericValue(option.charAt(0));

            Container contentPane = this.getContentPane();
            contentPane.removeAll();
            contentPane.add(gameGUI(size));
            this.pack();
            return;
        }

        if (e.getSource() == quit_button) {
            Container contentPane = this.getContentPane();
            contentPane.removeAll();
            contentPane.add(startGUI());
        }

        try {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (e.getSource() == buttons[i][j]) {
                        System.out.println("Button [" + i + ", " + j + "] pressed");
                        SwingUtilities.invokeLater(this::drawBoard);
                        if (game.mark(TicTacToe.EX, i, j))
                            endGame("Player won!");
                    }
                }
            }
        }
        catch (NullPointerException | AlreadyMarkedException ignored) {}
        catch (DrawException drawException) {
            endGame("It's a draw!");
        }
        catch (BotWonException botWonException) {
            endGame("Computer won!");
        }
    }
}
