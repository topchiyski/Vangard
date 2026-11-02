package com.vanguard.web.pages;

/** Whatever page is open right now. Powers the generic web steps (open a path, check URL and title). */
public class CurrentPage extends BasePage {

    public void openPath(String path) {
        open(path);
    }

    public String url() {
        return currentUrl();
    }

    public String pageTitle() {
        return title();
    }

    public void waitForUrl(String fraction) {
        waitForUrlContains(fraction);
    }
}
