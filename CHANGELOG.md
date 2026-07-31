# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.0.1] - 2026-07-31

### Fixed

- Magic circle no longer disappears instantly after being cast
- Marked mobs no longer look at the player while fighting
- VFX colors no longer wash out on bright backgrounds and stay visible over translucent backgrounds
- Magic circle and VFX now render above ice and water instead of fading into them
- Magic circle effect range now matches its visual size (32-block radius)

### Added

- Preview marker on selected mobs (left-click single target and area selection), cleared on reset or when the attack target is confirmed

### Changed

- Marked mobs show the hostility marker only while attacking their forced target
- Magic circle matches mobs by nearest distance instead of random assignment

### Optimized

- Extracted shader constants and unified the color palette; flame gradient now computes fewer color mixes per pixel

## [1.0.0] - 2026-07-30

Initial release.
