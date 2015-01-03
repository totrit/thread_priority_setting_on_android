
package com.example.thread_priority;

import java.util.Random;

import com.example.android_thread_test.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button btnJava = (Button)findViewById(R.id.button_java);
        btnJava.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sStopped = false;
                testJavaTypePrioritySetting();
            }
        });
        
        Button btnAndroid= (Button)findViewById(R.id.button_android);
        btnAndroid.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sStopped = false;
                testAndroidTypePrioritySetting();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private static final int MSG_GO = 0;
    private static final int MSG_TIME_UP = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case MSG_GO: {
                    synchronized (mLock) {
                        mLock.notifyAll();
                    }
                    sendEmptyMessageDelayed(MSG_TIME_UP, 2 * 60 * 1000);
                    break;
                }
                case MSG_TIME_UP: {
                    sStopped = true;
                    break;
                }
            }
        }
    };
    
    private volatile static boolean sStopped = false;
    private Object mLock = new Object();
    private void testJavaTypePrioritySetting() {
        for (int i = Thread.MIN_PRIORITY; i <= Thread.MAX_PRIORITY; i ++) {
            createThreadWithPriority(new TestRunnable(true, i), true, i);
        }
        mHandler.sendEmptyMessageDelayed(MSG_GO, 1000);
    }
    
    private void testAndroidTypePrioritySetting() {
        for (int i = 19; i >= -20; i --) {
            createThreadWithPriority(new TestRunnable(false, i), false, i);
        }
        mHandler.sendEmptyMessageDelayed(MSG_GO, 1000);
    }
    
    private void  createThreadWithPriority(Runnable func, boolean javaTypePriority, int priority) {
        Thread newThread = new Thread(func);
        if (javaTypePriority) {
            newThread.setPriority(priority);
        } else {
            // set thread priority in the runnable instead of here.
        }
        newThread.start();
    }
    
    private class TestRunnable implements Runnable {
        private int mPriority;
        private boolean mIsJavaPriority;
        
        public TestRunnable(boolean javaPriority, int priority) {
            mIsJavaPriority = javaPriority;
            mPriority = priority;
        }
        
        @Override
        public void run() {
            if (mIsJavaPriority) {
               // do nothing.
            } else {
                android.os.Process.setThreadPriority(mPriority);
            }
            synchronized (mLock) {
                try {
                    mLock.wait();
                } catch (InterruptedException e) {
                }
            }
            int counter = 0;
            while(!sStopped) {
                new Random().nextInt();
                counter ++;
            }
            android.util.Log.d("thread-test", "priority " + mPriority + " " + counter + " times.");
        }
    }
}
