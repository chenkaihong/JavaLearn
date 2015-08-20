package com.bear.demo.nettyDemo;

import com.bear.demo.nettyDemo.CmdBox.Cmd;

public class GameMessageTemp {
	public final Cmd cmd;
	public final Object message;
	
	public GameMessageTemp(Cmd cmd, Object message) {
		this.cmd = cmd;
		this.message = message;
	}
}
