import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

public class RegexCheckerApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Создаем главное окно
            JFrame frame = new JFrame("Анализатор регулярных выражений");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);
            frame.setLayout(new BorderLayout(10, 10));

            // Панель ввода011
            JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
            inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel titleLabel = new JLabel("Проверка строк по регулярным выражениям");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

            JLabel inputLabel = new JLabel("Введите бинарную строку (0 и 1):");
            JTextField inputField = new JTextField();
            inputField.setFont(new Font("Arial", Font.PLAIN, 14));

            inputPanel.add(titleLabel, BorderLayout.NORTH);
            inputPanel.add(inputLabel, BorderLayout.WEST);
            inputPanel.add(inputField, BorderLayout.CENTER);

            // Панель результатов
            JPanel resultPanel = new JPanel(new GridLayout(3, 1, 10, 10));
            resultPanel.setBorder(BorderFactory.createTitledBorder("Результаты проверки"));

            // Создаем карточки для каждого regex
            JPanel[] regexCards = new JPanel[3];
            JLabel[] regexLabels = new JLabel[3];
            JLabel[] resultLabels = new JLabel[3];

            String[] regexPatterns = {
                    "01* (начинается с 0, содержит хотя бы одну 1)",
                    "(0+1)01 (3 символа, заканчивается на 01)",
                    "00(0+1)* (начинается с двух нулей)"
            };

            for (int i = 0; i < 3; i++) {
                regexCards[i] = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
                regexCards[i].setBorder(BorderFactory.createEtchedBorder());

                regexLabels[i] = new JLabel(regexPatterns[i]);
                regexLabels[i].setFont(new Font("Arial", Font.PLAIN, 12));

                resultLabels[i] = new JLabel("?");
                resultLabels[i].setFont(new Font("Arial", Font.BOLD, 14));
                resultLabels[i].setForeground(Color.GRAY);

                regexCards[i].add(regexLabels[i]);
                regexCards[i].add(Box.createHorizontalStrut(20));
                regexCards[i].add(resultLabels[i]);

                resultPanel.add(regexCards[i]);
            }

            // Кнопка проверки
            JButton checkButton = new JButton("Проверить");
            checkButton.setFont(new Font("Arial", Font.BOLD, 14));
            checkButton.setBackground(new Color(70, 130, 180));
            checkButton.setForeground(Color.WHITE);
            checkButton.setFocusPainted(false);

            // Обработчик кнопки
            checkButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String input = inputField.getText();

                    // Сброс предыдущих результатов
                    for (JLabel label : resultLabels) {
                        label.setText("?");
                        label.setForeground(Color.GRAY);
                    }

                    // Проверка на допустимые символы
                    if (!Pattern.matches("[01]*", input)) {
                        JOptionPane.showMessageDialog(frame,
                                "Ошибка! Допустимы только символы 0 и 1.",
                                "Некорректный ввод",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Проверка по регулярным выражениям
                    boolean[] results = {
                            Pattern.matches("0.*1.*", input),  // 01*
                            Pattern.matches("[01]01", input),    // (0+1)01
                            Pattern.matches("00[01]*", input)    // 00(0+1)*
                    };

                    // Обновление UI с результатами
                    for (int i = 0; i < 3; i++) {
                        if (results[i]) {
                            resultLabels[i].setText("✓ Соответствует");
                            resultLabels[i].setForeground(new Color(0, 128, 0));
                        } else {
                            resultLabels[i].setText("✗ Не соответствует");
                            resultLabels[i].setForeground(Color.RED);
                        }
                    }
                }
            });

            // Собираем интерфейс
            frame.add(inputPanel, BorderLayout.NORTH);
            frame.add(resultPanel, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(checkButton);
            frame.add(buttonPanel, BorderLayout.SOUTH);

            // Центрируем окно
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}