package jces1209.vu.page.boards.view.cloud

import org.openqa.selenium.WebDriver
import java.net.URI

class ScrumBoardPage(
    private val driver: WebDriver, uri: URI
) : ClassicBoardPage(driver, uri)
