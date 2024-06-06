package org.example;

import org.example.base.TestBase;
import org.example.pages.HomePage;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;
import java.util.List;

public class HomePageTest extends TestBase {
    HomePage homePage;

    public HomePageTest() {
        super();
        initialization();
        homePage = new HomePage();
    }

//    @BeforeMethod
//    public void setUp() {
//        initialization();
//        homePage = new HomePage();
//    }

    @Test(priority=1)
    public void verifyPageLoad(){
        String pageTitle = homePage.getPageTitle();
        Assert.assertEquals(pageTitle, "Setur’dan güvenle al, tatilde kal! | Setur", "Page did not load");
    }

    @Test(priority=2)
    public void verifyDefaultTabText() {
        String defaultTabText = homePage.getDefaultTabText();
        Assert.assertEquals(defaultTabText, "Otel", "Text is not matched");
    }

    @Test(priority=3)
    public void closePopUps() {
        homePage.closeIndirimPopUp();
        homePage.closeCookies();
        homePage.sendDestinationKeys();
        homePage.chooseDate();
    }

    @Test(priority=4)
    public void checkAdultButton() {
        List<Integer> myRes = homePage.clickAdultButton();
        Assert.assertEquals(myRes.get(0) + 1, myRes.get(1), "Adult count not increased");
    }

    @Test(priority=5)
    public void checkSearchButton() {
        Assert.assertEquals(homePage.clickSearchButton(), Boolean.TRUE, "Search Button is not displayed");
    }

    @Test(priority=6)
    public void checkUrl(){
        Assert.assertEquals(homePage.checkURL(), Boolean.TRUE, "URL doesn't contain text");
    }

    @Test(priority=7)
    public void regionCheck() throws InterruptedException {
        WebElement chosenOne = homePage.chooseRegion();
        Integer chosenCount = Integer.parseInt(chosenOne.getText().replaceAll("[\\D]", ""));
        System.out.println(chosenOne.getText());
        Integer resultCount = homePage.getCountWithScroll(chosenOne);
        Assert.assertEquals(resultCount, chosenCount, "Region Count Data does not match");
    }

    @AfterSuite
    public void closeDriver() throws InterruptedException {
        TimeUnit.SECONDS.sleep(2);
        driver.quit();
    }
}
