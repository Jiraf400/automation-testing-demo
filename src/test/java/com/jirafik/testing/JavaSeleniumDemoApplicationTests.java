package com.jirafik.testing;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.support.ui.ExpectedConditions.alertIsPresent;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@Slf4j
@SpringBootTest
class JavaSeleniumDemoApplicationTests {

    public EdgeDriver getDriver() {
        System.setProperty("webdriver.edge.driver", "C:\\Program Files\\Software\\edgedriver\\msedgedriver.exe");

        EdgeOptions options = new EdgeOptions();
        options.addArguments("--remote-allow-origins=*");

        return new EdgeDriver(options);
    }

    @Test
    public void DemoSeleniumTest() {

        WebDriver driver = getDriver();

        driver.get("https://www.selenium.dev/selenium/web/web-form.html");

        String title = driver.getTitle();
        assertThat(title).isEqualTo("Web form");

        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

        WebElement textBox = driver.findElement(By.name("my-text"));
        assertThat(textBox.isDisplayed()).isEqualTo(true);
        textBox.sendKeys("Selenium");

        WebElement submitButton = driver.findElement(By.cssSelector("button"));
        assertThat(submitButton.isDisplayed()).isEqualTo(true);
        submitButton.click();

        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

        WebElement message = driver.findElement(By.id("message"));
        String value = message.getText();

        assertThat("Received!").isEqualTo(value);
    }

    @Test
    public void TestDevByPage() {
        WebDriver driver = getDriver();

        driver.get("https://devby.io/");

        WebElement el = driver.findElement(By.xpath("//a[text()='Вакансии']"));

        String cssValue = el.getCssValue("display");

        log.info(cssValue);

    }

    @Test
    public void TestKeys() {

        WebDriver driver = getDriver();

        driver.get("https://google.com");

        WebElement input = driver.findElement(By.xpath("//textarea[@title='Поиск']"));

//        input.sendKeys("Java", Keys.ENTER);
        input.sendKeys(Keys.CONTROL + "v", Keys.ENTER);

        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

        WebElement ipsum = driver.findElement(By.xpath("//h3[text()='Lipsum generator: Lorem Ipsum - All the facts']"));

        ipsum.click();

        System.out.println(driver.getTitle());

        // SOME TEXT

        //  //input[@name='q']
    }

    @Test
    public void TestActions() throws InterruptedException {
        WebDriver driver = getDriver();

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        try {
            driver.get("https://crossbrowsertesting.github.io/drag-and-drop");
            Thread.sleep(2000);

            WebElement el = driver.findElement(By.xpath("//div[@id='draggable']"));
            WebElement el2 = driver.findElement(By.xpath("//div[@id='droppable']"));

            Actions actions = new Actions(driver);

            actions.moveToElement(el)
                    .clickAndHold()
                    .moveToElement(el2)
                    .release()
                    .build()
                    .perform();

//            actions.dragAndDrop(el, el2);

            log.info(el2.getText());
            assertThat(el2.getText()).isEqualTo("Dropped!");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            Thread.sleep(10000);
            driver.quit();
        }


    }

    @Test
    public void TestPaginationPage() throws InterruptedException {
        WebDriver driver = getDriver();

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("https://pagination.js.org/");
        Thread.sleep(2000);

        List<WebElement> elems = driver.findElements(By.xpath("//div[@class='data-container']/ul/li"));
        List<WebElement> pages = driver.findElements(By.xpath("//div[@class='paginationjs-pages']/ul/li"));
        String text = elems.get(5).getText();
        assertThat(text).isEqualTo("6");

        pages.get(2).click();
        wait.until(ExpectedConditions.stalenessOf(elems.get(5))); //waiting for object disappearing

        elems = driver.findElements(By.xpath("//div[@class='data-container']/ul/li"));

        text = elems.get(5).getText();
        assertThat(text).isEqualTo("16");

    }

    @Test
    public void TestModalAlertPages() throws InterruptedException {

        WebDriver driver = getDriver();

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("http://127.0.0.1:5500/index.html"); //live server html page

        Thread.sleep(5000);

        WebElement el1 = driver.findElement(By.id("a"));
        WebElement el2 = driver.findElement(By.id("b"));
        WebElement el3 = driver.findElement(By.id("c"));

        el1.click();

        Alert alert = wait.until(alertIsPresent());
        alert.accept();

        el2.click();
        Alert prompt = wait.until(alertIsPresent());
        prompt.sendKeys("kitten on a catwalk");
        prompt.accept();

        Alert alert2 = wait.until(alertIsPresent());
        Thread.sleep(2000);
        alert2.accept();

        el3.click();
        Alert alert3 = wait.until(alertIsPresent());
        alert3.dismiss();

    }

    @Test
    public void TestWindowsAndTabs() throws InterruptedException {

        WebDriver driver = getDriver();

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        JavascriptExecutor js = (JavascriptExecutor) driver;

        driver.get("http://127.0.0.1:5500/index.html");

        Thread.sleep(5000);

        String window1 = driver.getWindowHandle();
        String window2 = null;

        js.executeScript("window.open()");

        Set<String> currentWindows = driver.getWindowHandles();

        for (String window : currentWindows) {
            if (!window.equals(window1)) {
                window2 = window;
                break;
            }
        }

        driver.switchTo().window(window2);
        driver.get("https://pagination.js.org/");
        Thread.sleep(5000);
        driver.close(); // tab closing

        driver.switchTo().window(window1); // switch to previous tab


    }

    @Test
    public void TestFindInvisibleInputs() throws InterruptedException {

        WebDriver driver = getDriver();

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        JavascriptExecutor js = (JavascriptExecutor) driver;

        driver.get("http://127.0.0.1:5500/index.html");

        Thread.sleep(5000);

        js.executeScript("document.querySelector('#d').setAttribute('style', " +
                "'display: block;', 'height: 300px;', ' width: 400px;')");

        Thread.sleep(2000);

        WebElement el = driver.findElement(By.xpath("//input[@id='d']"));

        assertThat(el.isEnabled() && el.isDisplayed()).isEqualTo(true);

    }

}






















