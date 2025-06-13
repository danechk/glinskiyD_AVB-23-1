import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegexApp {
    private JFrame frame;
    private JComboBox<String> taskComboBox;
    private JTextField inputField;
    private JLabel resultLabel;
    private Map<String, String> regexPatterns;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                RegexApp window = new RegexApp();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public RegexApp() {
        initialize();
        setupRegexPatterns();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Regex Checker");
        frame.setBounds(100, 100, 500, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        JLabel taskLabel = new JLabel("Выберите задание:");
        frame.getContentPane().add(taskLabel);

        String[] tasks = {
                "1.1. Цепочки с хотя бы одним 'a' и хотя бы одним 'b'",
                "1.2. Десятый от правого края символ '1'",
                "1.3. Не более одной пары последовательных '1'",
                "2.1. Пары смежных нулей перед парой смежных единиц",
                "2.2. Число нулей кратно пяти",
                "3.1. Нет подцепочки '101'",
                "3.2. Поровну нулей и единиц, ограничение на префиксы",
                "3.3. Число нулей делится на пять, а количество единиц четно",
                "4. Телефонные номера",
                "5. Заработная плата"
        };

        taskComboBox = new JComboBox<>(tasks);
        frame.getContentPane().add(taskComboBox);

        JLabel inputLabel = new JLabel("Введите строку для проверки:");
        frame.getContentPane().add(inputLabel);

        inputField = new JTextField();
        inputField.setColumns(25);
        frame.getContentPane().add(inputField);

        JButton checkButton = new JButton("Проверить");
        checkButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkRegex();
            }
        });
        frame.getContentPane().add(checkButton);

        resultLabel = new JLabel("");
        resultLabel.setPreferredSize(new Dimension(450, 50));
        frame.getContentPane().add(resultLabel);
    }

    private void setupRegexPatterns() {
        regexPatterns = new HashMap<>();
        // Задание 1
        regexPatterns.put("1.1. Цепочки с хотя бы одним 'a' и хотя бы одним 'b'",
                "^(?=[^a]*a)(?=[^b]*b)[a-c]+$");
        regexPatterns.put("1.2. Десятый от правого края символ '1'", "[0-1]*1[0-1]{9}");
        regexPatterns.put("1.3. Не более одной пары последовательных '1'", "^(0*10*10*)*$|^0*10*$|^0*$|^1$");

        // Задание 2
        regexPatterns.put("2.1. Пары смежных нулей перед парой смежных единиц", "^(0*10*1)*0*$");
        regexPatterns.put("2.2. Число нулей кратно пяти", "^(1*01*01*01*01*0)*(1*)$");

        // Задание 3
        regexPatterns.put("3.1. Нет подцепочки '101'", "^(?!.*101)[01]*$");
        regexPatterns.put("3.2. Поровну нулей и единиц, ограничение на префиксы",
                "^(?=(1*0)*1*$)(?<=^((1(0(1(0(1(0)*1)*0)*1)*0)*)$");
        regexPatterns.put("3.3. Число нулей делится на пять, а количество единиц четно",
                "^(?=([^0]*0){5}([^0]*0)*$)([^1]*1[^1]*1)*[^1]*$");

        // Задание 4 (телефонные номера)
        regexPatterns.put("4. Телефонные номера",
                "^(\\+\\d{1,3}[-.\\s]?)?(\\(\\d{1,4}\\)[-.\\s]?)?[\\d\\s-.]{6,15}$");

        // Задание 5 (заработная плата)
        regexPatterns.put("5. Заработная плата",
                "(?i)((\\$|р|руб|rub|eur|euro)\\s*)?\\d{1,3}(?:[,\\s]?\\d{3})*(?:\\.\\d{2})?(?:\\s*((р|руб|rub|\\$|eur|euro)|в\\s+час|в\\s+день|в\\s+неделю|в\\s+месяц|в\\s+год|\\s*/час|\\s*/день|\\s*/неделю|\\s*/месяц|\\s*/год))?");
    }

    private void checkRegex() {
        String userInput = inputField.getText();
        String selectedTask = (String) taskComboBox.getSelectedItem();
        String regexPattern = regexPatterns.get(selectedTask);

        if (regexPattern == null) {
            resultLabel.setText("Шаблон для выбранного задания не найден.");
            return;
        }

        if (Pattern.matches(regexPattern, userInput)) {
            resultLabel.setText("<html><div style='color:green'>Строка соответствует шаблону!</div></html>");
        } else {
            resultLabel.setText("<html><div style='color:red'>Строка НЕ соответствует шаблону.</div></html>");
        }
    }
}