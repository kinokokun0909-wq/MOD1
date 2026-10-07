# test_entity 戦艦モデルの導入

このブランチは、ダウンロード用ZIPと説明だけを追加しています。

1. test-entity-battleship.zip を開き、GitHubのRawまたはダウンロードボタンで保存します。
2. ZIPを右クリックして「すべて展開」します。
3. ローカルの既存TestEntityRenderer.javaをバックアップします。
4. 展開先のsrcフォルダを、C:\Users\kinok\IdeaProjects\my mod の中へ重ねてコピーします。
5. TestEntityRenderer.javaを置き換え、IntelliJでrunClientを起動します。
6. 開けた海の水面付近で /summon mod1:test_entity ~ ~ ~ {NoAI:1b,NoGravity:1b} を実行します。

ZIPに含むJavaファイルはTestEntityRenderer.javaとclient/TestEntityShipModel.javaです。
モデルJSONはsrc/main/resources/assets/mod1/models/test_entity_warship.geometry.jsonに入ります。
全長180ブロックの見た目を既存のtest_entityへ割り当てます。
移動、当たり判定、乗船、射撃は追加していません。
クラウドの通信制限によりMinecraftでのコンパイル・表示は未検証です。
