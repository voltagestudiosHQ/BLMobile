/*
 * Zalith Launcher 2
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

package com.movtery.zalithlauncher.ui.control.gamepad

import android.view.KeyEvent
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DeveloperModeSequenceTest {
    private val sequence = DeveloperModeSequence()
    private val code = intArrayOf(
        KeyEvent.KEYCODE_DPAD_UP,
        KeyEvent.KEYCODE_DPAD_UP,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_LEFT,
        KeyEvent.KEYCODE_DPAD_RIGHT,
        KeyEvent.KEYCODE_DPAD_LEFT,
        KeyEvent.KEYCODE_DPAD_RIGHT
    )

    @Test
    fun `only the complete sequence activates developer mode`() {
        code.dropLast(1).forEach { keyCode ->
            assertFalse(sequence.onKeyDown(keyCode))
        }
        assertTrue(sequence.onKeyDown(code.last()))
        assertFalse(sequence.onKeyDown(code.last()))
    }

    @Test
    fun `unexpected input restarts matching at the next up`() {
        sequence.onKeyDown(KeyEvent.KEYCODE_DPAD_UP)
        assertFalse(sequence.onKeyDown(KeyEvent.KEYCODE_DPAD_DOWN))

        code.dropLast(1).forEach { keyCode ->
            assertFalse(sequence.onKeyDown(keyCode))
        }
        assertTrue(sequence.onKeyDown(code.last()))
    }

    @Test
    fun `reset discards a partially entered sequence`() {
        code.take(4).forEach { sequence.onKeyDown(it) }
        sequence.reset()
        code.dropLast(1).forEach { keyCode ->
            assertFalse(sequence.onKeyDown(keyCode))
        }
        assertTrue(sequence.onKeyDown(code.last()))
    }
}
