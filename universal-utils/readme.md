### 配置表
#### common
配置|默认值|说明或用途
---|---|---
program.language|zh|设定程序语言（系统变量）
profiles.prefix|application|多配置文件前缀切换，默认为application
profiles.extension|properties|多配置文件扩展名切换，默认为properties
profiles.active| |多配置文件后缀切换，默认为空
print.alignment|center|打印对齐方式，可取left（-1）、center（0）、right（1）
print.border.flank|true|表格打印是否需要侧边框
print.border.transverse|true|表格打印是否需要行与行之间的分割线
print.border.vertical|true|表格打印是否需要列与列之间的分割线
print.cover|true|HBase行输出是否启用覆盖模式
print.explode|true|是否将含有换行符的文本换行显示
print.format|default|数据打印方式，default未表格输出
print.json.pretty|false|是否美化json输出
print.length|0|打印行数，0表示全部打印
print.linefeed|0|强制拆分长文本，0表示不拆分
print.padding|true|是否填充空白符以对齐
print.pageSize|20|每页打印行数
print.null2empty|false|是否将null值转为空字符串
print.render|0;32|打印颜色渲染
print.truncate|false|是否将长文本缩略显示
print.truncate.length|17|缩略为17个字符和...
