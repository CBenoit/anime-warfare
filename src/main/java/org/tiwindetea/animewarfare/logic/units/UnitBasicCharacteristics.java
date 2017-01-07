////////////////////////////////////////////////////////////
//
// Anime Warfare
// Copyright (C) 2016 TiWinDeTea - contact@tiwindetea.org
//
// This software is provided 'as-is', without any express or implied warranty.
// In no event will the authors be held liable for any damages arising from the use of this software.
//
// Permission is granted to anyone to use this software for any purpose,
// including commercial applications, and to alter it and redistribute it freely,
// subject to the following restrictions:
//
// 1. The origin of this software must not be misrepresented;
//    you must not claim that you wrote the original software.
//    If you use this software in a product, an acknowledgment
//    in the product documentation would be appreciated but is not required.
//
// 2. Altered source versions must be plainly marked as such,
//    and must not be misrepresented as being the original software.
//
// 3. This notice may not be removed or altered from any source distribution.
//
////////////////////////////////////////////////////////////

package org.tiwindetea.animewarfare.logic.units;

public class UnitBasicCharacteristics {
	public enum Gender {
		MALE,
		FEMALE
	}

	/**
	 * MAGIC_NBR_STUDIO_MASCOT = Number of studio of player and number of mascots of the player
	 */
	public static final float MAGIC_NBR_STUDIO_MASCOT = -1.f;

	/**
	 * MAGIC_NBR_ENEMY_FANS = (Number of enemy fans + 1) /2
	 */
	public static final float MAGIC_NBR_ENEMY_FANS = -2.f;

	public static final float MAGIC_NBR_NBR_ENEMY_HEROES = -3.f;

	private final Gender gender;
	private final float baseAttackPoints;

	UnitBasicCharacteristics(Gender gender, float baseAttackPoints) {
		this.gender = gender;
		this.baseAttackPoints = baseAttackPoints;
	}

	public Gender getGender() {
		return this.gender;
	}

	public float getBaseAttackPoints() {
		return this.baseAttackPoints;
	}
}
