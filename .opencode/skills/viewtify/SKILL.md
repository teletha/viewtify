---
name: Viewtify
description: Viewtify(teletha/viewtify)ライブラリでJavaFXアプリを開発する時使用。View/ViewDSLによる宣言的UI、ウィジェット、Stylistスタイル、ダイアログ、Toast、アニメーション、Preferences、Dock、kiss反応型パターンの実装ガイド。
---

# Viewtify

Viewtify は JavaFX を**宣言的かつ反応的**に扱うためのライブラリ。FXML を排除し、純粋な Java で UI を構築・リファクタリング・型安全にすることを目指す。

- 座標: `io.github.teletha:viewtify:2.35.1`（JitPack 経由。sinobu/stylist/psychopath/controlsfx/ikonli/JavaFX は推移依存で自動取得）
- 必須: Java 24+
- リポジトリ: https://github.com/teletha/viewtify
- API 一覧: https://teletha.github.io/viewtify/api/main.html

## API を推測しない — 調べる順序

このスキルは特定マシンの絶対パスに依存しない。参照先は次の順で解決する。

1. **ライブラリソースを直接読む**（実装と javadoc が唯一の真実）。次の順で実体を探す:
   - 作業中のプロジェクト自体が Viewtify 本体なら `src/main/java/viewtify/`（`Viewtify.java` の有無で判定）
   - 依存のソース JAR:
     - Maven: `~/.m2/repository/io/github/teletha/viewtify/<version>/viewtify-<version>-sources.jar`
     - Gradle: `~/.gradle/caches/modules-2/files-2.1/io.github.teletha/viewtify/<version>/`
     - 見つからなければ `mvn dependency:sources` か IDE の「Download Sources」
   - GitHub の該当タグ: `https://github.com/teletha/viewtify/tree/<version>/src/main/java/viewtify/`
   - `<version>` は実際に使用している依存バージョンに合わせる
2. **実アプリを grep する**（最も信頼できる使用例）:
   - 公式の公開サンプル: https://github.com/teletha/cointoss — リアルタイムテーブル/チャート/Toast/Tab/DockSystem の実装
   - 手元の Viewtify 採用プロジェクト。`git grep` などで `extends View` / `extends ViewDSL` を探す（特定のパスを仮定しない）
3. `references/widgets.md`（このスキルからの相対パス。ウィジェットとヘルパーメソッドの早見表）
4. 公式サイトの該当クラスページ

## セットアップ

```xml
<!-- Maven -->
<repository><id>jitpack.io</id><url>https://jitpack.io</url></repository>
<dependency>
    <groupId>io.github.teletha</groupId>
    <artifactId>viewtify</artifactId>
    <version>2.35.1</version>
</dependency>
```

```gradle
// Gradle
repositories { maven { url "https://jitpack.io" } }
dependencies { implementation 'io.github.teletha:viewtify:2.35.1' }
```

## アプリの起動

`main` から `Viewtify.application()` ビルダーで起動する。Application クラスの継承は不要。

```java
public static void main(String[] args) {
    Viewtify.application()
            .icon("app.png")                       // アプリアイコン
            .title("アプリ名")
            .version("1.0.0")
            .update(archivePath)                    // 自動更新（任意）
            .onOpening(LoginView.class)             // スプラッシュ/ログインを先に表示（任意）
            .onClosing(() -> true)                  // false を返すと閉じるのを阻止
            .use(Theme.Dark)                        // Theme/ThemeType/Font/StageStyle（任意）
            .activate(MainView.class);              // メインViewで起動
}
```

（出典: 実アプリの起動クラス）

## View の構造 — 最重要パターン

`viewtify.ui.View` を継承し、**フィールド宣言だけでウィジェットが自動生成される**。

