/*
 * Copyright (c) 2019-2020 GeyserMC. http://geysermc.org
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

package org.geysermc.connector.utils;

import com.github.steveice10.opennbt.tag.builtin.*;
import com.nukkitx.nbt.CompoundTagBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemUtils {

    public static int getEnchantmentLevel(CompoundTag itemNBTData, String enchantmentId) {
        ListTag enchantments = (itemNBTData == null ? null : itemNBTData.get("Enchantments"));
        if (enchantments != null) {
            int enchantmentLevel = 0;
            for (Tag tag : enchantments) {
                CompoundTag enchantment = (CompoundTag) tag;
                StringTag enchantId = enchantment.get("id");
                if (enchantId.getValue().equals(enchantmentId)) {
                    enchantmentLevel = (int) ((ShortTag) enchantment.get("lvl")).getValue();
                }
            }
            return enchantmentLevel;
        }
        return 0;
    }

    /**
     * Convert a list of patterns from Java nbt to Bedrock nbt
     *
     * @param patterns The patterns to convert
     * @return The new converted patterns
     */
    public static com.nukkitx.nbt.tag.ListTag convertBannerPattern(ListTag patterns) {
        List<com.nukkitx.nbt.tag.CompoundTag> tagsList = new ArrayList<>();
        for (com.github.steveice10.opennbt.tag.builtin.Tag patternTag : patterns.getValue()) {
            com.nukkitx.nbt.tag.CompoundTag newPatternTag = getBedrockBannerPattern((CompoundTag) patternTag);
            if (newPatternTag != null) {
                tagsList.add(newPatternTag);
            }
        }

        return new com.nukkitx.nbt.tag.ListTag<>("Patterns", com.nukkitx.nbt.tag.CompoundTag.class, tagsList);
    }

    /**
     * Convert the Java edition banner pattern nbt to Bedrock edition, null if the pattern doesn't exist
     *
     * @param pattern Java edition pattern nbt
     * @return The Bedrock edition format pattern nbt
     */
    public static com.nukkitx.nbt.tag.CompoundTag getBedrockBannerPattern(CompoundTag pattern) {
        String patternName = (String) pattern.get("Pattern").getValue();

        // Return null if its the globe pattern as it doesn't exist on bedrock
        if (patternName.equals("glb")) {
            return null;
        }

        return CompoundTagBuilder.builder()
                .intTag("Color", 15 - (int) pattern.get("Color").getValue())
                .stringTag("Pattern", (String) pattern.get("Pattern").getValue())
                .stringTag("Pattern", patternName)
                .buildRootTag();
    }

    /**
     * Convert a list of patterns from Bedrock nbt to Java nbt
     *
     * @param patterns The patterns to convert
     * @return The new converted patterns
     */
    public static ListTag convertBannerPattern(com.nukkitx.nbt.tag.ListTag patterns) {
        List<Tag> tagsList = new ArrayList<>();
        for (Object patternTag : patterns.getValue()) {
            CompoundTag newPatternTag = getJavaBannerPattern((com.nukkitx.nbt.tag.CompoundTag) patternTag);
            if (newPatternTag != null) {
                tagsList.add(newPatternTag);
            }
        }

        return new ListTag("Patterns", tagsList);
    }

    /**
     * Convert the Bedrock edition banner pattern nbt to Java edition
     *
     * @param pattern Bedorck edition pattern nbt
     * @return The Java edition format pattern nbt
     */
    public static CompoundTag getJavaBannerPattern(com.nukkitx.nbt.tag.CompoundTag pattern) {
        String patternName = (String) pattern.get("Pattern").getValue();

        Map<String, Tag> tags = new HashMap<>();
        tags.put("Color", new IntTag("Color", 15 - pattern.getInt("Color")));
        tags.put("Pattern", new StringTag("Pattern", pattern.getString("Pattern")));

        return new CompoundTag("", tags);
    }
}
