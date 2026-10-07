# MP5 — Minecraft Java item model

参考画像の伸縮ストック型MP5をもとに作った、Minecraft Java版用のオリジナルモデルです。165個の立体部品、9つの編集グループ、256×256のテクスチャで構成しています。

## 形式

- `mp5.json`: Minecraft Java標準のアイテムモデルJSON。
- `mp5.png`: モデルが参照するテクスチャ。
- `MP5.bbmodel`: Blockbenchの「ファイル → モデルを開く」で開ける編集データ。テクスチャを内蔵しています。
- `MP5.glb`: 一般的な3Dビューアーで開く確認用モデル。Minecraftに直接入れるファイルではありません。
- `MP5.blend`: Blenderの確認用シーン。撮影照明と背景も含みます。
- `preview-mp5.png` / `preview-mp5-profile.png`: 実際のMinecraft用形状とテクスチャから描画した3Dプレビュー。ゲーム内のスクリーンショットではありません。

Minecraft Java 1.20.1 / Forge 47.4.23向けです。Java標準の単軸回転角（0、±22.5、±45度）、標準の座標範囲、UVとテクスチャ参照を使用しています。GeckoLibなどの追加MODは不要です。

手持ち左右、三人称左右、インベントリ、地面、額縁用の表示設定を含みます。射撃、リロード、弾薬の処理は含みません。

## まず見た目だけゲームで確認する

`MP5-preview-resourcepack.zip`を使うと、Javaコードを編集せずにモデルを確認できます。このパックは棒の見た目をMP5に置き換えます。

1. Minecraftを終了します。
2. `MP5-preview-resourcepack.zip`を、ZIPのまま次の場所にコピーします。
   `C:\Users\kinok\IdeaProjects\my mod\run\resourcepacks`
   resourcepacksフォルダがなければ作ってください。
3. IntelliJでrunClientを実行します。
4. Minecraftの「設定 → リソースパック」でMP5パックを有効にします。
5. ワールドで `/give @p minecraft:stick` を入力して、棒を持ちます。
6. インベントリ表示と一人称・三人称での表示を確認してください。

これは見た目を確認するパックです。棒の名前や機能は元のままです。パックを無効にすると棒の見た目も戻ります。

## MODのmp5アイテムとして使用する

`MP5-mod-resources.zip`には、以下の配置でリソースを入れています。

```
src/main/resources/assets/mod1/models/item/mp5.json
src/main/resources/assets/mod1/textures/item/mp5.png
src/main/resources/assets/mod1/lang/ja_jp.json
src/main/resources/assets/mod1/lang/en_us.json
```

モデルとPNGを上記の場所に配置してください。言語ファイルが既にある場合は、既存の内容を残して `"item.mod1.mp5": "MP5"` の項目を追加してください。

既存のExampleMod.javaのITEMS登録と同じ場所に、次のアイテム登録を追加すると、mp5というアイテムに本モデルを割り当てられます。

```java
public static final RegistryObject<Item> MP5 = ITEMS.register(
        "mp5", () -> new Item(new Item.Properties().stacksTo(1)));
```

`ITEMS.register(modEventBus)`は既にあるものを使います。重ねて登録しません。上記は外観確認用の通常アイテムで、銃としての射撃処理は次の作業になります。

登録後は `/give @p mod1:mp5` で取得できます。確認用の棒パックを無効にしてもmp5アイテムの表示には影響しません。

## 検証

全165部品のJSON座標と寸法、回転の軸・角度、全ての面のUV、PNGの参照、表示設定、Blockbenchの部品参照と内蔵テクスチャを確認しました。Blenderでも同じJSON形状とPNGから描画しています。

Minecraftクライアントでの表示はこのクラウド環境では未検証です。射撃機能は実装していません。
