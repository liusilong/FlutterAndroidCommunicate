package com.lsl.commwidthflutter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Random;

import io.flutter.app.FlutterFragmentActivity;
import io.flutter.plugin.common.BasicMessageChannel;
import io.flutter.plugin.common.StringCodec;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends FlutterFragmentActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    FrameLayout flutterContainer;
    ScrollView scrollView;
    LinearLayout linearMessageContainer;
    Button btnSend;
    private BasicMessageChannel<String> basicMessageChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        GeneratedPluginRegistrant.registerWith(this);

        setContentView(R.layout.activity_main);

        flutterContainer = findViewById(R.id.flutter_container);
        scrollView = findViewById(R.id.scroll_view);
        linearMessageContainer = findViewById(R.id.linear_message_container);
        btnSend = findViewById(R.id.btn_send);
        btnSend.setOnClickListener(this);


        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        flutterContainer.addView(getFlutterView(), lp);

        basicMessageChannel = new BasicMessageChannel<String>(getFlutterView(), "foo", StringCodec.INSTANCE);

        // 接收 Flutter 发送的消息
        basicMessageChannel.setMessageHandler(new BasicMessageChannel.MessageHandler<String>() {
            @Override
            public void onMessage(final String s, final BasicMessageChannel.Reply<String> reply) {

                // 接收到的消息
                linearMessageContainer.addView(buildMessage(s, true));
                scrollToBottom();

                // 延迟 500ms 回复
                flutterContainer.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 回复 Flutter
                        String replyMsg = "Android : " + new Random().nextInt(100);
                        linearMessageContainer.addView(buildMessage(replyMsg, false));
                        scrollToBottom();
                        // 回复
                        reply.reply(replyMsg);
                    }
                }, 500);

            }
        });
    }

    private void scrollToBottom() {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private TextView buildMessage(String message, boolean isReply) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
        lp.setMarginEnd(16);
        lp.setMarginStart(16);
        TextView textView = new TextView(this);
        textView.setLayoutParams(lp);
        textView.setGravity(isReply ? Gravity.START : Gravity.END);
        textView.setTextColor(Color.BLACK);
        textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        textView.setText(message);

        return textView;
    }

    @Override
    public void onClick(View view) {

        String message = "Android: " + new Random().nextInt(100);

        linearMessageContainer.addView(buildMessage(message, false));
        scrollToBottom();

        // 向 Flutter 发送消息
        basicMessageChannel.send(message, new BasicMessageChannel.Reply<String>() {
            @Override
            public void reply(final String s) {
                linearMessageContainer.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Flutter 的回复
                        linearMessageContainer.addView(buildMessage(s, true));
                        scrollToBottom();
                    }
                }, 500);

            }
        });


    }

}