```java
public class SearchView extends View {

    // フィールド名がそのままウィジェットのIDになる。privateでOK、new不要。
    TextInput search;                          // UIText<String>等のサブクラスでもOK
    UILabel counter;
    UICheckBox showAll;
    UITableView<Client> table;                 // 行型をジェネリクスで指定
    UITableColumn<Client, Integer> columnId;   // <行型, セル型>
    DetailPanel panel;                         // View型なら入れ子Viewとして接続

    /** パターンA: declareUI をオーバーライドして無名 ViewDSL を返す */
    @Override
    protected ViewDSL declareUI() {
        return new ViewDSL() {
            {
                $(vbox, style.root, () -> {
                    $(hbox, () -> {
                        $(search);
                        $(counter);
                        $(showAll);
                    });
                    $(table, style.table, () -> {
                        $(columnId);
                    });
                    $(panel);
                });
            }
        };
    }

    /** UIツリー確定後に呼ばれる。値・イベント・バインディングの初期化専門 */
    @Override
    protected void initialize() {
        search.placeholder("名前や番号で検索");
        columnId.text("番号").model(c -> c.id);
        table.items(getClients());
    }
}
```

- `declareUI()` をオーバーライドしない場合、**`ViewDSL` を継承した非 static の内部クラス**（慣例名 `view`/`UI`）を自動検出する:

```java
class view extends ViewDSL {
    {
        $(sbox, () -> { $(DockSystem.UI); $(editMode); });
    }
}
```

- フィールドの型が `UserInterface` 系（UIウィジェット）または `View` サブクラスの場合のみ自動インスタンス化される。`String` 等は通常フィールド。配列も展開されて生成される。
- ツリーから拾う: `findUI(UIButton.class)`（全View横断）、`findAncestorView(MainView.class)`、`findRootView()`、`view()`（所属View）、`stage()`（`Variable<Stage>`）。

## ViewDSL で使えるもの

- コンテナ定数: `hbox`（横）`vbox`（縦）`sbox`(重ね) `hsep` `vsep`（区切り線）
- `$((UserInterfaceProvider) provider, Style... style, () -> children...)` — ツリー宣言。末尾ラムダが子
- `label("テキスト", style...)` / `text("テキスト")` — 簡易ラベル
- `form("ラベル", control...)` / `form("ラベル", () -> {...})` — 2 カラムフォーム行
- `title("ラベル", "詳細")` — フォーム見出し
- 動的な子: `$(Variable<? extends UserInterfaceProvider> provider)`（値が変わると差し替え）、`$(container, ObservableList<providers>)`
- 生 JavaFX ノード: `$(ui(new javafx...Node()))`

## スタイル（Stylist）— FXML/CSS ファイルなしで装飾

```java
interface style extends StyleDSL {          // 慣例名 "style"。ViewtyStyle を継承可
    Style root = () -> {
        display.width(1100, px).height(700, px);
        margin.all(8, px);
        font.size(13, px).color($.hsl(20, 0, 40));   // $ は StyleDSL への別名
        $.hover(() -> {                               // 疑似クラス
            font.color($.hsl(20, 0, 10));
            cursor.pointer();
        });
    };
    Style id = () -> display.width(60, px);
}
```

- 適用方法: DSL 内で `$(widget, style.root)`、命令的に `widget.style(style.id)`、条件付き `styleWhile(signal, ...)`、単独 `styleOnly(...)`
- 組み込み CSS: `viewtify.style.ViewtyStyle`（`useAntiAliasedFont()` 等）、`viewtify.style.FormStyles`（`Column1`〜`Column12` のグリッド幅、フォーム共通スタイル）
- テーマ: `Theme`（Light/Dark）と `ThemeType`（Modena/Caspian 等）。起動時 `use(...)`、実行時切替 `Viewtify.manage(Theme.Dark)`
- デザインカラーは `stylist.design.DesignScheme` を `design(MyScheme.class)` で差し替え可能。変更した CSS は自動リロードされる

## ウィジェット基本操作

よく使う型（詳細・全メソッドは `references/widgets.md`）:

```java
UIButton button = ...;  button.text("保存").icon(FontAwesomeSolid.SAVE).when(User.LeftClick, e -> save());
UIText<String> text  = ...;  text.value();            // 現在値
                                text.value("初期値");   // 設定
                                text.observe();         // Signal<String> 変更ストリーム
                                text.placeholder("検索").clearable().prefix(FontAwesomeSolid.USER);
UICheckBox check = ...;  check.text("領収書を表示").value(true);  check.is(true);
UITableView<Client> table = ...;
table.mode(SelectionMode.MULTIPLE);
table.addItemAtLast(created);   table.replaceItemAt(old, created);   table.items(list);
table.selected();               // Signal<Client>
table.selectedItems();          // List<Client>
table.keybind(Key.Delete, () -> delete(table.selectedItems()));
table.context(pop -> {                       // コンテキストメニュー
    pop.menu("全て選択").action(() -> table.selectAll());
    pop.separator();
    pop.menu("削除").disableWhen(table.selected().map(s -> s == null)).action(() -> delete());
});
UITableColumn<Client, String> col = ...;
col.text("名前").render(c -> c.name());     // 表示のみ
col.text("area").model(c -> c.area);        // 双方向バインド（編集可）
col.operatable(false);                       // 読取専用カラム
```

