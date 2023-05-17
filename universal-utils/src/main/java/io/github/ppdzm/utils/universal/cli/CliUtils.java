package io.github.ppdzm.utils.universal.cli;

import io.github.ppdzm.utils.universal.base.Logging;
import io.github.ppdzm.utils.universal.base.StringUtils;
import io.github.ppdzm.utils.universal.base.Symbols;
import io.github.ppdzm.utils.universal.core.SystemProperties;
import io.github.ppdzm.utils.universal.tuple.Tuple2;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import java.util.*;
import java.util.stream.Collectors;

import static io.github.ppdzm.utils.universal.base.Symbols.CARRIAGE_RETURN;

/**
 * @author Created by Stuart Alex on 2021/5/7.
 */
public class CliUtils {
    protected static Logging logging = new Logging(CliUtils.class);
    /**
     * 转义符
     */
    private static final String ESCAPE = "\u001b[";
    /**
     * 清除屏幕（光标位置不动）
     */
    private static final String CLEAR_SCREEN = "2J";
    /**
     * 光标上移
     */
    private static final String UP = "A";
    /**
     * 光标下移
     */
    private static final String DOWN = "B";
    /**
     * 光标左移
     */
    private static final String LEFT = "C";
    /**
     * 光标右移
     */
    private static final String RIGHT = "D";
    /**
     * 光标定点
     */
    private static final String POINTER = "H";
    /**
     * 删除光标后所有文本
     */
    private static final String DELETE_ALL_AFTER_CURSOR = "K";
    /**
     * 保存当前光标位置
     */
    private static final String STORE = "s";
    /**
     * 恢复上次光标位置
     */
    private static final String RESTORE = "u";

    /**
     * 清除屏幕（光标位置不动）
     *
     * @return string
     */
    public static String clearScreen() {
        return ESCAPE + CLEAR_SCREEN;
    }

    /**
     * 清除屏幕（光标移至最左上角）
     *
     * @return string
     */
    public static String clearScreen2TopLeft() {
        return ESCAPE + CLEAR_SCREEN + ESCAPE + "0;0" + POINTER;
    }

    /**
     * 删除光标后所有文本
     *
     * @return string
     */
    public static String deleteAllAfterCursor() {
        return ESCAPE + DELETE_ALL_AFTER_CURSOR;
    }

    /**
     * 删除当前行
     */
    public static void deleteCurrentRow() {
        System.out.print(move2Begin() + deleteAllAfterCursor());
    }

    /**
     * 向上删除若干行
     *
     * @param n 删除的行数
     */
    public static void deleteRowsUpward(int n) {
        for (int i = 0; i < n; i++) {
            System.out.print(up(1) + move2Begin() + deleteAllAfterCursor());
        }
    }

    /**
     * 下移若干行
     *
     * @param n 行数
     * @return string
     */
    public static String down(int n) {
        return ESCAPE + n + DOWN;
    }

    /**
     * 左移若干行
     *
     * @param n 行数
     * @return string
     */
    public static String left(int n) {
        return ESCAPE + n + LEFT;
    }

    /**
     * 光标移至行首
     *
     * @return string
     */
    public static String move2Begin() {
        return String.valueOf(CARRIAGE_RETURN);
    }

