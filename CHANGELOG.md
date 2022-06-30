# Changelog

### [2.5.1](https://www.github.com/teletha/viewtify/compare/v2.5.0...v2.5.1) (2022-06-30)


### Bug Fixes

* UIScrollPane can fit its contents properly. ([8242bb2](https://www.github.com/teletha/viewtify/commit/8242bb2f84b016acc57a28d3fd7f28a20326d46b))
* UIScrollPane can layout its contents well. ([e465514](https://www.github.com/teletha/viewtify/commit/e465514416d8da4ee2c902e6bd9db93204b48662))

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
