package io.github.ppdzm.utils.universal.cli;

import io.github.ppdzm.utils.universal.base.Symbols;
import io.github.ppdzm.utils.universal.log.Logging;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import java.util.*;

/**
 * @author Created by Stuart Alex on 2021/5/7.
 */
public class CliUtils {
    protected static Logging logging = new Logging();
    /**
     * 转义符
     */
    private static String ESCAPE = "\u001b[";
    /**
     * 清除屏幕（光标位置不动）
     */
    private static String CLEAR_SCREEN = "2J";
    /**
     * 光标上移
     */
    private static String UP = "A";
    /**
     * 光标下移
     */
    private static String DOWN = "B";
    /**
     * 光标左移
     */
    private static String LEFT = "C";
    /**
     * 光标右移
     */
    private static String RIGHT = "D";
    /**
     * 光标定点
     */
    private static String POINTER = "H";
    /**
     * 删除光标后所有文本
     */
    private static String DELETE_ALL_AFTER_CURSOR = "K";
    /**
     * 保存当前光标位置
     */
    private static String STORE = "s";
    /**
     * 恢复上次光标位置
     */
    private static String RESTORE = "u";

    /**
     * 清除屏幕（光标位置不动）
     */
    public static String clearScreen() {
        return ESCAPE + CLEAR_SCREEN;
    }

    /**
     * 清除屏幕（光标移至最左上角）
     */
    public static String clearScreen2TopLeft() {
        return ESCAPE + CLEAR_SCREEN + ESCAPE + "0;0" + POINTER;
    }

    /**
     * 删除光标后所有文本
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
     */
    public static String down(int n) {
        return ESCAPE + n + DOWN;
    }

    /**
     * 左移若干行
     *
     * @param n 行数
     */
    public static String left(int n) {
        return ESCAPE + n + LEFT;
    }

    /**
     * 光标移至行首
     */
    public static String move2Begin() {
        return Symbols.carriageReturn;
    }

    /**
     * 解析命令行参数
     *
     * @param args 命令行参数
     */
    public static List<String> parseArguments(String[] args, Properties properties) {
        // 解析完以后剩下的args
        List<String> restArgs = new ArrayList<>();
        if (args == null || args.length == 0) {
            return restArgs;
        }
        logging.logInfo("Receive args: " + String.join(",", args));
        Map<String, Object> argumentsMapping = new HashMap<>();
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
            logging.logInfo("Parsed key " + key + ", value " + value + "");
        }
        return restArgs;
    }

    /**
     * 光标移至指定行和列
     *
     * @param x 行呀
     * @param y 列呀
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
     * 渲染文本
     *
     * @param string  原始文本
     * @param renders 渲染器
     * @return String
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
     * @param messages       原始文本
     * @param messageRenders 原始文本与渲染器
     * @return String
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
     * 重置所有设置
     */
    public static String reset() {
        return ESCAPE + Render.RESET;
    }

    /**
     * 恢复上次保存的光标位置
     */
    public static String restore() {
        return ESCAPE + RESTORE;
    }

    /**
     * 右移若干行
     *
     * @param n 行数
     */
    public static String right(int n) {
        return ESCAPE + n + RIGHT;
    }

    /**
     * 保存光标当前所在位置
     */
    public static String store() {
        return ESCAPE + STORE;
    }

    /**
     * 上移若干行
     *
     * @param n 行数
     */
    public static String up(int n) {
        return ESCAPE + n + UP;
    }

}
