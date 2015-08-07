package com.gzyouai.hummingbird.engine.tryself5.initialization;

import com.gzyouai.hummingbird.engine.tryself5.handle.HandleRegistered;
import com.gzyouai.hummingbird.engine.tryself5.handle.quest.QuestHandle;
import com.gzyouai.hummingbird.engine.tryself5.handle.walk.WalkHandle;

public class HandleLoader implements Loader{

	@Override
	public void load() throws Exception{
		
		HandleRegistered.register(100, QuestHandle.class);
		HandleRegistered.register(101, WalkHandle.class);
		
	}
}
