package me.fingerart.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import me.fingerart.patternlock.interf.PatternLockListener;
import me.fingerart.patternlock.view.PatternLockIndicator;
import me.fingerart.patternlock.view.PatternLockView;

public class MainActivity extends AppCompatActivity {
    private String password;
    private PatternLockView mPl;
    private TextView mTvTips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mTvTips = findViewById(R.id.tv_tips);
        mPl = findViewById(R.id.pattern_lock);
        PatternLockIndicator mPli = findViewById(R.id.pattern_lock_indicator);
        mPl.setPatternLockIndicator(mPli);
        mPl.setPatternLockListener(mPatternLockListener);
    }

    PatternLockListener mPatternLockListener = new PatternLockListener() {
        @Override
        public void onError(String notify) {
            Toast.makeText(MainActivity.this, notify, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSuccess(List<Integer> index) {
            String p = generatePassword(index);
            if (TextUtils.isEmpty(MainActivity.this.password)) {
                mPl.notifyPatternChanged();
                MainActivity.this.password = p;
                mTvTips.setText("确认解锁图案");
            } else {
                if (MainActivity.this.password.equals(p)) {
                    Toast.makeText(MainActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "与上次绘制图案不一致", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Nullable
    private String generatePassword(List<Integer> index) {
        StringBuffer p = new StringBuffer();
        for (Integer integer : index) {
            p.append(integer);
        }
        return p.toString();
    }
}
