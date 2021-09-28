package studio.mandysa.bottomnavigationbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("ViewConstructor")
public class NavigationItem extends LinearLayout {

    private final int mPosition;
    private TextView mTextView;
    private ImageView mImageView;
    private int mChecked = R.color.default_checked_color;
    private int mUnChecked = R.color.default_unchecked_color;
    private boolean mState;

    public NavigationItem(int index, String text, Context context, int resource) {
        super(context);
        this.mPosition = index;
        setGravity(Gravity.CENTER);
        setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1f));
        setOrientation(VERTICAL);
        if (resource != 0) {
            mImageView = new ImageView(context);
            mImageView.setImageResource(resource);
            mImageView.setColorFilter(getContext().getColor(mUnChecked));
            addView(mImageView);
        }
        if (text != null) {
            mTextView = new TextView(context);
            mTextView.setText(text);
            mTextView.setTextColor(getContext().getColor(mUnChecked));
            mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            mTextView.setPadding(0, dp4(), 0, 0);
            mTextView.setGravity(Gravity.CENTER);
            addView(mTextView);
        }
    }

    void setTextColor(int checked, int unchecked) {
        if (checked != 0)
            this.mChecked = checked;
        if (unchecked != 0)
            this.mUnChecked = unchecked;
        if (mTextView != null)
            mTextView.setTextColor(getContext().getColor(mState ? this.mChecked : this.mUnChecked));
        if (mImageView != null)
            mImageView.setColorFilter(getContext().getColor(mState ? this.mChecked : this.mUnChecked));
    }

    public int getPosition() {
        return mPosition;
    }

    void check() {
        mState = !mState;
        setTextColor(0, 0);
    }

    private int dp4() {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                (float) 4, getResources().getDisplayMetrics());
    }

}
