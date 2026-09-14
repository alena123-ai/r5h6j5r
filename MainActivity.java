package com.example.calculator;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
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
    private final int COLOR_BG          = Color.parseColor("#FFE4EC");
    private final int COLOR_DISPLAY_BG  = Color.parseColor("#FF4D8D");
    private final int COLOR_DISPLAY_TXT = Color.WHITE;
    private final int COLOR_NUM_BG      = Color.WHITE;
    private final int COLOR_NUM_TXT     = Color.parseColor("#FF2D6F");
    private final int COLOR_OP_BG       = Color.parseColor("#FF85B3");
    private final int COLOR_OP_TXT      = Color.WHITE;
    private final int COLOR_EQ_BG       = Color.parseColor("#00B06B");
    private final int COLOR_EQ_TXT      = Color.WHITE;
    private final int COLOR_CLEAR_BG    = Color.parseColor("#FFD400");
    private final int COLOR_CLEAR_TXT   = Color.parseColor("#7A4A00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(10), dp(12), dp(10), dp(12));
        root.setBackgroundColor(COLOR_BG);

        // ── Дисплей: занимает ~22% высоты, но не больше 160dp ──
        display = new TextView(this);
        display.setText("0");
        display.setTextSize(48);
        display.setTextColor(COLOR_DISPLAY_TXT);
        display.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        display.setPadding(dp(18), dp(16), dp(18), dp(16));
        display.setBackground(rounded(COLOR_DISPLAY_BG, 24, 0, 0));
        display.setSingleLine(true);
        display.setEllipsize(android.text.TextUtils.TruncateAt.START);

        LinearLayout.LayoutParams dispParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 2.2f);
        dispParams.bottomMargin = dp(10);
        root.addView(display, dispParams);

        // ── Сетка кнопок: занимает всё оставшееся место ──
        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(4);
        grid.setRowCount(4); // 4 ряда цифр + операторов, C — отдельно снизу
        LinearLayout.LayoutParams gridParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 7.8f);
        root.addView(grid, gridParams);

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
                // Отступы стали меньше и асимметричнее, чтобы кнопки были крупнее
                int gap = dp(4);
                p.setMargins(gap, gap, gap, gap);
                grid.addView(b, p);
            }
        }

        // ── Кнопка "C" — отдельная строка снизу ──
        Button clear = makeButton("C");
        LinearLayout.LayoutParams clearParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.2f);
        clearParams.topMargin = dp(4);
        root.addView(clear, clearParams);

        setContentView(root);
    }

    private Button makeButton(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextSize(28);
        b.setAllCaps(false);
        b.setTypeface(null, Typeface.BOLD);
        b.setMinWidth(0);
        b.setMinHeight(0);
        b.setMinimumWidth(0);
        b.setMinimumHeight(0);
        b.setPadding(0, 0, 0, 0);
        b.setIncludeFontPadding(false);

        int bgColor, txtColor;
        boolean isOperator = label.equals("+") || label.equals("-")
                || label.equals("×") || label.equals("÷");
        boolean isEquals   = label.equals("=");
        boolean isClear    = label.equals("C");

        if (isEquals) {
            bgColor = COLOR_EQ_BG;   txtColor = COLOR_EQ_TXT;
        } else if (isClear) {
            bgColor = COLOR_CLEAR_BG; txtColor = COLOR_CLEAR_TXT;
        } else if (isOperator) {
            bgColor = COLOR_OP_BG;   txtColor = COLOR_OP_TXT;
        } else {
            bgColor = COLOR_NUM_BG;  txtColor = COLOR_NUM_TXT;
        }

        b.setTextColor(txtColor);
        b.setBackground(rounded(bgColor, 20, 0, 0));
        b.setOnClickListener(this);
        return b;
    }

    private GradientDrawable rounded(int color, int radiusDp, int strokeDp, int strokeColor) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.RECTANGLE);
        d.setCornerRadius(dp(radiusDp));
        d.setColor(color);
        if (strokeDp > 0) d.setStroke(dp(strokeDp), strokeColor);
        return d;
    }

    private int dp(int value) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    @Override
    public void onClick(View v) {
        String text = ((Button) v).getText().toString();

        if (text.equals("C")) {
            currentInput = ""; operator = ""; firstOperand = 0; isNewInput = true;
            display.setText("0");
            return;
        }
        if (text.equals("=")) { calculateResult(); return; }
        if (text.equals("+") || text.equals("-") || text.equals("×") || text.equals("÷")) {
            if (!currentInput.isEmpty()) {
                if (!operator.isEmpty()) calculateResult();
                else firstOperand = Double.parseDouble(currentInput);
            }
            operator = text; isNewInput = true;
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
                    currentInput = ""; operator = ""; isNewInput = true;
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
