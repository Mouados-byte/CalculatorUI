import java.util.Scanner;
import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;

import java.awt.*;
import java.awt.event.*;

class Window {
    static final int Window_width = 1000;
    static final int Window_height = 800;
    JFrame frame;
    JPanel buttons_panel;
    JLabel label;
    boolean res = false;

    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ')
                    nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length())
                    throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)` | number
            // | functionName `(` expression `)` | functionName factor
            // | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if (eat('+'))
                        x += parseTerm(); // addition
                    else if (eat('-'))
                        x -= parseTerm(); // subtraction
                    else
                        return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('*'))
                        x *= parseFactor(); // multiplication
                    else if (eat('/'))
                        x /= parseFactor(); // division
                    else
                        return x;
                }
            }

            double parseFactor() {
                if (eat('+'))
                    return +parseFactor(); // unary plus
                if (eat('-'))
                    return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    if (!eat(')'))
                        throw new RuntimeException("Missing ')'");
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.')
                        nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z')
                        nextChar();
                    String func = str.substring(startPos, this.pos);
                    if (eat('(')) {
                        x = parseExpression();
                        if (!eat(')'))
                            throw new RuntimeException("Missing ')' after argument to " + func);
                    } else {
                        x = parseFactor();
                    }
                    if (func.equals("sqrt"))
                        x = Math.sqrt(x);
                    else if (func.equals("sin"))
                        x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos"))
                        x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan"))
                        x = Math.tan(Math.toRadians(x));
                    else
                        throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                if (eat('^'))
                    x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }

    public Window() {
        // Declarations
        frame = new JFrame("My Calculator");
        buttons_panel = new JPanel(new GridLayout(7, 4));
        frame.setSize(Window_width, Window_height);
        label = new JLabel();

        GridBagLayout layout = new GridBagLayout();
        frame.setLayout(layout);
        GridBagConstraints c = new GridBagConstraints();

        buttons_panel.setBackground(new Color(0, 255, 255));
        for (int i = 1; i < 11; i++) {
            if (i == 10) {
                createButton(buttons_panel, "" + 0, 1);
            } else {
                createButton(buttons_panel, "" + i, 1);
            }
        }
        createButton(buttons_panel, "+", 1);
        createButton(buttons_panel, "-", 1);
        createButton(buttons_panel, "*", 1);
        createButton(buttons_panel, "/", 1);
        createButton(buttons_panel, "^", 1);
        createButton(buttons_panel, "=", 2);
        createButton(buttons_panel, "C", 2);
        createButton(buttons_panel, "Backslash", 2);
        // Position Label
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 0.3;
        c.gridx = 0;
        c.gridy = 0;
        label.setFont(new Font("Arial", Font.BOLD, 32));
        label.setHorizontalAlignment(JLabel.CENTER);
        frame.add(label, c);

        // position panel of buttons
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 0.7;
        c.weightx = 0.7;
        c.gridx = 0;
        c.gridy = 1;
        frame.add(buttons_panel, c);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void createButton(JPanel panel, String i, int size) {

        JButton button = new JButton();
        button.setText(i);
        button.setFont(new Font("Arial", Font.BOLD, 22));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String buttonText = ((JButton) e.getSource()).getText();
                String firstStr = label.getText();

                if (res) {
                    firstStr = "";
                    label.setText(firstStr);
                    res = false;
                } else {
                    if (buttonText == "=") {
                        Double result = eval(firstStr);
                        label.setText("" + result);
                        res = true;
                    } else if (buttonText == "C") {
                        label.setText("");
                    } else if (buttonText == "Backslash") {
                        label.setText(firstStr.substring(0, firstStr.length() - 1));
                    } else {
                        label.setText(firstStr + buttonText);
                    }
                }
            }
        });
        panel.add(button);
    }

}

/**
 * Calc
 */
public class Calc {
    public static void main(String[] args) {

        Window window = new Window();
    }
}