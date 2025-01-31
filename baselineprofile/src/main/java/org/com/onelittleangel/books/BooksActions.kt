package org.com.onelittleangel.books

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.untilHasChildren
import org.com.onelittleangel.flingElementDownUp

fun MacrobenchmarkScope.booksWaitForContent() {
    // Wait until content is loaded by checking if authors are loaded
    //device.wait(Until.gone(By.res("forYou:loadingWheel")), 5_000)
    // Sometimes, the loading wheel is gone, but the content is not loaded yet
    // So we'll wait here for authors to be sure
    //device.wait(Until.findObject(By.res("searchBar:placeholder")), 30_000)

    val obj = device.findObject(By.res("books:feed"))
    obj.wait(untilHasChildren(), 30_000)
}

/**
 * Selects some authors, which will show the feed content for them.
 * [recheckAuthorsIfChecked] Authors may be already checked from the previous iteration.
 */
fun MacrobenchmarkScope.forYouSelectAuthors(recheckAuthorsIfChecked: Boolean = false) {
    val authors = device.findObject(By.res("forYou:authors"))

    // Set gesture margin from sides not to trigger system gesture navigation
    val horizontalMargin = 10 * authors.visibleBounds.width() / 100
    authors.setGestureMargins(horizontalMargin, 0, horizontalMargin, 0)

    // Select some authors to show some feed content
    repeat(3) { index ->
        val author = authors.children[index % authors.childCount]

        when {
            // Author wasn't checked, so just do that
            !author.isChecked -> {
                author.click()
                device.waitForIdle()
            }

            // The author was checked already and we want to recheck it, so just do it twice
            recheckAuthorsIfChecked -> {
                repeat(2) {
                    author.click()
                    device.waitForIdle()
                }
            }

            else -> {
                // The author is checked, but we don't recheck it
            }
        }
    }
}

fun MacrobenchmarkScope.booksScrollFeedDownUp() {
    val feedList = device.findObject(By.res("books:feed"))
    device.flingElementDownUp(feedList)
}
