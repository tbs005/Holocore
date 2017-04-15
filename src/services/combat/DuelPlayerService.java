/************************************************************************************
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
package services.combat;

import com.projectswg.common.control.Service;

import intents.chat.ChatBroadcastIntent;
import intents.combat.DuelPlayerIntent;
import resources.PvpFlag;
import resources.encodables.ProsePackage;
import resources.encodables.StringId;
import resources.objects.creature.CreatureObject;

public class DuelPlayerService extends Service {
	
	public DuelPlayerService() {
		registerForIntent(DuelPlayerIntent.class, dpi -> handleDuelPlayerIntent(dpi));
	}
	
	private void handleAcceptDuel(CreatureObject accepter, CreatureObject target) {
		if (accepter.isDuelingPlayer(target)) {
			sendSystemMessage(accepter, target, "already_dueling");
			return;
		}
		accepter.addPlayerToSentDuels(target);
		sendSystemMessage(accepter, target, "accept_self");
		sendSystemMessage(target, accepter, "accept_target");
		accepter.setPvpFlags(PvpFlag.DUEL);
		target.setPvpFlags(PvpFlag.DUEL);
	}
	
	private void endDuel(CreatureObject ender, CreatureObject target) {
		ender.clearPvpFlags(PvpFlag.DUEL);
		target.clearPvpFlags(PvpFlag.DUEL);
		ender.removePlayerFromSentDuels(target);
		target.removePlayerFromSentDuels(ender);
		ender.setInCombat(false);
		target.setInCombat(false);
	}
	
	private void handleEndDuel(CreatureObject ender, CreatureObject target) {
		if (ender.isDuelingPlayer(target)) {
			sendSystemMessage(ender, target, "end_self");
			sendSystemMessage(target, ender, "end_target");
			endDuel(ender, target);
		} else {
			sendSystemMessage(ender, target, "not_dueling");
		}
	}
	
	private void handleCancelDuel(CreatureObject canceler, CreatureObject target) {
		canceler.removePlayerFromSentDuels(target);
		sendSystemMessage(canceler, target, "cancel_self");
		sendSystemMessage(target, canceler, "cancel_target");
	}
	
	private void handleDeclineDuel(CreatureObject decliner, CreatureObject target) {
		target.removePlayerFromSentDuels(decliner);
		sendSystemMessage(decliner, target, "reject_self");
		sendSystemMessage(target, decliner, "reject_target");
	}
	
	private void handleRequestDuel(CreatureObject requester, CreatureObject target) {
		if (!requester.hasSentDuelRequestToPlayer(target)) {
			requester.addPlayerToSentDuels(target);
			sendSystemMessage(requester, target, "challenge_self");
			sendSystemMessage(target, requester, "challenge_target");
		} else if (requester.isDuelingPlayer(target)) {
			sendSystemMessage(requester, target, "already_dueling");
		} else {
			sendSystemMessage(requester, target, "already_challenged");
		}
	}
	
	private void handleDuelPlayerIntent(DuelPlayerIntent dpi) {
		if (dpi.getReciever() == null || !dpi.getReciever().isPlayer() || dpi.getSender().equals(dpi.getReciever())) {
			return;
		}
		
		switch (dpi.getEventType()) {
			case ACCEPT:
				handleAcceptDuel(dpi.getSender(), dpi.getReciever());
				break;
			case CANCEL:
				handleCancelDuel(dpi.getSender(), dpi.getReciever());
				break;
			case DECLINE:
				handleDeclineDuel(dpi.getSender(), dpi.getReciever());
				break;
			case END:
				handleEndDuel(dpi.getSender(), dpi.getReciever());
				break;
			case REQUEST:
				handleRequestDuel(dpi.getSender(), dpi.getReciever());
				break;
		}
	}
	
	private void sendSystemMessage(CreatureObject playerToMessage, CreatureObject playerToMessageAbout, String message) {
		new ChatBroadcastIntent(playerToMessage.getOwner() , new ProsePackage(new StringId("duel", message), "TT", playerToMessageAbout.getObjectName())).broadcast();
	}
}