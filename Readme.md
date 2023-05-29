# 基于SQLite文件数据库的简单词典

## SQLite文件开放词典

SQLite是基于文件的数据库，数据类型灵活，支持索引，有众多图形化管理工具可以查看和编辑数据，作为开放词典的载体很合适。

开放词典结构：
```SQL
create table IF NOT EXISTS dict (id INTEGER PRIMARY KEY AUTOINCREMENT, word text, content text, word1 text, word2 text)
create table IF NOT EXISTS info (id INTEGER PRIMARY KEY AUTOINCREMENT, word varchar(200), content text, word1 text)
```

必要的索引：
```SQL
create index IF NOT EXISTS IDX_Dict on dict (word)
```
其他字段索引可按需增加。

1. dict 表为词典主表，word为索引词, word1和word2为辅助索引词，content为义项正文；
2. info 表为词典元数据表，word为元数据项目，content为内容，word1用作备注说明。

词典文件扩展名都用.db   

SQLite数据库文件有许多图形化编辑工具，例如DBeaver就是一个很好的工具，开源免费，界面也容易学习。   

## 单词表
单词表文件为utf-8无BOM文本文件，扩展名.txt   
第一行#号开头，后面文字用作标题显示；
其余各行每行一个单词。   


## 词典在手机上的位置
词典位于
/storage/emulated/0/Android/data/com.uniuwo.simpledict/files   

首次启动App后将在files中建立三个文件夹：   
1. simple : 用于索引的词典文件
2. detail : 用于详解的词典文件
3. wordlist : 用于单词表文件

然后连接电脑，将词典文件分别复制到相应文件夹。
再次启动App就可以搜索词典了。   

