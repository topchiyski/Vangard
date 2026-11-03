package com.vanguard.web.pages;

/**
 * Whatever page is open right now. Powers the generic web steps (open a path, check URL and title).
 */
public class CurrentPage extends BasePage {

    /**
     * Opens a path on the web base URL, e.g. "/cart.html".
     */
    public void openPath(String path) {
        open(path);
    }

    /**
     * The URL of the open page.
     */
    public String url() {
        return currentUrl();
    }

    /**
     * The browser tab title.
     */
    public String pageTitle() {
        return title();
    }

    /**
     * Waits until the URL contains the given text.
     */
    public void waitForUrl(String fraction) {
        waitForUrlContains(fraction);
    }
}
