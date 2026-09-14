package com.example.calculator;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.DecimalFormat;

public class MainActivity extends Activity implements View.OnClickListener {

    private TextView display;
    private String currentInput = "";
    private String operator = "";
    private double firstOperand = 0;
    private boolean isNewInput = true;
    private final DecimalFormat df = new DecimalFormat("#.##########");

    // 🎨 Палитра Roblox × Розовый
    private final int COLOR_BG          = Color.parseColor("#FFE4EC"); // нежно-розовый фон
    private final int COLOR_DISPLAY_BG  = Color.parseColor("#FF4D8D"); // ярко-розовый дисплей
    private final int COLOR_DISPLAY_TXT = Color.WHITE;
    private final int COLOR_NUM_BG      = Color.WHITE;                // белые цифры
    private final int COLOR_NUM_TXT     = Color.parseColor("#FF2D6F");
    private final int COLOR_OP_BG       = Color.parseColor("#FF85B3"); // операторы — розовые
    private final int COLOR_OP_TXT      = Color.WHITE;
    private final int COLOR_EQ_BG       = Color.parseColor("#00B06B"); // Roblox-зелёный для "="
    private final int COLOR_EQ_TXT      = Color.WHITE;
    private final int COLOR_CLEAR_BG    = Color.parseColor("#FFD400"); // Roblox-жёлтый для C
    private final int COLOR_CLEAR_TXT   = Color.parseColor("#7A4A00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(20), dp(16), dp(16));
        root.setBackgroundColor(COLOR_BG);

        // ── Дисплей ─────────────────────────────────────
        display = new TextView(this);
        display.setText("0");
        display.setTextSize(52);
        display.setTextColor(COLOR_DISPLAY_TXT);
        display.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        display.setPadding(dp(20), dp(24), dp(20), dp(24));
        display.setBackground(rounded(COLOR_DISPLAY_BG, 28, 0, 0));

        LinearLayout.LayoutParams dispParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        dispParams.bottomMargin = dp(18);
        root.addView(display, dispParams);

        // ── Сетка кнопок ────────────────────────────────
        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(4);
        grid.setRowCount(5);
        LinearLayout.LayoutParams gridParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        root.addView(grid, gridParams);

        // Строки: цифры + операторы
        String[][] rows = {
                {"7", "8", "9", "÷"},
                {"4", "5", "6", "×"},
                {"1", "2", "3", "-"},
                {"0", ".", "=", "+"},
        };

        for (String[] row : rows) {
            for (String label : row) {
                Button b = makeButton(label);
                GridLayout.LayoutParams p = new GridLayout.LayoutParams();
                p.width = 0;
                p.height = 0;
                p.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                p.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                p.setMargins(dp(6), dp(6), dp(6), dp(6));
                grid.addView(b, p);
            }
        }

        // Кнопка "C" на всю ширину снизу
        Button clear = makeButton("C");
        GridLayout.LayoutParams cp = new GridLayout.LayoutParams();
        cp.width = GridLayout.LayoutParams.MATCH_PARENT;
        cp.height = 0;
        cp.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        cp.columnSpec = GridLayout.spec(0, 4, 1f);
        cp.setMargins(dp(6), dp(6), dp(6), dp(6));
        grid.addView(clear, cp);

        setContentView(root);
    }

    /** Создаёт кнопку с нужным цветом и стилем в зависимости от текста. */
    private Button makeButton(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextSize(26);
        b.setAllCaps(false);
        b.setTypeface(null, android.graphics.Typeface.BOLD);

        int bgColor, txtColor;
        boolean isOperator = label.equals("+") || label.equals("-")
                || label.equals("×") || label.equals("÷");
        boolean isEquals   = label.equals("=");
        boolean isClear    = label.equals("C");

        if (isEquals) {
            bgColor = COLOR_EQ_BG;
            txtColor = COLOR_EQ_TXT;
        } else if (isClear) {
            bgColor = COLOR_CLEAR_BG;
            txtColor = COLOR_CLEAR_TXT;
        } else if (isOperator) {
            bgColor = COLOR_OP_BG;
            txtColor = COLOR_OP_TXT;
        } else {
            bgColor = COLOR_NUM_BG;
            txtColor = COLOR_NUM_TXT;
        }

        b.setTextColor(txtColor);
        b.setBackground(rounded(bgColor, 24, 0, 0));
        b.setPadding(0, dp(14), 0, dp(14));
        b.setOnClickListener(this);
        return b;
    }

    /** Рисует скруглённый прямоугольник. */
    private GradientDrawable rounded(int color, int radiusDp, int strokeDp, int strokeColor) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.RECTANGLE);
        d.setCornerRadius(dp(radiusDp));
        d.setColor(color);
        if (strokeDp > 0) {
            d.setStroke(dp(strokeDp), strokeColor);
        }
        return d;
    }

    private int dp(int value) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    // ── Логика калькулятора ─────────────────────────────
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
