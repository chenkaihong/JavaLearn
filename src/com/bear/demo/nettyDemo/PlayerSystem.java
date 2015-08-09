package com.bear.demo.nettyDemo;

import com.bear.demo.nettyDemo.CmdBox.PlayerMoveRequest;
import com.bear.demo.nettyDemo.CmdBox.PlayerMoveResponse;

@MessageSystem(description="玩家系统", startID=10000, finishID=10099)
public class PlayerSystem implements GameSystem{

	@MessageMothed(messageID=10001, description="移动")
	public PlayerMoveResponse move(PlayerMoveRequest request){
		int step = request.getMoveStep();
		int direction = request.getMoveDirection();
		
		int X = getX(step, direction);
		int Y = getY(step, direction);
		
		return PlayerMoveResponse.newBuilder().setX(X).setY(Y).build();
	}
	
	private int getX(int step, int direction){
		return 5;
	}
	private int getY(int step, int direction){
		return 10;
	}
}
