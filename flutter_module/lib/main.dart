import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'dart:math';
import 'dart:async';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

// FractionallySizedBox 按照屏幕比例
class _MyAppState extends State<MyApp> {
  // 每条消息的高度
  static const double _ITEM_HEIGHT = 30.0;

  // message 数组
  List<Widget> _messageWidgets = <Widget>[];

  // 消息通道
  static const BasicMessageChannel<String> channel =
      BasicMessageChannel<String>('foo', StringCodec());

  // ListView 滑动控制器
  ScrollController _scrollController = ScrollController();

  @override
  void initState() {
    super.initState();
    // 接收 Android 发送过来的消息，并且回复
    channel.setMessageHandler((String message) async {
      String replyMessage = 'Flutter: ${Random().nextInt(100)}';
      setState(() {
        // 收到的android 端的消息
        _messageWidgets.add(_buildMessageWidget(message, true));
        _scrollToBottom();
      });

      Future.delayed(const Duration(milliseconds: 500), () {
        setState(() {
          // 回复给 android 端的消息
          _messageWidgets.add(_buildMessageWidget(replyMessage, false));
          _scrollToBottom();
        });
      });

      // 回复
      return replyMessage;
    });
  }

  // 控制 ListView 滚动到最下方
  void _scrollToBottom() {
    double height = context.size.height;
    print('height $height');
    if (_messageWidgets.length * _ITEM_HEIGHT > height - (height * 0.2)) {
      _scrollController.animateTo(_messageWidgets.length * _ITEM_HEIGHT,
          duration: const Duration(milliseconds: 800), curve: Curves.ease);
    }
  }

  // 向 Android 发送消息
  void _sendMessageToAndroid(String message) {
    setState(() {
      _messageWidgets.add(_buildMessageWidget(message, false));
      _scrollToBottom();
    });
    // 向 Android 端发送发送消息并处理 Android 端给的回复
    channel.send(message).then((value) {
      setState(() {
        _messageWidgets.add(_buildMessageWidget(value, true));
        _scrollToBottom();
      });
    });
  }

  /// 创建一条消息
  Widget _buildMessageWidget(String message, bool isReply) {
    return Container(
      height: _ITEM_HEIGHT,
      child: FractionallySizedBox(
        widthFactor: 1,
        child: Padding(
          padding: EdgeInsets.only(left: 8.0, right: 8.0),
          child: Text(
            message,
            style: TextStyle(
              fontWeight: FontWeight.bold,
            ),
            textAlign: isReply ? TextAlign.start : TextAlign.end,
          ),
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        body: Column(
          children: <Widget>[
            Expanded(
              flex: 8,
              child: Stack(
                children: <Widget>[
                  Container(
                    color: Color.fromARGB(255, 86, 196, 245),
                  ),
                  ListView.builder(
                    itemCount: _messageWidgets.length,
                    controller: _scrollController,
                    shrinkWrap: true,
                    itemBuilder: (BuildContext context, int position) {
                      return _messageWidgets[position];
                    },
                  ),
                ],
              ),
            ),
            Expanded(
              flex: 2,
              child: Stack(
                children: <Widget>[
                  Container(
                    color: Colors.yellow,
                  ),
                  Center(
                    child: RaisedButton(
                      child: Text('Send message to Android'),
                      onPressed: () {
                        _sendMessageToAndroid(
                            'Flutter: ${Random().nextInt(100)}');
                      },
                    ),
                  )
                ],
              ),
            )
          ],
        ),
      ),
    );
  }
}
