package com.example.calc2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private StringBuilder input = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);

        // Number buttons
        int[] numberButtons = {R.id.button0, R.id.button1, R.id.button2, R.id.button3,
                R.id.button4, R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9};

        // Set onClickListener for number buttons
        for (int id : numberButtons) {
            findViewById(id).setOnClickListener(v -> {
                Button button = (Button) v;
                input.append(button.getText());
                textView.setText(input.toString());
            });
        }

        // Operator buttons
        int[] operatorButtons = {R.id.buttonPlus, R.id.buttonMinus, R.id.buttonMultiply, R.id.buttonDivide};

        // Set onClickListener for operator buttons
        for (int id : operatorButtons) {
            findViewById(id).setOnClickListener(v -> {
                Button button = (Button) v;
                input.append(" ").append(button.getText()).append(" ");
                textView.setText(input.toString());
            });
        }

        // Equal button
        findViewById(R.id.buttonEqual).setOnClickListener(v -> {
            try {
                double result = evaluateExpression(input.toString());
                textView.setText(String.valueOf(result));
                input.setLength(0); // Clear the input after displaying the result
                input.append(result); // Start new operations with the result as the base value
            } catch (Exception e) {
                textView.setText("Error");
                input.setLength(0);
            }
        });

        // Clear button
        findViewById(R.id.buttonAC).setOnClickListener(v -> {
            input.setLength(0);
            textView.setText("0");
        });

        // Back button
        findViewById(R.id.buttonBack).setOnClickListener(v -> {
            if (input.length() > 0) {
                input.deleteCharAt(input.length() - 1);
                textView.setText(input.length() > 0 ? input.toString() : "0");
            }
        });
    }

    // Evaluate the expression
    private double evaluateExpression(String expression) {
        // Split the expression by spaces to separate numbers and operators
        String[] tokens = expression.split(" ");
        Stack<Double> numbers = new Stack<>();
        Stack<String> operators = new Stack<>();

        for (String token : tokens) {
            if (isNumber(token)) {
                // If the token is a number, push it to the numbers stack
                numbers.push(Double.parseDouble(token));
            } else if (isOperator(token)) {
                // If the token is an operator, push it to the operators stack
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(token)) {
                    // Calculate using top operators if precedence is higher or equal
                    double second = numbers.pop();
                    double first = numbers.pop();
                    String op = operators.pop();
                    numbers.push(calculate(first, second, op));
                }
                operators.push(token);
            }
        }

        // Process remaining operators in the stack
        while (!operators.isEmpty()) {
            double second = numbers.pop();
            double first = numbers.pop();
            String op = operators.pop();
            numbers.push(calculate(first, second, op));
        }

        // The result will be the only number left in the stack
        return numbers.pop();
    }

    // Helper method to check if a string is a number
    private boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Helper method to check if a string is an operator
    private boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/");
    }

    // Helper method to determine precedence of operators
    private int precedence(String operator) {
        switch (operator) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
            default:
                return -1;
        }
    }

    // Helper method to calculate the result of two numbers with an operator
    private double calculate(double first, double second, String operator) {
        switch (operator) {
            case "+":
                return first + second;
            case "-":
                return first - second;
            case "*":
                return first * second;
            case "/":
                if (second != 0) {
                    return first / second;
                } else {
                    throw new ArithmeticException("Division by zero");
                }
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }
}
