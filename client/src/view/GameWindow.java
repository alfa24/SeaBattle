package view;

import controller.GamePresenter;
import model.Field;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicBorders;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Created by Александр on 17.01.2017.
 */
public class GameWindow extends JFrame implements GameInterface {
    private int sizeField;
    private static GameWindow gameWindow;
    private GamePresenter gamePresenter;

    private JButton[][] jButtons1;
    private JButton[][] jButtons2;
    private JPanel fieldPane1;
    private JPanel fieldPane2;

    private GameWindow() {
    }

    public static GameWindow getInstance() {
        if (gameWindow == null) {
            gameWindow = new GameWindow();
        }
        return gameWindow;
    }

    @Override
    public void init() {
        gamePresenter = GamePresenter.getInstance();
        sizeField = gamePresenter.getSizeField();
        setSize(1000, 600);
        setTitle("Sea Battle");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        Font font = new Font("TimesRoman", Font.BOLD, 20);

        c.gridx = 0;
        c.gridy = 0;
        JLabel jLabel1 = new JLabel("Ваше поле");
        jLabel1.setFont(font);
        jPanel.add(jLabel1, c);

        c.gridx = 1;
        c.gridy = 0;
        JLabel jLabel2 = new JLabel("Поле соперника");
        jLabel2.setFont(font);
        jPanel.add(jLabel2, c);

        jButtons1 = new JButton[sizeField][sizeField];
        c.gridx = 0;
        c.gridy = 1;
        fieldPane1 = fieldPanel(jButtons1, sizeField);
        jPanel.add(fieldPane1,c);

        jButtons2 = new JButton[sizeField][sizeField];
        c.gridx = 1;
        c.gridy = 1;
        fieldPane2 = fieldPanel(jButtons2, sizeField);
        jPanel.add(fieldPane2,c);
        add(jPanel);
        setVisible(true);
    }

    private JPanel fieldPanel(JButton[][] jButtons, int size) {
        JPanel jPanel = new JPanel();

        jPanel.setLayout(new GridLayout(size, size, 0, 0));
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                boolean enable = true;
                JButton jButton = new JButton("");
                jButton.setMargin(new Insets(0,0,0,0));
                jButton.setPreferredSize(new Dimension(44, 44));
                jButton.setOpaque(true);
                jButton.setFocusPainted(false);
                jButton.setEnabled(enable);
                jButtons[i][j] = jButton;
                jPanel.add(jButton);
                int finalJ = j;
                int finalI = i;
                jButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        gamePresenter.doShoot(finalI, finalJ);
                    }
                });
                jButton.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {

                    }

                    @Override
                    public void mousePressed(MouseEvent e) {

                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {

                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
//                        jButtons[finalI][finalJ].setBackground(Color.BLACK);
//                        jButtons[finalI][finalJ + 1].setBackground(Color.BLACK);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
//                        jButtons[finalI][finalJ].setBackground(Color.GRAY);
//                        jButtons[finalI][finalJ + 1].setBackground(Color.GRAY);
                    }
                });
            }
        }
        return jPanel;
    }

    @Override
    public String askUserString(String ask) {
        String result = JOptionPane.showInputDialog(gameWindow, ask);
        return result;
    }

    @Override
    public int askUserInteger(String ask) {
        return 0;
    }

    @Override
    public void showField(ArrayList<ArrayList<String>> field, boolean showShips) {
        ClassLoader cl = this.getClass().getClassLoader();
        JButton[][] jButtons;
        Boolean buttonEnabled;
        if (showShips) {
            jButtons = jButtons1;
            buttonEnabled = false;
        } else {
            buttonEnabled = true;
            jButtons = jButtons2;
        }

        for (int j = 0; j < field.size(); j++) {
            ArrayList<String> row = field.get(j);
            for (int i = 0; i < row.size(); i++) {
                String cell;
                cell = row.get(i);

                JButton jButton = jButtons[i][j];
                jButton.setEnabled(buttonEnabled);

                if (i >= 0 & j >= 0) {  //само поле
                    Icon icon = new ImageIcon(cl.getResource("icon/white.png"));

                    if (cell.equals("Hit")) {
                        jButton.setEnabled(false);
                        icon = new ImageIcon(cl.getResource("icon/fire.png"));
                    }
                    if (cell.equals("ShipOk")) {
                        icon = new ImageIcon(cl.getResource("icon/ship.png"));
                    }
                    if (cell.equals("ShipDamage")) {
                        icon = new ImageIcon(cl.getResource("icon/shipDamage.png"));
                        jButton.setEnabled(false);
                    }
                    if (cell.equals("ShipDead")) {
                        icon = new ImageIcon(cl.getResource("icon/shipDead.png"));
                        jButton.setEnabled(false);
                    }

                    jButton.setIcon(icon);
                    jButton.setDisabledIcon(icon);
                    jButton.setPressedIcon(icon);
                    jButton.setRolloverIcon(icon);

                }
            }
        }
        repaint();
    }

    @Override
    public void msg(String text) {
        JOptionPane.showMessageDialog(gameWindow,text);
    }

    @Override
    public Field.Point getShoot() {
        return null;
    }

    @Override
    public String askUserOpponent(String msg) {
        Object[] possibilities = {"Человек", "Компьютер"};
        String dev = (String) JOptionPane.showInputDialog(null, "Выберите пункт", msg, JOptionPane.PLAIN_MESSAGE, null, possibilities, null);
        return dev;
    }

    @Override
    public void setStep(boolean firstPlayer) {
        Border acrive;
        Border passive;
        acrive = new BasicBorders.ButtonBorder(Color.MAGENTA, Color.MAGENTA, Color.MAGENTA, Color.MAGENTA);
        passive = new BasicBorders.ButtonBorder(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK);
        if (firstPlayer) {
            fieldPane1.setBorder(passive);
            fieldPane2.setBorder(acrive);
        } else {
            fieldPane1.setBorder(acrive);
            fieldPane2.setBorder(passive);

        }
    }

    @Override
    public void setShips() {
        msg("Установите корабли");
    }

}
