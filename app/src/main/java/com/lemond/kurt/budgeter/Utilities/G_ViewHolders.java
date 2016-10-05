package com.lemond.kurt.budgeter.Utilities;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by kurt_capatan on 3/10/2016.
 */
public class G_ViewHolders {

    public static class CurrentMonthExpensesSearch{
        public TextView expenseDuration;
        public RelativeLayout circleBG;
        public TextView expenseInitial;
        public TextView expenseName;
        public TextView expensePrice;
        public TextView expenseQuantity;
        public TextView expenseDate;
//        public ImageView actualExpense_edit;
        public ImageView actualExpense_delete;
    }

    public static class SalaryBudgetSearch{
        public TextView salaryDuration;
        public RelativeLayout circleBG;
        public TextView salaryItemInitial;
        public TextView itemName;
        public TextView itemPrice;
        public TextView itemQuantity;
//        public ImageView actualExpense_edit;
        public ImageView actualExpense_delete;
    }

    public static class SavedCommodities{
        public LinearLayout circleBG;
        public TextView itemInitial;
        public ImageView ivChecked;
        public TextView itemName;
        public TextView itemPrice;
//        public ImageView savedItem_edit;
//        public ImageView savedItem_delete;
    }

    public static class ImportSavedItems{
        public TextView itemName;
        public TextView itemPrice;
    }

    public static class LoansByMonth{
        public TextView lenderName;
        public TextView loanAmount;
        public TextView loanDate;
        public ImageView loanByMonth_delete;
    }

    public static class SalaryHistory{
        public TextView salaryAmount;
        public TextView salaryDateChanged;
    }

    public static class Caldroid{
        public TextView tv1;
        public LinearLayout linearLayout;
        public TextView mTotal;
        public TextView mItems;
    }

    public static class SearchSuggestions{
        public TextView firstLetter;
        public TextView ItemName;
        public TextView ItemPrice;
    }

    public class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private final int[] ATTRS = new int[]{
                android.R.attr.listDivider
        };

        public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

        public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

        private Drawable mDivider;

        private int mOrientation;

        public DividerItemDecoration(Context context) {
            final TypedArray a = context.obtainStyledAttributes(ATTRS);
            mDivider = a.getDrawable(0);
            a.recycle();
            setOrientation(VERTICAL_LIST);
        }

        public void setOrientation(int orientation) {
            if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
                throw new IllegalArgumentException("invalid orientation");
            }
            mOrientation = orientation;
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            if (mOrientation == VERTICAL_LIST) {
                drawVertical(c, parent);
            } else {
                drawHorizontal(c, parent);
            }
        }

        public void drawVertical(Canvas c, RecyclerView parent) {
            final int left = parent.getPaddingLeft();
            final int right = parent.getWidth() - parent.getPaddingRight();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int top = child.getBottom() + params.bottomMargin;
                final int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }

        public void drawHorizontal(Canvas c, RecyclerView parent) {
            final int top = parent.getPaddingTop();
            final int bottom = parent.getHeight() - parent.getPaddingBottom();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int left = child.getRight() + params.rightMargin;
                final int right = left + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (mOrientation == VERTICAL_LIST) {
                outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
            } else {
                outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
            }
        }
    }

}
