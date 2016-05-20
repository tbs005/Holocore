/***********************************************************************************
* Copyright (c) 2015 /// Project SWG /// www.projectswg.com                        *
*                                                                                  *
* ProjectSWG is the first NGE emulator for Star Wars Galaxies founded on           *
* July 7th, 2011 after SOE announced the official shutdown of Star Wars Galaxies.  *
* Our goal is to create an emulator which will provide a server for players to     *
* continue playing a game similar to the one they used to play. We are basing      *
* it on the final publish of the game prior to end-game events.                    *
*                                                                                  *
* This file is part of Holocore.                                                   *
*                                                                                  *
* -------------------------------------------------------------------------------- *
*                                                                                  *
* Holocore is free software: you can redistribute it and/or modify                 *
* it under the terms of the GNU Affero General Public License as                   *
* published by the Free Software Foundation, either version 3 of the               *
* License, or (at your option) any later version.                                  *
*                                                                                  *
* Holocore is distributed in the hope that it will be useful,                      *
* but WITHOUT ANY WARRANTY; without even the implied warranty of                   *
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                    *
* GNU Affero General Public License for more details.                              *
*                                                                                  *
* You should have received a copy of the GNU Affero General Public License         *
* along with Holocore.  If not, see <http://www.gnu.org/licenses/>.                *
*                                                                                  *
***********************************************************************************/
package resources.commands.callbacks;

import network.packets.swg.zone.object_controller.BiographyUpdate;
import resources.commands.ICmdCallback;
import resources.objects.SWGObject;
import resources.objects.creature.CreatureObject;
import resources.player.Player;
import resources.server_info.Log;
import services.galaxy.GalacticManager;

/**
 *
 * @author mads
 */
public class RequestBiographyCmdCallback implements ICmdCallback {

	@Override
	public void execute(GalacticManager galacticManager, Player player, SWGObject target, String args) {
		CreatureObject creatureObject = player.getCreatureObject();
		if(target != null) {
			if(creatureObject.isPlayer()) {
				player.sendPacket(new BiographyUpdate(creatureObject.getObjectId(), target.getObjectId(), creatureObject.getPlayerObject().getBiography()));
			} else {
				Log.w("RequestBiographyCmdCallback", "%s tried to request biography of NPC %s", creatureObject, target);
			}
		} else {
			Log.w("RequestBiographyCmdCallback", "%s tried to request biography of a null object", creatureObject);
		}
	}
	
}