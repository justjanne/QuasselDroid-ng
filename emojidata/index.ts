import emojiData from 'emojibase-data/en/data.json';
import shortcodesEmojibase from 'emojibase-data/en/shortcodes/emojibase.json';
import shortcodesCustom from './custom.json';
import promises from "fs";
import * as path from "path";
import { Emoji } from "emojibase";

type QuasseldroidEmoji = {
    label: string,
    tags: string[],
    shortcodes: string[],
    replacement: string,
}

function mergeShortcodes(...data: (undefined | string | string[])[]): string[] {
    const codes = data.flat();
    const set: Set<string | undefined> = new Set(codes);
    set.delete(undefined);
    const result = Array.from(set as Set<string>).sort();
    console.error(data, result);
    return result;
}

function mapEmojiData(data: Emoji): QuasseldroidEmoji {
    return {
        label: data.label,
        tags: data.tags || [],
        shortcodes: mergeShortcodes(
            shortcodesEmojibase[data.hexcode],
            shortcodesCustom[data.hexcode]
        ),
        replacement: data.emoji,
    };
}

promises.writeFile(
    path.join(__dirname, "../app/src/main/assets/emoji.json"),
    JSON.stringify(emojiData.map(mapEmojiData), null, 2),
    () => undefined,
)
