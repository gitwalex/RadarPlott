package de.alexanderwinkler.main;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.RadioGroup;

import de.alexanderwinkler.R;

/**
 * Created by alex on 05.08.2016.
 */
public class GegnerView extends CardView implements RadioGroup.OnCheckedChangeListener {
    public GegnerView(Context context) {
        super(context);
    }

    public GegnerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GegnerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radioButtonSP1:
                findViewById(R.id.llSeitenpeilung1).setVisibility(VISIBLE);
                findViewById(R.id.llRadarseitenpeilung1).setVisibility(GONE);
                break;
            case R.id.radioButtonSP2:
                findViewById(R.id.llSeitenpeilung2).setVisibility(VISIBLE);
                findViewById(R.id.llRadarseitenpeilung2).setVisibility(GONE);
                break;
            case R.id.radioButtonRASP1:
                findViewById(R.id.llSeitenpeilung1).setVisibility(GONE);
                findViewById(R.id.llRadarseitenpeilung1).setVisibility(VISIBLE);
                break;
            case R.id.radioButtonRASP2:
                findViewById(R.id.llSeitenpeilung2).setVisibility(GONE);
                findViewById(R.id.llRadarseitenpeilung2).setVisibility(VISIBLE);
                break;
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        RadioGroup rb = (RadioGroup) findViewById(R.id.radioGroup1);
        rb.setOnCheckedChangeListener(this);
        rb = (RadioGroup) findViewById(R.id.radioGroup2);
        rb.setOnCheckedChangeListener(this);
    }
}