    /**
     * 解析命令行参数
     *
     * @param args       命令行参数
     * @param properties 解析后的配置存储在这里
     * @return string list
     */
    public static List<String> parseArguments(String[] args, Properties properties) {
        // 解析完以后剩下的args
        List<String> restArgs = new ArrayList<>();
        if (args == null || args.length == 0) {
            return restArgs;
        }
        logging.logInfo("Receive args: " + rendering(String.join(", ", args), Render.GREEN));
        Map<String, Object> argumentsMapping = new HashMap<>(4);
        int i = 0;
        while (i < args.length) {
            if (args[i].startsWith("--")) {
                if (args[i].contains("=")) {
                    String[] kva = args[i].substring(2).split("=");
                    i += 1;
                    // --current-key=value
                    if (kva.length == 1) {
                        argumentsMapping.put(kva[0], "");
                    } else {
                        argumentsMapping.put(kva[0], kva[1]);
                    }
                } else {
                    if (args.length > i + 1) {
                        if (args[i + 1].startsWith("--")) {
                            // --current-key --next-key
                            argumentsMapping.put(args[i].substring(2), true);
                            i += 1;
                        } else {
                            // --current-key value
                            argumentsMapping.put(args[i].substring(2), args[i + 1]);
                            i += 2;
                        }
                    } else {
                        // --last-key
                        argumentsMapping.put(args[i].substring(2), true);
                        i += 1;
                    }
                }
            } else {
                // parameter
                restArgs.add(args[i]);
                i += 1;
            }
        }
        for (String key : argumentsMapping.keySet()) {
            Object value = argumentsMapping.get(key);
            properties.put(key, value.toString());
            logging.logInfo(
                    CliUtils.rendering("Parsed config ", Render.MAGENTA) +
                            CliUtils.rendering(key, Render.GREEN) +
                            CliUtils.rendering(" => ", Render.MAGENTA) +
                            CliUtils.rendering(value.toString(), Render.GREEN)
            );
        }
        return restArgs;
    }

    /**
     * 光标移至指定行和列
     *
     * @param x 行呀
     * @param y 列呀
     * @return string
     */
    public static String point(int x, int y) {
        return ESCAPE + x + ";" + y + POINTER;
    }

