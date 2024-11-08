# Changelog

## [2.29.0](https://github.com/teletha/viewtify/compare/v2.28.0...v2.29.0) (2024-11-08)


### Features

* Drop LabelHelper#text(Property) ([1bf5231](https://github.com/teletha/viewtify/commit/1bf5231b9338d72b3d20b55a0d485ae56d7eeeb7))
* drop LabelHelper#text(UILabel) ([4330513](https://github.com/teletha/viewtify/commit/4330513162123e85ba708d64da5d1c1a5476c971))
* LabelHelper#text discard the old sync properly ([12e4e6b](https://github.com/teletha/viewtify/commit/12e4e6b7363ba264a824f0f677b29b4276bfd712))
* revert to altfx ([bf76ab1](https://github.com/teletha/viewtify/commit/bf76ab1a49ae96cb70dbe3db6e9e5a2d484c5b8a))
* support LabelHelper#graphic and #color properly ([13aef6b](https://github.com/teletha/viewtify/commit/13aef6b471b1a1b8c6782857f6f810df459c9c61))
* support reboot on native image ([0ac0dbe](https://github.com/teletha/viewtify/commit/0ac0dbedbb1f82e67a82e794a92ae161af2f2642))
* use Narcissus to bypass module encapsulation ([6a12264](https://github.com/teletha/viewtify/commit/6a122647cb9b8292ddfa62256e91b97b32097a4c))


### Bug Fixes

* remove deprecated api ([deedd0c](https://github.com/teletha/viewtify/commit/deedd0c929df7de4ff4ee8fbb609cfd53e3d836d))
* simplify api ([392e959](https://github.com/teletha/viewtify/commit/392e959ef07831557e5ca30f1a665cd0f034e6cf))
* toast should not active owner window ([3d75041](https://github.com/teletha/viewtify/commit/3d75041fa7d65b9bd9f953ff384e61a3c35dc342))
* update icy manipulator ([980aeb1](https://github.com/teletha/viewtify/commit/980aeb18bd8a0ba162d6c495158441f52a3f3f07))
* update icy manipulator ([3a56b03](https://github.com/teletha/viewtify/commit/3a56b03187a2c2de5f6beb62071077cc80bfc6e8))

## [2.28.0](https://github.com/teletha/viewtify/compare/v2.27.1...v2.28.0) (2024-09-28)


### Features

* support closing tab by middle click ([b8872a9](https://github.com/teletha/viewtify/commit/b8872a9589c9fc260cdf3cf26bcb07f34f5b9f16))


### Bug Fixes

* integrate monitor with toast ([34ae3e0](https://github.com/teletha/viewtify/commit/34ae3e033360b16a5b676b8fd5a46ee3f766446f))

## [2.27.1](https://github.com/teletha/viewtify/compare/v2.27.0...v2.27.1) (2024-09-06)


### Bug Fixes

* registered task is stopped by monitor's completion ([9930f36](https://github.com/teletha/viewtify/commit/9930f364c8b9568f01ccb0149d3f07a08b18f7ab))

## [2.27.0](https://github.com/teletha/viewtify/compare/v2.26.0...v2.27.0) (2024-09-06)


### Features

* brush up Monitor related API ([8eb55b4](https://github.com/teletha/viewtify/commit/8eb55b4b4863959c3929aeb6ba9312b78fdded70))

## [2.26.0](https://github.com/teletha/viewtify/compare/v2.25.0...v2.26.0) (2024-09-05)


### Features

* add Toast#show(View) and #show(ToastMonitor) ([33b7586](https://github.com/teletha/viewtify/commit/33b758622912068d1a7c77f38d97482ff05d4c17))

## [2.25.0](https://github.com/teletha/viewtify/compare/v2.24.1...v2.25.0) (2024-08-21)


### Features

* update sinobu ([465c55b](https://github.com/teletha/viewtify/commit/465c55b5542082f516774dae2778b822288f97a4))

## [2.24.1](https://github.com/teletha/viewtify/compare/v2.24.0...v2.24.1) (2024-07-22)


### Bug Fixes

* avoid ConcurrentModificationException ([12b3510](https://github.com/teletha/viewtify/commit/12b3510a04c8692ca1041247daae0d6d265a347e))
* typo ([68abb71](https://github.com/teletha/viewtify/commit/68abb717cb47b30e7d9232a932e8d1be889b0940))
* ValueHelper#valueOr is broken when null value ([19a0060](https://github.com/teletha/viewtify/commit/19a0060866f5826ad16a4e06ccf165379e3758e4))

## [2.24.0](https://github.com/teletha/viewtify/compare/v2.23.0...v2.24.0) (2024-04-03)


### Features

* add Toastable interface ([5137152](https://github.com/teletha/viewtify/commit/51371527fc4d0a28ab404ce3db1a9a5effde913a))


### Bug Fixes

* reduce error message ([862884e](https://github.com/teletha/viewtify/commit/862884ee0c2affdd4a65230972a7afca829ee18e))

## [2.23.0](https://github.com/teletha/viewtify/compare/v2.22.0...v2.23.0) (2024-03-28)


### Features

* add CollectableHelper#updateUnderlayModel ([c3c6d37](https://github.com/teletha/viewtify/commit/c3c6d37b2277d8e08aab77f84a39fda8c80be39d))
* add LayoutAssistant#registerLayout ([37199ae](https://github.com/teletha/viewtify/commit/37199ae1aa1dda7e7021029968e534e23b0aff60))
* add SelectableHelper#selectAll ([ea3a149](https://github.com/teletha/viewtify/commit/ea3a149aff27c895d3139b84904cefd29c0c8b50))
* add SelectableHelper#snapshotSelectedItems ([6158e9e](https://github.com/teletha/viewtify/commit/6158e9eaa4ed15f6bb328705753939756633f7ec))
* add StyleHelper#unstyleAll ([5db0ade](https://github.com/teletha/viewtify/commit/5db0adedbf72307d868efbf44e40c193d5ff6222))
* built-in window close command ([eb0ec10](https://github.com/teletha/viewtify/commit/eb0ec10ed7964f58dce61fdcc75d5997422db617))
* CollectableHelper drops ui thread safe action ([200056b](https://github.com/teletha/viewtify/commit/200056b5b3b7509fb60fac17230d570124540d88))
* manage windows order ([98c0d08](https://github.com/teletha/viewtify/commit/98c0d081d2e810f66595b26293ab96388a9402d5))
* Preference implements value supplier ([1a1d8fc](https://github.com/teletha/viewtify/commit/1a1d8fcc372583f4331800e01700449dca7f14fe))
* provide default error handler ([23c9e48](https://github.com/teletha/viewtify/commit/23c9e4882717db778361ca11e0d6bdfafb3bec61))
* support mouse wheel event ([e5f572c](https://github.com/teletha/viewtify/commit/e5f572cf056cbf37b1efcc671e4600b0ab307510))
* Viewtify can reorder managed windows ([63c4eb6](https://github.com/teletha/viewtify/commit/63c4eb6a8643d86b150130b2a926d03adf8b541b))


### Bug Fixes

* don't register the same dock duplicately ([ce7f3a9](https://github.com/teletha/viewtify/commit/ce7f3a949dcfad09f48f8f3215d7ac9c7eb81b6c))
* LabelHelper#text is UI thread safe ([0e38953](https://github.com/teletha/viewtify/commit/0e38953398c568dbde222797f07df3f3163d73f4))
* manage key pressing state in global ([0a12b74](https://github.com/teletha/viewtify/commit/0a12b7435a253cb69045a62643b01fa658f6df43))
* reduce memory usage ([70abde8](https://github.com/teletha/viewtify/commit/70abde8abaadbf070bc1faf1b439fc42656bb276))
* save the window info on terminating application ([cabc13d](https://github.com/teletha/viewtify/commit/cabc13dcb34635bae6c166731033437c4926f75d))
* show window of the selected dock ([ce7f3a9](https://github.com/teletha/viewtify/commit/ce7f3a949dcfad09f48f8f3215d7ac9c7eb81b6c))
* view support parameterized variable ([537aa35](https://github.com/teletha/viewtify/commit/537aa35d0bfd5db345bd1879cdd4df7e249bac3f))

## [2.22.0](https://github.com/teletha/viewtify/compare/v2.21.0...v2.22.0) (2024-02-15)


### Features

* AppearanceSetting provides tab closing policy ([adc0e46](https://github.com/teletha/viewtify/commit/adc0e46e9ac611f916cdf54944ffcff19c612cdc))


### Bug Fixes

* discard automatic title selection ([d2b5140](https://github.com/teletha/viewtify/commit/d2b514036ae772b4c45fd50d26f69561f152575b))
* Preference#syncTo emit value immediately ([89e81b7](https://github.com/teletha/viewtify/commit/89e81b74a6e6da664e439dfbfb4817588a622300))
* sub window is trackable smartly on dock system ([95fd248](https://github.com/teletha/viewtify/commit/95fd2488190d1ee29bbe832a2c5c83a30e6c9046))

## [2.21.0](https://github.com/teletha/viewtify/compare/v2.20.0...v2.21.0) (2024-02-13)


### Features

* add ContextMenuHelper#hookContext ([b8783c4](https://github.com/teletha/viewtify/commit/b8783c4c7e1c092c4f4245e7a2135341d6c41af2))
* context menu is rebuildable ([afcf0f3](https://github.com/teletha/viewtify/commit/afcf0f322e47d7666c651026b1e3e1c5df9c4a70))
* context submenu is rebuildable ([aceb266](https://github.com/teletha/viewtify/commit/aceb2665e67992528496f129b3b696c0b3709d15))
* open dock on the manipulated menu area ([e04dc7f](https://github.com/teletha/viewtify/commit/e04dc7f098d09042d7190e75e1b94a110af8cc94))
* support automatic disposing ([a6c6a2d](https://github.com/teletha/viewtify/commit/a6c6a2def05323f9be5255003ae57a2f5df42847))
* Tab can be closed on context menu. ([4286c97](https://github.com/teletha/viewtify/commit/4286c97bb2e09404082c996f137d8d47ded2d76f))
* UITabPane supports automatic disposing view ([a63a716](https://github.com/teletha/viewtify/commit/a63a7167927dde3300c0d86c11a66a07f3f111c2))


### Bug Fixes

* various dock related bugs ([805f9e8](https://github.com/teletha/viewtify/commit/805f9e8102aa4fcf250a520a1daab1afbf6d3f46))

## [2.20.0](https://github.com/teletha/viewtify/compare/v2.19.0...v2.20.0) (2024-02-11)


### Features

* enahance dock location ([ff8d893](https://github.com/teletha/viewtify/commit/ff8d8939c72fcea17275fde43de6bd99e4ab3bac))
* remake dock api ([079fdfd](https://github.com/teletha/viewtify/commit/079fdfd9998309028e901e1ab1ec755b243e01f9))
* require java 21 ([68ec5c7](https://github.com/teletha/viewtify/commit/68ec5c7370a57f42154272f5ce4b246f3d9484b4))


### Bug Fixes

* checkbox style ([7138bf4](https://github.com/teletha/viewtify/commit/7138bf4bd4d073306f51af28d5e24e7edca2ec30))
* combined form style ([0f0f614](https://github.com/teletha/viewtify/commit/0f0f6141f1ece19ae74cee774b2ef3ae69db7238))
* dock related bugs ([d352e1b](https://github.com/teletha/viewtify/commit/d352e1b70b76083f15c9d1a21dc2dba62ff5f2d9))
* docks are restorable ([5a0ef1d](https://github.com/teletha/viewtify/commit/5a0ef1d9b06c253d8293c402df2e150a6002253b))
* enahnce docsystem ([d7e20e4](https://github.com/teletha/viewtify/commit/d7e20e4e13a22b50e5a4d057b75116600a12e9f4))
* refactoring form ([aba354d](https://github.com/teletha/viewtify/commit/aba354dc8e99590ff406e60114d19606edd3a87f))

## [2.19.0](https://github.com/teletha/viewtify/compare/v2.18.0...v2.19.0) (2024-01-19)


### Features

* add BlockHelper ([36e0394](https://github.com/teletha/viewtify/commit/36e0394b0140a878a15aabd263383ab15feb30b9))
* refactoring form styles ([b863d59](https://github.com/teletha/viewtify/commit/b863d59da9e432744878debf59916a6ce2307144))


### Bug Fixes

* anonymous browser is broken ([c76532b](https://github.com/teletha/viewtify/commit/c76532bc65cadd0ac3e9c351597cdb9370c96ec0))
* form styel ([0389892](https://github.com/teletha/viewtify/commit/0389892711e4adc685c90f54e941c56aff0dcc61))
* update ci process ([fe92987](https://github.com/teletha/viewtify/commit/fe92987436ad775eac5f006d34db13bfbe6275b8))
* update dialog is translatable ([fd04f6b](https://github.com/teletha/viewtify/commit/fd04f6b5a68a683d11915a140aef93fd3321a63f))
* update lisence ([fcb1fda](https://github.com/teletha/viewtify/commit/fcb1fdad9ba63a4b8e82b93c9badd9806e72bb9a))

## [2.18.0](https://github.com/teletha/viewtify/compare/v2.17.0...v2.18.0) (2024-01-01)


### Features

* add Anime#delay ([620c84d](https://github.com/teletha/viewtify/commit/620c84d02fc120e75ca197edd4a9055d7180d183))
* add PrintInfo#print(WritableImage) ([880d430](https://github.com/teletha/viewtify/commit/880d430e8fb0401eee47dce224e48cec39ccf15e))
* add UserInterfaceProvider#snapshot ([acfede6](https://github.com/teletha/viewtify/commit/acfede6bbe11c3e1106d67b43e61b2469f73ecfd))
* animate the context menu when showing ([b37b200](https://github.com/teletha/viewtify/commit/b37b200911c98282f61c8c34086a683b5fa90b55))
* docking tab is translatable. ([bdc55de](https://github.com/teletha/viewtify/commit/bdc55de8109e620a5bc0726c1827072eb84c6b9a))
* DockSystem provides manage events. ([a965e3f](https://github.com/teletha/viewtify/commit/a965e3ffb9563c7964d381a8a85a97a1322d745b))
* enhance context menu ([b767c59](https://github.com/teletha/viewtify/commit/b767c599d7c429122aee8dbc343fc7f9a9590436))
* enhance popup style ([c3c4b4b](https://github.com/teletha/viewtify/commit/c3c4b4baac162b5790be221f5d45df0f8f2cf563))
* replace native context menu ([77c7201](https://github.com/teletha/viewtify/commit/77c72011b19233f4ebe211ef0b85eec27e4e6d07))
* replace popup control coz no-way to avoid IME bug ([432424b](https://github.com/teletha/viewtify/commit/432424b1feb91664e9a826139a9766de7c50bb8e))
* support CheckMenuItem ([f05f367](https://github.com/teletha/viewtify/commit/f05f36727f1d70907c8a6e4c1d38e0e38bea76c5))
* support eagar/lazy setup for context menu ([92b12d0](https://github.com/teletha/viewtify/commit/92b12d066ff34ee0717829daf4dc721af52a52b9))
* TooltipHelper support popup arrow. ([bc8157b](https://github.com/teletha/viewtify/commit/bc8157b6add8ed3bed770a3d96265213cc934026))
* UITabPane#registerIcon ([4a00ec6](https://github.com/teletha/viewtify/commit/4a00ec6b69881c1cacf9b33f4e174af5b9eb0390))
* UserActionHelper can accept component event type. ([43bc243](https://github.com/teletha/viewtify/commit/43bc243d082c608f8e67bb77bcd88c30919f83f4))
* View provides default title and icon path. ([bc11ab4](https://github.com/teletha/viewtify/commit/bc11ab409b79d9950dd83b05915a4d8573c24d73))
* ViewtyDialog is locatable. ([54800fe](https://github.com/teletha/viewtify/commit/54800fec126610ed374c6f243c17458e5ed12902))
* ViewtyDialog is slidable ([006316b](https://github.com/teletha/viewtify/commit/006316b2b8f32e3f51640c8b343bf01a83c75704))


### Bug Fixes

* calendar dtail's style ([95ea613](https://github.com/teletha/viewtify/commit/95ea613c09ef060e8f9f88ca36813ae370ebce55))
* close all nested context menu gracefully ([629600d](https://github.com/teletha/viewtify/commit/629600d3ded5a171a01302de7f292069b8fc8d2e))
* close context menu gracefully ([e7d36fc](https://github.com/teletha/viewtify/commit/e7d36fc3825ce08ca69f2b3defa24ff73596a358))
* context animation ([36af3c7](https://github.com/teletha/viewtify/commit/36af3c7cb64a5d26ce0ea347e95de11b1015ffb6))
* correct dialog position ([7b971a7](https://github.com/teletha/viewtify/commit/7b971a7849b32bf8ba461cdff9dcfb6261f9158a))
* correct the miss position of context menu ([d3f4854](https://github.com/teletha/viewtify/commit/d3f485407c6c775408a4d5d56d61f3ce50feb9e9))
* correct variable-color lookup ([62a50d6](https://github.com/teletha/viewtify/commit/62a50d6b944a2dbe4ff5112401d3dbafd45efe69))
* dialog can fit to the correct size ([3f06196](https://github.com/teletha/viewtify/commit/3f061969962bc777d5300d4fa02adde84f98f29f))
* enhance popup ([634fd4c](https://github.com/teletha/viewtify/commit/634fd4c6ee08f0e66008c2d16263e294bb2014b2))
* enlarge preference row ([606dba9](https://github.com/teletha/viewtify/commit/606dba95711ee21130f1cb0c4093be5f29821f6e))
* enlarge preference view ([44049ba](https://github.com/teletha/viewtify/commit/44049ba2e224dba490cc00d4b83d6d093b7c0d54))
* hide calendar view ([02d7a2e](https://github.com/teletha/viewtify/commit/02d7a2ebf89875adb6cb854e9722e59f29c6afae))
* normalize popup position ([b9c3a9b](https://github.com/teletha/viewtify/commit/b9c3a9bcb116c29a7742091387f608e728ac0d3b))
* remove unused code ([c9a34f5](https://github.com/teletha/viewtify/commit/c9a34f548dde10a1474b2d8b330cc29d2a95d5a0))
* rename method ([9123e01](https://github.com/teletha/viewtify/commit/9123e0177e12c9c14c8bba41716aaded6406876b))
* style context separator ([342dd62](https://github.com/teletha/viewtify/commit/342dd6262a39a9b4446ca7ef18618a0656589c1a))
* write doc ([43a72bb](https://github.com/teletha/viewtify/commit/43a72bb72a36cf9c39798430570d2cca3c994693))

## [2.17.0](https://github.com/teletha/viewtify/compare/v2.16.1...v2.17.0) (2023-12-14)


### Features

* add UISlider#step ([9c114c6](https://github.com/teletha/viewtify/commit/9c114c60671b6a65f3d5775df8df5eb9077bd34a))
* add UpdateSetting ([699a6c0](https://github.com/teletha/viewtify/commit/699a6c08a48dd7766824c45179f077cca003809b))
* Add UpdateSettingView. ([87ede51](https://github.com/teletha/viewtify/commit/87ede51891c5f70b4488e5f012bdedb3abf4baa9))
* Add Viewtify#replaceSplashScreen. ([0ca366e](https://github.com/teletha/viewtify/commit/0ca366e7e6435391a65dfb14999d0981fb980d90))
* Additional fonts. ([699ed4f](https://github.com/teletha/viewtify/commit/699ed4fb52005f016d9eae672ccaa09465589e2a))
* enahnce updater ([5ddb08e](https://github.com/teletha/viewtify/commit/5ddb08e5b7659f17c6c0046ebbf4005cc51f1ef1))
* FileChooser and DirectoryChooser accept the translatable title. ([377f1dd](https://github.com/teletha/viewtify/commit/377f1dd2f62f2fc92bb47e00d0433e5fc6234194))
* preference is importable and exportable ([d9021ae](https://github.com/teletha/viewtify/commit/d9021ae0a0c92285cc9a856d7c6b4ca7b7596fe4))
* provide update notification ([06579d7](https://github.com/teletha/viewtify/commit/06579d7143e3d9c74c2728d304c9ffe5e61abce2))
* remove Viewtify#icon ([24d0e9b](https://github.com/teletha/viewtify/commit/24d0e9b5f00d15196ad9a8ca7c142d7839550007))
* Smooth scroll is configurable. ([5544324](https://github.com/teletha/viewtify/commit/5544324741f481e50c7b73323493579cf11f1173))
* support smart preference import/export ([dc4085e](https://github.com/teletha/viewtify/commit/dc4085e6f1e9a31b2f04dcdde6878ca3562fb4a8))
* UserActionHelper supports multiple action types. ([ae63545](https://github.com/teletha/viewtify/commit/ae63545456ddc66417cffea9bf088d9afb476d20))


### Bug Fixes

* add various version info to UpdateSettingView ([7dacfc0](https://github.com/teletha/viewtify/commit/7dacfc0c9479c7f18960277af2f0551364ea3c5c))
* refactoring smooth scroll ([ec95121](https://github.com/teletha/viewtify/commit/ec9512154da51a4945caa7794c50222a7990d649))
* remove animefx ([41953da](https://github.com/teletha/viewtify/commit/41953da9cc0c0fb89e094923ee4fe9b92ba68548))
* remove bad signature method ([c5f0ba7](https://github.com/teletha/viewtify/commit/c5f0ba74d1d24ce8965a48a9daf013e3fdb4e269))
* remove extra fonts ([f0a4b87](https://github.com/teletha/viewtify/commit/f0a4b8731366c0a947c7f74464865f590532fa59))
* remove Viewtify#size ([ea0ea70](https://github.com/teletha/viewtify/commit/ea0ea70ffbc37db8fd05ed3ba9d2bdca9249256f))
* styles ([fa118ae](https://github.com/teletha/viewtify/commit/fa118aeef9c399257818e0a1db2ee3f572b13574))
* typo ([5be2534](https://github.com/teletha/viewtify/commit/5be25346dc504b427cb6bed4d518b75a82296e2b))
* ui text ([8b4c950](https://github.com/teletha/viewtify/commit/8b4c95096393feb04ce2d9e4071767840d461dd2))
* UISlide#step accepts the latest step only ([8722321](https://github.com/teletha/viewtify/commit/8722321348c163cc74c369896efa3f109e627cf6))
* update smooth scroll ([b3d583b](https://github.com/teletha/viewtify/commit/b3d583b868fc93c3514df62a1267c73cce843552))
* update toast setting view ([7b434f1](https://github.com/teletha/viewtify/commit/7b434f14bb552013fa4291320072f2408f4f9fe7))
* updater is broken ([f66045a](https://github.com/teletha/viewtify/commit/f66045a53165485261fa7bb9206f44ab0741196f))

## [2.16.1](https://github.com/teletha/viewtify/compare/v2.16.0...v2.16.1) (2023-12-04)


### Bug Fixes

* correct package name ([8fb8f30](https://github.com/teletha/viewtify/commit/8fb8f301017476fb81827d2baa76a23444c6cf3c))

## [2.16.0](https://github.com/teletha/viewtify/compare/v2.15.1...v2.16.0) (2023-12-04)


### Features

* Generalized preference store. ([eaef37a](https://github.com/teletha/viewtify/commit/eaef37a43228601742cfc836435d71bb455a6baa))
* provide preference package ([dc3d76f](https://github.com/teletha/viewtify/commit/dc3d76fc8a7322286ea65ff4c717417c2d701e24))
* StorableList is reusable generic model collection. ([a473100](https://github.com/teletha/viewtify/commit/a47310094dccd676ccf11b9ae5e0410c99c7d3c9))


### Bug Fixes

* change preference view's style ([650c27c](https://github.com/teletha/viewtify/commit/650c27c218c172e6e389f04bbc179e05bf6d77c2))
* preferences aggregate automatic saving ([5ea8968](https://github.com/teletha/viewtify/commit/5ea896803ed78dc5edf360c51e0354919cd72863))
* remove id on preferences ([ec12e84](https://github.com/teletha/viewtify/commit/ec12e84e3e1c2a249472c9c3fa3928c8f465b8e0))
* remove StorableList and StorableModel ([e1abc00](https://github.com/teletha/viewtify/commit/e1abc00dc05f8924d793123aada10465c5a6a441))

## [2.15.1](https://github.com/teletha/viewtify/compare/v2.15.0...v2.15.1) (2023-12-02)


### Bug Fixes

* rename view for preferences ([4448b8e](https://github.com/teletha/viewtify/commit/4448b8e6a3c4ffd39593647e4b87b35abb91eb2a))

## [2.15.0](https://github.com/teletha/viewtify/compare/v2.14.0...v2.15.0) (2023-12-01)


### Features

* Add InWindow option for toast's location. ([22ace24](https://github.com/teletha/viewtify/commit/22ace24db6d5502a8384f491ba143f7993f10838))
* Add VisibleHelper#show(boolean). ([f7d01f6](https://github.com/teletha/viewtify/commit/f7d01f6b7038215be3683bc8550212e0bff88c61))
* Additional fonts. ([8fad39a](https://github.com/teletha/viewtify/commit/8fad39ac3dc7d62dfe135974832bcded0c64e2d0))
* Automatic dialog's button disabler. ([7b663a0](https://github.com/teletha/viewtify/commit/7b663a0ead56f8ce3c53aff056df5e10d99fa98e))
* ComboBox supports edit color. ([6114f55](https://github.com/teletha/viewtify/commit/6114f5557c735826d984dc0d54ae10e130891430))
* Config notification more. ([4dcb2bc](https://github.com/teletha/viewtify/commit/4dcb2bc36978786d5528f253a833f92e654889f8))
* Provide PreferencesView. ([e122555](https://github.com/teletha/viewtify/commit/e1225557d2027f30255965f6fc01f70865f8b2fc))
* Support consumable user action. ([033385b](https://github.com/teletha/viewtify/commit/033385bed7aaeb5eabf0934f0e4890d520847b38))
* Support fadable dialog. ([412b0ae](https://github.com/teletha/viewtify/commit/412b0ae01046378cdcd7b33e2148effa7d00357a))
* support form title ([6a7c45d](https://github.com/teletha/viewtify/commit/6a7c45df4d42eecae45b168cfe7922050127bb55))
* Support owner node blur when dialog showing. ([2e07ec4](https://github.com/teletha/viewtify/commit/2e07ec42c443916b43c435c3efc2637367a3c3d5))
* Support scroll method on scroll pane. ([ac8d8d5](https://github.com/teletha/viewtify/commit/ac8d8d5cc9485264601bf4c7ddb3cb29f3d4bbe9))
* Support simple actionable link in text notation. ([ddb7496](https://github.com/teletha/viewtify/commit/ddb7496b96eba6ba9dd07bc68df9bbb02f367825))
* support table of contents on preferences view ([6639b05](https://github.com/teletha/viewtify/commit/6639b05e9c498bbdfd8c073410b1cfe16d243e02))


### Bug Fixes

* add line spaace on text notation ([21391e7](https://github.com/teletha/viewtify/commit/21391e7c1a277c604f07ce2f6a827d4209c3a67b))
* can search table's cell ([c9634d6](https://github.com/teletha/viewtify/commit/c9634d66ac3a5b7c5bfd0407a0f97a6ef87abe9b))
* correct location of notifications ([7121fd6](https://github.com/teletha/viewtify/commit/7121fd689254977c9c94d9d8ff62f265b242f804))
* enhance notification setting ([8b03b9c](https://github.com/teletha/viewtify/commit/8b03b9c5e8b04a7272665a85670eb513d014f685))
* Hide validation text on hover. ([ada3f07](https://github.com/teletha/viewtify/commit/ada3f079f8063759f4df4540d1f479bd2aa4a3bb))
* preference style ([345c7d4](https://github.com/teletha/viewtify/commit/345c7d4a0911ae7472052b1fbf773571a0b5b290))
* Queue UI actions before launching UI. ([afc9845](https://github.com/teletha/viewtify/commit/afc98453fbbbcc18e2bd4668b4660d22d89d1308))
* reflection error ([4d15c98](https://github.com/teletha/viewtify/commit/4d15c98a2ed0c85f66ca3c6184168c2506c9fd6c))
* remove padding on hyperlink ([16d3023](https://github.com/teletha/viewtify/commit/16d302342634a27dcb60526b8de16de7fea3b832))
* remove PreferenceViewBase ([5fa859b](https://github.com/teletha/viewtify/commit/5fa859b278799069c0381920c3f86833498624e6))
* search prefeerences from description ([6672273](https://github.com/teletha/viewtify/commit/66722734b30e4cd5f6825b296bb7e9d1c317b3ab))
* search well on preferences view ([bc57fe2](https://github.com/teletha/viewtify/commit/bc57fe2aa112bc1e8be106008efc6dc5c85477f8))
* style ([0ae30fb](https://github.com/teletha/viewtify/commit/0ae30fbb37fceb6d550632e5bb950bf4cdf1b3f3))
* Suppress JVM error when closing application with shown dialog. ([7d16bda](https://github.com/teletha/viewtify/commit/7d16bdafffbaf664efc28cfc6f8fce4167baea9f))
* today style on calendar ([d574522](https://github.com/teletha/viewtify/commit/d574522715c23ad4134abd73b6f88cf51f7aaa50))
* update dialog button ([123589f](https://github.com/teletha/viewtify/commit/123589feec124518311aea8ef5884efefe1cb4e8))
* update toast style ([1b6b809](https://github.com/teletha/viewtify/commit/1b6b8099ceaa40f563fba68f5475a4ee09693281))
* ViewDSL API ([3c21c88](https://github.com/teletha/viewtify/commit/3c21c885123eb858355d16670d0cdf4df262e27b))

## [2.14.0](https://github.com/teletha/viewtify/compare/v2.13.0...v2.14.0) (2023-10-31)


### Features

* Add new swap animations. (SlideLeft and SlideRight) ([9292c43](https://github.com/teletha/viewtify/commit/9292c434ed9acc5d13bc4a2b18a11586a025e06a))
* Add toast setting view. ([809a822](https://github.com/teletha/viewtify/commit/809a822156cf560b59cca547687aec5264b96e75))
* Enable the time interval margin. ([3e1a7a5](https://github.com/teletha/viewtify/commit/3e1a7a5fea034cb9a5ad402effd9741094010ca5))
* enhance theme ([5bda4d4](https://github.com/teletha/viewtify/commit/5bda4d4d592f05cf7e019f5aed636ededf71caf7))
* rewrite UISelectablePane ([119d194](https://github.com/teletha/viewtify/commit/119d194c5a8bbddcff38e0634c35bc24719b1db3))
* Support custom color for each time event sources. ([b0de559](https://github.com/teletha/viewtify/commit/b0de5591ca031a0dd9d15525a6cffe988bb09f66))
* Support loading effect. ([64a23bd](https://github.com/teletha/viewtify/commit/64a23bd6191c87f66bbe0fcff9d83d87c85aebf2))
* Theme can find the variable color. ([4025ad8](https://github.com/teletha/viewtify/commit/4025ad8448a0f733233d0c9fd58d91069464459d))


### Bug Fixes

* Calendar can load events asynchronously. ([162214e](https://github.com/teletha/viewtify/commit/162214ef1be6ab80e5edaecb63c1e54b13a078ec))
* enahnce progress indicator ([13d74f4](https://github.com/teletha/viewtify/commit/13d74f4b363e82a0f1aa71e8498712a40e3e6167))
* enhance loading effect ([1240df1](https://github.com/teletha/viewtify/commit/1240df187562173d5e4d880b870b82f64febe982))
* Never dispose the non-prototyped view. ([f410920](https://github.com/teletha/viewtify/commit/f410920f05d56512560d986166b6b373fe77a0c3))
* suppress listener registration bug ([00cbb2a](https://github.com/teletha/viewtify/commit/00cbb2a42e0e942ee0fdc331d4a488f2a53d4ff1))

## [2.13.0](https://github.com/teletha/viewtify/compare/v2.12.0...v2.13.0) (2023-10-03)


### Features

* Add -fx-error and -fx-warning color. ([292193b](https://github.com/teletha/viewtify/commit/292193b0bc94266ebeaf8509b765a3f1be5cf532))
* Add AppearanceSettingView ([db4b87c](https://github.com/teletha/viewtify/commit/db4b87c0246e8fd6f3a813ddba595825170cbb81))
* Add new theme (Bellini). ([7d30e8b](https://github.com/teletha/viewtify/commit/7d30e8b7cfc809d7afb1192b9875da57240624b0))
* Add new theme (Blue Hawaii). ([4831b7c](https://github.com/teletha/viewtify/commit/4831b7c0b6c6a8c19999549204ddc266a5b0d020))
* Add theme type (flat and gradient). ([a3f5724](https://github.com/teletha/viewtify/commit/a3f5724feb903f17c54f4eaee7e9ef0a7eefccc0))
* Add TooltipHelper#unpopup. ([38a1f07](https://github.com/teletha/viewtify/commit/38a1f073ca1ed8e7787cdd4676d2c57c93510071))
* Add UIScrollPane#fit. ([4b0ce35](https://github.com/teletha/viewtify/commit/4b0ce353d6a34ca945f216e0ecb213cadc884e95))
* Add UISegmentedButton. ([d9fbf4a](https://github.com/teletha/viewtify/commit/d9fbf4a91872594476583bbfd0b36f75a63d650c))
* Support injection of array type. ([c5c2cc4](https://github.com/teletha/viewtify/commit/c5c2cc42060c655c8d526e88cf9c4417fe29d9d0))
* Support sequencial constraints on UIGridView. ([cad1e3d](https://github.com/teletha/viewtify/commit/cad1e3d623823d8d72466356c1c63821ed6794e3))
* Support some grid related styles. ([b85c77f](https://github.com/teletha/viewtify/commit/b85c77f638e6bead25f17c47c62442cceafb4380))
* UIToggleButton is selectable. ([60540a6](https://github.com/teletha/viewtify/commit/60540a61c176982dbd29a88a5c3530c4530d2cf4))
* Viewtify can manage its font. ([fe9cbc2](https://github.com/teletha/viewtify/commit/fe9cbc2123ada8ad4f8442bb9aaa3c07eabddefb))


### Bug Fixes

* Avoid NPE. ([7aa6a17](https://github.com/teletha/viewtify/commit/7aa6a17cfa92134738c00f540293a826f550972a))
* change genric type ([a238955](https://github.com/teletha/viewtify/commit/a23895521938ca514fdc68d490eb532d8f59847d))
* drop borderless button ([278a32a](https://github.com/teletha/viewtify/commit/278a32aa46284937db9973d2b760de83fa62b4ce))
* Execute animetion on UI thread. ([0f11232](https://github.com/teletha/viewtify/commit/0f112327ac1de164196c014dc3bb4fed1cef2d63))
* expand UI padding ([fb5c191](https://github.com/teletha/viewtify/commit/fb5c191c88320f2b7f831670e362a675ef69e085))
* Ignore the abstract type on widget DI ([f753525](https://github.com/teletha/viewtify/commit/f753525be896cdf91371cfc34bfc46f146a0f6b2))
* Popup can switch more flexible. ([356dbce](https://github.com/teletha/viewtify/commit/356dbce1afaed891d585d61195f78fcad2160e95))
* rename theme from bellini to peach fizz ([ddd303b](https://github.com/teletha/viewtify/commit/ddd303bcb8771b800f1fed84f1b3cd84797d765c))
* Show popup on mouse location. ([d4190fb](https://github.com/teletha/viewtify/commit/d4190fbba18a72d42dd76e4bf2e5ab7d39ee6b15))
* Use system default locale. ([c392d16](https://github.com/teletha/viewtify/commit/c392d1651891f5e26f068cb174196ff0ebf48e44))
* View can inject widget to super class. ([daab68a](https://github.com/teletha/viewtify/commit/daab68a7f4d47f37a9ff43bd080c293519cfb5b1))

## [2.12.0](https://github.com/teletha/viewtify/compare/v2.11.0...v2.12.0) (2023-08-26)


### Features

* Add SelectableHelper#isSelectedAt, #isNotSelectedAt and #toggleAt. ([1a52ac4](https://github.com/teletha/viewtify/commit/1a52ac4725a4bf31bc51a8c0426ae4cd61df0e94))
* CollectableHelper can handle artifacts. ([d82a939](https://github.com/teletha/viewtify/commit/d82a939aaf150562a1d0c2f6bf6d8bd4b37caff8))
* Enhance ComboBox related UI. ([3762bbc](https://github.com/teletha/viewtify/commit/3762bbc08e60989f9d716d9b79c3f03bf753fdc7))
* Enhance validation tooltip. ([9406706](https://github.com/teletha/viewtify/commit/9406706121b9cf1e707142f5b9a5cccdfb12ece9))


### Bug Fixes

* Initializing CheckComboBox throws IndexOutOfBoundException. ([18be79c](https://github.com/teletha/viewtify/commit/18be79cc08d7736cc119723a9863e562c2e71fb5))
* Showing and Hiding navigation with good behavior. ([43b47c9](https://github.com/teletha/viewtify/commit/43b47c9472fc6b91dc2e2d7ecfbd2b03ede5e692))
* ui-model sync ([c2529f5](https://github.com/teletha/viewtify/commit/c2529f53d8495ba9c250920af83c84db424af4cd))
* UIComboCheckBox is stylable now. ([83376e6](https://github.com/teletha/viewtify/commit/83376e6b57c1a090207ec4e728987405d4a4af02))
* update sinobu ([f63d2aa](https://github.com/teletha/viewtify/commit/f63d2aa7f561dd860aaa555a734d7e7f3cae6ecc))

## [2.11.0](https://github.com/teletha/viewtify/compare/v2.10.0...v2.11.0) (2023-07-03)


### Features

* Add CollectableHelper#itemsWhen. ([58a9b68](https://github.com/teletha/viewtify/commit/58a9b6824ce719388394ee9a8ed2ad18597a6999))
* Add line chart widget. ([58d5643](https://github.com/teletha/viewtify/commit/58d56430c66703514ffa82b210a4a1204a8c2902))
* Add new light theme. ([d2b7d1f](https://github.com/teletha/viewtify/commit/d2b7d1f969188db0e8861a408ecf0b82cca115eb))
* Add new theme 'GreenTea'. ([b7b06ac](https://github.com/teletha/viewtify/commit/b7b06acf949115cdc34de56910d11259b302ca53))
* Add UIPieChart. ([b3e1d8d](https://github.com/teletha/viewtify/commit/b3e1d8d225b6cc9f0b647b9d754eb834f389d255))
* Add ValueHelper#historicalValue. ([fd23c31](https://github.com/teletha/viewtify/commit/fd23c31473af034e46ac6e780a847cd62624574c))
* Enhance chart. ([231e773](https://github.com/teletha/viewtify/commit/231e773e7076ba9e0b336c8fd5e57d4a74bb2aaa))
* LabelHelper can set image as icon. ([9055293](https://github.com/teletha/viewtify/commit/9055293dc81458041612650c44944908ab990d9c))
* Provide enhanced LineChart. ([90ee0d7](https://github.com/teletha/viewtify/commit/90ee0d7deb0c2216868be625b38d1510e1c99269))
* Provide new theme 'CaffeLatte'. ([dbb924f](https://github.com/teletha/viewtify/commit/dbb924fb7fc09a4ee09ea66684983e9b2e77a890))
* Remove LabelHelper#icon(Glyph, Color). ([e95b2da](https://github.com/teletha/viewtify/commit/e95b2da26c9edae011322706b6905bc488a3cc14))
* Support built-in high contrast and old caspian theme. ([586a98f](https://github.com/teletha/viewtify/commit/586a98f05c6d863ab0b979c176b8d099d8dbb3b8))


### Bug Fixes

* Apply current theme to the separated docking window. ([93a004f](https://github.com/teletha/viewtify/commit/93a004f22c5752c8b0cb5181aa6617eb18b7a3c7))
* default button is broken on dark theme ([a67e4a0](https://github.com/teletha/viewtify/commit/a67e4a0556ca9a42e28d874762fa9516111748e1))
* default button is dirty in dark theme ([bab8904](https://github.com/teletha/viewtify/commit/bab890418c047db70450e47cacd912a18d37a2c1))
* default button style is broken on light theme ([c2dae4b](https://github.com/teletha/viewtify/commit/c2dae4bdc1b5c54abba9c1f8821ed60e1e295445))
* Edito requires Styleable instead of UserInterface. ([c451e29](https://github.com/teletha/viewtify/commit/c451e29dddf1874f8c4f5b141c3a13d6e38983ff))
* Enhance various charts. ([fc84a10](https://github.com/teletha/viewtify/commit/fc84a108af6c6c74dd0bd0c1ded23d80c02b7629))
* HideAnime can't fire the complete action. ([d63cd24](https://github.com/teletha/viewtify/commit/d63cd245e11af4393c0cc0c699b85c3ada0c65d6))
* remove caspian theme ([0ad649b](https://github.com/teletha/viewtify/commit/0ad649b61414270db44b13384d2d94ffd485a9cb))
* Remove CollectableHelper#itemAll. ([dcb98d8](https://github.com/teletha/viewtify/commit/dcb98d8650562e5d664e4d3daf7a32764069c1b6))
* remove high contrast theme because character corruption ([fd5d6c0](https://github.com/teletha/viewtify/commit/fd5d6c0b37dab2916aa49c9cfdf0baf8c7f111cc))
* rename theme from Light to Gray ([6e987ae](https://github.com/teletha/viewtify/commit/6e987ae66ae33903dc1d9bffa43f0a8e05f741f9))
* update dark theme ([806892b](https://github.com/teletha/viewtify/commit/806892b828216df38deb7c6bee7bcd374fb44dc7))
* update focus color on dark theme ([e67ff79](https://github.com/teletha/viewtify/commit/e67ff79d72cf6ce946f19f8a285346be473f44c7))

## [2.10.0](https://github.com/teletha/viewtify/compare/v2.9.0...v2.10.0) (2023-06-20)


### Features

* Add KeyBindingView to edit shortcut. ([70f4313](https://github.com/teletha/viewtify/commit/70f4313b0a46be505288b37249a5d59073d02334))
* PreferenceModel can reset to the default value. ([9e6b3be](https://github.com/teletha/viewtify/commit/9e6b3be156a6341052dbd8f7a894cc73d154e8a1))
* Support design scheme. ([e28446a](https://github.com/teletha/viewtify/commit/e28446acf7da83599a1f289aadf645cac9711d98))
* Support font smooth property. ([bd3f04e](https://github.com/teletha/viewtify/commit/bd3f04e79528b5585a55392912670ff255025ac3))
* UISelectPane implements not ValueHelper but SelectableHelper. ([b05b291](https://github.com/teletha/viewtify/commit/b05b2918dfc2a400ccd968b593e44584e57ed6ad))
* UserInterface#keybind supports modifier. ([2235133](https://github.com/teletha/viewtify/commit/22351334ae5ea1123065dc267229f037ac82e425))


### Bug Fixes

* disable anti-alias ([183de7e](https://github.com/teletha/viewtify/commit/183de7e67bfaa7383b1a582a7c4f7f07ad2e1153))
* Duration codec is broken. ([869f13c](https://github.com/teletha/viewtify/commit/869f13c59c0ec844c9ad0dd81f5641866286d81d))
* keep backward compatibility ([e9ec5c4](https://github.com/teletha/viewtify/commit/e9ec5c4d2c08721eda883e3f974631d5cf9e6c2b))
* Preference#syncTo can propagate value lazy. ([4fdd73b](https://github.com/teletha/viewtify/commit/4fdd73b3fcfec3ec5c42e416fea324f8a7ad0bf1))
* refactoring toast ([a46ea3c](https://github.com/teletha/viewtify/commit/a46ea3cac40332d66fe4a5e3f47922efa4bcbe1c))
* remove pom ([7649f9b](https://github.com/teletha/viewtify/commit/7649f9b1b6c43f38fcd29c8add7caa9b88dde762))
* Shortcut related classes move to keys package. ([60628bf](https://github.com/teletha/viewtify/commit/60628bf5debd50a215f1be1d5c04690ca4ef9d2d))
* UIText should handle null string as empty string. ([3f1add0](https://github.com/teletha/viewtify/commit/3f1add0db5a0a0cd83d3ff13a21b7dca9af2a718))

## [2.9.0](https://github.com/teletha/viewtify/compare/v2.8.0...v2.9.0) (2023-06-14)


### Features

* Add configuration for closing request. ([1007b4f](https://github.com/teletha/viewtify/commit/1007b4ff1230c4f1c57af37b8128de8693c5bad3))
* Add dialog builder. ([e515913](https://github.com/teletha/viewtify/commit/e515913e6c8153264891329179aa75c40cfd4cd9))
* Add DisableHelper#disableWhenAny and #enableWhenAny. ([295b8d6](https://github.com/teletha/viewtify/commit/295b8d66d5c52cbe4f7b9aafc78e58c807bbadcc))
* Add global ViewtySetting. ([1b1e27e](https://github.com/teletha/viewtify/commit/1b1e27ec6b4497d8ffbee5ef258624c16483b2f8))
* Add MonitorableTask. ([d5d4479](https://github.com/teletha/viewtify/commit/d5d4479b0e8fa5ad19c9497891b4d82214a610a3))
* Add UpdatePolicy ([d69d13b](https://github.com/teletha/viewtify/commit/d69d13b3b9c016532dfb0f6736283f1e3c6aaed9))
* Add UserActionHelper#fire(javaf's event) ([417dbf7](https://github.com/teletha/viewtify/commit/417dbf752d52cb17a4ec5bac8d4733fddd721e10))
* Add Viewtify#dialog to build various dialog. ([cfa8a2d](https://github.com/teletha/viewtify/commit/cfa8a2d1acbe6f70ac551542b76799474b2a0c11))
* Enhance ColletableHelper. ([423c03e](https://github.com/teletha/viewtify/commit/423c03ed02edaa73bd04c581c4da90a5968bfd99))
* Enhance Edito. ([95baced](https://github.com/teletha/viewtify/commit/95baced25cd7a07c68097d724eec809678c7b413))
* Support splash image. ([bd252b0](https://github.com/teletha/viewtify/commit/bd252b047e5e9f8c498673c52985cc12914f2215))
* ValueHelper and CollectableHelper can manage histrical value. ([1d0daf2](https://github.com/teletha/viewtify/commit/1d0daf26f8685596fe831f408d32816b94e5b47f))
* ViewtyDialog can cofigure shortcut key for each buttons. ([17dab22](https://github.com/teletha/viewtify/commit/17dab22a9cce33369687145a22268929f8baf866))


### Bug Fixes

* checking update time is incorrect. ([db2fe68](https://github.com/teletha/viewtify/commit/db2fe6846221556ba3d92af2864ffcc33964b2e2))
* compile error ([940e83b](https://github.com/teletha/viewtify/commit/940e83bd5c8782b2b5520b4c1661b0b49a9c1f9a))
* edit style ([6c56e22](https://github.com/teletha/viewtify/commit/6c56e22a838cb7156488cc9d424f93f683ac4fe6))
* remove unused class ([325ef29](https://github.com/teletha/viewtify/commit/325ef298ed7faa241ce5bd776a7ad88c6bd8b95c))
* revert the setting of dialog's size ([810efb8](https://github.com/teletha/viewtify/commit/810efb895459d4172010c1acd7566d0fed26bb05))
* Simplify DialogView. ([377f8a2](https://github.com/teletha/viewtify/commit/377f8a23822dc51ae9006d751fb82d2772dd2357))
* Simplify DialogView. ([7584b0e](https://github.com/teletha/viewtify/commit/7584b0e22e5ec8b18e34132ff9ea4fe2f70ef271))
* Update customfx. ([eccbc62](https://github.com/teletha/viewtify/commit/eccbc62a7d558eab5d2bd25afa00608137f6f41d))
* Update PrintPreview. ([e66a225](https://github.com/teletha/viewtify/commit/e66a225b6b13bbaa94887492ced32f7f01af7b55))
* Use minimum size on dialog setting. ([e48d263](https://github.com/teletha/viewtify/commit/e48d26308d138acb8d885b80b5a4d57320ff068e))

## [2.8.0](https://github.com/teletha/viewtify/compare/v2.7.0...v2.8.0) (2023-05-23)


### Features

* Add Animatable. ([ff9cc3c](https://github.com/teletha/viewtify/commit/ff9cc3c9902bb1646ad01f7bef62cb94f697df7e))
* Add AnimateHelper. ([b122bf6](https://github.com/teletha/viewtify/commit/b122bf60a6bf08a5106425168be33cddf2f72b45))
* Add application updater. ([c461105](https://github.com/teletha/viewtify/commit/c461105c78f02f87def63139963f5ddedc041bd6))
* Add CollectableHelper#removeItemAt(int...) and #removeItemAt(List) ([71c44c3](https://github.com/teletha/viewtify/commit/71c44c38fb2be6da1ec37042a37d7ac271dca50b))
* Add DecorationHelper. ([f1bfb37](https://github.com/teletha/viewtify/commit/f1bfb37f00b9bf280009ff07a9b5115a83175f76))
* Add image UI. ([eabdcfa](https://github.com/teletha/viewtify/commit/eabdcfa613f6b1c17b4317718fbe9b3f5419a1b7))
* Add print preview. ([9e7f5ed](https://github.com/teletha/viewtify/commit/9e7f5ed2c80887e19462cda5041081c97d6b61f5))
* Add UIProgressBar. ([e76092d](https://github.com/teletha/viewtify/commit/e76092dfc9690c15d0ca8e77eb119e02657494fa))
* Add UserInterface#keybind(Key, WiseRunnable). ([8be29f6](https://github.com/teletha/viewtify/commit/8be29f63ec894be8b7625495307f5ca49472ca18))
* Add various interpolators. ([c6d2f76](https://github.com/teletha/viewtify/commit/c6d2f760c94aa35c277536b87d9b6f7272dc8dd1))
* Add VisibleHelper#opacity. ([f5d3544](https://github.com/teletha/viewtify/commit/f5d35447167bc3ea472e50953759231edab5c7a5))
* Edito can save editing state. ([1007299](https://github.com/teletha/viewtify/commit/10072998c7b62375f3fe8108d2bb21581651291d))
* Provide editing state manager. ([2f78cbd](https://github.com/teletha/viewtify/commit/2f78cbd3436489bbb1ad99124dcbd14a861f2b14))
* UISelectPane implements ValueHelper&lt;UserInterfaceProvide&gt;. ([336962e](https://github.com/teletha/viewtify/commit/336962ea141fe03ab61092fee7df1e467973cc7c))


### Bug Fixes

* Registering editing state on UI thread. ([d517ebd](https://github.com/teletha/viewtify/commit/d517ebd8060709e1e7e5cbd3fb81325f334448ab))
* remove extra code (List#clear then List#setAll) ([38ac6fe](https://github.com/teletha/viewtify/commit/38ac6feaf185801ff35bcb8f964fdd336e608567))
* Text translation must be executed on UI thread. ([90c5b51](https://github.com/teletha/viewtify/commit/90c5b51c0aaed43aaea5c663ae0fa4c0fe0a52cd))
* update print preview ([f623477](https://github.com/teletha/viewtify/commit/f623477344bb85e6f1c3f03320acb907b3c17fff))
* UserActionHelper accepts multiple action types. ([3465183](https://github.com/teletha/viewtify/commit/346518363319a4f0ed91fb333306033290e78ff7))
* View has memory leak. ([e02aa29](https://github.com/teletha/viewtify/commit/e02aa29feb5598f085c0c6736d9aceb3f95bca4c))

## [2.7.0](https://github.com/teletha/viewtify/compare/v2.6.3...v2.7.0) (2023-04-24)


### Features

* Add monkey patch for text node. ([d88ac55](https://github.com/teletha/viewtify/commit/d88ac553bbfcf531cee21b64178c5bce3f893bba))
* Add MonkeyPatch#applyAll to fix UI related bug. ([103c130](https://github.com/teletha/viewtify/commit/103c1304bdb68d4ec69a57f359035e9b49cc4219))
* Add shorthand method to register user action. ([a745939](https://github.com/teletha/viewtify/commit/a74593975d7abd239fba97762bd4672b576c8132))
* Add the specialized view for Node. ([1e9da17](https://github.com/teletha/viewtify/commit/1e9da1748cc6a18e79d07a59b306b46f26300ce9))
* Add various collection manipulators. ([eca2147](https://github.com/teletha/viewtify/commit/eca2147d6a255c05940dda09633b49e8da53fc17))
* Add various conditional state methods. ([acf1918](https://github.com/teletha/viewtify/commit/acf19180be818bfd33cffda41c65a084d156e8db))
* Add various sort pattern. ([243cda9](https://github.com/teletha/viewtify/commit/243cda98db63c0377d77bc098cffac9aa5bef276))
* Add Viewtify#isActive. ([7a4c5d3](https://github.com/teletha/viewtify/commit/7a4c5d390efa0d855d44e376ff118303cd3bad60))
* Support drag and drop on table rows. ([c024ea8](https://github.com/teletha/viewtify/commit/c024ea854159f5b4d5bb1302347afc1573027e3a))
* Support nested context menu. ([943c149](https://github.com/teletha/viewtify/commit/943c149baa9875ea41eeb7efe6823b986ec10cc8))
* Support the nested property listener. ([e6413ff](https://github.com/teletha/viewtify/commit/e6413ffc42435c47f26141137a85c1c791fffd3d))


### Bug Fixes

* CheckboxTableCell is broken. ([71e8a0a](https://github.com/teletha/viewtify/commit/71e8a0a38941b97308189dd6748c9b811cfdfc1f))
* Drag-and-drop is allowed only within the same table. ([e9fe311](https://github.com/teletha/viewtify/commit/e9fe31130031d386e7ae5ed544dca6cf1514f84f))
* Toast is UI thread-safe. ([347a9b1](https://github.com/teletha/viewtify/commit/347a9b1e9fc5d0d8bc8f5989aaf43c081b9d33f0))
* Updating checkbox on table is broken. ([3496b53](https://github.com/teletha/viewtify/commit/3496b53384279f18bb8f1538aa2a4f59e010b787))

## [2.6.3](https://github.com/teletha/viewtify/compare/v2.6.2...v2.6.3) (2023-01-01)


### Bug Fixes

* headless mode is broken when anonymous browser ([bf4184a](https://github.com/teletha/viewtify/commit/bf4184aba253ee7e301a1c3fc7ce0b4221736373))

## [2.6.2](https://github.com/teletha/viewtify/compare/v2.6.1...v2.6.2) (2022-12-27)


### Bug Fixes

* update ci ([49e0fc4](https://github.com/teletha/viewtify/commit/49e0fc428ee5f8d2a407c888fc2217823ca65dd8))
* update stylist ([5a3fb25](https://github.com/teletha/viewtify/commit/5a3fb256ab5c6a13bbc0feb6b38bff509d0ceb27))

## [2.6.1](https://github.com/teletha/viewtify/compare/v2.6.0...v2.6.1) (2022-12-26)


### Bug Fixes

* ime related bug ([c195b89](https://github.com/teletha/viewtify/commit/c195b890f337cc4b8cafea6d83276e2edb4200b2))
* update javafx ([437aad1](https://github.com/teletha/viewtify/commit/437aad10f6187966ce4ab13790c65fc50d5355bb))
* update sinobu and lycoris ([c9bb997](https://github.com/teletha/viewtify/commit/c9bb997b02fb8215d902aae65c48e394340f1739))
* update stylist ([8906110](https://github.com/teletha/viewtify/commit/89061107e9f1de149697cc24c572f33dc4ad8ee7))
* update stylist ([c6449b8](https://github.com/teletha/viewtify/commit/c6449b85d55cb9804e41ea792a5db7911ddb766b))

## [2.6.0](https://github.com/teletha/viewtify/compare/v2.5.0...v2.6.0) (2022-08-31)


### Features

* Add ValueHelper#value(Variable). ([88add90](https://github.com/teletha/viewtify/commit/88add90688f005e774aa8104d097d33f6fd260d3))
* UITableCheckBoxColumn can observe its state. ([e7d66b0](https://github.com/teletha/viewtify/commit/e7d66b00074c3541debbe638fd95c4a00311b2ab))


### Bug Fixes

* Observe variable text. ([7b41180](https://github.com/teletha/viewtify/commit/7b41180333a71eddef712e8956116c49cba5aeef))
* UIButton run its commnad asynchronously. ([922943d](https://github.com/teletha/viewtify/commit/922943d47071de5122c41386e819aa2e3e809d28))
* UIScrollPane can fit its contents properly. ([8242bb2](https://github.com/teletha/viewtify/commit/8242bb2f84b016acc57a28d3fd7f28a20326d46b))
* UIScrollPane can layout its contents well. ([e465514](https://github.com/teletha/viewtify/commit/e465514416d8da4ee2c902e6bd9db93204b48662))
* update javafx ([6632465](https://github.com/teletha/viewtify/commit/6632465156a2c49fa2fbd7b718778bd9f59282ae))
* Viewtify#inWorker ensures executing the task in Non-UI thread. ([146200a](https://github.com/teletha/viewtify/commit/146200aaca5e1a8c015c671eb7688baa56ef7b1f))

## [2.5.0](https://www.github.com/teletha/viewtify/compare/v2.4.0...v2.5.0) (2022-06-30)


### Features

* Add animation helper. ([65da300](https://www.github.com/teletha/viewtify/commit/65da3000fb515cc0f57a1ce6bc3d9369ba8ffa0e))
* Add ContainerHelper. ([aa0ad31](https://www.github.com/teletha/viewtify/commit/aa0ad316eac184affbc0a7015e0eecd1f3c3735f))
* Add DockSystem#select(tab id). ([1d49034](https://www.github.com/teletha/viewtify/commit/1d490347cc08bf631425c42bb0b986a2c90cd0b2))
* Add GuardedOperation#protect(Runnable). ([9d02967](https://www.github.com/teletha/viewtify/commit/9d029670433329a7321292969977d4ca2e49be7b))
* Add keyboard navigation system. ([8736254](https://www.github.com/teletha/viewtify/commit/87362548da715272362b88477b19fa88bbc6f9be))
* Add LabelHelper#icon. ([2c9fe0b](https://www.github.com/teletha/viewtify/commit/2c9fe0b4937fc302b7fbebb4a46a5863f66e9378))
* Add noborder css. ([9badc77](https://www.github.com/teletha/viewtify/commit/9badc77c12592ff69a014fd4c6cb193b3c565293))
* Add some animation effect. ([cb09a12](https://www.github.com/teletha/viewtify/commit/cb09a12066dd16a09b1cd2a25bbe9f1654c26699))
* Add StorableModel and StorableList. ([28d81d4](https://www.github.com/teletha/viewtify/commit/28d81d46d7971db2061609b4ad0c9d64b013dae0))
* Add UIComboBox#show, #hide and #toggle to operate the popup. ([37dd910](https://www.github.com/teletha/viewtify/commit/37dd9107b571a33985266d039b30f4c17939573a))
* Add UIFlowView. ([412be6a](https://www.github.com/teletha/viewtify/commit/412be6a328919486347cb496a416cc37f8a326db))
* Add UISelectPane. ([e6a97c3](https://www.github.com/teletha/viewtify/commit/e6a97c3aced75938e26d1f57bf4c1338b424da93))
* Add UITableCheckBoxColumn. ([58f7413](https://www.github.com/teletha/viewtify/commit/58f741382f9795d96067e39425f62cea70e719f2))
* Add UIText#isTextSelected and #isNotTextSelected. ([38a1610](https://www.github.com/teletha/viewtify/commit/38a1610a8d8adb7abb137a4648997deaa07ba6d2))
* Add UIText#prefix. ([52299c4](https://www.github.com/teletha/viewtify/commit/52299c47a1b6cc336ca167e5dbbea572eaddec85))
* Add UIText#suffix. ([b421619](https://www.github.com/teletha/viewtify/commit/b421619d9425f30b24129706ce4d3939e1331965))
* Add UITextArea. ([05813a4](https://www.github.com/teletha/viewtify/commit/05813a45f570d8e59eb4521504489c637ca77b4f))
* Add UITileView. ([5942c0b](https://www.github.com/teletha/viewtify/commit/5942c0bff3db80d80f42bf8144b7b766a73efd0d))
* Add ValueHelper#isNull. ([fd30cb9](https://www.github.com/teletha/viewtify/commit/fd30cb900b841fb2d0ec499e06b579363c8e744a))
* Add ValueHelper#observe(boolean skipNull). ([79ecdc0](https://www.github.com/teletha/viewtify/commit/79ecdc0ebc00120f72b7dec6aaaf7a161b91bc72))
* CollectableHelper can reapply the filter and sorter. ([29caa98](https://www.github.com/teletha/viewtify/commit/29caa98f994aa4b402a8afd8eaa482d4fa2bdb2f))
* KeyboardNavigation recognize the verifier. ([b95b787](https://www.github.com/teletha/viewtify/commit/b95b787648e91211b2b475798551f840bcce3d66))
* KeyboardNavigation supports checkbox input. ([1f8205a](https://www.github.com/teletha/viewtify/commit/1f8205a262a251b03dbf29595b537b6c6bff3f2d))
* Support auto focus keyboard navigation. ([0114cb4](https://www.github.com/teletha/viewtify/commit/0114cb4d58a9ac91db8ae56e939b3f109bd8b49a))
* Support style "text-decation: underline". ([5c39c75](https://www.github.com/teletha/viewtify/commit/5c39c7518466f4909b945c244047d71467ff700e))
* Traverser supports widget focus order. ([2ea0f00](https://www.github.com/teletha/viewtify/commit/2ea0f0080b78aaff7c449e3a376a917eadbbc7c2))
* UIButton can contribute any command. ([651ae13](https://www.github.com/teletha/viewtify/commit/651ae13b4482fea1ed53b2d6937b4533e9195782))
* UIText supports auto complettion. ([418fc19](https://www.github.com/teletha/viewtify/commit/418fc19119113c82b67ed1f0619743664cb41cd6))
* UIWeb#deleteCookies. ([8e70c6a](https://www.github.com/teletha/viewtify/commit/8e70c6a96ede4b98c8db9e7b57fae47597645803))
* ValueHelper implements Consumer. ([0532413](https://www.github.com/teletha/viewtify/commit/0532413590073e58722a9ad31ed5be63bd1f75d2))
* ValueHelper is Supplier. ([b0dbbaa](https://www.github.com/teletha/viewtify/commit/b0dbbaa235f89dead20dfc79178a9c7d78b84224))
* ViewDSL provides generic separator. ([979d7d6](https://www.github.com/teletha/viewtify/commit/979d7d62a159f66dd5a63ae93b35c961843ad463))


### Bug Fixes

* Application policy. ([1512789](https://www.github.com/teletha/viewtify/commit/151278993a57a6671cbc7742c3215ab9bf54274b))
* Auto focus for text input. ([0e0d532](https://www.github.com/teletha/viewtify/commit/0e0d5324a4f6e29f87000ff00b88d8772516102a))
* CollectableHelper#items accepts null or empty list properly. ([0e6c223](https://www.github.com/teletha/viewtify/commit/0e6c223ec8aa335df100934b73fdc5ab8bb3b66d))
* Enhance filter and sort function. ([1483c29](https://www.github.com/teletha/viewtify/commit/1483c29a8463a4a48b206c57acb6eb2c7e759fe0))
* Enhance swap anime. ([9c08c6c](https://www.github.com/teletha/viewtify/commit/9c08c6cf2a00a9c4cda91157964bc3b063d71c45))
* Key navigated combo box travese to next input when it select value. ([e9b59b4](https://www.github.com/teletha/viewtify/commit/e9b59b452a802f61b1348569fc0ed79f20403401))
* LabelHelper#text(icon, style). ([1cfdd83](https://www.github.com/teletha/viewtify/commit/1cfdd83eb2507a94cfa01ba13fdfa076d5807a63))
* Reset selection for redrawing. ([959a134](https://www.github.com/teletha/viewtify/commit/959a1347ef19a1d9270b529db83da5b8680cb5f9))
* Returns the same ummodifiable view. ([52c6488](https://www.github.com/teletha/viewtify/commit/52c6488ee3b403a4cc94d0dcd25017b1675897c7))
* scrollbar color ([7d49423](https://www.github.com/teletha/viewtify/commit/7d49423b5a30b60b7840328b3ee1a15363f94eeb))
* SelectionHelper#whenSelect is broken on table. ([49e0ecd](https://www.github.com/teletha/viewtify/commit/49e0ecdd3a86315fd3d5f1dfe280b5a389c362d8))
* Support virtual flow's size estimation. ([6053f65](https://www.github.com/teletha/viewtify/commit/6053f658e8da1ee3a67b1270bc8021ab451c2d9c))
* UISelectPane can accept vertical padding. ([09cdef9](https://www.github.com/teletha/viewtify/commit/09cdef93332d8eda677879fe6f748cd8182018a5))
* UIText#maximumInput can accept correctly at text replacement. ([1a3aae1](https://www.github.com/teletha/viewtify/commit/1a3aae168a6a5d9865af3f417baa037458674fd6))
* Viewtify#browser can be invoked more safely. ([1048fa4](https://www.github.com/teletha/viewtify/commit/1048fa447bbde982a09c3dfe2fbc50877fa9618f))

## [2.4.0](https://www.github.com/teletha/viewtify/compare/v2.3.0...v2.4.0) (2022-03-17)


### Features

* Add UIWeb#attribute which accessing to the attribute value. ([a794c08](https://www.github.com/teletha/viewtify/commit/a794c080429b4ff1852fea494f9792d1507276d3))

## [2.3.0](https://www.github.com/teletha/viewtify/compare/v2.2.1...v2.3.0) (2022-03-14)


### Features

* Add DockSystem#validate. ([ff2b1f8](https://www.github.com/teletha/viewtify/commit/ff2b1f86f32ab88713f60a880c5b8457f5a01bf4))
* Support more flexible dock layout. ([ed5abe6](https://www.github.com/teletha/viewtify/commit/ed5abe65e16f87ebaceac9a3143f15fd34c11d0d))


### Bug Fixes

* Activate automatic gc more. ([fa8f921](https://www.github.com/teletha/viewtify/commit/fa8f921662fc32f67ef1400a5b3babd2fc029738))
* Better draggable area. ([c4dacab](https://www.github.com/teletha/viewtify/commit/c4dacabf81126c4a42e9c0721f8272926e56f971))
* Canvas enable to override bounded size. ([8dc35aa](https://www.github.com/teletha/viewtify/commit/8dc35aa924a220a02ccb34f05e3868e1dee0f6e9))
* Dragging dock item is broken. ([886ccb9](https://www.github.com/teletha/viewtify/commit/886ccb9011f6d124b977e219717444f0a0c7b93c))
* Restore the location of separated window. ([c16dc7d](https://www.github.com/teletha/viewtify/commit/c16dc7d014bf493621e5841abf96daaecbd70ff8))

### [2.2.1](https://www.github.com/teletha/viewtify/compare/v2.2.0...v2.2.1) (2022-01-02)


### Bug Fixes

* Stabilize headless mode. ([fd5c781](https://www.github.com/teletha/viewtify/commit/fd5c781c2964d87412258b5bf782cb329087bccb))

## [2.2.0](https://www.github.com/teletha/viewtify/compare/v2.1.0...v2.2.0) (2022-01-02)


### Features

* UIWeb#load can load HTML contents directly. ([d467852](https://www.github.com/teletha/viewtify/commit/d4678529833fc675001bf18896add40ba13280ec))


### Bug Fixes

* headless mode ([b03cd70](https://www.github.com/teletha/viewtify/commit/b03cd705dfa31896c7e9fabf9990094a5ff8b110))
* Invoke Viewtify#browser on FX thread. ([ba96bbe](https://www.github.com/teletha/viewtify/commit/ba96bbedc22fef41dcb5a8c970720ca6f69516d3))

## [2.1.0](https://www.github.com/teletha/viewtify/compare/v2.0.0...v2.1.0) (2021-12-30)


### Features

* Add UIWeb#script to execute an arbitrary code. ([1177f72](https://www.github.com/teletha/viewtify/commit/1177f72c802d4c96340fa154d47c0bf60f29db05))
* Enable automatic GC. ([998cce3](https://www.github.com/teletha/viewtify/commit/998cce367ab34d532706501e8887959c05c7f034))
* Support headless mode to use Viewtify#inHeadless. ([51b40c7](https://www.github.com/teletha/viewtify/commit/51b40c770cad56e7d653b9f040e65eba6fbc7bec))
* update javafx. ([805350f](https://www.github.com/teletha/viewtify/commit/805350f6fe61662d3c08500c99adca95498b355b))

## 2.0.0 (2021-03-29)


### Bug Fixes

* Animete Toast in UI thread. ([3362ab7](https://www.github.com/Teletha/viewtify/commit/3362ab7bd2f40728fa822b711ffbeccdba85ced7))
* BorderBox hides dock window. ([ab0284d](https://www.github.com/Teletha/viewtify/commit/ab0284db2fda349efcc32088f8556bf1bc6d3bcf))
* Docking window accepts only dockable pane. ([df28ad0](https://www.github.com/Teletha/viewtify/commit/df28ad091432811fca70cca249238e05bfdb6e13))
* DropStage is not closed when drag and drop fails. ([6704724](https://www.github.com/Teletha/viewtify/commit/6704724bbd88e7864be840a694ade8e854aec56d))
* Enable CI. ([f72a6ff](https://www.github.com/Teletha/viewtify/commit/f72a6ff3d836e425e61fde2c91e7c42da56c51ce))
* Failing value transformation crush UI. ([fc43ed5](https://www.github.com/Teletha/viewtify/commit/fc43ed517fd52b90f5e11a852524718bdbced2d2))
* Initialize JavaFX capably. ([71cf0d7](https://www.github.com/Teletha/viewtify/commit/71cf0d7f4d41215987c02c6162133dd19feb3e90))
* Make code compilable by javac. ([f55a019](https://www.github.com/Teletha/viewtify/commit/f55a0197da9b08cc733d35b1ee70a581f928a8b0))
* Non ui-related test fails by toolkit not initialized. ([b19764f](https://www.github.com/Teletha/viewtify/commit/b19764f3f3325a78c5d5634fc3202ed901e7ecb2))
* NPE in CSS. ([93165b1](https://www.github.com/Teletha/viewtify/commit/93165b19811a17105fcba730b57b5d166c3d36f9))
* Remove window location info on closing the dock window. ([c1140e6](https://www.github.com/Teletha/viewtify/commit/c1140e6854c67bcb21c1769ecd9aa16ea5f2d1e4))
* Restart by exewrap is broken. ([44a4753](https://www.github.com/Teletha/viewtify/commit/44a4753074c454053a6baf78fc00d33c8668028c))
* Runtime lambda factory can't detect valid intersection type. ([3ce4d2c](https://www.github.com/Teletha/viewtify/commit/3ce4d2ca12df5506eed531cf20ee2a1e907d4283))
* SplitPane can't keep the dividier's position when content is ([10c4cc5](https://www.github.com/Teletha/viewtify/commit/10c4cc5f524d534c9c6f4ed98f26020dfe6804cc))
* StyleHelper#unstyle is wrong. ([edc413c](https://www.github.com/Teletha/viewtify/commit/edc413c5084c0b82235074cc98e398f4a1cf5ab9))
* Tab implementation don't bind id. ([d053264](https://www.github.com/Teletha/viewtify/commit/d0532644342f6ea7e464856e6d9aed1e2992a6a4))
* Throws NPE when dock system creates new window with icon. ([672ca93](https://www.github.com/Teletha/viewtify/commit/672ca935f1ab47d42b9f3392789e565b0f613825))
* Traversal action can skip disabled item. ([d1e058d](https://www.github.com/Teletha/viewtify/commit/d1e058dc2c159be681e4624752882624eadb3042))
* TypeMappingProvider on table column recognize subclass of the ([75ccf05](https://www.github.com/Teletha/viewtify/commit/75ccf05d1dacdd46b5cc3d85511eaf76cfacef9d))
* UIDatePicker's action throws NPE when it has null value. ([0e7475d](https://www.github.com/Teletha/viewtify/commit/0e7475d9790b2fbce95d9a5b40f136be095afeac))
* UIListView filter is broken. ([de22ab7](https://www.github.com/Teletha/viewtify/commit/de22ab7707b5806018a3cab26a525da53285ffb3))
* UITableCell brokes rendering. ([401ec8a](https://www.github.com/Teletha/viewtify/commit/401ec8ad760d40852b7a3a73f951b8e3869cee0e))
* UITabPane selects model by mouse scroll. ([8046e0a](https://www.github.com/Teletha/viewtify/commit/8046e0a800321b80b556379c05fc2d29efc96281))
* UIText can ignore blank value. ([1f1ee51](https://www.github.com/Teletha/viewtify/commit/1f1ee518d7f17a0c7bbffd8a8e31061518c87967))
* Untrack main window. ([e572d47](https://www.github.com/Teletha/viewtify/commit/e572d47aa6becb69983ac2431ac38bee590fbae6))
* User click event is broken. ([c706cf1](https://www.github.com/Teletha/viewtify/commit/c706cf178f51bf355b053d4a6c7407c51698d731))
* UserInterface#restore ignores invalid stored value. ([09c397d](https://www.github.com/Teletha/viewtify/commit/09c397ddbf70022b7788235bc078ad75c9609fd9))
* Validation doesn't verify initial state. ([cbb0eb0](https://www.github.com/Teletha/viewtify/commit/cbb0eb07c72c30a44a860cecfb2f448a9c2dd743))
