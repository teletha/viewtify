# Viewtify ウィジェット / ヘルパー早見表

すべて `viewtify.ui.*` パッケージ。各ウィジェットは `UserInterface<Self, W>` を実装し、下記のヘルパー機能を多重的に提供する（链式 API）。正確なシグネチャは Viewtify のソース `src/main/java/viewtify/ui/` を読むこと（解決方法は SKILL.md の「API を推測しない」を参照）。

## ウィジェット一覧

| 型 | JavaFX 対応 | 用途・備考 |
|---|---|---|
| `UIButton` | Button | `contribute(Command)` でショートカットコマンド接続、`fire()` で再現 |
| `UIToggleButton` | ToggleButton | |
| `UISegmentedButton` | — | 分割ボタン |
| `UILabel` | Label | `icon(Ikon)`（ikonli）/ `icon("path.png")` |
| `UIText` | TextField | `UIText<V>` 型安全入力。`masking(true)` パスワード、`clearable()`、`prefix(...)` |
| `UITextArea` | TextArea | |
| `UICheckBox` | CheckBox | `value()/is(true)` |
| `UICheckSwitch` | — | トグルスイッチ |
| `UIRadioButton` | RadioButton | |
| `UIChoiceBox` / `UIComboBox` | ChoiceBox/ComboBox | `items(...)`, `value(...)`, `render(...)` |
| `UIComboCheckBox` | — | 複数選択コンボ |
| `UISpinner` | Spinner | `UISpinner<T>` 型付き |
| `UISlider` | Slider | |
| `UIProgressBar` | ProgressBar | `MonitorableTask` 進捗接続可 |
| `UIColorPicker` / `UIFontPicker` | ColorPicker/— | |
| `UIDatePicker` | DatePicker | |
| `UITableView` | TableView | `UITableView<RowV>`。カラムは `UITableColumn<RowV, CellV>` フィールド |
| `UITreeTableView` | TreeTableView | `UITreeTableColumn` |
| `UITreeItem` / `UIMenuItem` / `UICheckMenuItem` | 対応控件 | |
| `UIListView` | ListView | `render(...)` |
| `UITileView` / `UIFlowView` / `UIGridView` | — | レイアウト系コレクションビュー |
| `UIHBox` / `UIVBox` / `UIStackPane` / `UIPane` / `UISplitPane` / `UIScrollPane` / `UIToolBar` / `UITabPane`+`UITab` / `UISelectPane` / `UISlidePane` | 対応 layout/控件 | DSL の `hbox/vbox/sbox` 等はこれらプロバイダ定数 |
| `UIImage` | ImageView | |
| `UILineChart` / `UIPieChart` / `EnhancedCanvas` | Chart/Canvas | チャート系 |
| `UIContextMenu` | ContextMenu | `menu("...").action(...)`, `separator()`, `disableWhen(...)` |
| `UIComboCheckBox` | — | |

## ヘルパーインターフェース（全ウィジェット共通の混合機能）

### ValueHelper\<Self, V\> — 値
`value()` / `value(V)` / `valueOr(default)` / `valueAs(Class)` / `get()` / `is(v)` / `isNot(v)` / `is(Predicate)` / `observe()`→Signal / `observing()` / `isChanged()` / `isNull()` / `isNotNull()` / `initialize(v|Variable|ObservableValue|Signal)` / `initializeLazy(Signal)` / `sync(other)` 各種（双方向バインド。`synchronizer` 関数・`unsynchronizer` Disposable 版あり）/ `syncFrom(...)` / `historicalValue(Variable[, save])` / `historical(save[, Edito])`（Undo対応）/ `valueProperty()`

### CollectableHelper\<E\> — アイテム管理（table/list/combo 系）
`items(E...|List|Iterable|Signal|BaseStream|int start,int end,IntFunction)` / `itemsWhen(reloadSignal, setter)` / `initialize(items)` / `historicalItems(save)` / `addItemAt(index,item)` / `addItemAtFirst/Last(item)`（+`IfAbsent` 版）/ `setItemAt` / `removeItem(s)` / `removeItemAt` / `removeItemAtFirst/Last` / `removeItemAll` / `replaceItemAt(target,replacer)` / `swap` / `sort(Comparator[,Predicate])` / `sortByInt/Long/Double` / `take(filterSignal)` / `skip` / `has(item)` / `first()/last()/firstArtifact()/lastArtifact()` →Variable / `hasNothing()/hasSomething()` →Signal / `isFiltering()` / `observeItems()/observingItems()`（+Artifacts 版）/ `reapply()`

### SelectableHelper\<E\> — 選択
`selected()` →Signal\<E\> / `selectedItem()` →Variable / `selectedItems()` / `select(item)` / `selectAt/All/First/Last/Next/Previous` / `toggleAt` / `unselect(...)` / `isSelected()/isNotSelected()/isSelectedAt(i)` / `hasSelection()/hasNoSelection()` / `mode(SelectionMode)` / `whenSelected(consumer)`

### UserActionHelper\<Self\> — ユーザー操作
`when(User<E>, action)` / `when(User)` →Signal / `keybind(Key, action)` / `action(...)` / `fire(Event)` / `static of(Node|Scene)`

### StyleHelper\<Self, W\> — スタイルクラス
`style(String...|Style...)`（+boolean add 版）/ `styleOnly(Style|Variable|Signal|ObservableValue)` / `styleWhile(timingSignal, ...)` / `unstyle(...)` / `unstyleAll()` / `hasStyle(name)`

### VisibleHelper\<Self\> — 表示
`visible(b)/invisible(b)/show(b)/managed(b)` / `visibleWhen(Signal|Variable|ObservableValue, ...)` / `invisibleWhen(...)` / `visibleDuring(t,unit)/invisibleDuring` / `visibleBriefly()/invisibleBriefly()` / `isVisible()/isInvisible()/isManaged()` / `opacity(double)`