- 数値入力は `UIText<Integer>` 等ジェネリクスで型安全に（`valueAs`, `converter` あり）。独自型は `UIText<String>` を継承して作る
- ラベル装飾: `UILabel.icon(Ikon)` は ikonli（`FontAwesomeSolid.*`）が使える

## イベント（User アクション）

```java
widget.when(User.LeftClick, e -> ...);      // クリック系: LeftClick/RightClick/DoubleClick/...
widget.when(User.Action, e -> ...);         // 確定アクション（ボタン押下・チェック切替等）
widget.when(User.Input).to(signal -> ...);  // Signal版（merge で合成しやすい）
widget.when(User.KeyType)/KeyPress/KeyRelease/KeyInputed
widget.when(User.Gesture).to(g -> ...);     // ドラッグ手势（"LURDL" 等）
widget.keybind(Key.Enter, () -> submit());  // ショートカット（Key.Ctrl+Alt+... も可）
widget.action(() -> ...);                   // User.Action のショートハンド
widget.whenFocus(...); widget.whenUnfocus(...); widget.focus();
```

## 反応型（sinobu の kiss.Variable / kiss.Signal）

Viewtify の返り値の多くは `Variable<T>` / `Signal<T>`。`.to()` が購読（非同期・Dispose管理）、`.exact()` がブロッキング取得。

```java
// JavaFX Property → Signal
Viewtify.observe(widget.textProperty())    // 変更時のみ
Viewtify.observing(widget.textProperty())  // 初回値も含めて流す（初期化兼バインドに便利）

// 検索フィルタの典型形（実アプリの検索Viewより）
Signal<Predicate<Client>> filter = search.when(User.Input)
        .merge(showAll.when(User.Action))
        .debounce(150, TimeUnit.MILLISECONDS)
        .on(Viewtify.UIThread)
        .map(e -> buildCondition());

// 双方向バインド
myField.text().sync(uiWidget);              // ValueHelper.sync 系
uiWidget.value(myVariable);                 // Variable → ウィジェット初期値 + 一方通行
Viewtify.propertyForUI(variable);           // Variable → javafx Property

// 可視性/有効性の宣言制御
widget.visibleWhen(flag);                   // Variable<Boolean>/Signal<Boolean> 可
widget.disableWhen(table.selected().map(Objects::isNull));
```

- スレッド横断: `Viewtify.inUI(() -> ...)` / `Viewtify.inWorker(() -> ...)`（仮想スレッドプール）。Signal 上は `.on(Viewtify.UIThread)`
- アプリ終了時まで Dispose を管理: `Viewtify.Terminator.add(disposable)`

## ダイアログ

```java
// アラート/確認（非同期で受取る）
Viewtify.dialog()
        .title("削除").button("削除する", "取りやめる")
        .show(AlertType.CONFIRMATION, "本当に削除しますか？")
        .to(b -> { if (b == ButtonType.OK) delete(); });

// カスタムViewのダイアログ。DialogView<V> を継承したViewが結果値Vを返す
Viewtify.dialog().title("霊名を編集").button("保存する", "キャンセル")
        .show(DeceasedInput.class, view -> view.set(target))
        .to(created -> table.replaceItemAt(target, created));

// 入力・ファイル・ディレクトリ・ウィザード・印刷プレビュー
String input = Viewtify.dialog().showInput("名前を入力", "既定値").or("");
File file    = Viewtify.dialog().showFile(dir, filter);          // psychopath.File
Directory d  = Viewtify.dialog().showDirectory();
Viewtify.dialog().size(500, 350).showWizard(Step1.class, Step2.class, Step3.class);
Viewtify.dialog().showPrintPreview(images).to(info -> print(info));

// 装飾・挙動: .modal(Modality.*) .style(StageStyle.*) .fadable() .blurable()
//            .location(ownerNode, ArrowLocation.*) .translatable()（自動翻訳）
//            .disableButtons(true) .disableCloseButton(true)
// ポップオーバー: Viewtify.dialog().showPopup(() -> myView)
```

