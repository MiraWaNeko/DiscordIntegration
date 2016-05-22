package chikachi.discord.config.message;

import java.util.regex.Pattern;

class Patterns {
    static final Pattern everyonePattern = Pattern.compile("(^|\\W)@everyone\\b");
    static final Pattern boldPattern = Pattern.compile("\\*\\*(.*)\\*\\*");
    static final Pattern italicPattern = Pattern.compile("\\*(.*)\\*");
    static final Pattern italicMePattern = Pattern.compile("\\*(.*)\\*");
    static final Pattern underlinePattern = Pattern.compile("__(.*)__");
    static final Pattern lineThroughPattern = Pattern.compile("~~(.*)~~");
    static final Pattern singleCodePattern = Pattern.compile("`(.*)`");
    static final Pattern multiCodePattern = Pattern.compile("```(.*)```");
}