    /**
     * 打印程序帮助文档
     *
     * @param usageSyntax 用法语法
     * @param header      文档首部显示的文字
     * @param options     程序选项列表
     * @param footer      文档尾部显示的文字
     */
    public static void printHelp(String usageSyntax, String header, Options options, String footer) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setWidth(150);
        helpFormatter.setSyntaxPrefix("");
        helpFormatter.printHelp(usageSyntax, header, options, footer);
    }

    /**
     * 打印输出帮助文档
     *
     * @param help 命令——帮助
     */
    public static void printHelp(List<Tuple2<String, String>> help, String render) {
        int maxLength = help.stream().mapToInt(e -> e.f1.length()).max().orElse(0);
        help.stream()
                .map(e -> {
                    String[] parts = e.f1.split("\n");
                    if (parts.length > 1) {
                        String head = parts[0];
                        String[] tail = Arrays.copyOfRange(parts, 1, parts.length);
                        String indentedTail =
                                Arrays.stream(tail)
                                        .map(line -> StringUtils.repeat(" ", maxLength) + "\t" + line)
                                        .collect(Collectors.joining("\t", "\n\t", ""));
                        return StringUtils.rightPad(e.f1, maxLength) + "\t" + head + indentedTail;
                    } else {
                        return StringUtils.rightPad(e.f1, maxLength) + "\t" + e.f1.toString();
                    }
                })
                .forEach(e -> System.out.println(rendering(e, render)));
    }


    /**
     * 以字符颜色渲染的方式输出有限循环程序的当前执行进度
     *
     * @param sum        总循环次数
     * @param present    当前已执行次数
     * @param textRender 文本渲染器 {@link io.github.ppdzm.utils.universal.cli.Render}
     */
    public static void printStage(int sum, int present, String textRender) {
        printStage(null, sum, present, ' ', textRender);
    }

    /**
     * 以字符颜色渲染的方式输出有限循环程序的当前执行进度
     *
     * @param mission    程序段的任务命名
     * @param sum        总循环次数
     * @param present    当前已执行次数
     * @param textRender 文本渲染器 {@link io.github.ppdzm.utils.universal.cli.Render}
     */
    public static void printStage(String mission, int sum, int present, String textRender) {
        printStage(mission, sum, present, ' ', textRender);
    }

    /**
     * 以字符颜色渲染的方式输出有限循环程序的当前执行进度
     *
     * @param sum        总循环次数
     * @param present    当前已执行次数
     * @param textRender 文本渲染器 {@link io.github.ppdzm.utils.universal.cli.Render}
     */
    public static void printStage(int sum, int present, char symbol, String textRender) {
        printStage(null, sum, present, symbol, textRender);
    }

    /**
     * 以字符颜色渲染的方式输出有限循环程序的当前执行进度
     *
     * @param mission    程序段的任务命名
     * @param sum        总循环次数
     * @param present    当前已执行次数
     * @param symbol     显示进度使用的字符
     * @param textRender 文本渲染器 {@link io.github.ppdzm.utils.universal.cli.Render}
     */
    public static void printStage(String mission, int sum, int present, char symbol, String textRender) {
        String backgroundColor = "";
        if (symbol == Symbols.BLANK.charAt(0)) {
            backgroundColor = "0;42";
        } else {
            backgroundColor = textRender;
        }
        int percentage = present * 100 / sum;
        String bar = StringUtils.repeat(symbol, percentage);
        String blanks = StringUtils.repeat(' ', 100 - percentage);
        String padding = StringUtils.repeat(' ', String.valueOf(sum).length() - String.valueOf(present).length());
        String missionInHead = "";
        String startPrompt = "";
        String endPrompt = "";
        if (StringUtils.isNotNullAndEmpty(mission)) {
            missionInHead = mission + ": ";
            startPrompt = "INFO Start execute mission【" + rendering(mission, textRender) + rendering("】", Render.MAGENTA);
            endPrompt = "INFO Mission 【" + rendering(mission, textRender) + "】 accomplished";
        }
        String head = rendering("[", Render.MAGENTA) +
                rendering(missionInHead + bar + ">", backgroundColor) +
                rendering(blanks + "(" + padding + present + "/" + sum + "," + StringUtils.takeRight("  " + percentage, 3) + "%)", textRender) +
                rendering("]", Render.MAGENTA);
        String progress = "";
        if (present == 1) {
            logging.logInfo(startPrompt);
            System.out.println(head);
        } else if (present < sum) {
            deleteCurrentRow();
            System.out.println(CARRIAGE_RETURN + head);
        } else {
            deleteCurrentRow();
            System.out.println(CARRIAGE_RETURN + head);
            logging.logInfo(endPrompt);
        }
    }

    /**
     * 渲染文本
     *
     * @param messages       原始文本
     * @param messageRenders 原始文本与渲染器
     * @return string
     */
    public static String rendering(List<String> messages, Map<String, Render> messageRenders) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : messages) {
            Render render = messageRenders.get(key);
            stringBuilder.append(rendering(key, render));
        }
        return stringBuilder.toString();
    }

    /**
     * 渲染文本
     *
     * @param messages 原始文本与渲染器
     * @return String
     */
    public static String rendering(Map<String, Render> messages) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String msg : messages.keySet()) {
            Render render = messages.get(msg);
            stringBuilder.append(rendering(msg, render));
        }
        return stringBuilder.toString();
    }

    /**
     * 渲染文本
     *
     * @param string  原始文本
     * @param renders 渲染器
     * @return string
     */
    public static String rendering(String string, String renders) {
        return rendering(string, Arrays.stream(renders.split(";")).map(Integer::parseInt).map(Render::valueOf).collect(Collectors.toList()));
    }

    /**
     * 渲染文本
     *
     * @param string  原始文本
     * @param renders 渲染器
     * @return string
     */
    public static String rendering(String string, Render... renders) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Render render : renders) {
            stringBuilder.append(render.getValue()).append(";");
        }
        return ESCAPE + stringBuilder.substring(0, stringBuilder.lastIndexOf(";")) + "m" + string + ESCAPE + Render.RESET + "m";
    }

    /**
     * 渲染文本
     *
     * @param string  原始文本
     * @param renders 渲染器
     * @return string
     */
    public static String rendering(String string, List<Render> renders) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Render render : renders) {
            stringBuilder.append(render.getValue()).append(";");
        }
        return ESCAPE + stringBuilder.substring(0, stringBuilder.lastIndexOf(";")) + "m" + string + ESCAPE + Render.RESET + "m";
    }

    /**
     * 重置所有设置
     *
     * @return string
     */
    public static String reset() {
        return ESCAPE + Render.RESET;
    }

    /**
     * 恢复上次保存的光标位置
     *
     * @return string
     */
    public static String restore() {
        return ESCAPE + RESTORE;
    }

    /**
     * 右移若干行
     *
     * @param n 行数
     * @return string
     */
    public static String right(int n) {
        return ESCAPE + n + RIGHT;
    }

    /**
     * 保存光标当前所在位置
     *
     * @return string
     */
    public static String store() {
        return ESCAPE + STORE;
    }

    /**
     * 上移若干行
     *
     * @param n 行数
     * @return string
     */
    public static String up(int n) {
        return ESCAPE + n + UP;
    }

    /**
     * 等待，并显示进度
     *
     * @param sum        总循环次数
     * @param textRender 文本渲染器 {@link io.github.ppdzm.utils.universal.cli.Render}
     */
    public static void waiting(int sum, String textRender) {
        waiting(null, sum, ' ', textRender, 1000);
    }

    /**
     * 等待，并显示进度
     *
     * @param sum        总循环次数
     * @param textRender 文本渲染器 {@link io.github.ppdzm.utils.universal.cli.Render}
     * @param interval   休眠间隔
     */
    public static void waiting(int sum, String textRender, int interval) {
        waiting(null, sum, ' ', textRender, interval);
    }

    /**
     * 等待，并显示进度
     *
     * @param sum        总循环次数
     * @param symbol     显示进度使用的字符
     * @param textRender 文本渲染器 {@link io.github.ppdzm.utils.universal.cli.Render}
     */
    public static void waiting(int sum, char symbol, String textRender) {
        waiting(null, sum, symbol, textRender, 1000);
    }

    /**
     * 等待，并显示进度
     *
     * @param sum        总循环次数
     * @param symbol     显示进度使用的字符
     * @param textRender 文本渲染器 {@link io.github.ppdzm.utils.universal.cli.Render}
     * @param interval   休眠间隔
     */
    public static void waiting(int sum, char symbol, String textRender, int interval) {
        waiting(null, sum, symbol, textRender, interval);
    }

    /**
     * 等待，并显示进度
     *
     * @param mission    程序段的任务命名
     * @param sum        总循环次数
     * @param textRender 文本渲染器 {@link io.github.ppdzm.utils.universal.cli.Render}
     */
    public static void waiting(String mission, int sum, String textRender) {
        waiting(mission, sum, ' ', textRender, 1000);
    }

    /**
     * 等待，并显示进度
     *
     * @param mission    程序段的任务命名
     * @param sum        总循环次数
     * @param textRender 文本渲染器 {@link io.github.ppdzm.utils.universal.cli.Render}
     * @param interval   休眠间隔
     */
    public static void waiting(String mission, int sum, String textRender, int interval) {
        waiting(mission, sum, ' ', textRender, interval);
    }

    /**
     * 等待，并显示进度
     *
     * @param mission    程序段的任务命名
     * @param sum        总循环次数
     * @param symbol     显示进度使用的字符
     * @param textRender 文本渲染器 {@link io.github.ppdzm.utils.universal.cli.Render}
     */
    public static void waiting(String mission, int sum, char symbol, String textRender) {
        waiting(mission, sum, symbol, textRender, 1000);
    }

    /**
     * 等待，并显示进度
     *
     * @param mission    程序段的任务命名
     * @param sum        总循环次数
     * @param symbol     显示进度使用的字符
     * @param textRender 文本渲染器 {@link io.github.ppdzm.utils.universal.cli.Render}
     * @param interval   休眠间隔
     */
    public static void waiting(String mission, int sum, char symbol, String textRender, int interval) {
        SystemProperties.setLogging2Stdout(true);
        for (int i = 1; i <= sum; i++) {
            printStage(mission, sum, i, symbol, textRender);
            if (interval > 0) {
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public static void main(String[] args) {
        waiting("my mission", 10, '=', "0;32", 0);
    }

}