### DisableHelper\<Self\> — 活性
`disable(b)/enable(b)/disableNow()/enableNow()` / `disableWhen(Signal...)/enableWhen(...)/disableWhenAny/enableWhenAny` / `disableDuring/enableDuring(t,unit)` / `disableBriefly()/enableBriefly()` / `isDisable()/isEnable()` / `showLoader()/hideLoader()`（読込中スピナー）

### VerifyHelper\<Self\> — 検証（保存ボタンの自動制御）
`verify(description, Predicate)` / `verifyBy(verifier)` / `verifyWhen(timings...)` / `verifyWhenChanging(valueHelpers...)` / `isValid()/isInvalid()` →Signal / `valid()/invalid(msg)`

### TooltipHelper\<Self, W\>
`tooltip(text|Variable)` / `untooltip()` / `popup(arrow, contents|builder)`（controlsfx PopOver）

### ContextMenuHelper\<Self\>
`context(id?, builderConsumer)` / `showContext()/hideContext()/toggleContext()` / `removeContext()` / `behaveLikeButton()`

### AnimateHelper\<Self, W\>
`animate(AnimePattern[, finisher])` / `animateLater(ms, ...)` / `translateX/Y/Z` / `scale(X|Y|Z|)`

### LabelHelper（text 系控件）
`text(Object|Variable|Node|providers...)` / `color(...)` / `font(name|size|Font|weight,...)` / `icon(Ikon|path|path,w,h)`

### CollectableItemRenderingHelper / CollectableValuedItemRenderingHelper
`render(Function<E,String>)` / `render(BiConsumer<UILabel,E>)` / `renderAsCheckBox(...)`

### DecorationHelper / AssociativeHelper / PropertyAccessHelper / ContainerHelper / BlockHelper / AlignmentHelper
`decorateBy(Icon[,msg])/undecorate()` / `dispose(key...)`（連想リソース解放） / `PropertyAccessHelper.Type` で JavaFX property 直接アクセス / `content(provider[,finisher][,SwapAnime])`（中身差し替え） / `height/width` / `alignment(Pos)` `orientation` `horizontalSpace/verticalSpace`

## View / UserInterface 共通

`View`: `declareUI()`, `initialize()`, `title()`, `name()`, `id()`, `icon()`, `ui()`, `stage()`, `screen()`, `centerize(ScreenSelector)`, `show()`, `blink()`, `visible(b)`, `en(text, args...)`, `findRootView()`, `findAncestorView(type|predicate)`, `findUI(type)` →Signal, `vandalize()`

`UserInterface`: `ui()`（実 Node）, `view()`, `parent()`, `stage()`, `focus()`, `focusable(b)`, `whenFocus/whenUnfocus(...)`, `keybind(...)`, `verifier()`, `restore(property, default)`（ウィンドウ状態復元）

## その他の重要クラス

| クラス | 用途 |
|---|---|
| `viewtify.Viewtify` | 起動・スレッド・observe・テーマ管理（SKILL.md 参照） |
| `viewtify.ViewtyDialog` | ダイアログビルダー + `DialogView<V>` 基底 |
| `viewtify.edit.Edito` | Undo/Redo。`Edito.Root` シングルトン、`Snapshot` |
| `viewtify.preference.Preferences` | `Preference<T>`/`Int/Long/Double/ComparablePreference` |
| `viewtify.keys.Key` | `Key.Enter`, `Key.Delete`, `Key.Ctrl`, `marionette.Key` 混同注意 |
| `viewtify.keys.Command`/`ShortcutManager`/`KeyBindingSettingView` | ショートカット体系 |
| `viewtify.ui.dock.DockSystem`/`Dock`/`TypedDock` | ドッキング |
| `viewtify.ui.toast.Toast`/`Toastable` | 通知 |
| `viewtify.ui.anime.Anime`/`Interpolators`/`AnimePattern`/`Show/Hide/Swap/Slide` | アニメ |
| `viewtify.ui.calendar.*` | カレンダー UI（Day/Week/Month/YearView, `TimeEvent`, `CalendarMarker`） |
| `viewtify.ui.query.QueryView`/`CompoundQuery` | 検索クエリビルダー |
| `viewtify.update.*` | 自動更新（`Update`, `Updater`, `Blueprint`） |
| `viewtify.task.MonitorableTask`/`Monitor` | 進捗監視タスク |
| `viewtify.util.Icon`/`FXUtils`/`Range`/`Corner`/`ScreenSelector`/`TextNotation`/`GuardedOperation` | 雑多ユーティリティ |
| `viewtify.Theme`/`ThemeType`/`Modena`/`ActivationPolicy` | テーマ・起動ポリシー |

## kiss（sinobu）反応型 primitives — よく使う操作

```java
Variable.of(v) / Variable.empty() / Variable.Es.empty()      // 作成
v.get() v.set(x) v.update(f) v.observe() →Signal v.observing() // 状態
v.map(f) v.or(V|Supplier) v.flip(V|Runnable) v.to(consumer)   // 変換・分岐
v.exact()                                                     // ブロック取得

Signal: map flatMap take filter merge startWith debounce(delay,unit)
        on(executor) to(consumer) first last scan skip distinct
        buffer(groupMs) delay ...                             // 合成・時間処理
Disposable: dispose() disposeIf(...) kiss.Disposable.empty()
I.schedule(delay, period, unit) → Signal<Long>                // タイマー
I.env(key, default)                                           // 環境設定
I.make(Class) / I.translate(...) / I.error / I.quiet          // DI・翻訳・エラー
```
