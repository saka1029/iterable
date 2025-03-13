# CoRoutines
## Same Fringe Problem
https://wiki.c2.com/?SameFringeProblem

さて、これがユーティリティのキラーな例です CoRoutines。
問題の設定は、リチャードガブリエルの1991年の論文
「並列プログラミング言語のデザイン」
(http://www.dreamsongs.com/10ideas.html)
「1977年、ACM SIGARTニュースレターのページで、
同じフリンジがマルチプロセッシングを必要とする最も単純な問題であるか、
CoRoutineは簡単に解決する...
同じフリンジの問題はこれです。2つのバイナリツリーは、左から右に読んでいる葉がまったく同じであれば、フリンジが同じです。
これが私の解決策です。これも疑似Cです。

```
Tree coroutine tree_leaves(Tree tree) {
    if (is_leaf(tree)) yield tree;
    yield tree_leaves(tree->left);
    yield tree_leaves(tree->right);
    yield NULL;
}

int same_fringe(Tree tree1, Tree tree2) {
    Tree tmp1, tmp2;
    while ((tmp1=tree_leaves(tree1))
        && (tmp2=tree_leaves(tree2)))
        if (tmp1 != tmp2) return 0;
    return 1;
}

```

これは以下のような木が等しいかどうかを調べる。
ただし木のノードは`(left right)`、リーフは`値`で表記する。

```
((1 2) 3)
(1 (2 3))
```