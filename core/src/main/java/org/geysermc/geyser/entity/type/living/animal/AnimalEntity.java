/*
 * Copyright (c) 2019-2022 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/Geyser
 */

package org.geysermc.geyser.entity.type.living.animal;

import com.github.steveice10.mc.protocol.data.game.entity.player.Hand;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.data.entity.EntityEventType;
import com.nukkitx.protocol.bedrock.data.entity.EntityFlag;
import org.geysermc.geyser.entity.EntityDefinition;
import org.geysermc.geyser.entity.type.living.AgeableEntity;
import org.geysermc.geyser.inventory.GeyserItemStack;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.geyser.registry.type.ItemMapping;
import org.geysermc.geyser.util.InteractionResult;
import org.geysermc.geyser.util.InteractiveTag;

import javax.annotation.Nonnull;
import java.util.UUID;

public class AnimalEntity extends AgeableEntity {

    public AnimalEntity(GeyserSession session, int entityId, long geyserId, UUID uuid, EntityDefinition<?> definition, Vector3f position, Vector3f motion, float yaw, float pitch, float headYaw) {
        super(session, entityId, geyserId, uuid, definition, position, motion, yaw, pitch, headYaw);
    }

    public final boolean canEat(GeyserItemStack itemStack) {
        ItemMapping mapping = itemStack.getMapping(session);
        String handIdentifier = mapping.getJavaIdentifier();
        return canEat(handIdentifier.replace("minecraft:", ""), mapping);
    }

    /**
     * @param javaIdentifierStripped the stripped Java identifier of the item that is potential breeding food. For example,
     *                               <code>wheat</code>.
     * @return true if this is a valid item to breed with for this animal.
     */
    public boolean canEat(String javaIdentifierStripped, ItemMapping mapping) {
        // This is what it defaults to. OK.
        return javaIdentifierStripped.equals("wheat");
    }

    @Nonnull
    @Override
    protected InteractiveTag testMobInteraction(Hand hand, @Nonnull GeyserItemStack itemInHand) {
        if (canEat(itemInHand)) {
            return InteractiveTag.FEED;
        }
        return super.testMobInteraction(hand, itemInHand);
    }

    @Nonnull
    @Override
    protected InteractionResult mobInteract(Hand hand, @Nonnull GeyserItemStack itemInHand) {
        if (canEat(itemInHand)) {
            // FEED
            if (getFlag(EntityFlag.BABY)) {
                playEntityEvent(EntityEventType.BABY_ANIMAL_FEED);
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.CONSUME;
            }
        }
        return super.mobInteract(hand, itemInHand);
    }
}
