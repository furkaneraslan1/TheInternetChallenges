package testpackage;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class Challenges {
	public String baseURL = "https://the-internet.herokuapp.com/";
	public WebDriver driver;
	WebDriverWait wait;

	private SoftAssert softAssert = new SoftAssert();

	@BeforeTest
	public void beforeTest() {
		System.setProperty("webdriver.chrome.driver", "C:\\Users\\furkan.eraslan\\Desktop\\Drivers\\chromedriver.exe");
		driver = new ChromeDriver();
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		driver.get(baseURL);
		driver.manage().window().maximize();
	}

	@AfterMethod
	public void afterMethod() {
		driver.navigate().to(baseURL);
	}

	@Test(priority = 0)
	public void verifyHomePageTitle() {
		String expectedTitle = "The Internet";
		String actualTitle = driver.getTitle();
		Assert.assertEquals(actualTitle, expectedTitle);
	}

	@Test(priority = 1)
	public void verifyABTestingPageHeader() {
		SoftAssert softAssert = new SoftAssert();
		driver.findElement(By.cssSelector("a[href='/abtest']")).click();
		String expectedHeader = "A/B Test Variation 1";
		String actualHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h3"))).getText();
		softAssert.assertEquals(actualHeader, expectedHeader);
		softAssert.assertAll();
	}

	@Test(priority = 2)
	public void verifyElements() {
		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/add_remove_elements/']"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[onclick='addElement()']"))).click();
		String expectedBtnText = "Delete";
		String actualBtnText = driver.findElement(By.xpath("//*[@id=\"elements\"]/button[1]")).getText();
		Assert.assertEquals(actualBtnText, expectedBtnText);
	}

	@Test(priority = 3)
	public void verifyAlertText() {
		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/context_menu']"))).click();
		Actions actions = new Actions(driver);
		WebElement elementLocator = driver.findElement(By.id("hot-spot"));
		actions.contextClick(elementLocator).perform();
		Alert alert = driver.switchTo().alert();
		String expectedString = "You selected a context menu";
		String actualString = alert.getText();
		Assert.assertEquals(actualString, expectedString);
		alert.accept();
	}

	@Test(priority = 4)
	public void verifyGalleryButtonVisibility() throws InterruptedException {

		// This will Pass or Fail randomly as the element disappears/reappears on each page load.
		SoftAssert softAssert = new SoftAssert();
		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/disappearing_elements']"))).click();
		List<WebElement> elements = driver.findElements(By.tagName("li"));
		String expectedString = "Gallery";
		boolean elementPresent = false;
		for (int i = 0; i < elements.size(); i++) {
			System.out.println(elements.get(i).getText());
			if (elements.get(i).getText().equals(expectedString)) {
				elementPresent = true;
			}
		}
		softAssert.assertTrue(elementPresent);
		softAssert.assertAll();
	}

	@Test(priority = 5)
	public void verifyDynamicControls() throws InterruptedException {
		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/dynamic_controls']"))).click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("checkbox")));
		WebElement removeBtn = driver.findElement(By.cssSelector("button[onclick='swapCheckbox()']"));
		removeBtn.click();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("checkbox")));
		softAssert.assertTrue(!isElementPresent(By.id("checkbox")));

		WebElement textField = driver.findElement(By.cssSelector("input[type='text']"));
		WebElement enableBtn = driver.findElement(By.cssSelector("button[onclick='swapInput()']"));
		enableBtn.click();
		wait.until(ExpectedConditions.elementToBeClickable(textField));
		softAssert.assertTrue(textField.isEnabled());

		enableBtn.click(); // Disable Button
		wait.until(ExpectedConditions.not(ExpectedConditions.elementToBeClickable(textField)));
		softAssert.assertTrue(!textField.isEnabled());

		softAssert.assertAll();
	}

	@Test(priority = 6)
	public void verifyFileUpload() {
		String filePath = "C:\\Users\\furkan.eraslan\\Desktop\\test.txt";
		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/upload']"))).click();

		wait.until(ExpectedConditions.elementToBeClickable(By.id("file-upload"))).sendKeys(filePath);
		driver.findElement(By.id("file-submit")).click();

		String expectedHeader = "File Uploaded!";
		String actualHeader = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"content\"]/div/h3")))
				.getText();
		softAssert.assertEquals(actualHeader, expectedHeader);

		String[] expectedFileNameArray = filePath.split("\\\\");
		String expectedFileName = expectedFileNameArray[expectedFileNameArray.length - 1];
		String actualFileName = driver.findElement(By.id("uploaded-files")).getText();
		softAssert.assertEquals(actualFileName, expectedFileName);

		softAssert.assertAll();
	}

	@Test(priority = 7)
	public void verifyMultipleTabs() {
		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/windows']"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/windows/new']"))).click();
		wait.until(ExpectedConditions.numberOfWindowsToBe(2));
		ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
		driver.switchTo().window(tabs.get(1));
		String expectedHeader = "New Window";
		String actualHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h3"))).getText();
		softAssert.assertEquals(actualHeader, expectedHeader);
		driver.close();
		driver.switchTo().window(tabs.get(0));
		softAssert.assertAll();
	}

	@AfterTest
	public void afterTest() {
		driver.quit();
	}

	// Methods

	public boolean isElementPresent(By locatorKey) {
		try {
			driver.findElement(locatorKey);
			return true;
		} catch (org.openqa.selenium.NoSuchElementException e) {
			return false;
		}
	}

}
