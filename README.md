# RAT: A Refactoring-Aware Tool for Tracking Code History

# Table of Contents

- [General Info](#general-info)
- [Refactoring Type](#refactoring-type)
- [How to run](#how-to-run)
- [Evaluation](#evaluation)
- [Demonstration of Usage](#demonstration-of-usage)
- [How to cite this paper](#how-to-cite-this-paper)




## General Info

This is the implementation of RAT tool, which is in paper: "RAT: A Refactoring-Aware Tool for Tracking Code History" submitted to ICSE 2024 Demonstration

Related work has been published in ICSE 2023: "RAT: A Refactoring-Aware Traceability Model for Bug Localization."

Video of this work: https://www.youtube.com/watch?v=VI_xwUaIPp4&ab_channel=flwx

Funny story: The name "RAT" came from my Chinese zodiac, and my necklace is a little rat holding an apple, means safe and healthy.

<img width="653" alt="image" src="https://github.com/feifeiniu-se/RAT_Demo/assets/20552618/325a3861-a520-4f53-8977-eaff233cc5ff">


## Refactoring Type

Here we list the refactoring types RAT used for reconstruct code history:

| Refactory Type   | Code Change Type  | Refactoring Type | Code Change Type |
| ---------------- | ----------------- | ---------------- | ---------------- |
| Rename Package  | rename             | Merge Parameter  | rename           |
| Move Package    | rename             | Split Parameter  | rename           |
| Split Package   | derive             | Add Parameter    | rename           |
| Merge Package   | derive             | Remove Parameter | rename           |
| Rename Class    | rename             | Reorder Parameter| rename           |
| Move Class      | rename             | Change Parameter Type | rename      |
| Move and Rename Class| rename        | Rename Attribute | rename           |
| Merge Class     | derive             | Move Attribute   | rename           |
| Extract Class   | derive             | Move and Rename Attribute| rename   |
| Extract Superclass| derive           | Merge Attribute  | derive           |
| Extract Subclass | derive            | Split Attribute  | derive           |
| Extract Interface| derive            | Extract Attribute| derive           |
| Rename Method   | rename             | Inline Attribute | derive           |
| Move Method     | rename             | Pull Up Attribute| derive           |
| Move and Rename Method| rename       | Push Down Attribute| derive         |
| Extract and Move Method | derive     | Change Attribute Type| rename       |
| Extract Method  | derive             | Encapsulate Attribute|
| Inline Method   | derive             | Parameterize Attribute|
| Pull Up Method  | derive             | Replace Attribute with Variable|
| Move and Inline Method| derive       | Replace Attribute (with Attribute)|
| Change Return Type| rename           |


## How to run

Requirements: JDK>=17

Download the "xxx-jar-with-dependency.jar" from: https://github.com/feifeiniu-se/RAT_Demo/releases/tag/release-v1.0.0

RAT is command line tool so far, it supports three types of commands:

```
> -a <git-repo-folder> -s <sqlite-file-path> # detects all code history

> -bc <git-repo-folder> <start-commit-sha1> <end-commit-sha1> -s <sqlite-file-path> # detects code history between start commit and end commit

> -c <git-repo-folder> <commit-sha1> -s <sqlite-file-path> #detects code history between last commit and this commit
```
"&lt; git-repo-folder &gt;" defines the path of the local repository, "&lt; sqlite-file-path &gt;" indicates the path for saving the SQLite database.

Tip: SQLite is a lightweight database, which requires no server, users can read the data with python, or other interfaces.

For future we may support IDEs, Chrome extension...



Example for how to use in command ：

```
> java -jar TraceabilityModel-1.0-SNAPSHOT-jar-with-dependencies.jar -bc C:\dataset\maven 698119ea0be74b3733fdcf49997a6bf46f160950 a15c47e27076ebfe87f4df881fe9173a7887ae50 -s D:\\tool\\database\\maven.sqlite3

> java -jar TraceabilityModel-1.0-SNAPSHOT-jar-with-dependencies.jar -bc C:\dataset\jitfine\ant-ivy 929363e121230286922d1ec3aaee21b2defba5b6 dbdf9d6c0534733bf98e14de914a337e133f0123 -s D:\\tool\\database\\ant-ivy.sqlite3

> java -jar TraceabilityModel-1.0-SNAPSHOT-jar-with-dependencies.jar -a C:\dataset\maven -s D:\\tool\\database\\maven.sqlite3

> java -jar TraceabilityModel-1.0-SNAPSHOT-jar-with-dependencies.jar -c C:\dataset\jitfine\archiva 8e757bd2a0faec0732b512ea1d4df1e082aea6ff -s D:\\tool\\database\\archiva.sqlite3
```

You can replace the path of git repository, start hash code, end hash code and the path of sqlite file.

The following are the github URLs in the example：

https://github.com/apache/ant-ivy

https://github.com/apache/maven

https://github.com/apache/archiva

## Evaluation
The evaluation of this tool is based on our previous work on ICSE2023.
We evaluate our tool on three bug localization techniques: SimiScore, TraceScore and BugCache.


## Demonstration of Usage
<img width="326" alt="image" src="https://github.com/feifeiniu-se/RAT_Demo/assets/20552618/bf11d059-7351-4c71-950c-31c8a4b54181">

For developers: This tool gives us hints that the current version control system is unaware of refactoring, which shows that code refactoring breaks code history. The RAT model can be a new mechanism for version control systems for tracking code history.

For researchers: Researches rely on code history but ignoring code refactoring may have biase. Researchers should be careful. Our tool transfers restore the code repository, researchers can use our result to search for complete code history.

## How to cite this paper
To be known...
