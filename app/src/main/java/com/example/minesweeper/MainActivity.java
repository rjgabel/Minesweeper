package com.example.minesweeper;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String DIG_ICON = "⛏";
    private static final String MINE_ICON = "\uD83D\uDCA3";
    private static final String FLAG_ICON = "\uD83D\uDEA9";
    private static final String FLAG_TEXT = FLAG_ICON + " ";
    private static final String TIME_TEXT = "\uD83D\uDD53 ";

    private ArrayList<TextView> cell_tvs;
    private TextView tv_flag;
    private TextView bottom_icon;

    private static final int COLUMN_COUNT = 10;
    private static final int ROW_COUNT = 12;
    private static final int NUM_MINES = 4;

    private final int[][] board = new int[ROW_COUNT][COLUMN_COUNT];
    private final int[][] status = new int[ROW_COUNT][COLUMN_COUNT];
    private boolean is_flag_mode = false;
    private int mines_left = NUM_MINES;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cell_tvs = new ArrayList<>();
        GridLayout grid = findViewById(R.id.gridLayout01);
        LayoutInflater li = LayoutInflater.from(this);
        for (int i = 0; i < ROW_COUNT; i++) {
            for (int j = 0; j < COLUMN_COUNT; j++) {
                TextView tv = (TextView) li.inflate(R.layout.custom_cell_layout, grid, false);
                tv.setTextColor(Color.GRAY);
                tv.setBackgroundColor(Color.GREEN);
                tv.setOnClickListener(this::onClickTV);
                GridLayout.LayoutParams lp = (GridLayout.LayoutParams) tv.getLayoutParams();
                lp.rowSpec = GridLayout.spec(i);
                lp.columnSpec = GridLayout.spec(j);
                grid.addView(tv, lp);
                cell_tvs.add(tv);
            }
        }

        tv_flag = findViewById(R.id.tv_flag);
        TextView tv_time = findViewById(R.id.tv_time);
        bottom_icon = findViewById(R.id.bottom_icon);
        tv_flag.setText(FLAG_TEXT + NUM_MINES);
        tv_time.setText(TIME_TEXT + NUM_MINES);
        bottom_icon.setText(DIG_ICON);
        bottom_icon.setOnClickListener(this::switch_mode);

        for (int i = 0; i < ROW_COUNT; i++) {
            for (int j = 0; j < COLUMN_COUNT; j++) {
                board[i][j] = 0;
                status[i][j] = 0;
            }
        }

        place_mines();
    }

    private int findIndexOfCellTextView(TextView tv) {
        for (int n = 0; n < cell_tvs.size(); n++) {
            if (cell_tvs.get(n) == tv) return n;
        }
        return -1;
    }

    @SuppressLint("SetTextI18n")
    public void onClickTV(View view) {
        TextView tv = (TextView) view;
        int n = findIndexOfCellTextView(tv);
        int i = n / COLUMN_COUNT;
        int j = n % COLUMN_COUNT;
        if (is_flag_mode) {
            flag_cell(i, j);
        } else {
            uncover_cell(i, j);
        }
    }

    private void place_mines() {
        Random rand = new Random();
        int mines_to_place = NUM_MINES;
        while (mines_to_place != 0) {
            int i = rand.nextInt(ROW_COUNT);
            int j = rand.nextInt(COLUMN_COUNT);
            if (board[i][j] != -1) {
                board[i][j] = -1;
                inc_count(i - 1, j - 1);
                inc_count(i - 1, j);
                inc_count(i - 1, j + 1);
                inc_count(i, j - 1);
                inc_count(i, j + 1);
                inc_count(i + 1, j - 1);
                inc_count(i + 1, j);
                inc_count(i + 1, j + 1);
                mines_to_place--;
            }
        }
    }

    private void inc_count(int i, int j) {
        if (i >= 0 && i < ROW_COUNT && j >= 0 && j < COLUMN_COUNT && board[i][j] != -1) {
            board[i][j]++;
        }
    }

    @SuppressLint("SetTextI18n")
    private void flag_cell(int i, int j) {
        int state = status[i][j];
        if (state != 1) {
            TextView tv = cell_tvs.get(i * COLUMN_COUNT + j);
            if (state == 0) {
                status[i][j] = 2;
                tv.setText(FLAG_ICON);
                mines_left--;
            } else {
                status[i][j] = 0;
                tv.setText("");
                mines_left++;
            }
            tv_flag.setText(FLAG_TEXT + mines_left);
        }
    }

    private void uncover_cell(int i, int j) {
        if (i >= 0 && i < ROW_COUNT && j >= 0 && j < COLUMN_COUNT && status[i][j] == 0) {
            status[i][j] = 1;
            int cell = board[i][j];
            TextView tv = cell_tvs.get(i * COLUMN_COUNT + j);
            if (cell == 0) {
                uncover_cell(i - 1, j - 1);
                uncover_cell(i - 1, j);
                uncover_cell(i - 1, j + 1);
                uncover_cell(i, j - 1);
                uncover_cell(i, j + 1);
                uncover_cell(i + 1, j - 1);
                uncover_cell(i + 1, j);
                uncover_cell(i + 1, j + 1);
            } else if (cell == -1) {
                tv.setText(MINE_ICON);
            } else {
                tv.setText(String.valueOf(cell));
            }
            tv.setBackgroundColor(Color.LTGRAY);
        }
    }

    public void switch_mode(View view) {
        is_flag_mode = !is_flag_mode;
        bottom_icon.setText(is_flag_mode ? FLAG_ICON : DIG_ICON);
    }
}