`DialogView<V>` の実装側:

```java
public class DeceasedInput extends ViewtyDialog.DialogView<Deceased> {
    // public フィールド value が結果入れ物。initialize で各UI→value の反映を行う。
    // verifier()/VerifyHelper で保存ボタン自動 disabled 化、isInvalid() も available。
}
```

## Toast（通知）

```java
Toast.show("保存しました");                                   // 一過性メッセージ
Toast.show(I.translate("更新があります [更新](0) [後にする](1)"), 0);  // インラインアクション

// 進捗付きトースト（長時間処理の定番。実アプリの進捗表示より）
Toast<ZonedDateTime> t = Toast.<ZonedDateTime> title(en("Building ticker."))
        .totalProgress(31)
        .whenProgressed((toast, date) -> toast.incrementProgress())
        .whenCompleted(() -> chart.layoutForcely())
        .whenCanceled(() -> abort());
t.show(backgroundSignal);   // Signal<T> を購読して進捗反映
```

## アニメーション（Anime）

```java
Anime.define()
        .duration(0.5)
        .interpolator(Interpolators.EaseIn.Elastic)
        .opacity(node, 0.0)
        .move(node, 100, 0)
        .effect(stage.opacityProperty(), 0)   // WritableValue 直接指定でもOK
        .run(() -> stage.close());

Anime.define().effect(...).runInfinitely();   // ループ
widget.animate(AnimePattern.Show.Fade);       // Show/Hide/Swap/Slide プリセット
```

## 環境設定（Preferences）

```java
public class YamatoSetting extends Preferences {
    public final Preference<Boolean> warnUnsaved = initialize(true);
    public final DoublePreference shutdownTime   = initialize(0d);
}

// 読み書き（ObservableValue なので observe/observing で反応できる）
if (Preferences.of(YamatoSetting.class).warnUnsaved.is(true)) { ... }
```

- 保存先は `Viewtify.UserPreference`（アプリごとに `.アプリ名` ディレクトリ）。
- 設定UI組み込みView: `AppearanceSettingView`, `KeyBindingSettingView`, `ToastSettingView`, `UpdateSettingView`, `PreferenceView` をそのままダイアログに出せる。

## Undo/Redo（Edito）

```java
if (Edito.Root.isEditing()) { ... }   // 未保存編集あり？
Edito.Root.save();  Edito.Root.revert();  Edito.Root.countEditing();
// ウィジェット側は historicalValue(v -> saveTo(v)) / historicalItems(...) で履歴登録
```

## Dock（マルチウィンドウ配置）

```java
// initialize で
DockSystem.initialize();
// declareUI ルートに
$(sbox, () -> { $(DockSystem.UI); $(overlayView); });
// ドック化はアプリ側で Dock/TypedDock モデルを実装する
```

## 翻訳・その他

- `en("Hello {0}", name)`（View メソッド）で英語→現在言語の自動翻訳 Variable。ダイアログは `.translatable()`
- ヘッドレス実行（CI/テスト）: `main` 直後に `Viewtify.inHeadless()` または env `javafx.headless=true`
- `Icon` / `FXUtils` / `Range` / `ShortcutManager` / `MonitorableTask` などユーティリティは API 一覧から検索

## 注意事項

1. FXML と JavaFX StyleClass 文字列に頼らない — 装飾は `StyleDSL`、状態反映は `styleWhile`/`visibleWhen` 等。
2. ウィジェットフィールドは declareUI 実行前に生成済み。`initialize()` で値とイベントを接続する。
3. `View` の `id()` はフィールド名由来。CSS 依存で壊しやすいので styleOnly を優先。
4. `Variable.exact()` は UI スレッドで呼ぶとロックリスク。イベント内は基本 `.to(...)` で非同期に受取る。
5. View は AutoDisposable。View 消滅時に listener が散った場合は `supportAutomaticDispose()` を確認し、アプリ寿命のリソースは `Viewtify.Terminator` へ。
