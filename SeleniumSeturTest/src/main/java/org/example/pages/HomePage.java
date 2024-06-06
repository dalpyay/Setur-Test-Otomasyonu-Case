package org.example.pages;

import org.example.base.TestBase;
import org.example.util.CsvReader;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.NoSuchElementException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HomePage extends TestBase {
    //CSV dosyasının path'ini dosyaya göre belirtiniz
    private final List<String[]> myCsv = CsvReader.readAllDataAtOnce("./src/test/java/org/example/csv/destination.csv");

    @FindBy(xpath = "//span[@class = 'ins-close-button']")
    private WebElement indirimButton;

    @FindBy(xpath = "//*[@id=\"CybotCookiebotDialogBodyLevelButtonLevelOptinDeclineAll\"]")
    private WebElement cookieButton;

    @FindBy(xpath = "//button[contains(@class, 'jmbIRo')]")
    private WebElement selectionType;

    @FindBy(xpath = "//input[contains(@placeholder, 'Yurt İçi - Yurt Dışı, Otel Adı veya Konum')]")
    private WebElement destinationInput;

    @FindBy(xpath = "//div[contains(text(), 'Giriş - Çıkış Tarihleri')]")
    private WebElement dateInputDiv;

    @FindBy(xpath = "//button[@class = 'sc-8de9de7b-0 kUHODY sc-6a048693-2 gcXzgW']")
    private WebElement dateNextMonthButton;

    @FindBy(xpath = "//span[contains(text(), '1 Oda, 2 Yetişkin')]")
    private WebElement adultDiv;

    @FindBy(xpath = "//button[@data-testid = 'increment-button']")
    private WebElement incrementAdultButton;

    @FindBy(xpath = "//span[@data-testid = 'count-label']")
    private WebElement adultCountLabel;

    @FindBy(xpath = "//button[@aria-label = 'Ara']")
    private WebElement searchButton;

    public HomePage() {
        PageFactory.initElements(driver, this);
    }

    public String getDefaultTabText() {
        return this.selectionType.getText();
    }

    public String getPageTitle(){
        return driver.getTitle();
    }

    public void closeIndirimPopUp(){
        try{
            Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.until(d -> indirimButton.isDisplayed());
            this.indirimButton.click();
        }
        catch (TimeoutException e){
            System.out.println("İndirim Popup'ı Beliritlen Zaman Aralığında Gelmedi");
        }
    }

    public void closeCookies(){
        this.cookieButton.click();
    }

    public void sendDestinationKeys() {
//        String input = "Antalya";
        String input = myCsv.get(0)[0];
        new Actions(driver)
                .sendKeys(destinationInput, input)
                .perform();
        WebElement destinationDiv = driver.findElement(By.xpath("//div[contains(@class, 'evugte')]"));
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(20), Duration.ofSeconds(2));
        wait.until(d -> destinationDiv.isDisplayed());
        destinationDiv.click();
    }

    public void chooseDate(){
        this.dateInputDiv.click();
        Boolean found = Boolean.FALSE;
        driver.manage().timeouts().implicitlyWait(200, TimeUnit.MILLISECONDS);
        while(!found){
            List<WebElement> aprilFirstButton = driver.findElements(By.xpath("//td[contains(@aria-label, 'Choose Salı, 1 Nisan 2025 as your check-in date. It’s available.')]"));
            if(!aprilFirstButton.isEmpty()){
                found = Boolean.TRUE;
                this.dateNextMonthButton.click();
                aprilFirstButton.get(0).click();
                WebElement aprilSeventhButton = driver.findElement(By.xpath("//td[contains(@aria-label, 'Choose Pazartesi, 7 Nisan 2025 as your check-out date. It’s available.')]"));
                aprilSeventhButton.click();
                break;
            }
            this.dateNextMonthButton.click();
        }
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

    }

    public List<Integer> clickAdultButton(){
        this.adultDiv.click();
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(d -> incrementAdultButton.isDisplayed());
        Integer prev = Integer.valueOf(adultCountLabel.getText());
        this.incrementAdultButton.click();
        Integer after = Integer.valueOf(adultCountLabel.getText());
        List<Integer> result = new ArrayList<Integer>();
        result.add(prev);
        result.add(after);
        return result;
    }

    public Boolean clickSearchButton(){
        if(this.searchButton.isDisplayed()){
//            String input = "Antalya";
            String input = myCsv.get(0)[0];
            this.searchButton.click();
            Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.until(d -> d.findElement(By.xpath("//span[contains(@class, 'sc-363be8ce-0 fsxtik')]")));
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Boolean checkURL(){
//        String input = "Antalya";
        String input = myCsv.get(0)[0];
        return driver.getCurrentUrl().contains(input.toLowerCase());
    }

    public WebElement chooseRegion(){
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(20), Duration.ofSeconds(2));
        WebElement showMoreButton = driver.findElement(By.xpath("//div[@data-testid = 'show-more-regions-button']"));
        wait.until(d -> showMoreButton.isDisplayed());
        showMoreButton.click();

        List<WebElement> listRegions = driver.findElements(By.xpath("//div[contains(@class, 'sc-4b246749-3 hlJEOp')]"));
        Random rand = new Random();
        int n = rand.nextInt(listRegions.size());
        return listRegions.get(n);
    }

    public Integer getCountWithScroll(WebElement chosenOne) throws InterruptedException {
        WebElement element = chosenOne.findElement(By.xpath(".//div[contains(@class, 'sc-bc724d78-0 llAzUN')]"));
        element.click();
        TimeUnit.SECONDS.sleep(3);

        WebElement scrollTo = driver.findElement(By.xpath("//div[contains(@class, 'sc-d22b8295-2 ePzwxx')]"));

        JavascriptExecutor jse = (JavascriptExecutor)driver;
        jse.executeScript("arguments[0].scrollIntoView(true);", scrollTo);
        jse.executeScript("window.scrollBy(0, -200)");

        //Seçilen otel sayısı az olması durumunda farklı elementten sayı adedi kontrolü sağlanır.
        try{
            WebElement endPage = driver.findElement(By.xpath("//div[contains(@class, 'sc-b01e5b98-1 jugDMt')]/span[2]"));
            Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.until(d -> endPage.isDisplayed());
            return Integer.parseInt(endPage.getText().replaceAll("[\\D]", ""));
        }
        catch (NoSuchElementException e){
            WebElement endPage = driver.findElement(By.xpath("//h3[@id = 'urun-sayisi']"));
            Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.until(d -> endPage.isDisplayed());
            return Integer.parseInt(endPage.getText().replaceAll("[\\D]", ""));
        }

    }
}
