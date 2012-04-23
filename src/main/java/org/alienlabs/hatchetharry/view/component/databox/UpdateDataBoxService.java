/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.alienlabs.hatchetharry.view.component.databox;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class UpdateDataBoxService
{
	private final Map<String, UpdateDataBoxChannel> chatroomsByName = new HashMap<String, UpdateDataBoxChannel>();

	public UpdateDataBoxChannel getChatRoom(final String name)
	{
		UpdateDataBoxChannel room = this.chatroomsByName.get(name);
		if (room == null)
		{
			room = new UpdateDataBoxChannel(name);
			this.chatroomsByName.put(name, room);
		}
		return room;
	}
}
