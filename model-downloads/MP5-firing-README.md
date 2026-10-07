# MP5 射撃版（Minecraft 1.20.1 / Forge 47.4.23）

右クリック長押しで連射できる、モデルと射撃コードのセットです。

## 入れ方

1. Minecraftを終了します。
2. GitHubで `MP5-firing.zip` を開き、右上の下向き矢印「Download raw file」で保存します。
3. ZIPを右クリックして「すべて展開」します。
4. 展開した中の `src` フォルダーをコピーします。
5. `C:\Users\kinok\IdeaProjects\my mod` を開き、そこに貼り付けます。既存の `src` と統合してください。
6. 同名のMP5モデル・画像・言語ファイルがあれば置き換えます。他のアイテムの翻訳を独自に追加している場合は、言語JSONのキーを統合してください。
7. IntelliJ IDEAで `runClient` を実行します。
8. チートを使えるワールドで `/give @p mod1:mp5` を実行します。
9. MP5を手に持って敵に照準を合わせ、右クリックを長押しします。

正しい配置の例：

```text
my mod/
  build.gradle
  src/
    main/
      java/com/example/examplemod/
        ModGuns.java
        Mp5Item.java
      resources/
        assets/mod1/models/item/mp5.json
        assets/mod1/textures/item/mp5.png
        assets/mod1/lang/ja_jp.json
        assets/mod1/lang/en_us.json
        data/mod1/damage_type/mp5_bullet.json
        data/minecraft/tags/damage_type/is_projectile.json
        data/minecraft/tags/damage_type/bypasses_cooldown.json
```

`src/src/main` にしないでください。`ExampleMod.java` を編集する必要はありません。
このZIPはリソースパックではありません。`run/resourcepacks` や `run/mods` ではなく、上のプロジェクトフォルダーへコピーします。
以前の棒の見た目をMP5にするリソースパックは、設定の「リソースパック」から外して構いません。今回のMP5は棒とは別のアイテムです。

## 現在の仕様

- 右クリック長押し：毎秒10発（ゲームが20TPSの場合）
- 1発3ダメージ（ハート1.5個、相手の防具などで減少）
- 射程64ブロック
- 狙った方向に即座に命中する方式。手前の敵に当たり、壁で止まります。
- 発射音、煙、弾道の粒子を表示
- 試作版のため弾薬無制限。弾倉・リロード・反動はまだありません。
- 認識されない場合はMinecraftを完全に終了して `runClient` で起動し直してください。`/reload` だけではJavaの変更は反映されません。

## 同梱ファイルについて

`ModGuns.java` が独立した登録イベントで `mod1:mp5` を追加します。
`Mp5Item.java` がサーバー側で射撃と当たり判定を処理します。
`data` のJSONも必ずコピーしてください。弾のダメージ種類と連射用のタグを登録しています。
軍艦や既存エンティティのJavaファイルは同梱していません。

## 検証

2026-10-07、Java 17 / Minecraft 1.20.1 / Forge 47.4.23で `gradlew build` 成功。
実際のGameTestServerで、手前の敵だけに命中・壁による遮蔽・2tick間隔と連射のダメージを検証し、3件すべて成功しました。
クラウドではGUIクライアントを起動していないため、画面や操作の最終確認はローカルの `runClient` で行ってください。
