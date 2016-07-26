package me.fingerart.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import me.fingerart.patternlock.interf.PatternLockListener;
import me.fingerart.patternlock.view.PatternLockIndicator;
import me.fingerart.patternlock.view.PatternLockView;

public class MainActivity extends AppCompatActivity {
    private String password;
    private PatternLockIndicator mPli;
    private TextView mTvTips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mTvTips = (TextView) findViewById(R.id.tv_tips);
        PatternLockView pl = (PatternLockView) findViewById(R.id.pattern_lock);
        mPli = (PatternLockIndicator) findViewById(R.id.pattern_lock_indicator);
        pl.setPatternLockListener(mPatternLockListener);
    }

    PatternLockListener mPatternLockListener = new PatternLockListener() {
        @Override
        public void onError(String notify) {
            Toast.makeText(MainActivity.this, notify, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSuccess(String p, List<Integer> i) {
            if (TextUtils.isEmpty(password)) {
                mPli.setIndicator(i);
                password = p;
                mTvTips.setText("确认解锁图案");
            } else {
                if (password.equals(p.toString())) {
                    Toast.makeText(MainActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "与上次绘制图案不一致", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
