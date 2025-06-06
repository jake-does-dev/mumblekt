# Changelog

## [1.0.1](https://github.com/jake-does-dev/mumblekt/compare/v1.0.0...v1.0.1) (2025-05-31)


### Bug Fixes

* **publish:** attempt to automatically publish on releases ([#35](https://github.com/jake-does-dev/mumblekt/issues/35)) ([4f74eb6](https://github.com/jake-does-dev/mumblekt/commit/4f74eb64170b35afb26f18945614f3e75bed2903))

## 1.0.0 (2025-05-29)


### Features

* **client:** introduce mumble client ([#19](https://github.com/jake-does-dev/mumblekt/issues/19)) ([46a7de6](https://github.com/jake-does-dev/mumblekt/commit/46a7de604bcd45ea1ea61ea8027d806b026c077a))
* **client:** stabilise TCP client public facing API ([#22](https://github.com/jake-does-dev/mumblekt/issues/22)) ([488392f](https://github.com/jake-does-dev/mumblekt/commit/488392fdf948bcec0a00f702922f848c2fd006c6))
* **messages:** add more message types ([#17](https://github.com/jake-does-dev/mumblekt/issues/17)) ([04c1290](https://github.com/jake-does-dev/mumblekt/commit/04c12901b3ef35247e7a7fe30e2661375b675784))
* **messages:** define remaining TCP messages ([#20](https://github.com/jake-does-dev/mumblekt/issues/20)) ([03d2bc5](https://github.com/jake-does-dev/mumblekt/commit/03d2bc53998f82f6e4160c5a3b65f87d51c57ee9))
* **messaging:** add handler for incoming messages ([#15](https://github.com/jake-does-dev/mumblekt/issues/15)) ([111e539](https://github.com/jake-does-dev/mumblekt/commit/111e539c2b4d7a64618e8da596cbc6098f3833e5))
* **protocol:** encode/decode according to Mumble Protocol stack (TCP) ([#6](https://github.com/jake-does-dev/mumblekt/issues/6)) ([310dbe2](https://github.com/jake-does-dev/mumblekt/commit/310dbe20886ffe6daa3503d0f5abed471a1ea562))
* **proto:** initial start on using kotlinx-serialization-protobuf ([#3](https://github.com/jake-does-dev/mumblekt/issues/3)) ([63b8091](https://github.com/jake-does-dev/mumblekt/commit/63b80918f1c22a2b0a20cdf687f9e7adc33dfe66))
* **ssl:** use Ktor to connect to Mumble via SSL  ([#8](https://github.com/jake-does-dev/mumblekt/issues/8)) ([55c5aa3](https://github.com/jake-does-dev/mumblekt/commit/55c5aa34537b56428ed3add60d3738c160e7a18c))
* **start:** initial commit ([20e179d](https://github.com/jake-does-dev/mumblekt/commit/20e179d71b99cf71a484dbdcce636d5bf9bdb0d2))
* **start:** initial commit ([2dd8904](https://github.com/jake-does-dev/mumblekt/commit/2dd89047379f065026c2c9645c314fcfe24ffb03))
* **subscribers:** add subscribers and callbacks in client ([#21](https://github.com/jake-does-dev/mumblekt/issues/21)) ([9449e5c](https://github.com/jake-does-dev/mumblekt/commit/9449e5caad4614227abcb9e2a73528f34a7077bb))
* **test:** add simple test ([a349bb1](https://github.com/jake-does-dev/mumblekt/commit/a349bb11c029a6b6d286e155604efe0523ea8079))


### Bug Fixes

* **perms:** fix permissions for github action ([3cb647a](https://github.com/jake-does-dev/mumblekt/commit/3cb647a96b40cddbc50c4c79d58bdfa3f9d19658))
* **proto:** fix up accidental serialization of sealed interface ([#10](https://github.com/jake-does-dev/mumblekt/issues/10)) ([e265447](https://github.com/jake-does-dev/mumblekt/commit/e26544738a04d536dd5be0430f207c9a76b35110))
* **proto:** using sealed interface causes types to be serialized too ([#13](https://github.com/jake-does-dev/mumblekt/issues/13)) ([459dbf5](https://github.com/jake-does-dev/mumblekt/commit/459dbf55fbc755a22f4f915e062e02e12a155550))
* **structure:** refactor, and fix issue with TextMessage's ProtoNumber ([#26](https://github.com/jake-does-dev/mumblekt/issues/26)) ([91bf2fb](https://github.com/jake-does-dev/mumblekt/commit/91bf2fb5819e059ba0d165def6076a045ad7d3fd)), closes [#25](https://github.com/jake-does-dev/mumblekt/issues/25)


### Miscellaneous Chores

* reset release version ([19ef3cd](https://github.com/jake-does-dev/mumblekt/commit/19ef3cda8ef1ca2edaac8ff9191824f645c9994d))
