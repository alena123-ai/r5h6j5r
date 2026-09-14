package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.util.TypedValue;
import java.text.DecimalFormat;

public class MainActivity extends Activity implements View.OnClickListener {

    private TextView display;
    private String currentInput = "";
    private String operator = "";
    private double firstOperand = 0;
    private boolean isNewInput = true;
    private final DecimalFormat df = new DecimalFormat("#.##########");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Создаём layout программно — не нужен XML
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(16), dp(16), dp(16));
        root.setBackgroundColor(Color.WHITE);

        display = new TextView(this);
        display.setText("0");
        display.setTextSize(48);
        display.setGravity(Gravity.END);
        display.setPadding(dp(16), dp(16), dp(16), dp(16));
        display.setBackgroundColor(Color.parseColor("#EEEEEE"));
        LinearLayout.LayoutParams dispParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        dispParams.bottomMargin = dp(16);
        root.addView(display, dispParams);

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(4);
        grid.setRowCount(5);
        LinearLayout.LayoutParams gridParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        root.addView(grid, gridParams);

        String[][] labels = {
                {"7", "8", "9", "÷"},
                {"4", "5", "6", "×"},
                {"1", "2", "3", "-"},
                {"0", ".", "=", "+"},
        };

        for (String[] row : labels) {
            for (String label : row) {
                Button b = new Button(this);
                b.setText(label);
                b.setTextSize(24);
                b.setOnClickListener(this);
                GridLayout.LayoutParams p = new GridLayout.LayoutParams();
                p.width = 0;
                p.height = 0;
                p.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                p.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                p.setMargins(dp(4), dp(4), dp(4), dp(4));
                grid.addView(b, p);
            }
        }

        Button clear = new Button(this);
        clear.setText("C");
        clear.setTextSize(24);
        clear.setOnClickListener(this);
        GridLayout.LayoutParams cp = new GridLayout.LayoutParams();
        cp.width = GridLayout.LayoutParams.MATCH_PARENT;
        cp.height = 0;
        cp.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        cp.columnSpec = GridLayout.spec(0, 4, 1f);
        cp.setMargins(dp(4), dp(4), dp(4), dp(4));
        grid.addView(clear, cp);

        setContentView(root);
    }

    private int dp(int value) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    @Override
    public void onClick(View v) {
        String text = ((Button) v).getText().toString();

        if (text.equals("C")) {
            currentInput = "";
            operator = "";
            firstOperand = 0;
            isNewInput = true;
            display.setText("0");
            return;
        }

        if (text.equals("=")) {
            calculateResult();
            return;
        }

        if (text.equals("+") || text.equals("-") || text.equals("×") || text.equals("÷")) {
            if (!currentInput.isEmpty()) {
                if (!operator.isEmpty()) {
                    calculateResult();
                } else {
                    firstOperand = Double.parseDouble(currentInput);
                }
            }
            operator = text;
            isNewInput = true;
            return;
        }

        if (text.equals(".") && currentInput.contains(".")) return;

        if (isNewInput) {
            currentInput = text.equals(".") ? "0." : text;
            isNewInput = false;
        } else {
            currentInput += text;
        }
        display.setText(currentInput);
    }

    private void calculateResult() {
        if (operator.isEmpty() || currentInput.isEmpty()) return;

        double second = Double.parseDouble(currentInput);
        double result = 0;

        switch (operator) {
            case "+": result = firstOperand + second; break;
            case "-": result = firstOperand - second; break;
            case "×": result = firstOperand * second; break;
            case "÷":
                if (second == 0) {
                    display.setText("Ошибка");
                    currentInput = "";
                    operator = "";
                    isNewInput = true;
                    return;
                }
                result = firstOperand / second;
                break;
        }

        display.setText(df.format(result));
        currentInput = String.valueOf(result);
        operator = "";
        isNewInput = true;
    }
}
